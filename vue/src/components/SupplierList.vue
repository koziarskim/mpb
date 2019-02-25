<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2 style="text-align: left;">Suppliers</h2>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToSupplier('')">New Supplier</b-button>
            </div>
        </div>
        <div v-if="suppliers.length==0">Not found any data...</div>
        <b-table v-if="suppliers.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="suppliers"
                :fields="fields">
                <template slot="account" slot-scope="row">
                    <b-button size="sm" @click.stop="goToSupplier(row.item.id)" variant="link">{{row.item.id}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteSupplier(row.item.id)">x</b-button>
                </template>
        </b-table>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      sortBy: "account",
      sortDesc: false,
      fields: [
        { key: "account", label: "Account", sortable: true },
        { key: "name", label: "Name", sortable: true },
        { key: "city", label: "City", sortable: true },
        { key: "phone", label: "Phone", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      suppliers: []
    };
  },
  methods: {
    getSuppliers() {
      http
        .get("/supplier")
        .then(response => {
          this.suppliers = response.data;
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
</style>
