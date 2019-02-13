<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Item</h2>
            <div>
                <b-button type="submit" variant="primary" @click="saveItem">Submit</b-button>
                <b-button type="reset" variant="danger" @click="cancelItem">Cancel</b-button>
            </div>
        </div>
        <b-row>
        <b-col cols=6>
            <b-row>
                <b-col cols=2>
                    <label>Brand:</label>
                </b-col>
                <b-col cols=6>
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
                <b-col cols=6>
                    <b-form-input type="text" v-model="item.name" placeholder="Enter your name"></b-form-input>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=12>
                    <b-form-textarea type="text" :rows=3 v-model="item.description" placeholder="Enter short description"></b-form-textarea>
                </b-col>                
            </b-row>
            <b-row>
                <b-col cols=3>
                    <label>Total Price:</label>
                </b-col>
                <b-col cols=3>
                    <label>{{totalPrice}}</label>
                </b-col>
            </b-row>
        </b-col>
        <b-col cols=6>
            <b-row>
                <b-col cols=2>
                    <label>Comp:</label>
                </b-col>
                <b-col cols=10>
                    <b-select option-value="id" option-text="label" :list="availableComponents" v-model="component" placeholder="Select component"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=2></b-col>
                <b-col cols=10>
                        <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in item.itemComponents" v-bind:key="ic.id">
                            <div style="width:100%">
                                <input size="sm" style="border: 0px; width: 25px" min=1 max=9 v-model="ic.units" type="number"/>
                                <b-button variant="link" @click="httpUtils.goTo('/componentEdit/'+ic.component.id)">{{ic.component.number}}</b-button>
                                <label>{{" | "+ic.component.name+" | "+ic.component.category.name+" | $"+ic.component.totalPrice}}</label>
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
import httpUtils from "../httpUtils";

export default {
  name: "edit-component",
  data() {
    return {
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
        totalPrice =
          +totalPrice +
          (+ic.component.assumedPrice +
            +ic.component.dutyFee +
            +ic.component.deliveryFee) *
            +ic.units;
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
    saveItem() {
      http
        .post("/item", this.item)
        .then(response => {
          console.log("Success post");
          window.history.back();
        })
        .catch(e => {
          console.log("Error post" + e);
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
