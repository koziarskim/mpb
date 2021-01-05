<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="componentAdjustments" :fields="fields">
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  name: "ComponentList",
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'date', sortDesc: true},
      fields: [
        { key: "id", label: "ID", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      componentAdjustments: [],
      availableComponents: [],
      componentKv: {},
    };
  },
  watch: {
    componentKv(new_value, old_value){
      this.getComponentAdjustments();
    },
  },
  methods: {
    sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getComponentAdjustments();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getComponentAdjustments();
    },
    getComponentAdjustments() {
      var query = {params: {pageable: this.pageable, componentId: this.componentKv.id}};
      http.get("/componentAdjustment/pageable", query).then(response => {
        this.componentAdjustments = response.data.content;
        this.pageable.totalElements = response.data.totalElements;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableComponents() {
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    goToComponent(component_id) {
      if (!component_id) {
        router.push("/componentEdit");
      } else {
        router.push("/componentEdit/" + component_id);
      }
    },
  },
  mounted() {
    this.getAvailableComponents();
  },
  activated(){
    this.getComponentAdjustments();
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
