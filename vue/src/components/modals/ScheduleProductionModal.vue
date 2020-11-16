<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=6>
          <div>Item: {{item.number + ' (' + item.name +')'}}</div>
          <div v-if="saleItem">Package: {{saleItem.itemPackaging.label}}</div>
          <div v-if="saleItem">Sale: {{saleItem.sale.number}}</div>
          <div v-if="saleItem">Customer: {{saleItem.sale.customer.name}}</div>
        </b-col>
        <b-col cols=6>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="saveModal()">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" type="reset" variant="secondary" @click="closeModal()">Cancel</b-button>
          </div>
        </b-col>        
      </b-row>
      <b-row>
        <b-col cols=5>
          <label class="top-label">Packaging:</label>
          <b-select :isDisabled="scheduleEvent.unitsProduced > 0" option-value="id" option-text="label" :list="item.itemPackagings" v-model="scheduleEvent.itemPackaging"></b-select>
        </b-col>
        <b-col cols=5>
          <b-row>
            <b-col cols=12>
              <label class="top-label">Schedule Date:</label>
              <input :disabled="scheduleEvent.unitsProduced > 0" class="form-control" type="date" v-model="scheduleEvent.date">
            </b-col>
          </b-row>
          <b-row>
            <b-col cols=10>
              <label class="top-label">Start Time:</label>
              <input :disabled="scheduleEvent.unitsProduced > 0" class="form-control" type="time" v-model="scheduleEvent.scheduleTime">
            </b-col>
          </b-row>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=3>
          <label class="top-label">Line:</label>
          <b-select :isDisabled="scheduleEvent.unitsProduced > 0" option-value="id" option-text="number" :list="availableLines" v-model="scheduleEvent.line"></b-select>
        </b-col>
        <b-col cols=4>
          <label class="top-label">Units to Schedule:</label>
          <input class="form-control" type="tel" v-model="scheduleEvent.unitsScheduled">
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
    itemId: {
      type: Number,
      required: true
    },
    saleItemId: {
      type: Number,
    },
    scheduleEventId: {
      type: Number,
    }
  },
  data() {
    return {
      visible: true,
      item: {
        itemPackagings: []
      },
      saleItem: {
        sale: {
          customer: {},
        },
        itemPackaging: {}
      },
      scheduleEvent: {
        itemPackaging: {},
        line: {},
        item: {},
        saleItem: {},
      },
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
    };

  },
  computed: {
  },
  watch: {},
  methods: {
    getScheduleEvent(){
      if(!this.scheduleEventId) {return}
      http.get("/scheduleEvent/"+this.scheduleEventId).then(r => {
        this.scheduleEvent = r.data;
      }).catch(e => {console.log("API error: "+e);});
    },
    getItem(){
      http.get("/item/"+this.itemId).then(r => {
        this.item = r.data;
        if(!this.scheduleEvent.item.id){
          this.scheduleEvent.item = r.data;
        }
      }).catch(e => {console.log("API error: "+e);});
    },
    getSaleItem(){
      if(!this.saleItemId) {return}
      http.get("/saleItem/"+this.saleItemId).then(r => {
        this.saleItem = r.data;
        if(!this.scheduleEvent.itemPackaging.id){
          this.scheduleEvent.itemPackaging = r.data.itemPackaging;
        }
        this.scheduleEvent.unitsScheduled = +r.data.units - +r.data.unitsScheduled;
      }).catch(e => {console.log("API error: "+e);});
    },
    validate(){
      if(!this.scheduleEvent.date || !this.scheduleEvent.scheduleTime || !this.scheduleEvent.line || !this.scheduleEvent.unitsScheduled){
        alert("Please enter all the fields");
        return false;
      }
      if(this.saleItem && this.scheduleEvent.unitsScheduled > this.saleItem.units){
        alert("Cannot schedule more that sold");
        return false;
      }
      if(this.scheduleEvent.unitsScheduled < this.scheduleEvent.unitsProduced){
        alert("Cannot schedule less than produced");
        return false;
      }
      return true;
    },
    saveModal(){
      if(!this.validate()){
        return false;
      }
      if(this.saleItem.id){
        this.scheduleEvent.saleItem = this.saleItem;
      }else{
        this.scheduleEvent.saleItem = null;
      }
      this.scheduleEvent.item = this.item;
      http
        .post("/scheduleEvent", this.scheduleEvent)
        .then(response => {
          this.closeModal();
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    closeModal(){
      this.$emit("close");
    },
  },
  mounted() {
    this.getScheduleEvent();
    this.getSaleItem();
    this.getItem();
  }
};
</script>
<style>
</style>
