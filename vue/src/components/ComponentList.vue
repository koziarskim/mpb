<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="nameSearch" @keyup.enter="getComponents()" placeholder="Number or Name"/>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplierKv" placeholder="Supplier"></b-select>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableUnitFilters" v-model="unitFilter" placeholder="Units"></b-select>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button size="sm" variant="primary" @click="goToComponent('')">New</b-button>
          <b-button size="sm" style="margin-left:3px" variant="primary" @click="createNewPurchase()">New P.O.({{selectedComponents.length}})</b-button>&nbsp;
        </div>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
      <template v-slot:cell(name)="row">
        <b-link role="button" @click.stop="goToComponent(row.item.id)">{{row.item.number}}</b-link>
        <div class="name-md" :title="row.item.name"> ({{row.item.name}})</div>
      </template>
      <template v-slot:cell(unitsOnStock)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.unitsOnStock}}</b-button>
      </template>
      <template v-slot:cell(action)="row">
        <input type="checkbox" v-model="selectedComponents" :value="row.item">
      </template>
      <template v-slot:head(action)="row">
          <b-button size="sm" @click="triggerAll(true)" variant="link">(+)</b-button>
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
  name: "edit-component",
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
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "unitsSoldNotProd", label: "Open Sales", sortable: false },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
        { key: "unitsPendingPo", label: "Pending PO", sortable: false },
        { key: "unitsLocked", label: "Reserved", sortable: false },
        { key: "unitsShort", label: "Short", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      components: [],
      availableSuppliers: [],
      supplierKv: {},
      availableItems: [],
      itemKv: {},
      selectedComponents: [],
      availableUnitFilters: [
        {id: "ONLY_SHORT", name: "Units Short"},
        {id: "OUT_STOCK", name: "Out of Stock"},
        {id: "OPEN_SALE", name: "Open Sales"},
      ],
      unitFilter: {},
    };
  },
  watch: {
    supplierKv(old_value, new_value){
      this.getComponents();
    },
    itemKv(old_value, new_value){
      this.getComponents();
    },
    unitFilter(old_value, new_value){
      this.getComponents();
    }
  },
  methods: {
    triggerAll(add){
      this.components.forEach(c=> {
        if(add){
          var idx = this.selectedComponents.findIndex(sc => sc.id == c.id);
          if(idx == -1){
            this.selectedComponents.push(c);
          }
        }else{
          var idx = this.selectedComponents.findIndex(sc => sc.id == c.id);
          if(idx > -1){
            this.selectedComponents.splice(idx, 1);
          }
        }
      })
    },
    createNewPurchase(){
      if(this.selectedComponents.length>50){
        alert("Maximum 50 components per P.O.");
        return;
      }
      var supplierId = this.selectedComponents[0].supplierId;
      var supplierIds = this.selectedComponents.filter(c=> c.supplierId != this.selectedComponents[0].supplierId);
      if(supplierIds.length>0){
        alert("Supplier missmatch! Only components to single supplier are allowed!");
        return false;
      }
      var query = { componentIds: this.selectedComponents.map(c=> c.id).join(",") };
      router.push({ path: "/purchaseNew", query: query })
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
      var query = {params: {pageable: this.pageable, nameSearch: this.nameSearch, supplierId: this.supplierKv.id,
          itemId: this.itemKv.id, unitFilter: this.unitFilter.id}};
      http.get("/component/pageable", query).then(response => {
        this.components = response.data.content;
        this.pageable.totalElements = response.data.totalElements;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableSuppliers() {
      http.get("/supplier/kv").then(r => {
        this.availableSuppliers = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
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
    }
  },
  mounted() {
    this.getComponents();
    this.getAvailableSuppliers();
    this.getAvailableItems();
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
