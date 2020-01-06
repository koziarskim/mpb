<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Sales by Item</span>
                <b-form-checkbox size="sm" v-model="itemView">Item View</b-form-checkbox>
            </b-col>
            <b-col cols="2">
                <input class="form-control" type="tel" v-model="numberName" @keyup.enter="getSaleItems()" placeholder="Search Sale #, Name"/>
            </b-col>
            <b-col cols="2">
              <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Search Customer"></b-select>
            </b-col>
            <b-col cols="2">
              <b-select option-value="id" option-text="name" :list="availableItems" v-model="item" placeholder="Search Item"></b-select>
            </b-col>
            <b-col cols="2">
              <div style="display:flex"><input style="margin-right: 7px; margin-top: 3px;" type="checkbox" v-model="hideShip"/><label class="top-label">Hide Shpped</label></div>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="newShipment()">New Shipment ({{selectedSaleItemIds.length}})</b-button>
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
          <template v-slot:cell(unitsShipped)="row">
              <b-button size="sm" @click=goToShipment(row.item.itemId,row.item.saleId) variant="link">{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <input type="checkbox" v-model="selectedSaleItemIds" :value="row.item.id" @change="checkboxSelected(row.item)" :disabled="checkboxDisabled(row.item)">
          </template>
        </b-table>
        <div style="display: flex">
		      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
          <span style="margin-top: 5px">Total of {{pageable.totalElements}} Items Sold</span>
        </div>
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
      hideShip: true,
      searchSale: "",
      searchItem: "",
      itemView: true,
      selectedSaleItemIds: [],
      saleItems: [],
      numberName: "",
      availableItems: [],
      item: {},
      availableCustomers: [],
      customer: {},
      selectedCustomerId: null,
      fields: [
        { key: "number", label: "Sale # (Name)", sortable: false },
        { key: "itemNumber", label: "Item", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "dc", label: "DC (State)", sortable: false },
        { key: "unitsSold", label: "Sold", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "unitsShipped", label: "Shipped", sortable: false },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
        { key: "action", label: "Action", sortable: false}
      ],
      sales: []
    };
  },
  watch: {
    hideShip(newValue, oldValue){
      this.getSaleItems();
    },
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
    checkboxSelected(saleItem){
      if(!this.selectedCustomerId){
        this.selectedCustomerId = saleItem.customerId;
      }
      if(this.selectedSaleItemIds.length==0){
        this.selectedCustomerId = null;
      }
    },
    checkboxDisabled(saleItem){
      return this.selectedCustomerId && !this.selectedSaleItemIds.includes(saleItem.id) && this.selectedCustomerId != saleItem.customerId;
    },
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
    http.get("/saleItem/pageable", {params: {pageable: this.pageable, numberName: this.numberName, customerId: this.customer.id, itemId: this.item.id, hideShip: this.hideShip}}).then(r => {
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
  newShipment(){
    var query = { saleItemIds: this.selectedSaleItemIds.join(",") };
    router.push({ path: "/shipmentEdit/new", query: query })
  },
  goToShipment(itemId, saleId){
    var query = { itemId: itemId, saleId: saleId };
    router.push({path: "/shipmentList", query: query})
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
