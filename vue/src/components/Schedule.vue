<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <span style="text-align: left; font-size: 18px; font-weight: bold">Scheduling</span>
    </div>
    <b-table :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="schedules" :fields="fields">
      <template slot="line1" slot-scope="row">
        <div v-for="scheduleItem in getScheduleItemsByLine(row.field.key, row.item.scheduleItems)" :key="scheduleItem.id">
          <span
            @click="showModal(row.item, scheduleItem)"
          >{{scheduleItem.item.number}} {{formatTime(scheduleItem.startTime)}} {{scheduleItem.unitsScheduled}} {{scheduleItem.totalProduced}}</span>
        </div>
      </template>
      <template slot="date" slot-scope="row">
        <span>{{formatDate(row.item.date)}}</span>
      </template>
      <template slot="action" slot-scope="row">
        <b-button @click="showModal(row.item, null)">+</b-button>
      </template>
    </b-table>
    <b-modal centered v-model="modalVisible" :title="modalData.title" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: {{modalData.schedule.date}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button v-if="modalData.scheduleItem.id" style="margin: 0 2px 0 2px" @click="deleteModal()">Delete</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
      <div style="text-align: left;">
        <b-row>
          <b-col cols="4">
            <label class="top-label">Line:</label>
            <b-select v-if="!modalData.scheduleItem.id" option-value="id" option-text="number" :list="modalData.availableLines" v-model="modalData.selectedLine"></b-select>
            <span v-if="modalData.scheduleItem.id">
              <br>
              {{modalData.selectedLine.number}}
            </span>
          </b-col>
          <b-col cols="4">
            <label class="top-label">Item:</label>
            <b-select
              v-if="!modalData.scheduleItem.id"
              option-value="id"
              option-text="number"
              @change="itemChanged()"
              :list="modalData.availableItems"
              v-model="modalData.selectedItem"
            ></b-select>
            <span v-if="modalData.scheduleItem.id">
              <br>
              {{modalData.selectedItem.number}}
            </span>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="4">
            <label class="top-label">Start:</label>
            <input class="form-control" type="time" v-model="modalData.scheduleItem.startTime">
          </b-col>
          <b-col cols="4">
            <label class="top-label">Units Scheduled:</label>
            <input class="form-control" type="number" min="0" :max="maxItems" v-model="modalData.scheduleItem.unitsScheduled">
          </b-col>
          <b-col cols="4">
            <label class="top-label">Units Available:</label>
            <span>
              <br>
              {{maxItems}}
            </span>
          </b-col>
        </b-row>
        <br/>
        <div class="d-flex flex-row">
          <span>Produced:</span>
          <input style="width:135px" class="form-control" type="tel" @keydown="validateUnitsProduced()" v-model="modalData.newProduction.unitsProduced" />
          <label>@</label>
          <input style="width:135px" class="form-control" type="time" v-model="modalData.newProduction.finishTime" />
          <a href="#" @click="addProduction()">(+)</a>
        </div>
        <b-row v-for="production in modalData.scheduleItem.productions" :key="production.id">
          <b-col>
            <span>Production: {{production.id}}</span>
          </b-col>
        </b-row>
      </div>
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
        scheduleItem: {},
        schedule: {},
        availableItems: [],
        selectedItem: {},
        availableLines: [],
        selectedLine: {},
        newProduction: {}
      },
      modalVisible: false,
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
  watch: {
    "modalData.selectedItem": function(new_value, old_value) {
      if (!new_value.id || new_value.id == old_value.id) {
        return;
      }
      if (!this.modalData.scheduleItem.id) {
        this.modalData.scheduleItem.unitsScheduled = new_value.unitsReady;
      }
    }
  },
  methods: {
    validateUnitsScheduled(){
        // console.log(this.modalData.scheduleItem.unitsScheduled)
        // this.modalData.scheduleItem.unitsScheduled='';
    },
    validateUnitsProduced(){
        // console.log(this.modalData.scheduleItem.unitsScheduled)
        // this.modalData.scheduleItem.unitsScheduled='';
    },
    addProduction(){
        console.log(this.modalData.newProduction)
        this.modalData.scheduleItem.schedule = {id: this.modalData.schedule.id}
        this.modalData.scheduleItem.productions.push(
            {unitsProduced: this.modalData.newProduction.unitsProduced,
            finishTime: this.modalData.newProduction.finishTime}
        )
        this.saveScheduleItem(this.modalData.scheduleItem).then(r=>{
            this.modalData.scheduleItem = r.data;
        });
        this.modalData.newProduction = {};
    },
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
          this.getSchedules();
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveScheduleItem(scheduleItem) {
      return http
        .post("/scheduleItem", scheduleItem)
        .then(response => {
          this.getSchedules();
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteScheduleItem(scheduleItem_id) {
      http
        .delete("/scheduleItem/" + scheduleItem_id)
        .then(response => {
          this.getSchedules();
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    getScheduleItemsByLine(lineNumber, scheduleItems) {
      var lineScheduleItems = [];
      if (scheduleItems) {
        scheduleItems.forEach(scheduleItem => {
          if (
            scheduleItem.line.number == parseInt(lineNumber.replace("line", ""))
          ) {
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
        this.modalData.selectedItem = scheduleItem ? scheduleItem.item : {};
      });
      this.getAvailableLines().then(lines => {
        this.modalData.availableLines = lines;
        this.modalData.selectedLine = scheduleItem ? scheduleItem.line : {};
      });
      this.modalData.title = "Schedule for: " + schedule.date;
      this.modalData.schedule = schedule;
      this.modalData.scheduleItem = scheduleItem
        ? JSON.parse(JSON.stringify(scheduleItem))
        : { startTime: "06:00:00" };
      this.modalData.tempUnitsScheduled = this.modalData.scheduleItem.unitsScheduled;
      this.modalVisible = !this.modalVisible;
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
    },
    saveModal(e) {
      this.validateModal();
      if (this.modalData.schedule.id) {
        this.bindToData(this.modalData.schedule);
      } else {
        this.saveSchedule(this.modalData.schedule).then(r => {
          this.bindToData(r.data);
        });
      }
    },
    bindToData(schedule) {
      this.modalData.scheduleItem.line = {
        id: this.modalData.selectedLine.id
      };
      this.modalData.scheduleItem.item = {
        id: this.modalData.selectedItem.id
      };
      this.modalData.scheduleItem.schedule = {
        id: schedule.id
      };
      this.saveScheduleItem(this.modalData.scheduleItem);
      this.modalVisible = false;
    },
    closeModal() {
      this.modalVisible = false;
    },
    deleteModal() {
      var si = this.modalData.schedule.scheduleItems.find(
        it => it.id == this.modalData.scheduleItem.id
      );
      this.deleteScheduleItem(si.id);
      this.modalVisible = false;
    },
    addScheduleItem(row) {
      console.log(row);
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
