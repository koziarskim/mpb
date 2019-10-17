<template>
  <b-container fluid>
    <b-row>
      <b-col>
        <div style="display:flex">
          <span style="padding-right:125px; text-align: left; font-size: 18px; font-weight: bold">Purchase<br/>Order</span>
          <div style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Number:</label>
            <input class="form-control" type="text" disabled="true" v-model="purchase.number" placeholder="P.O. Number">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Date:</label>
            <input class="form-control" type="date" disabled="true" v-model="purchase.date">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Shipping Date:</label>
            <input class="form-control" type="date" disabled="true" v-model="purchase.shippingDate">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Expected Date:</label>
            <input class="form-control" type="date" disabled="true" v-model="purchase.expectedDate">
          </div>
          <div style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Container:</label>
            <input class="form-control" type="text" disabled="true" v-model="purchase.containerNumber" placeholder="Container">
          </div>
          <div style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Invoice:</label>
            <input class="form-control" type="text" disabled="true" v-model="purchase.invoiceNumber" placeholder="Invoice">
          </div>
          <div style="text-align: right;">
            <b-button style="margin: 2px; margin-top:22px" type="reset" variant="success" @click="close()">Close</b-button>
          </div>
        </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 12px">
      <b-col cols=10>
        <label class="top-label">Components:</label>
        <b-table sort-by.sync="name" sort-desc.sync="false" :items="purchase.purchaseComponents" :fields="fields">
          <template v-slot:cell(totalNeeded)="row">
            {{row.item.totalSold - row.item.totalProduced}} ({{row.item.totalSold}} - {{row.item.totalProduced}})
          </template>
          <template v-slot:cell(unitsNeeded)="row">
            {{row.item.unitsSold - row.item.unitsProduced}} ({{row.item.unitsSold}} - {{row.item.unitsProduced}})
          </template>
          <template v-slot:cell(unitPrice)="row">
              ${{row.item.unitPrice}}
          </template>
          <template v-slot:cell(units)="row">
            {{row.item.units}}
          </template>
          <template v-slot:cell(totalPrice)="row">
            ${{row.item.totalPrice = getTotalPrice(row.item)}}
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
        date: moment().utc().format("YYYY-MM-DD"),
        purchaseComponents: [{
          totalSold: 0
        }]
      },
      selectedComponents: [],
      fields: [
        { key: "component.name", label: "Name", sortable: false },
        { key: "totalNeeded", label: "Total Needed", sortable: false },
        { key: "unitsInOrder", label: "Pen. Orders", sortable: false },
        { key: "component.unitsOnStack", label: "On-Stock", sortable: false },
        { key: "component.unitCost", label: "Unit Cost", sortable: false },
        { key: "unitPrice", label: "P.O. Price", sortable: false },
        { key: "units", label: "P.O. Units", sortable: false },
        { key: "totalPrice", label: "Total", sortable: false },
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
    getTotalPrice(item){
      return (item.units * item.unitPrice).toFixed(2);
    },
    updateComponents(components){
      this.selectedComponents = components;
    },
    getPurchase(purchase_id) {
      http.get("/purchase/" + purchase_id).then(r => {
          // this.purchase = r.data;
          this.getPoComponents(r.data);
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
        // PoComponentDtos
    getPoComponents(purchase){
      var searchDto = {components: []}
      purchase.purchaseComponents.forEach(pc => {
        searchDto.components.push(pc.component.id);
      })
      return http.post("/search/po/component", searchDto).then(r => {
        purchase.purchaseComponents.forEach(pc => {
          var dto = r.data.find(it => it.id == pc.component.id);
          pc.totalSold = dto.totalSold;
          pc.totalProduced = dto.totalProduced;
          pc.unitsInOrder = dto.unitsInOrder;
        })
        this.purchase = purchase;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    close() {
      router.push("/purchaseList");
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
    this.getPurchase(purchase_id);
  }
};
</script>

