<template>
    <b-container fluid>
        <div style="border: 0px" class="d-flex justify-content-between align-items-center">
            <h2>New Item</h2>
            <div>
                <b-button type="submit" variant="primary" @click="save">Submit</b-button>
                <b-button type="reset" variant="danger" @click="cancel">Reset</b-button>
            </div>
        </div>
        <b-row>
        <b-col cols=6>
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
                    <b-form-input type="text" v-model="item.stockNumber" placeholder="Enter MPB stock number"></b-form-input>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=2>
                    <label>Price:</label>
                </b-col>
                <b-col cols=10>
                    <b-form-input type="text" v-model="item.assumedPrice" placeholder="Enter assumed price"></b-form-input>
                </b-col>
            </b-row>
        </b-col>
        <b-col cols=6>
            <b-row>
                <b-col cols=2>
                    <label>Comp:</label>
                </b-col>
                <b-col cols=10>
                    <b-select option-value="id" option-text="label" :list="avaliable_components" v-model="component" placeholder="Select component"></b-select>
                </b-col>
            </b-row>
            <b-row>
                <b-col cols=2></b-col>
                <b-col cols=10>
                        <div style="border-bottom: 1px solid #ced4da" v-for="it in components" v-bind:key="it.id" class="d-flex justify-content-between align-items-center">
                            {{it.label+", "+it.desc}}
                            <b-button size="sm" type="reset" variant="danger" @click="removeComponent(it.id)">x</b-button>
                        </div>                
                </b-col>
            </b-row>
        </b-col>
        </b-row>
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
      components: [],
      avaliable_components: [
        { id: "1", label: "aa-1", desc: "Walmart cub" },
        { id: "2", label: "ab-2", desc: "desc" },
        { id: "3", label: "bc-3", desc: "desc" },
        { id: "4", label: "cd-4", desc: "desc" },
        { id: "5", label: "de-5", desc: "desc" }
      ]
    };
  },
  watch: {
    component: function(new_value, old_value) {
      if (new_value.id) {
        var found = this.components.some(function(element) {
          return element.id === new_value.id;
        });
        if (!found) {
          this.components.push(new_value);
        }
      }
    }
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
      this.components = [];
      this.component = {};
    },
    removeComponent(comp_id) {
      console.log("remove comp");
      for (var i = 0; i < this.components.length; i++) {
        if (this.components[i].id == comp_id) {
          this.components.splice(i, 1);
          break;
        }
      }
    }
  }
};
</script>

<style>
</style>
