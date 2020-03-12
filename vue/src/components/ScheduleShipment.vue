<template>
    <b-container fluid>
      <vue-cal ref="vuecal" hide-view-selector :min-event-width=0 :events="events" :time-from="6 * 60" :time-to="18 * 60" 
      :hide-weekdays="[7]">
        <template v-slot:event="{ event, view }">
          <div :id="'popover-'+event.id">
            {{event.customer}}<br/>
            {{event.number}}<br/>
            {{event.dc}}, {{event.city}}, {{event.state}}<br/>
            {{event.load}}<br/>
            {{event.pallets}}<br/>
          </div>
            <b-popover placement="top" :target="'popover-'+event.id">
              <template v-slot:title><b-link role="button" @click="goToShipment(event.id)"><b>Shipment #: </b>{{event.number}}</b-link></template>
                <b>Customer:</b> {{event.customer}}<br/>
                <b>Shipping Address:</b> {{event.dc}}, {{event.city}}, {{event.state}}<br/>
                <b>Load Number:</b> {{event.load}}<br/>
                <b>Total Pallets:</b> {{event.pallets}}
            </b-popover>
        </template>
      </vue-cal>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      visiblePopover: "",
      events: [],
    };
  },
  methods: {
    goToShipment(shipmentId){
      router.push("/shipmentEdit/"+shipmentId);
    },
    getShipmentEvents(){
      http.get("/shipment/events/ready").then(r=> {
        r.data.forEach(e=> {
          e.class = e.klass;
        })
        this.events = r.data;
      }).catch(e=> {
        console.log("API error: " + e);
      })
    }
  },
  mounted() {
    this.getShipmentEvents();
  }
};
</script>

<style>
.vuecal__title-bar {
  min-height: 0 !important;
}
.mpb-default-event {
  background-color: #9ff17566 !important;
  font-size: 10px !important;
  font-weight: 700 !important;
  border: 1px solid black !important;
  color: black !important;
}
</style>
