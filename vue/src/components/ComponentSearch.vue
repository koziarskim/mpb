<template>
  <b-container fluid style="font-size: 10px;">
    <!-- Season -->
    <label class="top-label">Seasons: <input type="checkbox" v-model="searchDto.seasonAll" @click="toggleAll(seasons, searchDto.seasonName, searchDto.seasonAll, itemClear())"></label>
    <div style="display: flex">
      <input @keydown.enter="getSeasons(true)" @click="showMenu(getSeasons(), visibleSeasonMenu)" class="form-control search-width" type="tel" v-model="searchDto.seasonName" placeholder="Pick Season">
      <div v-if="visibleSeasonMenu.value" class="btn-tab" @click="closeMenu(seasons, searchDto.seasons, visibleSeasonMenu)">Close</div>
    </div>
    <div v-if="visibleSeasonMenu.value" class="menu-tab">
        <div v-for="season in seasons" :key="season.id">
          <div v-if="!season.hide">
            <input type="checkbox" v-model="season.selected" @click="searchDto.seasonAll = false; itemClear()">
            <span>{{season.name}}</span>
          </div>
      </div>
    </div>
    <br/>

    <!-- Item -->
    <label class="top-label">Items: <input type="checkbox" v-model="searchDto.itemAll" @click="toggleAll(items, searchDto.itemName, searchDto.itemAll, customerClear())"></label>
    <div style="display: flex">
      <input @keydown.enter="getItems(true)" @click="showMenu(getItems(), visibleItemMenu)" class="form-control search-width" type="tel" v-model="searchDto.itemName" placeholder="Pick Item">
      <div v-if="visibleItemMenu.value" class="btn-tab" @click="closeMenu(items, searchDto.items, visibleItemMenu)">Close</div>
    </div>
    <div v-if="visibleItemMenu.value" class="menu-tab">
        <div v-for="item in items" :key="item.id">
          <div v-if="!item.hide">
            <input type="checkbox" v-model="item.selected" @click="searchDto.itemAll = false; customerClear()">
            <span>{{item.name}}</span>
          </div>
      </div>
    </div>
    <br/>

    <!-- Customer -->
    <label class="top-label">Customers: <input type="checkbox" v-model="searchDto.customerAll" @click="toggleAll(customers, searchDto.customerName, searchDto.customerAll, saleClear())"></label>
    <div style="display: flex">
      <input @keydown.enter="getCustomers(true)" @click="showMenu(getCustomers(), visibleCustomerMenu)" class="form-control search-width" type="tel" v-model="searchDto.customerName" placeholder="Pick Customer">
      <div v-if="visibleCustomerMenu.value" class="btn-tab" @click="closeMenu(customers, searchDto.customers, visibleCustomerMenu)">Close</div>
    </div>
    <div v-if="visibleCustomerMenu.value" class="menu-tab">
        <div v-for="customer in customers" :key="customer.id">
          <div v-if="!customer.hide">
            <input type="checkbox" v-model="customer.selected" @click="searchDto.customerAll = false; saleClear()">
            <span>{{customer.name}}</span>
          </div>
      </div>
    </div>
    <br/>

    <!-- Sale -->
    <label class="top-label">Sales: <input type="checkbox" v-model="searchDto.saleAll" @click="toggleAll(sales, searchDto.saleName, searchDto.saleAll, supplierClear())"></label>
    <div style="display: flex">
      <input @keydown.enter="getSales(true)" @click="showMenu(getSales(), visibleSaleMenu)" class="form-control search-width" type="tel" v-model="searchDto.saleNumber" placeholder="Pick Sale">
      <div v-if="visibleSaleMenu.value" class="btn-tab" @click="closeMenu(sales, searchDto.sales, visibleSaleMenu)">Close</div>
    </div>
    <div v-if="visibleSaleMenu.value" class="menu-tab">
        <div v-for="sale in sales" :key="sale.id">
          <div v-if="!sale.hide">
            <input type="checkbox" v-model="sale.selected" @click="searchDto.saleAll = false; supplierClear()">
            <span>{{sale.name}}</span>
          </div>
      </div>
    </div>
    <br/>

    <!-- Supplier -->
    <label class="top-label">Suppliers: <input type="checkbox" v-model="searchDto.supplierAll" @click="toggleAll(suppliers, searchDto.supplierName, searchDto.supplierAll, componentClear())"></label>
    <div style="display: flex">
      <input @keydown.enter="getSuppliers(true)" @click="showMenu(getSuppliers(), visibleSupplierMenu)" class="form-control search-width" type="tel" v-model="searchDto.supplierName" placeholder="Pick Supplier">
      <div v-if="visibleSupplierMenu.value" class="btn-tab" @click="closeMenu(suppliers, searchDto.suppliers, visibleSupplierMenu)">Close</div>
    </div>
    <div v-if="visibleSupplierMenu.value" class="menu-tab">
        <div v-for="supplier in suppliers" :key="supplier.id">
          <div v-if="!supplier.hide">
            <input type="checkbox" v-model="supplier.selected" @click="searchDto.supplierAll = false; componentClear()">
            <span>{{supplier.name}}</span>
          </div>
      </div>
    </div>
    <br/>

    <!-- Component -->
    <label class="top-label">Components: <input type="checkbox" v-model="searchDto.componentAll" @click="toggleAll(components, searchDto.componentName, searchDto.componentAll, null)"></label>
    <div style="display: flex">
      <input @keydown.enter="getComponents(true)" @click="showMenu(getComponents(), visibleComponentMenu)" class="form-control search-width" type="tel" v-model="searchDto.componentName" placeholder="Pick Component">
      <div v-if="visibleComponentMenu.value" class="btn-tab" @click="closeMenu(components, searchDto.components, visibleComponentMenu)">Close</div>
    </div>
    <div v-if="visibleComponentMenu.value" class="menu-tab">
        <div v-for="component in components" :key="component.id">
          <div v-if="!component.hide">
            <input type="checkbox" v-model="component.selected" @click="searchDto.componentAll = false">
            <span>{{component.name}}</span>
          </div>
      </div>
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
        seasonAll: true,
        items: [],
        itemName: "",
        itemAll: true,
        customers: [],
        customerName: "",
        customerAll: true,
        sales: [],
        saleNumbe: "",
        saleAll: true,
        suppliers: [],
        supplierName: "",
        supplierAll: true,
        components: [],
        componentName: "",
        componentAll: false,
      },

      visibleSeasonMenu: {value: false},
      seasons: [],

      visibleItemMenu: {value: false},
      items: [],

      visibleCustomerMenu: {value: false},
      customers: [],

      visibleSaleMenu: {value: false},
      sales: [],

      visibleSupplierMenu: {value: false},
      suppliers: [],

      visibleComponentMenu: {value: false},
      components: [],
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    // Season
    seasonClear(){
      this.seasons = [];
      this.searchDto.seasonAll = true;
      this.searchDto.seasons = [];
      this.itemClear();
      this.searchDto.seasonName = "";
      this.seasons.forEach(dto => dto.hide = false);
    },
    getSeasons(enter){
      if(this.searchDto.seasonAll){
        this.searchDto.seasonAll = false;
        this.toggleAll(this.seasons, this.searchDto.seasonName, !this.searchDto.seasonAll, this.itemClear())
      }
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      var useLocal = this.getLocal(this.seasons, [], this.searchDto.seasonName, true, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/season/kv", this.searchDto).then(r => {
          this.setResponse(this.seasons, this.searchDto.seasonAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Item
    itemClear(){
      this.items = [];
      this.searchDto.itemAll = true;
      this.searchDto.items = [];
      this.customerClear();
      this.searchDto.itemName = "";
      this.items.forEach(dto => dto.hide = false);
    },
    getItems(enter){
      if(this.searchDto.itemAll){
        this.searchDto.itemAll = false;
        this.toggleAll(this.items, this.searchDto.itemName, !this.searchDto.itemAll, this.customerClear())
      }
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      var useLocal = this.getLocal(this.items, this.searchDto.seasons, this.searchDto.itemName, this.searchDto.seasonAll, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/item/kv", this.searchDto).then(r => {
          this.setResponse(this.items, this.searchDto.itemAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Customer
    customerClear(){
      this.customers = [];
      this.searchDto.customerAll = true;
      this.searchDto.customers = [];
      this.saleClear();
      this.searchDto.customerName = "";
      this.customers.forEach(dto => dto.hide = false);
    },
    getCustomers(enter){
      if(this.searchDto.customerAll){
        this.searchDto.customerAll = false;
        this.toggleAll(this.customers, this.searchDto.customerName, !this.searchDto.customerAll, this.saleClear())
      }
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      var useLocal = this.getLocal(this.customers, this.searchDto.items, this.searchDto.customerName, this.searchDto.itemAll, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/customer/kv", this.searchDto).then(r => {
          this.setResponse(this.customers, this.searchDto.customerAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Sale
    saleClear(){
      this.sales = [];
      this.searchDto.saleAll = true;
      this.searchDto.sales = [];
      this.supplierClear();
      this.searchDto.saleName = "";
      this.sales.forEach(dto => dto.hide = false);
    },
    getSales(enter){
      if(this.searchDto.saleAll){
        this.searchDto.saleAll = false;
        this.toggleAll(this.sales, this.searchDto.saleName, !this.searchDto.saleAll, this.supplierClear())
      }
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      var useLocal = this.getLocal(this.sales, this.searchDto.customers, this.searchDto.saleNumber, this.searchDto.customerAll, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/sale/kv", this.searchDto).then(r => {
          this.setResponse(this.sales, this.searchDto.saleAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Supplier
    supplierClear(){
      this.suppliers = [];
      this.searchDto.supplierAll = true;
      this.searchDto.suppliers = [];
      this.componentClear();
      this.searchDto.supplierName = "";
      this.suppliers.forEach(dto => dto.hide = false);
    },
    getSuppliers(enter){
      if(this.searchDto.supplierAll){
        this.searchDto.supplierAll = false;
        this.toggleAll(this.suppliers, this.searchDto.supplierName, !this.searchDto.supplierAll, this.componentClear())
      }
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      var useLocal = this.getLocal(this.suppliers, this.searchDto.sales, this.searchDto.supplierName, this.searchDto.saleAll, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/supplier/kv", this.searchDto).then(r => {
          this.setResponse(this.suppliers, this.searchDto.supplierAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Component
    componentClear(){
      this.components = [];
      this.searchDto.componentAll = false;
      this.searchDto.components = [];
      this.searchDto.componentName = "";
      this.components.forEach(dto => dto.hide = false);
    },
    getComponents(enter){
      if(this.searchDto.componentAll){
        this.searchDto.componentAll = false;
        this.toggleAll(this.components, this.searchDto.componentName, !this.searchDto.componentAll, null)
      }
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      var useLocal = this.getLocal(this.components, this.searchDto.suppliers, this.searchDto.componentName, this.searchDto.supplierAll, enter);
      if(useLocal){
        return Promise.resolve();
      }
      return http.post("/search/component/kv", this.searchDto).then(r => {
          this.setResponse(this.components, this.searchDto.componentAll, r.data);
          return Promise.resolve();
        }).catch(e => {
          console.log("API error: " + e);
        });
    },

    //Common methods.
    toggleAll(objects, searchKey, allFlag){
      searchKey = "";
      objects.forEach(it => {
        it.selected = !allFlag;
        it.hide = false;
      })
    },
    showMenu(promise, menu){
      if(menu.value){
        return;
      }
     promise.then(r => {
        menu.value = true;
      });
    },
    closeMenu(objects, searchOjects, menu){
      searchOjects.splice(0,searchOjects.length)
      objects.forEach(dto => {
        if(dto.selected){
          searchOjects.push(dto.id);
        }
      })
      menu.value = false;
    },
    setResponse(objects, allFlag, data){
      data.forEach(dto => {
        dto.hide = false;
        dto.selected = allFlag;
        objects.push(dto);
      })
    },
    getLocal(objects, searchObjects, searchKey, allFlag, enter){
      if(objects.length > 0 && (searchKey || enter)){
          objects.forEach(dto => {
            dto.hide = true;
            if(!searchKey && enter){
              dto.hide = false;
            }else if(dto.name && dto.name.toUpperCase().indexOf(searchKey.toUpperCase()) >= 0){
              dto.hide = false;
            }
          });
        return true;
      }else if(objects.length > 0){
        return true;
      }
      if(searchObjects.length == 0 && !allFlag){
        objects.splice(0,objects.length)
        return true;
      }

      return false;
    },
    updateParent(){
      this.closeMenu(this.seasons, this.searchDto.seasons, this.visibleSeasonMenu)
      this.closeMenu(this.items, this.searchDto.items, this.visibleItemMenu)
      this.closeMenu(this.customers, this.searchDto.customers, this.visibleCustomerMenu)
      this.closeMenu(this.sales, this.searchDto.sales, this.visibleSaleMenu)
      this.closeMenu(this.suppliers, this.searchDto.suppliers, this.visibleSupplierMenu)
      this.closeMenu(this.components, this.searchDto.components, this.visibleComponentMenu)
      if(this.searchDto.components.length==0){
        alert("No Components selected. Please pick one.");
        return;
      }
      var dto = JSON.parse(JSON.stringify(this.searchDto));
      this.$emit("componentsUpdated", dto);
      this.itemClear();
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
