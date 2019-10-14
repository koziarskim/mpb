<template>
  <b-container fluid @click="show">
    <label class="top-label">Items:</label>
    <input @keydown.enter="getItems()" class="form-control" type="tel" v-model="searchKey" placeholder="Pick Item">
    <div v-if="visible" class="itemSearchContent">
      <div v-for="item in items" v-bind:key="item.id">
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
    selectedItems: Object,
    schedule: Object,
  },
  data() {
    return {
      firstClick: false,
      items: [],
      searchKey: "",
      visible: false,
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    getItems(){
      http.get("/item/kv").then(r => {
        this.items = r.data;
        this.$emit("itemsUpdated", r.data)
      }).catch(e => {
        console.log("API error: " + e);
      });
      this.searchKey = "";
    },
    init(){
      this.getItems();
    },
    show(){
      if(!this.visible){
        this.init();
      }
      this.firstClick = true;
      this.visible = true;
      document.addEventListener("click", this.hide);
    },
    hide(){
      if(!this.firstClick){
        this.visible = false;
        this.searchKey = "";
        document.removeEventListener("click", this.hide);
      }
      this.firstClick = false;
      this.items = [];
    },
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
