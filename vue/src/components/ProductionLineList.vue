<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="3">
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <!-- <b-button type="submit" variant="primary" @click="goToProductionLine('')">Set New Line</b-button> -->
        </div>
      </b-col>
    </b-row>
    <div v-if="productionLines.length==0">No lines set for this date</div>
    <b-table v-if="productionLines.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="productionLines" :fields="fields">
      <template slot="line.number" slot-scope="row">
        <b-button size="sm" @click.stop="goToProductionLine(row.item.id)" variant="link">{{row.item.line.number}}</b-button>
      </template>
      <!-- <template slot="addUnits" slot-scope="row">
		<b-button size="sm" type="submit" :disabled="row.item.dateFinished!=null" variant="success" @click="addUnits(row.item.id)">Add Units</b-button>
      </template>
      <template slot="finishProduction" slot-scope="row">
		<b-button size="sm" type="submit" :disabled="row.item.dateFinished!=null" variant="success" @click="finishProduction(row.item)">Finish</b-button>
      </template> -->
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
      productionLines: [],
      sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "line.number", label: "Line", sortable: true },
        { key: "item.number", label: "Item", sortable: true },
		{ key: "totalTime", label: "Total Time", sortable: true },
		{ key: "totalProduced", label: "Total Produced", sortable: true },
        { key: "unitsPending", label: "Still To Make", sortable: true },
      ]
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getProductionLines(newValue);
    }
  },
  methods: {
	formatTime(secs){
		const duration = moment.duration(secs, 'seconds');
		return duration.hours()+':'+duration.minutes()+':'+duration.seconds();
	},
    getProductionLines(dateStarted) {
      http
        .get("/productionLine/dateStarted/"+dateStarted)
        .then(response => {
          this.productionLines = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToProductionLine(production_line_id) {
      if (production_line_id) {
        router.push("/productionLine/" + production_line_id);
        return;
      }
      router.push("/productionLine");
	},
	addUnits(production_line_id){
		router.push("/productionOutputEdit/" + production_line_id);
	},
	finishProduction(productionLine){
      productionLine.dateFinished = moment()
        .format("YYYY-MM-DD");
      productionLine.timeFinished = moment()
        .format("HH:mm:ss");
      return http
        .post("/productionLine", productionLine)
        .then(response => {
          this.getProductionLines(this.date);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
  },
  mounted() {
    this.getProductionLines(this.date);
  }
};
</script>

<style>
</style>
