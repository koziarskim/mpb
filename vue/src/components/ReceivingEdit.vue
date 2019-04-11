<template>
  <b-container fluid>
    <div style="border: 0px" class="d-flex justify-content-between align-items-center">
      <h4 style="text-align: left;">Receiving: {{receiving.number}}</h4>
      <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
      </div>
    </div>
    <b-row>
        <b-col cols=6>
    <b-row>
      <b-col cols="4">
        <label class="top-label">Purchase</label>
        <input class="form-control" type="text" v-model="purchase.number" disabled="true">
      </b-col>
      <b-col cols="4">
        <label class="top-label">Component</label>
        <input class="form-control" type="text" v-model="purchaseComponent.componentNumber" disabled="true">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4">
        <label class="top-label">Shipped Date:</label>
        <input class="form-control" type="date" v-model="receiving.shippedDate" :disabled="editMode">
      </b-col>
      <b-col cols="4">
        <label class="top-label">ETA Date:</label>
        <input class="form-control" type="date" v-model="receiving.etaDate" :disabled="editMode">
      </b-col>
      <b-col cols="4">
        <label class="top-label">Received Date:</label>
        <input class="form-control" type="date" v-model="receiving.receivedDate" :disabled="editMode">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="4">
        <label class="top-label">Units (Shipped/Received):</label>
        <input class="form-control" type="text" v-model="receiving.units" :disabled="editMode" placeholder="Units">
      </b-col>
      <b-col cols="4">
        <label class="top-label">Container:</label>
        <input class="form-control" type="text" v-model="receiving.container" :disabled="editMode" placeholder="Container">
      </b-col>
    </b-row>
    </b-col>
    <b-col offset=2>
            <b-row>
          P.O.# {{purchase.number}}: <br/>
          Ordered: {{purchaseComponent.unitsOrdered}}<br/>
          In Transit: {{purchaseComponent.unitsInTransit}}<br/>
          Received: {{purchaseComponent.unitsReceived}}<br/>
          <br/>
          All P.O.s<br/>
          Ordered: {{purchaseComponent.component.unitsOrdered}}<br/>
          In Transit: {{purchaseComponent.component.unitsInTransit}}<br/>
          Received: {{purchaseComponent.component.unitsReceived}}<br/>
      </b-row>
    </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";
import moment from "moment";

export default {
  data() {
    return {
      receiving: {},
      purchase: {},
      purchaseComponent: {component: {}},
      availablePurchases: [],
      availablePurchaseComponents: [],
      editMode: true
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
          this.receiving.shippedDate = response.data.shippedDate ? moment(response.data.shippedDate).utc().format("YYYY-MM-DD") : "";
          this.receiving.etaDate = response.data.etaDate ? moment(response.data.etaDate).utc().format("YYYY-MM-DD") : "";
          this.receiving.receivedDate = response.data.receivedDate ? moment(response.data.receivedDate).utc().format("YYYY-MM-DD") : "";
          this.editMode = this.receiving.receivedDate?true:false;
          if (
            response.data.purchaseComponent &&
            response.data.purchaseComponent.purchase
          ) {
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
