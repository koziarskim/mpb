<template>
    <b-container fluid>
      <vue-cal ref="vuecal" @cell-click="createEvent" hide-view-selector :min-event-width=0 :events="events" @ready="fetchEvents" @view-change="fetchEvents" :time-from="6 * 60" :time-to="18 * 60" 
      :hide-weekdays="[7]">
        <template v-slot:event="{ event, view }">
          <div @click="editEvent(event)">
            {{event.heading1}}<br/>
            {{event.heading2}}<br/>
          </div>
            <b-popover placement="top" :target="'popover-'+event.id">
              <template v-slot:title>
                <b-link role="button" @click="goToShipment(event.id)"><b>Shipment #: </b>{{event.heading1}}</b-link>
              </template>
                <b>Customer:</b> {{event.heading2}}<br/>
                <b>Shipping Address:</b> {{event.line1}}<br/>
                <b>Load Number:</b> {{event.line2}}<br/>
                <b>Total Pallets:</b> {{event.line3}}
            </b-popover>
        </template>
      </vue-cal>
      <div v-if="calendarEventVisible">
			  <calendar-event :event="event" v-on:close="closeCalendarEvent"></calendar-event>
		</div>  
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

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
      event: {},
      startDate: null,
      endDate: null,
    };
  },
  methods: {
    createEvent(dateObject){
      if(!dateObject.date){
        return;
      }
      this.event = {
        startDate: moment(dateObject.date).format("YYYY-MM-DD"),
        endDate: moment(dateObject.date).format("YYYY-MM-DD"),
        start: dateObject.date.format("YYYY-MM-DD"),
        type: "DELIVERY"
      }
      this.calendarEventVisible = true;
    },
    getStyleClass(calendarEvent){
      var styleClass = "mpb-default-event";
      if(calendarEvent.type == 'SHIPMENT'){
        return "mpb-shipment-event";
      }
      if(calendarEvent.type == 'DELIVERY'){
        return "mpb-delivery-event";
      }
    },
    editEvent(calendarEvent){
      this.event = calendarEvent;
      this.event.startDate = moment(calendarEvent.start).format("YYYY-MM-DD");
      this.event.endDate = moment(calendarEvent.end).format("YYYY-MM-DD");
      this.event.startTime = moment(calendarEvent.start).format("HH:mm");
      this.event.endTime = moment(calendarEvent.end).format("HH:mm");
      this.calendarEventVisible = true;
    },
    closeCalendarEvent(calendarEvent){
      this.fetchEvents({view: null, startDate: this.startDate, endDate: this.endDate, week: null})
      this.calendarEventVisible = false;
    },
    fetchEvents ({ view, startDate, endDate, week }) {
      this.startDate = startDate
      this.endDate = endDate;
      this.events = [];
      this.getShipmentEvents(startDate, endDate);
      this.getCalendarEvents(startDate, endDate);
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
          e.class = this.getStyleClass(e);
        })
        this.events.push.apply(this.events, r.data);
      }).catch(e=> {console.log("API error: " + e);})
    },
    getCalendarEvents(startDate, endDate){
      http.get("/calendarEvent", {params: {
        startDate: startDate.format("YYYY-MM-DD"),
        endDate: endDate.format("YYYY-MM-DD"),
      }}).then(r=> {
        r.data.forEach(e=> {
          e.class = this.getStyleClass(e);
        })
        this.events.push.apply(this.events, r.data);
      }).catch(e=> {console.log("API error: " + e);})
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
.mpb-shipment-event {
  background-color: #9ff17566 !important;
  font-size: 10px !important;
  font-weight: 700 !important;
  border: 1px solid black !important;
  color: black !important;
}
.mpb-delivery-event {
  background-color: #f1a97566 !important;
  font-size: 10px !important;
  font-weight: 700 !important;
  border: 1px solid black !important;
  color: black !important;
}
</style>
