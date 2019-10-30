<template>
  <b-container fluid>
    <b-row>
      <b-col cols="6">
        <span style="font-size: 18px; font-weight: bold">{{schedule.date}} Production Output for Line: {{line_id}} </span>
      </b-col>
    </b-row>
	<b-row>
		<b-col cols=4>
			<div style="font-size:18px">Started: {{scheduleEvent.startTime}}</div>
			<div style="font-size:18px">Finished: {{scheduleEvent.finishTime}}</div>
			<div style="font-size:18px">Units Scheduled: {{scheduleEvent.unitsScheduled}}</div>
			<div style="font-size:18px">Total Produced: {{scheduleEvent.totalProduced}}</div>
		</b-col>
		<b-col cols=4>
			<div style="font-size:18px">Item: {{scheduleEvent.saleItem.item.name}}</div>
			<div style="font-size:18px">Customer: {{scheduleEvent.saleItem.sale.customer.name}}</div>
			<div style="font-size:18px">Sale: {{scheduleEvent.saleItem.sale.number}}</div>
		</b-col>
		<b-col cols=4>
			<b-button v-if="inProgress() && !isFinished()" style="margin: 3px" type="submit" variant="success" @click="openModal()">Add Units</b-button>
			<b-button v-if="!inProgress() && !isFinished()" style="margin: 3px" type="submit" variant="success" @click="startProduction">Start</b-button>
			<b-button v-if="inProgress() && !isFinished()" style="margin: 3px" type="submit" variant="success" @click="finishProduction">Finish</b-button>
		</b-col>
	</b-row>
	<b-row>
		<b-col cols=5>
			<div v-for="ie in itemEvents" :key="ie.id">
				<div :style="getStyle(ie.active)">{{ie.name}}</div>
				<ul v-for="customer in ie.customers" :key="customer.id">
					<li :style="getStyle(customer.active)">{{customer.name}}</li>
					<ul v-for="event in customer.events" :key="event.id">
						<li style="cursor: pointer;" :style="getStyle(event.active)" @click="setEvent(ie, customer, event)">
							{{event.saleItem.sale.number}} {{event.finishTime?" (Completed)":(event.startTime?" (Started)":" (Not Started)")}}
						</li>
					</ul>
				</ul>
			</div>
		</b-col>
		<b-col cols=7>
			<chart :chartdata="chartData" :options="chartOptions" :width="600" :height="300"></chart>
		</b-col>
	</b-row>
	<div v-if="modalVisible">
		<production-modal v-on:closeModal="closeModal()"></production-modal>
	</div>
	<br/>
	<div v-if="securite.hasRole(['PRODUCTION_ADMIN'])">
	<b-row>
		<b-col>
			<span style="font-size: 18px; font-weight: bold; align:left">Production Output </span>
		</b-col>
	</b-row>
	<br/>
	<b-row v-for="production in sortedProductions" v-bind:key="production.id">
		<b-col cols=2>
			<input class="form-control" type="time" v-model="production.finishTime">
		</b-col>
		<b-col cols=2>
			<input class="form-control" type="tel" v-model="production.unitsProduced">
		</b-col>
		<b-col cols=1>
			<input class="form-control" type="tel" v-model="production.people">
		</b-col>
		<b-col cols=2>
			<b-button size="sm" @click.stop="updateProduction(production)" variant="link">Update</b-button>
			<b-button size="sm" @click.stop="deleteProduction(production.id)" variant="link">Delete</b-button>
		</b-col>
	</b-row>
	</div>
	<br/>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";
import store from "../store.js";
import securite from "../securite";

