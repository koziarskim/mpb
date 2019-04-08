<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Receivings</span>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Purchase:</label>
        <b-select option-value="id" option-text="number" :list="availablePurchases" v-model="purchase"></b-select>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Component:</label>
        <b-select option-value="id" option-text="componentNumber" :list="availablePurchaseComponents" v-model="purchaseComponent"></b-select>
      </b-col>
      <b-col cols="2" style="margin-top: -12px">
        <label class="top-label">Search:</label>
        <input class="form-control" type="text" v-model="keyword" placeholder="Type to search">
      </b-col>

      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToReceiving('')">New Receiving</b-button>
        </div>
      </b-col>
    </b-row>
    <div v-if="receivings.length==0">Not found any purchase orders...</div>
    <b-table v-if="receivings.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="filteredReceivings" :fields="fields">
      <template slot="number" slot-scope="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.number}}</b-button>
      </template>
      <template slot="purchase" slot-scope="row">
        <b-button
          size="sm"
          @click.stop="goToPurchase(row.item.purchaseComponent.purchase.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.purchase.number:''}}</b-button>
      </template>
      <template slot="component" slot-scope="row">
        <b-button
          size="sm"
          @click.stop="goToComponent(row.item.purchaseComponent.component.id)"
          variant="link"
        >{{row.item.purchaseComponent?row.item.purchaseComponent.component.number:''}}</b-button>
      </template>
      <template slot="shippedDate" slot-scope="row">
        <span>{{formatDate(row.item.shippedDate)}}</span>
      </template>
      <template slot="etaDate" slot-scope="row">
        <span>{{formatDate(row.item.etaDate)}}</span>
      </template>
      <template slot="receivedDate" slot-scope="row">
        <span>{{formatDate(row.item.receivedDate)}}</span>
      </template>
      <template slot="action" slot-scope="row">
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
        { key: "number", label: "Receiving #", sortable: true },
        { key: "container", label: "Container", sortable: true },
        { key: "units", label: "Units", sortable: true },
        { key: "shippedDate", label: "Shipped", sortable: true },
        { key: "etaDate", label: "ETA", sortable: true },
        { key: "receivedDate", label: "Received", sortable: true },
        { key: "purchase", label: "Purchase #", sortable: true },
        { key: "component", label: "Component #", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      purchase: {},
      availablePurchases: [],
      purchaseComponent: {},
      availablePurchaseComponents: [],
      receivings: [],
      keyword: ""
    };
  },
  computed: {
    filteredReceivings() {
      var filtered = [];
      if (this.keyword) {
        this.receivings.filter(item => {
          if (
            item.number.includes(this.keyword) || item.purchase
              ? item.purchase.number.includes(this.keyword)
              : false || item.component
              ? item.component.number.includes(this.keyword)
              : false
          ) {
            filtered.push(item);
          }
        });
      } else {
        filtered = this.receivings;
      }
      return filtered;
    }
  },
  watch: {
    purchase(new_value, old_value) {
      this.purchaseComponent = {};
      this.getAvailablePurchaseComponents().then(r => {
        this.getReceivings();
      });
    },
    purchaseComponent() {
      this.getReceivings();
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
    getReceivings() {
      var purchase_id = this.purchase.id?this.purchase.id:"";
      var component_id = this.purchaseComponent.component
        ? this.purchaseComponent.component.id
        : "";
      http
        .get(
          "/receiving?purchase_id=" +
            purchase_id +
            "&component_id=" +
            component_id
        )
        .then(response => {
          this.receivings = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchases(purchase_id) {
      return http
        .get("/purchase/submitted")
        .then(response => {
          this.availablePurchases = response.data;
          if (purchase_id) {
            this.purchase = response.data.filter(it => it.id == purchase_id)[0];
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchaseComponents(component_id) {
        if(!this.purchase.id){
            this.availablePurchaseComponents = [];
            this.purchaseComponent = {};
            return new Promise((resolve, reject) => {});
        }
      return http
        .get("/purchaseComponent/purchase/" + this.purchase.id)
        .then(response => {
          this.availablePurchaseComponents = response.data;
          if (component_id) {
            this.purchaseComponent = response.data.filter(
              it => it.component.id == component_id
            )[0];
          }
        })
        .catch(e => {
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
    var purchase_id = parseInt(this.$route.query.purchase_id);
    var component_id = parseInt(this.$route.query.component_id);
    this.getAvailablePurchases(purchase_id).then(r => {
      this.getAvailablePurchaseComponents(component_id).then(r => {
        this.getReceivings();
      });
    });
    window.history.pushState({}, document.title, window.location.pathname);
  }
};
</script>

<style>
</style>
