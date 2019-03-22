<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Orders for Component</span>
      </b-col>
      <!-- <b-col cols="2">
        <input class="form-control" type="text" v-model="filterComponent" placeholder="Search Component">
      </b-col> -->
    </b-row>
    <div v-if="purchases.length==0">Not found any purchase orders...</div>
    <b-table v-if="purchases.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="purchases" :fields="fields">
      <template slot="componentNumber" slot-scope="row">
        <b-button size="sm" @click.stop="goToComponent(row.item.componentId)" variant="link">{{row.item.componentNumber}}</b-button>
      </template>
      <template slot="purchaseNumber" slot-scope="row">
        <b-button size="sm" @click.stop="goToPurchase(row.item.purchaseId)" variant="link">{{row.item.purchaseNumber}}</b-button>
      </template>
      <template slot="shipments" slot-scope="row">
        <b-button size="sm" @click.stop="goToShipment(row.item.componentId, row.item.purchaseId)">Shipment</b-button>
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
        { key: "number", label: "Purchase #", sortable: true },
        { key: "units", label: "On Stack", sortable: true },
        { key: "completed", label: "Completed", sortable: true },
        { key: "shipments", label: "Shipments", sortable: true },
      ],
      purchases: [],
    //   filterPurchase: "",
    };
  },
  computed: {
//     filteredPurchases() {
//       var filtered = [];
//       if (this.filterComponent) {
//         this.inventories.filter(it => {
//           var foundComponent = false;
//           if (
//             !this.filterComponent ||
//             it.componentNumber.includes(this.filterComponent) ||
//             it.componentName.includes(this.filterComponent) ||
//             it.supplierName.includes(this.filterComponent)
//           ) {
//             filtered.push(it);
//           }
//         });
//       }else{
//           filtered = this.inventories;
//       }
//       return filtered;
//     }
  },
  methods: {
    getPurchases(component_id) {
      http
        .get("/purchase/inventory/"+component_id)
        .then(response => {
          this.purchases = response.data;
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
      var component_id = this.$route.params.component_id;
      if(component_id){
          this.getPurchases(component_id)
      }
  }
};
</script>

<style>
</style>