export default {
	components: {
    ProductionModal: () => import("./ProductionModal")
  },
  data() {
    return {
			line_id: "",
			schedule: {},
			modalVisible: false,
			securite: securite,
			addInProgress: false,
			sortedProductions: [],
			unitsToAdd: 0,
			people: 0,
			itemEvents: [],
			scheduleEvents: [],
			scheduleEvent: {
				schedule: {},
				line: {},
				saleItem: {
					item: {},
					sale: {
						customer: {}
					}
				}
			},
			chartData: {datasets: []},
			chartOptions: {
				legend: {display: false},
				scales: {
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: 'Units Per Hour'
						}
					}],
					xAxes: [{
						type: 'time',
						time: {
							distribution: 'linear',
							unit: 'hour',
							// max: moment("00:00:00", "H:mm:ss").add(23, 'hours').add(59, 'minutes'),
							stepSize: 1,
							displayFormats: {hour : 'HH:mm'}
						}
					}]
				},
				tooltips: {
					callbacks: {
						label: (tooltipItem, data) => {
							return data.datasets[0].data[tooltipItem.index].tooltipLabel;
						}
					}
				}
			},
    };
  },
  watch: {},
  methods: {
		getStyle(active){
			var style = "";
			if(active){
				style = "color: red; font-weight: bold"; 
			}
			return style;
		},
		setEvent(itemEvent, customer, event){
			this.itemEvents.forEach(ie => {
				ie.active = false;
				ie.customers.forEach(cu => {
					cu.active = false;
					cu.events.forEach(e => {
						e.active = false;
					})
				})
			})
			itemEvent.active = !itemEvent.active;
			customer.active = !customer.active;
			event.active = !event.active;
			if(event.active){
				this.getScheduleEvent(event.id);
			}
			this.updateChart();
		},
		updateChart(){
			var prevTime = moment(this.scheduleEvent.startTime, 'HH:mm:ss');
			this.chartOptions.scales.xAxes[0].time.min = moment(prevTime.hour(), "HH");
			var tooltipLabel = "Started at "+ moment(this.scheduleEvent.startTime, 'HH:mm:ss').format('HH:mm');
			this.chartData.datasets = [{
				// label: this.scheduleEvent.saleItem.item.name, 
				data: [{x: prevTime, y: 0, tooltipLabel: tooltipLabel}], 
				steppedLine: 'after',
				fill: false,
				borderColor: '#C28535',
			}]; 
			this.sortedProductions.forEach(p => {
				var secs = moment(p.finishTime, 'HH:mm:ss').diff(prevTime, 'seconds');
				var time = moment().startOf('day').seconds(secs).format('HH:mm:ss')
				var perf = !secs?0:((p.unitsProduced/secs)*3600).toFixed(0);
				var tooltipLabel= perf+" u/h (" +p.unitsProduced+" units in "+time+")"
				this.chartData.datasets[0].data.push({x: moment(p.finishTime, 'HH:mm:ss'), y: perf, tooltipLabel: tooltipLabel});
				prevTime = moment(p.finishTime, 'HH:mm:ss');
			})
		},
		updateProduction(production){
			production.scheduleEvent = {id: this.scheduleEvent.id};
      return http.post("/production", production).then(response => {
				this.getScheduleEvent(this.scheduleEvent.id);
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
		deleteProduction(production_id){
      return http.delete("/production/"+production_id).then(response => {
				this.getScheduleEvent(this.scheduleEvent.id);
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
    getSchedule(schedule_id) {
      http.get("/schedule/"+schedule_id).then(response => {
				this.schedule = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
    getScheduleEvents(schedule_id) {
      http.get("/scheduleEvent/schedule/"+schedule_id+"/line/" + this.line_id).then(response => {
				this.itemEvents = [];
				this.scheduleEvents = response.data;
				response.data.forEach(event => {
					var itemEvent = this.itemEvents.find(ie => ie.id == event.saleItem.item.id);
					if(!itemEvent){
						itemEvent = {
							id: event.saleItem.item.id,
							name: event.saleItem.item.name,
							active: false,
							customers: [],
						}
						this.itemEvents.push(itemEvent);
					}
					var customer = itemEvent.customers.find(cu => cu.id == event.saleItem.sale.customer.id);
					if(!customer){
						customer = {
							id: event.saleItem.sale.customer.id,
							name: event.saleItem.sale.customer.name,
							active: false,
							events: []
						}
						itemEvent.customers.push(customer);
					}
					event.active = false;
					customer.events.push(event);
				})
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
    getScheduleEvent(schedule_event_id) {
      http.get("/scheduleEvent/" + schedule_event_id).then(response => {
				this.scheduleEvent = response.data;
				this.sortedProductions = response.data.productions.sort(function(a, b){
					return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
				});
		  	this.updateChart();
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
		startProduction() {
			var alreadyStarted = false;
			this.scheduleEvents.forEach(se => {
				if(se.startTime && !se.finishTime){
					alreadyStarted = true;
				}
			})
			if(alreadyStarted){
				alert("There is another Sale in progress. Please, finish it before start next one");
				return;
			}
			this.scheduleEvent.startTime = moment().format("HH:mm:ss");
				return http.post("/scheduleEvent", this.scheduleEvent).then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
					this.getScheduleEvents(this.schedule.id)
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
			finishProduction() {
				this.scheduleEvent.finishTime = moment().format("HH:mm:ss");
				return http.post("/scheduleEvent", this.scheduleEvent).then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
					this.getScheduleEvents(this.schedule.id);
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
		openModal(){
			this.modalVisible = true;
			// if(!this.unitsToAdd || this.unitsToAdd<1){
			// 	alert("Enter Units Produced!")
			// 	return;
			// }
			// if(!this.people || this.people<1){
			// 	alert("Enter Assigned People!")
			// 	return;
			// }
			// var totalUnits = +this.scheduleEvent.totalProduced + +this.unitsToAdd;
			// if(totalUnits > this.scheduleEvent.unitsScheduled){
			// 	alert("Cannot enter more units than scheduled");
			// 	return;
			// }
			// var prevTime = moment(this.scheduleEvent.startTime, 'HH:mm:ss');
			// if(this.sortedProductions.length>0){
			// 	prevTime = moment(this.sortedProductions[this.sortedProductions.length-1].finishTime, 'HH:mm:ss');
			// }
			// var currentTime = moment();
			// var secs = currentTime.diff(prevTime, 'seconds');
			// if(secs<60){
			// 	alert("It took "+secs+" seconds to produce "+this.unitsToAdd +" units?\n\n"
			// 		+"Few tips: \n"
			// 		+"Click Start Production button when production started (not later).\n"
			// 		+"Units entered don't have to equal previously entered\n"
			// 		+"Add units as soon as possible.\n"
			// 		+"Add units as often as possible.\n\n"
			// 		+"Units will added this time but please, follow the tips!");
			// }
			// var production = {
			// 	scheduleEvent: {id: this.scheduleEvent.id},
			// 	finishTime: currentTime.format("HH:mm:ss"),
			// 	unitsProduced: this.unitsToAdd,
			// 	people: this.people
			// };
			// return http.post("/production", production).then(response => {
			// 	this.getScheduleEvent(this.scheduleEvent.id);
			// }).catch(e => {
			// 	console.log("API error: " + e);
			// });
			// this.addInProgress = false;
		},
		closeModal(){
			this.modalVisible=false;
		},
		inProgress() {
			return (this.scheduleEvent.startTime != null && this.scheduleEvent.finishTime == null);
		},
		isFinished() {
			return this.scheduleEvent.finishTime != null;
		}
	},
	mounted() {
		var schedule_id = this.$route.params.schedule_id;
		this.getSchedule(schedule_id);
		this.line_id = this.$route.params.line_id;
		this.getScheduleEvents(schedule_id);
  }
};
</script>

<style>
</style>
