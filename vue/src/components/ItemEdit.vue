<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Item</h2>
            <div>
                <b-button type="submit" variant="primary" @click="saveAndUpload">Save</b-button>
                <b-button type="reset" variant="danger" @click="cancelItem">Close</b-button>
            </div>
        </div>
        <b-row>
        <b-col cols=8>
            <b-row>
                <b-col cols=7>
                    <b-row>
                        <b-col cols=2>
                            <label>Brand:</label>
                        </b-col>
                        <b-col cols=10>
                                <b-select option-value="id" option-text="name" :list="availableBrands" v-model="brand" placeholder="Select Brand"></b-select>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col cols=2>
                            <label>Item#:</label>
                        </b-col>
                        <b-col cols=6>
                            <b-form-input type="text" v-model="item.number" placeholder="Enter item number"></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col cols=2>
                            <label>Name:</label>
                        </b-col>
                        <b-col cols=10>
                            <b-form-input type="text" v-model="item.name" placeholder="Enter your name"></b-form-input>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col cols=12>
                            <b-form-textarea type="text" :rows=3 v-model="item.description" placeholder="Enter short description"></b-form-textarea>
                        </b-col>                
                    </b-row>
                    <b-row>
                        <b-col cols=5>
                            <label>Total Cost: {{totalCost}}</label>
                        </b-col>
                        <b-col cols=7>
                            <label>UPC#: {{item.upc.code}}</label>
                        </b-col>
                        <b-col>
                            <b-img width=150px :src="barcodeUrl" fluid />
                        </b-col>
                    </b-row>
                </b-col>
                <b-col cols=5>
                    <b-row>
                        <b-col cols=12>
                            <b-select option-value="id" option-text="name" :list="availableCategories" v-model="category" placeholder="Select category"></b-select>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col cols=12>
                            <b-select option-value="id" option-text="name" :list="availableSeasons" v-model="season" placeholder="Select season"></b-select>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <input type="file" @change="uploadImage" accept="image/png, image/jpeg">
                            <img width="200px" :src="imageUrl" fluid />
                        </b-col>
                    </b-row>
                </b-col>
            </b-row>
        </b-col>
        <!-- Column 2 -->
        <b-col cols=4 style="border-left: 1px solid #dededf;">
            <b-row>
                <b-col cols=12>
                    <b-select option-value="id" option-text="name" :list="availableComponents" v-model="component" placeholder="Select component"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=12>
                        <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in item.itemComponents" v-bind:key="ic.id">
                            <div style="width:100%">
                                <input size="sm" style="border: 0px; width: 25px" min=1 max=9 v-model="ic.units" type="number"/>
                                <b-button variant="link" @click="goTo('/componentEdit/'+ic.component.id)">{{ic.component.number}}</b-button>
                                <label>{{" | "+ic.component.name+" | $"+ic.component.totalCost}}</label>
                            </div>
                            <b-button size="sm" type="reset" variant="link" @click="removeItemComponent(ic.id)">(x)</b-button>
                        </div>                
                </b-col>
            </b-row>
        </b-col>
        </b-row>
        <hr class="hr-text" data-content="Unit and case dimensions">
        <b-row>
            <b-col cols=2>
                <label>Height (in):</label>
                <b-form-input type="number" min=0 v-model="item.height"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Width (in):</label>
                <b-form-input type="number" min=0 v-model="item.width"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Depth (in):</label>
                <b-form-input type="number" min=0 v-model="item.depth"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Weight (lbs):</label>
                <b-form-input type="number" min=0 v-model="item.weight"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Units per case:</label>
                 <b-form-input type="number" min=0 v-model="item.unitsPerCase"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case UPC#:</label>
                <b-form-input type="number" min=0 v-model="item.caseUpc"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case height (in):</label>
                <b-form-input type="number" min=0 v-model="item.caseHeight"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case width (in):</label>
                <b-form-input type="number" min=0 v-model="item.caseWidth"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case depth (in):</label>
                <b-form-input type="number" min=0 v-model="item.caseDepth"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case weight (lbs):</label>
                <b-form-input type="number" min=0 v-model="item.caseWeight"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label title="Number of case on single layer/tier">TI (in):</label>
                <b-form-input type="number" min=0 v-model="item.ti"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label title="Number of layers/tiers on the pallet">HI (in):</label>
                <b-form-input type="number" min=0 v-model="item.hi"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Pallet height (in):</label>
                <b-form-input readonly="true" type="number" min=0 v-model="palletHeight"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Cubic (ft):</label>
                <b-form-input readonly="true" type="number" min=0 v-model="itemCubic"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Case cubic (ft):</label>
                <b-form-input readonly="true" type="number" min=0 v-model="caseCubic"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label>Warehouse ($):</label>
                <b-form-input readonly="true" type="number" min=0 v-model="item.warehouseCost"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Package/mat. ($):</label>
                <b-form-input readonly="true" type="number" min=0 v-model="item.packageCost"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Labor ($):</label>
                <b-form-input type="number" min=0 v-model="item.laborCost"></b-form-input>
            </b-col>
            <b-col cols=2>
                <label>Other ($):</label>
                <b-form-input type="number" min=0 v-model="item.otherCost"></b-form-input>
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
  name: "edit-component",
  data() {
    return {
      item: {
        itemComponents: [],
        number: 0,
        upc: {}
      },
      image: "",
      httpUtils: httpUtils,
      brand: {},
      season: {},
      category: {},
      component: {},
      availableBrands: [],
      availableComponents: [],
      availableCategories: [],
      availableSeasons: []
    };
  },
  computed: {
    totalCost: function() {
      var totalCost = 0;
      this.item.itemComponents.forEach(ic => {
        totalCost = +totalCost + +ic.component.totalCost * +ic.units;
      });
      totalCost = +totalCost + +this.item.warehouseCost + +this.item.packageCost + +this.item.laborCost + +this.item.otherCost;
      return totalCost;
    },
    palletHeight: function(){
        return +this.item.caseHeight * +this.item.ti;
    },
    itemCubic: function(){
        return ((+this.item.height * +this.item.width * +this.item.depth) / 1728).toFixed(2);
    },
    caseCubic: function(){
        return ((+this.item.caseHeight * +this.item.caseWidth * +this.item.caseDepth) / 1728).toFixed(2);
    },
    barcodeUrl: function(){
        if(this.item.upc.code){
            return httpUtils.baseUrl + "/upc/image/"+this.item.upc.code
        }
    },
    imageUrl: function(){
        if(this.item.attachment){
            return httpUtils.baseUrl + "/attachment/" + this.item.attachment.id;
        }
    }
  },
  watch: {
    component: function(new_component, old_component) {
      if (new_component.id) {
        var found = this.item.itemComponents.some(function(ic) {
          return ic.component.id === new_component.id;
        });
        if (!found) {
          this.item.itemComponents.push({
            units: 1, //Default value
            component: new_component
          });
        }
      }
    },
    brand: function(newValue, oldValue) {
      this.item.brand = newValue;
    },
    category: function(newValue, oldValue) {
      this.item.category = newValue;
    },
    season: function(newValue, oldValue) {
      this.item.season = newValue;
      this.setItemNumber(this.item.season.id);
    }
  },
  methods: {
    getComponentsData() {
      http
        .get("/component")
        .then(response => {
          this.availableComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItemData(item_id) {
      http
        .get("/item/" + item_id)
        .then(response => {
          this.item = response.data;
          if (response.data.brand) {
            this.brand = response.data.brand;
          }
          if (response.data.category) {
            this.category = response.data.category;
          }
          if (response.data.season) {
            this.season = response.data.season;
          }
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
    getAvailableCategories() {
      http
        .get("/category/type/ITM")
        .then(response => {
          this.availableCategories = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSeasons() {
      http
        .get("/season")
        .then(response => {
          this.availableSeasons = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    setItemNumber(season_id) {
      http
        .get("/item/number/season/" + season_id)
        .then(response => {
          this.item.number = response.data.number;
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
      formData.append("jsonItem", JSON.stringify(this.item));
      axios
        .post(httpUtils.baseUrl + "/item/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        })
        .then(response => {
            this.getItemData(this.item.id);
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    cancelItem() {
      window.history.back();
    },
    goTo(view) {
      this.saveAndUpload();
      httpUtils.goTo(view);
    },
    removeItemComponent(ic_id) {
      console.log("remove comp");
      for (var i = 0; i < this.item.itemComponents.length; i++) {
        if (this.item.itemComponents[i].id == ic_id) {
          this.item.itemComponents.splice(i, 1);
          break;
        }
      }
    }
  },
  mounted() {
    this.getComponentsData();
    var item_id = this.$route.params.item_id;
    if (item_id) {
      this.getItemData(item_id);
    }
    this.getAvailableBrands();
    this.getAvailableCategories();
    this.getAvailableSeasons();
  }
};
</script>

<style>
</style>
