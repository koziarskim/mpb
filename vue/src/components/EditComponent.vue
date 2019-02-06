<template>
    <b-container fluid>
        <h1>New Component</h1>
        <b-row>
            <b-col cols=1>
                <label>Name:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="component.name" placeholder="Enter your name"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Stock#:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="component.stockNumber" placeholder="Enter MPB stock number"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Stock#:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="component.description" placeholder="Enter short description"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Price:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="component.assumedPrice" placeholder="Enter assumed price"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=5></b-col>
            <b-col cols=1>      
                <b-button type="submit" variant="primary" @click="saveComponent">Submit</b-button>
            </b-col>
            <b-col cols=1>
                <b-button type="reset" variant="danger" @click="cancelComponent">Reset</b-button>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "add-component",
  data() {
    return {
      component: {},
    };
  },
  methods: {
    saveComponent() {
        this.component.itemId = 6;
      http
        .post("/components", this.component)
        .then(response => {
            router.push("./ComponentList")
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    cancelComponent() {
      this.component = {};
    }
  }
};
</script>

<style>
</style>
