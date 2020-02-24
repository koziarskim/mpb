<template>
  <b-container fluid>
    <b-row>
      <b-col cols=6>
        <b-row>
          <b-col cols=3>
            <label class="top-label">Item Number:</label>
            <input class="form-control" type="text" v-model="item.number">
          </b-col>
          <b-col cols=5>
            <label class="top-label">Item Name:</label>
            <input class="form-control" type="text" v-model="item.name">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Season:</label>
            <b-select option-value="id" option-text="name" :list="availableSeasons" v-model="item.season" placeholder="Season"></b-select>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="4">
            <label class="top-label">Category:</label>
            <b-select option-value="id" option-text="value" :list="availableItemCategories" v-model="item.category" placeholder="Select category"></b-select>
          </b-col>
          <b-col cols="4">
            <label class="top-label">Brand:</label>
            <b-select option-value="id" option-text="name" :list="availableBrands" v-model="item.brand" placeholder="Select Brand"></b-select>
          </b-col>
          <b-col cols=2>
            <label class="top-label">Year:</label>
            <b-select option-value="id" option-text="name" :list="availableYears" v-model="item.year" placeholder="Year"></b-select>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=6>
        <b-row>
          <b-col cols=5>
            <label class="top-label">Notes:</label>
            <b-form-textarea type="text" :rows="4" v-model="item.description"></b-form-textarea>
          </b-col>
          <b-col cols=3>
            <upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
          </b-col>
          <b-col cols=4 style="margin-top: 10px">
            <b-button style="margin-left: 70%" size="sm" type="reset" variant="success" @click="saveAndClose">Save</b-button>
            <label class="top-label">Stock: {{item.unitsOnStock}}</label><br/>
            <label class="top-label">Sch/Pro: <b-link role="button" @click="goToItemScheduleList()">{{item.unitsScheduled}}/{{item.unitsProduced}}</b-link></label><br/>
            <label class="top-label">Sold: <b-link role="button" @click="goToItemSaleList()">{{item.unitsSold}}</b-link></label><br/>
            <label class="top-label">Shipped: <b-link role="button" @click="goToItemShippedList()">{{item.unitsShipped}}</b-link></label>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <!-- Units Section -->
        <hr class="hr-text" data-content="Unit dimenstion">
        <b-row>
          <b-col cols=12>
            <label class="top-label">Item Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.height"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.width"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.depth">
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=5>
            <label class="top-label">Weight (lbs):</label>
            <input class="form-control" v-model="item.weight">
          </b-col>
          <b-col cols=5>
            <br/>
            <label class="top-label">Cubic (ft): {{itemCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <hr class="hr-text" data-content="Case dimenstion">
        <b-row>
          <b-col cols=12>
            <label class="top-label">Case Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.caseHeight"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.caseWidth"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.caseDepth">
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=4>
            <label class="top-label">Case Pack:</label>
            <input class="form-control" v-model="item.casePack">
          </b-col>
          <b-col cols=8>
            <br/>
            <label class="top-label">Case weight: {{caseWeight}}</label><br/>
            <label class="top-label">Case cubic: {{caseCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <hr class="hr-text" data-content="Pallet dimenstion">
        <b-row>
          <b-col cols=6>
            <label class="top-label">TI x HI (pcs):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.ti" placeholder="0"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.hi" placeholder="0">
            </div>
          </b-col>
          <b-col cols=6>
            <label class="top-label">Pallet Weight:</label>
            <input class="form-control" v-model="item.palletWeight">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=6>
            <br/>
            <label class="top-label">Units p/ pallet: {{unitsPerPallet}}</label><br/>
            <label class="top-label">Pallet height: {{palletHeight}}</label>
          </b-col>
          <b-col cols=6>
            <br/>
            <label class="top-label">Cases p/p: {{casesPerPallet}}</label><br/>
            <label class="top-label">Pallet cubic: {{palletCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3>
        <hr class="hr-text" data-content="Prices are in USD">
        <b-row>
          <b-col cols=6>
            <label class="top-label">Labor ($):</label>
            <input class="form-control" v-model="item.laborCost">
          </b-col>
          <b-col cols=6>
            <label class="top-label">Other ($):</label>
            <input class="form-control" v-model="item.otherCost">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=6>
            <br/>
            <label class="top-label">Warehouse ($): {{warehouseCost}}</label><br/>
            <label class="top-label">Package/mat. ($): {{packageCost}}</label>
          </b-col>
          <b-col cols=6>
            <br/>
            <label class="top-label">Total Cost: {{totalCost}}</label>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <!-- Item Components -->
    <div style="border: 1px solid #d6d3d3; margin-top: 10px;">
      <b-row>
        <b-col cols=6>
          <label class="top-label">Components:</label>
          <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv"></b-select>
        </b-col>
        <b-col cols=1>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addComponent()">Add &#x25BC;</b-button>
        </b-col>
      </b-row>
      <br>
      <b-row>
        <b-col>
          <b-table v-if="item.itemComponents.length>0" :items="item.itemComponents" :fields="columns" sort-by="component.category.name">
            <template v-slot:cell(component)="row">
              <b-link role="button" @click="goToComponent(row.item.component.id)">{{row.item.component.number}}</b-link>
              <span style="font-size:11px"> {{row.item.component.name}}</span>
            </template>
            <template v-slot:cell(units)="row">
              <input class="form-control" style="width:100px" type="tel" v-model="row.item.units">
            </template>
            <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="removeItemComponent(row.item.id)">x</b-button>
            </template>
          </b-table>
        </b-col>
      </b-row>
    </div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import axios from "axios";
import httpUtils from "../httpUtils";
import navigation from "../utils/navigation";

export default {
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
        palletWeight: 60,
        // upc: {},
        // caseUpc: {},
        // caseUpc: {},
      },
      image: "",
      httpUtils: httpUtils,
      componentKv: {},
      availableBrands: [],
      availableComponents: [],
      availableItemCategories: [],
      availableCompCategories: [],
    availableSeasons: [],
    availableYears: [],
    uploadedFile: null,
    columns: [
        { key: "component", label: "Component", sortable: false },
        { key: "component.category.name", label: "Component", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "component.totalLandedCost", label: "Cost", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
    };
  },
  computed: {
    totalCost() {
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

    palletHeight() {
      return +this.item.caseHeight * +this.item.ti;
    },
    itemCubic() {
      return (
        (+this.item.height * +this.item.width * +this.item.depth) /
        1728
      ).toFixed(2);
    },
    caseCubic() {
      return (
        (+this.item.caseHeight * +this.item.caseWidth * +this.item.caseDepth) /
        1728
      ).toFixed(2);
    },
    palletCubic() {
      return (+this.item.hi * +this.item.ti * +this.caseCubic).toFixed(2);
    },
    unitsPerPallet() {
      return +this.item.hi * +this.item.ti * +this.item.casePack;
    },
    casesPerPallet() {
      return +this.item.hi * +this.item.ti;
    },
    caseWeight() {
      return (+this.item.casePack * +this.item.weight).toFixed(2);
    },
    warehouseCost() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
    packageCost() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
    barcodeUrl() {
      if (this.item.upc.code) {
        return httpUtils.baseUrl + "/upc/image/" + this.item.upc.code;
      }
    },
    caseBarcodeUrl() {
      if (this.item.caseUpc.code) {
        return httpUtils.baseUrl + "/upc/image/" + this.item.caseUpc.code;
      }
    },
  },
  watch: {},
  methods: {
    addComponent(){
      if(!this.componentKv){
        alert("Select Component to Add");
        return;
      }
      this.getComponent(this.componentKv.id).then(c=> {
        this.item.itemComponents.push({
          units: 1,
          component: c
        })
        this.getAvailableComponents();
      })
    },
    getComponent(componentId){
      return http.get("/component/" + componentId).then(r => {
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
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
    getComponentsById(id) {
      return this.item.itemComponents.filter(ic => {
        return ic.component.category.id == id;
      });
    },
    getAvailableComponents() {
      http.get("/component/kv").then(response => {
        this.availableComponents = [];
        response.data.forEach(c => {
          var found = this.item.itemComponents.findIndex(ic =>  ic.component.id == c.id);
          if(found == -1){
            this.availableComponents.push(c);
          }
        })
      }).catch(e => {
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
      http.get("/brand").then(response => {
        this.availableBrands = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableCategories() {
      http.get("/category/item/kv").then(response => {
        response.data.forEach(category => {
          this.availableItemCategories.push(category);
        });
      }).catch(e => {
        console.log("API error: " + e);
      });
      http.get("/category/component/kv").then(response => {
        response.data.forEach(category => {
          this.availableCompCategories.push(category);
        });
      }).catch(e => {
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
      return axios.post(httpUtils.baseUrl + "/item", formData, headers).then(r => {
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
    this.getAvailableComponents();
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
