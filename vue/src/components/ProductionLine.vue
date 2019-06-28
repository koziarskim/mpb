<template>
  <b-container fluid>
    <b-row>
      <b-col cols="3">
        <h4 style="text-align: left;">Production Line: {{line.number}}</h4>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Line:</label>
            <b-select option-value="id" option-text="number" :list="availableLines" v-model="line" placeholder="Select line"></b-select>
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

export default {
  name: "edit-component",
  data() {
    return {
      availableLines: [],
      line: {},
    };
  },
  watch: {
	  line(new_value, old_value){
		  console.log(new_value)
	  }
  },
  methods: {
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
  },
  mounted() {
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
