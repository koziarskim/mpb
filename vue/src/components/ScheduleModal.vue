<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: {{schedule.date}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button v-if="scheduleItem.id && (!scheduleItem.productions || scheduleItem.productions.length<=0)" style="margin: 0 2px 0 2px" @click="deleteModal()">Delete</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols="2">
          <label class="top-label">Line:</label>
          <b-select option-value="id" option-text="number" :list="availableLines" v-model="line"></b-select>
        </b-col>
        <b-col cols="3">
          <label class="top-label">Item:</label>
          <b-select v-if="!scheduleItem.id" option-value="id" option-text="number" :list="availableItems" v-model="item"></b-select>
          <span v-if="scheduleItem.id">
            <br>
            {{item.number}}
          </span>
        </b-col>
        <b-col cols="5">
          <label class="top-label">Sale:</label>
          <b-select v-if="!scheduleItem.id" option-value="id" option-text="label" :list="availableSaleItems" v-model="saleItem"></b-select>
          <span v-if="scheduleItem.id">
            <br>
            {{saleItem.label}}
          </span>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols="4">
          <label class="top-label">Start:</label>
          <input class="form-control" type="time" v-model="scheduleItem.startTime">
        </b-col>
        <b-col cols="4">
          <label class="top-label">Units Scheduled:</label>
          <input class="form-control" type="tel" v-model="scheduleItem.unitsScheduled">
        </b-col>
        <b-col cols="4">
          <label class="top-label">Total Sold: {{saleItem.units}}</label>
          <br>
          <label class="top-label">Total Scheduled: {{item.totalScheduled}}</label>
          <br>
          <label class="top-label">Ready to schedule: {{item.unitsReadySchedule}}</label>
          <br>
          <label class="top-label">Ready for production: {{item.unitsReadyProduction}}</label>
        </b-col>
      </b-row>
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
    schedule: Object
  },
  data() {
    return {
      title: "Testing title",
      visible: true,
      availableSaleItems: [], //SaleItemDto
      saleItem: {}, //SaleItemDto
      availableItems: [], //ItemDto
      item: {}, //ItemDto
      availableLines: [],
      line: {}
    };
  },
  computed: {},
  watch: {
    item(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      if (this.item.id) {
        this.getAvailableSaleItems(this.item.id);
      }
    }
  },
  methods: {
    getAvailableSaleItems(item_id) {
      if (!item_id) {
        this.modalData.availableSales = [];
      }
      http
        .get("/saleItem/item/" + item_id)
        .then(response => {
          this.availableSaleItems = response.data;
          this.saleItem = this.scheduleItem.saleItem
            ? this.scheduleItem.saleItem
            : {};
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems(date) {
      var query = "";
      //TODO: This could query only single item instead of all.
      if(this.scheduleItem.id){
          query = "?includeAll=true"
      }
      http
        .get("/item/eta/" + date+query)
        .then(response => {
          this.availableItems = response.data;
          if(this.scheduleItem.item){
              this.item = response.data.find(itemDto=> itemDto.id == this.scheduleItem.item.id);
          }
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
          this.line = this.scheduleItem.line ? this.scheduleItem.line : {};
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
    validate() {
      if (
        !this.line ||
        !this.item ||
        !this.saleItem ||
        !this.scheduleItem.startTime ||
        this.scheduleItem.unitsScheduled <= 0
      ) {
        alert("Make sure all fields are entered");
        return false;
      }
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
      }
      this.scheduleItem.line = { id: this.line.id };
      this.scheduleItem.item = { id: this.item.id };
      this.scheduleItem.saleItem = { id: this.saleItem.id };
      this.scheduleItem.schedule = { id: this.schedule.id };

      http
        .post("/scheduleItem", this.scheduleItem)
        .then(response => {
          this.closeModal();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteModal() {
      http
        .delete("/scheduleItem/" + this.scheduleItem.id)
        .then(response => {
          this.closeModal();
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
    this.getAvailableItems(this.schedule.date);
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
