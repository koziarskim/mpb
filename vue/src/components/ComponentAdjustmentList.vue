<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
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
      <template v-slot:cell(action)="row">
        <input type="checkbox" v-model="selectedComponents" :value="row.item">
      </template>
      <template v-slot:head(action)="row">
          <b-button style="margin-left:-10px; margin-bottom:-10px" size="sm" @click="triggerAll(false)" variant="link">(-)</b-button><br/><b-button style="margin-left: -10px; margin-bottom: -10px" size="sm" @click="triggerAll(true)" variant="link">(+)</b-button>
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
  name: "ComponentList",
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
        { key: "unitsOnStock", label: "Floor", sortable: false },
        { key: "unitsForSale", label: "Sales", sortable: false },
        { key: "unitsSchedProd", label: "Sched/Prod", sortable: false },
        { key: "unitsOrderedRec", label: "PO/Received", sortable: false },
        { key: "unitsShort", label: "Short", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      components: [],
      availableComponents: [],
      componentKv: {},
      availableItems: [],
      itemKv: {},
      selectedComponents: [],
      availableUnitFilters: [
        {id: "ONLY_SHORT", name: "Units Short"},
        {id: "ON_STOCK", name: "On Stock"},
        {id: "OPEN_SALE", name: "Open Sales"},
      ],
      unitFilter: {},
      availableComponentTypes: [],
      componentType: {},
      availableCategories: [],
      category: {},
      showFilterMenu: false,
    };
  },
  watch: {
    componentKv(new_value, old_value){
      this.getComponentAdjustments();
    },
  },
  methods: {
    searchFilterMenu(){
      this.getComponentAdjustments();
      this.showFilterMenu = false;
    },
    clearFilterMenu(){
      this.category = {};
      this.componentType = {};
      this.getComponentAdjustments();
      this.showFilterMenu = false;
    },    
    getUnitsShort(component){
      return component.unitsShort<0?0:component.unitsShort;
    },
    getUnitsOnStock(component){
      return component.unitsOnStock<0?0:component.unitsOnStock;
    },
    triggerAll(add){
      this.components.forEach(c=> {
        if(add){
          var idx = this.selectedComponents.findIndex(sc => sc.id == c.id);
          if(idx == -1){
            this.selectedComponents.push(c);
          }
        }else{
          this.selectedComponents = [];
          // var idx = this.selectedComponents.findIndex(sc => sc.id == c.id);
          // if(idx > -1){
          //   this.selectedComponents.splice(idx, 1);
          // }
        }
      })
    },
    sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getComponentAdjustments();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getComponentAdjustments();
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getComponentAdjustments() {
      var query = {params: {pageable: this.pageable, componentId: this.componentKv.id}};
      http.get("/componentAdjustment/pageable", query).then(response => {
        this.componentAdjustments = response.data.content;
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
      var query = {params: {categoryId: this.category.id}}
      http.get("/registery/componentType/kv", query).then(r => {
        this.availableComponentTypes = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableComponents() {
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
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
    this.getAvailableComponents();
  },
  activated(){
    this.getComponentAdjustments();
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
