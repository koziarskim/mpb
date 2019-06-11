<template>
  <b-container fluid>
    <div class="d-flex justify-content-between align-items-center">
      <span style="text-align: left; font-size: 18px; font-weight: bold">Components</span>
      <div style="text-align: right;">
        <b-button type="submit" variant="primary" @click="goToComponent('')">New Component</b-button>
      </div>
    </div>
    <div v-if="components.length==0">Not found any components...</div>
    <b-table v-if="components.length>0" no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
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
    <b-pagination
      v-model="pageable.currentPage"
      :per-page= "pageable.perPage"
      :total-rows="pageable.totalElements"
      @change="paginationChange"
    ></b-pagination>
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
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'number', sortDesc: false},
      limit: 6,
      alertSecs: 0,
      alertMessage: "",
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "number", label: "Component #", sortable: true },
        { key: "name", label: "Name", sortable: true },
        { key: "units", label: "Awaiting/Transit", sortable: false },
        { key: "unitsOnStack", label: "On Stack", sortable: false },
        { key: "unitsReserved", label: "Reserved", sortable: false },
        { key: "category.name", label: "Category", sortable: true },
        { key: "supplier.name", label: "Supplier", sortable: true },
        { key: "action", label: "Action", sortable: false }
      ],
      components: []
    };
  },
  methods: {
    sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getComponentsData();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getComponentsData();
    },
    showAlert(message) {
      (this.alertSecs = 3), (this.alertMessage = message);
    },
    getComponentsData() {
      var apiCounter = new Date().getTime();
      http
        .get("/component/pageable", {params: {pageable: this.pageable}})
        .then(response => {
          this.components = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
          console.log(new Date().getTime() - apiCounter);
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
