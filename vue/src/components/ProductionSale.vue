<template>
  <b-container fluid>
    <b-row>
      <b-col cols="6">
        <span style="font-size: 18px; font-weight: bold">Production Line Output </span>
      </b-col>
    </b-row>
	<br/>
	<b-row>
		<b-col cols=4>
			<div style="font-size:18px">Scheduled: {{scheduleEvent.schedule.date}} @ {{scheduleEvent.scheduleTime}}</div>
			<div style="font-size:18px">Started: {{scheduleEvent.startTime}}, Finished: {{scheduleEvent.finishTime}}</div>
			<div style="font-size:18px">Units Scheduled: {{scheduleEvent.unitsScheduled}}</div>
			<div style="font-size:18px">Total Produced: {{scheduleEvent.unitsProduced}}</div>
		</b-col>
		<b-col cols=8>
			<b-row>
				<b-col cols="2" v-if="inProgress() && !isFinished()" style="margin-top: 25px">
					<b-button v-if="inProgress() && !isFinished()" type="submit" variant="success" @click="openAddModal()">Add Units</b-button>
				</b-col>
				<b-col cols="4">
					<b-button v-if="!inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="startProduction">Start Production</b-button>
					<b-button v-if="inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="finishProduction">Finish Production</b-button>
				</b-col>
			</b-row>
		</b-col>
	</b-row>
	<b-row>
		<b-col cols=8>
			<chart :chartdata="chartData" :options="chartOptions" :width="900" :height="300"></chart>
		</b-col>
		<b-col cols=4>
			<div style="font-size:18px">Line: {{scheduleEvent.line.number}}</div>
			<div style="font-size:18px">Item: {{scheduleEvent.saleItem.item.name}}</div>
			<div style="font-size:18px">Customer: {{scheduleEvent.saleItem.sale.customer.name}}</div>
			<div style="font-size:18px">Sale: {{scheduleEvent.saleItem.sale.number}}</div>
		</b-col>
	</b-row>
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
	<div v-if="addModalVisible">
		<production-modal :schedule-event="scheduleEvent" v-on:closeModal="closeModals()"></production-modal>
	</div>
	<b-modal centered size="lg" v-model="startModalVisible" :hide-header="true" :hide-footer="true">
		<b-row>
			<b-col>
				<div style="text-align: right;">
					<b-button style="margin: 0 2px 0 2px" @click="closeModals()">Close</b-button>
					<b-button style="margin: 0 2px 0 2px" @click="saveStartProduction()" variant="success">Save</b-button>
				</div>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols="3">
				<label class="top-label">Time Started:</label>
				<input class="form-control" type="time" v-model="startTime">
			</b-col>
		</b-row>
	</b-modal>

  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";
import securite from "../securite";

