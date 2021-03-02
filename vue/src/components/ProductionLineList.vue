<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=3>
        <span style="font-size: 18px; font-weight: bold">Daily Production Status:</span>
        <b-form-checkbox size="sm" v-model="itemView">Item View</b-form-checkbox>
      </b-col>
      <b-col cols=2>
        <input class="form-control" type="date" v-model="date" placeholder="Date">
      </b-col>
      <b-col cols=1>
        <b-select option-value="id" option-text="number" :list="availableLines" v-model="lineKv" placeholder="Line"></b-select>
      </b-col>
      <b-col cols=3>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemkv" placeholder="Pick Item"></b-select>
      </b-col>
      <b-col cols=1>
        <div style="cursor: pointer; width: 23px" @click="downloadProdSchedulePdf()"><img src="../assets/pdf-download.png" width="23px"></div>				
      </b-col>
    </b-row>
    <b-table :sticky-header="browserHeight()" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="scheduleEvents" :fields="fields">
      <template v-slot:cell(line.number)="row">
        <b-link role="button" @click="goToProductionLine(row.item)">{{row.item.line.id}}</b-link>
      </template>
      <template v-slot:cell(item)="row">
        <b-link role="button" @click="goToItem(row.item.itemPackaging.item.id)">{{row.item.itemPackaging.item.number}}</b-link>
		    <span> ({{row.item.itemPackaging.item.name}})</span>
      </template>
      <template v-slot:cell(sale)="row">
		    <span v-if="row.item.saleItem">{{row.item.saleItem.sale.number}} - {{row.item.saleItem.sale.customer.name}}</span>
      </template>
      <template v-slot:cell(dc)="row">
		    <span v-if="row.item.saleItem">{{getAddress(row.item.saleItem.sale)}}</span>
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
      </template>
    </b-table>
    <b-row>
      <b-col cols=2>
        <span>Total Scheduled: {{totalScheduled}}</span>
      </b-col>
      <b-col cols=2>
        <span>Total Produced: {{totalProduced}}</span>
      </b-col>
    </b-row>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import moment from "moment";
import securite from "../securite";
import navigation from "../utils/navigation";
import formatter from "../utils/formatter"

export default {
  data() {
    return {
      navigation: navigation,
      formatter: formatter,
      date: moment()
        .format("YYYY-MM-DD"),
      sortBy: "line.number",
      sortDesc: false,
      fields: [
        { key: "line.number", label: "Line", sortable: false },
        { key: "item", label: "Item # (Name)", sortable: false },
        { key: "sale", label: "Sale - Customer", sortable: false },
        { key: "dc", label: "DC (State)", sortable: false },
		    { key: "saleItem.units", label: "Sold", sortable: true },
		    { key: "unitsScheduled", label: "Scheduled", sortable: true },
		    { key: "unitsProduced", label: "Produced", sortable: true },
        { key: "unitsPending", label: "Still To Make", sortable: true },
        { key: "totalTime", label: "Total Time", sortable: true },
        { key: "action", label: "", sortable: false },
    ],
    scheduleEvents: [],
    availableLines: [],
    availableItems: [],
    lineKv: {},
    itemkv: {},
    totalScheduled: 0,
    totalProduced: 0,
    itemView: false,
    };
  },
  watch: {
    date(newValue, oldValue) {
      this.getScheduleEvents(newValue);
    },
    lineKv(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    itemkv(newValue, oldValue){
      this.getScheduleEvents(this.date);
    },
    itemView(newValue, oldValue){
      if(newValue==true){
        navigation.goTo("/productionItemList/"+this.date)
      }
    }
  },
  methods: {
    downloadProdSchedulePdf(){
      http.get("/scheduleEvent/date/"+this.date+"/schedule/pdf", { responseType: 'blob'}).then(r => {
        const url = URL.createObjectURL(new Blob([r.data], { type: r.headers['content-type']}))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute("download", r.headers['file-name'])
        document.body.appendChild(link)
        link.click()
          });
    },    
    getAddress(sale){
      var addressName = "";
      if(sale.shippingAddress){
       sale.shippingAddress.dc + " ("+sale.shippingAddress.state+")";
      }
      return addressName;
    },
    browserHeight(){
      return +window.innerHeight - 150 +"px";
    },
    editScheduleEvent(se){
      if(!securite.hasRole(['PRODUCTION_ADMIN', 'ADMIN'])){
        alert("You don't have permission for this operation");
        return;
      }
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
        this.getScheduleEvents(this.date)
      });
      se.edit = false;
    },
    deleteDisabled(se){
      return se.unitsProduced > 0;
    },
    deleteScheduleEvent(se_id){
      if(!securite.hasRole(['PRODUCTION_ADMIN', 'ADMIN'])){
        alert("You don't have permission for this operation");
        return;
      }
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(value => {
        if(value){
          http.delete("/scheduleEvent/" + se_id).then(response => {
            this.getScheduleEvents(this.date);
          });
        }
      })
    },
    getAvailableLines() {
      this.availableLines = [
        { id: 1, number: 1 },
        { id: 2, number: 2 },
        { id: 3, number: 3 },
        { id: 4, number: 4 },
        { id: 5, number: 5 },
        { id: 6, number: 6 },
        { id: 7, number: 7 },
        { id: 8, number: 8 }
      ];
    },
    getAvailableItems(){
      if(this.itemkv.id){
        return;
      }
      this.availableItems = [];
      this.scheduleEvents.forEach(se => {
        //Remove duplicates.
        if(this.availableItems.find(item => 
          item.id == se.itemPackaging.item.id
        )){
          return;
        }
        this.availableItems.push({
          id: se.itemPackaging.item.id,
          name: se.itemPackaging.item.name
        })
      })
    },
	  getScheduleEvents(date){
      var query = {line_id: this.lineKv.id};
      http
        .get("/scheduleEvent/date/"+date, {params: {
          line_id: this.lineKv.id
        }})
        .then(response => {
          this.scheduleEvents = [];
          this.totalScheduled = 0;
          this.totalProduced = 0;
          if(response.data){
            response.data.forEach(se =>{
              se.edit = false;
              if(this.lineKv.id && se.line.id != this.lineKv.id){
                return;
              }
              if(this.itemkv.id && se.itemPackaging.item.id != this.itemkv.id){
                return;
              }
              this.totalScheduled += se.unitsScheduled;
              this.totalProduced += se.unitsProduced;
              this.scheduleEvents.push(se)
            })
          }
          this.getAvailableItems();
        });
	  },
    goToItem(item_id) {
      router.push("/itemEdit/" +item_id);
	  },
    goToProductionLine(se) {
      var query = { date: this.date, seId: se.id};
      router.push({ path: "/productionLine/"+se.line.id, query: query } );
	  },
  },
  mounted() {
    var date = this.$route.params.date;
    if(date){
      this.date = date;
    }
    this.getScheduleEvents(this.date);
    this.getAvailableLines();
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
