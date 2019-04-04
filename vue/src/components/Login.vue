<template>
  <b-container fluid>
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Username:</label>
        <input class="form-control" type="text" v-model="user.username" placeholder="Username">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Password:</label>
        <input class="form-control" type="password" v-model="user.password" placeholder="Password">
      </b-col>
    </b-row>
    <b-row>
        <b-col cols="2" offset="6">
            <br/>
            <b-button type="submit" variant="success" @click="login()">Login</b-button>
        </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  name: "add-component",
  data() {
    return {
        user: {}
    };
  },
  computed: {},
  watch: {},
  methods: {
    login() {
      return http
        .post("/user/login", this.user)
        .then(response => {
            //TODO: Get page context instead.
            this.$store.dispatch("changeUser", response.data);
            router.push("/home")
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
  },
  mounted() {
  }
};
</script>

<style>
</style>
