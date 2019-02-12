<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2>Component List</h2>
            <div>
                <b-button type="submit" variant="primary" @click="goToComponent('')">New Item</b-button>
            </div>
        </div>
        <div v-if="components.length==0">Not found any components...</div>
        <b-table v-if="components.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="components"
                :fields="fields">
                <template slot="stockNumber" slot-scope="row">
                    <b-button size="sm" @click.stop=goToComponent(row.item.id) variant="link">{{row.item.stockNumber}}</b-button>
                </template>
                <template slot="action" slot-scope="row">
                    <b-button size="sm" @click.stop="deleteComponent(row.item.id)">x</b-button>
                </template>
        </b-table>
        <b-alert :show="alertSecs" dismissible variant="warning" @dismiss-count-down="(secs) => { alertSecs = secs }">
                {{alertMessage}}
        </b-alert>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "edit-component",
  data() {
    return {
      alertSecs: 0,
      alertMessage: "",
      sortBy: 'age',
      sortDesc: false,
      fields: [
        { key: 'stockNumber', label: 'Stock #', sortable: true },
        { key: 'name', label: 'Name', sortable: true },
        { key: 'description', label: 'Description', sortable: true },
        { key: 'totalPrice', label: 'Price', sortable: false },
        { key: 'action', label: 'Action', sortable: false}
      ],
      components: []
    };
  },
  methods: {
    showAlert (message) {
      this.alertSecs = 3,
      this.alertMessage = message
    },
    getComponentsData() {
      http
        .get("/component")
        .then(response => {
          this.components = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    getItem(component_id){
        var component;
        var found = this.components.some(function(element) {
           if(element.id === component_id){
                component = element;
           }
        });
        return component;
    },
    deleteComponent(component_id) {
        var item = this.getItem(component_id);
        if(item && item.locked){
            this.showAlert("Component is locked. It may be currently used by Item(s)")
            return;
        }
      http
        .delete("/component/"+component_id)
        .then(response => {
          this.getComponentsData();
        })
        .catch(e => {
            this.showAlert(e)
        });
    },
    goToComponent(component_id){
        router.push('/componentEdit/'+component_id);
    },
  },
  mounted() {
     this.getComponentsData();
  }
};
</script>

<style>
</style>
