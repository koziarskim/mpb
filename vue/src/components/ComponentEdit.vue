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
                        <label>Category:</label>
                    </b-col>
                    <b-col cols=4>
                            <b-select option-value="id" option-text="name" :list="availableCategories" v-model="category" placeholder="Select Category"></b-select>
                    </b-col>
                    <b-col cols=2>
                        <label>Supplier:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier" placeholder="Select Supplier"></b-select>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=2>
                        <label>Component#:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-form-input type="text" v-model="component.number" placeholder="Component number"></b-form-input>
                    </b-col>
                    <b-col cols=2>
                        <label>Name:</label>
                    </b-col>
                    <b-col cols=4>
                        <b-form-input type="text" v-model="component.name" placeholder="Component name"></b-form-input>
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
            <b-col cols=2>
                <label>Unit height (lbs):</label>
                <b-form-input type="number" min=0 v-model="component.height" placeholder="Height"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Unit width (lbs):</label>
                <b-form-input type="number" min=0 v-model="component.width" placeholder="Width"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Unit depth (lbs):</label>
                <b-form-input type="number" min=0 v-model="component.depth" placeholder="Depth"></b-form-input>
            </b-col>
           <b-col cols=2>
                <label>Unit weight (lbs):</label>
                <b-form-input type="number" min=0 v-model="component.weight" placeholder="Weight"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Units per case:</label>
                <b-form-input type="number" min=0 v-model="component.unitsPerCase" placeholder="Units"></b-form-input>
            </b-col>            
        </b-row>

        <b-row>
            <b-col cols=12>
                <hr class="hr-text" data-content="Unit prices/fees are in USD ($)">
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Purchase $:</label>
                <b-form-input type="number" min=0 v-model="component.assumedPrice" placeholder="Price"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Duty $:</label>
                <b-form-input type="number" min=0 v-model="component.dutyFee" placeholder="Duty"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Delivery $:</label>
                <b-form-input type="number" min=0 v-model="component.deliveryFee" placeholder="Delivery"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Other $:</label>
                <b-form-input type="number" min=0 v-model="component.otherFee" placeholder="Other"></b-form-input>
            </b-col>
            <b-col cols=2 offset=2>
                <label>Unit total: {{unitTotalPrice}}</label>
                <label>Case total: {{caseTotalPrice}}</label>
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
      supplier: {},
      category: {},
      component: {
          assumedPrice: 0,
          dutyFee: 0,
          deliveryFee: 0,
          otherFee: 0,
          unitsPerCase: 0,
      },
      availableSuppliers: [],
      availableCategories: []
    };
  },
  computed: {
      unitTotalPrice(){
          return +this.component.assumedPrice + +this.component.dutyFee + +this.component.deliveryFee + +this.component.otherFee;
      },
      caseTotalPrice(){
          return +this.unitTotalPrice * +this.component.unitsPerCase;
      }
  },
  watch: {
    supplier: function(newValue, oldValue) {
        this.component.supplier=newValue;
    },
    category: function(newValue, oldValue) {
        this.component.category=newValue;
    }
  },
  methods: {
    getComponentData(component_id) {
      http
        .get("/component/" + component_id)
        .then(response => {
          this.component = response.data;
          if(response.data.attachment){
            this.imageUrl = httpUtils.baseUrl + "/attachment/" + response.data.attachment.id;
          }
          if (response.data.supplier) {
            this.supplier = response.data.supplier;
          }
          if (response.data.category) {
            this.category = response.data.category;
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
    getAvailableCategories() {
      http
        .get("/category")
        .then(response => {
          this.availableCategories = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
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
    this.getAvailableCategories();
  }
};
</script>

<style>
</style>
