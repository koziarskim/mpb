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
    <b-table v-if="scheduleEvents.length>100000" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="fields">
      <template v-slot:cell(line.number)="row">
        <b-button size="sm" @click.stop="goToProductionLine(row.item.id)" variant="link">{{row.item.line.number}}</b-button>
      </template>
      <template v-slot:cell(item)="row">
        <b-button size="sm" @click.stop="navigation.goToItemEdit(row.item.saleItem.item.id)" variant="link">{{row.item.saleItem.item.name}}</b-button>
		    <span>({{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}})</span>
      </template>
      <template v-slot:cell(totalTime)="row">
		  <span>{{formatTime(row.item.totalTime)}}</span>
      </template>
      <template v-slot:cell(unitsScheduled)="row">
        <b-button v-if="!row.item.edit" @click="editScheduleEvent(row.item)" variant="light">{{row.item.unitsScheduled}}</b-button>
        <b-input-group>
          <b-form-input style="width:100px" v-if="row.item.edit" class="form-control" type="tel" v-model="row.item.unitsScheduled">
          </b-form-input>
          <b-input-group-append>
            <b-button v-if="row.item.edit" style="margin-left: 5px" variant="link" @click="saveScheduleEvent(row.item)">save</b-button>
          </b-input-group-append>
        </b-input-group>
      </template>
      <template v-slot:cell(action)="row">
        <span v-if="row.item.eventCompleted">Done</span>
        <b-button v-if="!row.item.eventCompleted" size="sm" :disabled="deleteDisabled(row.item)" @click="deleteScheduleEvent(row.item.id)" variant="primary">X</b-button>
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
    <b-row>
      <b-col>Line (Item/Sale)</b-col>
      <b-col>Sold</b-col>
    </b-row>
    <div v-for="line in lines" v-bind:key="line.id">
      <b-row>
        <b-col>
          <b-button size="sm" @click="toggleRow(line)" variant="link">{{line.show?'[-]':'[+]'}}</b-button>{{line.name}}
        </b-col>
        <b-col>{{line.id}}</b-col>
      </b-row>
        <div v-for="item in line.items" v-bind:key="item.id">
          <div v-if="line.show">
          <b-row>
            <b-col>
              <b-button size="sm" @click="toggleRow(item)" variant="link" style="padding-left:30px">{{item.show?'[-]':'[+]'}}</b-button>{{item.name}}
            </b-col>
            <b-col>{{item.id}}</b-col>
          </b-row>
            <div v-for="sale in item.sales" v-bind:key="sale.id">
              <div v-if="item.show">
              <b-row>
                <b-col>
                  <span style="padding-left:65px">{{sale.name}}</span>
                </b-col>
                <b-col>{{sale.id}}</b-col>
              </b-row>
            </div>
          </div>
        </div>
      </div>
    </div>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import store from "../store.js";
import moment from "moment";
import securite from "../securite";
import navigation from "../utils/navigation"

export default {
  data() {
    return {
      navigation: navigation,
      date: moment()
        .format("YYYY-MM-DD"),
      sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "line.number", label: "Line", sortable: true },
		    { key: "item", label: "Item (Sale & Customer)", sortable: true },
		    { key: "saleItem.units", label: "Sold", sortable: true },
		    { key: "unitsScheduled", label: "Scheduled", sortable: true },
		    { key: "totalProduced", label: "Produced", sortable: true },
        { key: "unitsPending", label: "Still To Make", sortable: true },
        { key: "totalTime", label: "Total Time", sortable: true },
        { key: "action", label: "Action", sortable: true },
    ],
    schedule_id: null,
    scheduleEvents: [],
    availableLines: [],
    availableItems: [],
    selectedLine: {},
    selectedItem: {},
    totalScheduled: 0,
    totalProduced: 0,
    lines: []
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getScheduleEvents(newValue);
      this.getProductionTree(newValue)
    },
    selectedLine(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    selectedItem(newValue, oldValue){
      this.getScheduleEvents(this.date);
    }
  },
  methods: {
    toggleRow(row){
      row.show = !row.show;
    },
    editScheduleEvent(se){
      if(!securite.hasRole(['SUPER_USER', 'PROD_ADMIN'])){
        alert("You don't have permission for this operation");
        return;
      }
      se.edit = true;
    },
    saveScheduleEvent(se){
      if(se.unitsScheduled > se.saleItem.units){
        alert("Cannot schedule more that sold");
        return;
      }
      if(se.unitsScheduled < se.totalProduced){
        alert("Cannot schedule less than produced");
        return;
      }
      se.schedule = {id: this.schedule_id}
      http.post("/scheduleEvent", se).then(response => {
        this.getScheduleEvents(this.date)
      }).catch(e => {
        console.log("API error: " + e);
      });
      se.edit = false;
    },
    deleteDisabled(se){
      return se.totalProduced > 0;
    },
    deleteScheduleEvent(se_id){
      if(!securite.hasRole(['SUPER_USER', 'PROD_ADMIN'])){
        alert("You don't have permission for this operation");
        return;
      }
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
    getProductionTree(date){
      http.get("/production/tree/date/"+date).then(response => {
        this.lines = response.data;
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
	  getScheduleEvents(date){
      http
        .get("/schedule/single/date/"+date)
        .then(response => {
          this.scheduleEvents = [];
          this.totalScheduled = 0;
          this.totalProduced = 0;
          if(response.data){
            this.schedule_id = response.data.id;
            response.data.scheduleEvents.forEach(se =>{
              se.edit = false;
              if(this.selectedLine.id && se.line.id != this.selectedLine.id){
                return;
              }
              if(this.selectedItem.id && se.saleItem.item.id != this.selectedItem.id){
                return;
              }
              this.totalScheduled += se.unitsScheduled;
              this.totalProduced += se.totalProduced;
              this.scheduleEvents.push(se)
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
    this.getProductionTree(this.date);
    this.getScheduleEvents(this.date);
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
