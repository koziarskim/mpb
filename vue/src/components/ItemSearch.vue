<template>
  <b-container fluid @click="show">
    <label class="top-label">Items:</label>
    <input class="form-control" type="tel" v-model="searchKey" placeholder="Pick Item">
    <div v-if="visible" class="itemSearchContent">
      <div>testing akfdsj adsf asdkflj asdfkj asdlfj aldksjf asldkjf akdsjf alksdjf l</div>
      <div>testin adskj asdf asdkfj a a dfslkjasd lkajds fla lakdsjfl aklsdjff adksfj g</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
      <div>testing</div>
    </div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ItemSearch",
  props: {
    scheduleEvent: Object,
    schedule: Object,
  },
  data() {
    return {
      firstClick: false,
      availableItems: [],
      searchKey: "",
      visible: false,
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    show(){
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
