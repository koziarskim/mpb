<template>
  <b-container fluid>
    <b-modal centered size="md" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=6>
          <div>Sale: {{scheduleEvent.saleItem.sale.number}}</div>
        </b-col>
        <b-col cols=6>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="saveModal()">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" type="reset" variant="secondary" @click="closeModal()">Close</b-button>
          </div>
        </b-col>        
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Line #</label>
          <input class="form-control" type="number" min="1" max="8" v-model="scheduleEvent.line.id">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Start Time</label>
          <input :disabled="scheduleEvent.startTime==null" class="form-control" type="time" v-model="scheduleEvent.startTime">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Finish Time</label>
          <input :disabled="scheduleEvent.finishTime==null" class="form-control" type="time" v-model="scheduleEvent.finishTime">
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import state from "../../data/state";
import moment from "moment";

export default {
  props: {
    scheduleEventId: {
      type: Number,
      required: true
    },
  },
  data() {
    return {
      visible: true,
      scheduleEvent: {
        line: {}
      },
    };
  },
  computed: {
  },
  watch: {},
  methods: {
    save() {
      return http.post("/scheduleEvent/", this.scheduleEvent).then(r => {
        return Promise.resolve();
      }).catch(e => {console.log("API error: " + e);});
    },
    closeModal(){
      this.$emit("close");
    },
    saveModal() {
      this.save().then(r => {
        this.closeModal();
      });
    },
    getScheduleEvent(){
      http.get("/scheduleEvent/"+this.scheduleEventId).then(r=> {
        this.scheduleEvent = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
  },
  mounted() {
    this.getScheduleEvent();
  }
};
</script>

<style>
.modal-lg{
  max-width: 90%;
}
</style>
