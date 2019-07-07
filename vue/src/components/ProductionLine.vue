<template>
  <b-container fluid>
    <b-row>
      <b-col cols="3">
        <h4 style="text-align: left;">Production {{this.date}}</h4>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Line:</label>
            <b-select option-value="id" option-text="number" :list="availableLines" v-model="line" placeholder="Select line"></b-select>
          </b-col>
          <b-col cols="3">
            <label class="top-label">Item:</label>
            <b-select option-value="id" option-text="number" :list="availableItems" v-model="item" placeholder="Select item"></b-select>
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
	  production: {},
      availableLines: [],
	  line: {},
	  availableItems: [],
	  item: {},
	  date: moment().utc().format("YYYY-MM-DD"),
    };
  },
  watch: {
	  line(new_value, old_value){
		  this.getAvailableItems();
	  }
  },
  methods: {
	getProduction(production_id) {
      http
        .get("/production/"+production_id)
        .then(response => {
          this.production = response.data;
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
  },
  mounted() {
	var production_id = this.$route.params.production_id;
	if(production_id){
		this.getProduction(production_id);
	}  
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
