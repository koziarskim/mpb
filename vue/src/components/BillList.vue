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
                <label class="top-label">Received From:</label>
                <input class="form-control" type="date" v-model="filter.receivedFrom">
              </b-col>
              <b-col cols=6>
                <label class="top-label">Received To:</label>
                <input class="form-control" type="date" v-model="filter.receivedTo">
              </b-col>
            </b-row>
          </div>
        </b-popover>
      </b-col>      
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="packingList" @keyup.enter="getReceivings()" placeholder="Packing Slip"/>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availablePurchases" v-model="purchaseKv" placeholder="Purchase"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=1 style="margin-right: 35px">
        <input class="form-control" style="font-size: 12px; width: 140px" type="tel" v-model="invoiceNumber" @keyup.enter="getReceivings()" placeholder="Inoice #"/>
      </b-col>
      <b-col cols=2>
        <div style="display: flex">
          <b-button id="totalsMenu" size="sm" @click="toggleShowTotals()">Totals</b-button>
          <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
            <div style="width: 300px; font-size: 16px">
              <div>Total of {{pageable.totalElements}} rows</div>
              <div>Total ordered: ${{totalOrdered}}</div>
              <div>Total received: ${{totalUnitsPrice}}</div>
              <div>Units ordered: {{unitsOrdered}}</div>
              <div>Units received: {{unitsReceived}}</div>
            </div>
          </b-popover>
          <b-button size="sm" style="margin-left:3px" variant="primary" @click="editReceivings()">Edit ({{selectedReceivings.length}})</b-button>
        </div>
      </b-col>  
    </b-row>
    <b-table :items="receivings" :fields="fields" no-local-sorting>
      <template v-slot:head(action)="row">
        <div style="display: flex; width: 20px; margin-left: -25px">
          <b-button size="sm" @click="triggerAll(false)" variant="link">(-)</b-button><b-button size="sm" @click="triggerAll(true)" variant="link">(+)</b-button>
        </div>
      </template>
      <template v-slot:cell(name)="row">
        <div style="width:150px; overflow: wrap; font-size: 14px">
          <b-link role="button" @click.stop="goToReceiving(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}
        </div>
      </template>
      <template v-slot:cell(purchase)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px">
          <b-link role="button" @click.stop="goToPurchase(row.item.purchaseId)">{{row.item.purchaseNumber}}</b-link> {{row.item.purchaseName}}
        </div>
      </template>
      <template v-slot:cell(component)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px">
          <b-link role="button" @click.stop="goToComponent(row.item.componentId)">{{row.item.componentNumber}}</b-link> {{row.item.componentName}}
        </div>
      </template>
      <template v-slot:cell(receivedDate)="row">
        <span>{{formatDate(row.item.receivedDate)}}</span>
      </template>
      <template v-slot:cell(unitPrice)="row">
        <span>${{row.item.unitPrice}}</span>
      </template>
      <template v-slot:cell(totalPrice)="row">
        <span>${{getTotalPrice(row.item)}}</span>
      </template>
      <template v-slot:cell(unitsReceived)="row">
        <span style="color:red" v-if="row.item.extraUnits">+{{row.item.unitsReceived}}</span>
        <span v-if="!row.item.extraUnits">{{row.item.unitsReceived}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <input type="checkbox" v-model="selectedReceivings" :value="row.item">
      </template>      
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>

    <div v-if="billEditVisible">
			  <bill-edit :receivingId="receivingId" v-on:close="closeBillEdit"></bill-edit>
		</div>  
    <div v-if="billEditMultipleVisible">
			<bill-edit-multiple :receivingIds="receivingIds" v-on:close="closeBillEditMultiple"></bill-edit-multiple>
		</div>  
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";

export default {
  name: "BillList",
  components: {
    BillEdit: () => import("./modals/BillEdit"),
    BillEditMultiple: () => import("./modals/BillEditMultiple"),
  },
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: true},
      fields: [
        { key: "name", label: "Packing list #", sortable: false },
        { key: "purchase", label: "Purchase", sortable: false },
        { key: "component", label: "Component", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "receivedDate", label: "Received", sortable: false },
        { key: "invoiceNumber", label: "Invoice", sortable: false },
        { key: "unitsReceived", label: "Units", sortable: false },
        { key: "unitPrice", label: "Price", sortable: false },
        { key: "totalPrice", label: "Total", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availablePurchases: [],
      purchaseKv: {},
      availableComponents: [],
      componentKv: {},
      availableSuppliers: [],
      supplierKv: {},
      invoiceNumber: "",
      packingList: "",
      receivings: [], //ReceivingListDto
      showFilterMenu: false,
      showTotalsMenu: false,
      availableTotals: [],
      filter: {
        receivedFrom: null,
        receivedTo: null,
      },
      totalUnitsPrice: 0,
      totalOrdered: 0,
      unitsReceived: 0,
      unitsOrdered: 0,
      billEditVisible: false,
      billEditMultipleVisible: false,
      receivingId: null,
      selectedReceivings: [],
      receivingIds: [],
    };
  },
  computed: {},
  watch: {
    purchaseKv(new_value, old_value) {
      this.getReceivings();
    },
    componentKv(new_value, old_value) {
      this.getReceivings();
    },
    supplierKv(new_value, old_value) {
      this.getReceivings();
    }
  },
  methods: {
    getTotalPrice(dto){
      return parseFloat(dto.totalPrice).toLocaleString('en-US',{minimumFractionDigits: 2});
    },
    triggerAll(add){
      this.receivings.forEach(receiving => {
        if(add){
          var idx = this.selectedReceivings.findIndex(sr => sr.id == receiving.id);
          if(idx == -1){
            this.selectedReceivings.push(receiving);
          }
        }else{
          this.selectedReceivings = [];
        }
      })
    },    
    editReceivings(){
      this.selectedReceivings.forEach(r=>{
        this.receivingIds.push(r.id);
      })
      this.billEditMultipleVisible = true;
    },
    closeBillEdit(event){
      this.billEditVisible = false;
      this.getReceivings();
    },
    closeBillEditMultiple(event){
      this.billEditMultipleVisible = false;
      this.selectedReceivings = [];
      this.receivingIds = [];
      this.getReceivings();
    },
    toggleShowTotals(){
      if(!this.showTotalsMenu){
        this.getReceivings(true);
        this.getTotalPo();
      }
      this.showTotalsMenu = !this.showTotalsMenu;
    },
    getTotalPo(){
      http.get("/receiving/total/po", {params: {pageable: this.pageable,
        totals: true, 
        purchaseId: this.purchaseKv.id, 
        componentId: this.componentKv.id,
        supplierId: this.supplierKv.id,
        invoiceNumber: this.invoiceNumber,
        packingList: this.packingList,
        receivedFrom: this.filter.receivedFrom,
        receivedTo: this.filter.receivedTo}})
        .then(r => {
              this.totalOrdered = parseFloat(r.data.content[0][0]).toLocaleString('en-US',{minimumFractionDigits: 2});
              this.unitsOrdered = r.data.content[0][1].toLocaleString('en-US',{minimumFractionDigits: 2});
        }).catch(e => {console.log("API error: " + e);});
    },
    searchFilterMenu(){
      this.getReceivings();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.filter.receivedFrom = null;
      this.filter.receivedTo = null;
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
        return date? moment(date).utc().format("MM/DD/YYYY"):"";
    },
    getReceivings(totals) {
      http.get("/receiving/pageable", {params: {pageable: this.pageable,
        totals: totals, 
        purchaseId: this.purchaseKv.id, 
        componentId: this.componentKv.id,
        supplierId: this.supplierKv.id,
        invoiceNumber: this.invoiceNumber,
        packingList: this.packingList,
        receivedFrom: this.filter.receivedFrom,
        receivedTo: this.filter.receivedTo}})
        .then(r => {
          if(totals){
            this.totalUnitsPrice = parseFloat(r.data.content[0][0]).toLocaleString('en-US',{minimumFractionDigits: 2});
            this.unitsReceived = r.data.content[0][1].toLocaleString('en-US',{minimumFractionDigits: 2});
            this.getTotalPo();
          }else{
            this.receivings = r.data.content;
            this.pageable.totalElements = r.data.totalElements;
          }
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
    goToReceiving(receivingId) {
      this.receivingId = receivingId
      this.billEditVisible = true;
    },
    goToPurchase(purchase_id) {
      router.push("/purchaseEdit/" + purchase_id);
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
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
    // this.getReceivings();
    window.history.replaceState({}, document.title, window.location.pathname);
  },
  activated(){
    this.getReceivings();
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
