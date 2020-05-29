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
              </b-col>
            </b-row>
          </div>
        </b-popover>
      </b-col>      
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availablePurchases" v-model="purchaseKv" placeholder="Purchase"></b-select>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="searchInvoice" @keyup.enter="getPurchases()" placeholder="Inoice #"/>
      </b-col>
      <b-col cols=1 offset=1>
        <div style="text-align: right;">
          <b-button size="sm" type="submit" variant="primary" @click="goToReceiving('')">New Rec</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="receivings" :fields="fields" no-local-sorting>
      <template v-slot:cell(name)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.number}} ({{row.item.name}})</b-button>
      </template>
      <template v-slot:cell(purchase)="row">
        <b-button
          size="sm"
          @click.stop="goToPurchase(row.item.purchaseComponent.purchase.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.purchase.number:''}}</b-button>
      </template>
      <template v-slot:cell(component)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" :title="row.item.name" @click.stop="goToComponent(row.item.purchaseComponent.component.id)">{{row.item.purchaseComponent.component.number}}</b-link> - {{row.item.purchaseComponent.component.name}}</div>
      </template>
      <template v-slot:cell(shippedDate)="row">
        <span>{{formatDate(row.item.shippedDate)}}</span>
      </template>
      <template v-slot:cell(etaDate)="row">
        <span>{{formatDate(row.item.etaDate)}}</span>
      </template>
      <template v-slot:cell(receivedDate)="row">
        <span>{{formatDate(row.item.receivedDate)}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <b-button size="sm" @click.stop="deleteReceiving(row.item.id)">x</b-button>
      </template>
    </b-table>
    <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";

export default {
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: true},
      fields: [
        { key: "name", label: "Receiving # (Name)", sortable: false },
        { key: "purchase", label: "Purchase", sortable: false },
        { key: "component", label: "Component", sortable: false },
        { key: "containerNumber", label: "Container", sortable: false },
        { key: "shippingDate", label: "Shipped", sortable: false },
        { key: "receivingDate", label: "Received", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availablePurchases: [],
      purchaseKv: {},
      availableComponents: [],
      componentKv: {},
      availableSuppliers: [],
      supplierKv: {},
      searchInvoice: "",
      receivings: [],
      showFilterMenu: false,
    };
  },
  computed: {},
  watch: {
    purchaseKv(new_value, old_value) {
      this.getReceivings();
    },
    componentKv(new_value, old_value) {
      this.getReceivings();
    }
  },
  methods: {
    searchFilterMenu(){
      this.getReceivings();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.getReceivings();
      this.showFilterMenu = false;
    },     
    getAvailableSuppliers(){
      http.get("/supplier/kv").then(response => {
        this.availableSuppliers = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getReceivings();
    },
    formatDate(date){
        return date
            ? moment(date)
                .utc()
                .format("YYYY-MM-DD")
            : "";
    },
    getReceivings() {
      http.get("/receiving/pageable", {params: {pageable: this.pageable, purchase_id: this.purchaseKv.id, component_id: this.componentKv.id}})
        .then(r => {
          this.receivings = r.data.content;
          this.pageable.totalElements = r.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchases() {
      return http.get("/purchase/kv").then(response => {
        this.availablePurchases = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableComponents() {
      return http.get("/component/kv").then(response => {
        this.availableComponents = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getPurchaseComponent(purchase_id, component_id) {
      return http.get("/purchaseComponent/purchase/"+purchase_id+"/component/"+component_id).then(r => {
       return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    goToReceiving(receiving_id) {
      if (!receiving_id) {
        if(!this.purchaseKv.id || !this.componentKv.id){
          alert("Please pick Purchase and Component first!")
          return Promise.resolve();
        }
        this.getPurchaseComponent(this.purchaseKv.id, this.componentKv.id).then(pc => {
          router.push("/receivingEdit/pc/" + pc.id);
        })
      } else {
        router.push("/receivingEdit/" + receiving_id);
      }
    },
    goToPurchase(purchase_id) {
      router.push("/purchaseEdit/" + purchase_id);
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    deleteReceiving(receiving_id) {
      http
        .delete("/receiving/" + receiving_id)
        .then(response => {
          this.getReceivings();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    }
  },
  mounted() {
    var purchase_id = this.$route.query.purchase_id;
    var component_id = this.$route.query.component_id;
    if(purchase_id){
      this.purchaseKv = {id: purchase_id};
    }
    if(component_id){
      this.componentKv = {id: component_id};
    }
    this.getAvailableSuppliers();
    this.getAvailablePurchases();
    this.getAvailableComponents();
    this.getReceivings();
    window.history.replaceState({}, document.title, window.location.pathname);
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
