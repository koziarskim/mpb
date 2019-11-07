<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Shipment: </span>
        <input class="form-control" type="tel" v-model="shipment.number">
      </b-col>
      <b-col cols="3">
        <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Select Customer"></b-select>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="date" v-model="shipment.date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="reset" variant="primary" @click="save()">Save</b-button>
          <img @click="openPdf()" style="margin: 2px;" src="../assets/pdf-download.png" width="25px">
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3">
        <label class="top-label">Ship To:</label>
        <b-select option-value="id" option-text="name" :list="availableShippingAddresses" v-model="shippingAddress" placeholder="Pick shipping address"></b-select>
      </b-col>
      <b-col cols="3">
        <label class="top-label">Freight Address:<a href="#" @click="openModal()"> (Edit/New) </a></label>
        <b-select option-value="id" option-text="name" :list="availableFreightAddresses" v-model="freightAddress" placeholder="Pick freight address"></b-select>
      </b-col>
      <b-col>
        <b-form-textarea type="text" :rows="3" v-model="shipment.notes" placeholder="Special Notes"></b-form-textarea>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="2">
        <label class="top-label">P.O. No.:</label>
        <br>
        <input class="form-control" type="tel" v-model="shipment.poNumber">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Ship Date:</label>
        <br>
        <input class="form-control" type="date" v-model="shipment.shippingDate">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Via:</label>
        <br>
        <input class="form-control" type="tel" v-model="shipment.via">
      </b-col>
      <b-col cols="2">
        <label class="top-label">FOB:</label>
        <br>
        <input class="form-control" type="tel" v-model="shipment.fob">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Freight Class</label>
        <br>
        <input class="form-control" type="tel" v-model="shipment.freight">
      </b-col>
      <b-col cols="2">
        <label class="top-label">CS No.:</label>
        <br>
        <input class="form-control" type="tel" v-model="shipment.csNumber">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3">
        <label class="top-label">Sale:</label>
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="sale"></b-select>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Item:</label>
        <b-select option-value="id" option-text="label" :list="availableSaleItems" v-model="saleItem"></b-select>
      </b-col>
      <b-col cols="1">
        <b-button style="padding-top: 30px; padding-left: 0px" variant="link" @click="addItem()">(+)</b-button>
      </b-col>
      <b-col>
        <br>
        <b>Total units:</b>
        {{totalUnits}},
        <b>Total cases:</b>
        {{totalCases}},
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
            <b-button size="sm" @click.stop="deleteItem(row.item.id)">x</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
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
      this.getAvailableShippingAddresses(new_value.id)
    },
    sale(new_value, old_value) {
      if (new_value.id != old_value.id) {
        this.availableSaleItems = [];
        new_value.saleItems.forEach(si => {
          si.label = si.item.number;
          this.availableSaleItems.push(si);
        });
      }
    }
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
      http
        .get("/shipment/" + id)
        .then(response => {
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
    saveShipmentItem(shipmentItem) {
      return http
        .post("/shipmentItem", shipmentItem)
        .then(response => {
          return Promise.resolve();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      this.saveShipment()
    },
    getSaleItems(saleItemIds){
      if(!saleItemIds){
        return;
      }
      return http.get("/saleItem", {params: {ids: saleItemIds}}).then(r => {
        this.customer = {id: r.data[0].sale.customer.id}
        r.data.forEach(saleItem => {
          this.shipment.shipmentItems.push(
            {
              shipment: {id: this.shipment.id},
              saleItem: saleItem,
              units: saleItem.unitsOnStock,
              cases: 0,
              pallets: 0
            }
          )
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
    getAvailableSales() {
      return http
        .get("/sale/customer/" + this.customer.id)
        .then(response => {
          this.availableSales = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    addItem() {
      this.saveShipment().then(r => {
        var shipmentItem = {
          shipment: { id: this.shipment.id },
          saleItem: { id: this.saleItem.id }
        };
        http
          .post("/shipmentItem", shipmentItem)
          .then(response => {
            this.getShipment(this.shipment.id);
          })
          .catch(e => {
            console.log("API error: " + e);
          });
      });
    },
    deleteItem(shipmentItemId) {
      http
        .delete("/shipmentItem/" + shipmentItemId)
        .then(response => {
          this.getShipment(this.shipment.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
    formatDate(date) {
      return date
        ? moment(date)
            .utc()
            .format("YYYY-MM-DD")
        : "";
    },
    submitShipment() {
      if (this.shipment.shipmentItems.length == 0) {
        alert("No Items selected!!!");
        return Promise.resolve();
      }
      this.shipment.submitted = true;
      this.saveShipment();
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
