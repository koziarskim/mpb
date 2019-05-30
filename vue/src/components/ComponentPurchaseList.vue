<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Component: {{component.number}}</span>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Purchases (POs):</label>
        <b-table v-if="purchaseComponents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="purchaseComponents" :fields="columns">
          <template slot="purchase" slot-scope="row">
            <b-button size="sm" @click.stop="goToPurchaseEdit(row.item.purchase.id)" variant="link">{{row.item.purchase.number}}</b-button>
          </template>
          <template slot="unitsAwaiting" slot-scope="row">
            <span>{{calculateAwaiting(row.item)}}</span>
          </template>
          <template slot="action" slot-scope="row">
            <b-button size="sm" @click.stop="goToReceiving(row.item.purchase.id)" :disabled="!row.item.purchase.submitted || row.item.purchase.received">Receivings</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      purchaseComponents: [],
      component: {},
      sortBy: "purchase.number",
      sortDesc: false,
      columns: [
        { key: "purchase", label: "Purchase #", sortable: false },
        { key: "unitsOrdered", label: "Ordered", sortable: false },
        { key: "unitsAwaiting", label: "Awaiting", sortable: false },
        { key: "unitsInTransit", label: "In Transit", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        // { key: "status", label: "Status", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      expectedDate: ""
    };
  },
  computed: {},
  watch: {},
  methods: {
    calculateAwaiting(item){
        var units = item.unitsOrdered - +item.unitsInTransit - +item.unitsReceived;
        if(units < 0){
            return 0;
        }
        return units;
    },  
    getComponent(component_id) {
      http
        .get("/component/" + component_id)
        .then(response => {
          this.component = response.data;
          this.getPurchaseComponents(component_id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getPurchaseComponents(component_id) {
      http
        .get("/purchaseComponent/component/" + component_id)
        .then(response => {
          this.purchaseComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToPurchaseEdit(purchase_id) {
      router.push("/purchaseEdit/" + purchase_id);
    },
    goToReceiving(purchase_id) {
      var query = { purchase_id: purchase_id, component_id: this.component.id };
      router.push({ path: "/receivingList", query: query });
    }
  },
  mounted() {
    var component_id = this.$route.params.component_id;
    if (component_id) {
      this.getComponent(component_id);
    }
  }
};
</script>

<style>
</style>
