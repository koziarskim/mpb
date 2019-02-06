<template>
    <b-container fluid>
        <h1>New Item</h1>
        <b-row>
            <b-col cols=1>
                <label>Name:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="item.name" placeholder="Enter your name"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Stock#:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="item.stockNumber" placeholder="Enter MPB stock number"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Price:</label>
            </b-col>
            <b-col cols=6>
                <b-form-input type="text" v-model="item.assumedPrice" placeholder="Enter assumed price"></b-form-input>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=1>
                <label>Comp:</label>
            </b-col>
            <b-col cols=6>
                <b-select :options="components" v-model="component" placeholder="Select component"></b-select>
            </b-col>
        </b-row>
        <b-row>
            <b-col cols=5></b-col>
            <b-col cols=1>      
                <b-button type="submit" variant="primary" @click="save">Submit</b-button>
            </b-col>
            <b-col cols=1>
                <b-button type="reset" variant="danger" @click="cancel">Reset</b-button>
            </b-col>
        </b-row>
        <div>{{component}}</div>
    </b-container>
</template>

<script>
import http from "../http-common";

export default {
  name: "edit-component",
  data() {
    return {
      item: {},
      component: {},
      components: [
          { value: '1', text: 'aa' + ' - ' + '1' },
          { value: '2', text: 'ab' + ' - ' + '2' },
          { value: '3', text: 'bc' + ' - ' + '3' },
          { value: '4', text: 'cd' + ' - ' + '4' },
          { value: '5', text: 'de' + ' - ' + '5' }
        ]
    };
  },
  methods: {
    save() {
      http
        .post("/items", this.item)
        .then(response => {
          console.log("Success post");
        })
        .catch(e => {
          console.log("Error post");
        });
    },
    cancel() {
      this.item = {};
    }
  }
};
</script>

<style>
</style>
