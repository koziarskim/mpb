<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Sales by Item</span>
                <b-form-checkbox size="sm" v-model="itemView">Item View</b-form-checkbox>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="numberName" @keyup.enter="getSaleItems()" placeholder="Search Sale Number or Name"/>
            </b-col>
            <b-col cols="2">
              <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Search Customer"></b-select>
            </b-col>
            <b-col cols="2">
              <b-select option-value="id" option-text="name" :list="availableItems" v-model="item" placeholder="Search Item"></b-select>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSale('')">New Shipment ({{selectedSaleItems.length}})</b-button>
                </div>
            </b-col>
        </b-row>
        <b-table :items="saleItems" :fields="fields" no-local-sorting @sort-changed="sorted">
          <template v-slot:cell(number)="row">
              <b-button size="sm" @click=goToSale(row.item.saleId) variant="link">{{row.item.saleNumber}} ({{row.item.saleName}})</b-button>
          </template>
          <template v-slot:cell(itemNumber)="row">
              <b-button size="sm" @click=goToItem(row.item.itemId) variant="link">{{row.item.itemNumber}} ({{row.item.itemName}})</b-button>
          </template>
          <!-- <template v-slot:cell(action)="row">
              <b-button size="sm" @click="deleteSale(row.item.id)">x</b-button>
          </template> -->
        </b-table>
		<b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite";
import navigation from "../utils/navigation";

export default {
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'id', sortDesc: false},
      searchSale: "",
      searchItem: "",
      itemView: true,
      selectedSaleItems: [],
      saleItems: [],
      numberName: "",
      availableItems: [],
      item: {},
      availableCustomers: [],
      customer: {},
      fields: [
        { key: "number", label: "Sale # (Name)", sortable: false },
        { key: "itemNumber", label: "Item", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "unitsSold", label: "Sold", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
        { key: "action", label: "Action", sortable: false}
      ],
      sales: []
    };
  },
  watch: {
    itemView(newValue, oldValue){
      if(newValue==false){
        navigation.goTo("/saleList/")
      }
    },
    item(newValue, oldValue){
      this.getSaleItems();
    },
    customer(newValue, oldValue){
      this.getSaleItems();
    }
  },
  methods: {
	sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getSaleItems();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSaleItems();
    },
  getSaleItems(){
    http.get("/saleItem/pageable", {params: {pageable: this.pageable, numberName: this.numberName, customerId: this.customer.id, itemId: this.item.id}}).then(r => {
      this.saleItems = r.data.content;
      this.pageable.totalElements = r.data.totalElements;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  getAvailableCustomers() {
    http.get("/customer/kv").then(r => {
      this.availableCustomers = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  getAvailableItems() {
    http.get("/item/kv").then(r => {
      this.availableItems = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
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
    gotToItem(itemId){
      router.push("/itemEdit/"+itemId);
    },
    goToSale(saleId){
        if(!saleId){
            http
            .post("/sale")
            .then(response =>{
                router.push('/saleEdit/'+response.data.id);
            })
            .catch(e =>{
                console.log("API Error: "+e);
            })
        }else{
            router.push('/saleEdit/'+saleId);
        }
    },
  },
  mounted() {
     this.getSaleItems();
     this.getAvailableCustomers();
     this.getAvailableItems();
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
