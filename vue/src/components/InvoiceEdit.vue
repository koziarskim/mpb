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
    </b-row>
    <hr class="hr-text" data-content="Sales/Items">
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
        sale: {},
        shipment: {}
      },
      columns: [
        { key: "sale", label: "Sale", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
    }
  },
  computed: {
  },
  watch: {
  },
  methods: {
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
  }   
};
</script>

<style>
</style>
