<template>
  <b-container fluid>
    <b-row>
      <b-col cols=10>
        <div style="display:flex">
          <div style="margin-top:-5px;">
            <span style="font-size: 18px; font-weight: bold">Purchase Order</span>
            <input style="width: 175px" class="form-control" type="text" v-model="purchase.number" :disabled="!editMode">
          </div>
          <div v-if="receiveMode" style="display:flex">
            <div style="margin-top:-2px; margin-left: 8px;">
              <label class="top-label">Receiving Number:</label>
              <input style="width: 175px" class="form-control" type="text" v-model="receivingNumber">
            </div>
            <div style="margin-top:-2px; margin-left: 8px;">
              <label class="top-label">Container Number:</label>
              <input style="width: 175px" class="form-control" type="text" v-model="receivingContainerNumber">
            </div>
            <div style="margin-top:-2px; margin-left: 8px;">
              <label class="top-label">Invoice Number:</label>
              <input style="width: 175px" class="form-control" type="text" v-model="receivingInvoiceNumber">
            </div>
            <div style="margin-top:-2px; margin-left: 8px;">
              <label class="top-label">Received Date:</label>
              <input style="width: 175px" class="form-control" type="date" v-model="receivingDate">
            </div>
          </div>
          <div v-if="!receiveMode" style="display:flex">
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">P.O. Date:</label>
              <input class="form-control" type="date" v-model="purchase.date" :disabled="!editMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Shipping Date:</label>
              <input class="form-control" type="date" v-model="purchase.shippingDate" :disabled="!editMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">ETA Date:</label>
              <input class="form-control" type="date" v-model="purchase.expectedDate" :disabled="!editMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Container:</label>
              <input class="form-control" type="text" v-model="purchase.containerNumber" :disabled="!editMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Invoice:</label>
              <input class="form-control" type="text" v-model="purchase.invoiceNumber" :disabled="!editMode">
            </div>
          </div>
        </div>
      </b-col>
      <b-col cols=2>
          <div style="text-align: right;">
            <div v-if="!editMode && !receiveMode">
              <b-button size="sm" style="margin-right: 2px;" type="reset" variant="success" @click="edit()">Edit</b-button>
              <b-button size="sm" style="width: 28px;" type="reset" variant="secondary" @click="deletePo()">x</b-button><br/>
              <b-button size="sm" style="margin: 2px; width: 70px" type="reset" variant="success" @click="receive()">Receive</b-button>
            </div>
            <div v-if="editMode || receiveMode">
            <b-button size="sm" style="margin-right: 2px;" type="reset" variant="success" @click="cancel()">Cancel</b-button><br/>
            <b-button size="sm" style="margin: 2px; width: 70px" type="reset" variant="success" @click="save()">Save</b-button>
            </div>
          </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 12px">
      <b-col>
        <b-table sort-by.sync="name" sort-desc.sync="false" :items="purchase.purchaseComponents" :fields="fields">
          <template v-slot:cell(name)="row">
            <b-link @click.stop="goToComponent(row.item.component.id)">{{row.item.component.number}}</b-link>
            <span style="font-size: 11px"> ({{row.item.component.name}})</span>
          </template>
          <template v-slot:cell(unitsReceived)="row">
            <b-button v-if="!receiveMode" size="sm" @click.stop="goToReceiving(purchase.id, row.item.component.id)" variant="link">{{row.item.unitsReceived}}</b-button>
            <div v-if="receiveMode" style="display:flex">
              <span style="margin-top:10px">{{row.item.unitsReceived}}&nbsp;+&nbsp;</span>
              <input class="form-control" style="width: 80px" type="tel" v-model="row.item.unitsToReceive" placeholder="0">
              <span style="font-size: 20px; margin-left: 10px">$</span><input class="form-control" style="width: 80px" type="tel" v-model="row.item.unitPriceReceived" placeholder="0">
            </div>          
          </template>
          <template v-slot:cell(unitPrice)="row">
            <div v-if="editMode" style="display:flex">
              <span style="font-size:20px">$</span><input class="form-control" style="width: 100px" type="tel" v-model="row.item.unitPrice">
            </div>
            <span v-if="!editMode">${{row.item.unitPrice}}</span>
          </template>
          <template v-slot:cell(units)="row">
            <input v-if="editMode" class="form-control" style="width: 120px" type="tel" v-model="row.item.units">   
            <span v-if="!editMode">{{row.item.units}}</span>
          </template>
          <template v-slot:cell(cases)="row">
            <span>{{Math.ceil(row.item.units / row.item.component.casePack)}}</span>
          </template>             
          <template v-slot:cell(totalPrice)="row">
            ${{row.item.totalPrice = getTotalPrice(row.item)}}
          </template>
          <template v-slot:cell(action)="row">
            <b-button :disabled="!editMode" size="sm" @click="deletePc(row.item)">x</b-button>
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
      receivingDate: null,
      receivingNumber: null,
      receivingContainerNumber: null,
      receivingInvoiceNumber: null,
      editMode: false,
      receiveMode: false,
      purchase: {},
      purchaseComponents: [],
      purchaseComponent: {},
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "component.unitCost", label: "Unit Cost", sortable: false },
        { key: "unitPrice", label: "P.O. Price", sortable: false },
        { key: "units", label: "P.O. Units", sortable: false },
        { key: "component.casePack", label: "C/P", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "totalPrice", label: "Total", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ],
    };
  },
  computed: {},
  watch: {
  },
  methods: {
    deletePo(){
      if(this.purchase.unitsReceived > 0){
        alert("There are units already received!");
        return;
      }
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(ok => {
        if(ok){
          http.delete("/purchase/" + this.purchase.id).then(response => {
            router.push("/purchaseList");
          }).catch(e => {console.log("API Error: " + e);});          }
      })

    },
    deletePc(pc){
      var idx = this.purchase.purchaseComponents.findIndex(it => it.id == pc.id);
      this.purchase.purchaseComponents.splice(idx, 1);
      this.save();
    },
    cancel(){
      this.getPurchase(this.purchase.id).then(purchase =>{
        this.editMode = false;
        this.receiveMode = false;
      })
    },
    edit(){
      var received = false;
      this.purchase.purchaseComponents.forEach(pc=> {
        pc.receivings.forEach(rec=> {
          if(rec.units>0){
            received = true;
          }
        })
      })
      if(received){
        alert("Purchase is partially received. Action not allowed!");
        return;
      }
      this.editMode = true;
    },
    receive(){
      this.purchase.purchaseComponents.forEach(pc=> {
        pc.unitsToReceive = +pc.units - +pc.unitsReceived;
        pc.unitPriceReceived = pc.unitPrice;
      })
      this.receivingNumber = "Rec-"+this.purchase.number;
      this.receivingContainerNumber = this.purchase.containerNumber;
      this.receivingInvoiceNumber = this.purchase.invoiceNumber;
      this.receivingDate = null;
      this.receiveMode = true;
    },
    save(){
      if(this.editMode){
        this.updatePurchase();
      }else if(this.receiveMode){
        this.saveReceive();
      }
    },
    updatePurchase(){
      return http.post("/purchase", this.purchase).then(r => {
        this.purchase = r.data;
        this.editMode = false;
        this.receiveMode = false;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    saveReceive(){
      if(!this.receivingDate || moment(this.receivingDate).isAfter(moment.utc())){
        alert("Received Date cannot be in future");
        return Promise.reject();
      }
      var receivings = [];
      var isNegative = false;
      this.purchase.purchaseComponents.forEach(pc => {
        if(pc.unitsToReceive < 0){
          isNegative = true;
          return;
        }
        var receiving = {purchaseComponent: pc}
        receiving.number = this.receivingNumber;
        receiving.containerNumber = this.receivingContainerNumber;
        receiving.invoiceNumber = this.receivingInvoiceNumber;
        receiving.shippingDate = this.purchase.shippingDate;
        receiving.etaDate = this.purchase.expectedDate;
        receiving.receivingDate = this.receivingDate;
        receiving.units = pc.unitsToReceive;
        receiving.unitPrice = pc.unitPriceReceived;
        receivings.push(receiving);
      })
      if(isNegative){
        alert("Units have to be positive");
        return Promise.reject();
      }
      if(receivings.length<1){
        alert("Nothing to save");
      }else{
        return http.post("/receivings/purchase/"+this.purchase.id, receivings).then(r => {
          this.purchase = r.data;
          this.editMode = false;
          this.receiveMode = false;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
    },
    getTotalPrice(pc){
      return (pc.units * pc.unitPrice).toFixed(4);
    },
    getPurchase(purchase_id) {
      return http.get("/purchase/" + purchase_id).then(r => {
        this.purchase = r.data;
        this.receivingDate = r.data.receivingDate;
        return r.data;
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
    goToReceiving(purchase_id, component_id) {
      var query = { purchase_id: purchase_id, component_id: component_id };
      router.push({ path: "/receivingList", query: query });
    },
  },
  mounted() {
    var purchase_id = this.$route.params.purchase_id;
    this.getPurchase(purchase_id);
  }
};
</script>

