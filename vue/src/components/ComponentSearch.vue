<template>
  <b-container fluid>
    <!-- Season -->
    <label class="top-label">Seasons: <a href="#" @click="clearSeasons()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSeasons(true)" @click="showSeasonMenu()" class="form-control search-width" type="tel" v-model="searchDto.seasonName" placeholder="Pick Season">
      <b-button v-if="visibleSeasonMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeSeasonMenu()">Close</b-button>
    </div>
    <div v-if="visibleSeasonMenu" class="menu-tab">
        <div v-for="season in seasons" :key="season.id">
          <input type="checkbox" v-model="season.selected">
          <span>{{season.name}}</span>
      </div>
    </div>
    <div v-for="season in selectedSeasons" :key="season.id">
      {{season.name}}
    </div>
    <br/>

    <!-- Customer -->
    <label class="top-label">Customers: <a href="#" @click="clearCustomers()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getCustomers(true)" @click="showCustomerMenu()" class="form-control search-width" type="tel" v-model="searchDto.customerName" placeholder="Pick Customer">
      <b-button v-if="visibleCustomerMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeCustomerMenu()">Close</b-button>
    </div>
    <div v-if="visibleCustomerMenu" class="menu-tab">
        <div v-for="customer in customers" :key="customer.id">
          <input type="checkbox" v-model="customer.selected">
          <span>{{customer.name}}</span>
      </div>
    </div>
    <div v-for="customer in selectedCustomers" :key="customer.id">
      {{customer.name}}
    </div>
    <br/>

    <!-- Item -->
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getItems(true)" @click="showItemMenu()" class="form-control search-width" type="tel" v-model="searchDto.itemName" placeholder="Pick Item">
      <b-button v-if="visibleItemMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeItemMenu()">Close</b-button>
    </div>
    <div v-if="visibleItemMenu" class="menu-tab">
        <div v-for="item in items" :key="item.id">
          <input type="checkbox" v-model="item.selected">
          <span>{{item.name}}</span>
      </div>
    </div>
    <div v-for="item in selectedItems" :key="item.id">
      {{item.name}}
    </div>
    <br/>

    <!-- Sale -->
    <label class="top-label">Sales: <a href="#" @click="clearSales()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSales(true)" @click="showSaleMenu()" class="form-control search-width" type="tel" v-model="searchDto.saleNumber" placeholder="Pick Sale">
      <b-button v-if="visibleSaleMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeSaleMenu()">Close</b-button>
    </div>
    <div v-if="visibleSaleMenu" class="menu-tab">
        <div v-for="sale in sales" :key="sale.id">
          <input type="checkbox" v-model="sale.selected">
          <span>{{sale.name}}</span>
      </div>
    </div>
    <div v-for="sale in selectedSales" :key="sale.id">
      {{sale.name}}
    </div>
    <br/>

    <!-- Supplier -->
    <label class="top-label">Suppliers: <a href="#" @click="clearSuppliers()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSuppliers(true)" @click="showSupplierMenu()" class="form-control search-width" type="tel" v-model="searchDto.supplierName" placeholder="Pick Supplier">
      <b-button v-if="visibleSupplierMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeSupplierMenu()">Close</b-button>
    </div>
    <div v-if="visibleSupplierMenu" class="menu-tab">
        <div v-for="supplier in suppliers" :key="supplier.id">
          <input type="checkbox" v-model="supplier.selected">
          <span>{{supplier.name}}</span>
      </div>
    </div>
    <div v-for="supplier in selectedSuppliers" :key="supplier.id">
      {{supplier.name}}
    </div>
    <br/>

    <!-- Component -->
    <label class="top-label">Components: <a href="#" @click="clearComponents()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getComponents(true)" @click="showComponentMenu()" class="form-control search-width" type="tel" v-model="searchDto.componentName" placeholder="Pick Component">
      <b-button v-if="visibleComponentMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeComponentMenu()">Close</b-button>
    </div>
    <div v-if="visibleComponentMenu" class="menu-tab">
        <div v-for="component in components" :key="component.id">
          <input type="checkbox" v-model="component.selected">
          <span>{{component.name}}</span>
      </div>
    </div>
    <div v-for="component in selectedComponents" :key="component.id">
      {{component.name}}
    </div>
    <br/>

    <div style="text-align: right;">
      <b-button size="sm" type="reset" variant="success" @click="updateParent()">Apply</b-button>
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
      searchDto: {
        seasons: [],
        seasonName: "",
        customers: [],
        customerName: "",
        items: [],
        itemName: "",
        sales: [],
        saleNumbe: "",
        components: [],
        componentName: ""
      },

      visibleSeasonMenu: false,
      seasons: [],
      selectedSeasons: [],

      visibleCustomerMenu: false,
      customers: [],
      selectedCustomers: [],

      visibleItemMenu: false,
      items: [],
      selectedItems: [],

      visibleSaleMenu: false,
      sales: [],
      selectedSales: [],

      visibleSupplierMenu: false,
      suppliers: [],
      selectedSuppliers: [],

      visibleComponentMenu: false,
      components: [],
      selectedComponents: [],

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
    // Season
    showSeasonMenu(){
      this.getSeasons().then(r => {
        this.visibleSeasonMenu = true;
      });
    },
    closeSeasonMenu(){
      this.visibleSeasonMenu = false;
      this.selectedSeasons = this.seasons.filter(it => it.selected == true);
      this.searchDto.seasons = this.selectedSeasons.map(it => it.id);
    },
    clearSeasons(){
      this.searchDto.seasonName = "";
      this.searchDto.seasons = [];
      this.seasons = [];
      this.selectedSeasons = [];
      this.closeSeasonMenu();
      this.clearCustomers();
    },
    getSeasons(fresh){
      if(this.seasons.length == 0 || fresh){
        return http.post("/search/season/kv", this.searchDto).then(r => {
          r.data.forEach(season => {
            var found = this.selectedSeasons.find(it => it.id==season.id && it.selected);
            season.selected = found?true:false;
          })
          this.seasons = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },

    // Customer
    showCustomerMenu(){
      this.getCustomers().then(r => {
        this.visibleCustomerMenu = true;
      });
    },
    closeCustomerMenu(){
      this.visibleCustomerMenu = false;
      this.selectedCustomers = this.customers.filter(it => it.selected == true);
      this.searchDto.customers = this.selectedCustomers.map(it => it.id);
    },
    clearCustomers(){
      this.searchDto.customerName = "";
      this.searchDto.customers = [];
      this.customers = [];
      this.selectedCustomers = [];
      this.closeCustomerMenu();
      this.clearItems();
    },
    getCustomers(fresh){
      if(this.customers.length == 0 || fresh){
        return http.post("/search/customer/kv", this.searchDto).then(r => {
          r.data.forEach(customer => {
            var found = this.selectedCustomers.find(it => it.id==customer.id && it.selected);
            customer.selected = found?true:false;
          })
          this.customers = r.data;
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
      this.searchDto.items = this.selectedItems.map(it => it.id);
    },
    clearItems(){
      this.searchDto.itemName = "";
      this.searchDto.items = [];
      this.items = [];
      this.selectedItems = [];
      this.closeItemMenu();
      this.clearSales();
    },
    getItems(fresh){
      if(this.items.length == 0 || fresh){
        return http.post("/search/item/kv", this.searchDto).then(r => {
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
      this.searchDto.sales = this.selectedSales.map(it => it.id);
    },
    clearSales(){
      this.searchDto.saleName = "";
      this.searchDto.sales = [];
      this.sales = [];
      this.selectedSales = [];
      this.closeSaleMenu();
      this.clearSuppliers();
    },
    getSales(fresh){
      if(this.sales.length == 0 || fresh){
        return http.post("/search/sale/kv", this.searchDto).then(r => {
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

    // Supplier
    showSupplierMenu(){
      this.getSuppliers().then(r => {
        this.visibleSupplierMenu = true;
      });
    },
    closeSupplierMenu(){
      this.visibleSupplierMenu = false;
      this.selectedSuppliers = this.suppliers.filter(it => it.selected == true);
      this.searchDto.suppliers = this.selectedSuppliers.map(it => it.id);
    },
    clearSuppliers(){
      this.searchDto.supplierName = "";
      this.searchDto.suppliers = [];
      this.suppliers = [];
      this.selectedSuppliers = [];
      this.closeSupplierMenu();
      this.clearComponents();
    },
    getSuppliers(fresh){
      if(this.suppliers.length == 0 || fresh){
        return http.post("/search/supplier/kv", this.searchDto).then(r => {
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

    // Component
    showComponentMenu(){
      this.getComponents().then(r => {
        this.visibleComponentMenu = true;
      });
    },
    closeComponentMenu(){
      this.visibleComponentMenu = false;
      this.selectedComponents = this.components.filter(it => it.selected == true);
      this.searchDto.components = this.selectedComponents.map(it => it.id);
    },
    clearComponents(){
      this.searchDto.componentName = "";
      this.searchDto.components = [];
      this.components = [];
      this.selectedComponents = [];
      this.closeComponentMenu();
    },
    getComponents(fresh){
      if(this.components.length == 0 || fresh){
        return http.post("/search/component/kv", this.searchDto).then(r => {
          r.data.forEach(component => {
            var found = this.selectedComponents.find(it => it.id==component.id && it.selected);
            component.selected = found?true:false;
          })
          this.components = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },

    updateParent(){
      this.$emit("componentsUpdated", this.selectedComponents);
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
  height: 250px;
  overflow: auto;
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
