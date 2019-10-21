<template>
  <b-container fluid>
    <b-row>
      <b-col cols="2">
        <span style="text-align: left; font-size: 18px; font-weight: bold">Item: {{item.name}}</span>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Components Invetory:</label>
        <b-table v-if="itemComponents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="itemComponents" :fields="columns">
          <template v-slot:cell(component)="row">
            <b-button size="sm" @click.stop="goToComponentEdit(row.item.component.id)" variant="link">{{row.item.component.number}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="goToReceiving(row.item.component.id)">Receivings</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      itemComponents: [],
      item: {},
      sortBy: "number",
      sortDesc: false,
      columns: [
        { key: "component", label: "Component #", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "component.unitsOnStock", label: "On Stock", sortable: false },
        { key: "component.unitsOrdered", label: "Awaiting", sortable: false },
        { key: "component.unitsInTransit", label: "In Transit", sortable: false },
      ],
    };
  },
  computed: {},
  watch: {},
  methods: {
    getItem(item_id) {
      http
        .get("/item/" + item_id)
        .then(response => {
          this.item = response.data;
          this.getItemComponents(item_id);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItemComponents(item_id) {
      http
        .get("/itemComponent/item/" + item_id)
        .then(response => {
          this.itemComponents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToComponentEdit(component_id) {
      router.push("/componentEdit/" + component_id);
    },
  },
  mounted() {
    var item_id = this.$route.params.item_id;
    if (item_id) {
      this.getItem(item_id);
    }
  }
};
</script>

<style>
</style>
