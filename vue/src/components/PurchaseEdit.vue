<template>
  <b-container fluid>
    <b-row>
      <b-col cols=10>
        <div style="display:flex">
          <div>
            <label class="top-label">Purchase Order</label>
            <input style="width: 130px" class="form-control" type="text" v-model="purchase.number" :disabled="receiveMode">
          </div>
          <div v-if="receiveMode" style="display:flex">
            <div style="margin-top:-2px; margin-left: 8px;">
              <label class="top-label">Packing List #:</label>
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
              <input class="form-control" type="date" v-model="purchase.date" :disabled="receiveMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Shipping Date:</label>
              <input class="form-control" type="date" v-model="purchase.shippingDate" :disabled="receiveMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">ETA Date:</label>
              <input class="form-control" type="date" v-model="purchase.expectedDate" :disabled="receiveMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Container:</label>
              <input class="form-control" type="text" v-model="purchase.containerNumber" :disabled="receiveMode">
            </div>
            <div style="width: 175px; padding-left: 3px; padding-right: 3px;">
              <label class="top-label">Invoice:</label>
              <input class="form-control" type="text" v-model="purchase.invoiceNumber" :disabled="receiveMode">
            </div>
            <div style="width: 50px">
              <label class="top-label">Confirmed:</label>
              <b-form-checkbox style="margin-left: 20px" size="lg" v-model="purchase.confirmed" :disabled="receiveMode"></b-form-checkbox>
            </div>
          </div>
        </div>
      </b-col>
      <b-col cols=2>
        <div>
          <div style="text-align: right;">
            <b-button size="sm" style="margin-right: 2px;" type="reset" variant="success" @click="save()">Save</b-button>
            <b-button v-if="!receiveMode" size="sm" style="width: 28px;" type="reset" variant="secondary" @click="deletePo()">x</b-button><br/>
            <b-button v-if="!receiveMode" size="sm" style="margin: 2px;" type="reset" variant="success" @click="receive()">Receive</b-button>
            <b-button v-if="!receiveMode" :disabled="purchase.canceled" size="sm" style="margin: 2px;" type="reset" variant="success" @click="cancelPO()">Cancel</b-button>
            <b-button v-if="receiveMode" size="sm" style="margin: 2px;" type="reset" variant="success" @click="back()">Back</b-button><br/>
          </div>          
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
          <div style="width: 200px; padding-left: 3px; padding-right: 3px;">
            <label class="top-label">P.O. Name:</label>
            <input class="form-control" type="text" v-model="purchase.name" placeholder="P.O. Name/Description">
          </div>
        Units: {{totalUnits.toLocaleString()}}<br/>
        Cases: {{totalCases.toLocaleString()}}<br/>
        Amount: ${{totalAmount.toLocaleString('en-US',{minimumFractionDigits: 2})}}
      </b-col>
      <b-col cols=6>
        <b-form-textarea :disabled="receiveMode" maxlength="250" type="text" :rows="3" v-model="purchase.notes" placeholder="Notes"></b-form-textarea>
      </b-col>
    </b-row>
    <b-row style="font-size: 12px">
      <b-col cols=2>
        <component-search v-if="!receiveMode" v-on:componentsUpdated="updateComponents"></component-search>
      </b-col>
      <b-col>
        <b-table sort-by.sync="name" sort-desc.sync="false" :items="purchase.purchaseComponents" :fields="fields">
          <template v-slot:cell(name)="row">
            <b-link @click.stop="goToComponent(row.item.component.id)">{{row.item.component.number}}</b-link>
            <span style="font-size: 11px"> ({{row.item.component.name}})</span>
          </template>
          <template v-slot:cell(unitsReceived)="row">
            <b-button v-if="!receiveMode" size="sm" @click.stop="goToReceiving(purchase.id, row.item.component.id)" variant="link">{{row.item.unitsReceived.toLocaleString()}}</b-button>
            <div v-if="receiveMode" style="display:flex">
              <span style="margin-top:10px">{{row.item.unitsReceived.toLocaleString()}}&nbsp;+&nbsp;</span>
              <v-money class="form-control" type="tel" style="width: 120px" v-bind="{precision: 0}" v-model="row.item.unitsToReceive" placeholder="0"></v-money>
              <span style="font-size: 20px; margin-left: 10px">$</span><input class="form-control" style="width: 80px" type="tel" v-model="row.item.unitPriceReceived" placeholder="0">
            </div>          
          </template>
          <template v-slot:cell(unitPrice)="row">
            <div v-if="!receiveMode" style="display:flex">
              <span style="font-size:20px">$</span><input class="form-control" style="width: 100px" type="tel" v-model="row.item.unitPrice">
            </div>
            <span v-if="receiveMode">${{row.item.unitPrice}}</span>
          </template>
          <template v-slot:cell(units)="row">
            <v-money v-if="!receiveMode" class="form-control" style="width: 120px" type="tel" v-bind="{precision: 0}" v-model="row.item.units"></v-money>  
            <span v-if="receiveMode">{{row.item.units.toLocaleString()}}</span>
          </template>
          <template v-slot:cell(casePack)="row">
            <span>{{row.item.component.casePack.toLocaleString()}}</span>
          </template>             
          <template v-slot:cell(cases)="row">
            <span>{{Math.ceil(row.item.units / row.item.component.casePack).toLocaleString()}}</span>
          </template>             
          <template v-slot:cell(totalPrice)="row">
            ${{row.item.totalPrice = getTotalPrice(row.item).toLocaleString('en-US',{minimumFractionDigits: 2})}}
          </template>
          <template v-slot:cell(spoilage)="row">
            ${{(getTotalPrice(row.item) + (getTotalPrice(row.item)* 0.03)).toLocaleString('en-US',{minimumFractionDigits: 2})}}
          </template>
          <template v-slot:cell(action)="row">
            <b-button :disabled="receiveMode" size="sm" @click="deletePc(row.item)">x</b-button>
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
  components:{
    'component-search': ComponentSearch
  },
  data() {
    return {
      receivingDate: null,
      receivingNumber: null,
      receivingContainerNumber: null,
      receivingInvoiceNumber: null,
      receiveMode: false,
      purchase: {
        purchaseComponents: [],
        supplier: {},
        number: null,
        date: moment().format("YYYY-MM-DD"),
        shippingDate: moment().format("YYYY-MM-DD"),
        expectedDate: moment().format("YYYY-MM-DD"),
      },
      purchaseComponents: [],
      purchaseComponent: {},
      fields: [
        { key: "name", label: "Name", sortable: false },
        { key: "component.unitCost", label: "Unit Cost", sortable: false },
        { key: "unitPrice", label: "P.O. Price", sortable: false },
        { key: "units", label: "P.O. Units", sortable: false },
        { key: "casePack", label: "C/P", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
        { key: "unitsReceived", label: "Received", sortable: false },
        { key: "totalPrice", label: "Total", sortable: false },
        { key: "spoilage", label: "Spoilage", sortable: false },
        { key: "action", label: "Action", sortable: false },
      ],
      componentDtos: [],
    };
  },
  computed: {
    totalUnits(){
      var units = 0;
      if(this.purchase.purchaseComponents){
        this.purchase.purchaseComponents.forEach(pc => {
          units += +pc.units;
        })
      }
      return units;
    },
    totalCases(){
      var cases = 0;
      if(this.purchase.purchaseComponents){
        this.purchase.purchaseComponents.forEach(pc => {
          if(pc.component.casePack>0){
            cases += +Math.ceil(pc.units / pc.component.casePack);
          }
        })
      }
      return cases;
    },
    totalAmount(){
      var amount = 0;
      if(this.purchase.purchaseComponents){
        this.purchase.purchaseComponents.forEach(pc => {
          amount += +this.getTotalPrice(pc);
        })
      }
      return amount;
    }
  },
  watch: {
  },
  methods: {
    updateComponents(searchDto){
      this.getComponentDtos(searchDto.components.join(","));
    },
    getComponentDtos(componentIds){
      http.get("/components/dto/", {params: {componentIds: componentIds}}).then(r=> {
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
            if(this.componentDtos.length==0){
              this.purchase.supplier = {};
            }
            return Promise.reject();
        }
        r.data.forEach(dto => {
          var existing = this.componentDtos.find(selected => selected.id == dto.id)
          if(!existing){
            var units = dto.unitsShort < 0?0:dto.unitsShort;
            this.purchase.purchaseComponents.push({component: {id: dto.id, name: dto.name, number: dto.number, casePack: dto.casePack, unitCost: dto.unitCost}, 
              units: units, unitPrice: dto.unitCost, unitsReceived: dto.unitsReceived});
          }
        })
      }).catch(e=> {
        console.log("API error: "+e);
      })
    },
    getPurchaseNumber() {
      if(this.purchase.number){
        return
      }
      return http.get("/purchase/number/"+moment.utc().local().format("YYYY-MM-DD")).then(r => {
        this.purchase.number = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    }, 
    getCases(pc){
      var cases = 0;
      if(pc.casePack>0){
        cases = Math.ceil(pc.units / pc.component.casePack).toLocaleString();
      }
      return cases;
    },
    formatter(value){
      console.log("Format: "+value)
    },
    deletePo(){
      if(!this.purchase.id){
        router.push("/purchaseList");
        return;
      }
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
      // this.save();
    },
    back(){
      this.getPurchase(this.purchase.id).then(purchase =>{
        this.receiveMode = false;
      })
    },
    cancelPO(){
      this.purchase.canceled = true;
      this.updatePurchase();
    },
    receive(){
      this.updatePurchase().then(r => {
        this.purchase.purchaseComponents.forEach(pc=> {
          pc.unitsToReceive = (+pc.units - +pc.unitsReceived) < 0?0:(+pc.units - +pc.unitsReceived);
          pc.unitPriceReceived = pc.unitPrice;
        })
        // this.receivingNumber = "Rec-"+this.purchase.number;
        this.receivingContainerNumber = this.purchase.containerNumber;
        this.receivingInvoiceNumber = this.purchase.invoiceNumber;
        this.receivingDate = null;
        this.receiveMode = true;
      })
    },
    save(){
      if(this.receiveMode){
        this.saveReceive();
      }else {
        this.updatePurchase();
      }
    },
    validateReceiving(){
      if(!this.receivingNumber || !this.receivingDate){
        alert("Packing list # and Received date required");
        return false;
      }
      return true;
    },
    validatePurchase(){
      if(this.purchase.purchaseComponents.length == 0){
        alert("No Component selected.");
        return false;
      }
      if(!this.purchase.number || !this.purchase.date){
        alert("Purchase number and date required");
        return false;
      }
      var empty = this.purchase.purchaseComponents.find(c => c.units <= 0 || c.unitPrice <=0);
      if(empty){
          alert("Enter positive units and price for each component");
          return false;
      }
      return true;
    },
    updatePurchase(){
      if(!this.validatePurchase()) {return}
      return http.post("/purchase", this.purchase).then(r => {
        this.purchase = r.data;
        this.receiveMode = false;
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    saveReceive(){
      if(!this.validateReceiving()) {return}
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
        if(pc.unitsToReceive == 0){
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
        receiving.totalPrice = (+receiving.units * +receiving.unitPrice);
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
          this.receiveMode = false;
        }).catch(e => {
          console.log("API error: " + e);
        });
      }
    },
    getTotalPrice(pc){
      return (pc.units * pc.unitPrice);
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
    var purchaseId = this.$route.params.purchase_id;
    var componentIds = this.$route.query.componentIds;
    if(purchaseId){
      this.getPurchase(purchaseId);
    }
    if(componentIds){
      this.getComponentDtos(componentIds);
    }
    this.getPurchaseNumber();
  }
};
</script>

