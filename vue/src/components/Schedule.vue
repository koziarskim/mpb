<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <!-- <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span> -->
    </div>
    <b-row class="n-row" style="border-top: 1px solid black;">
      <div class="n-cell">Date</div><div class="n-cell" v-for="line in numberOfLines" :key="line"><div>Line {{line}}</div></div>
    </b-row>
    <b-row class="n-row" style="height: 75px" v-for="s in schedules" :key="s.date">
      <div class="n-cell"><a href="#" @click="showNewModal(s)">{{formatDate(s.date)}}</a></div>
      <div class="n-cell" v-for="line in numberOfLines" :key="line">
          <div :style="getColor(si.unitsShort)" v-for="si in getScheduleItemsByLine(line, s.scheduleItems)" :key="si.id">
              {{si.unitsShort}} {{si.item.number}}
              <a href="#" @click="showEditModal(s, si)">{{formatTime(si.startTime)}}</a> {{si.unitsScheduled}} 
              <a href="#" @click="showEditModal(s, si)">{{si.totalProduced}}</a>
        </div>
      </div>
    </b-row>
    <a href="#" @click="previousDays()">Previous 7 days</a> |
    <a href="#" @click="nextDays()">Next 7 days</a>
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
      numberOfLines: 8,
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
        { key: "line8", sortable: false, label: "Line 8" }
      ]
    };
  },
  computed: {
    maxItems: function() {
      if (this.modalData.scheduleItem.id) {
        var sidto = this.modalData.availableItems.find(
          dto => dto.id == this.modalData.scheduleItem.item.id
        );
        return (
          +this.modalData.tempUnitsScheduled + +(sidto ? sidto.unitsReady : 0)
        );
      }
      return this.modalData.selectedItem.unitsReady;
    }
  },
  watch: {},
  methods: {
    getColor(units) {
      if (units < 0) {
        return "background-color: #f9b3ae";
      }
      return "";
    },
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
        .get("/item/eta/" + date + "?negativeOnly=true")
        .then(response => {
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
              scheduleItem.unitsShort = itemDto.unitsReady;
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
    addProduction() {
      if (
        !this.modalData.newProduction.unitsProduced ||
        this.modalData.newProduction.unitsProduced <= 0
      ) {
        alert("Units produced must be positive");
        return;
      }
      if (
        this.modalData.newProduction.unitsProduced < 0 ||
        +this.modalData.scheduleItem.totalProduced +
          +this.modalData.newProduction.unitsProduced >
          this.modalData.scheduleItem.unitsScheduled
      ) {
        alert("Units produced cannot be more that scheduled");
        return;
      }
      if (!this.modalData.newProduction.finishTime) {
        alert("Enter time");
        return;
      }
      var production = {
        scheduleItem: { id: this.modalData.scheduleItem.id },
        unitsProduced: this.modalData.newProduction.unitsProduced,
        finishTime: this.modalData.newProduction.finishTime
      };
      this.saveProduction(production).then(r => {
        this.modalData.scheduleItem.totalProduced += +this.modalData
          .newProduction.unitsProduced;
        this.modalData.scheduleItem.productions.push(r.data);
        this.modalData.scheduleItem.productions.sort(function(a, b) {
          if (a.finishTime < b.finishTime) {
            return 1;
          }
          if (a.finishTime > b.finishTime) {
            return -1;
          }
          return 0;
        });
        this.modalData.newProduction = {
          unitsProduced:
            +this.modalData.scheduleItem.unitsScheduled -
            +this.modalData.scheduleItem.totalProduced,
          finishTime: moment().format("hh:mm")
        };
      });
    },
    showNewModal(schedule) {
      if (!schedule.id) {
        this.saveSchedule(schedule).then(r => {
          this.schedule = r.data;
          this.scheduleModalVisible = true;
        });
      } else {
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
      (this.scheduleItem = {}), (this.scheduleModalVisible = false);
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

<style lang="scss">
.n-cell{
    width:11.11%;
    border-right: 1px solid black;
    height:100%;
}
.n-row{
   border-bottom: 1px solid black; 
   border-left: 1px solid black 
}
</style>
