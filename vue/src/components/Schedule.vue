<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
    </div>
    <b-row class="n-row" style="border-top: 1px solid black;">
      <div class="n-cell" style="width:7%">Date</div>
      <div class="n-cell" v-for="line in numberOfLines" :key="line">
        <div>Line {{line}}</div>
      </div>
    </b-row>
    <b-row class="n-row" style="height: 75px" v-for="s in schedules" :key="s.date">
      <div class="n-cell" style="width: 7%">
        <div>{{dayOfWeek(s.date)}}</div>
        <span>{{formatDate(s.date)}}</span>
        <a style="padding-left: 15%" href="#" @click="newSchedule(s)">(+)</a>
      </div>
      <div class="n-cell" v-for="line in numberOfLines" :key="line">
        <!-- <div :style="getColor(se, s)" v-for="se in getScheduleEventsByLine(line, s.scheduleEvents)" :key="se.id"> -->
        <div v-for="se in getScheduleEventsByLine(line, s.scheduleEvents)" :key="se.id">
          <a href="#" @click="editSchedule(s, se)">{{se.saleItem.item.number}}:{{se.saleItem.sale?se.saleItem.sale.customer.name:''}}</a>
        </div>
      </div>
    </b-row>
    <a href="#" @click="previousDays()">Previous 7 days</a> |
    <a href="#" @click="nextDays()">Next 7 days</a>
    <div v-if="scheduleModalVisible">
      <schedule-modal v-on:closeModal="closeScheduleModal()" :schedule="schedule" :scheduleEvent="scheduleEvent"></schedule-modal>
    </div>
    <div v-if="productionModalVisible">
      <production-modal v-on:closeModal="closeProductionModal()" :schedule="schedule" :scheduleEvent="scheduleEvent"></production-modal>
    </div>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      numberOfLines: 8,
      scheduleEvent: { schedule:{}, unitsScheduled: 0 },
      schedule: {},
      schedules: [{ id: 1 }],
      scheduleModalVisible: false,
      productionModalVisible: false,
      itemDtos: [],
      scheduleDate: moment()
        .utc()
        .format("YYYY-MM-DD")
    };
  },
  computed: {},
  watch: {},
  methods: {
    getSchedules() {
      http
        .get("/schedule/date/" + this.scheduleDate)
        .then(response => {
            this.schedules = response.data;
        //   response.data.forEach(schedule => {
        //     this.markItemsToReschedule(schedule).then(r => {
        //       this.schedules = response.data;
        //       this.schedules.sort(function(a, b) {
        //         var dateA = new Date(a.date),
        //           dateB = new Date(b.date);
        //         return dateA - dateB;
        //       });
        //     });
        //   });
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    // getItemsToReschedule(date) {
    //   return http
    //     .get("/item/eta/" + date)
    //     .then(response => {
    //       this.itemDtos = response.data;
    //       return response.data;
    //     })
    //     .catch(e => {
    //       console.log("API error: " + e);
    //     });
    // },
    // markItemsToReschedule(schedule) {
    //   return this.getItemsToReschedule(schedule.date).then(itemDtos => {
    //     itemDtos.forEach(itemDto => {
    //       schedule.scheduleEvents.forEach(scheduleEvent => {
    //         if (scheduleEvent.item.id == itemDto.id) {
    //           scheduleEvent.unitsShort = itemDto.unitsShort;
    //           scheduleEvent.unitsAvailable = itemDto.unitsAvailable;
    //           scheduleEvent.unitsReadyProduction = itemDto.unitsReadyProduction;
    //           scheduleEvent.totalUnitsScheduled = itemDto.unitsScheduled;
    //         }
    //       });
    //     });
    //     return "";
    //   });
    // },
    getScheduleEventsByLine(lineNumber, scheduleEvents) {
      var lineScheduleEvents = [];
      if (scheduleEvents) {
        scheduleEvents.forEach(scheduleEvent => {
          if (scheduleEvent.line.number == lineNumber) {
            lineScheduleEvents.push(scheduleEvent);
          }
        });
      }
      return lineScheduleEvents;
    },
    newSchedule(schedule) {
      if (!schedule.id) {
        this.saveSchedule(schedule).then(r => {
          this.schedule = r.data;
          this.scheduleModalVisible = true;
        });
      } else {
        this.scheduleEvent = {schedule: {}, line: {}, saleItem:{}, productions: [], unitsScheduled:0};
        this.schedule = schedule;
        this.scheduleModalVisible = true;
      }
    },
    editSchedule(schedule, scheduleEvent) {
      this.schedule = schedule;
      this.scheduleEvent = scheduleEvent;
      this.scheduleModalVisible = true;
    },
    validateModal() {
      if (
        !this.modalData.scheduleEvent.scheduleTime ||
        !this.modalData.selectedLine ||
        !this.modalData.selectedItem
      ) {
        alert("Time, Line and Item must be selected");
        return;
      }
      if (
        this.modalData.scheduleEvent.unitsScheduled < 0 ||
        this.modalData.scheduleEvent.unitsScheduled > this.maxItems
      ) {
        alert("Units scheduled cannot exceed available");
        return;
      }
      if (
        this.modalData.scheduleEvent.totalProduced > 0 &&
        this.modalData.scheduleEvent.unitsScheduled <
          this.modalData.scheduleEvent.totalProduced
      ) {
        alert("Units scheduled cannot be less that total produced");
        return;
      }
      return true;
    },
    saveSchedule(schedule) {
      return http
        .post("/schedule", schedule)
        .then(response => {
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    closeScheduleModal() {
      this.scheduleEvent = {unitsScheduled: 0 };
      this.schedule = {};
      this.scheduleModalVisible = false;
      this.getSchedules();
    },
    closeProductionModal() {
      this.productionModalVisible = false;
      this.getSchedules();
    },
    deleteModal() {
      var si = this.modalData.schedule.scheduleEvents.find(
        it => it.id == this.modalData.scheduleEvent.id
      );
      this.deleteScheduleEvent(si.id);
      this.scheduleModalVisible = false;
    },
    nextDays() {
      this.scheduleDate = moment(this.scheduleDate)
        .add(7, "days")
        .utc()
        .format("YYYY-MM-DD");
      this.getSchedules();
    },
    previousDays() {
      this.scheduleDate = moment(this.scheduleDate)
        .subtract(7, "days")
        .utc()
        .format("YYYY-MM-DD");
      this.getSchedules();
    },
    formatDate(date) {
      return moment(date)
        .utc()
        .format("MM/DD");
    },
    formatTime(time) {
      if (!time) {
        return "";
      }
      var hours = time.split(":")[0];
      var mins = time.split(":")[1];
      return hours + ":" + mins;
    },
    dayOfWeek(date) {
      if (!date) {
        return "";
      }
      return moment(date)
        .utc()
        .format("dddd");
    },
    getColor(se, s) {
      //Green - production is done.
      if (se.unitsScheduled == se.totalProduced) {
        return "background-color: #c2f5c4";
      }
      var item = s.items.find(i => i.id == se.saleItem.item.id)
      //Red - too much scheduled. Needs to reschedule.
      if (item.unitsToSchedule < se.unitsScheduled - se.totalProduced) {
        return "background-color: yellow";
      }
      //Yellow - not ready for production.
      if (item.unitsToProduction < se.unitsScheduled - se.totalProduced) {
        return "background-color: #f9b3ae";
      }
      return "";
    }
  },
  mounted() {
    this.getSchedules();
  }
};
</script>

<style lang="scss">
.n-cell {
  width: 11.62%;
  border-right: 1px solid black;
  height: 100%;
}
.n-row {
  border-bottom: 1px solid black;
  border-left: 1px solid black;
}
</style>
