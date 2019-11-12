<template>
  <b-container fluid>
    <b-row>
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
        <b-form-checkbox size="sm" v-model="itemView">Item View</b-form-checkbox>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
    </b-row>
    <b-row style="font-weight:bold; background-color:lightgray">
      <b-col cols=4>Item<br/> (Sale - Customer)</b-col>
      <b-col cols=1>Total<br/>Sold</b-col>
      <b-col cols=1>Total<br/>Produced</b-col>
      <b-col cols=1>Total %<br/>Produced</b-col>
      <b-col cols=1>Daily [u]<br/>Sched/Prod</b-col>
      <b-col cols=1>Daily %<br/>Produced</b-col>
      <b-col cols=1>Daily<br/>Duration</b-col>
      <b-col cols=1>Perf. [u/h]<br/>Total/Daily</b-col>
      <b-col cols=1>Perf. %<br/>Total/Daily</b-col>
    </b-row>
    <div v-for="treeItem in treeItems" v-bind:key="treeItem.id">
      <b-row>
        <b-col cols=4 style="overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
          <b-button size="sm" @click="toggleRow(treeItem)" variant="link">{{treeItem.show?'[-]':'[+]'}}</b-button>{{treeItem.name}}
        </b-col>
        <b-col cols=1>{{treeItem.totalSold}}</b-col>
        <b-col cols=1>
          {{treeItem.totalProduced}}
        </b-col>
        <b-col cols=1>{{percOut(treeItem.totalProduced,treeItem.totalSold)}}</b-col>
        <b-col cols=1>{{treeItem.dailyScheduled+"/"+treeItem.dailyProduced}}</b-col>
        <b-col cols=1>{{percOut(treeItem.dailyProduced,treeItem.dailyScheduled)}}</b-col>
        <b-col cols=1>{{formatter.secondsToTime(treeItem.dailySeconds)}}</b-col>
        <b-col cols=1>{{treeItem.totalAverage+"/"+treeItem.dailyAverage}}</b-col>
        <b-col cols=1>{{percPerf(treeItem.dailyAverage,treeItem.totalAverage)}}</b-col>
      </b-row>
        <div v-for="event in treeItem.events" v-bind:key="event.id">
          <div v-if="treeItem.show">
          <b-row style="color: gray; font-style: italic">
            <b-col cols=4><div style="padding-left:50px">{{"Line "+event.lineNumber+": ("+event.saleNumber + " - " + event.customerName+")"}}</div></b-col>
            <b-col cols=1>{{event.unitsSold}}</b-col>
            <b-col cols=1>{{event.saleTotalProduced}}</b-col>
            <b-col cols=1>{{percOut(event.saleTotalProduced,event.unitsSold)}}</b-col>
            <b-col cols=1><a href="#" @click="goToProductionLine(event.id)">{{event.dailyScheduled+"/"+event.dailyProduced}}</a></b-col>
            <b-col cols=1>{{percOut(event.dailyProduced,event.dailyScheduled)}}</b-col>
            <b-col cols=1>{{formatter.secondsToTime(event.dailySeconds)}}</b-col>
            <b-col cols=1>{{treeItem.totalAverage+"/"+event.dailyAverage}}</b-col>
            <b-col cols=1>{{percPerf(event.dailyAverage,treeItem.totalAverage)}}</b-col>
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
import formatter from "../utils/formatter";

export default {
  data() {
    return {
      navigation: navigation,
      formatter: formatter,
      date: moment().format("YYYY-MM-DD"),
      treeItems: [],
      itemView: true,
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getItemTree(newValue);
    },
    itemView(newValue, oldValue){
      if(newValue == false){
        navigation.goTo("/productionLineList/"+this.date)
      }
    }
  },
  methods: {
    toggleRow(row){
      row.show = !row.show;
    },
    getItemTree(date){
      http.get("/item/production/date/"+date).then(response => {
        this.treeItems = response.data;
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
    goToProductionLine(schedule_event_id) {
      router.push("/productionLine/" + schedule_event_id);
    },
    percPerf(high, low){
      if(high==0 || low==0){
        return "-"
      }
      return (((high - low) / ((high + low)/2)) * 100).toFixed(0)+"%";
    },
    percOut(high, low){
      return ((high / low) * 100).toFixed(0)+"%";
    }

  },
  mounted() {
    var date = this.$route.params.date;
    if(date){
      this.date = date;
    }
    this.getItemTree(this.date);
  }
};
</script>

<style>
</style>
