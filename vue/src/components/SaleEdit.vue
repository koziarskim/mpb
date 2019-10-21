<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <h4 style="text-align: left;">Sale Order:</h4>
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="text" v-model="sale.name" placeholder="Sale name/description">
      </b-col>
      <b-col cols="2" offset="5">
        <div style="text-align: right;">
          <b-button type="reset" variant="success" @click="saveAndClose()">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4">
        <label class="top-label">Customer:</label>
        <b-select option-value="id" option-text="value" :list="availableCustomers" v-model="customerDto" placeholder="Customer"></b-select>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Date:</label>
        <input class="form-control" type="date" v-model="sale.date" placeholder="Date">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Sale Number:</label>
        <input class="form-control" type="tel" v-model="sale.number" placeholder="Number">
      </b-col>
      <b-col cols="2" offset="2">
        <label class="top-label">Pay Terms:</label>
        <input class="form-control" type="tel" v-model="sale.paymentTerms" placeholder="Pay Terms">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4" style="border: ">
        <label class="top-label">Vendor:</label>
        <br>
        <label class="top-label">Marketplace Brands LLC</label>
      </b-col>
      <b-col cols="4">
        <label class="top-label">shipping to Address:</label>
        <b-select option-value="id" option-text="dc" :list="customer.addresses" v-model="shippingAddress" placeholder="shipping to address"></b-select>
      </b-col>
      <b-col cols="2" offset="2">
        <label class="top-label">Expected Date:</label>
        <input class="form-control" type="date" v-model="sale.expectedDate" placeholder="Date">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4" offset="4">
        <label class="top-label">Street:</label>
        <input class="form-control" type="tel" readonly :value="shippingAddress.street" placeholder="City">
      </b-col>
      <b-col cols="2" offset="2">
        <label class="top-label">FOB:</label>
        <input class="form-control" type="tel" v-model="sale.freightTerms" placeholder="Flight Terms">
      </b-col>
      <b-row></b-row>
      <b-col cols="2" offset="4">
        <label class="top-label">City:</label>
        <input class="form-control" type="tel" readonly :value="shippingAddress.city" placeholder="City">
      </b-col>
      <b-col cols="1">
        <label class="top-label">State:</label>
        <input class="form-control" type="tel" readonly :value="shippingAddress.state" placeholder>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Zip Code:</label>
        <input class="form-control" type="tel" readonly :value="shippingAddress.zip" placeholder="Zip">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4">
        <label class="top-label">Available Items:</label>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemDto" placeholder="Customer"></b-select>
      </b-col>
      <b-col style="padding-top: 30px" cols="1">
        <b-button variant="link" @click="addItem()">(+)</b-button>
      </b-col>
      <b-col cols=5 offset=1 style="padding-top: 44px; padding-left: 0px;">
          <span style="font-weight: bold">Items #: </span>{{totalItems}},
          <span style="font-weight: bold">Units: </span>{{totalUnits}},
          <span style="font-weight: bold">Cases: </span>{{totalCases}},
          <span style="font-weight: bold">Total: </span>${{totalPrice}}
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="sale.saleItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="sale.saleItems" :fields="columns">
          <template v-slot:cell(item.number)="row">
            <b-button size="sm" @click.stop="goToItem(row.item.item.id)" variant="link">{{row.item.item.name}}</b-button>
          </template>
          <template v-slot:cell(components)="row">
            <b-button size="sm" variant="link" @click.stop="gotToItemComponentList(row.item.item.id)">View</b-button>
          </template>
          <template v-slot:cell(cost)="row">
            <span>${{row.item.item.totalCost}}</span>
          </template>
          <template v-slot:cell(units)="row">
            <input class="form-control" style="width:100px" type="tel" v-model="row.item.units">
          </template>
          <template v-slot:cell(cases)="row">
            <span>{{getCases(row.item)}}</span>
          </template>
          <template v-slot:cell(unitPrice)="row">
            $<input class="form-control" style="display: inline; width:100px" type="tel" v-model="row.item.unitPrice">
          </template>
          <template v-slot:cell(totalUnitPrice)="row">
            <span>${{row.item.totalUnitPrice = (+row.item.unitPrice * +row.item.units).toFixed(2)}}</span>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteItem(row.item.item.id)">x</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      locked: false,
      sale: {
        saleItems: [],
        customer: {},
        shippingAddress: {}
      },
      customer: {
        addresses: []
      },
      item: {},
      shippingAddress: {},
      availableCustomers: [],
      availableItems: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item.number", label: "Item", sortable: false },
        { key: "components", label: "Intentory", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "cost", label: "Cost", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "unitPrice", label: "Unit Price", sortable: false },
        { key: "totalUnitPrice", label: "Total", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      customerDto: {},
      itemDto: {},
    };
  },

  computed: {
      totalPrice(){
          var price = 0;
          this.sale.saleItems.forEach(si=> {
              price += +(si.totalUnitPrice?si.totalUnitPrice:0);
          })
          return price.toFixed(2);
      },
      totalItems(){
          var items = 0;
          this.sale.saleItems.forEach(si=> {
              items ++;
          })
          return items;
      },
      totalUnits(){
          var units = 0;
          this.sale.saleItems.forEach(si=> {
              units += +(si.units?si.units:0);
          })
          return units;
      },
      totalCases(){
          var cases = 0;
          this.sale.saleItems.forEach(si=> {
            var units = si.units?si.units:0;
            var casePack = si.item.casePack?si.item.casePack:0
              cases += +units/+casePack.toFixed(0);
          })
          return cases;
      },
  },
  watch: {
    shippingAddress(new_value, old_value) {
      this.sale.shippingAddress = new_value;
    },
    customerDto(new_value, old_value){
      if(new_value && new_value.id){
        this.getCustomer(new_value.id);
      }
    },
    customer(new_value, old_value) {
      this.sale.customer = new_value;
      if (old_value.id && new_value.id != old_value.id) {
        this.shippingAddress = {};
      }
    },
    itemDto(new_value, old_value){
      if(new_value && new_value.id){
        this.getItem(new_value.id)
      }
    }
  },
  methods: {
    getCustomer(customer_id){
      http.get("/customer/"+customer_id).then(response =>{
        this.customer = response.data;
      }).catch(e =>{
        console.log("API error: " + e);
      })
    },
    getItem(item_id){
      http.get("/item/"+item_id).then(response =>{
        this.item = response.data;
      }).catch(e =>{
        console.log("API error: " + e);
      })
    },
    getSaleData(id) {
      http
        .get("/sale/" + id)
        .then(response => {
          this.sale = response.data;
          if (response.data.customer) {
            this.customerDto = {
              id: response.data.customer.id,
              value: response.data.customer.name
            }
          }
          if (response.data.shippingAddress) {
            this.shippingAddress = response.data.shippingAddress;
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSale() {
      this.sale.totalPrice = this.totalPrice;
      return http
        .post("/sale", this.sale)
        .then(response => {
          return response;
        })
        .catch(e => {
          alert("There was an error! "+e);
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.saveSale().then(r => {
        window.history.back();
      });
    },
    getAvailableCustomers() {
      http
        .get("/customer/kv")
        .then(response => {
          this.availableCustomers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems() {
      http
        .get("/item/kv")
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addItem() {
      if (!this.item.id) {
        return;
      }
      var item = this.sale.saleItems.find(it => it.item.id == this.item.id);
      if (item) {
        return;
      }
      this.sale.saleItems.push({ 
          units: 0,
          unitPrice: 0.00,
          item: this.item });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    gotToItemComponentList(item_id){
        router.push('/itemComponentList/'+item_id);
    },
    deleteItem(item_id) {
      var idx = this.sale.saleItems.findIndex(it => it.item.id == item_id);
      this.sale.saleItems.splice(idx, 1);
    },
    getCases(si){
      return (+si.units / +si.item.casePack).toFixed(0);
    }
  },
  mounted() {
    var id = this.$route.params.sale_id;
    if (id) {
      this.getSaleData(id);
      this.getAvailableCustomers();
      this.getAvailableItems();
    }
  }
};
</script>

<style>
</style>
