<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
        <b-form-checkbox v-model="itemView">Item View</b-form-checkbox>
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
    <b-table v-if="scheduleEvents.length>0" :sticky-header="browserHeight()" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="fields">
      <template v-slot:cell(line.number)="row">
        <b-button size="sm" @click="goToProductionLine(row.item.line.id)" variant="link">{{row.item.line.id}}</b-button>
      </template>
      <template v-slot:cell(item)="row">
        <b-button size="sm" @click="goToItem(row.item.saleItem.item.id)" variant="link">{{row.item.saleItem.item.name}}</b-button>
		    <span>({{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}})</span>
      </template>
      <template v-slot:cell(totalTime)="row">
		  <span>{{formatter.secondsToTime(row.item.totalTime)}}</span>
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
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import store from "../store.js";
import moment from "moment";
import securite from "../securite";
import navigation from "../utils/navigation";
import formatter from "../utils/formatter"

export default {
  data() {
    return {
      navigation: navigation,
      formatter: formatter,
      date: moment()
        .format("YYYY-MM-DD"),
      sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "line.number", label: "Line", sortable: false },
		    { key: "item", label: "Item (Sale & Customer)", sortable: false },
		    { key: "saleItem.units", label: "Sold", sortable: true },
		    { key: "unitsScheduled", label: "Scheduled", sortable: true },
		    { key: "totalProduced", label: "Produced", sortable: true },
        { key: "unitsPending", label: "Still To Make", sortable: true },
        { key: "totalTime", label: "Total Time", sortable: true },
        { key: "action", label: "Action", sortable: false },
    ],
    schedule: {},
    scheduleEvents: [],
    availableLines: [],
    availableItems: [],
    selectedLine: {},
    selectedItem: {},
    totalScheduled: 0,
    totalProduced: 0,
    itemView: false,
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
    },
    itemView(newValue, oldValue){
      if(newValue==true){
        navigation.goTo("/productionItemList/"+this.date)
      }
    }
  },
  methods: {
    browserHeight(){
      return +window.innerHeight - 150 +"px";
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
      se.schedule = {id: this.schedule.id}
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
	  getScheduleEvents(date){
      http
        .get("/schedule/single/date/"+date)
        .then(response => {
          this.scheduleEvents = [];
          this.totalScheduled = 0;
          this.totalProduced = 0;
          if(response.data){
            this.schedule = response.data;
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
    goToItem(item_id) {
      router.push("/itemEdit/" +item_id);
	  },
    goToProductionLine(line_id) {
      var query = { date: this.schedule.date };
      router.push({ path: "/productionLine/"+line_id, query: query } );
	  },
  },
  mounted() {
    var date = this.$route.params.date;
    if(date){
      this.date = date;
    }
    this.getScheduleEvents(this.date);
    this.getAvailableLines();
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
