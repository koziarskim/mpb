<template>
  <b-container fluid>
        <b-button type="reset" size="sm" @click="previousDays()">&#x22D8; Previous week</b-button> &#x22C7; 
        <b-button type="reset" size="sm" @click="nextDays()">Next week &#x22D9;</b-button>
        <b-row class="n-row" style="border-top: 1px solid black;">
          <div class="n-cell" style="width:7%">Date</div>
          <div class="n-cell" v-for="line in numberOfLines" :key="line">
            <div>Line {{line}}</div>
          </div>
        </b-row>
      <b-row class="n-row" style="min-height: 75px" v-for="s in schedules" :key="s.id">
        <div class="n-cell" style="width: 7%;">
          <div>{{dayOfWeek(s.date)}}</div>
          <span>{{formatDate(s.date)}}</span>
          <a style="padding-left: 15%" href="#" @click="newSchedule(s)">(+)</a>
        </div>
        <div class="n-cell" style="margin-top: 3px; margin-bottom: 3px;" v-for="i in 8" :key="i">
          <div v-for="se in getScheduleEventsByLine(i, s.lines)" :key="se.id" style="margin-top: -8px; margin-bottom: -8px; overflow: hidden; white-space: nowrap; text-overflow: ellipsis;">
            <a href="#" @click="editSchedule(se.id)" :title="se.itemName" style="font-size: 15px; ">{{se.itemName}}</a>
          </div>
        </div>
      </b-row>
    <div v-if="scheduleModalVisible">
      <schedule-modal v-on:closeModal="closeScheduleModal()" :schedule="schedule" :scheduleEvent="scheduleEvent"></schedule-modal>
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
      schedules: [],
      scheduleModalVisible: false,
      itemDtos: [],
      scheduleDate: moment()
        .utc().startOf("isoWeek")
        .format("YYYY-MM-DD")
    };
  },
  computed: {},
  watch: {},
  methods: {
    getSchedules(){
      http.get("/schedule/date/" + this.scheduleDate).then(response => {
          this.schedules = [];
          this.schedules.push(this.getScheduleByDate(response.data, this.scheduleDate));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(1, 'days').format("YYYY-MM-DD")));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(2, 'days').format("YYYY-MM-DD")));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(3, 'days').format("YYYY-MM-DD")));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(4, 'days').format("YYYY-MM-DD")));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(5, 'days').format("YYYY-MM-DD")));
          this.schedules.push(this.getScheduleByDate(response.data, moment(this.scheduleDate, "YYYY-MM-DD").add(6, 'days').format("YYYY-MM-DD")));
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getScheduleEvent(se_id){
      return http.get("/scheduleEvent/" + se_id).then(response => {
        return response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getScheduleByDate(schedules, date){
      var schedule = schedules.find(s => s.date == date);
      if(!schedule){
        schedule = {
          id: null,
          date: date
        }
      }
      return schedule;
    },
    getScheduleEventsByLine(lineNumber, lines) {
      var scheduleEvents = [];
      if (lines) {
        var line = lines.find(line => line.number == lineNumber);
        if(line){
          scheduleEvents = line.events;
        }
      }
      return scheduleEvents;
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
    editSchedule(se_id) {
      this.getScheduleEvent(se_id).then(se => {
        this.schedule = se.schedule;
        this.scheduleEvent = se;
        this.scheduleModalVisible = true;
      })
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
}
.n-row {
  border-bottom: 1px solid black;
  border-left: 1px solid black;
}
</style>
