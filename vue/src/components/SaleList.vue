<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Sale Orders</span>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="searchKey" @keyup.enter="getSales()" placeholder="Search by Number or Customer"/>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSale('')">New S.O.</b-button>
                </div>
            </b-col>
        </b-row>
        <div v-if="sales.length==0">Not found any sale orders...</div>
        <b-table v-if="sales.length>0"
                :items="sales"
                :fields="fields"
				no-local-sorting @sort-changed="sorted">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" @click.stop=goToSale(row.item.id) variant="link">{{row.item.number}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteSale(row.item.id)">x</b-button>
                </template>
        </b-table>
		<b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
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
      pageable: {totalElements: 100, currentPage: 1, perPage: 15, sortBy: 'number', sortDesc: false},
      searchKey: "",
      fields: [
        { key: "number", label: "S.O. #", sortable: true },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "date", label: "Date", sortable: true },
        { key: "dc", label: "Distribution", sortable: false },
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
	sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getSales();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSales();
    },
	getSales() {
      http
        .get("/sale/pageable", {params: {pageable: this.pageable, searchKey: this.searchKey}})
        .then(response => {
          //SaleListDto
          this.sales = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
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
          this.getSales();
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
     this.getSales();
  }
};
</script>

<style>
</style>
