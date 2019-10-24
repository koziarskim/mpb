<template>
  <b-container fluid>
    <b-row>
      <b-col>
        <div style="display:flex">
          <div style="margin-top:-5px;">
            <span style="font-size: 18px; font-weight: bold">Purchase Order</span>
            <input style="width: 190px" class="form-control" type="text" v-model="purchase.number" placeholder="P.O. Number">
          </div>
          <div style="width: 200px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Name:</label>
            <input class="form-control" type="text" v-model="purchase.name" placeholder="P.O. Name/Description">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Date:</label>
            <input class="form-control" type="date" v-model="purchase.date">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Shipping Date:</label>
            <input class="form-control" type="date" v-model="purchase.shippingDate">
          </div>
          <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">ETA Date:</label>
            <input class="form-control" type="date" v-model="purchase.expectedDate">
          </div>
          <div style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Container:</label>
            <input class="form-control" type="text" v-model="purchase.containerNumber" placeholder="Container">
          </div>
          <div style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Invoice:</label>
            <input class="form-control" type="text" v-model="purchase.invoiceNumber" placeholder="Invoice">
          </div>
          <div style="text-align: right;">
            <b-button style="margin: 2px; margin-top:22px" type="reset" variant="success" @click="saveAndClose">Save</b-button>
          </div>
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
          <template v-slot:cell(totalNeeded)="row">
            {{row.item.totalSold - row.item.totalProduced}} ({{row.item.totalSold}} - {{row.item.totalProduced}})
          </template>
          <template v-slot:cell(unitsNeeded)="row">
            {{row.item.unitsSold - row.item.unitsProduced}} ({{row.item.unitsSold}} - {{row.item.unitsProduced}})
          </template>
          <template v-slot:cell(unitPrice)="row">
            <div style="display: flex">
              $<b-form-input style="width:100px" class="form-control" type="tel" v-model="row.item.unitPrice"></b-form-input>
            </div>
          </template>
          <template v-slot:cell(units)="row">
            <b-form-input style="width:100px" class="form-control" type="tel" v-model="row.item.units"></b-form-input>
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
        date: moment().format("YYYY-MM-DD"),
        supplier: {}
      },
      selectedComponents: [],
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "totalNeeded", label: "Total Needed", sortable: false },
        { key: "unitsNeeded", label: "Needed (S-P)", sortable: false },
        { key: "unitsInOrder", label: "Orders", sortable: false },
        { key: "unitsOnStock", label: "Stock", sortable: false },
        { key: "unitCost", label: "Unit Cost", sortable: false },
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
    updateComponents(searchDto){
      this.getPoComponents(searchDto);
    },
    getPoComponents(searchDto){
      return http.post("/search/po/component", searchDto).then(r => {
        if(!this.purchase.supplier.id){
          this.purchase.supplier = {id: r.data[0].supplierId}
        }
        var missmatch = false;
        r.data.forEach(dto => {
          if(dto.supplierId != this.purchase.supplier.id){
            missmatch = true;
          }
        })
        if(missmatch){
            alert("Supplier missmatch! Only components to single supplier are allowed!");
            if(this.selectedComponents.length==0){
              this.purchase.supplier = {};
            }
            return Promise.reject();
        }
        r.data.forEach(dto => {
          var existing = this.selectedComponents.find(selected => selected.id == dto.id)
          if(!existing){
            this.selectedComponents.push(dto);
          }
        })
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    validate(){
      if(this.selectedComponents.length == 0){
        alert("No Component selected.");
        return false;
      }
      if(!this.purchase.number){
        alert("Purchase Number not entered");
        return false;
      }
      var empty = this.selectedComponents.find(c => c.units < 0);
      if(empty){
          alert("One of the Compnents has negative number of P.O. Units! Please fix.");
          return false;
      }
      return true;
    },
    savePurchase() {
      if(!this.validate()){
        return Promise.reject();
      }
      this.purchase.purchaseComponents = [];
      this.selectedComponents.forEach(c => {
       this.purchase.purchaseComponents.push({component: {id: c.id}, units: c.units, unitPrice: c.unitPrice});
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
      }).catch(e => {
        //TODO: Do nothing.
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
  }
};
</script>

