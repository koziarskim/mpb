<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
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
                <b-select option-value="id" option-text="name" :list="availableFreightTerms" v-model="freightTerms" placeholder="Freight"></b-select>
              </b-col>
            </b-row>
            <b-row style="margin-top: 10px">
              <b-col cols=6>
                <b-form-checkbox size="lg" v-model="confirmedFilter" value="CONFIRMED">Confirmed Only</b-form-checkbox>
              </b-col>
              <b-col cols=6>
                <b-form-checkbox size="lg" v-model="confirmedFilter" value="NOT_CONFIRMED">Not-Confirmed</b-form-checkbox>
              </b-col>
            </b-row>
          </div>
        </b-popover>
      </b-col>      
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="searchPurchase" @keyup.enter="getPurchases()" placeholder="PO Name/Number"/>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableStatuses" v-model="status" placeholder="Status"></b-select>
      </b-col>
      <b-col cols=1 offset=1>
        <div style="text-align: right;">
          <b-button size="sm" type="submit" variant="primary" @click="goToPurchaseNew()">New P.O.</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="purchases" :fields="fields" no-local-sorting>
      <template v-slot:cell(number)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click.stop="goToPurchaseEdit(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}</div>
      </template>
      <template v-slot:cell(received)="row">
        <span>{{row.item.received?"Yes":"No"}}</span>
      </template>
      <template v-slot:cell(pdf)="row">
        <a :href="rowPdfUrl(row.item.id)" target="_blank">
          <img src="../assets/pdf-download.png" width="20px">
        </a>
      </template>
      <template v-slot:cell(poDate)="row">
        <span>{{row.item.poDate | formatDate}}</span>
      </template>
      <template v-slot:cell(freightTerm)="row">
        <span>{{getFreightName(row.item.freightTermId)}}</span>
      </template>
      <template v-slot:cell(etaDate)="row">
        <span>{{row.item.etaDate | formatDate}}</span>
      </template>
      <template v-slot:cell(unitsPurchRec)="row">
        <b-link role="button" @click.stop="goToReceiving(row.item.id)">{{row.item.unitsPurchased}} / {{row.item.unitsReceived}}</b-link>
      </template>
      <template v-slot:cell(unitsPending)="row">
        <div>{{+row.item.unitsPurchased - +row.item.unitsReceived}}</div>
      </template>
      <template v-slot:cell(date)="row">
        <span>{{row.item.date | formatDates}}</span>
      </template>
      <template v-slot:cell(totalPoAmount)="row">
        <span>${{parseFloat(row.item.totalPoAmount).toLocaleString('en-US',{minimumFractionDigits: 2, maximumFractionDigits: 2})}}</span>
      </template>
    </b-table>
    <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";

export default {
  name: "PurchaseList",
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: true},
      searchPurchase: "",
      searchComponent: "",
      fields: [
        { key: "number", label: "Purchase # (Name)", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "poDate", label: "P.O. Date", sortable: false },
        { key: "etaDate", label: "Expected", sortable: false },
        { key: "unitsPurchRec", label: "Purch/Rec", sortable: false },
        { key: "unitsPending", label: "Pending", sortable: false },
        { key: "freightTerm", label: "Freight", sortable: false },
        { key: "totalPoAmount", label: "Total", sortable: false },
        { key: "pdf", label: "PDF", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availableFreightTerms: [
        {id: "TPB", name: "TP Bill"},
        {id: "PRP", name: "Pre Paid"},
        {id: "TPO", name: "TP Bill Other"},
        {id: "COL", name: "Collect"},
        {id: "DEL", name: "Delivered"},
        {id: "CPU", name: "Customer Pickup"}
      ],
      freightTerms: {},
      availableStatuses: [
        {id: "OPEN", name: "Open/No Recivings"},
        {id: "PARTIAL", name: "Partially Received"},
        {id: "RECEIVED", name: "Fully Received"},
      ],
      status: {},
      availableComponents: [],
      componentKv: {},
      availableSuppliers: [],
      supplierKv: {},
      component: {},
      purchases: [], //PurchaseListDto
      keyword: "",
      showAll: false,
      showFilterMenu: false,
      confirmedFilter: false,
    };
  },
  computed: {
  },
  watch: {
    componentKv(newValue, oldValue){
      this.getPurchases();
    },
    supplierKv(newValue, oldValue){
      this.getPurchases();
    },
    // freightTerms(newValue, oldValue){
    //   this.getPurchases();
    // },
    status(newValue, oldValue){
      this.getPurchases();
    }
  },
  methods: {
    searchFilterMenu(){
      this.getPurchases();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.freightTerms = {};
      this.confirmedFilter = false;
      this.getPurchases();
      this.showFilterMenu = false;
    },     
    getFreightName(id) {
        var name = "";
        this.availableFreightTerms.filter(it =>{
            if(it.id == id){
                name = it.name;
            }
        })
        return name;
    },    
    getAvailableComponents(){
      http.get("/component/kv").then(response => {
        this.availableComponents = response.data;
      });
    },
    getAvailableSuppliers(){
      http.get("/supplier/kv").then(response => {
        this.availableSuppliers = response.data;
      });
    },
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getPurchases();
    },
    showAllChange(){
      this.showAll = !this.showAll
      if (this.component.id) {
        this.component = {};
      }
      this.keyword = "";
      this.getPurchases();
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getPurchases() {
      http.get("/purchase/pageable", {params: {
        pageable: this.pageable, 
        purchaseName: this.searchPurchase, 
        componentId: this.componentKv.id,
        supplierId: this.supplierKv.id,
        freightTerms: this.freightTerms.id,
        status: this.status.id,
        confirmed: this.confirmedFilter,
      }}).then(response => {
        this.purchases = response.data.content;
        this.pageable.totalElements = response.data.totalElements;
      });
    },
    goToPurchaseNew() {
      router.push("/purchaseEdit");
    },
    goToPurchaseEdit(id) {
      router.push("/purchaseEdit/" + id);
    },
    goToReceiving(purchase_id) {
      var query = { purchase_id: purchase_id };
      if (this.component.id) {
        query.component_id = this.component.id;
      }
      router.push({ path: "/receivingList", query: query });
    },
    rowPdfUrl: function(purchase_id) {
      return httpUtils.getUrl("/purchase/" + purchase_id + "/pdf", "");
    }
  },
  mounted() {
    var componentId = this.$route.query.component_id;
    if(componentId){
      this.componentKv = {id: componentId}
    }
    this.getAvailableComponents();
    this.getAvailableSuppliers();
    // this.getPurchases();
  },
  activated(){
    this.getPurchases();
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
