<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Item</h2>
            <div>
                <b-button type="submit" variant="primary" @click="saveAndUpload">Submit</b-button>
                <b-button type="reset" variant="danger" @click="cancelItem">Cancel</b-button>
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
                        <b-col cols=12>
                            <label>Total Price: {{totalPrice}}</label>
                        </b-col>
                    </b-row>
                </b-col>
                <b-col cols=5>
                    <b-row>
                        <b-col cols=12>
                            <b-form-file type="file" v-model="image"/>
                        </b-col>
                    </b-row>
                    <b-row>
                        <b-col>
                            <b-img width="300px" height="300px" :src="imageUrl" fluid />
                        </b-col>
                    </b-row>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=12>
                    <hr class="hr-text" data-content="Unit prices/fees are in USD ($)">
                </b-col>
            </b-row>
        </b-col>
        <!-- Column 2 -->
        <b-col cols=4 style="border-left: 1px solid #dededf;">
            <b-row>
                <b-col cols=12>
                    <b-select option-value="id" option-text="number" :list="availableComponents" v-model="component" placeholder="Select component"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=12>
                        <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in item.itemComponents" v-bind:key="ic.id">
                            <div style="width:100%">
                                <input size="sm" style="border: 0px; width: 25px" min=1 max=9 v-model="ic.units" type="number"/>
                                <b-button variant="link" @click="httpUtils.goTo('/componentEdit/'+ic.component.id)">{{ic.component.number}}</b-button>
                                <label>{{" | "+ic.component.name+" | $"+ic.component.totalPrice}}</label>
                            </div>
                            <b-button size="sm" type="reset" variant="link" @click="removeItemComponent(ic.id)">(x)</b-button>
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
      image: "",
      imageUrl: "",
      httpUtils: httpUtils,
      brand: {},
      item: {
        itemComponents: []
      },
      component: {},
      availableBrands: [],
      availableComponents: []
    };
  },
  computed: {
    totalPrice: function() {
      var totalPrice = 0;
      this.item.itemComponents.forEach(ic => {
        totalPrice = +totalPrice + +ic.component.totalPrice * +ic.units;
      });
      return totalPrice;
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
          if (response.data.attachment) {
            this.imageUrl =
              httpUtils.baseUrl + "/attachment/" + response.data.attachment.id;
          }
          if (response.data.brand) {
            this.brand = response.data.brand;
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
        .then(function() {
          window.history.back();
        })
        .catch(function() {
          console.log("FAILURE!!");
        });
    },
    cancelItem() {
      window.history.back();
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
  }
};
</script>

<style>
</style>
