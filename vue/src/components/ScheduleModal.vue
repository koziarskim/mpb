<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: {{schedule.date}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button v-if="scheduleEvent.id && (!scheduleEvent.productions || scheduleEvent.productions.length<=0)" style="margin: 0 2px 0 2px" @click="deleteModal()">Delete</b-button>
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
        <b-col cols="5">
          <label class="top-label">Sale/S.O.:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="value" :list="availableSales" v-model="kvSale"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{kvSale.value}}
          </span>
        </b-col>
        <b-col cols="3">
          <label class="top-label">Item:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="value" :list="availableSaleItems" v-model="kvSaleItem"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{kvSaleItem.value}}
          </span>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols="4">
          <label class="top-label">Start:</label>
          <input class="form-control" type="time" v-model="scheduleEvent.startTime">
        </b-col>
        <b-col cols="4">
          <label class="top-label">Units Scheduled:</label>
          <input class="form-control" type="tel" v-model="scheduleEvent.unitsScheduled">
        </b-col>
        <b-col cols="4" v-if="itemAvailability.id">
          <label class="top-label">Total Sold: {{saleItem.units}}</label>
          <br>
          <label class="top-label">Total scheduled: {{unitsTotalScheduled}}</label>
          <br>
          <label class="top-label">Total produced: {{saleItem.unitsProduced}}</label>
          <br>
          <label class="top-label">Still ready to schedule: {{unitsReadyToSchedule}}</label>
          <br>
          <label class="top-label">Still ready for production: {{unitsReadyForProduction}}</label>

          <!-- <br>
          <label class="top-label">Total scheduled: {{itemAvailability.unitsScheduled}}</label>-->
          <br>
          <label class="top-label">Diff: {{unitsDiff}}</label>
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
      availableSales: [],
      kvSale: {},
      availableSaleItems: [], //SaleItemDto
      kvSaleItem: {}, //SaleItemDto
      saleItem: {},
      availableItems: [], //ItemDto
      item: {}, //ItemDto
      itemAvailability: {},
      availableLines: [],
      line: {},
      initScheduled: 0
    };
  },
  computed: {
    unitsReadyToSchedule() {
       return +this.itemAvailability.unitsToSchedule - +this.unitsDiff;
    },
    unitsReadyForProduction() {
      return +this.itemAvailability.unitsToProduction;
    },
    unitsTotalScheduled() {
      return +this.saleItem.unitsScheduled + +this.unitsDiff;
    },
    unitsDiff() {
      return +this.scheduleEvent.unitsScheduled - +this.initScheduled;
    }
  },
  watch: {
    kvSale(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      if (this.kvSale.id) {
        this.getAvailableSaleItems(this.kvSale.id);
      }
    },
    kvSaleItem(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      if (this.kvSaleItem.id) {
        this.getSaleItem(this.kvSaleItem.id);
      }
    }
  },
  methods: {
    getAvailableSaleItems(sale_id) {
      if (!sale_id) {
        this.modalData.availableSales = [];
      }
      http
        .get("/saleItem/sale/" + sale_id)
        .then(response => {
          this.availableSaleItems = response.data;
          if (this.scheduleEvent.id) {
            this.kvSaleItem = response.data.find(
              kvDto => kvDto.id == this.scheduleEvent.saleItem.id
            );
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSales() {
      http
        .get("/sale/available")
        .then(response => {
          this.availableSales = response.data;
          if (this.scheduleEvent.id) {
            this.kvSale = response.data.find(
              kvDto => kvDto.id == this.scheduleEvent.saleItem.sale.id
            );
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableLines() {
      http
        // TODO: Need to change it to line url.
        .get("/item/1")
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
          this.line = this.scheduleEvent.line ? this.scheduleEvent.line : {};
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getSaleItem(saleItemId) {
      http
        .get("/saleItem/" + saleItemId)
        .then(response => {
          this.saleItem = response.data;
          this.getItemAvailability(response.data.item.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItemAvailability(itemId) {
      http
        .get("/item/available/eta/" + this.schedule.date, {
          params: { itemIds: itemId }
        })
        .then(response => {
          this.itemAvailability = response.data[0];
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate() {
      if (
        !this.line ||
        !this.saleItem ||
        !this.scheduleEvent.startTime ||
        this.scheduleEvent.unitsScheduled <= 0
      ) {
        alert("Make sure all fields are entered");
        return false;
      }
      if (this.scheduleEvent.unitsScheduled > this.saleItem.units) {
        alert("Units scheduled cannot exceed sold");
        return false;
      }
      if (this.scheduleEvent.unitsScheduled > this.unitsReadyToSchedule) {
        alert("Cannot schedule more that available to schedule");
        return false;
      }
      return true;
    },
    saveModal() {
      if (!this.validate()) {
        return;
      }
      this.scheduleEvent.line = { id: this.line.id };
      this.scheduleEvent.saleItem = { id: this.saleItem.id };
      this.scheduleEvent.schedule = { id: this.schedule.id };

      http
        .post("/scheduleEvent", this.scheduleEvent)
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
    }
  },
  mounted() {
    if (this.scheduleEvent.id) {
      this.initScheduled = this.scheduleEvent.unitsScheduled;
    }
    this.getAvailableSales();
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
