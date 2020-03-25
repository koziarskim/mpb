<template>
  <b-container fluid>
    <b-row>
      <b-col cols=4>
        <label class="top-label">Invoice Number:</label>
        <input class="form-control" type="tel" v-model="invoice.number">
      </b-col>
      <b-col cols=4>
        <label class="top-label">Invoice Date:</label>
        <input class="form-control" type="date" v-model="invoice.date">
      </b-col>
      <b-col cols=2>
        <b-button size="sm" variant="success" @click="saveInvoice()">Save</b-button>
        <b-button style="margin-left: 3px" size="sm" @click="deleteInvoice()">x</b-button>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Sales/Items">
    <b-row>
      <b-col cols=4>
        <label class="top-label">Available Sales:</label>
        <b-select option-value="id" option-text="name" :list="availableSaleItems" v-model="saleItemKv"></b-select>
      </b-col>
      <b-col cols=1 style="padding-top: 30px">
        <b-button variant="link" @click="addInvoiceItem()">(+)</b-button>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table :items="invoice.invoiceItems" :fields="columns">
          <template v-slot:cell(sale)="row">
            <b-link role="button" @click="goToSale(row.item.saleItem.sale)">{{row.item.saleItem.sale.number}}</b-link>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
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
      },
      columns: [
        { key: "sale", label: "Sale", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availableSaleItems: [],
      saleItemKv: {},
      saleItem: {}
    }
  },
  computed: {
  },
  watch: {
    saleItemKv(newValue, oldValue){
      this.getSaleItem(newValue.id);
    }
  },
  methods: {
    getAvailableSaleItems() {
      return http.get("/saleItem/kv").then(r => {
        this.availableSaleItems = r.data;
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
    addInvoiceItem() {
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
    saveInvoice() {
      return http.post("/invoice/", this.invoice).then(r => {
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
    getInvoice(invoiceId) {
      return http.get("/invoice/" + invoiceId).then(r => {
        this.invoice = r.data;
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
    this.getAvailableSaleItems();
  }   
};
</script>

<style>
</style>
