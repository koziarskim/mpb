<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2 style="text-align: left;">Users</h2>
            <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="goToUser('')">New User</b-button>
            </div>
        </div>
        <div v-if="users.length==0">Not found any data...</div>
        <b-table v-if="users.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="users"
                :fields="fields">
                <template slot="number" slot-scope="row">
                    <b-button size="sm" @click.stop="goToUser(row.item.id)" variant="link">{{row.item.id}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteUser(row.item.id)">x</b-button>
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
      sortBy: "id",
      sortDesc: false,
      fields: [
        { key: "number", label: "Account", sortable: true },
        { key: "fullName", label: "Name", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      users: []
    };
  },
  methods: {
    getUsers() {
      http
        .get("/user")
        .then(response => {
          this.users = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteUser(id) {
      http
        .delete("/user/" + id)
        .then(() => {
          this.getUsers();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    },
    goToUser(id) {
      if (id) {
        router.push("/user/" + id);
      } else {
        http
          .post("/user")
          .then(response => {
            router.push("/user/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      }
    }
  },
  mounted() {
    this.getUsers();
  }
};
</script>

<style>
</style>
