<template>
  <b-container fluid>
    <br>
    <b-row>
      <b-col cols="3" offset="5">
        <div>Access Denied!!!</div>
      </b-col>
    </b-row>
    <br>
    <b-row>
      <b-col cols="3" offset="5">
        <div>Return to
          <a href="#" @click="goToHome()">Home</a>
        </div>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";

export default {
  data() {
    return {
      user: {}
    };
  },
  computed: {},
  watch: {},
  methods: {
    goToHome() {
      router.push("/Login");
    }
  },
  mounted() {}
};
</script>

<style>
</style>
