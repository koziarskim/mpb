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
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ItemSearch",
  props: {
    selectedItems: Array,
    supplierId: Number,
  },
  data() {
    return {
      firstClick: false,
      skipShow: false,
      items: [],
      searchKey: "",
      visible: false,
      supplier_id: ""
    };
  },
  computed: {
  },
  watch: {
    supplierId(new_value, old_value){
      if(this.supplier_id != new_value){
        this.items = [];
        this.supplier_id = new_value;
      }
    }
  },
  methods: {
    clearItems(){
      this.items = [];
      this.$emit("itemsUpdated", [])
      this.skipShow = true;
    },
    getItems(){
      if(this.items.length == 0){
      http.get("/item/kv", { params: {itemName: this.searchKey, supplierId: this.supplierId}}).then(r => {
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
      var selectedItems = this.items.filter(item => item.selected == true);
      this.$emit("itemsUpdated", selectedItems)
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
