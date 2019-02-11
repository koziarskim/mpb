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
                    <label>Stock#:</label>
                </b-col>
                <b-col cols=10>
                    <b-form-input type="text" v-model="item.stockNumber" placeholder="Enter MPB stock number"></b-form-input>
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
                <b-col cols=2>
                    <label>Stock#:</label>
                </b-col>
                <b-col cols=10>
                    <b-form-input type="text" v-model="item.description" placeholder="Enter short description"></b-form-input>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=3>
                    <label>Total Price:</label>
                </b-col>
                <b-col cols=8>
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
                    <b-select option-value="id" option-text="stockNumber" :list="avaliable_components" v-model="component" placeholder="Select component"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=2></b-col>
                <b-col cols=10>
                        <div style="display: flex; border-bottom: 1px solid #ced4da" v-for="ic in item.itemComponents" v-bind:key="ic.id">
                            <div style="width:100%">
                                <input size="sm" style="border: 0px; width: 25px" min=1 max=9 v-model="ic.units" type="number"/>
                                <b-button variant="link" @click="httpUtils.goTo('/editComponent/'+ic.component.id)">{{ic.component.stockNumber}}</b-button>
                                <label>{{ic.component.name+", "+", $"+ic.component.assumedPrice}}</label>
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
import httpUtils from "../httpUtils"

export default {
  name: "edit-component",
  data() {
    return {
        httpUtils: httpUtils,
      item: {
        itemComponents: [],
      },
      component: {},
      avaliable_components: [
        { id: "1", name: "aa-1", desc: "Walmart cub" },
        { id: "2", name: "ab-2", desc: "desc" },
        { id: "3", name: "bc-3", desc: "desc" },
        { id: "4", name: "cd-4", desc: "desc" },
        { id: "5", name: "de-5", desc: "desc" }
      ]
    };
  },
  computed: {
    totalPrice: function() {
      var totalPrice = 0;
      this.item.itemComponents.forEach(ic =>{
          totalPrice= +totalPrice 
          + ((+ic.component.assumedPrice + +ic.component.dutyFee + +ic.component.deliveryFee) * +ic.units);
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
              component: new_component});
        }
      }
    }
  },
  methods: {
    getComponentsData() {
      http
        .get("/component")
        .then(response => {
          this.avaliable_components = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItemData(item_id) {
      http
        .get("/item/"+item_id)
        .then(response => {
          this.item = response.data;
          console.log("Success getting item data");
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
          console.log("Error post"+e);
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
  created() {
    this.getComponentsData();
    var item_id = this.$route.params.item_id;
    if(item_id){
        this.getItemData(item_id);
    }
  }
};
</script>

<style>
</style>
