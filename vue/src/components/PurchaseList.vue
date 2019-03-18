<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2 style="text-align: left;">Purchase Orders</h2>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToPurchase('')">New P.O.</b-button>
            </div>
        </div>
        <div v-if="purchases.length==0">Not found any purchase orders...</div>
        <b-table v-if="purchases.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="purchases"
                :fields="fields">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" @click.stop=goToPurchase(row.item.id) variant="link">{{row.item.number}}</b-button>
                </template>
                <template slot="completed" slot-scope="row">
                    <span>{{row.item.completed?"Yes":"No"}}</span>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deletePurchase(row.item.id)">x</b-button>
                </template>
                <template slot="pdf" slot-scope="row">
                    <a :href="rowPdfUrl(row.item.id)" target="_blank"><img src="../assets/pdf-download.png" width="20px"/></a>
                </template>
                <template slot="expectedDate" slot-scope="row">
                    <span>{{row.item.expectedDate | formatDate}}</span>
                </template>
                <template slot="date" slot-scope="row">
                    <span>{{row.item.date | formatDate}}</span>
                </template>
        </b-table>
        <b-alert :show="alertSecs" dismissible variant="warning" @dismiss-count-down="(secs) => { alertSecs = secs }">
                {{alertMessage}}
        </b-alert>
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
      sortBy: 'id',
      sortDesc: false,
      fields: [
        { key: "number", label: "P.O. #", sortable: true },
        { key: "supplier.name", label: "Supplier", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "expectedDate", label: "Expected", sortable: true },
        { key: "completed", label: "Completed", sortable: true },
        { key: "pdf", label: "PDF", sortable: true },
        { key: "action", label: "Action", sortable: false}
      ],
      purchases: []
    };
  },
  methods: {
    showAlert (message) {
      this.alertSecs = 3,
      this.alertMessage = message
    },
    getPurchasesData() {
      http
        .get("/purchase")
        .then(response => {
          this.purchases = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    getItem(component_id){
        var component;
        var found = this.purchases.some(function(element) {
           if(element.id === component_id){
                component = element;
           }
        });
        return component;
    },
    deletePurchase(id) {
      http
        .delete("/purchase/"+id)
        .then(response => {
          this.getPurchasesData();
        })
        .catch(e => {
            console.log("API Error: "+e);
        });
    },
    goToPurchase(id){
        if(!id){
            http
            .post("/purchase")
            .then(response =>{
                router.push('/purchaseEdit/'+response.data.id);
            })
            .catch(e =>{
                console.log("API Error: "+e);
            })
        }else{
            router.push('/purchaseEdit/'+id);
        }
    },
    rowPdfUrl: function(purchase_id) {
      return httpUtils.baseUrl + "/purchase/" + purchase_id + "/pdf";
    }
  },
  mounted() {
     this.getPurchasesData();
  }
};
</script>

<style>
</style>
