<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Suppliers</span>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="searchKey" @keyup.enter="getSuppliers" placeholder="Search by Name"/>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSupplier('')">New Supplier</b-button>
                </div>
            </b-col>
        </b-row>
        <div v-if="suppliers.length==0">Not found any data...</div>
        <b-table v-if="suppliers.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="suppliers"
                :fields="fields">
                <template v-slot:cell(account)="row">
                    <b-button size="sm" @click.stop="goToSupplier(row.item.id)" variant="link">{{row.item.account}}</b-button>
                </template>
                <template v-slot:cell(action)="row">
                    <b-button size="sm" @click.stop="deleteSupplier(row.item.id)">x</b-button>
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
      searchKey: "",
      sortBy: "account",
      sortDesc: false,
      fields: [
        { key: "account", label: "Account", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "city", label: "City", sortable: false },
        { key: "phone", label: "Phone", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      suppliers: []
    };
  },
  methods: {
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSuppliers();
    },
    getSuppliers() {
      http
        .get("/supplier/pageable", {params: {pageable: this.pageable, searchKey: this.searchKey}})
        .then(response => {
          this.suppliers = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteSupplier(id) {
      http
        .delete("/supplier/" + id)
        .then(() => {
          this.getSuppliers();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    },
    goToSupplier(id) {
      if (id) {
        router.push("/supplierEdit/" + id);
      } else {
        http
          .post("/supplier")
          .then(response => {
            router.push("/supplierEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      }
    }
  },
  mounted() {
    this.getSuppliers();
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
