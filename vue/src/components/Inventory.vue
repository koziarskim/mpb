<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Inventory:</span>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="reset" variant="success">Save & Close</b-button>
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
  name: "add-component",
  data() {
    return {
      supplier: {
        name: "",
        account: "",
        street: "",
        city: "",
        state: "",
        zip: "",
        phone: "",
        paymentTerms: "",
        freightTerms: "Collect"
      },
      freightTerms: {},
      availableStates: state.states,
      availableFreights: [
        { code: "Delivered", name: "Delivered" },
        { code: "Collect", name: "Collect" }
      ]
    };
  },
  computed: {},
  watch: {
    freightTerms: function(newValue, oldValue) {
      this.supplier.freightTerms = newValue.code;
    }
  },
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
