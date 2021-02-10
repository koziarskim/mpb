<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=1 offset=9>
        <div>
          <b-button size="sm" variant="primary" @click="adjustStock()">Adjust</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table no-local-sorting @sort-changed="sorted" :items="componentAdjustments" :fields="fields">
      <template v-slot:cell(component)="row">
        {{row.item.componentNumber + " - " + row.item.componentName}}
      </template>
      <template v-slot:cell(unitsAdjusted)="row">
          <b-button size="sm" @click="adjustStock(row.item.id)" variant="link">{{row.item.unitsAdjusted}}</b-button>
      </template>
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>
    <div v-if="componentAdjustmentModalVisible">
      <component-adjustment-modal :component-id="componentKv.id" :component-adjustment-id="componentAdjustmentId" v-on:close="closeComponentAdjustmentModal"></component-adjustment-modal>
    </div>    
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  name: "ComponentAdjustmentList",
  components: {
    ComponentAdjustmentModal: () => import("./modals/ComponentAdjustmentModal"),
  },
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'date', sortDesc: true},
      fields: [
        { key: "component", label: "Component", sortable: false },
        { key: "unitsAdjusted", label: "Units", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "reason", label: "Reason", sortable: false },
      ],
      componentAdjustments: [],
      availableComponents: [],
      componentKv: {},
      componentAdjustmentModalVisible: false,
      componentAdjustmentId: null,
    };
  },
  watch: {
    componentKv(new_value, old_value){
      this.getComponentAdjustments();
    },
  },
  methods: {
    adjustStock(componentAdjustmentId){
      if(!componentAdjustmentId && !this.componentKv.id){
        alert("Please, select component to adjust");
        return false;
      }
      this.componentAdjustmentId = componentAdjustmentId;
      this.componentAdjustmentModalVisible = true;
    },
    closeComponentAdjustmentModal(){
      this.componentAdjustmentModalVisible = false;
      this.getComponentAdjustments();
    },
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
      });
    },
    getAvailableComponents() {
      http.get("/component/kv").then(r => {
        this.availableComponents = r.data;
      });
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
    var componentId = this.$route.query.componentId;
    if(componentId){
      this.componentKv = {id: componentId}
    }
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
