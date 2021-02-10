<template>
  <b-container fluid>
    <b-modal centered size="md" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=3>
          <div>{{getStartDate()}} </div>
        </b-col>
        <b-col cols=5>
          <input v-if="!event.type.includes('SHIPMENT')" class="form-control" type="date" v-model="startDate">
          <div><b-link v-if="event.type.includes('SHIPMENT')" role="button" @click="goToShipment(event.id)">{{event.heading1}}</b-link></div>
        </b-col>
        <b-col style="display: flex; justify-content: flex-end">
            <b-button v-if="allowEdit()" size="sm" @click="save()" variant="success">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" @click="close()" variant="secondary">Close</b-button>
            <b-button v-if="allowEdit()" size="sm" style="margin-left: 3px" @click="deleteEvent()" variant="secondary">X</b-button>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=4>
          <label class="top-label">From:</label>
          <input :disabled="!allowEdit()" class="form-control" type="time" v-model="event.startTime">
        </b-col>
        <b-col cols=4>
          <label class="top-label">To:</label>
          <input :disabled="!allowEdit()" class="form-control" type="time" v-model="event.endTime">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">{{event.type.includes('SHIPMENT')?'Shipment:':'Purchase/PO:'}}</label>
          <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="event.heading1">
        </b-col>
        <b-col cols=6>
          <label class="top-label">{{event.type.includes('SHIPMENT')?'Vendor:':'Supplier:'}}</label>
          <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="event.heading2">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=12>
          <label class="top-label">Address:</label>
          <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="event.line1">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=6>
          <label class="top-label">Load/Container #:</label>
          <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="event.line2">
        </b-col>
        <b-col cols=6>
          <label class="top-label">Total Pallets:</label>
          <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="event.line3">
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
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
      startDate: "",
    };
  },
  computed: {},
  watch: {
    'event.startTime': {
      deep: true,
      handler(newValue, oldValue){
        this.event.endTime = moment(newValue, "HH:mm").add(1, 'hour').format("HH:mm");
      }
    }
  },
  methods: {
    getStartDate(){
      return moment(this.event.start).format("MMMM, ddd DD");
    },
    goToShipment(shipmentId){
      router.push("/shipmentEdit/"+shipmentId);
    },
    allowEdit(){
      return !this.event.type.includes('SHIPMENT');
    },
    validate() {
      return true;
    },
    close(event) {
      this.$emit("close", event);
    },
    save(){
      if(!this.validate()){return}
      this.event.startDate = moment(this.startDate).format("YYYY-MM-DD");
      this.event.endDate = moment(this.startDate).format("YYYY-MM-DD");
      // this.event.start = moment(this.startDate, "YYYY-MM-dd").set({hour: moment(this.event.startTime, "HH"), minute: moment(this.event.startTime, "mm")});
      http.post("/calendarEvent", this.event).then(r => {
        this.close(this.event);
      });
    },
    deleteEvent(){
      this.$bvModal.msgBoxConfirm("Are you sure you want to delete?").then(ok => {
        if (ok) {
          http.delete("/calendarEvent/"+this.event.id).then(r => {
            this.close();
          });
        }
      });
    }
  },
  mounted() {
    this.startDate = moment(this.event.start).format("YYYY-MM-DD");
  }
};
</script>

<style>
</style>
