<template>
    <b-container fluid>
      <b-row style="font-size: 12px">
        <b-col cols=1 style="margin-right: -45px;">
          <b-button id="filterMenu" size="sm" @click="showFilterMenu = true">Filter</b-button>
          <b-popover :show="showFilterMenu" placement="bottom" target="filterMenu" variant="secondary">
            <template v-slot:title>
              <span>Advanced Filters</span>
              <b-button style="margin-left: 185px" size="sm" @click="searchFilterMenu()">Search</b-button>
              <b-button style="margin-left: 10px" size="sm" @click="clearFilterMenu()">Clear</b-button>
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
        </b-col>      
        <b-col cols=2>
          <input class="form-control" style="font-size: 12px" type="tel" v-model="invoiceNumber" @keyup.enter="getInvoiceItems()" placeholder="Invoice"/>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Sale"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv" placeholder="Customer"></b-select>
        </b-col>
        <b-col cols=2>
          <b-select option-value="id" option-text="name" :list="availableShipments" v-model="shipmentKv" placeholder="Shipment"></b-select>
        </b-col>
      <b-col cols=1 style="margin-right: -45px;">
        <b-button id="totalsMenu" size="sm" @click="toggleShowTotals()">Totals</b-button>
        <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
          <div style="width: 300px; font-size: 16px">
            <div>Total of {{pageable.totalElements}} rows</div>
            <div>Total price: ${{totalUnitsPrice}}</div>
            <div>Total units: {{totalUnits}}</div>
          </div>
        </b-popover>
      </b-col>      

      </b-row>
      <b-table :items="invoiceItems" :fields="fields" no-local-sorting>
        <template v-slot:cell(number)="row">
          <b-button size="sm" @click="goToInvoice(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
        <template v-slot:cell(sent)="row">
          <span>{{row.item.sent?'Yes':'No'}}</span>
        </template>
        <template v-slot:cell(unitPrice)="row">
          <span>${{row.item.unitPrice}}</span>
        </template>
        <template v-slot:cell(totalUnitPrice)="row">
          <span>${{getTotalUnitPrice(row.item)}}</span>
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
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: false},
      fields: [
        { key: "number", label: "Invoice #", sortable: false },
        { key: "saleNumber", label: "Sale #", sortable: false },
        { key: "itemNumber", label: "Item #", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shipmentNumber", label: "Shipment", sortable: false },
        { key: "unitsInvoiced", label: "Units", sortable: false },
        { key: "unitPrice", label: "Price", sortable: false },
        { key: "totalUnitPrice", label: "Total", sortable: false },
        { key: "sent", label: "Sent", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      invoiceItems: [], //InvoiceItemListDto
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
      showTotalsMenu: false,
      filter: {
        invoiceFrom: null,
        invoiceTo: null,
      },
      totalUnitsPrice: 0,
      totalUnits: 0,
    };
  },
  watch: {
    saleKv(newValue, oldValue){
      this.getInvoiceItems();
    },
    customerKv(newValue, oldValue){
      this.getInvoiceItems();
    },
    shipmentKv(newValue, oldValue){
      this.getInvoiceItems();
    },
    itemKv(newValue, oldValue){
      this.getInvoiceItems();
    },
  },
  methods: {
    getTotalUnitPrice(ii){
      return parseFloat(ii.totalUnitPrice).toLocaleString('en-US',{minimumFractionDigits: 2})
    },
    toggleShowTotals(){
      if(!this.showTotalsMenu){
        this.getInvoiceItems(true);
      }
      this.showTotalsMenu = !this.showTotalsMenu;
    },
    searchFilterMenu(){
      this.getInvoiceItems();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.filter.invoiceFrom = null;
      this.filter.invoiceTo = null;
      this.getInvoiceItems();
      this.showFilterMenu = false;
    },      
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getInvoiceItems();
    },
	  getInvoiceItems(totals) {
      var query = {params: {
        totals: totals,
        pageable: this.pageable,
        invoiceNumber: this.invoiceNumber,
        itemId: this.itemKv.id,
        saleId: this.saleKv.id,
        customerId: this.customerKv.id,
        shipmentId: this.shipmentKv.id,
        invoiceFrom: this.filter.invoiceFrom,
        invoiceTo: this.filter.invoiceTo
      }}
      http.get("/invoiceItem/pageable", query).then(r => {
        if(totals){
          this.totalUnitsPrice = parseFloat(r.data.content[0][0]).toLocaleString('en-US',{minimumFractionDigits: 2});
          this.totalUnits = r.data.content[0][1];
        } else {
          this.invoiceItems = r.data.content;
          this.pageable.totalElements = r.data.totalElements;
        }
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    goToInvoice(id){
      router.push('/invoiceEdit/'+(id?id:''));
    },
    goToSale(id){
      router.push('/saleEdit/'+(id?id:''));
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
    this.getInvoiceItems();
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
