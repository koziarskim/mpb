<template>
  <b-container fluid>
    <div style="border: 0px" class="d-flex justify-content-between align-items-center">
      <h4 style="text-align: left;">Schedule Items: {{item.number}}</h4>
      <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="close">Close</b-button>
      </div>
    </div>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="scheduleEvents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="columns">
          <template slot="sale" slot-scope="row">
            <b-button size="sm" @click.stop="goToSale(row.item.saleItem.sale.id)" variant="link">{{row.item.saleItem.sale.number}}</b-button>
          </template>
          <template slot="eventCompleted" slot-scope="row">
            <span>{{row.item.eventCompleted?"Yes":"No"}}</span>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      scheduleEvents: [],
      item: {},
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "schedule.date", label: "Date", sortable: false },
        { key: "startTime", label: "Time", sortable: false },
        { key: "line.number", label: "Line", sortable: false },
        { key: "sale", label: "Sale", sortable: false },
        { key: "saleItem.sale.customer.name", label: "Customer", sortable: false },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "totalProduced", label: "Produced", sortable: false },
        { key: "eventCompleted", label: "Completed", sortable: false },
      ]
    };
  },

  computed: {},
  watch: {},
  methods: {
    close() {
        window.history.back();
    },
    getScheduleEvents(item_id){
      http
        .get("scheduleEvent/item/" + item_id)
        .then(response => {
          this.scheduleEvents = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItem(item_id) {
      http
        .get("/item/" + item_id)
        .then(response => {
          this.item = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
  },
  mounted() {
    var item_id = this.$route.params.item_id;
    this.getScheduleEvents(item_id);
    this.getItem(item_id);
  }
};
</script>

<style>
</style>
