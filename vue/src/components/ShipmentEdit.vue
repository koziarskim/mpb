<template>
  <b-container fluid style="background-color: #f9f9f9; padding-left: 5px !important; padding-right: 5px !important; height: 90vh;">
    <b-row>
      <b-col cols=2>
          <label style="font-size: 18px; font-weight: bold; margin-top: -10px">Shipment #:</label>
          <input class="form-control" style="margin-top: -7px;" type="tel" v-model="shipment.number">
      </b-col>
      <b-col cols=3>
        <label class="top-label">Customer:</label>
        <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Select Customer"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Date:</label>
        <input class="form-control" type="date" v-model="shipment.date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Freight Terms:</label>
        <input class="form-control" type="tel" v-model="shipment.freightTerms">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Load Number:</label>
        <input class="form-control" type="tel" v-model="shipment.loadNumber">
      </b-col>      
      <b-col>
        <div style="margin-top: 22px;">
          <b-button type="reset" variant="primary" style="margin-left: -18px" @click="saveShipment()">Save</b-button>
          <img @click="openPdf()" style="margin: 2px; cursor: pointer" src="../assets/pdf-download.png" width="25px">
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <b-row>
          <b-col cols=6>
            <label class="top-label">Ship To:</label>
            <b-select option-value="id" option-text="name" :list="availableShippingAddresses" v-model="shippingAddress" placeholder="Pick shipping address"></b-select>
          </b-col>
          <b-col cols=6>
            <label class="top-label">Freight Address:<a href="#" @click="openModal()"> (Edit/New) </a></label>
            <b-select option-value="id" option-text="name" :list="availableFreightAddresses" v-model="freightAddress" placeholder="Pick freight address"></b-select>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=4>
            <label class="top-label">Via:</label>
            <input class="form-control" type="tel" v-model="shipment.via">
          </b-col>
          <b-col cols=4>
            <label class="top-label">FOB:</label>
            <input class="form-control" type="tel" v-model="shipment.fob">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Freight/NMFC:</label>
            <input class="form-control" type="tel" v-model="shipment.freightNmfc">
          </b-col>
        </b-row>
      </b-col>
      <b-col>
        <label class="top-label">Special Notes:</label>
        <b-form-textarea style="padding-bottom: -15px;" type="text" :rows="4" v-model="shipment.notes" placeholder="Special Notes"></b-form-textarea>
      </b-col>
    </b-row>
    <div style="border: 1px solid #d6d3d3; margin-top: 10px;">
      <b-row>
        <b-col cols=6>
          <label class="top-label">Sale ( Item ):</label>
          <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItem"></b-select>
        </b-col>
        <b-col cols=1>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addSaleItemKv(saleItem.id)">Add &#x25BC;</b-button>
        </b-col>
        <b-col style="margin-top: 15px">
          <b>Total units:</b>
          {{totalUnits}},
          <b>Total cases:</b>
          {{totalCases}},<br/>
          <b>Total pallets:</b>
          {{totalPallets}}
          <b>Total weight:</b>
          {{totalWeight}}
        </b-col>
      </b-row>
      <br>
      <b-row>
        <b-col>
          <b-table v-if="shipment.shipmentItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="shipment.shipmentItems" :fields="columns">
            <template v-slot:cell(item)="row">
              <b-button @click.stop="goToItem(row.item.saleItem.item.id)" variant="link">{{row.item.saleItem.item.number}}</b-button>
            </template>
            <template v-slot:cell(sale)="row">
              <b-button @click.stop="goToSale(row.item.saleItem.sale.id)" variant="link">{{row.item.saleItem.sale.number}}</b-button>
            </template>
            <template v-slot:cell(units)="row">
              <input class="form-control" style="width:100px" type="tel" v-model="row.item.units" @blur="unitsBlur(row.item)">
            </template>
            <template v-slot:cell(cases)="row">
              <span>{{row.item.cases = Math.ceil(+row.item.units / +row.item.saleItem.item.casePack)}}</span>
            </template>
            <template v-slot:cell(pallets)="row">
              <span>{{row.item.pallets = Math.ceil(+row.item.cases / (+row.item.saleItem.item.ti * +row.item.saleItem.item.hi))}}</span>
            </template>
            <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="removeSaleItem(row.item.saleItem.id)">x</b-button>
            </template>
          </b-table>
        </b-col>
      </b-row>
    </div>
    <div v-if="modalVisible">
			<address-modal :address-id="freightAddress.id" v-on:closeModal="closeModal"></address-modal>
		</div>
  </b-container>
</template>

<script>
import http from "../http-common";
import httpUtils from "../httpUtils";
import router from "../router";
import moment from "moment";

