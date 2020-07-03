<template>
  <b-container fluid>
    <b-row>
      <b-col cols=3>
        <label class="top-label">Packing list #:</label>
        <input class="form-control" type="text" v-model="receiving.number">
      </b-col>
      <b-col cols=3 offset=5>
        <div style="text-align: right;">
          <b-button size="sm" type="reset" variant="success" @click="saveAndClose()">Save & Close</b-button>
          <b-button size="sm" style="width: 28px; margin-left: 3px" type="reset" variant="secondary" @click="deleteReceiving()">x</b-button>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=8>
        <b-row>
          <b-col cols="4">
            <label class="top-label">Expiration Date:</label>
            <input class="form-control" type="date" v-model="receiving.expirationDate">
          </b-col>
          <!-- <b-col cols="4">
            <label class="top-label">ETA Date:</label>
            <input class="form-control" type="date" v-model="receiving.etaDate">
          </b-col> -->
          <b-col cols="4">
            <label class="top-label">Received Date:</label>
            <input class="form-control" type="date" v-model="receiving.receivingDate">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols="4">
            <label class="top-label">Units (Shipped/Received):</label>
            <input class="form-control" type="text" v-model="receiving.units" placeholder="Units Received">
          </b-col>
          <b-col cols="4">
            <label class="top-label">Container:</label>
            <input class="form-control" type="text" v-model="receiving.containerNumber" placeholder="Container #">
          </b-col>
        </b-row>
      </b-col>
      <b-col>
        <b-row>
            P.O.#: {{purchaseComponent.purchase.number}}<br/>
            Component: {{purchaseComponent.component.name}}<br/>
            Ordered: {{purchaseComponent.units}}<br/>
            Received: {{purchaseComponent.unitsReceived}}<br/>
        </b-row>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import state from "../data/state";
import moment from "moment";

export default {
  data() {
    return {
      editMode: true,
      receiving: {
        shippingDate: moment().format("YYYY-MM-DD"),
        etaDate: moment().add(7, 'days').format("YYYY-MM-DD"),
      },
      purchaseComponent: {purchase: {}, component: {}},
    };
  },
  computed: {
  },
  watch: {},
  methods: {
    deleteReceiving() {
      http.delete("/receiving/" + this.receiving.id).then(response => {
        router.push("/receivingList");
      }).catch(e => {console.log("API Error: " + e);});
    },
    getReceiving(receiving_id) {
      return http.get("/receiving/" + receiving_id).then(r => {
          this.receiving = r.data;
          if(r.data.receivingDate){
            this.editMode = false;
          }
          if(!this.purchaseComponent.id){
            this.purchaseComponent = r.data.purchaseComponent;
          }
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    validate(){
      if(!this.receiving.etaDate && !this.receiving.receivingDate){
        alert("Please enter ETA or Received Date!");
        return false;
      }
      if(!this.receiving.receivingDate || moment(this.receiving.receivingDate).isAfter(moment.utc())){
        alert("Received Date cannot be in future");
        return false;
      }
      return true;
    },
    save() {
      if(!this.validate()){
        return Promise.reject();
      }
      if(!this.receiving.id){
        this.receiving.purchaseComponent = this.purchaseComponent;
      }
      this.receiving.totalPrice = (+this.receiving.unitPrice * +this.receiving.units);
      return http.post("/receiving", this.receiving).then(r => {
        return Promise.resolve();
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    saveAndClose() {
      this.save().then(r => {
        window.history.back();
      });
    },
    getPurchaseComponent(pc_id) {
      return http.get("/purchaseComponent/"+pc_id).then(r => {
          this.purchaseComponent = r.data;
          this.receiving.shippingDate = r.data.purchase.shippingDate;
          this.receiving.etaData = r.data.purchase.expectedDate;
          this.receiving.containerNumber = r.data.purchase.containerNumber;
          this.receiving.invoiceNumber = r.data.purchase.invoiceNumber;
          if(!this.receiving.number){
            this.receiving.number = r.data.purchase.number + "-" + r.data.component.number;
          }
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
  },
  mounted() {
    var receiving_id = this.$route.params.receiving_id;
    var pc_id = this.$route.params.pc_id;
    if(pc_id){
      this.getPurchaseComponent(pc_id);
    }
    if(receiving_id){
      this.getReceiving(receiving_id);
    }
    
  }
};
</script>

<style>
</style>
