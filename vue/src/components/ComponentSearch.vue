<template>
  <b-container fluid @click="show">
    <label class="top-label">Items: <a href="#" @click="clearItems()">(x)</a></label>
    <input @keydown.enter="getItems()" class="form-control" type="tel" v-model="searchKey" placeholder="Pick Item">
    <div v-if="visible" class="itemSearchContent">
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
      <b-button type="reset" variant="success" @click="updateParent()">Search</b-button>
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
      firstClick: false,
      skipShow: false,
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
      this.skipShow = true;
      this.items = [];
      this.selectedItems = [];
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
      if(this.skipShow){
        this.skipShow = false;
        return;
      }
      this.getItems();
      this.firstClick = true;
      this.visible = true;
      document.addEventListener("click", this.hide);
    },
    hide(){
      if(!this.firstClick){
        this.visible = false;
        this.searchKey = "";
        document.removeEventListener("click", this.hide);
        this.updateSelected();
      }
      this.firstClick = false;
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
  z-index: 100;
  position: sticky;
  width: 400px;
  background-color:white;
  border: 1px solid gray;
}
</style>
