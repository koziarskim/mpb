<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <h2 style="text-align: left;">Users</h2>
      <div style="text-align: right;">
        <b-button v-if="!editMode" type="submit" variant="primary" @click="createUser()">New User</b-button>
        <b-button v-if="editMode" type="reset" variant="success" @click="saveAndClose()">Save & Close</b-button>
      </div>
    </div>
    <b-row>
      <b-col cols="5">
        <b-table v-if="users.length>0" sticky-header :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="users" :fields="fields">
          <template v-slot:cell(number)="row">
            <b-button size="sm" @click.stop="editUser(row.item)" variant="link">{{row.item.number}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteUser(row.item.id)">x</b-button>
          </template>
        </b-table>
      </b-col>
      <b-col cols="7">
        <div v-if="editMode">
          <b-row>
            <b-col cols="4">
              <label class="top-label">Account:</label>
              <input class="form-control" type="text" v-model="user.number" placeholder="Account">
            </b-col>
            <b-col cols="4">
              <label class="top-label">First Name:</label>
              <input class="form-control" type="text" v-model="user.firstName" placeholder="First Name">
            </b-col>
            <b-col cols="4">
              <label class="top-label">Last Name:</label>
              <input class="form-control" type="text" v-model="user.lastName" placeholder="Last Name">
            </b-col>
          </b-row>
          <b-row>
            <b-col cols="4">
              <label class="top-label">Username:</label>
              <input class="form-control" type="text" v-model="user.username" placeholder="Username">
            </b-col>
            <b-col cols="4">
              <label class="top-label">Password:</label>
              <input class="form-control" type="password" v-model="user.password" placeholder="Password">
            </b-col>
          </b-row>
          <br>
          <div v-for="role in availableRoles" v-bind:key="role.id">
            <b-row>
              <b-col>
                <b-form-checkbox v-model="role.selected">{{role.name}} - {{role.description}}</b-form-checkbox>
              </b-col>
            </b-row>
          </div>
        </div>
      </b-col>
    </b-row>
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
        { key: "number", label: "Account", sortable: false },
        { key: "fullName", label: "Name", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      users: [],
      availableRoles: [],
      user: {},
      editMode: false
    };
  },
  methods: {
    getUsers() {
      http
        .get("/user")
        .then(response => {
          this.users = response.data;
          if (response.data.length == 0) {
            this.createUser();
          }
          this.getAvailableRoles();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      this.user.roles = [];
      this.availableRoles.forEach(role => {
        if (role.selected) {
          this.user.roles.push(role);
        }
      });
      return http
        .post("/user", this.user)
        .then(response => {
          this.getUsers();
          if (this.user.id == this.$store.getters.userContext.user.id) {
            this.$store.dispatch("changeUser", response.data);
          }
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.save().then(r => {
        this.editMode = false;
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
    createUser() {
      return http
        .post("/user")
        .then(response => {
          this.editUser(response.data);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    editUser(user) {
      this.user = user;
      if (this.user.roles) {
        this.availableRoles.forEach(role => {
          role.selected = false;
          var userRole = this.user.roles.find(r => r.id == role.id);
          if (userRole) {
            role.selected = true;
          }
        });
      }
      this.editMode = true;
    },
    getAvailableRoles() {
      http
        .get("/role")
        .then(response => {
          this.availableRoles = response.data.sort(function(a, b){
				    return a.id - b.id;
			    });
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    isSelected(role_id) {
      return false;
    }
  },
  mounted() {
    this.getUsers();
  }
};
</script>

<style>
</style>
