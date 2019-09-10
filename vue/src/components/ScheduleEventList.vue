<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Schedule Items: {{item.name}}</span>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="selectedSale"></b-select>
      </b-col>
      <b-col cols=1 offset=5>
        <div style="text-align: right;">
          <b-button type="reset" variant="success" @click="close">Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="scheduleEvents.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="columns">
          <template slot="sale" slot-scope="row">
            <b-button size="sm" @click.stop="goToSale(row.item.saleItem.sale.id)" variant="link">{{row.item.saleItem.sale.number}}</b-button>
          </template>
          <template slot="eventCompleted" slot-scope="row">
            <span v-if="row.item.eventCompleted">Done</span>
            <b-button v-if="!row.item.eventCompleted" :disabled="deleteDisabled(row.item)" size="sm" type="submit" variant="primary" @click="deleteScheduleEvent(row.item.id)">X</b-button>
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
      availableSales: [],
      selectedSale: {},
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
  watch: {
    selectedSale(newValue, oldValue){
      this.getScheduleEvents(this.item.id);
    }
  },
  methods: {
    setup(item_id, sale_id){
      this.getItem(item_id);
      this.getSale(sale_id);
      this.getScheduleEvents(item_id);
    },
    deleteDisabled(se){
      return se.totalProduced > 0;
    },
    deleteScheduleEvent(se_id){
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(value => {
        if(value){
          http.delete("/scheduleEvent/" + se_id).then(response => {
            this.getScheduleEvents(this.item.id);
          }).catch(e => {
            console.log("API error: " + e);
          });
        }
      })
    },
    close() {
        window.history.back();
    },
    getScheduleEvents(item_id){
      http
        .get("scheduleEvent/item/" + item_id)
        .then(response => {
          this.scheduleEvents = [],
          response.data.forEach(se => {
            if(this.selectedSale.id == se.saleItem.sale.id || !this.selectedSale.id){
              this.scheduleEvents.push(se);
            }
            //Remove duplicates.
            if(this.availableSales.find(i => i.id == se.saleItem.sale.id)){
              return;
            }
            this.availableSales.push(se.saleItem.sale);
          })
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
    getSale(sale_id) {
      http
        .get("/sale/" + sale_id)
        .then(response => {
          this.selectedSale = response.data;
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
    var sale_id = this.$route.params.sale_id;
    this.setup(item_id, sale_id);
  }
};
</script>

<style>
</style>
