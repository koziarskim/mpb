<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Orders</span>
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
      <template slot="action" slot-scope="row">
        <b-button size="sm" @click.stop="deletePurchase(row.item.id)" :disabled="disabled(row.item)">x</b-button>&nbsp;
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" :disabled="!disabled(row.item)">Receivings</b-button>
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
        { key: "completed", label: "Completed", sortable: true },
        { key: "pdf", label: "PDF", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      availableComponents: [
        { id: 3, number: "1003" },
        { id: 4, number: "1004" },
        { id: 5, number: "1005" },
        { id: 33, number: "10033" }
      ],
      component: {},
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
  watch: {
      component(){
          this.getPurchasesData();
      }
  },
  methods: {
    disabled(purchase) {
      return purchase.completed;
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getAvailableComponents(component_id) {
      http
        .get("/component/")
        .then(response => {
          this.availableComponents = response.data;
          if (component_id) {
            this.component = response.data.filter(
              it => it.id == component_id
            )[0];
          }
          this.getPurchasesData();
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
          this.getPurchasesData();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getPurchasesData() {
      //TODO: apply filters to the GET url.
      var component_id = this.component.id?this.component.id:"";
      http
        .get("/purchase?component_id=" + component_id)
        .then(response => {
          this.purchases = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deletePurchase(id) {
      http
        .delete("/purchase/" + id)
        .then(response => {
          this.getPurchasesData();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
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
    goToReceiving(purchase_id) {
      var query = { purchase_id: purchase_id };
      if (this.component.id) {
        query.component_id = this.component.id;
      }
      router.push({ path: "/receivingList", query: query });
    },
    rowPdfUrl: function(purchase_id) {
      return httpUtils.baseUrl + "/purchase/" + purchase_id + "/pdf";
    }
  },
  mounted() {
    this.getAvailableComponents(parseInt(this.$route.query.component_id));
    window.history.pushState({}, document.title, window.location.pathname);
  }
};
</script>

<style>
</style>
