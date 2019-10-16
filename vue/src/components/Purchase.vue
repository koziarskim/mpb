<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <span style="text-align: left; font-size: 18px; font-weight: bold">Purchase Order:</span>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="text" v-model="purchase.number" placeholder="P.O. Number">
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="purchase.date" placeholder="Date">
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button style="margin: 2px;" type="reset" variant="success" @click="saveAndClose">Save & Close</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 12px">
      <b-col cols=2>
        <component-search v-on:componentsUpdated="updateComponents"></component-search>
      </b-col>
      <b-col cols=10>
        <label class="top-label">Components:</label>
        <b-table sort-by.sync="name" sort-desc.sync="false" :items="selectedComponents" :fields="fields">
          <!-- <template v-slot:cell(line.number)="row">
            <b-button size="sm" @click.stop="goToProductionLine(row.item.id)" variant="link">{{row.item.line.number}}</b-button>
          </template>
          <template v-slot:cell(item)="row">
            <b-button size="sm" @click.stop="navigation.goToItemEdit(row.item.saleItem.item.id)" variant="link">{{row.item.saleItem.item.name}}</b-button>
            <span>({{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}})</span>
          </template>
          <template v-slot:cell(totalTime)="row">
          <span>{{formatter.secondsToTime(row.item.totalTime)}}</span>
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
            <b-button v-if="!row.item.eventCompleted" size="sm" :disabled="deleteDisabled(row.item)" @click="deleteScheduleEvent(row.item.id)" variant="primary">X</b-button>
          </template> -->
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";
import vue from "vue";
import ComponentSearch from "./ComponentSearch";

export default {
  data() {
    return {
      purchase: {
        date: moment().utc().format("YYYY-MM-DD")
      },
      selectedComponents: [],
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "unitsNeeded", label: "Needed (Sold - Produced)", sortable: false },
        { key: "unitsInOrder", label: "Ordered (Not Received)", sortable: false },
        { key: "unitsOnStock", label: "On-Stock", sortable: false },
        { key: "unitsShort", label: "Short", sortable: false },
        { key: "unitsCost", label: "Unit Cost", sortable: false },
        { key: "units", label: "To Order", sortable: false },
        { key: "totalCost", label: "Total", sortable: false },
      ],
    };
  },
  components:{
    'component-search': ComponentSearch
  },
  computed: {},
  watch: {
  },
  methods: {
    updateComponents(components){
      this.selectedComponents = components;
    },
    getPurchase(purchase_id) {
      http.get("/purchase/" + purchase_id).then(r => {
          this.purchase = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    savePurchase() {
      return http.post("/purchase", this.purchase).then(r => {
          this.getPurchaseData(r.data.id);
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    saveAndClose() {
      this.savePurchase().then(r => {
        router.push("/purchaseList");
      });
    },
    goToComponent(component_id) {
      router.push("/componentEdit/" + component_id);
    },
    saleHeight(){
      return +window.innerHeight - 150 +"px";
    },
  },
  mounted() {
    var purchase_id = this.$route.params.purchase_id;
    if (purchase_id) {
      this.getPurchaseData(purchase_id);
    }
  }
};
</script>

