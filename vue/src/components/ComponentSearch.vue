<template>
  <b-container fluid style="font-size: 10px;">
    <!-- Season -->
    <label class="top-label">Seasons: <a href="#" @click="clearSeasons()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSeasons()" @click="showSeasonMenu()" class="form-control search-width" type="tel" v-model="searchDto.seasonName" placeholder="Pick Season">
      <div v-if="visibleSeasonMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllSeason"><span>All</span></div>
      <div v-if="visibleSeasonMenu" class="btn-tab" @click="closeSeasonMenu()">Close</div>
    </div>
    <div v-if="visibleSeasonMenu" class="menu-tab">
        <div v-for="season in seasons" :key="season.id">
          <input type="checkbox" v-model="season.selected">
          <span>{{season.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="season in selectedSeasons" :key="season.id+season.name">{{season.name}}</div>
    </div>
    <br/>

    <!-- Item -->
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getItems()" @click="showItemMenu()" class="form-control search-width" type="tel" v-model="searchDto.itemName" placeholder="Pick Item">
      <div v-if="visibleItemMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllItem"><span>All</span></div>
      <div v-if="visibleItemMenu" class="btn-tab" @click="closeItemMenu()">Close</div>
    </div>
    <div v-if="visibleItemMenu" class="menu-tab">
        <div v-for="item in items" :key="item.id">
          <input type="checkbox" v-model="item.selected">
          <span>{{item.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="item in selectedItems" :key="item.id+item.name">{{item.name}}</div>
    </div>
    <br/>

    <!-- Customer -->
    <label class="top-label">Customers: <a href="#" @click="clearCustomers()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getCustomers()" @click="showCustomerMenu()" class="form-control search-width" type="tel" v-model="searchDto.customerName" placeholder="Pick Customer">
      <div v-if="visibleCustomerMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllCustomer"><span>All</span></div>
      <div v-if="visibleCustomerMenu" class="btn-tab" @click="closeCustomerMenu()">Close</div>
    </div>
    <div v-if="visibleCustomerMenu" class="menu-tab">
        <div v-for="customer in customers" :key="customer.id">
          <input type="checkbox" v-model="customer.selected">
          <span>{{customer.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="customer in selectedCustomers" :key="customer.id+customer.name">{{customer.name}}</div>
    </div>
    <br/>

    <!-- Sale -->
    <label class="top-label">Sales: <a href="#" @click="clearSales()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSales()" @click="showSaleMenu()" class="form-control search-width" type="tel" v-model="searchDto.saleNumber" placeholder="Pick Sale">
      <div v-if="visibleSaleMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllSale"><span>All</span></div>
      <div v-if="visibleSaleMenu" class="btn-tab" @click="closeSaleMenu()">Close</div>
    </div>
    <div v-if="visibleSaleMenu" class="menu-tab">
        <div v-for="sale in sales" :key="sale.id">
          <input type="checkbox" v-model="sale.selected">
          <span>{{sale.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="sale in selectedSales" :key="sale.id+sale.name">{{sale.name}}</div>
    </div>
    <br/>

    <!-- Supplier -->
    <label class="top-label">Suppliers: <a href="#" @click="clearSuppliers()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getSuppliers()" @click="showSupplierMenu()" class="form-control search-width" type="tel" v-model="searchDto.supplierName" placeholder="Pick Supplier">
      <div v-if="visibleSupplierMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllSupplier"><span>All</span></div>
      <div v-if="visibleSupplierMenu" class="btn-tab" @click="closeSupplierMenu()">Close</div>
    </div>
    <div v-if="visibleSupplierMenu" class="menu-tab">
        <div v-for="supplier in suppliers" :key="supplier.id">
          <input type="checkbox" v-model="supplier.selected">
          <span>{{supplier.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="supplier in selectedSuppliers" :key="supplier.id+supplier.name">{{supplier.name}}</div>
    </div>
    <br/>

    <!-- Component -->
    <label class="top-label">Components: <a href="#" @click="clearComponents()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getComponents()" @click="showComponentMenu()" class="form-control search-width" type="tel" v-model="searchDto.componentName" placeholder="Pick Component">
      <div v-if="visibleComponentMenu" class="chk-all-tab"><input type="checkbox" v-model="checkAllComponent"><span>All</span></div>
      <div v-if="visibleComponentMenu" class="btn-tab" @click="closeComponentMenu()">Close</div>
    </div>
    <div v-if="visibleComponentMenu" class="menu-tab">
        <div v-for="component in components" :key="component.id">
          <input type="checkbox" v-model="component.selected">
          <span>{{component.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="component in selectedComponents" :key="component.id+component.name">{{component.name}}</div>
    </div>
    <br/>

    <div style="text-align: right;">
      <b-button size="sm" type="reset" variant="success" @click="updateParent()">Add >></b-button>
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
      showSelected: true,
      searchDto: {
        seasons: [],
        seasonName: "",
        items: [],
        itemName: "",
        customers: [],
        customerName: "",
        sales: [],
        saleNumbe: "",
        components: [],
        componentName: ""
      },

      checkAllSeason: false,
      visibleSeasonMenu: false,
      seasons: [],
      selectedSeasons: [],

      checkAllItem: false,
      visibleItemMenu: false,
      items: [],
      selectedItems: [],

      checkAllCustomer: false,
      visibleCustomerMenu: false,
      customers: [],
      selectedCustomers: [],

      checkAllSale: false,
      visibleSaleMenu: false,
      sales: [],
      selectedSales: [],

      checkAllSupplier: false,
      visibleSupplierMenu: false,
      suppliers: [],
      selectedSuppliers: [],

      checkAllComponent: false,
      visibleComponentMenu: false,
      components: [],
      selectedComponents: [],

    };
  },
  computed: {
  },
  watch: {
    checkAllSeason(new_value, old_value){
      this.seasons.forEach(it => {it.selected = new_value;})
      this.selectedSeasons = [];
    },
    checkAllItem(new_value, old_value){
      this.items.forEach(it => {it.selected = new_value;})
      this.selectedItems = [];
    },
    checkAllCustomer(new_value, old_value){
      this.customers.forEach(it => {it.selected = new_value;})
      this.selectedCustomers = [];
    },
    checkAllSale(new_value, old_value){
      this.sales.forEach(it => {it.selected = new_value;})
      this.selectedSeasons = [];
    },
    checkAllSupplier(new_value, old_value){
      this.suppliers.forEach(it => {it.selected = new_value;})
      this.selectedSuppliers = [];
    },
    checkAllComponent(new_value, old_value){
      this.components.forEach(it => {it.selected = new_value;})
      this.selectedComponents = [];
    }
  },
  methods: {
    // Season
    showSeasonMenu(){
      this.updateSelected(this.seasons, this.selectedSeasons);
      if(this.visibleSeasonMenu){
        return;
      }
      this.getSeasons().then(r => {
        this.visibleSeasonMenu = true;
      });
    },
    closeSeasonMenu(){
      this.updateSelected(this.seasons, this.selectedSeasons);
      this.visibleSeasonMenu = false;
      this.searchDto.seasons = this.selectedSeasons.map(it => it.id);
    },
    clearSeasons(){
      this.checkAllSeason = false;
      this.searchDto.seasonName = "";
      this.searchDto.seasons = [];
      this.seasons = [];
      this.selectedSeasons = [];
      this.closeSeasonMenu();
      this.clearItems();
    },
    getSeasons(){
        return http.post("/search/season/kv", this.searchDto).then(r => {
          r.data.forEach(season => {
            var found = this.selectedSeasons.find(it => it.id==season.id && it.selected);
            season.selected = found?true:false;
          })
          this.seasons = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Item
    showItemMenu(){
      this.updateSelected(this.items, this.selectedItems);
      if(this.visibleItemMenu){
        return;
      }
      this.getItems().then(r => {
        this.visibleItemMenu = true;
      });
    },
    closeItemMenu(){
      this.updateSelected(this.items, this.selectedItems);
      this.visibleItemMenu = false;
      this.searchDto.items = this.selectedItems.map(it => it.id);
    },
    clearItems(){
      this.searchDto.itemName = "";
      this.searchDto.items = [];
      this.items = [];
      this.selectedItems = [];
      this.closeItemMenu();
      this.clearCustomers();
    },
    getItems(){
        return http.post("/search/item/kv", this.searchDto).then(r => {
          r.data.forEach(item => {
            var found = this.selectedItems.find(it => it.id==item.id && it.selected);
            item.selected = found?true:false;
          })
          this.items = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Customer
    showCustomerMenu(){
      this.updateSelected(this.customers, this.selectedCustomers);
      if(this.visibleCustomerMenu){
        return;
      }
      this.getCustomers().then(r => {
        this.visibleCustomerMenu = true;
      });
    },
    closeCustomerMenu(){
      this.updateSelected(this.customers, this.selectedCustomers);
      this.visibleCustomerMenu = false;
      this.searchDto.customers = this.selectedCustomers.map(it => it.id);
    },
    clearCustomers(){
      this.checkAllCustomer = false;
      this.searchDto.customerName = "";
      this.searchDto.customers = [];
      this.customers = [];
      this.selectedCustomers = [];
      this.closeCustomerMenu();
      this.clearSales();
    },
    getCustomers(){
        return http.post("/search/customer/kv", this.searchDto).then(r => {
          r.data.forEach(customer => {
            var found = this.selectedCustomers.find(it => it.id==customer.id && it.selected);
            customer.selected = found?true:false;
          })
          this.customers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Sale
    showSaleMenu(){
      this.updateSelected(this.sales, this.selectedSales);
      if(this.visibleSaleMenu){
        return;
      }
      this.getSales().then(r => {
        this.visibleSaleMenu = true;
      });
    },
    closeSaleMenu(){
      this.updateSelected(this.sales, this.selectedSales);
      this.visibleSaleMenu = false;
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
    getSales(){
        return http.post("/search/sale/kv", this.searchDto).then(r => {
          r.data.forEach(sale => {
            var found = this.selectedSales.find(it => it.id==sale.id && it.selected);
            sale.selected = found?true:false;
          })
          this.sales = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Supplier
    showSupplierMenu(){
      this.updateSelected(this.suppliers, this.selectedSuppliers);
      if(this.visibleSupplierMenu){
        return;
      }
      this.getSuppliers().then(r => {
        this.visibleSupplierMenu = true;
      });
    },
    closeSupplierMenu(){
      this.updateSelected(this.suppliers, this.selectedSuppliers);
      this.searchDto.suppliers = this.selectedSuppliers.map(it => it.id);
      this.visibleSupplierMenu = false;
    },
    clearSuppliers(){
      this.searchDto.supplierName = "";
      this.searchDto.suppliers = [];
      this.suppliers = [];
      this.selectedSuppliers = [];
      this.closeSupplierMenu();
      this.clearComponents();
    },
    getSuppliers(){
        return http.post("/search/supplier/kv", this.searchDto).then(r => {
          r.data.forEach(supplier => {
            var found = this.selectedSuppliers.find(it => it.id==supplier.id && it.selected);
            supplier.selected = found?true:false;
          })
          this.suppliers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
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
    getComponents(){
        return http.post("/search/component/kv", this.searchDto).then(r => {
          r.data.forEach(component => {
            var found = this.selectedComponents.find(it => it.id==component.id && it.selected);
            component.selected = found?true:false;
          })
          this.components = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    //Common methods.
    updateSelected(menuObjects, selectedObjects){
      menuObjects.forEach(dto => {
        var existing = selectedObjects.find(selected => selected.id == dto.id)
        if(dto.selected && !existing){
          selectedObjects.push(dto);
        }
      })
    },
    updateParent(){
      if(this.searchDto.components.length==0){
        alert("No Components selected. Please pick one.");
        return;
      }
      // if(this.searchDto.suppliers.length > 1){
      //   alert("Only one Supplier is allowed! Please pick only one.");
      //   return;
      // }
      this.$emit("componentsUpdated", this.searchDto);
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
.chk-all-tab {
  color: black; 
  background-color: white; 
  margin-bottom: -2px;
  margin-top: 15px;
  border-width: 1px 1px 0px 1px;
  border-style: solid;
  margin-left: 10px; 
  border-color: gray;
  z-index: 110;
  display: flex;
  border-radius: 4px;
  height: 25px;
}
.chk-all-tab input {
  width: 45px;
  margin-top: 6px;
  cursor: pointer;
}
.chk-all-tab span {
  margin-top: 6px;
  margin-left: -13px;
  margin-right: 10px;
}
.btn-tab {
  color: black; 
  background-color: white; 
  margin-bottom: -2px;
  margin-top: 15px;
  border-width: 1px 1px 0px 1px;
  border-style: solid;
  margin-left: 94px; 
  border-color: gray;
  z-index: 110;
  display: flex;
  border-radius: 4px;
  height: 25px;
  padding-right: 12px;
  padding-left: 12px;
  padding-top: 5px;
  cursor: pointer;
}
.search-width {
  width: 190px !important;
}
</style>
