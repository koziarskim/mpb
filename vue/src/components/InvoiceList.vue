<template>
    <b-container fluid>
      <b-row style="font-size: 12px">
          <b-button id="filterMenu" size="sm" @click="showFilterMenu = true">Filter</b-button>
          <b-popover :show="showFilterMenu" placement="bottom" target="filterMenu" variant="secondary">
            <template v-slot:title>
              <span>Advanced Filters</span>
              <b-button style="margin-left: 220px" size="sm" @click="searchFilterMenu()">Search</b-button>
            </template>
            <div style="width: 400px">
                <b-row>
                  <b-col cols=6>
                    <label class="top-label">Invoice From:</label>
                    <input class="form-control" type="date" v-model="filter.invoiceFrom">
                  </b-col>
                  <b-col cols=6>
                    <label class="top-label">Invoice To:</label>
                    <input class="form-control" type="date" v-model="filter.invoiceTo">
                  </b-col>
                </b-row>
            </div>
          </b-popover>
          <b-button style="margin-left: 3px" size="sm" @click="clearFilterMenu()">Clear</b-button>
          <input class="form-control" style="font-size: 12px; width: 150px; margin-left: 15px" type="tel" v-model="invoiceNumber" @keyup.enter="getInvoices()" placeholder="Invoice"/>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Sale"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv" placeholder="Customer"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableShipments" v-model="shipmentKv" placeholder="Shipment"></b-select>
          <b-select style="width: 100px; margin-left: 15px" option-value="id" option-text="name" :list="availableSent" v-model="filterSent" placeholder="Sent"></b-select>
          <b-button style="margin-left: 130px" type="submit" :disabled="true" variant="primary" size="sm" @click="goToInvoice('')">New</b-button>
      </b-row>
      <b-table :items="invoices" :fields="fields" no-local-sorting>
        <template v-slot:cell(number)="row">
            <b-button size="sm" @click="goToInvoice(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
        <template v-slot:cell(totalAmount)="row">
          <span>${{parseFloat(row.item.totalAmount).toLocaleString('en-US',{minimumFractionDigits: 2})}}</span>
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
  name: "InvoiceList",
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'date', sortDesc: true},
      fields: [
        { key: "number", label: "Invoice #", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shipmentNumber", label: "Shipment", sortable: false },
        { key: "type", label: "Type", sortable: false },
        { key: "totalAmount", label: "Total", sortable: false },
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
      showFilterMenu: false,
      availableSent: [
        {id: "YES", name: "Yes"},
        {id: "NO", name: "No"},
      ],
      filterSent: {},
      filter: {
        invoiceFrom: null,
        invoiceTo: null,
      },
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
    filterSent(newValue, oldValue){
      this.getInvoices();
    },
  },
  methods: {
    showPopover(dto){
      this.getInvoice(dto.id).then(invoice => {
        dto.invoiceItems = invoice.invoiceItems;
      })
    },
    searchFilterMenu(){
      this.getInvoices();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      // this.filter.invoiceFrom = null;
      // this.filter.invoiceTo = null;
      // this.getInvoices();
      // this.showFilterMenu = false;
      router.go();
    },      
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
        shipmentId: this.shipmentKv.id,
        invoiceFrom: this.filter.invoiceFrom,
        invoiceTo: this.filter.invoiceTo,
        sent: this.filterSent.id,
      }}
      http.get("/invoice/pageable", query).then(r => {
        r.data.content.forEach(dto => {
          dto.invoiceItems = []
        })
        this.invoices = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    goToInvoice(id){
      router.push('/invoiceEdit/'+(id?id:''));
    },
    getInvoice(invoiceId) {
      return http.get("/invoice/"+invoiceId).then(r => {
        return r.data;
      }).catch(e => {console.log("API error: "+e);});
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
    // this.getInvoices();
    this.getAvailableItems();
    this.getAvailableSales();
    this.getAvailableCustomers();
    this.getAvailableShipments();
  },
  activated(){
    this.getInvoices();
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
