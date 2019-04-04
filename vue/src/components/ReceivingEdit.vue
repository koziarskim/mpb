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
        <b-select option-value="id" option-text="componentNumber" :list="availablePurchaseComponents" v-model="purchaseComponent"></b-select>
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
      purchaseComponent: {},
      availablePurchases: [],
      availablePurchaseComponents: []
    };
  },
  computed: {},
  watch: {
    purchase(new_value, old_value) {
      this.getAvailablePurchaseComponents(new_value.id);
    }
  },
  methods: {
    getReceiving(receiving_id) {
      return http
        .get("/receiving/" + receiving_id)
        .then(response => {
          this.receiving = response.data;
          if (response.data.purchaseComponent && response.data.purchaseComponent.purchase) {
            this.purchase = this.availablePurchases.filter(
              it => it.id == response.data.purchaseComponent.purchase.id
            )[0];
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    save() {
      this.receiving.purchaseComponent = this.purchaseComponent;
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
    getAvailablePurchaseComponents(purchase_id) {
      return http
        .get("/purchaseComponent/purchase/" + this.purchase.id)
        .then(response => {
          this.availablePurchaseComponents = response.data;
          this.purchaseComponent = response.data.filter(
            it => it.id == this.receiving.purchaseComponent.id
          )[0];
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    }
  },
  mounted() {
    var receiving_id = this.$route.params.receiving_id;
    this.getAvailablePurchases().then(r => {
      if (receiving_id) {
        this.getReceiving(receiving_id);
      }
    });
  }
};
</script>

<style>
</style>