export default {
	components: {
    ProductionModal: () => import("./ProductionModal")
  },
  data() {
    return {
			addModalVisible: false,
			startModalVisible: false,
			startTime: moment().format("HH:mm:ss"),
			securite: securite,
			addInProgress: false,
			sortedProductions: [],
			unitsToAdd: 0,
			people: 0,
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
			chartData: {labels: [], datasets: [{data: [], lineTension: 0}]},
			chartOptions: {
				legend: {display: false},
				scales: {
					yAxes: [{
						scaleLabel: {
							display: true,
							labelString: 'Units Produced'
						}
					}],
					xAxes: [{
						type: 'time',
						time: {
							distribution: 'linear',
							unit: 'hour',
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
		openAddModal(){
			this.addModalVisible = true;
		},
		closeModals(){
			this.addModalVisible=false;
			this.startModalVisible = false;
			this.getScheduleEvent(this.scheduleEvent.id);
		},
		updateChart(){
			this.chartOptions.scales.xAxes[0].time.min = moment(this.scheduleEvent.startTime, 'HH');
			this.chartOptions.scales.xAxes[0].time.max = moment(this.scheduleEvent.finishTime, 'HH').add(1, 'hours')
			var tooltipLabel = "Started at "+ moment(this.scheduleEvent.startTime, 'HH:mm:ss').format('HH:mm:ss');
			this.chartData.datasets = [{
				data: [{x: moment(this.scheduleEvent.startTime, 'HH:mm:ss'), y: 0, tooltipLabel: tooltipLabel}], 
				// steppedLine: 'after',
				fill: false,
				lineTension: 0,
				borderColor: '#C28535',
			}];
			var sumProduced = 0;
			var prevTime = moment(this.scheduleEvent.startTime, 'HH:mm:ss');
			this.sortedProductions.forEach(p => {
				var secs = moment(p.finishTime, 'HH:mm:ss').diff(prevTime, 'seconds');
				var time = moment().startOf('day').seconds(secs).format('HH:mm:ss')
				var perf = !secs?0:((p.unitsProduced/secs)*3600).toFixed(0);
				sumProduced += p.unitsProduced;
				var tooltipLabel= perf+" u/h (" +p.unitsProduced+" units in "+time+")"
				this.chartData.datasets[0].data.push({x: moment(p.finishTime, 'HH:mm:ss'), y: sumProduced, tooltipLabel: tooltipLabel});
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
		startProduction(){
			this.startModalVisible = true;
		},
		saveStartProduction() {
			this.scheduleEvent.startTime = this.startTime;
				return http.post("/scheduleEvent", this.scheduleEvent).then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
					this.startModalVisible = false;
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
			finishProduction() {
				if(this.sortedProductions.length == 0){
					alert("Nothing produced yet");
					return;
				}
				this.scheduleEvent.finishTime = this.sortedProductions[this.sortedProductions.length-1].finishTime;
				return http.post("/scheduleEvent", this.scheduleEvent).then(response => {
					router.push("/productionLineList");
				}).catch(e => {
					console.log("API error: " + e);
				});
			},
		saveOutput(){
			if(!this.unitsToAdd || this.unitsToAdd<1){
				alert("Enter Units Produced!")
				return;
			}
			if(!this.people || this.people<1){
				alert("Enter Assigned People!")
				return;
			}
			var totalUnits = +this.scheduleEvent.unitsProduced + +this.unitsToAdd;
			if(totalUnits > this.scheduleEvent.unitsScheduled){
				alert("Cannot enter more units than scheduled");
				return;
			}
			var prevTime = moment(this.scheduleEvent.startTime, 'HH:mm:ss');
			if(this.sortedProductions.length>0){
				prevTime = moment(this.sortedProductions[this.sortedProductions.length-1].finishTime, 'HH:mm:ss');
			}
			var currentTime = moment();
			var secs = currentTime.diff(prevTime, 'seconds');
			if(secs<60){
				alert("It took "+secs+" seconds to produce "+this.unitsToAdd +" units?\n\n"
					+"Few tips: \n"
					+"Click Start Production button when production started (not later).\n"
					+"Units entered don't have to equal previously entered\n"
					+"Add units as soon as possible.\n"
					+"Add units as often as possible.\n\n"
					+"Units will added this time but please, follow the tips!");
			}
			var production = {
				scheduleEvent: {id: this.scheduleEvent.id},
				finishTime: currentTime.format("HH:mm:ss"),
				unitsProduced: this.unitsToAdd,
				people: this.people
			};
			return http.post("/production", production).then(response => {
				this.getScheduleEvent(this.scheduleEvent.id);
			}).catch(e => {
				console.log("API error: " + e);
			});
			this.addInProgress = false;
		},
		inProgress() {
			return (this.scheduleEvent.startTime != null && this.scheduleEvent.finishTime == null);
		},
		isFinished() {
			return this.scheduleEvent.finishTime != null;
		}
	},
	mounted() {
		var schedule_event_id = this.$route.params.schedule_event_id;
    if (schedule_event_id) {
      this.getScheduleEvent(schedule_event_id);
    }
  }
};
</script>

<style>
</style>
