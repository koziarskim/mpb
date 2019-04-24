<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span>
    </div>
    <b-table class="table table-bordered" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="schedules" :fields="fields">
      <template slot="line1" slot-scope="row">
        <div v-for="scheduleItem in getScheduleItemsByLine(row.field.key, row.item.scheduleItems)" :key="scheduleItem.id">
          <span>
              {{scheduleItem.item.number}}
              <a href="#" @click="showEditModal(row.item, scheduleItem)">{{formatTime(scheduleItem.startTime)}}</a> {{scheduleItem.unitsScheduled}} 
              <a href="#" @click="showEditModal(row.item, scheduleItem)">{{scheduleItem.totalProduced}}</a>
            </span>
        </div>
      </template>
      <template slot="line2" slot-scope="row">
        <div v-for="scheduleItem in getScheduleItemsByLine(row.field.key, row.item.scheduleItems)" :key="scheduleItem.id">
          <span>
              {{scheduleItem.item.number}}
              <a href="#" @click="showEditModal(row.item, scheduleItem)">{{formatTime(scheduleItem.startTime)}}</a> {{scheduleItem.unitsScheduled}} 
              <a href="#" @click="showEditModal(row.item, scheduleItem)">{{scheduleItem.totalProduced}}</a>
            </span>
        </div>
      </template>
      <template slot="date" slot-scope="row">
          <b-button size="sm" @click.stop="showNewModal(row.item)" variant="link">{{formatDate(row.item.date)}}</b-button>
      </template>
    </b-table>
    <a href="#" @click="previousDays()">Previous 7 days</a> | <a href="#" @click="nextDays()">Next 7 days</a>
    <div v-if="scheduleModalVisible">
        <schedule-modal v-on:closeModal="closeScheduleModal()" :schedule="schedule" :scheduleItem="scheduleItem"></schedule-modal>
    </div>
    <div v-if="productionModalVisible">
        <schedule-modal v-on:closeModal="closeProductionModal()" :schedule="schedule" :scheduleItem="scheduleItem"></schedule-modal>
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
      scheduleItem: {},
      schedule: {},
      schedules: [{ id: 1 }],
      scheduleModalVisible: false,
      productionModalVisible: false,
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
      ]
    };
  },
  computed: {
    maxItems: function() {
      if (this.modalData.scheduleItem.id) {
        var sidto = this.modalData.availableItems.find(
          dto => dto.id == this.modalData.scheduleItem.item.id
        );
        return (+this.modalData.tempUnitsScheduled + +(sidto ? sidto.unitsReady : 0));
      }
      return this.modalData.selectedItem.unitsReady;
    }
  },
  watch: {},
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
    getScheduleItemsByLine(lineNumber, scheduleItems) {
      var lineScheduleItems = [];
      if (scheduleItems) {
        scheduleItems.forEach(scheduleItem => {
          if (scheduleItem.line.number == parseInt(lineNumber.replace("line", ""))) {
            lineScheduleItems.push(scheduleItem);
          }
        });
      }
      return lineScheduleItems;
    },
    addProduction(){
        if(!this.modalData.newProduction.unitsProduced || this.modalData.newProduction.unitsProduced <=0){
            alert("Units produced must be positive");
            return;
        }
        if(this.modalData.newProduction.unitsProduced<0 || (+this.modalData.scheduleItem.totalProduced + +this.modalData.newProduction.unitsProduced) > this.modalData.scheduleItem.unitsScheduled){
            alert("Units produced cannot be more that scheduled");
            return;
        }
        if(!this.modalData.newProduction.finishTime){
            alert("Enter time");
            return;
        }
        var production = {
                scheduleItem: {id: this.modalData.scheduleItem.id},
                unitsProduced: this.modalData.newProduction.unitsProduced,
                finishTime: this.modalData.newProduction.finishTime
            }
        this.saveProduction(production).then(r=>{
            this.modalData.scheduleItem.totalProduced += +this.modalData.newProduction.unitsProduced;
            this.modalData.scheduleItem.productions.push(r.data);
            this.modalData.scheduleItem.productions.sort(function(a, b){  
                if (a.finishTime < b.finishTime) {return 1;}
                if (a.finishTime > b.finishTime) {return -1;}
                return 0;});
            this.modalData.newProduction = {unitsProduced: (+this.modalData.scheduleItem.unitsScheduled - +this.modalData.scheduleItem.totalProduced),
            finishTime : moment().format("hh:mm")};
        });
    },
    showNewModal(schedule){
        if(!schedule.id){
            this.saveSchedule(schedule).then(r=>{
                this.schedule = r.data;
                this.scheduleModalVisible = true;
            })
        }else{
            this.schedule = schedule;
            this.scheduleModalVisible = true;
        }
    },
    showEditModal(schedule, scheduleItem) {
        this.schedule = schedule;
        this.scheduleItem = scheduleItem;
        this.scheduleModalVisible = true;
    },
    validateModal() {
        if (!this.modalData.scheduleItem.startTime || !this.modalData.selectedLine || !this.modalData.selectedItem) {
            alert("Time, Line and Item must be selected");
            return;
        }
        if(this.modalData.scheduleItem.unitsScheduled < 0 || this.modalData.scheduleItem.unitsScheduled > this.maxItems){
            alert("Units scheduled cannot exceed available");
            return;
        }
        if(this.modalData.scheduleItem.totalProduced > 0 && this.modalData.scheduleItem.unitsScheduled < this.modalData.scheduleItem.totalProduced){
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
      this.scheduleItem = {},
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
    nextDays(){
         this.scheduleDate = moment(this.scheduleDate).add(7, 'days').utc().format("YYYY-MM-DD");
         this.getSchedules();
    },
    previousDays(){
         this.scheduleDate = moment(this.scheduleDate).subtract(7, 'days').utc().format("YYYY-MM-DD");
         this.getSchedules();
    },
    formatDate(date) {
      return moment(date)
        .utc()
        .format("YYYY-MM-DD");
    },
    formatTime(time) {
      if (!time) {
        return "";
      }
      var hours = time.split(":")[0];
      var mins = time.split(":")[1];
      return hours + ":" + mins;
    }
  },
  mounted() {
    this.getSchedules();
  }
};
</script>

<style>
</style>
