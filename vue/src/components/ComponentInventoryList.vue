<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
        <div style="position: flex">
          <b-button style="margin-left: 3px" id="filterMenu" size="sm" @click="showFilterMenu = true">Filter</b-button>
          <b-button style="margin-left: 3px" size="sm" @click="clearFilterMenu()">Clear</b-button>
        </div>
        <b-popover :show="showFilterMenu" placement="bottom" target="filterMenu" variant="secondary">
          <template v-slot:title>
            <span>Advanced Filters</span>
          </template>
          <div style="width: 400px">
            <b-row>
              <b-col cols=6>
                <b-select option-value="id" option-text="name" :list="availableCategories" v-model="categoryKv" placeholder="Category"></b-select>
              </b-col>
              <b-col cols=6>
                <b-select option-value="id" option-text="name" :list="availableComponentTypes" v-model="componentTypeKv" placeholder="Type"></b-select>
              </b-col>
            </b-row>
            <br/>
            <b-row>
              <b-col cols=12>
                Include All Floor is &#62; 0:<input type="checkbox" style="margin-left: 3px" v-model="positiveFloor">
                , is &#61; 0:<input type="checkbox" style="margin-left: 3px" v-model="zeroFloor">
                , is &#60; 0:<input type="checkbox" style="margin-left: 3px" v-model="negativeFloor">
              </b-col>
            </b-row>
            <br/>
            <b-row>
              <b-col cols=12>
                Include Non Inventory:<input type="checkbox" style="margin-left: 3px" v-model="nonInventory">
              </b-col>
            </b-row>
          </div>
        </b-popover>
        <input class="form-control" style="width: 200px; margin-left: 3px; font-size: 12px" type="tel" v-model="nameSearch" @keyup.enter="getComponents()" placeholder="Number or Name"/>
        <b-select style="width: 200px; margin-left: 3px;" option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
        <b-select style="width: 200px; margin-left: 3px;" option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
        <input style="width: 165px; margin-left: 3px; height: 33px" class="form-control" type="date" v-model="dateFrom" @change="dateToUpdated()">
        <input style="width: 165px; margin-left: 3px; height: 33px" class="form-control" type="date" v-model="dateTo" @change="dateToUpdated()">
      <b-col cols=1>
        <div style="margin-left: 15px">
          <b-button id="totalsMenu" size="sm" @click="toggleTotals()">Totals</b-button>
          <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
            <div style="width: 300px; font-size: 16px">
              <div><b>Total Components:</b> {{pageable.totalElements.toLocaleString()}}</div>
              <div><b>Total Received:</b> {{totals.received.toLocaleString()}}</div>
              <div><b>Total Adjusted:</b> {{totals.adjusted.toLocaleString()}}</div>
              <div><b>Total Produced:</b> {{totals.produced.toLocaleString()}}</div>
              <div><b>Total Shipped:</b> {{totals.shipped.toLocaleString()}}</div>
              <div><b>Total Units Floor:</b> {{totals.floor.toLocaleString()}}</div>
              <div><b>Total Asmbly Floor:</b> {{totals.asmbly.toLocaleString()}}</div>
              <div><b>Total All Floor:</b> {{totals.all.toLocaleString()}}</div>
              <div><b>Total Price:</b> ${{totals.price.toLocaleString('en-US',{minimumFractionDigits: 2})}}</div>
            </div>
          </b-popover>
        </div>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
      <template v-slot:head(unitsReceived)="row">
        <div>Received</div><div class="mpb-head-line">Units Received</div>
      </template>
      <template v-slot:head(unitsAdjusted)="row">
        <div>Adjusted</div><div class="mpb-head-line">Units Adjusted</div>
      </template>
      <template v-slot:head(unitsProduced)="row">
        <div>Produced</div><div class="mpb-head-line">Used For Prod</div>
      </template>
      <template v-slot:head(unitsShipped)="row">
        <div>Shipped</div><div class="mpb-head-line">Units Shipped</div>
      </template>
      <template v-slot:head(compOnFloor)="row">
        <div>Units Floor</div><div class="mpb-head-line">Received-Produced</div>
      </template>
      <template v-slot:head(prodOnFloor)="row">
        <div>Asmbly Floor</div><div class="mpb-head-line">Produced-Shipped</div>
      </template>
      <template v-slot:head(unitsOnFloor)="row">
        <div>All Floor</div><div class="mpb-head-line">Units + Asmbly</div>
      </template>
      <template v-slot:head(averageUnitPrice)="row">
        <div>Unit Price</div><div class="mpb-head-line">Average Price</div>
      </template>
      <template v-slot:head(totalFloorPrice)="row">
        <div>Total Price</div><div class="mpb-head-line">All Floor * Price</div>
      </template>
      <template v-slot:cell(name)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToComponent(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}</div>
      </template>
      <template v-slot:cell(supplierName)="row">
        <div style="font-size: 12px">{{row.item.supplierName}}</div>
        <div style="font-size: 12px">{{row.item.categoryName}} - {{row.item.componentTypeName}}</div>
      </template>
      <template v-slot:cell(unitsReceived)="row">
        {{row.item.unitsReceived.toLocaleString()}}
      </template>
      <template v-slot:cell(unitsAdjusted)="row">
        <b-link role="button" @click="goToComponentAdjustment(row.item.id)">{{row.item.unitsAdjusted.toLocaleString()}}</b-link>
      </template>
      <template v-slot:cell(unitsProduced)="row">
        {{row.item.unitsProduced.toLocaleString()}}
      </template>
      <template v-slot:cell(unitsShipped)="row">
        {{row.item.unitsShipped.toLocaleString()}}
      </template>
      <template v-slot:cell(compOnFloor)="row">
        {{row.item.compOnFloor.toLocaleString()}}
      </template>
      <template v-slot:cell(prodOnFloor)="row">
        {{row.item.prodOnFloor.toLocaleString()}}
      </template>
      <template v-slot:cell(unitsOnFloor)="row">
        {{row.item.unitsOnFloor.toLocaleString()}}
      </template>
      <template v-slot:cell(averageUnitPrice)="row">
        ${{row.item.averageUnitPrice.toLocaleString('en-US',{minimumFractionDigits: 2})}}
      </template>
      <template v-slot:cell(totalFloorPrice)="row">
        ${{row.item.totalFloorPrice.toLocaleString('en-US',{minimumFractionDigits: 2})}}
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
import moment from "moment";

