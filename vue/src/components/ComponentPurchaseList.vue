<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="3">
        <span style="text-align: left; font-size: 18px; font-weight: bold">POs for Component: {{component.number}}</span>
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="text" v-model="keyword" placeholder="Type to search">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToPurchase('')">New P.O.</b-button>
        </div>
      </b-col>
    </b-row>
    <div v-if="purchases.length==0">Not found any purchase orders...</div>
    <b-table v-if="purchases.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="items" :fields="fields" :keyword="keyword">
      <template slot="number" slot-scope="row">
        <b-button size="sm" @click.stop="goToPurchase(row.item.id)" variant="link">{{row.item.number}}</b-button>
      </template>
      <template slot="completed" slot-scope="row">
        <span>{{row.item.completed?"Yes":"No"}}</span>
      </template>
      <template slot="receiving" slot-scope="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)">View</b-button>
      </template>
      <template slot="pdf" slot-scope="row">
        <a :href="rowPdfUrl(row.item.id)" target="_blank">
          <img src="../assets/pdf-download.png" width="20px">
        </a>
      </template>
      <template slot="expectedDate" slot-scope="row">
        <span>{{row.item.expectedDate | formatDate}}</span>
      </template>
      <template slot="date" slot-scope="row">
        <span>{{row.item.date | formatDate}}</span>
      </template>
    </b-table>
    <b-alert :show="alertSecs" dismissible variant="warning" @dismiss-count-down="(secs) => { alertSecs = secs }">{{alertMessage}}</b-alert>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";

export default {
  data() {
    return {
      alertSecs: 0,
      alertMessage: "",
      sortBy: "id",
      sortDesc: false,
      fields: [
        { key: "number", label: "P.O. #", sortable: true },
        { key: "supplier.name", label: "Supplier", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "expectedDate", label: "Expected", sortable: true },
        { key: "ordered", label: "Ordered", sortable: true },
        { key: "completed", label: "Completed", sortable: true },
        { key: "pdf", label: "PDF", sortable: true },
        { key: "receiving", label: "Receivings", sortable: false }
      ],
      component: {},
      purchaseComponents: [],
      purchases: [],
      keyword: ""
    };
  },
  computed: {
    items() {
      var filtered = [];
      if (this.keyword) {
        this.purchases.filter(item => {
          if (
            item.number.includes(this.keyword) ||
            item.supplier.name.includes(this.keyword) ||
            (item.date ? item.date.includes(this.keyword) : false)
          ) {
            filtered.push(item);
          }
        });
      } else {
        filtered = this.purchases;
      }
      return filtered;
    }
  },
  methods: {
    disabled(purchase) {
      return purchase.completed;
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
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
    getPurchasesData(component_id) {
      http
        .get("/purchase/component/" + component_id)
        .then(response => {
          this.purchases = response.data;
          console.log("Success getting component data");
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
    goToReceiving(purchase_id) {
      router.push("/receivingList/" + purchase_id + "/" + this.component.id);
    },
    goToPurchase(id) {
      if (!id) {
        http
          .post("/purchase")
          .then(response => {
            router.push("/purchaseEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      } else {
        router.push("/purchaseEdit/" + id);
      }
    },
    rowPdfUrl: function(purchase_id) {
      return httpUtils.baseUrl + "/purchase/" + purchase_id + "/pdf";
    }
  },
  mounted() {
    var component_id = this.$route.params.component_id;
    this.getComponent(component_id);
  }
};
</script>

<style>
</style>
