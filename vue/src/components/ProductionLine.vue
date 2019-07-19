<template>
  <b-container fluid>
    <b-row>
      <b-col cols="6">
        <span style="font-size: 18px; font-weight: bold">Production Line Output </span>
		<span style="font-size:18px">Started: {{dateStarted}} </span>
		<span v-if="inProgress() || isFinished()" style="font-size:18px">@: {{timeStarted}} </span>
      </b-col>
  	  <b-col cols=2>
		  <input v-if="!inProgress()" class="form-control" type="time" v-model="timeStarted">
	  </b-col>
    </b-row>
    <b-row>
      <b-col cols="2">
        <label class="top-label">Line:</label>
        <b-select v-if="!inProgress() && !isFinished()" option-value="id" option-text="number" :list="availableLines" v-model="line" placeholder="Select line"></b-select>
        <input v-if="inProgress() || isFinished()" class="form-control" type="tel" readonly :value="productionLine.line.number">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Item:</label>
        <b-select v-if="!inProgress() && !isFinished()" option-value="id" option-text="number" :list="availableItems" v-model="item" placeholder="Select item"></b-select>
        <input v-if="inProgress() || isFinished()" class="form-control" type="tel" readonly :value="productionLine.item.number">
      </b-col>
	  <b-col cols=2>
		  <label class="top-label">Scheduled Units:</label>
		  <input :disabled="inProgress() || isFinished()" class="form-control" type="tel" v-model="productionLine.unitsScheduled">
	  </b-col>
	  <b-col cols=1>
		  <label class="top-label">People:</label>
		  <input :disabled="inProgress() || isFinished()" class="form-control" type="tel" v-model="productionLine.people">
	  </b-col>
      <b-col cols="1" v-if="inProgress() && !isFinished()" style="margin-top: 25px">
        <b-button v-if="!addInProgress" type="submit" variant="success" @click="addOutput">Add</b-button>
		<b-button v-if="addInProgress" type="submit" variant="success" @click="saveOutput">Save</b-button>
	</b-col>
	<b-col cols="1" style="margin-top: 25px">
		<input v-if="addInProgress" class="form-control"  type="tel" v-model="unitsToAdd">
      </b-col>
      <b-col cols="2">
        <b-button v-if="!inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="startProduction">Start Production</b-button>
        <b-button v-if="inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="finishProduction">Finish Production</b-button>
      </b-col>
    </b-row>
    <!-- <b-row>
      <b-col cols=4>
        <b-table v-if="productionLine.productionOutputs.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="productionLine.productionOutputs" :fields="fields">
        </b-table>
      </b-col>
    </b-row> -->
	<b-row>
		<chart :chartdata="cd" :options="co" :width="900" :height="300"></chart>
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
	  cd: {},
	  co: {legend: {display: false}},
      productionLine: { line: {}, item: {}, productionOutputs: [] },
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
      ]
    };
  },
  watch: {
    line(new_value, old_value) {
      this.getAvailableItems();
    }
  },
  methods: {
    getProductionLine(production_line_id) {
      http
        .get("/productionLine/" + production_line_id)
        .then(response => {
		  this.productionLine = response.data;
		  this.updateChart();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
	updateChart(){
		this.cd = {
			labels: [this.productionLine.timeStarted],
			datasets: [{data: [0], lineTension: 0}]
		}
		this.productionLine.productionOutputs.forEach(output => {
			this.cd.labels.push(output.timeProduced);
			this.cd.datasets[0].data.push(output.units);
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
      if (!this.validate()) {
        return;
      }
      var productionLine = {
        line: { id: this.line.id },
        item: { id: this.item.id },
        dateStarted: this.dateStarted,
		timeStarted: this.timeStarted,
		people: this.productionLine.people,
		unitsScheduled: this.productionLine.unitsScheduled
      };
      return http
        .post("/productionLine", productionLine)
        .then(response => {
          router.push("/productionLineList");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    finishProduction() {
      this.productionLine.dateFinished = moment()
        .format("YYYY-MM-DD");
      this.productionLine.timeFinished = moment()
        .format("HH:mm:ss");
      return http
        .post("/productionLine", this.productionLine)
        .then(response => {
          router.push("/productionLineList");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addOutput() {
      this.addInProgress = true;
	},
	saveOutput(){
		var productionOutput = {
		  productionLine: {id: this.productionLine.id},
		  dateProduced: this.productionLine.dateStarted,
		  timeProduced: moment().format("HH:mm:ss"),
		  units: this.unitsToAdd
	  };
      return http
        .post("/productionOutput", productionOutput)
        .then(response => {
			this.getProductionLine(this.productionLine.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
		this.addInProgress = false;
	},
    inProgress() {
      return (
        this.productionLine.dateStarted != null &&
        this.productionLine.dateFinished == null
      );
    },
    isFinished() {
      return this.productionLine.dateFinished != null;
    }
  },
  mounted() {
    var production_line_id = this.$route.params.production_line_id;
    if (production_line_id) {
      this.getProductionLine(production_line_id);
    }
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
