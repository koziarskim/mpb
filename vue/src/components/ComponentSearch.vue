<template>
  <b-container fluid>
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <div style="display: flex">
      <input @keydown.enter="getItems(true)" @click="showItemMenu" class="form-control search-width" type="tel" v-model="itemSearchKey" placeholder="Pick Item">
      <b-button v-if="visibleItemMenu" class="btn-tab" size="sm" type="reset" variant="success" @click="closeItemMenu()">Close</b-button>
    </div>
    <div v-if="visibleItemMenu" class="menu-tab">
        <div v-for="item in items" v-bind:key="item.id">
          <input type="checkbox" v-model="item.selected">
          <span>{{item.name}}</span>
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
      itemSearchKey: "",
      visibleItemMenu: false,
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
      this.closeItemMenu();
    },
    getItems(fresh){
      if(this.items.length == 0 || fresh){
        return http.get("/search/item/kv", { params: {itemName: this.itemSearchKey, supplierId: this.supplierId}}).then(r => {
          r.data.forEach(item => {
            var foundItem = this.selectedItems.find(it => it.id==item.id && it.selected);
            item.selected = foundItem?true:false;
          })
          this.items = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
      return Promise.resolve();
    },
    getComponents(){
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    showItemMenu(){
      this.getItems().then(r => {
        this.visibleItemMenu = true;
      });
    },
    closeItemMenu(){
      this.visibleItemMenu = false;
      this.selectedItems = this.items.filter(item => item.selected == true);
    },
    updateParent(){
      this.$emit("componentsUpdated", [{id: 1, name: 'test1'}]);
    }
  },
  mounted() {
  }
};
</script>

<style>
.menu-tab {
  z-index: 100; 
  position: sticky; 
  width: 400px;
  background-color:white; 
  border: 1px solid gray;
}
.btn-tab {
  color: black !important; 
  background-color: white !important; 
  margin-bottom: -1px !important; 
  border-bottom: 0px !important; 
  margin-left: 156px; 
  border-color: gray !important;
  z-index: 110 !important;
}
.search-width {
  width: 190px !important;
}
</style>
