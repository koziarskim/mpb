<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Production: {{schedule.date}} @ {{formatTime(scheduleEvent.startTime)}}, </span>
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
          <b-row v-for="production in scheduleEvent.productions" :key="production.id">
            <span>Units: {{production.unitsProduced}} @ {{production.finishTime}}</span>
          </b-row>
        </b-col>
        <b-col>
          <label class="top-label">Item: {{scheduleEvent.saleItem.item.number}}</label>
          <br>
          <label class="top-label">S.O.: {{scheduleEvent.saleItem.label}}</label>
          <br>
          <label class="top-label">Line: {{scheduleEvent.line.number}}</label>
          <br>
          <label class="top-label">Scheduled for production: {{scheduleEvent.unitsScheduled}}</label>
          <br>
          <label class="top-label">Still ready for production: {{unitsReadyForProduction}}</label>
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
    scheduleEvent: Object,
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
      itemAvailability: {},
      totalSold: 0,
      availableToSchedule: 0,
      initScheduled: 0,
      newProduction: { unitsProduced: 0 }
    };
  },
  computed: {
    unitsReadyForProduction(){
        return this.itemAvailability.unitsToProduction - +this.newProduction.unitsProduced;
    },
    totalProduced() {
      return (
        +this.scheduleEvent.totalProduced + +this.newProduction.unitsProduced
      );
    },
    unitsDiff() {
      return +this.scheduleEvent.unitsScheduled - +this.initScheduled;
    }
  },
  watch: {},
  methods: {
    getItemAvailability(itemId) {
      http
        .get("/item/available/eta/"+this.schedule.date, {params: {itemIds: itemId}})
        .then(response => {
          this.itemAvailability = response.data[0];
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
      if (this.totalProduced > this.scheduleEvent.unitsScheduled) {
        alert("Units cannot exceed scheduled for production");
        return false;
      }
      if (this.unitsReadyForProduction < 0) {
        alert("Units cannot exceed ready for production");
        return false;
      }
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
      }
      var production = {
        scheduleEvent: { id: this.scheduleEvent.id },
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
        .delete("/scheduleEvent/" + this.scheduleEvent.id)
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
    this.initScheduled = this.scheduleEvent.unitsScheduled;
    this.getItemAvailability(this.scheduleEvent.saleItem.item.id);
    // this.getAvailableLines();
  }
};
</script>

<style>
</style>
