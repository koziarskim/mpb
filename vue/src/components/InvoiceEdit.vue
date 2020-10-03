<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Invoice Number:</label>
        <input class="form-control" type="tel" v-model="invoice.number">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Invoice Date:</label>
        <input class="form-control" type="date" v-model="invoice.date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Customers:</label><br/>
        <b-link role="button" @click="goToCustomer(invoice.shipment.customer.id)">{{invoice.shipment.customer.name}}</b-link>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipments:</label><br/>
        <b-link role="button" @click="goToShipment(invoice.shipment.id)">{{invoice.shipment.number}}</b-link>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Invoice Type:</label><br/>
        <span>{{invoice.type}}</span>
      </b-col>
      <b-col cols=2 style="text-align: right; margin-top: 20px">
        <label class="top-label">Sent</label>
        <input type="checkbox" style="margin-left: 3px;" v-model="invoice.sent">
        <a :href="getPdfUrl(invoice.id)" target="_blank" style="margin-left: 10px;">
          <img src="../assets/pdf-download.png" width="23px">
        </a>
        <b-button style="margin-left: 5px" size="sm" variant="success" @click="saveInvoice(false)">Save</b-button>
        <b-button style="margin-left: 3px" size="sm" @click="deleteInvoice()">x</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Shipping Date:</label>
        <input class="form-control" type="date" v-model="invoice.shippingDate">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Pay Terms:</label>
        <input class="form-control" type="tel" v-model="invoice.paymentTerms">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Load Number:</label>
        <input class="form-control" type="tel" v-model="invoice.loadNumber">
      </b-col>      
      <b-col cols=3>
        <label class="top-label">Billing Address:</label><br/>
        <span>{{invoice.shipment.customer.billingAddress?invoice.shipment.customer.billingAddress.line:''}}</span><br/>
        <span>{{invoice.shipment.customer.billingAddress?invoice.shipment.customer.billingAddress.street:''}}</span><br/>
        <span>{{invoice.shipment.customer.billingAddress?(invoice.shipment.customer.billingAddress.city+', '+invoice.shipment.customer.billingAddress.state+' '+invoice.shipment.customer.billingAddress.zip):''}}</span>
      </b-col>
      <b-col cols=3>
        <label class="top-label">Shipping Address:</label><br/>
        <span>{{invoice.shipment.shippingAddress?invoice.shipment.shippingAddress.dc:''}}</span><br/>
        <span>{{invoice.shipment.shippingAddress?invoice.shipment.shippingAddress.street:''}}</span><br/>
        <span>{{invoice.shipment.shippingAddress?(invoice.shipment.shippingAddress.city+', '+invoice.shipment.shippingAddress.state+' '+invoice.shipment.shippingAddress.zip):''}}</span>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Via:</label>
        <input class="form-control" type="tel" v-model="invoice.via">
      </b-col>
      <b-col cols=2>
        <label class="top-label">FOB:</label>
        <input class="form-control" type="tel" v-model="invoice.fob">
      </b-col>
      <b-col cols=3>
        <label class="top-label">Invoice Send Email:</label>
        <input class="form-control" type="tel" v-model="invoice.invoiceEmail">
      </b-col>
      <b-col cols=1>
        <label class="top-label" style="margin-top: 15px">CC</label><br/>
        <input type="checkbox" v-model="includeCc">
      </b-col>
      <b-col cols=1>
        <b-button style="margin-top: 25px; margin-left: -80px" size="sm" variant="success" @click="saveInvoice(true)">Send</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Total:</label><br/>
        <b>${{invoiceTotal.toLocaleString('en-US',{minimumFractionDigits: 2})}}</b>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Cost:</label>
        <input class="form-control" type="tel" v-model="invoice.shippingCost">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Payments:</label>
        <input class="form-control" type="tel" v-model="invoice.payments">
      </b-col>      
      <b-col cols=2>
        <label class="top-label">Balance Due:</label><br/>
        <b>${{balanceDue.toLocaleString('en-US',{minimumFractionDigits: 2})}}</b>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table :items="invoice.invoiceItems" :fields="columns">
          <template v-slot:cell(sale)="row">
            <b-link role="button" @click="goToSale(row.item.saleItem.sale)">{{row.item.saleItem.sale.number}}</b-link>
          </template>
          <template v-slot:cell(item)="row">
            <b-link role="button" @click="goToItem(row.item.saleItem.item)">{{row.item.saleItem.item.number}}</b-link>
          </template>
          <template v-slot:cell(unitsInvoiced)="row">
            <input class="form-control" style="width:100px" type="tel" v-model="row.item.unitsInvoiced">
          </template>
          <template v-slot:cell(unitPrice)="row">
            <input class="form-control" style="width:100px" type="tel" v-model="row.item.unitPrice">
          </template>          
          <template v-slot:cell(totalUnitPrice)="row">
            <span>${{getTotalUnitPrice(row.item)}}</span>
          </template>          
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click="deleteInvoiceItem(row.item)">x</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import httpUtils from "../httpUtils";
import router from "../router";
import securite from "../securite";
import moment from "moment";

