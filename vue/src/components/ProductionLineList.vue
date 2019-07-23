<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="3">
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
    </b-row>
    <div v-if="schedule.scheduleEvents.length==0">No lines set for this date</div>
    <b-table v-if="schedule.scheduleEvents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="schedule.scheduleEvents" :fields="fields">
      <template slot="line.number" slot-scope="row">
        <b-button size="sm" @click.stop="goToProductionLine(row.item.id)" variant="link">{{row.item.line.number}}</b-button>
      </template>
      <template slot="item" slot-scope="row">
		  <span>{{row.item.saleItem.item.number}} ({{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}})</span>
      </template>
      <template slot="totalTime" slot-scope="row">
		  <span>{{formatTime(row.item.totalTime)}}</span>
      </template>
    </b-table>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import store from "../store.js";
import moment from "moment";

export default {
  data() {
    return {
      date: moment()
        .format("YYYY-MM-DD"),
      sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "line.number", label: "Line", sortable: true },
		{ key: "item", label: "Item (Sale & Customer)", sortable: true },
		{ key: "unitsScheduled", label: "Units Scheduled", sortable: true },
		{ key: "totalProduced", label: "Total Produced", sortable: true },
        { key: "unitsPending", label: "Still To Make", sortable: true },
		{ key: "totalTime", label: "Total Time", sortable: true },
	  ],
	  schedule: {
		  scheduleEvents: []
	  }
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getSchedule(newValue);
    }
  },
  methods: {
	formatTime(secs){
		const duration = moment.duration(secs, 'seconds');
		return duration.hours()+':'+duration.minutes()+':'+duration.seconds();
	},
	getSchedule(date){
      http
        .get("/schedule/single/date/"+date)
        .then(response => {
			if(response.data){
		  		this.schedule = response.data;
			}else{
				this.schedule = {
					scheduleEvents: []
				}
			}
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
    goToProductionLine(schedule_event_id) {
      if (schedule_event_id) {
        router.push("/productionLine/" + schedule_event_id);
        return;
      }
      router.push("/productionLine");
	},
  },
  mounted() {
	this.getSchedule(this.date);
  }
};
</script>

<style>
</style>
