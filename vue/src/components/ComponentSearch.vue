<template>
  <b-container fluid style="font-size: 10px;">
    <!-- Season -->
    <label class="top-label">Seasons: <input type="checkbox" v-model="searchDto.seasonAll" @click="seasonAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getSeasons()" @click="showSeasonMenu()" class="form-control search-width" type="tel" v-model="searchDto.seasonName" placeholder="Pick Season">
      <div v-if="visibleSeasonMenu" class="btn-tab" @click="closeSeasonMenu()">Close</div>
    </div>
    <div v-if="visibleSeasonMenu" class="menu-tab">
        <div v-for="season in seasons" :key="season.id">
          <input type="checkbox" v-model="season.selected" @click="seasonSelect()">
          <span>{{season.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="season in selectedSeasons" :key="season.id+season.name">{{season.name}}</div>
    </div>
    <br/>

    <!-- Item -->
    <label class="top-label">Items: <input type="checkbox" v-model="searchDto.itemAll" @click="itemAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getItems()" @click="showItemMenu()" class="form-control search-width" type="tel" v-model="searchDto.itemName" placeholder="Pick Item">
      <div v-if="visibleItemMenu" class="btn-tab" @click="closeItemMenu()">Close</div>
    </div>
    <div v-if="visibleItemMenu" class="menu-tab">
        <div v-for="item in items" :key="item.id">
          <input type="checkbox" v-model="item.selected" @click="itemSelect()">
          <span>{{item.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="item in selectedItems" :key="item.id+item.name">{{item.name}}</div>
    </div>
    <br/>

    <!-- Customer -->
    <label class="top-label">Customers: <input type="checkbox" v-model="searchDto.customerAll" @click="customerAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getCustomers()" @click="showCustomerMenu()" class="form-control search-width" type="tel" v-model="searchDto.customerName" placeholder="Pick Customer">
      <div v-if="visibleCustomerMenu" class="btn-tab" @click="closeCustomerMenu()">Close</div>
    </div>
    <div v-if="visibleCustomerMenu" class="menu-tab">
        <div v-for="customer in customers" :key="customer.id">
          <input type="checkbox" v-model="customer.selected" @click="customerSelect()">
          <span>{{customer.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="customer in selectedCustomers" :key="customer.id+customer.name">{{customer.name}}</div>
    </div>
    <br/>

    <!-- Sale -->
    <label class="top-label">Sales: <input type="checkbox" v-model="searchDto.saleAll" @click="saleAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getSales()" @click="showSaleMenu()" class="form-control search-width" type="tel" v-model="searchDto.saleNumber" placeholder="Pick Sale">
      <div v-if="visibleSaleMenu" class="btn-tab" @click="closeSaleMenu()">Close</div>
    </div>
    <div v-if="visibleSaleMenu" class="menu-tab">
        <div v-for="sale in sales" :key="sale.id">
          <input type="checkbox" v-model="sale.selected" @click="saleSelect()">
          <span>{{sale.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="sale in selectedSales" :key="sale.id+sale.name">{{sale.name}}</div>
    </div>
    <br/>

    <!-- Supplier -->
    <label class="top-label">Suppliers: <input type="checkbox" v-model="searchDto.supplierAll" @click="supplierAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getSuppliers()" @click="showSupplierMenu()" class="form-control search-width" type="tel" v-model="searchDto.supplierName" placeholder="Pick Supplier">
      <div v-if="visibleSupplierMenu" class="btn-tab" @click="closeSupplierMenu()">Close</div>
    </div>
    <div v-if="visibleSupplierMenu" class="menu-tab">
        <div v-for="supplier in suppliers" :key="supplier.id">
          <input type="checkbox" v-model="supplier.selected" @click="supplierSelect()">
          <span>{{supplier.name}}</span>
      </div>
    </div>
    <div v-if="showSelected">
      <div v-for="supplier in selectedSuppliers" :key="supplier.id+supplier.name">{{supplier.name}}</div>
    </div>
    <br/>

    <!-- Component -->
    <label class="top-label">Components: <input type="checkbox" v-model="searchDto.componentAll" @click="componentAll()"></label>
    <div style="display: flex">
      <input @keydown.enter="getComponents(true)" @click="showComponentMenu()" class="form-control search-width" type="tel" v-model="searchDto.componentName" placeholder="Pick Component">
      <div v-if="visibleComponentMenu" class="btn-tab" @click="closeComponentMenu()">Close</div>
    </div>
    <div v-if="visibleComponentMenu" class="menu-tab">
        <div v-for="component in components" :key="component.id">
          <div v-if="!component.hide">
            <input type="checkbox" v-model="component.selected" @click="componentSelect()">
            <span>{{component.name}}</span>
          </div>
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
        componentAll: true,
      },

      visibleSeasonMenu: false,
      seasons: [],
      selectedSeasons: [],

      visibleItemMenu: false,
      items: [],
      selectedItems: [],

      visibleCustomerMenu: false,
      customers: [],
      selectedCustomers: [],

      visibleSaleMenu: false,
      sales: [],
      selectedSales: [],

      visibleSupplierMenu: false,
      suppliers: [],
      selectedSuppliers: [],

      visibleComponentMenu: false,
      components: [],
      componentsFull: [],
      selectedComponents: [],

    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    // Season
    seasonSelect(){
      this.searchDto.seasonAll = false;
      this.itemClear();
    },
    seasonAll(value){
      this.seasons.forEach(it => {it.selected = !this.searchDto.seasonAll});
      this.itemClear();
    },
    showSeasonMenu(){
      if(this.visibleSeasonMenu){
        return;
      }
      this.getSeasons().then(r => {
        this.visibleSeasonMenu = true;
      });
    },
    closeSeasonMenu(){
      this.searchDto.seasons = [];
      this.seasons.forEach(dto => {
        if(dto.selected){
          this.searchDto.seasons.push(dto.id);
        }
      })
       this.visibleSeasonMenu = false;
    },
    getSeasons(){
      if(this.seasons.length > 0){
        return Promise.resolve();
      }
        return http.post("/search/season/kv", this.searchDto).then(r => {
          this.seasons = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Item
    itemSelect(){
      this.searchDto.itemAll = false;
      this.customerClear();
    },
    itemClear(){
      this.items = [];
      this.searchDto.itemAll = true;
      this.searchDto.items = [];
      this.customerClear();
    },
    itemAll(value){
      this.items.forEach(it => {it.selected = !this.searchDto.itemAll;})
    },
    showItemMenu(){
      if(this.visibleItemMenu){
        return;
      }
      this.getItems().then(r => {
        this.visibleItemMenu = true;
      });
    },
    closeItemMenu(){
      this.searchDto.items = [];
      this.items.forEach(dto => {
        if(dto.selected){
          this.searchDto.items.push(dto.id);
        }
      })
      this.visibleItemMenu = false;
    },
    getItems(){
      if(this.items.length > 0){
        return Promise.resolve();
      }
      if(this.searchDto.seasons.length == 0 && !this.searchDto.seasonAll){
        this.items = [];
        return Promise.resolve();
      }
        return http.post("/search/item/kv", this.searchDto).then(r => {
          this.items = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Customer
    customerSelect(){
      this.searchDto.customerAll = false;
      this.saleClear();
    },
    customerClear(){
      this.customers = [];
      this.searchDto.customerAll = true;
      this.searchDto.customers = [];
      this.saleClear();
    },
    customerAll(value){
      this.customers.forEach(it => {it.selected = !this.searchDto.customerAll;})
    },
    showCustomerMenu(){
      if(this.visibleCustomerMenu){
        return;
      }
      this.getCustomers().then(r => {
        this.visibleCustomerMenu = true;
      });
    },
    closeCustomerMenu(){
      this.searchDto.customers = [];
      this.customers.forEach(dto => {
        if(dto.selected){
          this.searchDto.customers.push(dto.id);
        }
      })
      this.visibleCustomerMenu = false;
    },
    getCustomers(){
      if(this.customers.length > 0){
        return Promise.resolve();
      }
      if(this.searchDto.items.length == 0 && !this.searchDto.itemAll){
        this.customers = [];
        return Promise.resolve();
      }
        return http.post("/search/customer/kv", this.searchDto).then(r => {
          this.customers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Sale
    saleSelect(){
      this.searchDto.saleAll = false;
      this.supplierClear();
    },
    saleClear(){
      this.sales = [];
      this.searchDto.saleAll = true;
      this.searchDto.sales = [];
      this.supplierClear();
    },
    saleAll(value){
      this.sales.forEach(it => {it.selected = !this.searchDto.saleAll;})
    },
    showSaleMenu(){
      if(this.visibleSaleMenu){
        return;
      }
      this.getSales().then(r => {
        this.visibleSaleMenu = true;
      });
    },
    closeSaleMenu(){
      this.searchDto.sales = [];
      this.sales.forEach(dto => {
        if(dto.selected){
          this.searchDto.sales.push(dto.id);
        }
      })
      this.visibleSaleMenu = false;
    },
    getSales(){
      if(this.sales.length > 0){
        return Promise.resolve();
      }
      if(this.searchDto.customers.length == 0 && !this.searchDto.customerAll){
        this.sales = [];
        return Promise.resolve();
      }
        return http.post("/search/sale/kv", this.searchDto).then(r => {
          this.sales = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Supplier
    supplierSelect(){
      this.searchDto.supplierAll = false;
      this.componentClear();
    },
    supplierClear(){
      this.suppliers = [];
      this.searchDto.supplierAll = true;
      this.searchDto.suppliers = [];
      this.componentClear();
    },
    supplierAll(value){
      this.suppliers.forEach(it => {it.selected = !this.searchDto.supplierAll;})
    },
    showSupplierMenu(){
      if(this.visibleSupplierMenu){
        return;
      }
      this.getSuppliers().then(r => {
        this.visibleSupplierMenu = true;
      });
    },
    closeSupplierMenu(){
      this.searchDto.suppliers = [];
      this.suppliers.forEach(dto => {
        if(dto.selected){
          this.searchDto.suppliers.push(dto.id);
        }
      })
      this.visibleSupplierMenu = false;
    },
    getSuppliers(){
      if(this.suppliers.length > 0){
        return Promise.resolve();
      }
      if(this.searchDto.sales.length == 0 && !this.searchDto.saleAll){
        this.suppliers = [];
        return Promise.resolve();
      }
        return http.post("/search/supplier/kv", this.searchDto).then(r => {
          this.suppliers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    // Component
    componentSelect(){
      this.searchDto.componentAll = false;
    },
    componentClear(){
      this.components = [];
      this.searchDto.componentAll = true;
      this.searchDto.components = [];
    },
    componentAll(value){
      this.searchDto.componentName = "";
      this.components.forEach(it => {
        it.selected = !this.searchDto.componentAll;
        it.hide = false;
      })
    },
    showComponentMenu(){
      if(this.visibleComponentMenu){
        return;
      }
      this.getComponents().then(r => {
        this.visibleComponentMenu = true;
      });
    },
    closeComponentMenu(){
      this.searchDto.components = [];
      this.components.forEach(dto => {
        if(dto.selected){
          this.searchDto.components.push(dto.id);
        }
      })
      this.visibleComponentMenu = false;
    },
    getComponents(enter){
      if(this.components.length > 0 && (this.searchDto.componentName || enter)){
          this.components.forEach(dto => {
            dto.hide = true;
            if(!this.searchDto.componentName && enter){
              dto.hide = false;
            }else if(dto.name && dto.name.toUpperCase().indexOf(this.searchDto.componentName.toUpperCase()) >= 0){
              dto.hide = false;
            }
          });
        return Promise.resolve();
      }else if(this.components.length > 0){
        return Promise.resolve();
      }
      if(this.searchDto.suppliers.length == 0 && !this.searchDto.supplierAll){
        this.components = [];
        return Promise.resolve();
      }
        return http.post("/search/component/kv", this.searchDto).then(r => {
          r.data.forEach(dto => {
            dto.hide = false;
            dto.selected = this.searchDto.componentAll;
          })
          this.components = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      return Promise.resolve();
    },

    //Common methods.
    updateParent(){
      if(this.searchDto.components.length==0){
        alert("No Components selected. Please pick one.");
        return;
      }
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