export default {
  data() {
    return {
      securite: securite,
      modalVisible: false,
      invoice: {
        invoiceItems: [],
        shipment: {
          customer: {}
        },
        billingAddress: {},
        shippingAddress: {},
      },
      columns: [
        { key: "sale", label: "Sale", sortable: false },
        { key: "item", label: "Item", sortable: false },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "saleItem.unitsShipped", label: "Shipped", sortable: false },
        { key: "unitsInvoiced", label: "Units", sortable: false },
        { key: "unitPrice", label: "Price", sortable: false },
        { key: "totalUnitPrice", label: "Total", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availableSaleItems: [],
      saleItemKv: {},
      saleItem: {},
      availableCustomers: [],
      customerKv: {},
      availableShipments: [],
      shipmentKv: {},
      availableShipmentItems: [],
      shipmentItemKv: {},
      includeCc: true
    }
  },
  computed: {
    invoiceTotal(){
      var total = 0;
      this.invoice.invoiceItems.forEach(ii=> {
        total += ii.totalUnitPrice;
      })
      return total;;
    },
    balanceDue(){
      var total = 0;
      total = parseFloat(this.invoiceTotal) + parseFloat(this.invoice.shippingCost) - parseFloat(this.invoice.payments);
      return total;
    }
  },
  watch: {
    customerKv(newValue, oldValue){
      this.getAvailableShipments(newValue.id);
      this.getAvailableSaleItems(newValue.id);
    },
    shipmentKv(newValue, oldValue){
      this.getAvailableShipmentItems(newValue.id);
    },
    saleItemKv(newValue, oldValue){
      this.getSaleItem(newValue.id);
    },
    shipmentItemKv(newValue, oldValue){
      this.getShipmentItem(newValue.id);
    }
  },
  methods: {
    getPdfUrl(invoiceId) {
      return httpUtils.getUrl("/invoice/" + invoiceId + "/pdf");
    },
    getTotalUnitPrice(invoiceItem){
      invoiceItem.totalUnitPrice = +invoiceItem.unitPrice * +invoiceItem.unitsInvoiced;
      return invoiceItem.totalUnitPrice.toLocaleString('en-US',{minimumFractionDigits: 2});;
    },
    deleteInvoiceItem(invoiceItem) {
      var idx = this.invoice.invoiceItems.findIndex(ii => ii.id == invoiceItem.id);
      this.invoice.invoiceItems.splice(idx, 1);
	  },
    getAvailableCustomers() {
      return http.get("/customer/kv").then(r => {
        this.availableCustomers = r.data;
        return r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableShipments(customerId) {
      return http.get("/shipment/kv/customer/"+customerId).then(r => {
        this.availableShipments = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableSaleItems(customerId) {
      return http.get("/saleItem/kv/customer/"+customerId).then(r => {
        this.availableSaleItems = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableShipmentItems(shipmentId) {
      return http.get("/shipmentItem/kv/shipment/"+shipmentId).then(r => {
        this.availableShipmentItems = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getSaleItem(saleItemId) {
      return http.get("/saleItem/" + saleItemId).then(r => {
        this.saleItem = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getShipmentItem(shipmentItemId) {
      return http.get("/shipmentItem/" + shipmentItemId).then(r => {
        this.shipmentItem = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    addSaleItem() {
      if (!this.saleItem.id) {
        return;
      }
      var invoiceItem = this.invoice.invoiceItems.find(it => it.saleItem.id == this.saleItem.id);
      if (invoiceItem) {
        return;
      }
      this.invoice.invoiceItems.push({ 
          unitsInvoiced: this.saleItem.unitsShipped,
          unitPrice: this.saleItem.unitPrice,
          totalUnitPrice: this.saleItem.totalUnitPrice,
          saleItem: this.saleItem,
      });
    },
    saveInvoice(sendEmail) {
      if(sendEmail && !this.invoice.invoiceEmail){
        //TODO: Verify invoiceEmail format;
        alert("A-P Email is wrong!");
        return false;
      }
      this.invoice.balanceDue = this.balanceDue;
      var query = {params: {sendEmail: sendEmail, includeCc: this.includeCc}}
      return http.post("/invoice/", this.invoice, query).then(r => {
        this.invoice = r.data;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    deleteInvoice() {
      http.delete("/invoice/"+this.invoice.id).then(r => {
        router.push({path: "/invoiceList"});
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    goToSale(sale){
      router.push({path: "/saleEdit/"+sale.id})
    },
    goToItem(item){
      router.push({path: "/itemEdit/"+item.id})
    },
    goToCustomer(customerId){
      router.push({path: "/customerEdit/"+customerId})
    },
    goToShipment(shipmentId){
      router.push({path: "/shipmentEdit/"+shipmentId})
    },
    getInvoice(invoiceId) {
      return http.get("/invoice/" + invoiceId).then(r => {
        this.invoice = r.data;
        if(!this.invoice.invoiceEmail){
          this.invoice.invoiceEmail = r.data.shipment.customer.invoiceEmail;
        }
        if(!this.invoice.shippingCost){
          this.invoice.shippingCost = 0;
        }
        if(!this.invoice.payments){
          this.invoice.payments = 0;
        }
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
  },
  mounted() {
    var invoiceId = this.$route.params.invoiceId;
    if (invoiceId) {
      this.getInvoice(invoiceId);
    }
    this.getAvailableCustomers();
  }   
};
</script>

<style>
</style>
