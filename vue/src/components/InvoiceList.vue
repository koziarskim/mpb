<template>
    <b-container fluid>
      <b-row style="font-size: 12px">
        <b-col cols=1 offset=11>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" size="sm" @click="goToInvoice()">New</b-button>
          </div>
        </b-col>
      </b-row>
      <b-table :items="invoices" :fields="fields" no-local-sorting>
        <template v-slot:cell(number)="row">
          <b-button size="sm" @click="goToInvoice(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
      </b-table>
      <div style="display: flex">
		    <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
        <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
      </div>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"
import navigation from "../utils/navigation";

export default {
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'date', sortDesc: false},
      fields: [
        { key: "number", label: "Invoice #", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      invoices: [] //InvoiceListDto
    };
  },
  watch: {
  },
  methods: {
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getInvoices();
    },
	  getInvoices() {
      var query = {params: {
        pageable: this.pageable
      }}
      http.get("/invoice/pageable", query).then(r => {
        this.invoices = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    goToInvoice(id){
      router.push('/invoiceEdit/'+id?id:'');
    },
  },
  mounted() {
    this.getInvoices();
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
