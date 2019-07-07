<template>
  <b-container fluid>
    <b-row>
      <b-col cols="4">
		<span style="text-align: left; font-size: 20px; font-weight: bold">Date: {{productionLine.dateStarted}}</span><br/>
		<span style="text-align: left; font-size: 20px; font-weight: bold">Line: {{productionLine.line.number}}</span><br/>
		<span style="text-align: left; font-size: 20px; font-weight: bold">Item: {{productionLine.item.number}}</span><br/>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Units Produced:</label>
			<input class="form-control" type="tel" v-model="unitsProduced">
          </b-col>
        </b-row>
		<b-row>
          <b-col cols="3">
			<b-button type="submit" variant="success" @click="submitUnits">Submit</b-button>
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
	  unitsProduced: 0
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
		if(!this.unitsProduced){
			alert("Please enter units produced");
			return false;
		}
		return true;
	},
	submitUnits() {
	  if(!this.validate()){
		  return;
	  }
      var productionOutput = {
		  productionLine: {id: this.productionLine.id},
		  dateProduced: this.productionLine.dateStarted,
		  timeProduced: moment().utc().format("hh:mm:ss"),
		  units: this.unitsProduced
	  };
      return http
        .post("/productionOutput", productionOutput)
        .then(response => {
          router.push('/productionLine/'+this.productionLine.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
	},
  },
  mounted() {
    var production_line_id = this.$route.params.production_line_id;
    this.getProductionLine(production_line_id);
  }
};
</script>

<style>
</style>
