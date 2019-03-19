<template>
  <b-container fluid>
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Username:</label>
        <input class="form-control" type="text" v-model="username" placeholder="Username">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="3" offset="4">
        <label class="top-label">Password:</label>
        <input class="form-control" type="password" v-model="password" placeholder="Password">
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
        username: "",
        password: "",
    };
  },
  computed: {},
  watch: {},
  methods: {
    getSupplier(id) {
      http
        .get("/supplier/" + id)
        .then(response => {
          this.supplier = response.data;
          this.freightTerms = this.getFreightById(response.data.freightTerms);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      return http
        .post("/supplier", this.supplier)
        .then(response => {})
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.save().then(r => {
        window.history.back();
      });
    },
    getFreightById(code) {
      var freight = {};
      this.availableFreights.filter(it => {
        if (it.code == code) {
          freight = it;
        }
      });
      return freight;
    }
  },
  mounted() {
    var supplier_id = this.$route.params.supplier_id;
    if (supplier_id) {
      this.getSupplier(supplier_id);
    }
  }
};
</script>

<style>
</style>
