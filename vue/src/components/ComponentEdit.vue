<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Component</h2>
            <div style="color: red;" v-bind:style="{visibility: component.locked?'visible':'hidden'}">
                Component is locked. Changes here will update Item(s) as well.
            </div>
            <div>
                <b-button type="submit" variant="primary" @click="saveAndUpload">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelComponent">Close</b-button>
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
                    <b-col cols=3>
                        <label>Units in case:</label>
                        <b-form-input type="number" min=0 v-model="component.unitsPerCase" placeholder="Units"></b-form-input>
                    </b-col>                      
                </b-row>
                <b-row>
                    <b-col cols=8>
                        <b-form-textarea type="text" :rows=3 v-model="component.description" placeholder="Enter short description"></b-form-textarea>
                    </b-col>
                    <b-col cols=3>
                        <label title="Full Container Load (units per container)">FCL:</label>
                        <b-form-input type="number" min=0 v-model="component.unitsPerContainer" placeholder="Units"></b-form-input>
                    </b-col>                      
                </b-row>
                <b-row>
                    <b-col cols=3>
                        <label>Height (in):</label>
                        <b-form-input type="number" min=0 v-model="component.height" placeholder="Height"></b-form-input>
                    </b-col>
                    <b-col cols=3>
                        <label>Width (in):</label>
                        <b-form-input type="number" min=0 v-model="component.width" placeholder="Width"></b-form-input>
                    </b-col>
                    <b-col cols=3>
                        <label>Depth (in):</label>
                        <b-form-input type="number" min=0 v-model="component.depth" placeholder="Depth"></b-form-input>
                    </b-col>
                    <b-col cols=3>
                        <label>Weight (lbs):</label>
                        <b-form-input type="number" min=0 v-model="component.weight" placeholder="Weight"></b-form-input>
                    </b-col>
                </b-row>
            </b-col>
            <b-col cols=4 style="border-left: 1px solid #dededf;">
                <b-row>
                    <b-col cols=12>
                        <input type="file" @change="uploadImage" accept="image/png, image/jpeg">
                        <img width="200px" :src="imageUrl" fluid />
                    </b-col>
                </b-row>
            </b-col>
        </b-row>
        <hr class="hr-text" data-content="Unit prices/fees are in USD ($)">
        <b-row>
            <b-col cols=2>
                <label>Purchase $:</label>
                <b-form-input type="number" min=0 v-model="component.purchaseCost" placeholder="Price"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Duty %:</label>
                <b-form-input type="number" min=0 v-model="component.dutyPercentage" placeholder="Duty"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Container Cost $:</label>
                <b-form-input type="number" min=0 v-model="component.containerCost" placeholder="Container"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Other $:</label>
                <b-form-input type="number" min=0 v-model="component.otherCost" placeholder="Other"></b-form-input>
            </b-col>
            <b-col cols=2 offset=2>
                <label>Unit total: {{unitTotalCost}}</label>
                <label>Case total: {{caseTotalCost}}</label>
                <label>Delivery: {{deliveryCost}}</label>
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
      supplier: {},
      category: {},
      component: {
          purchaseCost: 0,
          dutyPercentage: 0,
          deliveryCost: 0,
          containerCost: 0,
          otherCost: 0,
          unitsPerCase: 1,
          unitsPerContainer: 1,
          number: 0,
          totalCost: 0
      },
      availableSuppliers: [],
      availableCategories: []
    };
  },
  computed: {
      unitTotalCost(){
          return (+this.component.purchaseCost + +this.component.deliveryCost + +this.component.otherCost).toFixed(2);
      },
      caseTotalCost(){
          return (+this.unitTotalCost * +this.component.unitsPerCase).toFixed(2);
      },
      deliveryCost(){
          return ((+this.component.containerCost / +this.component.unitsPerContainer) * +this.component.dutyPercentage).toFixed(2);
      },
      imageUrl: function(){
        if(this.component.attachment){
            return httpUtils.baseUrl + "/attachment/" + this.component.attachment.id;
        }
      }
  },
  watch: {
    supplier: function(newValue, oldValue) {
        this.component.supplier=newValue;
    },
    category: function(newValue, oldValue) {
        this.component.category=newValue;
        this.setCategoryNumber(this.category.id);
    },
    deliveryCost: function(newValue, oldValue){
        this.component.deliveryCost = newValue;
    },
    unitTotalCost: function(newValue, oldValue){
        this.component.totalCost = newValue;
    }
  },
  methods: {
    getComponentData(component_id) {
      http
        .get("/component/" + component_id)
        .then(response => {
          this.component = response.data;
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
        .get("/category/type/CMP")
        .then(response => {
          this.availableCategories = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    setCategoryNumber(category_id) {
      http
        .get("/component/number/category/"+category_id)
        .then(response => {
          this.component.number = response.data.number;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    uploadImage(e){
        this.image = e.target.files[0] || e.dataTransfer.files[0];
        this.saveAndUpload();
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
        .then(response =>{
            this.getComponentData(this.component.id);
        })
        .catch(e => {
          console.log("API error: "+e);
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
