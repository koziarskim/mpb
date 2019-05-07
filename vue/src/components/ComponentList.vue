<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <span style="text-align: left; font-size: 18px; font-weight: bold">Components</span>
      <div style="text-align: right;">
        <b-button type="submit" variant="primary" @click="goToComponent('')">New Component</b-button>
      </div>
    </div>
    <div v-if="components.length==0">Not found any components...</div>
    <b-table v-if="components.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="components" :fields="fields">
      <template slot="number" slot-scope="row">
        <b-button size="sm" @click.stop="goToComponent(row.item.id)" variant="link">{{row.item.number}}</b-button>
      </template>
      <template slot="units" slot-scope="row">
        <b-button size="sm" @click.stop="goToComponentPurchaseList(row.item.id)" variant="link">{{row.item.unitsOrdered}} / {{row.item.unitsInTransit}}</b-button>
      </template>
      <template slot="action" slot-scope="row">
        <b-button size="sm" @click.stop="deleteComponent(row.item.id)" :disabled="row.item.locked" >x</b-button>
      </template>
    </b-table>
    <b-alert :show="alertSecs" dismissible variant="warning" @dismiss-count-down="(secs) => { alertSecs = secs }">{{alertMessage}}</b-alert>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  name: "edit-component",
  data() {
    return {
      alertSecs: 0,
      alertMessage: "",
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "number", label: "Component #", sortable: false },
        { key: "name", label: "Name", sortable: false },
        { key: "units", label: "Awaiting/Transit", sortable: false },
        { key: "unitsOnStack", label: "On Stack", sortable: false },
        { key: "unitsReserved", label: "Reserved", sortable: false },
        { key: "category.name", label: "Category", sortable: false },
        { key: "supplier.name", label: "Supplier", sortable: false },
        { key: "action", label: "Action", sortable: false }
      ],
      components: []
    };
  },
  methods: {
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getComponentsData() {
      var apiCounter = new Date().getTime();
      http
        .get("/component")
        .then(response => {
          this.components = response.data;
          console.log(new Date().getTime() - apiCounter);
          console.log("Success getting component data");
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItem(component_id) {
      var component;
      var found = this.components.some(function(element) {
        if (element.id === component_id) {
          component = element;
        }
      });
      return component;
    },
    deleteComponent(component_id) {
      var item = this.getItem(component_id);
      if (item && item.locked) {
        this.showAlert(
          "Component is locked. It may be currently used by Item(s)"
        );
        return;
      }
      http
        .delete("/component/" + component_id)
        .then(response => {
          this.getComponentsData();
        })
        .catch(e => {
          console.log("API Error: " + e);
        });
    },
    goToComponent(component_id) {
      if (!component_id) {
        http
          .post("/component")
          .then(response => {
            router.push("/componentEdit/" + response.data.id);
          })
          .catch(e => {
            console.log("API Error: " + e);
          });
      } else {
        router.push("/componentEdit/" + component_id);
      }
    },
    goToComponentPurchaseList(component_id) {
      router.push("/ComponentPurchaseList/" + component_id);
    }
  },
  mounted() {
    this.getComponentsData();
  }
};
</script>

<style>
</style>
