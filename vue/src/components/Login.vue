<template>
  <b-container fluid @keyup.enter="login()">
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Username:</label>
        <input class="form-control" type="text" v-model="loginDto.username" placeholder="Username">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Password:</label>
        <input class="form-control" type="password" v-model="loginDto.password" placeholder="Password">
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
import securite from "../securite";
import { EventBus } from '../utils/eventBus.js';

export default {
  name: "add-component",
  data() {
    return {
      securite: securite,
      loginDto: {
        username: "",
        password: "",
      }
    };
  },
  computed: {},
  watch: {},
  methods: {
    login() {
      return http.post("/user/login", this.loginDto).then(response => {
        this.securite.setUser(response.data)
        EventBus.$emit('userChanged', response.data);
        router.push("/home")
      }).catch(e => {
        console.log("API error: " + e);
        alert(e.response.data)
      });
    },
  },
  mounted() {
  }
};
</script>

<style>
</style>
