<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Receivings</span>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Purchase:</label>
        <b-select option-value="id" option-text="name" :list="availablePurchases" v-model="purchase"></b-select>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Component:</label>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="component"></b-select>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToReceiving('')">New Receiving</b-button>
        </div>
      </b-col>
    </b-row>
    <div v-if="receivings.length==0">Not found any purchase orders...</div>
    <b-table v-if="receivings.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="receivings" :fields="fields">
      <template v-slot:cell(number)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.number}}</b-button>
      </template>
      <template v-slot:cell(purchase)="row">
        <b-button
          size="sm"
          @click.stop="goToPurchase(row.item.purchaseComponent.purchase.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.purchase.number:''}}</b-button>
      </template>
      <template v-slot:cell(component)="row">
        <b-button
          size="sm"
          @click.stop="goToComponent(row.item.purchaseComponent.component.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.component.number:''}}</b-button>
      </template>
      <template v-slot:cell(shippedDate)="row">
        <span>{{formatDate(row.item.shippedDate)}}</span>
      </template>
      <template v-slot:cell(etaDate)="row">
        <span>{{formatDate(row.item.etaDate)}}</span>
      </template>
      <template v-slot:cell(receivedDate)="row">
        <span>{{formatDate(row.item.receivedDate)}}</span>
      </template>
      <template v-slot:cell(action)="row">
        <b-button size="sm" @click.stop="deleteReceiving(row.item.id)">x</b-button>
      </template>
    </b-table>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";

export default {
  data() {
    return {
      sortBy: "id",
      sortDesc: false,
      fields: [
        { key: "number", label: "Receiving #", sortable: false },
        { key: "container", label: "Container", sortable: false },
        { key: "units", label: "Units", sortable: false },
        // { key: "unitsReserved", label: "Reserved", sortable: false },
        { key: "shippedDate", label: "Shipped", sortable: false },
        { key: "etaDate", label: "ETA", sortable: false },
        { key: "receivedDate", label: "Received", sortable: false },
        { key: "purchase", label: "Purchase #", sortable: false },
        { key: "component", label: "Component #", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      availablePurchases: [],
      purchase: {},
      availableComponents: [],
      component: {},
      receivings: [],
    };
  },
  computed: {},
  watch: {
    purchase(new_value, old_value) {
      this.component = {};
      this.getAvailableComponents().then(r => {
        this.getReceivings(new_value.id, null);
      });
    },
    component(new_value, old_value) {
      this.getReceivings(this.purchase.id, new_value.id);
    }
  },
  methods: {
    formatDate(date){
        return date
            ? moment(date)
                .utc()
                .format("YYYY-MM-DD")
            : "";
    },
    getReceivings(purchase_id, component_id) {
      http.get("/receiving", {params: {purchase_id: purchase_id, component_id: component_id}})
        .then(response => {
          this.receivings = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchases(purchase_id) {
      return http.get("/purchase/kv").then(response => {
        this.availablePurchases = response.data;
        if (purchase_id) {
          this.purchase = {id: purchase_id}
        }
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableComponents(component_id) {
      return http.get("/component/kv").then(response => {
        this.availableComponents = response.data;
        if (component_id) {
          this.component = {id: component_id};
        }
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    goToReceiving(receiving_id) {
      if (!receiving_id) {
        var receiving = {};
        if (!this.purchaseComponent.id) {
          alert("Please select Purchase and Component first!!!")
          return;
        }
        receiving.purchaseComponent = { id: this.purchaseComponent.id };
        return http
          .post("/receiving", receiving)
          .then(response => {
            router.push("/receivingEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      } else {
        router.push("/receivingEdit/" + receiving_id);
      }
    },
    goToPurchase(purchase_id) {
      router.push("/purchaseEdit/" + purchase_id);
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    deleteReceiving(receiving_id) {
      http
        .delete("/receiving/" + receiving_id)
        .then(response => {
          this.getReceivings();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    }
  },
  mounted() {
    var purchase_id = this.$route.query.purchase_id;
    var component_id = this.$route.query.component_id;
    this.getAvailablePurchases(purchase_id).then(r => {
      this.getAvailableComponents(component_id).then(r => {
        this.getReceivings(purchase_id, component_id);
      });
    });
    window.history.replaceState({}, document.title, window.location.pathname);
  }
};
</script>

<style>
</style>
