<template>
  <b-container fluid>
    <!-- Supplier -->
    <label class="top-label">Suppliers: <a href="#" @click="clearSuppliers()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSuppliers(true)" @click="showSupplierMenu()" class="form-control search-width" type="tel" v-model="supplierSearchKey" placeholder="Pick Supplier">
      <b-button v-if="visibleSupplierMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeSupplierMenu()">Close</b-button>
    </div>
    <div v-if="visibleSupplierMenu" class="menu-tab">
        <div v-for="supplier in suppliers" v-bind:key="supplier.id">
          <input type="checkbox" v-model="supplier.selected">
          <span>{{supplier.name}}</span>
      </div>
    </div>
    <div v-for="supplier in selectedSuppliers" v-bind:key="supplier.id">
      {{supplier.name}}
    </div>
    <br/>
    <!-- Item -->
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getItems(true)" @click="showItemMenu()" class="form-control search-width" type="tel" v-model="itemSearchKey" placeholder="Pick Item">
      <b-button v-if="visibleItemMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeItemMenu()">Close</b-button>
    </div>
    <div v-if="visibleItemMenu" class="menu-tab">
        <div v-for="item in items" v-bind:key="item.id">
          <input type="checkbox" v-model="item.selected">
          <span>{{item.name}}</span>
      </div>
    </div>
    <div v-for="item in selectedItems" v-bind:key="item.id">
      {{item.name}}
    </div>
    <br/>
    <!-- Sales -->
    <label class="top-label">Sales: <a href="#" @click="clearSales()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSales(true)" @click="showSaleMenu()" class="form-control search-width" type="tel" v-model="saleSearchKey" placeholder="Pick Sale">
      <b-button v-if="visibleSaleMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeSaleMenu()">Close</b-button>
    </div>
    <div v-if="visibleSaleMenu" class="menu-tab">
        <div v-for="sale in sales" v-bind:key="sale.id">
          <input type="checkbox" v-model="sale.selected">
          <span>{{sale.name}}</span>
      </div>
    </div>
    <div v-for="sale in selectedSales" v-bind:key="sale.id">
      {{sale.name}}
    </div>
    <br/>
    <div style="text-align: right;">
      <b-button size="sm" type="reset" variant="success" @click="updateParent()">Search</b-button>
    </div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ComponentSearch",
  props: {
    ss: Number,
  },
  data() {
    return {
      supplierSearchKey: "",
      visibleSupplierMenu: false,
      suppliers: [],
      selectedSuppliers: [],

      itemSearchKey: "",
      visibleItemMenu: false,
      items: [],
      selectedItems: [],

      saleSearchKey: "",
      visibleSaleMenu: false,
      sales: [],
      selectedSales: [],

    };
  },
  computed: {
  },
  watch: {
    supplier(new_value, old_value){
      // this.items = [];
    }
  },
  methods: {
    getComponents(){
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },

    // Supplier
    showSupplierMenu(){
      this.getSuppliers().then(r => {
        this.visibleSupplierMenu = true;
      });
    },
    closeSupplierMenu(){
      this.visibleSupplierMenu = false;
      this.selectedSuppliers = this.suppliers.filter(it => it.selected == true);
    },
    clearSuppliers(){
      this.suppliers = [];
      this.selectedSuppliers = [];
      this.closeSupplierMenu();
    },
    getSuppliers(fresh){
      if(this.suppliers.length == 0 || fresh){
        return http.get("/search/supplier/kv", { params: {supplierName: this.supplierSearchKey}}).then(r => {
          r.data.forEach(supplier => {
            var found = this.selectedSuppliers.find(it => it.id==supplier.id && it.selected);
            supplier.selected = found?true:false;
          })
          this.suppliers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },

    // Item
    showItemMenu(){
      this.getItems().then(r => {
        this.visibleItemMenu = true;
      });
    },
    closeItemMenu(){
      this.visibleItemMenu = false;
      this.selectedItems = this.items.filter(it => it.selected == true);
    },
    clearItems(){
      this.items = [];
      this.selectedItems = [];
      this.closeItemMenu();
    },
    getItems(fresh){
      if(this.items.length == 0 || fresh){
        return http.get("/search/item/kv", { params: {itemName: this.itemSearchKey, supplierId: this.supplierId}}).then(r => {
          r.data.forEach(item => {
            var found = this.selectedItems.find(it => it.id==item.id && it.selected);
            item.selected = found?true:false;
          })
          this.items = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },

    // Sale
    showSaleMenu(){
      this.getSales().then(r => {
        this.visibleSaleMenu = true;
      });
    },
    closeSaleMenu(){
      this.visibleSaleMenu = false;
      this.selectedSales = this.sales.filter(it => it.selected == true);
    },
    clearSales(){
      this.sales = [];
      this.selectedSales = [];
      this.closeSaleMenu();
    },
    getSales(fresh){
      if(this.sales.length == 0 || fresh){
        return http.get("/search/sale/kv", { params: {itemName: this.saleSearchKey}}).then(r => {
          r.data.forEach(sale => {
            var found = this.selectedSales.find(it => it.id==sale.id && it.selected);
            sale.selected = found?true:false;
          })
          this.sales = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },

    updateParent(){
      this.$emit("componentsUpdated", [{id: 1, name: 'test1'}]);
    }
  },
  mounted() {
  }
};
</script>

<style>
.menu-tab {
  z-index: 100; 
  position: sticky; 
  width: 400px;
  background-color:white; 
  border: 1px solid gray;
}
.btn-tab {
  color: black !important; 
  background-color: white !important; 
  margin-bottom: -1px !important; 
  border-bottom: 0px !important; 
  margin-left: 156px; 
  border-color: gray !important;
  z-index: 110 !important;
}
.search-width {
  width: 190px !important;
}
</style>
