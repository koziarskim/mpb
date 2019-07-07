<template>
  <b-container fluid>
    <b-row>
      <b-col cols="3">
        <h4 style="text-align: left;">Production {{this.dateStarted}}</h4>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Line:</label>
            <b-select v-if="!inProgress() && !isFinished()" option-value="id" option-text="number" :list="availableLines" v-model="line" placeholder="Select line"></b-select>
			<input v-if="inProgress() || isFinished()" class="form-control" type="tel" readonly :value="productionLine.line.number">
          </b-col>
          <b-col cols="3">
            <label class="top-label">Item:</label>
            <b-select v-if="!inProgress() && !isFinished()" option-value="id" option-text="number" :list="availableItems" v-model="item" placeholder="Select item"></b-select>
			<input v-if="inProgress() || isFinished()" class="form-control" type="tel" readonly :value="productionLine.item.number">
          </b-col>
          <b-col cols="3">
            <b-button v-if="!inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="startProduction">Start Production</b-button>
			<b-button v-if="inProgress() && !isFinished()" type="submit" style="margin-top: 25px" variant="success" @click="finishProduction">Finish Production</b-button>
          </b-col>
        </b-row>
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
      productionLine: {line: {},item:{}},
      availableLines: [],
      line: {},
      availableItems: [],
      item: {},
      dateStarted: moment().utc().format("YYYY-MM-DD")
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
        })
        .catch(e => {
          console.log("API error: " + e);
        });
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
	validate(){
		if(!this.line.id){
			alert("Please select line");
			return false;
		}
		if(!this.item.id){
			alert("Please select item");
			return false;
		}
		return true;
	},
	startProduction() {
	  if(!this.validate()){
		  return;
	  }
      var productionLine = {
		  line: {id: this.line.id},
		  item: {id: this.item.id},
		  dateStarted: this.dateStarted,
		  timeStarted: moment().utc().format("hh:mm:ss")
	  };
      return http
        .post("/productionLine", productionLine)
        .then(response => {
          router.push('/productionLineList');
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
	finishProduction() {
	  this.productionLine.dateFinished = moment().utc().format("YYYY-MM-DD");
	  this.productionLine.timeFinished = moment().utc().format("hh:mm:ss");
      return http
        .post("/productionLine", this.productionLine)
        .then(response => {
		  router.push('/productionLineList');
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
	inProgress(){
		return this.productionLine.dateStarted != null && this.productionLine.dateFinished==null;
	},
	isFinished(){
		return this.productionLine.dateFinished !=null;
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
