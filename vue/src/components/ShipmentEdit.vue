<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Shipment: {{shipment.number}}</span>
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer"></b-select>
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="sale"></b-select>
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="label" :list="availableSaleItems" v-model="saleItem"></b-select>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Sale Items</label>
        <b-table v-if="shipment.shipmentItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="shipment.shipmentItems" :fields="columns">
          <!-- <template slot="action" slot-scope="row">
            <b-form-checkbox v-model="row.item.selected" @input="rowSelect(row.item, row.item.id, row.item.selected)"></b-form-checkbox>
          </template> -->
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
      shipment: {shipmentItems: []},
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
        { key: "sale", label: "Sale", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
    };
  },
  computed: {},
  watch: {
    customer(new_value, old_value) {
      if (new_value.id != old_value.id) {
        this.shipment.customer = new_value;
        this.getAvailableSales();
      }
    },
    sale(new_value, old_value) {
      if (new_value.id != old_value.id) {
        new_value.saleItems.forEach(si =>{
            si.label = si.item.number;
            this.availableSaleItems.push(si)
        })
      }
    },
  },
  methods: {
    getShipment(id) {
      http
        .get("/shipment/" + id)
        .then(response => {
          this.shipment = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveShipment() {
      return http
        .post("/shipment", this.shipment)
        .then(response => {
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.saveShipment().then(r => {
        router.push("/purchaseList");
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
