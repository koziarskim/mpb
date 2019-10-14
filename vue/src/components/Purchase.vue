<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order:</span>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="text" v-model="purchase.number" placeholder="P.O. Number">
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="purchase.date" placeholder="Date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 10px;">
      <b-col cols=2>
        <label class="top-label">Supplier:</label>
        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier" placeholder="Pick Supplier"></b-select>
        <label class="top-label">Item:</label>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="item" placeholder="Pick Item"></b-select>
        <div v-for="item in items" v-bind:key="item.id">{{item.name}}</div>
        <item-search></item-search>
      </b-col>
      <b-col cols=10>
        <b-row>
          <b-col cols=6>
            <label class="top-label">Sales/S.O.:</label>
            <div v-for="s in availableSales" v-bind:key="s.id"><span>{{s.number}}</span> | 
              <span>{{s.customerName}}</span> | <span>{{s.unitsSold}}</span> | <span>{{s.unitsProduced}}</span>
            </div>
            <!-- <b-table style="overflow-x: scroll;" sort-by.sync="id" sort-desc.sync="false" :sticky-header="saleHeight()" :items="availableSales" :fields="saleColumns">
            </b-table> -->
          </b-col>
          <b-col cols=6>
            <label class="top-label">Components:</label>
            <div v-for="c in availableComponents" v-bind:key="c.id">{{c.name}}</div>
            <!-- <b-table sort-by.sync="id" sort-desc.sync="false" :items="availableComponents" :fields="componentColumns">
              <template v-slot:cell(name)="row">
                <b-button size="sm" variant="link">{{row.item.name}}</b-button>
              </template>
            </b-table> -->
          </b-col>
        </b-row>
      </b-col>
    </b-row>

  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";
import vue from "vue";
import ItemSearch from "./ItemSearch";

export default {
  data() {
    return {
      purchase: {
        date: moment().utc().format("YYYY-MM-DD")
      },
      availableItems: [],
      item: {},
      items: [],
      availableSales: [],
      sales: [],
      availableSuppliers: [],
      supplier: {},
      availableComponents: [],
      component: {},
      saleColumns: [
        { key: "name", label: "Sale", sortable: false },
        { key: "sale.customer.name", label: "Customer", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      componentColumns: [
        { key: "name", label: "Component", sortable: false },
        { key: "sale.customer.name", label: "Customer", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
    };
  },
  components:{
    'item-search': ItemSearch
  },
  computed: {},
  watch: {
    item(new_value, old_value){
      if(new_value && new_value.id){
        this.getAvailableSuppliers();
        this.getAvailableSales();
        this.getAvailableComponents();
      }
    },
    supplier(new_value, old_value){
      if(new_value && new_value.id){
        this.getAvailableItems();
        this.getAvailableSales();
        this.getAvailableComponents();
      }
    }
  },
  methods: {
    getPurchase(purchase_id) {
      http.get("/purchase/" + purchase_id).then(r => {
          this.purchase = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems() {
      this.availableItems = [];
      http.get("/item/kv").then(response => {
          this.availableItems = response.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers() {
      this.availableSuppliers = [];
      var url = "/supplier/kv";
      http.get(url).then(r => {
          this.availableSuppliers = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSales() {
      var url = "/purchaseSaleDto";
      http.get(url).then(r => {
          this.availableSales = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableComponents() {
      var url = "/component/kv";
      http.get(url).then(r => {
          this.availableComponents = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      return http.post("/purchase", this.purchase).then(r => {
          this.getPurchaseData(r.data.id);
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.savePurchase().then(r => {
        router.push("/purchaseList");
      });
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    saleHeight(){
      return +window.innerHeight - 150 +"px";
    },
  },
  mounted() {
    var purchase_id = this.$route.params.purchase_id;
    if (purchase_id) {
      this.getPurchaseData(purchase_id);
    }
    this.getAvailableItems();
    this.getAvailableSuppliers();
  }
};
</script>

