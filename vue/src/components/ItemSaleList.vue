<template>
  <b-container fluid>
    <div style="border: 0px" class="d-flex justify-content-between align-items-center">
      <h4 style="text-align: left;">Sales for Item: {{item.name}}</h4>
      <div style="text-align: right;">
        <b-button type="reset" variant="success" @click="close">Close</b-button>
      </div>
    </div>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="item.saleItems && item.saleItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="item.saleItems" :fields="columns">
          <template v-slot:cell(sale)="row">
            <b-link role="button" @click.stop="goToSale(row.item.sale.id)">{{row.item.sale?row.item.sale.number:''}}</b-link>
          </template>
          <template v-slot:cell(dc)="row">
            <span>{{getDc(row.item)}}</span>
          </template>
          <template v-slot:cell(unitsSoldAdj)="row">
            <span>{{row.item.units}} {{row.item.unitsAdjusted >= 0?'+':''}}{{row.item.unitsAdjusted}}</span>
          </template>
          <template v-slot:cell(unitsOnStock)="row">
            <span>{{getUnitsOnStock(row.item)}}</span>
          </template>
          <template v-slot:cell(unitsSchedProd)="row">
            <b-button size="sm" @click="goToScheduled(row.item.sale.id)" variant="link">{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(status)="row">
            <span>{{getStatus(row.item.status)}}</span>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" :disabled="disableSchedule(row.item)" type="submit" variant="primary" @click="toggleModal(row.item)">Schedule</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
    <b-modal v-model="modalShow" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col>
          <span>Create Schedule for: {{scheduleData.item.number}}</span>
        </b-col>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=5>
        <b-row>
          <b-col cols=12>
            <label class="top-label">Schedule Date:</label>
            <input class="form-control" type="date" v-model="scheduleData.date">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=10>
            <label class="top-label">Start Time:</label>
            <input class="form-control" type="time" v-model="scheduleData.time">
          </b-col>
        </b-row>
        </b-col>
        <b-col cols=7>
          <br/>
          <label>{{scheduleData.saleItem.sale.number}}</label><br/>
          <label>{{scheduleData.saleItem.sale.customer.name}}</label>
        </b-col>
        </b-row>
        <b-row>
          <b-col cols=3>
            <label class="top-label">Line:</label>
            <b-select option-value="id" option-text="number" :list="availableLines" v-model="scheduleData.line"></b-select>
          </b-col>
          <b-col cols=4>
            <label class="top-label">Units to Schedule:</label>
            <input class="form-control" type="tel" v-model="scheduleData.units">
          </b-col>
        </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import moment from "moment";

export default {
  data() {
    return {
      item: {},
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "sale", label: "Sale", sortable: false },
        { key: "sale.customer.name", label: "Customer", sortable: false },
        { key: "dc", label: "DC (State)", sortable: false },
        { key: "unitsSoldAdj", label: "Sold (+/-Adj)", sortable: false },
        { key: "unitsOnStock", label: "Stock (Pro+Ret+/-Tran-Ship)", sortable: false },
        { key: "unitsOverstock", label: "Overstock", sortable: false },
        { key: "unitsSchedProd", label: "Sched/Prod", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false },
      ],
      scheduleData: {
        date: moment().format("YYYY-MM-DD"),
        time: "08:00:00",
        line: {id: 1, number: '1'},
        units: 0,
        maxUnits: 0,
        saleItem: {
          sale: {
            customer: {}
          }
        },
        item: {}
      },

      modalShow: false,
      availableLines: [
        { id: 1, number: 1 },
        { id: 2, number: 2 },
        { id: 3, number: 3 },
        { id: 4, number: 4 },
        { id: 5, number: 5 },
        { id: 6, number: 6 },
        { id: 7, number: 7 },
        { id: 8, number: 8 }
      ],
      availableStatus: [
        {id: 'DRAFT', name: 'Draft'},
        {id: 'PENDING_APPROVAL', name: 'Pending Approval'},
        {id: 'APPROVED', name: 'Pending Schedule'},
        {id: 'PENDING_PROD', name: 'Pending Prod'},
        {id: 'PENDING_SHIPMENT', name: 'Pending Shipment'},
        {id: 'SHIPPED', name: 'Fully Shipped'},
      ],
    };
  },

  computed: {},
  watch: {},
  methods: {
    getUnitsOnStock(si){
      var unitsTransfered = +si.unitsTransferedTo - +si.unitsTransferedFrom;
      var transfered = unitsTransfered<0?" "+unitsTransfered:" +"+unitsTransfered;
      var units = si.unitsOnStock +" ("+si.unitsProduced+ " +" +si.unitsReturned + transfered +" -" +si.unitsShipped +")";
      return units;
    },
    getStatus(statusId){
      var statusKv = this.availableStatus.find(stat => stat.id == statusId)
      return statusKv.name
    },
    getDc(si){
      var dc = "";
      if(si.sale.shippingAddress){
        dc = si.sale.shippingAddress.dc + " ("+si.sale.shippingAddress.state+")";
      }
      return dc;
    },
    toggleModal(saleItem){
      this.modalShow = !this.modalShow;
      this.scheduleData.units = +saleItem.units - +saleItem.unitsScheduled;
      this.scheduleData.maxUnits = this.scheduleData.units;
      this.scheduleData.saleItem = saleItem;
      this.scheduleData.item = this.item;
    },
    disableSchedule(saleItem){
      return +saleItem.units - +saleItem.unitsScheduled <=0;
    },
    saveSchedule() {
      var schedule = {
        date: this.scheduleData.date
      }
      return http
        .post("/schedule", schedule)
        .then(response => {
          return response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate(){
      if(!this.scheduleData.date || !this.scheduleData.time || !this.scheduleData.line || !this.scheduleData.units){
        alert("Please enter all the fields");
        return false;
      }
      if(this.scheduleData.units > this.scheduleData.maxUnits){
        alert("Cannot schedule more that sold");
        return false;
      }
      return true;
    },
    saveModal(){
      if(!this.validate()){
        return false;
      }
      this.saveSchedule().then(schedule => {
        var scheduleEvent = {
            line: {id: this.scheduleData.line.id},
            saleItem: { id: this.scheduleData.saleItem.id },
            schedule: { id: schedule.id },
            scheduleTime: this.scheduleData.time,
            unitsScheduled: this.scheduleData.units,
          }
        http
          .post("/scheduleEvent", scheduleEvent)
          .then(response => {
            this.getItem(this.item.id);
          })
          .catch(e => {
            console.log("API error: " + e);
          });
        this.modalShow = !this.modalShow;
      })
    },
    closeModal(){
      this.modalShow = !this.modalShow;
    },
    close() {
        window.history.back();
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
    goToScheduled(sale_id) {
      router.push("/scheduleEventList/" + this.item.id + "/sale/" + sale_id);
    },
  },
  mounted() {
    var item_id = this.$route.params.item_id;
    this.getItem(item_id);
  }
};
</script>

<style>
</style>
