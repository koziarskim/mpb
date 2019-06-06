<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Shipment: {{shipment.number}}</span>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="shipment.date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3">
        <label class="top-label">Customer:</label>
        <b-select v-if="shipment.shipmentItems.length==0" option-value="id" option-text="name" :list="availableCustomers" v-model="customer"></b-select>
        <br/><span v-if="shipment.shipmentItems.length>0">{{customer.name}}</span>
      </b-col>
      <b-col cols="3">
        <label class="top-label">Distribution:</label>
        <b-select option-value="id" option-text="label" :list="shippingAddresses" v-model="shippingAddress"></b-select>
      </b-col>
      <b-col cols="3">
        <label class="top-label">Ship To:</label><br/>
        <label>{{shippingAddress.street}}</label><br/>
        <label>{{shippingAddress.city}}, {{shippingAddress.state}} {{shippingAddress.zip}}</label>
      </b-col>
      <b-col cols="3">
        <label class="top-label">Bill To:</label><br/>
        <label v-if="customer.billingAddress">{{customer.billingAddress.street}}</label><br/>
        <label v-if="customer.billingAddress">{{customer.billingAddress.city}}, {{customer.billingAddress.state}} {{customer.billingAddress.zip}}</label>
      </b-col>
    </b-row>
    <b-row>
        <b-col cols=2>
            <label class="top-label">P.O. No.:</label><br/>
            <input class="form-control" type="tel" v-model="shipment.poNumber">
        </b-col>
        <b-col cols=2>
            <label class="top-label">Ship Date:</label><br/>
            <input class="form-control" type="date" v-model="shipment.shipDate">
        </b-col>
        <b-col cols=2>
            <label class="top-label">Via:</label><br/>
            <input class="form-control" type="tel" v-model="shipment.via">
        </b-col>
        <b-col cols=2>
            <label class="top-label">FOB:</label><br/>
            <input class="form-control" type="tel" v-model="shipment.fob">
        </b-col>
        <b-col cols=2>
            <label class="top-label">Freight Class</label><br/>
            <input class="form-control" type="tel" v-model="shipment.freight">
        </b-col>
        <b-col cols=2>
            <label class="top-label">CS No.:</label><br/>
            <input class="form-control" type="tel" v-model="shipment.csNumber">
        </b-col>
    </b-row>
    <b-row>
      <b-col cols="3">
        <label class="top-label">Sale:</label>
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="sale"></b-select>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Item:</label>
        <b-select option-value="id" option-text="label" :list="availableSaleItems" v-model="saleItem"></b-select>
      </b-col>
      <b-col cols="1">
        <b-button style="padding-top: 30px; padding-left: 0px" variant="link" @click="addItem()">(+)</b-button>
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
          <template slot="cases" slot-scope="row">
            <span>{{row.item.cases = Math.ceil(+row.item.units / +row.item.saleItem.item.casePack)}}</span>
          </template>
          <template slot="pallets" slot-scope="row">
            <span>{{row.item.pallets = Math.ceil(+row.item.cases / (+row.item.saleItem.item.ti * +row.item.saleItem.item.hi))}}</span>
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
      shippingAddresses: [],
      shippingAddress: {},
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
        { key: "saleItem.item.casePack", label: "Case Pack", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "pallets", label: "Pallets", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ]
    };
  },
  computed: {},
  watch: {
    customer(new_value, old_value) {
      if (new_value.id == old_value.id) {
        return;
      }
      this.shippingAddresses = new_value.addresses;
      if (this.shipment.customer == null || this.shipment.customer.id != new_value.id) {
        this.shippingAddress = {};
        this.shipment.customer = new_value;
        this.saveShipment();
      }
      this.getAvailableSales();
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
          response.data.shipmentItems.forEach(si => {
            si.existingUnits = si.units;
          });
          this.shipment = response.data;
          if (response.data.customer) {
            this.customer = response.data.customer;
          }
          if(response.data.shippingAddress){
            this.shippingAddress = response.data.shippingAddress;
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveShipment() {
      if(this.shippingAddress.id){
          this.shipment.shippingAddress = {id: this.shippingAddress.id}
      }
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
      this.saveShipment().then(r=>{
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
      })
    },
    deleteItem(shipmentItemId) {
      http
        .delete("/shipmentItem/" + shipmentItemId)
        .then(response => {
          this.getShipment(this.shipment.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    formatDate(date){
        return date ? moment(date).utc().format("YYYY-MM-DD"): "";
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
