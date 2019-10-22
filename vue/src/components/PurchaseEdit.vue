<template>
  <b-container fluid>
    <b-row>
      <b-col>
        <div style="display:flex">
          <div style="margin-top:-5px;">
            <span style="font-size: 18px; font-weight: bold">Purchase Order</span>
            <input style="width: 150px" class="form-control" type="text" v-model="purchase.number" :disabled="!editMode">
          </div>
          <div style="width: 200px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Name:</label>
            <input class="form-control" type="text" v-model="purchase.name" :disabled="!editMode">
          </div>
          <div v-if="!receiveMode" style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Date:</label>
            <input class="form-control" type="date" v-model="purchase.date" :disabled="!editMode">
          </div>
          <div v-if="!receiveMode" style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Shipping Date:</label>
            <input class="form-control" type="date" v-model="purchase.shippingDate" :disabled="!editMode">
          </div>
          <div v-if="!receiveMode" style="width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">ETA Date:</label>
            <input class="form-control" type="date" v-model="purchase.expectedDate" :disabled="!editMode">
          </div>
          <div v-if="!receiveMode" style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Container:</label>
            <input class="form-control" type="text" v-model="purchase.containerNumber" :disabled="!editMode">
          </div>
          <div v-if="!receiveMode" style="width: 160px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Invoice:</label>
            <input class="form-control" type="text" v-model="purchase.invoiceNumber" :disabled="!editMode">
          </div>
          <div v-if="receiveMode" style="margin-left: 680px; width: 175px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">Received:</label>
            <input class="form-control" type="date" v-model="purchase.receivingDate">
          </div>
          <div style="text-align: right;">
            <b-button v-if="!editMode && !receiveMode" size="sm" style="margin-right: 2px; width: 70px; margin-left: -50px" type="reset" variant="success" @click="edit()">Edit</b-button>
            <b-button v-if="!editMode && !receiveMode" size="sm" style="margin: 2px; width: 70px" type="reset" variant="success" @click="receive()">Receive</b-button>
            <b-button v-if="editMode || receiveMode" size="sm" style="margin: 2px; width: 70px; margin-top: 25px;" type="reset" variant="success" @click="save()">Save</b-button>
          </div>
        </div>
      </b-col>
    </b-row>
    <b-row style="font-size: 12px">
      <b-col>
        <b-table sort-by.sync="name" sort-desc.sync="false" :items="purchaseComponents" :fields="fields">
          <template v-slot:cell(name)="row">
            <b-button size="sm" @click.stop="goToComponent(row.item.component.id)" variant="link">{{row.item.component.number}} - {{row.item.component.name}}</b-button>
          </template>
          <template v-slot:cell(unitsReceived)="row">
            <b-button size="sm" @click.stop="goToReceiving(row.item.purchase.id, row.item.component.id)" variant="link">{{row.item.unitsReceived}}</b-button>
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
      editMode: false,
      receiveMode: false,
      purchase: {},
      purchaseComponents: [],
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "component.unitCost", label: "Unit Cost", sortable: false },
        { key: "unitPrice", label: "P.O. Price", sortable: false },
        { key: "units", label: "P.O. Units", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "totalPrice", label: "Total", sortable: false },
      ],
    };
  },
  computed: {},
  watch: {
  },
  methods: {
    edit(){
      this.editMode = true;
    },
    receive(){
      this.receiveMode = true;
    },
    save(){
      this.editMode = false;
      this.receiveMode = false;
    },
    getTotalPrice(pc){
      return (pc.units * pc.unitPrice).toFixed(2);
    },
    getPurchase(purchase_id) {
      http.get("/purchase/" + purchase_id).then(r => {
          this.purchase = r.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getPurchaseComponents(purchase_id) {
      http.get("/purchaseComponent/purchase/" + purchase_id).then(r => {
          this.purchaseComponents = r.data;
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
    this.getPurchaseComponents(purchase_id);
    this.getPurchase(purchase_id);
  }
};
</script>

