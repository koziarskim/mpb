<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Orders</span>
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="tel" v-model="searchPurchase" @click="searchComponent = ''" @keyup.enter="getPurchases('purchase')" placeholder="Search Number or Name"/>
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="tel" v-model="searchComponent" @click="searchPurchase = ''" @keyup.enter="getPurchases('component')" placeholder="Search Component"/>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToPurchaseNew()">New P.O.</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="purchases" :fields="fields" no-local-sorting>
      <template v-slot:cell(number)="row">
        <b-button size="sm" @click.stop="goToPurchaseEdit(row.item.id)" variant="link">{{row.item.number}} ({{row.item.name}})</b-button>
      </template>
      <template v-slot:cell(received)="row">
        <span>{{row.item.received?"Yes":"No"}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <b-button size="sm" @click.stop="deletePurchase(row.item.id)">x</b-button>&nbsp;
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)">Receivings</b-button>
      </template>
      <template v-slot:cell(pdf)="row">
        <a :href="rowPdfUrl(row.item.id)" target="_blank">
          <img src="../assets/pdf-download.png" width="20px">
        </a>
      </template>
      <template v-slot:cell(expectedDate)="row">
        <span>{{row.item.expectedDate | formatDate}}</span>
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
import store from "../store.js";

export default {
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'number', sortDesc: false},
      searchPurchase: "",
      searchComponent: "",
      fields: [
        { key: "number", label: "Purchase # (Name)", sortable: false },
        { key: "invoiceNumber", label: "Invoice", sortable: false },
        { key: "supplier.name", label: "Supplier", sortable: false },
        { key: "date", label: "P.O. Date", sortable: false },
        { key: "expectedDate", label: "Expected", sortable: false },
        { key: "shippingDate", label: "Shipping", sortable: false },
        { key: "received", label: "Received", sortable: false },
        { key: "pdf", label: "PDF", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      availableComponents: [],
      component: {},
      purchases: [],
      keyword: "",
      showAll: false
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
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
    getPurchases(type) {
      var searchKey = type=="purchase"?this.searchPurchase:this.searchComponent;
      http
        .get("/purchase/pageable", {params: {pageable: this.pageable, searchKey: searchKey, searchType: type}})
        .then(response => {
          this.purchases = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deletePurchase(id) {
      http
        .delete("/purchase/" + id)
        .then(response => {
          this.getPurchases();
        })
        .catch(e => {
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
      return httpUtils.baseUrl + "/purchase/" + purchase_id + "/pdf";
    }
  },
  mounted() {
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
