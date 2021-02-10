<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
          <span style="text-align: left; font-size: 18px; font-weight: bold">Customers</span>
      </b-col>
      <b-col cols="3">
          <input class="form-control" type="tel" v-model="customerName" @keyup.enter="getCustomers()" placeholder="Search Name"/>
      </b-col>
      <b-col>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goTo('')">New Customer</b-button>
          </div>
      </b-col>
    </b-row>
    <b-table :items="customers" :fields="fields" no-local-sorting>
      <template v-slot:cell(name)="row">
          <b-link role="button" @click.stop="goTo(row.item.id)">{{row.item.name}}</b-link>
      </template>
      <template v-slot:cell(unitsSoldShipped)="row">
          <span>{{row.item.unitsSold}}/{{row.item.unitsShipped}}</span>
      </template>
      <template v-slot:cell(action)="row">
          <b-button size="sm" @click.stop="remove(row.item.id)">x</b-button>
      </template>
    </b-table>
    <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "CustomerList",
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'name', sortDesc: false},
      customerName: "",
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "addressName", label: "Address", sortable: false },
        { key: "phone", label: "Phone", sortable: false },
        // { key: "unitsSoldShipped", label: "Sold/Shipped", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      customers: []
    };
  },
  methods: {
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getCustomers();
    },
    getCustomers() {
      http
        .get("/customer/pageable", {params: {pageable: this.pageable, customerName: this.customerName}})
        .then(response => {
          this.customers = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        });
    },
    remove(id) {
      http
        .delete("/customer/" + id)
        .then(() => {
          this.getCustomers();
        });
    },
    goTo(id) {
      if (id) {
        router.push("/customerEdit/" + id);
      } else {
        router.push("/customerEdit");
      }
    }
  },
  mounted() {
    // this.getCustomers();
  },
  activated(){
    this.getCustomers();
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
