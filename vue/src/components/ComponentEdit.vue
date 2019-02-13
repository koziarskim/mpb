<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Component</h2>
            <b-alert style="width:58%" :show="this.component.locked" dismissible variant="warning">
                Component is locked. Changes here will update Item(s) as well.
            </b-alert>
            <div>
                <b-button type="submit" variant="primary" @click="saveAndUpload">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelComponent">Cancel</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=8>
                <b-row>
                    <b-col cols=2>
                        <label>MPB#:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-form-input type="text" v-model="component.stockNumber" placeholder="Internal stock number"></b-form-input>
                    </b-col>
                    <b-col cols=2>
                        <label>Brand:</label>
                    </b-col>
                    <b-col cols=4>
                            <b-select option-value="id" option-text="name" :list="availableBrands" v-model="component.brand" placeholder="Select Brand"></b-select>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=2>
                        <label>Name:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-form-input type="text" v-model="component.name" placeholder="Component name"></b-form-input>
                    </b-col>
                    <b-col cols=2>
                        <label>Supplier:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="component.supplier" placeholder="Select Supplier"></b-select>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=3>
                        <label>Supplier's Stock#:</label>
                    </b-col>
                    <b-col cols=5>
                        <b-form-input type="text" v-model="component.supplierStockNumber" placeholder="Supplier's stock number"></b-form-input>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=12>
                        <b-form-textarea type="text" :rows=3 v-model="component.description" placeholder="Enter short description"></b-form-textarea>
                    </b-col>
                </b-row>
            </b-col>
            <b-col cols=4>
                <b-row>
                    <b-col cols=12>
                        <b-form-file type="file" v-model="image"/>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col>
                        <b-img width="300px" :src="imageUrl" fluid />
                    </b-col>
                </b-row>
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
            <b-col cols=2>
                <label>Total: {{totalPrice}}</label>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import axios from "axios";
import httpUtils from "../httpUtils";

export default {
  name: "add-component",
  data() {
    return {
      image: "",
      imageUrl: "",
      component: {
        supplier: {},
        brand: {},
        attachment: {}
      },
      availableSuppliers: [],
      availableBrands: []
    };
  },
  computed: {
      totalPrice(){
          return +this.component.assumedPrice + +this.component.dutyFee + +this.component.deliveryFee;
      }
  },
  methods: {
    getComponentData(component_id) {
      http
        .get("/component/" + component_id)
        .then(response => {
          this.component = response.data;
          this.imageUrl =
            httpUtils.baseUrl + "/attachment/" + this.component.attachment.id;
          if (!this.component.supplier) {
            this.component.supplier = {};
          }
          if (!this.component.brand) {
            this.component.brand = {};
          }
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers() {
      http
        .get("/supplier")
        .then(response => {
          this.availableSuppliers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableBrands() {
      http
        .get("/brand")
        .then(response => {
          this.availableBrands = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveComponent() {
      http
        .post("/component", this.component)
        .then(response => {
          window.history.back();
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    saveAndUpload() {
      let formData = new FormData();
      formData.append("image", this.image);
      formData.append("jsonComponent", JSON.stringify(this.component));
      axios
        .post(httpUtils.baseUrl + "/component/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        })
        .then(function() {
          window.history.back();
        })
        .catch(function() {
          console.log("FAILURE!!");
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
    this.getAvailableSuppliers();
    this.getAvailableBrands();
  }
};
</script>

<style>
</style>
