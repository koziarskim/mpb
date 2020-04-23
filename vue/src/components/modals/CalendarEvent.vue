<template>
  <b-container fluid>
    <b-modal centered size="sm" v-model="visible" :hide-header="true" :hide-footer="true">
      <div style="text-align: right">
      <b-button size="sm" @click="close()" variant="success">Close</b-button>
      </div>
      <b-row>
        <b-col cols=1>
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
// import router from "router";
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
