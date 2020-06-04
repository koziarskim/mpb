<template>
    <b-container fluid>
      <b-row style="font-size: 12px">
        <b-col cols=2>
          <input class="form-control" style="font-size: 12px" type="tel" v-model="invoiceNumber" @keyup.enter="getInvoices()" placeholder="Invoice"/>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Sale"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv" placeholder="Customer"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableShipments" v-model="shipmentKv" placeholder="Shipment"></b-select>
        </b-col>
        <!-- <b-col cols=1>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" size="sm" @click="goToInvoice()">New</b-button>
          </div>
        </b-col> -->
      </b-row>
      <b-table :items="invoices" :fields="fields" no-local-sorting>
        <template v-slot:cell(number)="row">
          <b-button size="sm" @click="goToInvoice(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
        <template v-slot:cell(sent)="row">
          <span>{{row.item.sent?'Yes':'No'}}</span>
        </template>
      </b-table>
      <div style="display: flex">
		    <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
        <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
      </div>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"
import navigation from "../utils/navigation";

export default {
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'date', sortDesc: false},
      fields: [
        { key: "number", label: "Invoice #", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shipmentNumber", label: "Shipment", sortable: false },
        { key: "type", label: "Type", sortable: false },
        { key: "sent", label: "Sent", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      invoices: [], //InvoiceListDto
      availableSales: [],
      saleKv: {},
      availableCustomers: [],
      customerKv: {},
      availableShipments: [],
      shipmentKv: {},
      availableItems: [],
      itemKv: {},
      invoiceNumber: "",
    };
  },
  watch: {
    saleKv(newValue, oldValue){
      this.getInvoices();
    },
    customerKv(newValue, oldValue){
      this.getInvoices();
    },
    shipmentKv(newValue, oldValue){
      this.getInvoices();
    },
    itemKv(newValue, oldValue){
      this.getInvoices();
    },
  },
  methods: {
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getInvoices();
    },
	  getInvoices() {
      var query = {params: {
        pageable: this.pageable,
        invoiceNumber: this.invoiceNumber,
        itemId: this.itemKv.id,
        saleId: this.saleKv.id,
        customerId: this.customerKv.id,
        shipmentId: this.shipmentKv.id
      }}
      http.get("/invoice/pageable", query).then(r => {
        this.invoices = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    goToInvoice(id){
      router.push('/invoiceEdit/'+(id?id:''));
    },
    getAvailableItems() {
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      }).catch(e => {console.log("API error: "+e);});
    },
    getAvailableSales() {
      http.get("/sale/kv").then(r => {
        this.availableSales = r.data;
      }).catch(e => {console.log("API error: "+e);});
    },
    getAvailableCustomers() {
      http.get("/customer/kv").then(r => {
        this.availableCustomers = r.data;
      }).catch(e => {console.log("API error: "+e);});
    },
    getAvailableShipments() {
      http.get("/shipment/kv").then(r => {
        this.availableShipments = r.data;
      }).catch(e => {console.log("API error: "+e);});
    },
  },
  mounted() {
    this.getInvoices();
    this.getAvailableItems();
    this.getAvailableSales();
    this.getAvailableCustomers();
    this.getAvailableShipments();
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
