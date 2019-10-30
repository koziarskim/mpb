<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px;">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Schedule Items: {{item.name}}</span>
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
        <b-table v-if="scheduleEvents.length>0" :sort-compare="sortCompare" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="columns">
          <template v-slot:cell(sale)="row">
            <b-button size="sm" @click.stop="goToSale(row.item.saleItem.sale.id)" variant="link">{{row.item.saleItem.sale.number}}</b-button>
          </template>
          <template v-slot:cell(startTime)="row">
            <b-button size="sm" @click.stop="goToProduction(row.item.id)" variant="link">{{row.item.startTime}}</b-button>
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
        { key: "schedule.date", label: "Date", sortable: true },
        { key: "startTime", label: "Started", sortable: false },
        { key: "line.number", label: "Line", sortable: true },
        { key: "sale", label: "Sale", sortable: true },
        { key: "saleItem.sale.customer.name", label: "Customer", sortable: true },
        { key: "saleItem.units", label: "Sold", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "totalProduced", label: "Produced", sortable: false },
        { key: "action", label: "Action", sortable: false },
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
      if(sale_id){
        this.getSale(sale_id);
      }
      this.getScheduleEvents(item_id);
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
      if(se.unitsScheduled < se.totalProduced){
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
    goToProduction(se_id) {
      router.push("/productionSale/" + se_id);
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