export default {
  components: {
    AddressModal: () => import("./AddressModal")
  },
  data() {
    return {
      modalVisible: false,
      shipment: { 
        shipmentItems: [],
        date: moment().format("YYYY-MM-DD"),
      },
      availableCustomers: [],
      availableShippingAddresses: [],
      availableFreightAddresses: [],
      freightAddress: {},
      customer: {},
      shippingAddresses: [],
      shippingAddress: {},
      availableSales: [],
      sale: {},
      availableSaleItems: [],
      saleItem: {},
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item", label: "Item", sortable: false },
        { key: "sale", label: "Sale", sortable: false },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "saleItem.unitsOnStock", label: "Stock", sortable: false },
        { key: "saleItem.unitsShipped", label: "Shipped", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "saleItem.item.casePack", label: "Case Pack", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "pallets", label: "Pallets", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ]
    };
  },
  computed: {
    totalUnits() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.units;
      });
      return total;
    },
    totalCases() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.cases;
      });
      return total;
    },
    totalPallets() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.pallets;
      });
      return total;
    },
    totalWeight() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.saleItem.item.weight;
      });
      return total * +this.totalUnits;
    }
  },
  watch: {
    customer(new_value, old_value) {
      if (!new_value.id || new_value.id == old_value.id) {
        return;
      }
      this.getAvailableSaleItems();
      this.getAvailableShippingAddresses(new_value.id)
    },
  },
  methods: {
    openModal(){
			this.modalVisible = true;
		},
		closeModal(address){
      this.getAvailableFreightAddresses();
      if(address){
        this.freightAddress = address;
      }
			this.modalVisible=false;
		},
    getShipment(id) {
      http.get("/shipment/" + id).then(response => {
        response.data.shipmentItems.forEach(si => {
          si.existingUnits = si.units;
        });
        this.shipment = response.data;
        if (response.data.customer) {
          this.customer = response.data.customer;
        }
        if (response.data.shippingAddress) {
          this.shippingAddress = response.data.shippingAddress;
        }
        if (response.data.freightAddress){
          this.freightAddress = response.data.freightAddress;
        }
      })
      .catch(e => {
        console.log("API error: " + e);
      });
    },
    saveShipment() {
      this.shipment.customer = this.customer.id?{id: this.customer.id}:null;
      this.shipment.shippingAddress = this.shippingAddress.id?{ id: this.shippingAddress.id }:null;
      this.shipment.freightAddress = this.freightAddress.id?{ id: this.freightAddress.id }:null;
      this.shipment.totalUnits = this.totalUnits;
      this.shipment.totalCases = this.totalCases;
      this.shipment.totalPallets = this.totalPallets;
      this.shipment.totalWeight = this.totalWeight;
      return http.post("/shipment", this.shipment).then(r => {
        //This is to replace "new" with ID from url.
        if(!this.shipment.id){
          router.push("/shipmentEdit/"+r.data.id);
        }else{
          this.shipment = r.data;
        }
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
    getSaleItems(saleItemIds){
      if(!saleItemIds){
        return;
      }
      return http.get("/saleItem", {params: {ids: saleItemIds}}).then(r => {
        this.customer = {id: r.data[0].sale.customer.id}
        r.data.forEach(saleItem => {
          this.addSaleItem(saleItem);
        })
      }).catch(e => {
        console.log("API error: " + e); 
      })
    },
    getAvailableCustomers() {
      return http
        .get("/customer/kv")
        .then(response => {
          this.availableCustomers = response.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableFreightAddresses() {
      this.availableFreightAddresses = [];
      return http.get("/address/type/frg").then(r => {
        this.availableFreightAddresses = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableShippingAddresses(customer_id) {
      return http.get("/address/customer/"+customer_id).then(r => {
        this.availableShippingAddresses = r.data;
      })
    },
    getAvailableSaleItems() {
      return http
        .get("/saleItem/kv/customer/" + this.customer.id)
        .then(response => {
          this.availableSaleItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addSaleItemKv(saleItemId){
      if(!saleItemId){
        alert("Please select Sale and Item to add");
        return;
      }
      var idx = this.shipment.shipmentItems.findIndex(shipmentItem => shipmentItem.saleItem.id == saleItemId);
      if(idx > -1){
        alert("Sale (Item) already added to the list");
        return;
      }
      http.get("/saleItem/"+saleItemId).then(r => {
        this.addSaleItem(r.data);
        this.sale = {};
        this.saleItem = {};
      }).catch(e => {
        console.log("API error: " + e);
      })
    },
    addSaleItem(saleItem){
      this.shipment.shipmentItems.push(
        {
          shipment: {id: this.shipment.id},
          saleItem: saleItem,
          units: saleItem.unitsOnStock,
          cases: 0,
          pallets: 0
        }
      )
    },
    removeSaleItem(saleItemId) {
      var idx = this.shipment.shipmentItems.findIndex(shipItem => shipItem.saleItem.id == saleItemId);
      this.shipment.shipmentItems.splice(idx, 1)
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
    openPdf(){
      this.saveShipment().then(r=>{
        var url = httpUtils.baseUrl + "/shipment/" + this.shipment.id + "/pdf";
        window.open(url, "_blank","")
      })
    },
    unitsBlur(si){
        if(si.units > si.saleItem.item.unitsOnStock){
            alert("Cannot ship more that on stock");
            si.units = 0;
        }
    }
  },
  mounted() {
    var id = this.$route.params.shipment_id;
    var saleItemIds = this.$route.query.saleItemIds;
    window.history.replaceState({}, document.title, window.location.pathname);
    if(id!="new"){
      this.getShipment(id);
    }
    this.getAvailableCustomers();
    this.getAvailableFreightAddresses();
    this.getSaleItems(saleItemIds);
  }
};
</script>

<style>
</style>
