<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <!-- <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span> -->
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
        <div :style="getColor(si)" v-for="si in getScheduleItemsByLine(line, s.scheduleItems)" :key="si.id">
          <a href="#" @click="editSchedule(s, si)">{{si.item.number}}: </a>
          <a href="#" @click="editProduction(s, si)">{{si.saleItem.sale?si.saleItem.sale.customer.name:''}}</a>
        </div>
      </div>
    </b-row>
    <a href="#" @click="previousDays()">Previous 7 days</a> |
    <a href="#" @click="nextDays()">Next 7 days</a>
    <div v-if="scheduleModalVisible">
      <schedule-modal v-on:closeModal="closeScheduleModal()" :schedule="schedule" :scheduleItem="scheduleItem"></schedule-modal>
    </div>
    <div v-if="productionModalVisible">
      <production-modal v-on:closeModal="closeProductionModal()" :schedule="schedule" :scheduleItem="scheduleItem"></production-modal>
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
      scheduleItem: {unitsScheduled: 0},
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
          this.schedules.sort(function(a, b) {
            var dateA = new Date(a.date),
              dateB = new Date(b.date);
            return dateA - dateB;
          });
          response.data.forEach(schedule => {
            this.markItemsToReschedule(schedule);
          });
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItemsToReschedule(date) {
      return http
        .get("/item/eta/" + date)
        .then(response => {
          this.itemDtos = response.data;
          return response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    markItemsToReschedule(schedule) {
      this.getItemsToReschedule(schedule.date).then(itemDtos => {
        itemDtos.forEach(itemDto => {
          schedule.scheduleItems.forEach(scheduleItem => {
            if (scheduleItem.item.id == itemDto.id) {
              scheduleItem.unitsShort = itemDto.unitsShort;
              scheduleItem.unitsAvailable = itemDto.unitsAvailable;
            }
          });
        });
      });
    },
    getScheduleItemsByLine(lineNumber, scheduleItems) {
      var lineScheduleItems = [];
      if (scheduleItems) {
        scheduleItems.forEach(scheduleItem => {
          if (scheduleItem.line.number == lineNumber) {
            lineScheduleItems.push(scheduleItem);
          }
        });
      }
      return lineScheduleItems;
    },
    newSchedule(schedule) {
      if (!schedule.id) {
        this.saveSchedule(schedule).then(r => {
          this.schedule = r.data;
          this.scheduleModalVisible = true;
        });
      } else {
        this.scheduleItem = {unitsScheduled: 0, unitsAvailable: 0};
        this.schedule = schedule;
        this.scheduleModalVisible = true;
      }
    },
    editSchedule(schedule, scheduleItem) {
      this.schedule = schedule;
      this.scheduleItem = scheduleItem;
      this.scheduleModalVisible = true;
    },
    editProduction(schedule, scheduleItem) {
      this.schedule = schedule;
      this.scheduleItem = scheduleItem;
      this.productionModalVisible = true;
    },
    validateModal() {
      if (
        !this.modalData.scheduleItem.startTime ||
        !this.modalData.selectedLine ||
        !this.modalData.selectedItem
      ) {
        alert("Time, Line and Item must be selected");
        return;
      }
      if (
        this.modalData.scheduleItem.unitsScheduled < 0 ||
        this.modalData.scheduleItem.unitsScheduled > this.maxItems
      ) {
        alert("Units scheduled cannot exceed available");
        return;
      }
      if (
        this.modalData.scheduleItem.totalProduced > 0 &&
        this.modalData.scheduleItem.unitsScheduled <
          this.modalData.scheduleItem.totalProduced
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
      this.scheduleModalVisible = false;
      this.getSchedules();
    },
    closeProductionModal() {
      this.productionModalVisible = false;
      this.getSchedules();
    },
    deleteModal() {
      var si = this.modalData.schedule.scheduleItems.find(
        it => it.id == this.modalData.scheduleItem.id
      );
      this.deleteScheduleItem(si.id);
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
    dayOfWeek(date){
        if(!date){
            return "";
        }
        return moment(date).utc().format("dddd");
    },
    getColor(si) {
      if (si.unitsShort < 0) {
        return "background-color: #f9b3ae";
      }
      if (si.unitsScheduled == si.totalProduced){
          return "background-color: #c2f5c4"
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
