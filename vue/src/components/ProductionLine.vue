<template>
  <b-container fluid>
		<b-row>
			<b-col cols=10 style="margin-top:7px; margin-bottom:7px">
				<div style="display:flex">
					<input class="form-control" style="width: 170px; height: 33px; margin-top: -10px; margin-right: 5px;" type="date" v-model="date"> 
					<b-button size="sm" type="submit" variant="success" style="margin-top: -10px; margin-right: 5px;" @click="setToday()">Today</b-button>
					Line: <span style="font-weight: bold; margin-right: 5px;">{{line_id}}</span> 
					Started: <span style="font-weight: bold; margin-right: 5px;">{{scheduleEvent.startTime}}</span> 
					Finished: <span style="font-weight: bold; margin-right: 5px;">{{scheduleEvent.finishTime}}</span> 
					Units Scheduled: <span style="font-weight: bold; margin-right: 5px;">{{scheduleEvent.unitsScheduled}}</span> 
					Total Produced: <span style="font-weight: bold">{{scheduleEvent.totalProduced}}</span> 
				</div>
			</b-col>
			<b-col>
				<b-button v-if="inProgress() && !isFinished()" style="margin-right: 3px" type="submit" variant="success" @click="openModal()">Add Units</b-button>
				<b-button v-if="this.scheduleEvent.id !=null && !inProgress() && !isFinished()" type="submit" variant="success" @click="startProduction">Start</b-button>
				<b-button v-if="inProgress() && !isFinished()" type="submit" variant="success" @click="finishProduction">Finish</b-button>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=4>
				<div v-for="ie in itemEvents" :key="ie.id">
					<div style="display:inline; color: blue">{{ie.name}}</div>
					<div v-for="customer in ie.customers" :key="customer.id" style="margin-bottom: 0px">
						<div style="display:inline; color: #4bb316">&nbsp;&nbsp;&nbsp;&#9679;{{customer.name}}</div>
						<div v-for="event in customer.events" :key="event.id">
							<div style="cursor: pointer; display:inline; color:#e22626; font-weight: bold" :style="getTreeItemStyle(event.active)" @click="getScheduleEvent(event.id)">
								&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&#9656;SO: {{event.saleItem.sale.number}} {{event.finishTime?" (Completed)":(event.startTime?" (Started)":" (Not Started)")}}
							</div>
						</div>
					</div>
				</div>
			</b-col>
			<b-col cols=8>
				<div v-if="!scheduleEvent.id" style="margin-top: 120px; font-weight: bold">Please select sale order (SO) on the left (red)</div>
				<div id="1234" :style="chartVisibility">
					<div style="font-size: 12px; margin-left: 260px">{{scheduleEvent.saleItem.item.name}} ({{scheduleEvent.saleItem.sale.customer.name}} - {{scheduleEvent.saleItem.sale.number}})</div>
					<chart :chartdata="chartData" :options="chartOptions" :width="600" :height="300"></chart>
				</div>
			</b-col>
		</b-row>
		<div v-if="modalVisible">
			<production-modal :schedule-event="scheduleEvent" v-on:closeModal="closeModal()"></production-modal>
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
			chartVisibility: "visibility: hidden",
			date: moment().format("YYYY-MM-DD"),
			line_id: "",
			schedule: {},
			modalVisible: false,
			securite: securite,
			addInProgress: false,
			sortedProductions: [],
			unitsToAdd: 0,
			people: 0,
			itemEvents: [],
			activeItemEvent: {},
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
  watch: {
		date(new_value, old_value){
			this.scheduleEvent = {
				schedule: {},
				line: {},
				saleItem: {
					item: {},
					sale: {
						customer: {}
					}
				}
			};
			this.getScheduleEvents();
			this.sortedProductions = [];
			this.chartVisibility = "visibility: hidden";
		}
	},
  methods: {
		setToday(){
			this.date = moment().format("YYYY-MM-DD");
		},
		getTreeItemStyle(active){
			var style = "";
			if(active){
				style = "background-color: #dbe0db"; 
			}
			return style;
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
    getScheduleEvents() {
      http.get("/scheduleEvent/date/"+this.date+"/line/" + this.line_id).then(response => {
				this.itemEvents.splice(0, this.itemEvents.length);
				this.scheduleEvents = response.data;
				response.data.forEach(event => {
					var itemEvent = this.itemEvents.find(ie => ie.id == event.saleItem.item.id);
					if(!itemEvent){
						itemEvent = {
							id: event.saleItem.item.id,
							name: event.saleItem.item.name,
							active: this.scheduleEvent.saleItem.item.id == event.saleItem.item.id?true:false,
							customers: [],
						}
						this.itemEvents.push(itemEvent);
					}
					var customer = itemEvent.customers.find(cu => cu.id == event.saleItem.sale.customer.id);
					if(!customer){
						customer = {
							id: event.saleItem.sale.customer.id,
							name: event.saleItem.sale.customer.name,
							active: this.scheduleEvent.saleItem.sale.customer.id == event.saleItem.sale.customer.id?true:false,
							events: []
						}
						itemEvent.customers.push(customer);
					}
					event.active = this.scheduleEvent.id == event.id?true:false;
					customer.events.push(event);
				})
      }).catch(e => {
        console.log("API error: " + e);
      });
		},
    getScheduleEvent(schedule_event_id) {
      return http.get("/scheduleEvent/" + schedule_event_id).then(response => {
				this.scheduleEvent = response.data;
				this.sortedProductions = response.data.productions.sort(function(a, b){
					return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
				});
				this.updateChart();
				this.getScheduleEvents();
				this.chartVisibility = "visiblility: visible;"
				return response.data;
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
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
			finishProduction() {
				if(this.scheduleEvent.totalProduced < this.scheduleEvent.unitsScheduled){
					if(!confirm("There are more units scheduled that produced. \n Are you sure you want to finish it?")){
						return;
					}
				}
				this.scheduleEvent.finishTime = moment().format("HH:mm:ss");
				return http.post("/scheduleEvent", this.scheduleEvent).then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
		openModal(){
			this.modalVisible = true;
		},
		closeModal(){
			this.modalVisible=false;
			this.getScheduleEvent(this.scheduleEvent.id);
		},
		inProgress() {
			return (this.scheduleEvent.startTime !=null && this.scheduleEvent.finishTime == null);
		},
		isFinished() {
			return this.scheduleEvent.finishTime != null;
		}
	},
	mounted() {
		this.date = this.$route.query.date;
		if(!this.date){
			this.date = moment().format("YYYY-MM-DD");
		}
		this.line_id = this.$route.params.line_id;
		this.getScheduleEvents();
		window.history.replaceState({}, document.title, window.location.pathname);
  }
};
</script>

<style>
</style>
