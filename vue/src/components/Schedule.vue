<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span>
    </div>
    <b-table :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="schedules" :fields="fields">
      <template slot="line1" slot-scope="row">
        <div v-for="scheduleItem in getScheduleItemsByLine(row.field.key, row.item.scheduleItems)" :key="scheduleItem.id">
            <span @click="showModal(row.item, scheduleItem)">{{scheduleItem.item.number}} {{scheduleItem.startTime}}  {{scheduleItem.unitsScheduled}} {{scheduleItem.unitsProduced}}</span>
        </div>
      </template>
      <template slot="date" slot-scope="row">
        <span>{{formatDate(row.item.date)}}</span>
      </template>
      <template slot="action" slot-scope="row">
        <b-button @click="showModal(row.item, null)">+</b-button>
      </template>
    </b-table>
    <b-modal centered v-model="modalShow" ok-title="Save" @ok="saveModal">
      <span>Schedule ID: {{modalData.schedule.id}}</span>
      <span>ScheduleLine ID: {{modalData.scheduleLine.id}}</span>
      <input type="time" v-model="modalData.scheduleLine.startTime">
      <span>Date: {{modalData.schedule.date}}</span>
      <b-select option-value="id" option-text="number" :list="modalData.availableLines" v-model="modalData.selectedLine"></b-select>
      <b-select option-value="id" option-text="number" :list="modalData.availableItems" v-model="modalData.selectedItem"></b-select>
    </b-modal>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      schedules: [{ id: 1 }],
      modalData: {
        scheduleLine: {},
        schedule: {},
        availableItems: [],
        selectedItem: {},
        availableLines: [],
        selectedLine: {}
      },
      modalShow: false,
      scheduleDate: moment()
        .utc()
        .format("YYYY-MM-DD"),
      sortBy: "date",
      sortDesc: false,
      fields: [
        { key: "date", sortable: false, label: "Date" },
        { key: "line1", sortable: false, label: "Line 1" },
        { key: "line2", sortable: false, label: "Line 2" },
        { key: "line3", sortable: false, label: "Line 3" },
        { key: "line4", sortable: false, label: "Line 4" },
        { key: "line5", sortable: false, label: "Line 5" },
        { key: "line6", sortable: false, label: "Line 6" },
        { key: "line7", sortable: false, label: "Line 7" },
        { key: "line8", sortable: false, label: "Line 8" },
        { key: "action", sortable: false, label: "Action" }
      ]
    };
  },
  methods: {
    getSchedules() {
      http
        .get("/schedule/date/" + this.scheduleDate)
        .then(response => {
          this.schedules = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSchedule(schedule) {
      return http
        .post("/schedule", schedule)
        .then(response => {
          let schedule = this.schedules.find(s => {
            return s.date === response.data.date;
          });
          this.schedules.splice(
            this.schedules.indexOf(schedule),
            1,
            response.data
          );
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getScheduleItemsByLine(lineNumber, scheduleItems) {
      var lineScheduleItems = [];
      if (scheduleItems) {
        scheduleItems.forEach(scheduleItem => {
          if (scheduleItem.line.number == parseInt(lineNumber.replace("line",""))) {
            lineScheduleItems.push(scheduleItem);
          }
        });
      }
      return lineScheduleItems;
    },
    getAvailableItems(date) {
      if (!date) {
        return [];
      }
      return http
        .get("/item/eta/" + date)
        .then(response => {
          return response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableLines() {
      return http
        .get("/item")
        .then(response => {
          return [
            { id: 1, number: 1 },
            { id: 2, number: 2 },
            { id: 3, number: 3 },
            { id: 4, number: 4 },
            { id: 5, number: 5 },
            { id: 6, number: 6 },
            { id: 7, number: 7 },
            { id: 8, number: 8 }
          ];
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    showModal(schedule, scheduleItem) {
      this.getAvailableItems(schedule.date).then(itemDtos => {
        this.modalData.availableItems = itemDtos;
        this.modalData.selectedItem = {};
      });
      this.getAvailableLines().then(lines => {
        this.modalData.availableLines = lines;
        this.modalData.selectedLine = {};
      });
      this.modalData.schedule = schedule;
      this.modalShow = !this.modalShow;
    },
    validateModal(){
      if (!this.modalData.scheduleLine.startTime || !this.modalData.selectedLine || !this.modalData.selectedItem) {
        alert("Time, Line and Item must be selected");
        return;
      }
    },
    saveModal(e) {
      this.validateModal();
      this.saveSchedule(this.modalData.schedule).then(r =>{
          this.modalData.scheduleLine.line = {id: this.modalData.selectedLine.id};
          this.modalData.scheduleLine.item = {id: this.modalData.selectedItem.id};
        //   this.modalData.scheduleLine.schedule = {id: r.data.id};
        this.modalData.schedule.scheduleItems.push(this.modalData.scheduleLine);
        this.saveSchedule(this.modalData.schedule);
      });

    },
    addScheduleItem(row) {
      console.log(row);
    },
    formatDate(date) {
      return moment(date)
        .utc()
        .format("YYYY-MM-DD");
    }
  },
  mounted() {
    this.getSchedules();
  }
};
</script>

<style>
</style>
