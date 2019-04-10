<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <span style="text-align: left; font-size: 18px; font-weight: bold">Sale Orders</span>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSale('')">New S.O.</b-button>
            </div>
        </div>
        <div v-if="sales.length==0">Not found any sale orders...</div>
        <b-table v-if="sales.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="sales"
                :fields="fields">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" @click.stop=goToSale(row.item.id) variant="link">{{row.item.number}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteSale(row.item.id)">x</b-button>
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

export default {
  data() {
    return {
      alertSecs: 0,
      alertMessage: "",
      sortBy: 'id',
      sortDesc: false,
      fields: [
        { key: "number", label: "S.O. #", sortable: true },
        { key: "customer.name", label: "Customer", sortable: true },
        { key: "date", label: "Date", sortable: true },
        { key: "shippingAddress.dc", label: "Distribution", sortable: true },
        { key: "action", label: "Action", sortable: false}
      ],
      sales: []
    };
  },
  methods: {
    showAlert (message) {
      this.alertSecs = 3,
      this.alertMessage = message
    },
    getSalesData() {
      http
        .get("/sale")
        .then(response => {
          this.sales = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    getItem(component_id){
        var component;
        var found = this.sales.some(function(element) {
           if(element.id === component_id){
                component = element;
           }
        });
        return component;
    },
    deleteSale(id) {
      http
        .delete("/sale/"+id)
        .then(response => {
          this.getSalesData();
        })
        .catch(e => {
            console.log("API Error: "+e);
        });
    },
    goToSale(id){
        if(!id){
            http
            .post("/sale")
            .then(response =>{
                router.push('/saleEdit/'+response.data.id);
            })
            .catch(e =>{
                console.log("API Error: "+e);
            })
        }else{
            router.push('/saleEdit/'+id);
        }
    },
  },
  mounted() {
     this.getSalesData();
  }
};
</script>

<style>
</style>
