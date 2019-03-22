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
        <b-select option-value="id" option-text="number" :list="availableComponents" v-model="component"></b-select>
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
        <b-button size="sm" @click.stop="goToPurchase(row.item.purchase.id)" variant="link">{{row.item.purchase?row.item.purchase.number:''}}</b-button>
      </template>
      <template slot="component" slot-scope="row">
        <b-button size="sm" @click.stop="goToComponent(row.item.component.id)" variant="link">{{row.item.component?row.item.component.number:''}}</b-button>
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

export default {
  data() {
    return {
      sortBy: "id",
      sortDesc: false,
      fields: [
        { key: "number", label: "Receiving #", sortable: true },
        { key: "reference", label: "Reference", sortable: true },
        { key: "units", label: "Units", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "purchase", label: "Purchase #", sortable: true },
        { key: "component", label: "Component #", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      purchase: {},
      component: {},
      availablePurchases: [],
      availableComponents: [],
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
  methods: {
    getReceivings() {
      http
        .get("/receiving/")
        .then(response => {
          this.receivings = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getComponent(component_id) {
      http
        .get("/component/" + component_id)
        .then(response => {
          this.component = response.data;
          this.getPurchasesData(component_id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailablePurchases() {
      return http
        .get("/purchase/")
        .then(response => {
          this.availablePurchases = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableComponents() {
      return http
        .get("/component/")
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItem(component_id) {
      var component;
      var found = this.purchases.some(function(element) {
        if (element.id === component_id) {
          component = element;
        }
      });
      return component;
    },
    goToReceiving(receiving_id) {
      if (!receiving_id) {
        http
          .post("/receiving")
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
    this.getAvailablePurchases().then(r => {
      this.getAvailableComponents().then(r => {
        this.getReceivings();
      });
    });
  }
};
</script>

<style>
</style>
