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
			<div style="font-size:18px">Scheduled: {{scheduleEvent.schedule.date}}</div>
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
					<label v-if="inProgress() && !isFinished()" class="top-label">People Assiged:</label>
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
			<chart :chartdata="cd" :options="co" :width="900" :height="300"></chart>
		</b-col>
		<b-col cols=4>
			<div style="font-size:18px">Line: {{scheduleEvent.line.number}}</div>
			<div style="font-size:18px">Item: {{scheduleEvent.saleItem.item.number}}</div>
			<div style="font-size:18px">Customer: {{scheduleEvent.saleItem.sale.customer.name}}</div>
			<div style="font-size:18px">Sale: {{scheduleEvent.saleItem.sale.number}}</div>
		</b-col>
	</b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";


export default {
  name: "edit-component",
  data() {
    return {
	  addInProgress: false,
	  unitsToAdd: 0,
	  people: 0,
	  cd: {},
	  co: {legend: {display: false}},
      availableLines: [],
      line: {},
      availableItems: [],
      item: {},
      dateStarted: moment()
		.format("YYYY-MM-DD"),
	  timeStarted: moment()
          .format("HH:mm:ss"),
	  sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "timeProduced", label: "Time", sortable: true },
        { key: "units", label: "Units Produced", sortable: true },
	  ],
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
	updateChart(){
		this.cd = {
			labels: [this.scheduleEvent.startTime],
			datasets: [{data: [0], lineTension: 0}]
		}
		this.scheduleEvent.productions.forEach(production => {
			this.cd.labels.push(production.finishTime);
			this.cd.datasets[0].data.push(production.unitsProduced);
		})
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
