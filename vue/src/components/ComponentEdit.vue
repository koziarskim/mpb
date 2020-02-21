<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h4 style="text-align: left;">New/Edit Component</h4>
            <div style="text-align: center; color: red;" v-bind:style="{visibility: component.locked?'visible':'hidden'}">
                Component is locked. Changes here will update Item(s) as well.
            </div>
            <div style="text-align: right;">
                <b-button type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
            </div>
        </div>
        <b-row>
            <b-col cols=8>
                <b-row>
                    <b-col cols=4>
                        <label class="top-label">Category:</label>
                        <b-select option-value="id" option-text="name" :list="availableCategories" v-model="category" placeholder="Select Category"></b-select>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">Name:</label>
                        <input class="form-control" type="tel" v-model="component.name"/>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">Component#:</label>
                        <input class="form-control" type="tel" v-model="component.number" />
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=4>
                        <label class="top-label">Supplier:</label>
                        <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier" placeholder="Select Supplier"></b-select>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">Supplier's Stock#:</label>
                        <input class="form-control" type="text" v-model="component.supplierStockNumber" placeholder="Supplier's stock number"/>
                    </b-col>
                    <b-col cols=4>
                        <label class="top-label">Dimension (H x W x D):</label>
                        <div style="display:flex">
                          <input class="form-control" type="number" v-model="component.height"><span style="padding:7px">x</span> 
                          <input class="form-control" type="number" v-model="component.width"><span style="padding:7px">x</span> 
                          <input class="form-control" type="number" v-model="component.depth">
                        </div>
                    </b-col>
                </b-row>
                <b-row>
                    <b-col cols=8>
                        <label class="top-label">Description:</label>
                        <b-form-textarea type="text" :rows=3 v-model="component.description" placeholder="Enter short description"></b-form-textarea>
                    </b-col>
                    <b-col cols=4>
                        <b-row>
                            <b-col cols=6>
                                <label class="top-label">Case Pack:</label>
                                <input class="form-control" type="number" min=0 v-model="component.casePack" placeholder="Units"/>
                            </b-col>                      
                            <b-col cols=6>
                                <label class="top-label">Weight:</label>
                                <input class="form-control" type="number" min=0 v-model="component.weight"/>
                            </b-col>                 
                        </b-row>
                        <b-row>
                            <b-col cols=6>
                                <label class="top-label" title="Full Container Load (units per container)">FCL:</label>
                                <input class="form-control" type="number" min=0 v-model="component.unitsPerContainer" placeholder="Units"/>
                            </b-col>
                        </b-row>
                    </b-col>                      
                </b-row>
                <b-row>
                </b-row>
            </b-col>
            <b-col cols=4 style="border-left: 1px solid #dededf;">
                <b-row>
                    <b-col cols=12>
						<upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
                    </b-col>
                </b-row>
            </b-col>
        </b-row>
        <hr class="hr-text" data-content="Unit prices/fees are in USD ($)">
        <b-row>
            <b-col cols=2>
                <label class="top-label">Unit Cost $:</label>
                <input class="form-control" type="number" min=0 v-model="component.unitCost"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Duty %:</label>
                <input class="form-control" type="number" min=0 v-model="component.dutyPercentage"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Container Cost $:</label>
                <input class="form-control" type="number" min=0 v-model="component.containerCost"/>
            </b-col>
            <b-col cols=2>
                <label class="top-label">Other $:</label>
                <input class="form-control" type="number" min=0 v-model="component.otherCost"/>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=2>
                <label class="top-label">Delivery $:</label>
                <input class="form-control" readOnly :value="deliveryCost" />
            </b-col>
            <b-col cols=2>
                <label class="top-label">Duty cost $:</label>
                <input class="form-control" readOnly :value="dutyCost" />
            </b-col>
            <b-col cols=2>
                <label class="top-label">Total Landed Cost $:</label>
                <input class="form-control" readOnly :value="totalLandedCost" />
            </b-col>
            <b-col cols=2>
                <label class="top-label">Case Cost $:</label>
                <input class="form-control" readOnly :value="caseCost" />
            </b-col>
        </b-row>
        <!-- <b-row>
          <b-col cols="4">
            <label class="top-label">Available Items:</label>
            <b-select option-value="id" option-text="name" :list="availableItems" v-model="item" placeholder="Select Item"></b-select>
          </b-col>
          <b-col cols=2 v-if="item.id">
              <label class="top-label">Units:</label>
              <input class="form-control" type="number" min=0 v-model="icUnits"/>
          </b-col>
          <b-col style="padding-top: 30px" cols="1">
            <b-button variant="link" @click="addItem()">(+)</b-button>
          </b-col>
        </b-row> -->
        <b-row>
            <b-col>
              <label class="top-label">Items using this Component:</label>
              <b-table :items="component.itemComponents" :fields="columns">
                  <template v-slot:cell(item.number)="row">
                      <b-button size="sm" @click.stop="goToItem(row.item.item.id)" variant="link">{{row.item.item.number}}</b-button>
                  </template>
                  <template v-slot:cell(action)="row">
                    <b-button size="sm" @click.stop="deleteItemComponent(row.item.id)">x</b-button>
                  </template>

              </b-table>
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
      component: {
          unitCost: 0,
          dutyPercentage: 0,
          deliveryCost: 0,
          containerCost: 0,
          otherCost: 0,
          casePack: 1,
          unitsPerContainer: 1,
          totalLandedCost: 0,
          itemComponents: [],
      },
      uploadedFile: null,
      dimension: "",
      supplier: {},
      category: {},
      availableSuppliers: [],
      availableCategories: [],
      availableItems: [],
      item: {},
      icUnits: 0,
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item.number", label: "Item #", sortable: false },
        { key: "item.name", label: "Name", sortable: false },
        { key: "units", label: "Qty", sortable: false },
        { key: "item.brand.name", label: "Brand", sortable: false },
        { key: "item.unitsReadyProd", label: "RFP", sortable: false },
        // { key: "action", label: "Action", sortable: false },
      ]
    };
  },
  computed: {
      totalLandedCost(){
          return (+this.component.unitCost + +this.component.deliveryCost + +this.component.otherCost).toFixed(2);
      },
      deliveryCost(){
          return (+this.component.containerCost / +this.component.unitsPerContainer).toFixed(2);
      },
      dutyCost: function(){
        return +this.component.unitCost * +this.component.dutyPercentage;
    },
    caseCost(){
           return +this.component.unitCost * +this.component.casePack;
    }
  },
  watch: {
    supplier: function(newValue, oldValue) {
        this.component.supplier=newValue;
    },
    category: function(newValue, oldValue) {
        if(!this.component.category || this.component.category.id != newValue.id){
            this.component.category=newValue;
        }
    },
    deliveryCost: function(newValue, oldValue){
        this.component.deliveryCost = newValue;
    },
    totalLandedCost: function(newValue, oldValue){
        this.component.totalLandedCost = newValue;
    },
  },
  methods: {
    addItem(){
      if (!this.item.id) {
        return;
      }
      var found = this.component.itemComponents.find(it => it.item.id == this.item.id);
      if (found) {
        alert("Item already added")
        return;
      }
      this.getItem(this.item.id).then(item => {
        var ic = {
          item: item,
          units: this.icUnits
        }
        this.component.itemComponents.push(ic);
      })
    },
    onUpload(file){
      this.uploadedFile = file;
    },
    getComponentData(component_id) {
      return http.get("/component/" + component_id).then(r => {
        this.component = r.data;
        if (r.data.supplier) {
          this.supplier = r.data.supplier;
        }
        if (r.data.category) {
          this.category = r.data.category;
        }
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableSuppliers() {
      http
        .get("/supplier/kv")
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
    getAvailableItems() {
      http
        .get("/item/kv")
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItem(item_id) {
      return http.get("/item/"+item_id).then(r => {
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    save() {
      if(!this.component.name || !this.component.number){
        alert("Please enter Component Name and Number");
        return Promise.reject();
      }
      var formData = new FormData();
      formData.append("image", this.uploadedFile);
      formData.append("jsonComponent", JSON.stringify(this.component));
      var headers = {headers: {"Content-Type": "multipart/form-data"}}
      return axios.post(httpUtils.baseUrl + "/component", formData, headers).then(r =>{
        return r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    deleteItemComponent(ic_id) {
      var idx = this.component.itemComponents.findIndex(ic => ic.id == ic_id);
      http.delete("/itemComponent/"+ic_id).then(response => {
            this.component.itemComponents.splice(idx, 1)
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.save().then(c =>{
        this.getComponentData(c.id).then(component => {
          router.push("/componentList");
        })
      })
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
	},
	getImageUrl(){
        if(this.component.attachment){
            return httpUtils.baseUrl + "/attachment/" + this.component.attachment.id;
		}
		return null;
      }
  },
  mounted() {
    var component_id = this.$route.params.component_id;
    if (component_id) {
      this.getComponentData(component_id);
    }
    this.getAvailableSuppliers();
    this.getAvailableCategories();
    this.getAvailableItems();
  },
};
</script>

<style>
</style>
