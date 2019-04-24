<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: {{scheduleItem.schedule.date}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button v-if="!modalData.productionMode && modalData.scheduleItem.id" style="margin: 0 2px 0 2px" @click="deleteModal()">Delete</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button v-if="!modalData.productionMode" style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
      <div style="text-align: left;">
          <div v-if="!modalData.productionMode">
            <b-row>
                <b-col cols="2">
                    <label class="top-label">Line:</label>
                    <b-select v-if="!modalData.scheduleItem.id" option-value="id" option-text="number" :list="modalData.availableLines" v-model="modalData.selectedLine"></b-select>
                    <span v-if="modalData.scheduleItem.id">
                    <br>
                    {{modalData.selectedLine.number}}
                    </span>
                </b-col>
                <b-col cols="3">
                    <label class="top-label">Item:</label>
                    <b-select v-if="!modalData.scheduleItem.id" option-value="id" option-text="number" :list="modalData.availableItems" v-model="modalData.selectedItem"></b-select>
                    <span v-if="modalData.scheduleItem.id"><br>{{modalData.selectedItem.number}}</span>
                </b-col>
                <b-col cols="5">
                    <label class="top-label">Sale:</label>
                    <b-select v-if="!modalData.scheduleItem.id" option-value="id" option-text="label" :list="modalData.availableSales" v-model="modalData.selectedSale"></b-select>
                    <span v-if="modalData.scheduleItem.id"><br>{{modalData.selectedSale.number}}</span>
                </b-col>
                <b-col cols="2">
                    <label class="top-label">Units Transit:</label>
                    <span><br>{{this.modalData.scheduleItem.unitsInTransit}}</span>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols="4">
                    <label class="top-label">Start:</label>
                    <input class="form-control" type="time" v-model="modalData.scheduleItem.startTime">
                </b-col>
                <b-col cols="4">
                    <label class="top-label">Units Scheduled:</label>
                    <input class="form-control" type="tel" v-model="modalData.scheduleItem.unitsScheduled">
                </b-col>
                <b-col cols="4">
                    <label class="top-label">Ready To Schedule:</label>
                    <span><br>{{maxItems}}</span>
                </b-col>
            </b-row>
        </div>
        <div v-if="modalData.productionMode">
            <span>Line: {{modalData.scheduleItem.line.number}}, Item: {{modalData.scheduleItem.item.number}}, 
                Scheduled: {{modalData.scheduleItem.unitsScheduled}}, Produced: {{modalData.scheduleItem.totalProduced}}, 
                Still to produce: {{modalData.scheduleItem.unitsScheduled-modalData.scheduleItem.totalProduced}}</span>
            <br/><br/>
            <div class="d-flex flex-row">
                <span>Produced:</span>
                <input style="width:135px" class="form-control" type="tel" v-model="modalData.newProduction.unitsProduced"/>
                <label>@</label>
                <input style="width:135px" class="form-control" type="time" v-model="modalData.newProduction.finishTime" />
                <a href="#" @click="addProduction()">(+)</a>
            </div>
            <span v-for="production in modalData.scheduleItem.productions" :key="production.id">
                <span>Produced: {{production.unitsProduced}} @ {{production.finishTime}}</span><br/>
            </span>
        </div>
      </div>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "schedule-modal",
  props: {
    scheduleItem: Object,
  },
  data() {
    return {
      title: "Testing title",
      visible: true,
      availableSales: [],
      sale: {},
      availableItems: [],
      item: {},
      availableLines: [],
      line: {}
    };
  },
  computed: {},
  watch: {
      item(new_value, old_value){
        if (!new_value.id || new_value.id == old_value.id) {
            return;
        }
        if (this.item.id) {
            this.getAvailableSales(this.item.id);
        }
      }
  },
  methods: {
    getAvailableSales(item_id) {
      if (!item_id) {
        this.modalData.availableSales = [];
      }
      http
        .get("/sale/item/" + item_id)
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems(date) {
      http
        .get("/item/eta/" + date)
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableLines() {
      http
        .get("/item")
        .then(response => {
          this.availableLines = [
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
    saveProduction(production) {
      return http
        .post("/production", production)
        .then(response => {
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
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
    this.getAvailableItems(this.scheduleItem.schedule.date);
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
