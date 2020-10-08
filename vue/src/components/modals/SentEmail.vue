<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=3>
          <label class="top-label">Packing list #:</label>
          <input class="form-control" type="text" v-model="receiving.number">
        </b-col>
        <b-col cols=3 offset=5>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="saveBill()">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" type="reset" variant="secondary" @click="closeBill()">Close</b-button>
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
            <b-col cols="4">
              <label class="top-label">Received Date:</label>
              <input class="form-control" type="date" v-model="receiving.receivingDate">
            </b-col>
          </b-row>
          <b-row>
            <b-col cols=4>
              <label class="top-label">Units (Shipped/Received):</label>
              <input class="form-control" type="text" v-model="receiving.units" placeholder="Units Received">
            </b-col>
            <b-col cols=1>
              <label class="top-label">Extra units:</label>
              <b-form-checkbox size="sm" v-model="receiving.extraUnits"></b-form-checkbox>
            </b-col>            
            <b-col cols=4>
              <label class="top-label">Container:</label>
              <input class="form-control" type="text" v-model="receiving.containerNumber" placeholder="Container #">
            </b-col>
            <b-col cols=4>
              <label class="top-label">Invoice:</label>
              <input class="form-control" type="text" v-model="receiving.invoiceNumber">
            </b-col>
            <b-col cols=4>
              <label class="top-label">Unit Price:</label>
              <input class="form-control" type="text" v-model="receiving.unitPrice">
            </b-col>          
            <b-col cols=4>
              <label class="top-label">Total Price:</label>
              <input class="form-control" type="text" v-model="totalPrice" disabled="true">
            </b-col>          
          </b-row>
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import state from "../../data/state";
import moment from "moment";

export default {
  props: {
    receivingId: {
      type: Number,
      required: true
    },
  },
  data() {
    return {
      visible: true,
      editMode: true,
      receiving: {
        shippingDate: moment().format("YYYY-MM-DD"),
        etaDate: moment().add(7, 'days').format("YYYY-MM-DD"),
      },
      purchaseComponent: {purchase: {}, component: {}},
    };
  },
  computed: {},
  watch: {},
  methods: {
    deleteReceiving() {
      http.delete("/receiving/" + this.receiving.id).then(response => {
        router.push("/receivingList");
      }).catch(e => {console.log("API Error: " + e);});
    },
    getReceiving() {
      return http.get("/receiving/" + this.receivingId).then(r => {
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
      if(!this.receiving.units || !this.receiving.unitPrice){
        alert("Please enter units and price!");
        return false;
      }
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
      this.receiving.totalPrice = this.totalPrice;
      return http.post("/receiving", this.receiving).then(r => {
        return Promise.resolve();
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    closeBill(){
      this.$emit("close");
    },
    saveBill() {
      this.save().then(r => {
        this.closeBill();
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
          if(!this.receiving.unitPrice){
            this.receiving.unitPrice = r.data.unitPrice;
          }
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
  },
  mounted() {
    this.getReceiving();
  }
};
</script>

<style>
.modal-lg{
  max-width: 90%;
}
</style>
