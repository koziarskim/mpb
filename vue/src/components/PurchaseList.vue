<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="searchPurchase" @keyup.enter="getPurchases()" placeholder="PO Name/Number"/>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=1 offset=4>
        <div style="text-align: right;">
          <b-button size="sm" type="submit" variant="primary" @click="goToPurchaseNew()">New P.O.</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="purchases" :fields="fields" no-local-sorting>
      <template v-slot:cell(number)="row">
        <b-link role="button" @click.stop="goToPurchaseEdit(row.item.id)">{{row.item.number}}</b-link>
        <div class="name-md" :title="row.item.name"> ({{row.item.name}})</div>
      </template>
      <template v-slot:cell(received)="row">
        <span>{{row.item.received?"Yes":"No"}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <b-button size="sm" @click.stop="deletePurchase(row.item)">x</b-button>
      </template>
      <template v-slot:cell(pdf)="row">
        <a :href="rowPdfUrl(row.item.id)" target="_blank">
          <img src="../assets/pdf-download.png" width="20px">
        </a>
      </template>
      <template v-slot:cell(expectedDate)="row">
        <span>{{row.item.expectedDate | formatDate}}</span>
      </template>
      <template v-slot:cell(unitsReceived)="row">
        <b-link role="button" @click.stop="goToReceiving(row.item.id)">{{row.item.unitsReceived}}</b-link>
      </template>
      <template v-slot:cell(date)="row">
        <span>{{row.item.date | formatDate}}</span>
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
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: true},
      searchPurchase: "",
      searchComponent: "",
      fields: [
        { key: "number", label: "Purchase # (Name)", sortable: false },
        { key: "invoiceNumber", label: "Invoice", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "poDate", label: "P.O. Date", sortable: false },
        { key: "etaDate", label: "Expected", sortable: false },
        { key: "shippingDate", label: "Shipping", sortable: false },
        { key: "unitsOrdered", label: "Purchased", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "pdf", label: "PDF", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availableComponents: [],
      componentKv: {},
      availableSuppliers: [],
      supplierKv: {},
      component: {},
      purchases: [], //PurchaseListDto
      keyword: "",
      showAll: false
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
    }
  },
  methods: {
    getAvailableComponents(){
      http.get("/component/kv").then(response => {
        this.availableComponents = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableSuppliers(){
      http.get("/supplier/kv").then(response => {
        this.availableSuppliers = response.data;
      }).catch(e => {console.log("API error: " + e);});
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
        supplierId: this.supplierKv.id
      }}).then(response => {
        this.purchases = response.data.content;
        this.pageable.totalElements = response.data.totalElements;
      }).catch(e => {console.log("API error: " + e);});
    },
    deletePurchase(purchase) {
      if(purchase.unitsReceived > 0){
        alert("There are units already received. Please delete receiving first");
        return;
      }
      http.delete("/purchase/" + purchase.id).then(response => {
        this.getPurchases();
      }).catch(e => {
        console.log("API Error: " + e);
      });
    },
    goToPurchaseNew() {
      router.push("/purchaseNew");
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
      return httpUtils.getUrl("/purchase/" + purchase_id + "/pdf");
    }
  },
  mounted() {
    var componentId = this.$route.query.component_id;
    if(componentId){
      this.componentKv = {id: componentId}
    }
    this.getAvailableComponents();
    this.getAvailableSuppliers();
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
