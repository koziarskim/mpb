<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <!-- <b-col cols=1 style="margin-right: -45px;">
        <b-button id="filterMenu" size="sm" @click="showFilterMenu = true">Filter</b-button>
        <b-popover :show="showFilterMenu" placement="bottom" target="filterMenu" variant="secondary">
          <template v-slot:title>
            <span>Advanced Filters</span>
            <b-button style="margin-left: 185px" size="sm" @click="searchFilterMenu()">Search</b-button>
            <b-button style="margin-left: 10px" size="sm" @click="clearFilterMenu()">Clear</b-button>
          </template>
          <div style="width: 400px">
            <b-row>
            </b-row>
          </div>
        </b-popover>
      </b-col> -->
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="nameSearch" @keyup.enter="getComponents()" placeholder="Number or Name"/>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableCategories" v-model="categoryKv" placeholder="Category"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponentTypes" v-model="componentTypeKv" placeholder="Type"></b-select>
      </b-col>
      <b-col cols=2>
        <input style="height: 33px" class="form-control" type="date" v-model="dateTo" @change="dateToUpdated()">
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
      <template v-slot:cell(name)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click.stop="goToComponent(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}</div>
      </template>
      <template v-slot:cell(unitsOnStock)="row">
        <span>{{getUnitsOnStock(row.item)}}</span>
      </template>
      <template v-slot:cell(unitsOrderedRec)="row">
        <b-button size="sm" @click.stop="goToPurchaseList(row.item.id)" variant="link">{{row.item.unitsOrdered}}</b-button>/<b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.unitsReceived}}</b-button>
      </template>
      <template v-slot:cell(unitsShort)="row">
        <span>{{getUnitsShort(row.item)}}</span>
      </template>
      <template v-slot:cell(unitsSchedProd)="row">
        <span>{{row.item.unitsLocked + row.item.unitsForProduction}} / {{row.item.unitsForProduction}}</span>
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
        { key: "categoryName", label: "Category", sortable: false },
        { key: "componentTypeName", label: "Type", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "unitsShipped", label: "Shipped", sortable: false },
        { key: "unitsOnFloor", label: "Floor", sortable: false },
        { key: "averageUnitPrice", label: "Unit Price", sortable: false },
        { key: "floorPrice", label: "Total Floor", sortable: false },
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
      dateTo: moment().format("YYYY-MM-DD"),
      showFilterMenu: false,
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
  },
  methods: {
    dateToUpdated(){
      this.getComponents();
    },
    searchFilterMenu(){
      this.getComponents();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.getComponents();
      this.showFilterMenu = false;
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
    getComponents() {
      var query = {params: {
        pageable: this.pageable, 
        nameSearch: this.nameSearch, 
        supplierId: this.supplierKv.id,
        itemId: this.itemKv.id, 
        categoryId: this.categoryKv.id, 
        componentTypeId: this.componentTypeKv.id,
        dateTo: this.dateTo}};
      http.get("/component/inventory/pageable", query).then(response => {
        this.components = response.data.content;
        this.pageable.totalElements = response.data.totalElements;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableCategories() {
      http.get("/category/component/kv").then(r => {
        this.availableCategories = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableComponentTypes() {
      var query = {params: {categoryId: this.categoryKv.id}}
      http.get("/registery/componentType/kv", query).then(r => {
        this.availableComponentTypes = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableSuppliers() {
      http.get("/supplier/kv").then(r => {
        this.availableSuppliers = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableItems() {
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      }).catch(e => {
        console.log("API error: " + e);
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
