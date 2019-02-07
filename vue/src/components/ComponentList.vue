<template>
    <b-container fluid>
        <div class="d-flex justify-content-between align-items-center">
            <h2>Component List</h2>
            <div>
                <b-button type="submit" variant="primary" @click="createNewComponent">New Item</b-button>
            </div>
        </div>
        <div v-if="components.length==0">Not found any components...</div>
        <b-table v-if="components.length>0"
                :sort-by.sync="sortBy"
                :sort-desc.sync="sortDesc"
                :items="components"
                :fields="fields">
                <template slot="stockNumber" slot-scope="row">
                    <b-button size="sm" variant="link">{{row.item.stockNumber}}</b-button>
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
        { key: 'stockNumber', sortable: true },
        { key: 'name', sortable: true },
        { key: 'description', sortable: true },
        { key: 'assumedPrice', sortable: false },
        { key: 'action', sortable: false}
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
        .get("/components")
        .then(response => {
          this.components = response.data;
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    deleteComponent(item_id) {
      http
        .delete("/components/"+item_id)
        .then(response => {
          this.getComponentsData();
        })
        .catch(e => {
            this.showAlert(e.response.data.message)
        });
    },
    createNewComponent(){
        router.push('/editComponent');
    },
  },
  mounted() {
     this.getComponentsData();
  }
};
</script>

<style>
</style>
