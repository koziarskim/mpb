<template>
  <b-container fluid @focus="focusEvent()" @blur="blurEvent()">
    <label class="top-label">Items:</label>
    <input @click="show" class="form-control" type="tel" v-model="searchKey" placeholder="Pick Item">
    <div v-if="visible" @mousedown="mouseDown()" class="itemSearchContent">
      <div style="display: flex;">
        <div>testing</div><b-button variant="success" @click="hide">Close</b-button>
      </div>
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
    hideContent: Boolean,
  },
  data() {
    return {
      isFocus: false,
      availableItems: [],
      searchKey: "",
      visible: this.hideContent,
    };
  },
  computed: {
  },
  watch: {
  },
  methods: {
    focusEvent(){
      this.isFocus = true;
    },
    blurEvent(){
      this.isFocus = false;
    },
    mouseDown(){
      console.log("Mouse down")
    },
    show(){
      this.visible = true;
      document.addEventListener('click',this.clickEvent);
    },
    clickEvent(){
      console.log(this.isFocus)
      if(!this.isFocus){
        this.visible = false;
        this.searchKey = "";
        document.removeEventListener('click',this.clickEvent);
      }
    },
    hide(){
      if(!this.isFocus){
        this.visible = false;
        this.searchKey = "";
        document.removeEventListener('click',this.clickEvent);
      }
    },
    closeModal() {
      this.$emit("closeModal");
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
