<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Schedule for: </span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
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
  }
};
</script>

<style>
</style>
