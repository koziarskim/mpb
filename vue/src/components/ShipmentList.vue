<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px; font-size: 12px">
          <b-col cols=1 style="margin-right: -45px">
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
                    <label class="top-label">Created/Modified On:</label>
                    <input class="form-control" type="date" v-model="filter.updated">                    
                  </b-col>
                </b-row>
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
            <div style="text-align: right; margin-left: -10px">
              <b-button size="sm" type="submit" variant="primary" @click="goToShipment('new')">New</b-button>
              <b-button size="sm" style="margin-left:3px" variant="primary" @click="exportSelected()">Export ({{selectedShipments.length}})</b-button>          
            </div>
          </b-col>
      </b-row>
      <b-table :items="shipments" :fields="fields">
        <template v-slot:head(action)="row">
          <div style="display: flex; width: 20px; margin-left: -25px">
            <b-button size="sm" @click="triggerAll(false)" variant="link">(-)</b-button><b-button size="sm" @click="triggerAll(true)" variant="link">(+)</b-button>
          </div>
        </template>
        <template v-slot:cell(number)="row">
          <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToShipment(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}</div>
          <!-- <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" :id="'popover-'+row.item.id" @click="getShipmentItems(row.item)">{{row.item.number}}</b-link> {{row.item.name}}</div>
            <b-popover placement="bottomright" :target="'popover-'+row.item.id" triggers="focus" variant="primary">
              <template v-slot:title>
                <b-button size="sm" @click="goToShipment(row.item.id)" variant="link">View/Edit Details</b-button>
              </template>
              <div class = "shipItemTable">
              <table>
                <tr><th>Sale</th><th>Item</th><th>Sold</th><th>Produced</th><th>Shipped</th></tr>
              <tr v-for="si in row.item.shipmentItems" :key="si.id">
                <td>{{si.saleItem.sale.number}}</td>
                <td>{{si.saleItem.item.number}} ({{si.saleItem.item.name}})</td>
                <td>{{si.saleItem.units}}</td>
                <td>{{si.saleItem.unitsProduced}}</td>
                <td>{{si.units}}</td>
              </tr>
              </table>
              </div>
            </b-popover> -->
        </template>
        <template v-slot:cell(status)="row">
            <b :class="getStatusClass(row.item)">{{getStatus(row.item)}}</b>
        </template>
        <template v-slot:cell(action)="row">
          <input type="checkbox" v-model="selectedShipments" :value="row.item">
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
  name: "ShipmentList",
  data() {
    return {
      sortBy: "number",
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'modifiedDate', sortDesc: true},
      fields: [
        { key: "number", label: "Shipment #", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "shippingDate", label: "Expected", sortable: false },
        { key: "shippingWindow", label: "Ship Window", sortable: false },
        { key: "shippedDate", label: "Shippped", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      filter: {
        updated: null,
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
      selectedShipments: [],
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
    exportSelected(){
      var selectedIds = [];
      this.selectedShipments.forEach(ship=> {
        selectedIds.push(ship.id);
      })
      http.put("/shipment/xls", selectedIds, { responseType: 'blob'}).then(r => {
        const url = URL.createObjectURL(new Blob([r.data], { type: r.headers['content-type']}))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute("download", r.headers['file-name'])
        document.body.appendChild(link)
        link.click()
      }).catch(e => {
        console.log("API error: "+e);
      });
    },    
    triggerAll(add){
      this.shipments.forEach(ship => {
        if(add){
          var idx = this.selectedShipments.findIndex(s => s.id == ship.id);
          if(idx == -1){
            this.selectedShipments.push(ship);
          }
        }else{
          this.selectedShipments = [];
        }
      })
    },
    getStatusClass(shipItem){
      if(shipItem.status == "REA") { return "status-blue" }
      if(shipItem.status == "SHP") { return "status-green" }
      return "status-red";
    },
    getShipmentItems(shipmentDto){
      http.get("/shipment/"+shipmentDto.id).then(r => {
        shipmentDto.shipmentItems = r.data.shipmentItems
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    searchFilterMenu(){
      this.getShipments();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.filter.updated = null;
      this.filter.shipFrom = null;
      this.filter.shipTo = null;
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
      http.get("/shipment/pageable", {params: {pageable: this.pageable, 
          number: this.number, customerId: this.customer.id, saleId: this.sale.id, itemId: this.item.id, 
          status: this.status.id, updated: this.filter.updated, shipFrom: this.filter.shipFrom, shipTo: this.filter.shipTo}}).then(r => {
        r.data.content.forEach(ship => {
          ship.shipmentItems = []
        })
        this.shipments = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }).catch(e => {
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
    if(this.$route.query.saleId){this.sale = {id: this.$route.query.saleId};}
    if(this.$route.query.itemId){this.item = {id: this.$route.query.itemId};}
    window.history.replaceState({}, document.title, window.location.pathname);
    // this.getShipments();
    this.getAvailableCustomers();
    this.getAvailableSales()
    this.getAvailableItems()
  },
  activated(){
    this.getShipments();
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
.shipItemTable table{
  border: 1px solid black;
}
.shipItemTable th{
  border: 1px solid black;
}
.shipItemTable td{
  border: 1px solid black;
}
/* .table tr {
  font-size: 14px;
  line-height: 0.5;
} */
</style>
