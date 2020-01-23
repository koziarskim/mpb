<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols=1>
                <span style="text-align: left; font-size: 18px; font-weight: bold">Shipments</span>
            </b-col>
            <b-col cols=3>
                <input class="form-control" type="tel" v-model="number" @keyup.enter="getShipments()" placeholder="Search Shipment Number"/>
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
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToShipment('new')">New Shipment</b-button>
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
     		<b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      sortBy: "number",
      pageable: {totalElements: 100, currentPage: 1, perPage: 100, sortBy: 'modifiedDate', sortDesc: true},
      fields: [
        { key: "number", label: "Shipment #", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shippingDate", label: "Shipping Date", sortable: false },
        { key: "shippedDate", label: "Shippped", sortable: false },
        { key: "modifiedDate", label: "Last Modified", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      shipments: [],
      number: '',
      availableSales: [],
      sale: {},
      availableCustomers: [],
      customer: {},
      availableItems: [],
      item: {}
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
    }
  },
  methods: {
    browserHeight(){
      return +window.innerHeight - 170 +"px";
    },
    getStatus(shipItem){
      var status = "In Progress";
      if(shipItem.ready){
        status = "Ready To Ship";
      }
      if(shipItem.shippedDate){
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
            number: this.number, customerId: this.customer.id, saleId: this.sale.id, itemId: this.item.id}}).then(r => {
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
</style>
