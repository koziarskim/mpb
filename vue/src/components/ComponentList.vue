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
      <b-col>
        <div style="text-align: right;">
          <b-button size="sm" type="submit" variant="primary" @click="createNewPurchase()">New P.O.({{selectedComponents.length}})</b-button>&nbsp;
          <b-button size="sm" type="submit" variant="primary" @click="goToComponent('')">New Component</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
      <template v-slot:cell(name)="row">
        <b-link role="button" @click.stop="goToComponent(row.item.id)">{{row.item.number}}</b-link>
        <span style="font-size:11px"> ({{row.item.name}})</span>
      </template>
      <template v-slot:cell(unitsOnStock)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.unitsOnStock}}</b-button>
      </template>
      <template v-slot:cell(action)="row">
        <input type="checkbox" v-model="selectedComponents" :value="row.item">&nbsp;
        <b-button size="sm" @click.stop="deleteComponent(row.item.id)" :disabled="row.item.locked">x</b-button>
      </template>
    </b-table>
    <b-pagination v-model="pageable.currentPage" :per-page= "pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
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
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'updated', sortDesc: true},
      nameSearch: "",
      alertSecs: 0,
      alertMessage: "",
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "name", label: "Component # (Name)", sortable: false },
        { key: "categoryName", label: "Category", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
        { key: "unitsLocked", label: "Reserved", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      components: [],
      availableSuppliers: [],
      supplierKv: {},
      availableItems: [],
      itemKv: {},
      selectedComponents: []
    };
  },
  watch: {
    supplierKv(old_value, new_value){
      this.getComponents();
    },
    itemKv(old_value, new_value){
      this.getComponents();
    }
  },
  methods: {
    createNewPurchase(){
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
          itemId: this.itemKv.id}};
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
    deleteComponent(component_id) {
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete Component?').then(ok => {
        if(ok){
          var item = this.getItem(component_id);
          if (item && item.locked) {
              this.showAlert("Component is locked. It may be currently used by Item(s)");
              return;
          }
          http
              .delete("/component/" + component_id)
              .then(response => {this.getComponents();})
              .catch(e => {
              console.log("API Error: " + e);
              });
            }
        })
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
