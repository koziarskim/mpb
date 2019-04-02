<template>
  <b-container fluid>
    <div style="border: 0px" class="d-flex justify-content-between align-items-center">
      <h4 style="text-align: left;">Receiving</h4>
      <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
      </div>
    </div>
    <b-row>
      <b-col cols="2">
        <label class="top-label">Purchase</label>
        <b-select option-value="id" option-text="number" :list="availablePurchases" v-model="purchase"></b-select>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Component</label>
        <b-select option-value="id" option-text="number" :list="availableComponents" v-model="component"></b-select>
      </b-col>
      <b-col cols="2">
        <label class="top-label">Number:</label>
        <input class="form-control" type="text" v-model="receiving.number" placeholder="Number">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="2">
        <label class="top-label">Units:</label>
        <input class="form-control" type="text" v-model="receiving.units" placeholder="Units">
      </b-col>
      <b-col cols="2">
        <label class="top-label">Reference:</label>
        <input class="form-control" type="text" v-model="receiving.reference" placeholder="Reference">
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
      receiving: {},
      purchase: {},
      component: {},
      availablePurchases: [],
      availableComponents: []
    };
  },
  computed: {},
  watch: {},
  methods: {
    getReceiving(receiving_id) {
      return http
        .get("/receiving/" + receiving_id)
        .then(response => {
          this.receiving = response.data;
          if (response.data.purchase) {
            this.purchase = this.availablePurchases.filter(
              it => it.id == response.data.purchase.id
            )[0];
          }
          if (response.data.component) {
            this.component = this.availableComponents.filter(
              it => it.id == response.data.component.id
            )[0];
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      this.receiving.purchase = this.purchase;
      this.receiving.component = this.component;
      return http
        .post("/receiving", this.receiving)
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
    getAvailablePurchases() {
      return http
        .get("/purchase/submitted")
        .then(response => {
          this.availablePurchases = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableComponents() {
      return http
        .get("/component/")
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    }
  },
  mounted() {
    var receiving_id = this.$route.params.receiving_id;
    this.getAvailablePurchases().then(r => {
      this.getAvailableComponents().then(r => {
        if (receiving_id) {
          this.getReceiving(receiving_id);
        }
      });
    });
  }
};
</script>

<style>
</style>
