<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
        <b-form-checkbox v-model="itemView">Line View</b-form-checkbox>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
    </b-row>
    <br/>
    <b-row style="font-weight:bold">
      <b-col cols=5>Item<br/> (Sale - Customer)</b-col>
      <b-col cols=1>Total<br/>Sold</b-col>
      <b-col cols=1>Daily<br/>Scheduled</b-col>
      <b-col cols=1>Daily<br/>Produced</b-col>
      <b-col cols=1>Total<br/>Average</b-col>
      <b-col cols=1>Daily<br/>Average</b-col>
      <b-col cols=1>Line #</b-col>
    </b-row>
    <div v-for="item in items" v-bind:key="item.id">
      <b-row>
        <b-col cols=5>
          <b-button size="sm" @click="toggleRow(item)" variant="link">{{item.show?'[-]':'[+]'}}</b-button>{{item.name}}
        </b-col>
        <b-col cols=1>{{item.unitsSold}}</b-col>
        <b-col cols=1>{{item.unitsScheduled}}</b-col>
        <b-col cols=1>{{item.unitsProduced}}</b-col>
        <b-col cols=1>{{roundNumber(item.averageProduced)}}</b-col>
        <b-col cols=1>{{getDailyProduced(item)}}</b-col>
      </b-row>
        <div v-for="event in item.events" v-bind:key="event.id">
          <div v-if="item.show">
          <b-row style="color: gray">
            <b-col cols=5><div style="padding-left:50px">{{"Line "+event.lineNumber+": ("+event.saleNumber + " - " + event.customerName+")"}}</div></b-col>
            <b-col cols=1>{{event.unitsSold}}</b-col>
            <b-col cols=1>{{event.unitsScheduled}}</b-col>
            <b-col cols=1>
              <b-button size="sm" @click="goToProductionLine(event.id)" variant="link">{{event.unitsProduced}}</b-button>
            </b-col>
            <b-col cols=1></b-col>
            <b-col cols=1>{{roundNumber(event.averageProduced)}}</b-col>
            <b-col cols=1>{{event.lineNumber}}</b-col>
          </b-row>
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
      date: moment().format("YYYY-MM-DD"),
      schedule_id: null,
      scheduleEvents: [],
      availableLines: [],
      availableItems: [],
      selectedLine: {},
      selectedItem: {},
      totalScheduled: 0,
      totalProduced: 0,
      items: [],
      itemView: true,
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getItemTree(newValue);
    },
    selectedLine(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    selectedItem(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    itemView(newValue, oldValue){
      if(newValue == false){
        navigation.goTo("/productionLineList/"+this.date)
      }
    }
  },
  methods: {
    roundNumber(number){
      return parseInt(number).toFixed(0);
    },
    getDailyProduced(item){
      var average = 0;
      var count = 0;
      item.events.forEach(event =>{
        if(event.averageProduced == 0){
          return;
        }
        average += +event.averageProduced;
        count ++;
      })
      return (+average / +count).toFixed(0);
    },
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
    getItemTree(date){
      http.get("/item/production/date/"+date).then(response => {
        this.items = response.data;
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
      router.push("/productionLine/" + schedule_event_id);
	  },
  },
  mounted() {
    var date = this.$route.params.date;
    if(date){
      this.date = date;
    }
    this.getItemTree(this.date);
    // this.getScheduleEvents(this.date);
    // this.getAvailableLines();
  }
};
</script>

<style>
</style>
