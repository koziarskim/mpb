<template>
  <b-container fluid>
    <b-row style="margin-top:-5px">
      <b-col cols=1>
        <label class="top-label">Item Number:</label>
        <input class="form-control" type="text" v-model="item.number">
      </b-col>
      <b-col cols=3>
        <label class="top-label">Item Name:</label>
        <input class="form-control" type="text" v-model="item.name">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Season:</label>
        <b-select option-value="id" option-text="name" :list="availableSeasons" v-model="item.season" placeholder="Season"></b-select>
      </b-col>
      <b-col cols=1>
        <label class="top-label">Year:</label>
        <b-select option-value="id" option-text="name" :list="availableYears" v-model="item.year" placeholder="Year"></b-select>
      </b-col>
      <b-col cols=3 offset=1 style="margin-top: 5px">
        <label class="top-label">Stock: {{item.unitsOnStock}},&nbsp;&nbsp;</label>
        <label class="top-label">Shed/Prod: <b-link role="button" @click="goToItemScheduleList()">{{item.unitsScheduled}}/{{item.unitsProduced}}</b-link></label><br/>
        <label class="top-label">Sold: <b-link role="button" @click="goToItemSaleList()">{{item.unitsSold}}</b-link>,&nbsp;&nbsp;</label>
        <label class="top-label">Shipped: <b-link role="button" @click="goToItemShippedList()">{{item.unitsShipped}}</b-link></label>
      </b-col>
      <b-col cols=1 style="margin-top: 20px">
        <div style="text-align: right;">
          <b-button size="sm" type="reset" variant="success" @click="saveAndClose">Save</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols="8">
        <b-row>
          <b-col cols="3" offset="3">
            <!-- <label class="top-label">Case UPC#</label>
            <br>
            <img width="150px" :src="caseBarcodeUrl" fluid> -->
          </b-col>
          <b-col cols="3">
            <!-- <label class="top-label">UPC#</label>
            <br>
            <img width="150px" :src="barcodeUrl" fluid> -->
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="9">
            <b-row>
              <b-col cols="4">
                <label class="top-label">Category:</label>
                <b-select option-value="id" option-text="value" :list="availableItemCs" v-model="item.category" placeholder="Select category"></b-select>
              </b-col>
              <b-col cols="4">
                <label class="top-label">Brand:</label>
                <b-select option-value="id" option-text="name" :list="availableBrands" v-model="item.brand" placeholder="Select Brand"></b-select>
              </b-col>
            </b-row>
            <b-row>
              <b-col cols="8" style="margin-top: 5px;">
                <b-form-textarea type="text" :rows="3" v-model="item.description" placeholder="Enter short description"></b-form-textarea>
              </b-col>
            </b-row>
          </b-col>
          <b-col cols="3">
              <b-col style="margin-top: 10px; margin-left: -13px">
				        <upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
              </b-col>
          </b-col>
        </b-row>
        <hr class="hr-text" data-content="Unit dimenstion">
        <b-row>
          <b-col cols="4">
            <label class="top-label">Item Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" type="number" v-model="item.height"><span style="padding: 7px">x</span>
              <input class="form-control" type="number" v-model="item.width"><span style="padding: 7px">x</span>
              <input class="form-control" type="number" v-model="item.depth">
            </div>
          </b-col>
          <b-col cols="2">
            <label class="top-label">Weight (lbs):</label>
            <input class="form-control" type="number" v-model="item.weight">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Cubic (ft):</label>
            <input class="form-control" readonly :value="itemCubic">
          </b-col>
        </b-row>
        <hr class="hr-text" data-content="Case dimenstion">
        <b-row>
          <b-col cols=4>
            <label class="top-label">Case Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" type="number" v-model="item.caseHeight"><span style="padding: 7px">x</span>
              <input class="form-control" type="number" v-model="item.caseWidth"><span style="padding: 7px">x</span>
              <input class="form-control" type="number" v-model="item.caseDepth">
            </div>
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
        </b-row>
        <b-row>
          <b-col cols="2">
            <label class="top-label">TI x HI (pcs):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.ti" placeholder="0"><span style="padding: 7px">x</span>
              <input class="form-control" v-model="item.hi" placeholder="0">
            </div>
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
          <b-col cols="2">
            <label class="top-label">Pallet height:</label>
            <input class="form-control" readonly :value="palletHeight">
          </b-col>
          <b-col cols="2">
            <label class="top-label">Pallet Weight:</label>
            <input class="form-control" v-model="item.palletWeight">
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
            <b-select option-value="id" option-text="value" :list="availableComponents" v-model="component" placeholder="Pick Component"></b-select>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="12">
            <div v-for="category in availableComponentCs" v-bind:key="category.id">
              <div style="color: #91c959; font-style: italic; font-weight: bold" v-if="getComponentsById(category.id).length>0">{{category.value}}</div>
              <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in getComponentsById(category.id)" v-bind:key="ic.id">
                <div style="width:100%">
                  <input size="sm" style="border: 0px; width: 25px" min="1" max="9" v-model="ic.units" type="number">
                  <b-button variant="link" @click="goToComponent(ic.component.id)">{{ic.component.number}}</b-button>
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
import navigation from "../utils/navigation";

