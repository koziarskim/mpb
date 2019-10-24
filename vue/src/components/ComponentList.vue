<template>
  <b-container fluid>
      <b-row style="padding-bottom: 4px;">
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Components</span>
      </b-col>
      <b-col cols="3">
        <input class="form-control" type="tel" v-model="nameSearch" @keyup.enter="getComponentsData" placeholder="Search by Number, Name or Supplier"/>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="goToComponent('')">New Component</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="components" :fields="fields">
      <template v-slot:cell(name)="row">
        <b-button size="sm" @click.stop="goToComponent(row.item.id)" variant="link">{{row.item.number}} ({{row.item.name}})</b-button>
      </template>
      <template v-slot:cell(unitsOnStock)="row">
        <b-button size="sm" @click.stop="goToReceiving(row.item.id)" variant="link">{{row.item.unitsOnStock}}</b-button>
      </template>
      <template v-slot:cell(action)="row">
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
      nameSearch: "",
      alertSecs: 0,
      alertMessage: "",
      sortBy: "age",
      sortDesc: false,
      fields: [
        { key: "name", label: "Component # (Name)", sortable: true },
        { key: "categoryName", label: "Category", sortable: true },
        { key: "supplierName", label: "Supplier", sortable: true },
        { key: "unitsOnStock", label: "On Stock", sortable: false },
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
      http
        .get("/component/pageable", {params: {pageable: this.pageable, nameSearch: this.nameSearch}})
        .then(response => {
          this.components = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
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
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete Component?').then(ok => {
        if(ok){
          var item = this.getItem(component_id);
          if (item && item.locked) {
              this.showAlert("Component is locked. It may be currently used by Item(s)");
              return;
          }
          http
              .delete("/component/" + component_id)
              .then(response => {this.getComponentsData();})
              .catch(e => {
              console.log("API Error: " + e);
              });
            }
        })
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
    goToReceiving(component_id){
      var query = { component_id: component_id };
      router.push({ path: "/receivingList", query: query });
    }
  },
  mounted() {
    this.getComponentsData();
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
