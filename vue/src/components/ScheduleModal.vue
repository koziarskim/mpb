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
        <b-col cols=8>
      <b-row>
        <b-col cols="3">
          <label class="top-label">Line:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="number" :list="availableLines" v-model="line"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{line.number}}
          </span>
        </b-col>
        <b-col cols=6 offset=3>
          <label class="top-label">Customer:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="value" :list="availableCustomers" v-model="kvCustomer"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{kvCustomer.value}}
          </span>
        </b-col>
		</b-row>
		<b-row>
        <b-col cols="6">
          <label class="top-label">Sale/S.O.:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="value" :list="availableSales" v-model="kvSale"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{kvSale.value}}
          </span>
        </b-col>
        <b-col cols="6">
          <label class="top-label">Item:</label>
          <b-select v-if="!scheduleEvent.id" option-value="id" option-text="name" :list="availableSaleItems" v-model="kvSaleItem"></b-select>
          <span v-if="scheduleEvent.id">
            <br>
            {{kvSaleItem.name}}
          </span>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=5>
          <label class="top-label">Scheduled to Start:</label>
          <input :disabled="scheduleEvent.id" class="form-control" type="time" v-model="scheduleEvent.scheduleTime">
        </b-col>
        <b-col cols=6 offset=1>
          <label class="top-label">Units Scheduled:</label>
          <input :disabled="scheduleEvent.id" class="form-control" type="tel" v-model="scheduleEvent.unitsScheduled">
        </b-col>
      </b-row>
      </b-col>
        <b-col cols="4">
          <br/>
          <label class="top-label">Sold (this sale only): {{saleItem.units}}</label>
          <br/>
          <label class="top-label">Scheduled (this sale only): {{saleItem.unitsScheduled}}</label>
          <br/>
          <label class="top-label">Produced (this sale only): {{saleItem.unitsProduced}}</label>
          <br/>
          <label class="top-label">Total Sold (all sales): {{saleItem.item.unitsSold}}</label>
          <br/>
          <label class="top-label">Total Scheduled (all sales): {{saleItem.item.unitsScheduled}}</label>
          <br/>
          <label class="top-label">Total Produced (all sales): {{saleItem.item.unitsProduced}}</label>
        </b-col>
      <b-col cols=4>
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
	  availableCustomers: [],
	  kvCustomer: {},
      availableSales: [],
      kvSale: {},
      availableSaleItems: [], //SaleItemDto
      kvSaleItem: {}, //SaleItemDto
      saleItem: {item: {}},
      availableItems: [], //ItemDto
      item: {}, //ItemDto
      availableLines: [],
      line: {},
      initScheduled: 0
    };
  },
  computed: {
  },
  watch: {
    kvCustomer(new_value, old_value) {
      if (!new_value || new_value.id == old_value.id) {
        return;
      }
      if (this.kvCustomer.id) {
        this.getAvailableSales(this.kvCustomer.id);
      }
    },
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
    getAvailableLines() {
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
    },
    getAvailableCustomers() {
      http
        .get("/customer/kv")
        .then(response => {
          this.availableCustomers = response.data;
          if (this.scheduleEvent.id) {
            this.kvCustomer = response.data.find(
              kvDto => kvDto.id == this.scheduleEvent.saleItem.sale.customer.id
            );
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSales(customer_id) {
      http
        .get("/kv/sale/customer/"+customer_id)
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
    getSaleItem(saleItemId) {
      http
        .get("/saleItem/" + saleItemId)
        .then(response => {
          this.saleItem = response.data;
          this.scheduleEvent.unitsScheduled = +this.saleItem.units - +this.saleItem.unitsScheduled;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate() {
      if (!this.line || !this.kvCustomer || !this.saleItem.id || !this.scheduleEvent.scheduleTime || this.scheduleEvent.unitsScheduled <= 0) {
        alert("Make sure all fields are entered");
        return false;
      }
      if ((+this.saleItem.unitsScheduled + +this.scheduleEvent.unitsScheduled) > this.saleItem.units){
        alert("Cannot schedule more units than sold!")
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
    this.getAvailableCustomers();
    this.getAvailableLines();
  }
};
</script>

<style>
</style>