export default {
  name: "ComponentInventoryList",
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'number', sortDesc: true},
      nameSearch: "",
      alertSecs: 0,
      alertMessage: "",
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "name", label: "Component # (Name)", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "unitsReceived", label: "Rec", sortable: false },
        { key: "unitsAdjusted", label: "Adjusted", sortable: false },
        { key: "unitsProduced", label: "Prod", sortable: false },
        { key: "unitsShipped", label: "Shipp", sortable: false },
        { key: "compOnFloor", label: "Comp Floor", sortable: false },
        { key: "prodOnFloor", label: "Asbly Floor", sortable: false },
        { key: "unitsOnFloor", label: "All Floor", sortable: false },
        { key: "averageUnitPrice", label: "Unit Price", sortable: false },
        { key: "totalFloorPrice", label: "Total Floor", sortable: false },
      ],
      components: [],
      availableSuppliers: [],
      supplierKv: {},
      availableItems: [],
      itemKv: {},
      availableComponentTypes: [],
      componentTypeKv: {},
      availableCategories: [],
      categoryKv: {},
      dateFrom:moment('2019-01-01').format("YYYY-MM-DD"),
      dateTo: moment().format("YYYY-MM-DD"),
      positiveFloor: true,
      zeroFloor: true,
      negativeFloor: true,
      nonInventory: false,
      showFilterMenu: false,
      showTotalsMenu: false,
      totals: {
        received: 0,
        adjusted: 0,
        produced: 0,
        shipped: 0,
        floor: 0,
        asmbly: 0,
        all: 0,
        price: 0,
      }
    };
  },
  watch: {
    supplierKv(new_value, old_value){
      this.getComponents();
    },
    itemKv(new_value, old_value){
      this.getComponents();
    },
    categoryKv(new_value, old_value){
      this.getAvailableComponentTypes();
      this.getComponents();
    },
    componentTypeKv(new_value, old_value){
      this.getComponents();
    },
    positiveFloor(new_value, old_value){
      this.getComponents();
    },
    zeroFloor(new_value, old_value){
      this.getComponents();
    },
    negativeFloor(new_value, old_value){
      this.getComponents();
    },
    nonInventory(new_value, old_value){
      this.getComponents();
    },
  },
  methods: {
    toggleTotals(){
      if(!this.showTotalsMenu){
        this.getComponents(true);
      }
      this.showTotalsMenu = !this.showTotalsMenu;
    },    
    dateToUpdated(){
      this.getComponents();
    },
    searchFilterMenu(){
      this.getComponents();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      router.go();
    },    
    getUnitsShort(component){
      return component.unitsShort<0?0:component.unitsShort;
    },
    getUnitsOnStock(component){
      return component.unitsOnStock<0?0:component.unitsOnStock;
    },
    sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getComponents();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getComponents();
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getComponents(totals) {
      this.showTotalsMenu = false;
      var query = {params: {
        pageable: this.pageable,
        totals: totals, 
        nameSearch: this.nameSearch, 
        supplierId: this.supplierKv.id,
        itemId: this.itemKv.id, 
        categoryId: this.categoryKv.id, 
        componentTypeId: this.componentTypeKv.id,
        dateFrom: this.dateFrom,
        dateTo: this.dateTo,
        positiveFloor: this.positiveFloor,
        zeroFloor: this.zeroFloor,
        negativeFloor: this.negativeFloor,
        nonInventory: this.nonInventory}};
      http.get("/component/inventory/pageable", query).then(r => {
        if(totals){
          this.totals.received = parseFloat(r.data.content[0][0]);
          this.totals.produced = parseFloat(r.data.content[0][1]);
          this.totals.shipped = parseFloat(r.data.content[0][2]);
          this.totals.floor = parseFloat(r.data.content[0][3]);
          this.totals.asmbly = parseFloat(r.data.content[0][4]);
          this.totals.all = parseFloat(r.data.content[0][5]);
          this.totals.price = parseFloat(r.data.content[0][6]);
          this.totals.adjusted = parseFloat(r.data.content[0][7]);
        } else {
          this.components = r.data.content;
          this.pageable.totalElements = r.data.totalElements;
        }
      });
    },
    getAvailableCategories() {
      http.get("/category/component/kv").then(r => {
        this.availableCategories = r.data;
      });
    },
    getAvailableComponentTypes() {
      var query = {params: {categoryId: this.categoryKv.id}}
      http.get("/registery/componentType/kv", query).then(r => {
        this.availableComponentTypes = r.data;
      });
    },
    getAvailableSuppliers() {
      http.get("/supplier/kv").then(r => {
        this.availableSuppliers = r.data;
      });
    },
    getAvailableItems() {
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      });
    },
    getItem(component_id) {
      var component;
      var found = this.components.some(function(element) {
        if (element.id === component_id) {
          component = element;
        }
      });
      return component;
    },
    goToComponent(component_id) {
      if (!component_id) {
        router.push("/componentEdit");
      } else {
        router.push("/componentEdit/" + component_id);
      }
    },
    goToComponentAdjustment(componentId) {
      var query = { componentId: componentId };
      router.push({ path: "/componentAdjustmentList", query: query });
    },
    goToReceiving(component_id){
      var query = { component_id: component_id };
      router.push({ path: "/receivingList", query: query });
    },
    goToPurchaseList(component_id){
      var query = { component_id: component_id };
      router.push({ path: "/purchaseList", query: query });
    }
  },
  mounted() {
    // this.getComponents();
    this.getAvailableSuppliers();
    this.getAvailableItems();
    this.getAvailableCategories();
    this.getAvailableComponentTypes();
  },
  activated(){
    this.getComponents();
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
