<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
          <span style="text-align: left; font-size: 18px; font-weight: bold">Customers</span>
      </b-col>
      <b-col cols="3">
          <input class="form-control" type="tel" v-model="searchCustomer" @keyup.enter="getCustomers('customer')" placeholder="Search Name"/>
      </b-col>
      <b-col>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goTo('')">New Customer</b-button>
          </div>
      </b-col>
    </b-row>
    <b-table :items="customers" :fields="fields" no-local-sorting>
      <template v-slot:cell(account)="row">
          <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
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
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'name', sortDesc: false},
      searchCustomer: "",
      fields: [
        { key: "account", label: "Account", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "phone", label: "Phone", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      customers: []
    };
  },
  methods: {
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getCustomers();
    },
    getCustomers(type) {
      var searchKey = type=="customer"?this.searchCustomer:"";
      http
        .get("/customer/pageable", {params: {pageable: this.pageable, searchKey: searchKey, searchType: type}})
        .then(response => {
          this.customers = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    remove(id) {
      http
        .delete("/customer/" + id)
        .then(() => {
          this.getCustomers();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    },
    goTo(id) {
      if (id) {
        router.push("/customerEdit/" + id);
      } else {
        http
          .post("/customer")
          .then(response => {
            router.push("/customerEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      }
    }
  },
  mounted() {
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
