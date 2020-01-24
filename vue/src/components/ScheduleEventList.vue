<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3 style="margin-top: -7px">
        <span style="font-size: 18px; font-weight: bold;">Schedule for Item:</span>
            <b-button size="sm" id="item-popover" variant="link">{{item.name}}</b-button>
            <b-popover placement="bottomright" target="item-popover" triggers="focus" variant="info">
              <template v-slot:title>
                <b-button size="sm" @click="goToItem(item.id)" variant="link">View Item Details</b-button>
              </template>
              <div>Avg Performance: {{item.performance}} [units per hour]</div>
            </b-popover>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="number" :list="availableSales" v-model="selectedSale" placeholder="Pick Sale"></b-select>
      </b-col>
      <b-col cols=1 offset=4>
        <div style="display: flex; text-align: right; margin-left: 20px">
          <b-button style="margin: 3px" type="reset" variant="success" @click="goToGraph()" :disabled="false">Graph</b-button>
          <b-button style="margin: 3px" type="reset" variant="success" @click="close()">Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table :sort-compare="sortCompare" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="columns">
          <template v-slot:cell(sale)="row">
            <b-link role="button" @click.stop="goToSale(row.item.saleItem.sale.id)">{{row.item.saleItem.sale.number}}</b-link>
            <span style="font-size: 11px"> ({{row.item.saleItem.sale.customer.name}})</span>
          </template>
          <template v-slot:cell(unitsProduced)="row">
            <b-button size="sm" @click.stop="goToProduction(row.item)" variant="link">{{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(performance)="row">
            <span :style="getEfficiencyStyle(row.item.efficiency)">{{row.item.performance}} ({{row.item.efficiency}}%)</span>
          </template>
          <template v-slot:cell(unitsScheduled)="row">
            <b-button v-if="!row.item.edit" @click="editScheduleEvent(row.item)" variant="light">{{row.item.unitsScheduled}}</b-button>
            <b-input-group>
              <b-form-input style="width:100px" v-if="row.item.edit" class="form-control" type="tel" v-model="row.item.unitsScheduled">
              </b-form-input>
              <b-input-group-append>
                <b-button v-if="row.item.edit" style="margin-left: 5px" variant="link" @click="saveScheduleEvent(row.item)">save</b-button>
              </b-input-group-append>
            </b-input-group>
          </template>
          <template v-slot:cell(action)="row">
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
        { key: "sale", label: "Sale (Customer)", sortable: true },
        { key: "schedule.date", label: "Date", sortable: true },
        { key: "startTime", label: "Started", sortable: false },
        { key: "finishTime", label: "Finished", sortable: false },
        { key: "performance", label: "Perform [u/h]", sortable: true },
        { key: "line.number", label: "Line", sortable: true },
        { key: "sale", label: "Sale", sortable: true },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ]
    };
  },

  computed: {},
  watch: {
    selectedSale(newValue, oldValue){
      if(this.item.id){
        this.getScheduleEvents(this.item.id);
      }
    }
  },
  methods: {
    setup(item_id, sale_id){
      this.getItem(item_id);
      if(sale_id){
        this.getSale(sale_id);
      }
      this.getScheduleEvents(item_id);
    },
    getEfficiencyStyle(eff){
      var style = "color: ";
      style += eff>=100?"#28a745":"#e25454";
      return style;
    },
    sortCompare(a, b, key) {
      if (key === 'sale') {
        return  a.saleItem.sale.number.localeCompare(b.saleItem.sale.number);
      }
    },
    editScheduleEvent(se){
      se.edit = true;
    },
    saveScheduleEvent(se){
      if(se.unitsScheduled > se.saleItem.units){
        alert("Cannot schedule more that sold");
        return;
      }
      if(se.unitsScheduled < se.unitsProduced){
        alert("Cannot schedule less than produced");
        return;
      }
      http.post("/scheduleEvent", se).then(response => {
        this.getScheduleEvents(this.item.id)
      }).catch(e => {
        console.log("API error: " + e);
      });
      se.edit = false;
    },
    deleteDisabled(se){
      return se.unitsProduced > 0;
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
            se.edit = false;
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
    goToItem(itemId){
      router.push("/itemEdit/"+itemId);
    },
    goToProduction(se) {
      var query = { date: se.schedule.date, seId: se.id };
      router.push({ path: "/productionLine/"+se.line.id, query: query } );
    },
    goToGraph() {
      router.push("/itemGraph/" + this.item.id);
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
