<template>
  <b-container fluid>
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <input @keydown.enter="getItems()" @click="show" class="form-control" type="tel" v-model="searchKey" placeholder="Pick Item">
    <div v-if="visible" style="margin-top: -30px; z-index: 100; position: sticky; width: 400px;">
      <b-button style="color: black; background-color: white; margin-bottom: -1px; border-bottom: 0px; margin-left: 346px; border-color: gray" size="sm" type="reset" variant="success" @click="closeMenu()">Close</b-button>
      <div style="background-color:white; border: 1px solid gray;">
        <div v-for="item in items" v-bind:key="item.id">
          <input type="checkbox" v-model="item.selected">
          <span>{{item.name}}</span>
        </div>
      </div>
    </div>
    <div v-for="item in selectedItems" v-bind:key="item.id">
      {{item.name}}
    </div>
    <br/>
    <div style="text-align: right;">
      <b-button size="sm" type="reset" variant="success" @click="updateParent()">Search</b-button>
    </div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ComponentSearch",
  props: {
    ss: Number,
  },
  data() {
    return {
      items: [],
      searchKey: "",
      visible: false,
      supplier_id: "",
      selectedItems: [],
      supplier: {}
    };
  },
  computed: {
  },
  watch: {
    supplier(new_value, old_value){
      // this.items = [];
    }
  },
  methods: {
    clearItems(){
      this.items = [];
      this.selectedItems = [];
      this.closeMenu();
    },
    getItems(){
      if(this.items.length == 0){
        http.get("/search/item/kv", { params: {itemName: this.searchKey, supplierId: this.supplierId}}).then(r => {
          r.data.forEach(item => {
            var foundItem = this.selectedItems.find(it => it.id==item.id && it.selected);
            item.selected = foundItem?true:false;
          })
          this.items = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
    },
    getComponents(){
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    init(){
      this.getItems();
    },
    show(){
      this.getItems();
      this.visible = true;
    },
    hide(){
      this.visible = false;
      this.searchKey = "";
      this.updateSelected();
    },
    closeMenu(){
      this.visible = false;
      this.updateSelected();
    },
    updateSelected(){
      this.selectedItems = this.items.filter(item => item.selected == true);
    },
    updateParent(){
      this.skipShow = true;
      this.$emit("componentsUpdated", [{id: 1, name: 'test1'}]);
    }
  },
  mounted() {
  }
};
</script>

<style>
.itemSearchContent {


}
</style>
