<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Sale Orders</span>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="searchSale" @click="searchItem = ''" @keyup.enter="getSales('sale')" placeholder="Search Number, Name or Customer"/>
            </b-col>
            <b-col cols="2">
                <input class="form-control" type="tel" v-model="searchItem" @click="searchSale = ''" @keyup.enter="getSales('item')" placeholder="Search Item"/>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSale('')">New S.O.</b-button>
                </div>
            </b-col>
        </b-row>
        <b-table :items="sales" :fields="fields" no-local-sorting @sort-changed="sorted">
          <template v-slot:cell(number)="row">
              <b-button size="sm" @click.stop=goToSale(row.item.id) variant="link">{{row.item.number}} ({{row.item.name}})</b-button>
          </template>
          <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="deleteSale(row.item.id)">x</b-button>
          </template>
        </b-table>
		<b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"

export default {
  data() {
    return {
      securite: securite,
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'number', sortDesc: false},
      searchSale: "",
      searchItem: "",
      fields: [
        { key: "number", label: "Sale # (Name)", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "dc", label: "DC", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "unitsSold", label: "Sold", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "action", label: "Action", sortable: false}
      ],
      sales: []
    };
  },
  methods: {
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
	getSales(type) {
    var searchKey = type=="sale"?this.searchSale:this.searchItem;
      http
        .get("/sale/pageable", {params: {pageable: this.pageable, searchKey: searchKey, searchType: type}})
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
      if(!securite.hasRole(["ADMIN"])){
        alert("Don't have permission to delete sale");
        return;
      }
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete this Sale? '+
      'This will also delete all Schedules and Productions associated with this Sale').then(ok => {
        if(ok){
          http.delete("/sale/"+id).then(response => {
            this.getSales();
          }).catch(e => {
            console.log("API Error: "+e);
          });
            }
        })
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
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
