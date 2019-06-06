<template>
  <b-container fluid>
      <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">Shipment: {{shipment.number}}</h4>
            <div style="text-align: right;">
                <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
            </div>
        </div>
    <b-row>
      <b-col cols="3">
        <b-select v-if="shipment.shipmentItems.length==0" option-value="id" option-text="name" :list="availableCustomers" v-model="customer"></b-select>
        <span v-if="shipment.shipmentItems.length>0">{{customer.name}}</span>
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="sale"></b-select>
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="label" :list="availableSaleItems" v-model="saleItem"></b-select>
      </b-col>
      <b-col cols="1">
        <b-button variant="link" @click="addItem()">(+)</b-button>
      </b-col>
    </b-row>
    <br>
    <b-row>
      <b-col>
        <label class="top-label">Sale Items</label>
        <b-table v-if="shipment.shipmentItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="shipment.shipmentItems" :fields="columns">
          <template slot="units" slot-scope="row">
            <input class="form-control" style="width:100px" type="tel" :disabled="locked" v-model="row.item.units">
          </template>
          <template slot="unitsShipped" slot-scope="row">
          <span>{{(+row.item.saleItem.unitsShipped - +row.item.existingUnits + +row.item.units)}}</span>
          </template>

          <template slot="action" slot-scope="row">
            <b-button size="sm" @click.stop="deleteItem(row.item.id)">x</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      locked: false,
      shipment: { shipmentItems: [] },
      availableCustomers: [],
      customer: {},
      availableSales: [],
      sale: {},
      availableSaleItems: [],
      saleItem: {},
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "id", label: "#", sortable: false },
        { key: "saleItem.item.number", label: "Item", sortable: false },
        { key: "saleItem.sale.number", label: "Sale", sortable: false },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "unitsShipped", label: "Shipped", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ]
    };
  },
  computed: {},
  watch: {
    customer(new_value, old_value) {
      if (new_value.id != old_value.id) {
          if(this.shipment.customer == null || this.shipment.customer.id!=new_value.id){
            this.shipment.customer = new_value;
            this.saveShipment();
          }
        this.getAvailableSales();
      }
    },
    sale(new_value, old_value) {
      if (new_value.id != old_value.id) {
        new_value.saleItems.forEach(si => {
          si.label = si.item.number;
          this.availableSaleItems.push(si);
        });
      }
    }
  },
  methods: {
    getShipment(id) {
      http
        .get("/shipment/" + id)
        .then(response => {
          response.data.shipmentItems.forEach(si=>{
              si.existingUnits = si.units;
          })
          this.shipment = response.data;
          if(response.data.customer){
              this.customer = response.data.customer;
          }
          
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveShipment() {
      return http
        .post("/shipment", this.shipment)
        .then(response => {})
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveShipmentItem(shipmentItem) {
      return http
        .post("/shipmentItem", shipmentItem)
        .then(response => {
          return Promise.resolve();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.saveShipment().then(r => {
        router.push("/shipmentList");
      });
    },
    getAvailableCustomers() {
      return http
        .get("/customer/")
        .then(response => {
          this.availableCustomers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSales() {
      return http
        .get("/sale/")
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addItem() {
      var shipmentItem = {
        shipment: { id: this.shipment.id },
        saleItem: { id: this.saleItem.id }
      };
      http
        .post("/shipmentItem", shipmentItem)
        .then(response => {
          this.getShipment(this.shipment.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteItem(shipmentItemId) {
      http
        .delete("/shipmentItem/"+shipmentItemId)
        .then(response => {
          this.getShipment(this.shipment.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
  },
  mounted() {
    var id = this.$route.params.shipment_id;
    this.getShipment(id);
    this.getAvailableCustomers();
  }
};
</script>

<style>
</style>