export default {
  name: "edit-component",
  data() {
    return {
      navigation: navigation,
      availableComp: [],
      query: "",
      selectedComp: null,
      item: {
        itemComponents: [],
        brand: {},
        category: {},
        season: {},
        year: {},
        // upc: {},
        // caseUpc: {},
        // caseUpc: {},
      },
      image: "",
      httpUtils: httpUtils,
      component: {},
      availableBrands: [],
      availableComponents: [],
      availableItemCs: [],
      availableComponentCs: [],
    availableSeasons: [],
    availableYears: [],
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
      return totalCost?totalCost.toFixed(2):0.00;
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
    // brand: function(newValue, oldValue) {
    //   this.item.brand = newValue;
    // },
    // category: function(newValue, oldValue) {
    //   this.item.category = newValue;
    // },
    // season: function(newValue, oldValue) {
    //   if (!this.item.season || this.item.season.id != newValue.id) {
    //     this.item.season = newValue;
    //   }
    // },
  },
  methods: {
    goToItemSaleList(){
        router.push('/itemSaleList/'+this.item.id);
    },
    goToItemScheduleList(){
        router.push('/scheduleEventList/'+this.item.id);
    },
    goToItemShippedList(){
      var query = { itemId: this.item.id};
      router.push({path: "/shipmentList", query: query})
    },
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
      return http.get("/item/" + item_id).then(response => {
        this.item = response.data;
        return response.data;
      }).catch(e => {
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
      http.get("/season").then(response => {
        this.availableSeasons = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableYears() {
      http.get("/year").then(response => {
        this.availableYears = response.data;
      }).catch(e => {
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
    save() {
        if(!this.validate()){
            return Promise.reject();
        }
      this.item.totalCost = this.totalCost;
      var formData = new FormData();
      formData.append("image", this.uploadedFile);
      formData.append("jsonItem", JSON.stringify(this.item));
      var headers = {"Content-Type": "multipart/form-data"};
      return axios.post(httpUtils.baseUrl + "/item/upload", formData, headers).then(r => {
        return r.data;
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
    saveAndClose() {
      this.save().then(i => {
        this.getItemData(i.id).then(item => {
          router.push("/itemList");
        })
      });
    },
    goToComponent(componentId) {
      router.push('/componentEdit/'+componentId);
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
    }else{
      this.item.season = navigation.getSeason();
      this.item.year = navigation.getYear();
    }
    this.getAvailableBrands();
    this.getAvailableCategories();
    this.getAvailableSeasons();
    this.getAvailableYears();
  }
};
</script>

<style>
</style>
