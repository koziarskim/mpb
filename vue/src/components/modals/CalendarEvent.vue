<template>
  <b-container fluid>
    <b-modal centered size="md" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=9>
          <div>{{getStartDate()}}</div>
        </b-col>
        <b-col cols=1>
          <div style="display: flex">
            <b-button size="sm" @click="save()" variant="success">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" @click="close()" variant="secondary">Close</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=4>
          <label class="top-label">From:</label>
          <input class="form-control" type="time" v-model="event.startTime">
        </b-col>
        <b-col cols=4>
          <label class="top-label">To:</label>
          <input class="form-control" type="time" v-model="event.endTime">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Purchase/PO:</label>
          <input class="form-control" type="tel" v-model="event.heading1">
        </b-col>
        <b-col cols=6>
          <label class="top-label">Supplier:</label>
          <input class="form-control" type="tel" v-model="event.heading2">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=12>
          <label class="top-label">Address:</label>
          <input class="form-control" type="tel" v-model="event.line1">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Load/Container #:</label>
          <input class="form-control" type="tel" v-model="event.line2">
        </b-col>
        <b-col cols=6>
          <label class="top-label">Total Pallets:</label>
          <input class="form-control" type="tel" v-model="event.line3">
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
  event: {
    type: Object,
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
    getStartDate(){
      return moment(this.event.start).format("MMMM, dddd DD");
    },
    validate() {
      return true;
    },
    close(event) {
      this.$emit("close", event);
    },
    save(){
      http.post("/calendarEvent", this.event).then(r => {
        this.close(this.event);
      }).catch(e=> {console.log("API error: " + e);})
    }
  },
  mounted() {
  }
};
</script>

<style>
</style>
