<template>
  <b-container fluid>
    <b-modal centered size="sm" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=9>
          <div>{{getWeekDay()}}</div>
        </b-col>
        <b-col cols=1>
          <b-button size="sm" @click="close()" variant="success">Close</b-button>
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import moment from "moment";

export default {
  props: {
	startTime: {
    	type: Date,
    	required: true
  	},
	eventType: {
    	type: String,
    	required: true
  	},
  },
  data() {
    return {
      visible: true,
    };
  },
  computed: {},
  watch: {},
  methods: {
    getWeekDay(){
      return moment(this.startTime).format("MMMM, dddd DD")
    },
    validate() {
      return true;
    },
    close() {
      this.$emit("close");
    }
  },
  mounted() {
  }
};
</script>

<style>
</style>
