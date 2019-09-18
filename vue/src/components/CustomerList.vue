<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <span style="text-align: left; font-size: 18px; font-weight: bold">Customers</span>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goTo('')">New Customer</b-button>
            </div>
        </div>
        <div v-if="customers.length==0">Not found any data...</div>
        <b-table v-if="customers.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="customers"
                :fields="fields">
                <template v-slot:cell(account)="row">
                    <b-button size="sm" @click.stop="goTo(row.item.id)" variant="link">{{row.item.id}}</b-button>
                </template>
                <template v-slot:cell(action)="row">
                    <b-button size="sm" @click.stop="remove(row.item.id)">x</b-button>
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
        { key: "account", label: "Account", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "phone", label: "Phone", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      customers: []
    };
  },
  methods: {
    getCustomers() {
      http
        .get("/customer")
        .then(response => {
          this.customers = response.data;
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
</style>
