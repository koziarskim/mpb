<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Inventory</span>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="text" v-model="filterComponent" placeholder="Search Component">
      </b-col>
    </b-row>
    <div v-if="filteredInventories.length==0">Not found any purchase orders...</div>
    <b-table v-if="filteredInventories.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="filteredInventories" :fields="fields">
      <template v-slot:cell(componentNumber)="row">
        <b-button size="sm" @click.stop="goToComponent(row.item.componentId)" variant="link">{{row.item.componentNumber}}</b-button>
      </template>
      <template v-slot:cell(unitsOrdered)="row">
        <b-button size="sm" @click.stop="goToPurchase(row.item.componentId)" variant="link">{{row.item.unitsOrdered?row.item.unitsOrdered:0}}</b-button>
      </template>
    </b-table>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";

export default {
  data() {
    return {
      sortBy: "componentId",
      sortDesc: false,
      fields: [
        { key: "componentNumber", label: "Component #", sortable: false },
        { key: "componentName", label: "Name", sortable: false },
        { key: "supplierName", label: "Supplier", sortable: false },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
        { key: "unitsOrdered", label: "Ordered", sortable: false }
      ],
      inventories: [],
      filterComponent: "",
    };
  },
  computed: {
    filteredInventories() {
      var filtered = [];
      if (this.filterComponent) {
        this.inventories.filter(it => {
          var foundComponent = false;
          if (
            !this.filterComponent ||
            it.componentNumber.includes(this.filterComponent) ||
            it.componentName.includes(this.filterComponent) ||
            it.supplierName.includes(this.filterComponent)
          ) {
            filtered.push(it);
          }
        });
      }else{
          filtered = this.inventories;
      }
      return filtered;
    }
  },
  methods: {
    getInventories() {
      http
        .get("/component/inventory")
        .then(response => {
          this.inventories = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    goToPurchase(component_id) {
      router.push("/inventoryPurchase/" + component_id);
    }
  },
  mounted() {
    this.getInventories();
  }
};
</script>

<style>
</style>
