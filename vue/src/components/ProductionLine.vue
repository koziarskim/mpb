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
			<div style="font-size:18px">Started: {{scheduleEvent.startTime}}</div>
			<div style="font-size:18px">Units Scheduled: {{scheduleEvent.unitsScheduled}}</div>
			<div style="font-size:18px">Total Produced: {{scheduleEvent.totalProduced}}</div>
		</b-col>
		<b-col cols=8>
			<b-row>
				<b-col cols=2>
					<label v-if="inProgress() && !isFinished()" class="top-label">Units Produced:</label>
					<input v-if="inProgress() && !isFinished()" class="form-control"  type="tel" v-model="unitsToAdd">
				</b-col>
				<b-col cols="2">
					<label v-if="inProgress() && !isFinished()" class="top-label">People Assigned:</label>
					<input v-if="inProgress() && !isFinished()" class="form-control" type="tel" v-model="people">
				</b-col>
				<b-col cols="2" v-if="inProgress() && !isFinished()" style="margin-top: 25px">
					<b-button v-if="inProgress() && !isFinished()" type="submit" variant="success" @click="saveOutput">Add Units</b-button>
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
	<div v-if="hasRoles(['PRODUCTION_ADMIN'])">
	<b-row>
		<b-col>
			<span style="font-size: 18px; font-weight: bold; align:left">Production Output </span>
		</b-col>
	</b-row>
	<br/>
	<b-row v-for="(production) in scheduleEvent.productions" v-bind:key="production.id">
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

export default {
  name: "edit-component",
  data() {
    return {
	  addInProgress: false,
	  unitsToAdd: 0,
	  people: 0,
	  chartData: {
			labels:[],
      datasets: [{
        label: 'Units per Time',
				data: [],
				lineTension: 0
      }]
		},
	  chartOptions: {
			legend: {
				display: false,
			},
			scales: {
        xAxes: [{
					type: 'time',
					time: {
						distribution: 'linear',
						unit: 'hour',
						min: this.newDate(0,0),
						max: this.newDate(23,59),
						stepSize: 1,
						displayFormats: {
							'millisecond': 'MMM DD',
							'second': 'MMM DD',
							'minute': 'MMM DD',
							'hour': 'HH:mm',
							'day': 'MMM DD H:mm:ss',
							'week': 'MMM DD',
							'month': 'MMM DD',
							'quarter': 'MMM DD',
							'year': 'MMM DD',
						}
					}
				}]
			},
		},
    availableLines: [],
    line: {},
    availableItems: [],
    item: {},
    dateStarted: moment().format("YYYY-MM-DD"),
		timeStarted: moment().format("HH:mm:ss"),
		today: moment(),
	  scheduleEvent: {
		  schedule: {},
		  line: {},
		  saleItem: {
			  item: {},
			  sale: {
				  customer: {}
			  }
		  }
	  }
    };
  },
  watch: {
    line(new_value, old_value) {
      this.getAvailableItems();
    }
  },
  methods: {
		newDate(hour, min){
			return moment("00:00:00", "H:mm:ss").add(hour, 'hours').add(min, 'minutes');
		},
		hasRoles(roles){
			var hasRole = this.$store.getters.userContext.hasRoles(['PRODUCTION_ADMIN']);
			return hasRole;
		},
		updateProduction(production){
			production.scheduleEvent = {id: this.scheduleEvent.id};
      return http
        .post("/production", production)
        .then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
		},
		deleteProduction(production_id){
      return http
        .delete("/production/"+production_id)
        .then(response => {
					this.getScheduleEvent(this.scheduleEvent.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
		},
    getScheduleEvent(schedule_event_id) {
      http
        .get("/scheduleEvent/" + schedule_event_id)
        .then(response => {
		  		this.scheduleEvent = response.data;
		  		this.updateChart();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
	mtime(time){
		return moment(time,'HH:mm:ss');
	},
	updateChart(){
		var prevTime = this.mtime(this.scheduleEvent.startTime);
		this.chartData.labels.push(prevTime);
		this.chartData.datasets[0].data.push(0); 
		var sortedProductions = this.scheduleEvent.productions.sort(function(a, b){
			return moment(a.finishTime, 'HH:mm:ss').diff(moment(b.finishTime, 'HH:mm:ss'));
		});
		sortedProductions.forEach(p => {
			var secs = moment(p.finishTime, 'HH:mm:ss').diff(prevTime, 'seconds');
			var perf = ((p.unitsProduced/secs)*3600).toFixed(0);
			this.chartData.labels.push(this.mtime(p.finishTime));
			this.chartData.datasets[0].data.push(perf);
			prevTime = this.mtime(p.finishTime);
		})
	},
	getUnitsForHour(hour, productions){
		var units = 0;
		var index = 0;
		productions.forEach(p => {
			var nextProduction = productions[index+1];
			var nextHour = null;
			if(nextProduction){
				nextHour = moment(productions[index+1].finishTime,'HH:mm:ss').hour();;
			}
			var currentHour = moment(p.finishTime,'HH:mm:ss').hour();
			if(currentHour==hour){
				if(nextHour==hour){
					units += p.unitsProduced;
				}else{
					var reminingSecs = moment(nextProduction.finishTime, 'HH:mm:ss').diff(moment(p.finishTime, 'HH:mm:ss'),'seconds');
					var perc = reminingSecs/3600;
					var nextUnits = nextProduction.unitsProduced;
					var reminingUnits = nextUnits*perc;
					units += reminingUnits;
				}
				
			}
			index++;
		})
		return units.toFixed(0);
	},
    getAvailableLines() {
      this.availableLines = [
        { id: 1, number: 1 },
        { id: 2, number: 2 },
        { id: 3, number: 3 },
        { id: 4, number: 4 },
        { id: 5, number: 5 },
        { id: 6, number: 6 },
        { id: 7, number: 7 },
        { id: 8, number: 8 }
      ];
    },
    getAvailableItems() {
      http
        .get("/item")
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate() {
      if (!this.line.id) {
        alert("Please select line");
        return false;
      }
      if (!this.item.id) {
        alert("Please select item");
        return false;
      }
      return true;
    },
    startProduction() {
		this.scheduleEvent.startTime = moment().format("HH:mm:ss");
      return http
        .post("/scheduleEvent", this.scheduleEvent)
        .then(response => {
			this.getScheduleEvent(this.scheduleEvent.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    finishProduction() {
      this.scheduleEvent.finishTime = moment().format("HH:mm:ss");
      return http
        .post("/scheduleEvent", this.scheduleEvent)
        .then(response => {
          router.push("/productionLineList");
        })
        .catch(e => {
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
		var totalUnits = +this.scheduleEvent.totalProduced + +this.unitsToAdd;
		if(totalUnits > this.scheduleEvent.unitsScheduled){
			alert("Cannot enter more units than scheduled");
			return;
		}
		var production = {
		  scheduleEvent: {id: this.scheduleEvent.id},
		  finishTime: moment().format("HH:mm:ss"),
		  unitsProduced: this.unitsToAdd,
		  people: this.people
	  };
      return http
        .post("/production", production)
        .then(response => {
			this.getScheduleEvent(this.scheduleEvent.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
		this.addInProgress = false;
	},
    inProgress() {
      return (
        this.scheduleEvent.startTime != null &&
        this.scheduleEvent.finishTime == null
	  );
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
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
