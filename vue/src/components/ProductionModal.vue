<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: {{schedule.date}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Add</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row style="padding-left: 30px">
        <b-col cols="7">
          <b-row class="d-flex flex-row">
            <span>Units:</span>
            <input style="width:135px" class="form-control" type="tel" v-model="newProduction.unitsProduced">
            <label>@</label>
            <input style="width:135px" class="form-control" type="time" v-model="newProduction.finishTime">
          </b-row>
          <b-row>
            <span>Total Production Output: {{totalProduced}}</span>
          </b-row>
          <b-row v-for="production in scheduleItem.productions" :key="production.id">
            <span>Units: {{production.unitsProduced}} @ {{production.finishTime}}</span>
          </b-row>
        </b-col>
        <b-col>
          <label class="top-label">Item: {{item.number}}</label>
          <br>
          <label class="top-label">S.O.: {{saleItem.label}}</label>
          <br>
          <label class="top-label">Line: {{line.number}} @ {{formatTime(scheduleItem.startTime)}}</label>
          <br>
          <label class="top-label">Available to schedule: {{availableToSchedule}}</label>
          <br>
          <label class="top-label">Still to produce: {{stillToProduce}}</label>
          <br>
          <label class="top-label">Scheduled: {{scheduleItem.unitsScheduled}}</label>
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
      visible: true,
      availableSaleItems: [], //SaleItemDto
      saleItem: {},
      availableItems: [],
      allItems: [],
      item: {},
      availableLines: [],
      line: {},
      totalSold: 0,
      availableToSchedule: 0,
      newProduction: { unitsProduced: 0 }
    };
  },
  computed: {
    stillToProduce() {
      return (
        +this.scheduleItem.unitsScheduled -
        +this.scheduleItem.totalProduced -
        +this.newProduction.unitsProduced
      );
    },
    totalProduced(){
        return +this.scheduleItem.totalProduced + +this.newProduction.unitsProduced;
    }
  },
  watch: {
    item(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      if (this.item.id) {
        var itemDto = this.allItems.find(dto => dto.id == this.item.id);
        this.availableToSchedule = itemDto.unitsReady;
        this.getAvailableSaleItems(this.item.id);
      }
    },
    saleItem(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      this.totalSold = this.saleItem.units;
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
      http
        .get("/item/eta/" + date)
        .then(response => {
          this.allItems = response.data;
          this.availableItems = response.data.filter(dto => dto.unitsReady > 0);
          this.item = this.scheduleItem.item ? this.scheduleItem.item : {};
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
    validate() {
      if (
        this.newProduction.unitsProduced <= 0 ||
        !this.newProduction.finishTime
      ) {
        alert("Make sure all fields are entered");
        return false;
      }
      if (this.stillToProduce < 0) {
        alert("Total produced cannot exceed scheduled");
        return false;
      }
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
      }
      var production = {
        scheduleItem: { id: this.scheduleItem.id },
        unitsProduced: this.newProduction.unitsProduced,
        finishTime: this.newProduction.finishTime
      };
      http
        .post("/production", production)
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
    this.getAvailableItems(this.schedule.date);
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
