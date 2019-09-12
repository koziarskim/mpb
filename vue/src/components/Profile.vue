<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <h2 style="text-align: left;">User Profile</h2>
      <div style="text-align: right;">
        <b-button type="submit" variant="success" @click="save()">Save</b-button>
      </div>
    </div>
    <b-row>
      <b-col cols="2">
        <label class="top-label">Account:</label>
        <input class="form-control" disabled=true type="text" v-model="user.number" placeholder="Account">
      </b-col>
      <b-col cols="3">
        <label class="top-label">First Name:</label>
        <input class="form-control" type="text" v-model="user.firstName" placeholder="First Name">
      </b-col>
      <b-col cols="3">
        <label class="top-label">Last Name:</label>
        <input class="form-control" type="text" v-model="user.lastName" placeholder="Last Name">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="2">
        <label class="top-label">Username:</label>
        <input class="form-control" type="tel" v-model="user.username" placeholder="Username">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Password:</label>
        <input class="form-control" type="password" v-model="user.password" placeholder="Password">
      </b-col>
    </b-row>
    <br>
    <div v-for="role in user.roles" v-bind:key="role.id">
      <b-row>
        <b-col>
          <b-form-checkbox :disabled=true checked=true>{{role.name}} - {{role.description}}</b-form-checkbox>
        </b-col>
      </b-row>
    </div>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"

export default {
  data() {
    return {
      user: {},
    };
  },
  methods: {
    getUser(){
      this.user = securite.getUser();
    },
    save() {
      return http
        .post("/user", this.user)
        .then(response => {
          this.$store.dispatch("changeUser", response.data);
          this.getUser();
          return response;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
  },
  mounted() {
    this.getUser();
  }
};
</script>

<style>
</style>
