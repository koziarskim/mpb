<template>
    <b-container fluid>
      <vue-cal ref="vuecal" @cell-click="createEvent" hide-view-selector :min-event-width=0 :events="events" @ready="fetchEvents" @view-change="fetchEvents" :time-from="6 * 60" :time-to="18 * 60" 
      :hide-weekdays="[7]">
        <template v-slot:event="{ event, view }">
          <div :id="'popover-'+event.id">
            {{event.heading1}}<br/>
            {{event.heading2}}<br/>
          </div>
            <b-popover placement="top" :target="'popover-'+event.id">
              <template v-slot:title>
                <b-link role="button" @click="goToShipment(event.id)"><b>Shipment #: </b>{{event.heading1}}</b-link>
              </template>
                <b>Customer:</b> {{event.line1}}<br/>
                <b>Shipping Address:</b> {{event.line2}}<br/>
                <b>Load Number:</b> {{event.line3}}<br/>
                <b>Total Pallets:</b> {{event.line4}}
            </b-popover>
        </template>
      </vue-cal>
      <div v-if="calendarEventVisible">
			  <calendar-event event-type="DELIVERY" :start-time="startTime" v-on:close="closeCalendarEvent"></calendar-event>
		</div>  
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  components: {
    CalendarEvent: () => import("./modals/CalendarEvent"),
  },
  data() {
    return {
      calendarEventVisible: false,
      startTime: null,
      visiblePopover: "",
      events: [],
    };
  },
  methods: {
    createEvent(dateObject){
      if(!dateObject.date){
        return;
      }
      this.startTime = dateObject.date;
      this.calendarEventVisible = true;
    },
    closeCalendarEvent(event){
      if(event){
        //TODO: http POST
        this.events.push(event);
      }
      this.calendarEventVisible = false;
    },
    fetchEvents ({ view, startDate, endDate, week }) {
      this.getShipmentEvents(startDate, endDate);
    },
    goToShipment(shipmentId){
      router.push("/shipmentEdit/"+shipmentId);
    },
    getShipmentEvents(startDate, endDate){
      http.get("/shipment/events", {params: {
        startDate: startDate.format("YYYY-MM-DD"),
        endDate: endDate.format("YYYY-MM-DD"),
      }}).then(r=> {
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
