<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
      <b-col cols=1>
        <b-select option-value="id" option-text="number" :list="availableLines" v-model="selectedLine"></b-select>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="selectedItem"></b-select>
      </b-col>
    </b-row>
    <div v-if="scheduleEvents.length==0">No lines set for this date</div>
    <b-table v-if="scheduleEvents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="fields">
      <template slot="line.number" slot-scope="row">
        <b-button size="sm" @click.stop="goToProductionLine(row.item.id)" variant="link">{{row.item.line.number}}</b-button>
      </template>
      <template slot="item" slot-scope="row">
		  <span>{{row.item.saleItem.item.name}} ({{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}})</span>
      </template>
      <template slot="totalTime" slot-scope="row">
		  <span>{{formatTime(row.item.totalTime)}}</span>
      </template>
      <template slot="action" slot-scope="row">
        <b-button size="sm" :disabled="deleteDisabled(row.item)" @click="deleteScheduleEvent(row.item.id)" variant="primary">X</b-button>
      </template>
    </b-table>
    <b-row>
      <b-col cols=2>
        <span>Total Scheduled: {{totalScheduled}}</span>
      </b-col>
      <b-col cols=2>
        <span>Total Produced: {{totalProduced}}</span>
      </b-col>
    </b-row>
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
        { key: "action", label: "Action", sortable: true },
	  ],
    scheduleEvents: [],
    availableLines: [],
    availableItems: [],
    selectedLine: {},
    selectedItem: {},
    totalScheduled: 0,
    totalProduced: 0,
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getScheduleEvents(newValue);
    },
    selectedLine(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    selectedItem(newValue, oldValue){
      this.getScheduleEvents(this.date);
    }
  },
  methods: {
    deleteDisabled(se){
      return se.totalProduced > 0;
    },
    deleteScheduleEvent(se_id){
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(value => {
        if(value){
          http.delete("/scheduleEvent/" + se_id).then(response => {
            this.getScheduleEvents(this.date);
          }).catch(e => {
            console.log("API error: " + e);
          });
        }
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
  getAvailableItems(){
    if(this.selectedItem.id){
      return;
    }
    this.availableItems = [];
    this.scheduleEvents.forEach(event => {
      //Remove duplicates.
      if(this.availableItems.find(item => 
        item.id == event.saleItem.item.id
      )){
        return;
      }
      this.availableItems.push({
        id: event.saleItem.item.id,
        name: event.saleItem.item.name
      })
    })
  },
	formatTime(secs){
		const duration = moment.duration(secs, 'seconds');
		return duration.hours()+':'+duration.minutes()+':'+duration.seconds();
	},
	getScheduleEvents(date){
      http
        .get("/schedule/single/date/"+date)
        .then(response => {
          this.scheduleEvents = [];
          this.totalScheduled = 0;
          this.totalProduced = 0;
          if(response.data){
            response.data.scheduleEvents.forEach(event =>{
              if(this.selectedLine.id && event.line.id != this.selectedLine.id){
                return;
              }
              if(this.selectedItem.id && event.saleItem.item.id != this.selectedItem.id){
                return;
              }
              this.totalScheduled += event.unitsScheduled;
              this.totalProduced += event.totalProduced;
              this.scheduleEvents.push(event)
            })
          }
          this.getAvailableItems();
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
  this.getScheduleEvents(this.date);
  this.getAvailableLines();
  }
};
</script>

<style>
</style>
