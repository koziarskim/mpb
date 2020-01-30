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
      <b-col cols=4>
        <label class="top-label">Available Items:</label>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemDto" placeholder="Customer"></b-select>
      </b-col>
      <b-col cols=1 style="padding-top: 30px">
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
            <b-link role="button" @click.stop="goToItem(row.item.item.id)">{{row.item.item.number}}</b-link>
            <span> ({{row.item.item.name}})</span>
          </template>
          <template v-slot:cell(sku)="row">
            <input class="form-control" style="width:100px" type="tel" v-model="row.item.sku">
          </template>
          <template v-slot:cell(cost)="row">
            <span>${{row.item.item.totalCost}}</span>
          </template>
          <template v-slot:cell(unitsSchedProd)="row">
            <span>{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</span>
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
          <template v-slot:cell(unitsTransfered)="row">
            <b-button size="sm" variant="link" @click="openTransferModal(row.item)">{{row.item.unitsTransferedTo}}/{{row.item.unitsTransferedFrom}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click="deleteItem(row.item.item.id)">x</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
    <b-modal centered size="lg" v-model="modalVisible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols="2">
          <label class="top-label">Units Sold:</label>
          <input class="form-control" type="tel" v-model="unitsForSale" placeholder="0">
        </b-col>
        <b-col cols="2">
          <label class="top-label">Unit Price:</label>
          <input class="form-control" type="tel" v-model="unitPrice" placeholder="0.00">
        </b-col>
      </b-row>
      <br/>
      <b-row>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
    </b-modal>
    <div v-if="transferModalVisible">
			<transfer-modal :sale-item-to="saleItemTo" v-on:saveModal="saveTransferModal"></transfer-modal>
		</div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  components: {
    TransferModal: () => import("./TransferModal")
  },
  data() {
    return {
      modalVisible: false,
      transferModalVisible: false,
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
        { key: "sku", label: "SKU#", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "unitsSchedProd", label: "Sched/Prod", sortable: false },
        { key: "unitsTransfered", label: "Trans", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "cost", label: "Cost", sortable: false },
        { key: "unitPrice", label: "Unit Price", sortable: false },
        { key: "totalUnitPrice", label: "Total", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      customerDto: {},
      itemDto: {},
      unitsForSale: null,
      unitPrice: null,
      saleItemTo: {},
      saleFromIds: [],
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
    openTransferModal(saleItem){
      this.saleItemTo = saleItem;
      this.saleItemTo.saleNumber = this.sale.number
      this.transferModalVisible = true;
    },
    saveTransferModal(saleItemTo){
      this.saleItemTo = {},
      this.transferModalVisible = false;
    },
    saveModal(){
      this.saveSaleItem().then(si => {
        this.getSaleData(this.sale.id).then(sale => {
          this.closeModal();
        })
      })
    },
    closeModal(){
      this.unitsForSale = null,
      this.unitPrice = null,
      this.modalVisible = false;
    },
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
      return http.get("/sale/" + id).then(response => {
          this.sale = response.data;
          this.setSaleFromIds();
          if (response.data.customer) {
            this.customerDto = {
              id: response.data.customer.id,
              value: response.data.customer.name
            }
          }
          if (response.data.shippingAddress) {
            this.shippingAddress = response.data.shippingAddress;
          }
          return response.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    setSaleFromIds(){
      this.sale.saleItems.forEach(si => {
        si.transfersTo.forEach(sit => {
          this.saleFromIds.push(sit.saleFromId);
        })
      })
    },
    saveSaleItem() {
      var saleItem = {
        sale: {id: this.sale.id},
        item: {id: this.itemDto.id},
        units: this.unitsForSale,
        unitPrice: this.unitPrice,
      };
      return http.post("/saleItem", saleItem).then(r => {
          return r;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    saveSale() {
      this.sale.totalPrice = this.totalPrice;
      if(!this.sale.customer || !this.sale.customer.id){
        this.sale.customer = null;
      }
      if(!this.sale.shippingAddress || !this.sale.shippingAddress.id){
        this.sale.shippingAddress = null;
      }
      return http.post("/sale", this.sale).then(r => {
          return r;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    validate(){
      if(!this.sale.number || !this.sale.name){
        alert("Sale Name and Number required");
        return false;
      }
      return true;
    },
    saveAndClose() {
      if(!this.validate()){
        return;
      }
      this.saveSale().then(r => {
        this.setSaleFromIds();
        var uniqueIds = [...new Set(this.saleFromIds)]
        uniqueIds.forEach(id => {
          this.updateSale(id);
        })
        window.history.back();
      });
    },
    updateSale(saleId){
      http.post("/sale/units/"+saleId);
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
      // this.modalVisible = true;
      this.sale.saleItems.push({ 
          units: 0,
          unitPrice: 0.00,
          item: this.item,
          unitsTransferedTo: 0,
          unitsTransferedFrom: 0,
          transfersTo: [],
          transfersFrom: [] });
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
    }
    this.getAvailableCustomers();
    this.getAvailableItems();
  }
};
</script>

<style>
</style>
