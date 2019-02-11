<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Component</h2>
            <div>
                <b-button type="submit" variant="primary" @click="saveComponent">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelComponent">Cancel</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=2>
                <label>MPB Stock#:</label>
            </b-col>
            <b-col cols=3>
                <b-form-input type="text" v-model="component.stockNumber" placeholder="Internal stock number"></b-form-input>
            </b-col>
            <b-col cols=1>
                <label>Category:</label>
            </b-col>
            <b-col cols=4>
                    <b-select option-value="id" option-text="name" :list="availableBrands" v-model="component.brand" placeholder="Select Brand"></b-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Name:</label>
            </b-col>
            <b-col cols=4>
                <b-form-input type="text" v-model="component.name" placeholder="Component name"></b-form-input>
            </b-col>
            <b-col cols=1>
                <label>Vendor:</label>
            </b-col>
            <b-col cols=4>
                    <b-select option-value="id" option-text="name" :list="availableVendors" v-model="component.vendor" placeholder="Select Vendor"></b-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>MPB Stock#:</label>
            </b-col>
            <b-col cols=3>
                <b-form-input type="text" v-model="component.stockNumber" placeholder="Internal stock number"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Supplier's Stock#:</label>
            </b-col>
            <b-col cols=3>
                <b-form-input type="text" v-model="component.supplierStockNumber" placeholder="Supplier's stock number"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Description:</label>
            </b-col>
            <b-col cols=5>
                <b-form-textarea type="text" :rows=3 v-model="component.description" placeholder="Enter short description"></b-form-textarea>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=12>
                <hr class="hr-text" data-content="Price/fees are per unit only">
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Purchase Price:</label>
            </b-col>
            <b-col cols=2>
                <b-form-input type="number" v-model="component.assumedPrice" placeholder="Price"></b-form-input>
            </b-col>
            <b-col cols=1>
                <label>Duty:</label>
            </b-col>
            <b-col cols=2>
                <b-form-input type="number" v-model="component.dutyFee" placeholder="Duty"></b-form-input>
            </b-col>
            <b-col cols=1>
                <label>Delivery:</label>
            </b-col>
            <b-col cols=2>
                <b-form-input type="number" v-model="component.deliveryFee" placeholder="Delivery"></b-form-input>
            </b-col>
        </b-row>
        <b-alert :show="this.component.locked" dismissible variant="warning">
                Component may be currently used by Item(s). Changes here will update Item(s) as well.
        </b-alert>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "add-component",
  data() {
    return {
      component: {
        vendor: {},
        brand: {}
      },
      availableVendors: [
        { id: 1, name: "Walmart" },
        { id: 2, name: "Costco" },
        { id: 3, name: "Big Lots" }
      ],
      availableBrands: [
        { id: 1, name: "Food" },
        { id: 2, name: "Coca Cola" },
        { id: 3, name: "Butweiser" }
      ]
    };
  },
  methods: {
    getComponentData(component_id) {
      http
        .get("/components/" + component_id)
        .then(response => {
          this.component = response.data;
          if (!this.component.vendor) {
            this.component.vendor = {};
          }
          if (!this.component.brand) {
            this.component.brand = {};
          }
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveComponent() {
      http
        .post("/components", this.component)
        .then(response => {
          window.history.back();
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    cancelComponent() {
      window.history.back();
    }
  },
  mounted() {
    var component_id = this.$route.params.component_id;
    if (component_id) {
      this.getComponentData(component_id);
    }
  }
};
</script>

<style>
</style>
