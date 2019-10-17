<template>
  <b-container fluid>
    <b-row>
      <b-col cols="1">
        <h4 style="text-align: left;">Item:</h4>
      </b-col>
      <b-col cols="2">
        <input class="form-control" type="text" v-model="item.number" placeholder="Item number">
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="text" v-model="item.name" placeholder="Item name">
      </b-col>
      <b-col cols="2" offset="4">
        <div style="text-align: right;">
          <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Season:</label>
            <b-select option-value="id" option-text="name" :list="availableSeasons" v-model="season" placeholder="Select season"></b-select>
          </b-col>
          <b-col cols="3" offset="3">
            <label class="top-label">Case UPC#</label>
            <br>
            <img width="150px" :src="caseBarcodeUrl" fluid>
          </b-col>
          <b-col cols="3">
            <label class="top-label">UPC#</label>
            <br>
            <img width="150px" :src="barcodeUrl" fluid>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="9">
            <b-row>
              <b-col cols="4">
                <label class="top-label">Category:</label>
                <b-select option-value="id" option-text="value" :list="availableItemCs" v-model="category" placeholder="Select category"></b-select>
              </b-col>
              <b-col cols="4">
                <label class="top-label">Brand:</label>
                <b-select option-value="id" option-text="name" :list="availableBrands" v-model="brand" placeholder="Select Brand"></b-select>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="8" style="padding-top: 5px;">
                <b-form-textarea type="text" :rows="3" v-model="item.description" placeholder="Enter short description"></b-form-textarea>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="3">
            <b-row>
              <b-col>
				<upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
              </b-col>
            </b-row>
          </b-col>
        </b-row>
        <hr class="hr-text" data-content="Unit dimenstion">
        <b-row>
          <b-col cols="4">
            <label class="top-label">Dimension (H x W x D):</label>
            <input class="form-control" v-mask="/\d{1,100} x \d{1,100} x \d{1,100}/" v-model="dimension">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Weight (lbs):</label>
            <input class="form-control" type="number" min="0" v-model="item.weight">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Cubic (ft):</label>
            <input class="form-control" readonly :value="itemCubic">
          </b-col>
        </b-row>
        <hr class="hr-text" data-content="Case dimenstion">
        <b-row>
          <b-col cols="3">
            <label class="top-label">Case Dimension:</label>
            <input class="form-control" v-mask="/\d{1,100} x \d{1,100} x \d{1,100}/" v-model="caseDimension">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Case weight:</label>
            <input class="form-control" readonly :value="caseWeight">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Case Pack:</label>
            <input class="form-control" type="number" min="0" v-model="item.casePack">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Case cubic:</label>
            <input class="form-control" readonly :value="caseCubic">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Pallet height:</label>
            <input class="form-control" readonly :value="palletHeight">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="2">
            <label class="top-label">TI x HI (pcs):</label>
            <input class="form-control" v-mask="/\d{1,100} x \d{1,100}/" v-model="tiHi">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Pallet cubic:</label>
            <input class="form-control" readonly :value="palletCubic">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Units p/ pallet:</label>
            <input class="form-control" readonly :value="unitsPerPallet">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Cases p/ pallet:</label>
            <input class="form-control" readonly :value="casesPerPallet">
          </b-col>
        </b-row>
        <hr class="hr-text" data-content="Prices are in USD">
        <b-row>
          <b-col cols="2">
            <label class="top-label">Warehouse ($):</label>
            <input class="form-control" readonly :value="warehouseCost">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Package/mat. ($):</label>
            <input class="form-control" readonly :value="packageCost">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Labor ($):</label>
            <input class="form-control" type="number" min="0" v-model="item.laborCost">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Other ($):</label>
            <input class="form-control" type="number" min="0" v-model="item.otherCost">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Total Cost:</label>
            <input class="form-control" readonly :value="totalCost">
          </b-col>
        </b-row>
      </b-col>
      <!-- Column 2 -->
      <b-col cols="4" style="border-left: 1px solid #dededf;">
        <b-row>
          <b-col cols="12">
            <label class="top-label">Components:</label>
            <b-select option-value="id" option-text="value" :list="availableComponents" v-model="component" placeholder="Select component"></b-select>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="12">
            <div v-for="category in availableComponentCs" v-bind:key="category.id">
              <div style="color: #91c959; font-style: italic; font-weight: bold" v-if="getComponentsById(category.id).length>0">{{category.value}}</div>
              <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in getComponentsById(category.id)" v-bind:key="ic.id">
                <div style="width:100%">
                  <input size="sm" style="border: 0px; width: 25px" min="1" max="9" v-model="ic.units" type="number">
                  <b-button variant="link" @click="goTo('/componentEdit/'+ic.component.id)">{{ic.component.number}}</b-button>
                  <label>{{" | "+ic.component.name+" | $"+ic.component.totalLandedCost}}</label>
                </div>
                <b-button size="sm" type="reset" variant="link" @click="removeItemComponent(ic.id)">(x)</b-button>
              </div>
            </div>
          </b-col>
        </b-row>
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
      availableComp: [],
      query: "",
      selectedComp: null,
      item: {
        itemComponents: [],
        number: 0,
        upc: {},
        caseUpc: {},
        caseUpc: {},
        height: 0,
        width: 0,
        depth: 0,
        caseHeight: 0,
        caseWidth: 0,
        caseDepth: 0,
        ti: 1,
        hi: 1
      },
      dimension: "",
      caseDimension: "",
      tiHi: "",
      image: "",
      httpUtils: httpUtils,
      brand: {},
      season: {},
      category: {},
      component: {},
      availableBrands: [],
      availableComponents: [],
      availableItemCs: [],
      availableComponentCs: [],
	  availableSeasons: [],
	  uploadedFile: null
    };
  },
  computed: {
    totalCost: function() {
      var totalCost = 0;
      this.item.itemComponents.forEach(ic => {
        totalCost = +totalCost + +ic.component.totalLandedCost * +ic.units;
      });
      totalCost =
        +totalCost +
        +this.warehouseCost +
        +this.packageCost +
        +this.item.laborCost +
        +this.item.otherCost;
      return totalCost.toFixed(2);
    },

    palletHeight: function() {
      return +this.item.caseHeight * +this.item.ti;
    },
    itemCubic: function() {
      return (
        (+this.item.height * +this.item.width * +this.item.depth) /
        1728
      ).toFixed(2);
    },
    caseCubic: function() {
      return (
        (+this.item.caseHeight * +this.item.caseWidth * +this.item.caseDepth) /
        1728
      ).toFixed(2);
    },
    palletCubic: function() {
      return (+this.item.hi * +this.item.ti * +this.caseCubic).toFixed(2);
    },
    unitsPerPallet: function() {
      return +this.item.hi * +this.item.ti * +this.item.casePack;
    },
    casesPerPallet: function() {
      return +this.item.hi * +this.item.ti;
    },
    caseWeight: function() {
      return (+this.item.casePack * +this.item.weight).toFixed(2);
    },
    warehouseCost: function() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
    packageCost: function() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
    barcodeUrl: function() {
      if (this.item.upc.code) {
        return httpUtils.baseUrl + "/upc/image/" + this.item.upc.code;
      }
    },
    caseBarcodeUrl: function() {
      if (this.item.caseUpc.code) {
        return httpUtils.baseUrl + "/upc/image/" + this.item.caseUpc.code;
      }
    },
  },
  watch: {
    component: function(new_component, old_component) {
      if (new_component.id) {
        var found = this.item.itemComponents.find(
          ic => ic.component.id === new_component.id
        );
        if (!found) {
          http
            .get("/component/" + new_component.id)
            .then(response => {
              this.item.itemComponents.push({
                units: 1, //Default value
                component: response.data
              });
            })
            .catch(e => {
              console.log("API error: " + e);
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
      if (!this.item.season || this.item.season.id != newValue.id) {
        this.item.season = newValue;
      }
    },
    dimension: function(newValue, oldValue) {
      var dimension = newValue.replace(/\s+/g, "").split("x");
      this.item.height = dimension[0];
      this.item.width = dimension[1];
      this.item.depth = dimension[2];
    },
    tiHi: function(newValue, oldValue) {
      var tiHi = newValue.replace(/\s+/g, "").split("x");
      this.item.ti = tiHi[0];
      this.item.hi = tiHi[1];
    },
    caseDimension: function(newValue, oldValue) {
      var dimension = newValue.replace(/\s+/g, "").split("x");
      this.item.caseHeight = dimension[0];
      this.item.caseWidth = dimension[1];
      this.item.caseDepth = dimension[2];
    }
  },
  methods: {
	onUpload(file){
		this.uploadedFile = file;
	},
    getComponentsById: function(id) {
      return this.item.itemComponents.filter(ic => {
        return ic.component.category.id == id;
      });
    },
    getComponentsData() {
      http
        .get("/component/kv")
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
          this.dimension =
            response.data.height +
            " x " +
            response.data.width +
            " x " +
            response.data.depth;
          this.caseDimension =
            response.data.caseHeight +
            " x " +
            response.data.caseWidth +
            " x " +
            response.data.caseDepth;
          this.tiHi = response.data.ti + " x " + response.data.hi;
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
        .get("/category/item/keyValue")
        .then(response => {
          response.data.forEach(category => {
            this.availableItemCs.push(category);
          });
        })
        .catch(e => {
          console.log("API error: " + e);
        });
      http
        .get("/category/component/kv")
        .then(response => {
          response.data.forEach(category => {
            this.availableComponentCs.push(category);
          });
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
    validate(){
        if(!this.item.name || !this.item.number){
            alert("Item Name and Number is required");
            return false;
        }
        if(!this.item.season || !this.item.season.id){
            alert("Season is required");
            return false;
        }

        return true;
    },
    saveAndUpload() {
        if(!this.validate()){
            return Promise.reject();
        }
      this.item.totalCost = this.totalCost;
      let formData = new FormData();
      formData.append("image", this.uploadedFile);
      formData.append("jsonItem", JSON.stringify(this.item));
      return axios
        .post(httpUtils.baseUrl + "/item/upload", formData, {
          headers: {
            "Content-Type": "multipart/form-data"
          }
        })
        .then(response => {
          this.getItemData(this.item.id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.saveAndUpload().then(r => {
        window.history.back();
      });
    },
    goTo(view) {
      this.saveAndUpload();
      httpUtils.goTo(view);
    },
    removeItemComponent(ic_id) {
      var idx = this.item.itemComponents.findIndex(ic => ic.id == ic_id);
      this.item.itemComponents.splice(idx, 1);
	},
	getImageUrl(){
		if(this.item.attachment){
        	return httpUtils.baseUrl + "/attachment/" + this.item.attachment.id;
		}
		return null;
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
