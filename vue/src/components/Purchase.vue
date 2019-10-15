<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order:</span>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="text" v-model="purchase.number" placeholder="P.O. Number">
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="purchase.date" placeholder="Date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 10px;">
      <b-col cols=2>
        <component-search v-on:componentsUpdated="updateComponents"></component-search>
      </b-col>
      <b-col cols=10>
        <b-row>
          <b-col>
            <label class="top-label">Components:</label>
            <div v-for="c in selectedComponents" v-bind:key="c.id">{{c.name}}</div>
          </b-col>
        </b-row>
      </b-col>
    </b-row>

  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";
import vue from "vue";
import ComponentSearch from "./ComponentSearch";

export default {
  data() {
    return {
      purchase: {
        date: moment().utc().format("YYYY-MM-DD")
      },
      selectedComponents: [],
    };
  },
  components:{
    'component-search': ComponentSearch
  },
  computed: {},
  watch: {
  },
  methods: {
    updateComponents(components){
      this.selectedComponents = components;
    },
    getPurchase(purchase_id) {
      http.get("/purchase/" + purchase_id).then(r => {
          this.purchase = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      return http.post("/purchase", this.purchase).then(r => {
          this.getPurchaseData(r.data.id);
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.savePurchase().then(r => {
        router.push("/purchaseList");
      });
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    saleHeight(){
      return +window.innerHeight - 150 +"px";
    },
  },
  mounted() {
    var purchase_id = this.$route.params.purchase_id;
    if (purchase_id) {
      this.getPurchaseData(purchase_id);
    }
  }
};
</script>

