<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px; font-size: 12px">
          <b-col cols=1 style="margin-right: -45px">
            <b-button id="shipmentListMenu" size="sm" @click="openFilterMenu()">Filter</b-button>
            <b-popover :show="showFilterMenu" placement="bottom" target="shipmentListMenu" variant="secondary">
              <template v-slot:title>
                <span>Advanced Filters</span>
                <b-button style="margin-left: 185px" size="sm" @click="searchFilterMenu()">Search</b-button>
                <b-button style="margin-left: 10px" size="sm" @click="closeFilterMenu()">X</b-button>
              </template>
              <div style="width: 400px">
                <b-row>
                  <b-col cols=6>
                    <label class="top-label">Ship From:</label>
                    <input class="form-control" type="date" v-model="filter.shipFrom">
                  </b-col>
                  <b-col cols=6>
                    <label class="top-label">Ship To:</label>
                    <input class="form-control" type="date" v-model="filter.shipTo">
                  </b-col>
                </b-row>
              </div>
            </b-popover>
          </b-col>
          <b-col cols=2>
              <input class="form-control" style="font-size: 12px" type="tel" v-model="number" @keyup.enter="getShipments()" placeholder="Search Number"/>
          </b-col>
          <b-col cols=2>
            <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Search Customer"></b-select>
          </b-col>
          <b-col cols=2>
            <b-select option-value="id" option-text="name" :list="availableSales" v-model="sale" placeholder="Search Sale"></b-select>
          </b-col>
          <b-col cols=2>
            <b-select option-value="id" option-text="name" :list="availableItems" v-model="item" placeholder="Search Item"></b-select>
          </b-col>
          <b-col cols=2>
            <b-select option-value="id" option-text="name" :list="availableStatuses" v-model="status" placeholder="Search Status"></b-select>
          </b-col>
          <b-col>
              <div style="text-align: right;">
              <b-button size="sm" type="submit" variant="primary" @click="goToShipment('new')">New</b-button>
              </div>
          </b-col>
      </b-row>
      <b-table :items="shipments" :fields="fields">
        <template v-slot:cell(number)="row">
            <b-button size="sm" @click.stop="goToShipment(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
        <template v-slot:cell(status)="row">
            <span>{{getStatus(row.item)}}</span>
        </template>
        <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteShipment(row.item.id)">x</b-button>
        </template>
      </b-table>
      <div style="display: flex">
        <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
        <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
      </div>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      sortBy: "number",
      pageable: {totalElements: 100, currentPage: 1, perPage: 20, sortBy: 'modifiedDate', sortDesc: true},
      fields: [
        { key: "number", label: "Shipment #", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shippingDate", label: "Shipping Date", sortable: false },
        { key: "shippedDate", label: "Shippped", sortable: false },
        { key: "modifiedDate", label: "Last Modified", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      filter: {
        shipFrom: null,
        shipTo: null,
      },
      shipments: [],
      number: '',
      availableSales: [],
      sale: {},
      availableCustomers: [],
      customer: {},
      availableItems: [],
      item: {},
      availableStatuses: [
        {id: 'INP', name: "Progress"},
        {id: 'REA', name: "Ready"},
        {id: "SHP", name: "Shipped"}
      ],
      status: {},
      showFilterMenu: false,
    };
  },
  watch: {
    sale(newValue, oldValue){
      this.getShipments();
    },
    customer(newValue, oldValue){
      this.getShipments();
    },
    item(newValue, oldValue){
      this.getShipments();
    },
    status(newValue, oldValue){
      this.getShipments();
    }
  },
  methods: {
    openFilterMenu(){
      this.showFilterMenu = true;
    },
    closeFilterMenu(){
      this.showFilterMenu = false;
    },
    searchFilterMenu(){
      this.getShipments();
      this.showFilterMenu = false;
    },
    browserHeight(){
      return +window.innerHeight - 170 +"px";
    },
    getStatus(shipItem){
      var status = "In Progress";
      if(shipItem.status == "REA"){
        status = "Ready To Ship";
      }
      if(shipItem.status == "SHP"){
        status = "Shipped";
      }
      return status;
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getShipments();
    },
    getShipments() {
      http
        .get("/shipment/pageable", {params: {pageable: this.pageable, 
            number: this.number, customerId: this.customer.id, saleId: this.sale.id, itemId: this.item.id, 
            status: this.status.id, shipFrom: this.filter.shipFrom, shipTo: this.filter.shipTo}}).then(r => {
          this.shipments = r.data.content;
          this.pageable.totalElements = r.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableCustomers() {
      http.get("/customer/kv").then(r => {
        this.availableCustomers = r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    getAvailableSales() {
      return http.get("/sale/kv").then(r => {
        this.availableSales = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    getAvailableItems() {
      return http.get("/item/kv").then(r => {
        this.availableItems = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    deleteShipment(id) {
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete Shipment?').then(ok => {
        if(ok){
          http.delete("/shipment/" + id).then(() => {
            this.getShipments();
          }).catch(e => {
            console.log("API Error: " + e);
          });
        }
      })
    },
    goToShipment(id) {
      if (id) {
        router.push("/shipmentEdit/" + id);
      }
    }
  },
  mounted() {
    this.sale = {id: this.$route.query.saleId};
    this.item = {id: this.$route.query.itemId};
    window.history.replaceState({}, document.title, window.location.pathname);
    this.getShipments();
    this.getAvailableCustomers();
    this.getAvailableSales()
    this.getAvailableItems()
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
/* .table tr {
  font-size: 14px;
  line-height: 0.5;
} */
</style>
