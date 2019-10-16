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
          <template v-slot:head(unitsInOrder)="row">
            <span>Ordered </span><span style="font-size: 8px">(Not Received)</span>
          </template>
          <template v-slot:cell(unitsNeeded)="row">
            {{row.item.unitsSold - row.item.unitsProduced}} ({{row.item.unitsSold}} - {{row.item.unitsProduced}})
          </template>
          <template v-slot:cell(unitCost)="row">
            <div style="display: flex">
              $<b-form-input style="width:100px" class="form-control" type="tel" v-model="row.item.unitCost"></b-form-input>
            </div>
          </template>
          <template v-slot:cell(units)="row">
            <b-form-input style="width:100px" class="form-control" type="tel" v-model="row.item.units"></b-form-input>
          </template>
          <template v-slot:cell(totalCost)="row">
            ${{row.item.totalCost = getTotalCost(row.item)}}
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
        { key: "unitCost", label: "Unit Cost", sortable: false },
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
    getTotalCost(item){
      return (item.units * item.unitCost).toFixed(2);
    },
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
      this.purchase.purchaseComponents = [];
      this.selectedComponents.forEach(c => {
       this.purchase.purchaseComponents.push({component: {id: c.id}, units: c.units, unitCost: c.unitCost});
      })
      return http.post("/purchase", this.purchase).then(r => {
        this.purchase = r.data;
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

