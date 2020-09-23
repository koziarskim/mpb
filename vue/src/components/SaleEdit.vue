<template>
  <b-container fluid>
    <b-row>
      <b-col cols=6>
        <b-row>
          <b-col cols=4>
            <label class="top-label">Sale Number:</label>
            <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="sale.number" placeholder="Number">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Sale Date:</label>
            <input :disabled="!allowEdit()" class="form-control" type="date" v-model="sale.date" placeholder="Date">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Expected Date:</label>
            <input :disabled="!allowEdit()" class="form-control" type="date" v-model="sale.expectedDate" placeholder="Date">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=6>
            <label class="top-label">Customer:</label>
            <b-select :isDisabled="!allowEdit()" option-value="id" option-text="value" :list="availableCustomers" v-model="customerDto" placeholder="Customer"></b-select>
          </b-col>
          <b-col cols=4>
            <label class="top-label">Pay Terms:</label>
            <input :disabled="!allowEdit()" class="form-control" type="tel" v-model="sale.paymentTerms">
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Notes:</label>
        <b-form-textarea :disabled="!allowEdit()" type="text" :rows="4" v-model="sale.notes"></b-form-textarea>
      </b-col>
      <b-col cols=2>
        <div style="display: flex; text-align: right">
          <upload-file v-if="sale.id" v-on:close="closeUpload" :entity-id="sale.id" type="Sale" :attachments="sale.attachments"></upload-file>
          <b-button v-if="!sale.approved && sale.pendingApproval" style="margin-left: 3px" size="sm" variant="success" @click="approveSale()">Approve</b-button>
          <b-button v-if="!sale.pendingApproval" style="margin-left: 3px" size="sm" variant="success" @click="readySale()">Ready</b-button>
          <b-button :title="getSaveTitle(sale)" style="margin-left: 3px" :disabled="!allowSave()" size="sm" variant="success" @click="saveSale()">Save</b-button>
          <b-button style="margin-left: 3px" :disabled="!allowEdit()" size="sm" @click="cancelSale()">Cancel</b-button>
          <b-button style="margin-left: 3px" :disabled="!allowEdit()" size="sm" @click="deleteSale()">x</b-button>
        </div>
        <br/>
        <span style="font-weight: bold">{{getStatus(sale.status)}}</span><br/>
        <label class="top-label">Stock: {{sale.unitsOnStock}},&nbsp;&nbsp;</label>
        <label class="top-label">Shed/Prod: {{sale.unitsScheduled}}/{{sale.unitsProduced}}</label><br/>
        <label class="top-label">Sold: {{sale.unitsSold}},&nbsp;&nbsp;</label>
        <label class="top-label">Shipped: <b-link role="button" @click="goToShipment()">{{sale.unitsShipped}}</b-link></label>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Shipment Information">
    <b-row>
      <b-col cols=4>
        <label class="top-label">Shipping to Address:</label>
        <b-select :isDisabled="!allowEdit()" option-value="id" option-text="label" :list="customer.addresses" v-model="shippingAddress" placeholder="shipping to address"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Window From:</label>
        <input :disabled="!allowEdit()" class="form-control" type="date" v-model="sale.shippingFrom" placeholder="Date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Window To:</label>
        <input :disabled="!allowEdit()" class="form-control" type="date" v-model="sale.shippingTo" placeholder="Date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Freight Terms:</label>
        <b-select option-value="id" option-text="name" :list="availableFreightTerms" v-model="sale.freightTerms"></b-select>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Sale Items">
    <b-row>
      <b-col cols=4>
        <label class="top-label">Available Items:</label>
        <b-select :isDisabled="!allowEdit()" option-value="id" option-text="name" :list="availableItems" v-model="itemDto" placeholder="Items"></b-select>
      </b-col>
      <b-col cols=1 style="padding-top: 30px">
        <b-button :disabled="!allowEdit()" variant="link" @click="addItem()">(+)</b-button>
      </b-col>
      <b-col cols=5 offset=1 style="margin-top: 44px; margin-left: 0px;">
          <span style="font-weight: bold">Items #: </span>{{totalItems}},
          <span style="font-weight: bold">Units: </span>{{totalUnits}},
          <span style="font-weight: bold">Cases: </span>{{totalCases}},
          <span style="font-weight: bold">Total: </span>${{totalPrice}}
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="sale.saleItems.length>0" :tbody-tr-class="rowClass" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="sale.saleItems" :fields="columns">
          <template v-slot:cell(item)="row">
            <b-link role="button" @click.stop="goToItem(row.item.item.id)">{{row.item.item.number}}</b-link>
            <div class="name-sm" :title="row.item.item.name"> ({{row.item.item.name}})</div>
          </template>
          <template v-slot:cell(sku)="row">
            <input :disabled="!allowEdit()" class="form-control" style="width:100px" type="tel" v-model="row.item.sku">
          </template>
          <template v-slot:cell(cost)="row">
            <span>${{row.item.item.totalCost}}</span>
          </template>
          <template v-slot:cell(unitsOnStockRet)="row">
            <span>{{row.item.unitsOnStock}} </span><b-link @click.stop="goToItemReturnList(row.item)">({{row.item.unitsReturned}})</b-link>
          </template>
          <template v-slot:cell(unitsSchedProd)="row">
            <b-button size="sm" variant="link" @click="goToScheduled(row.item)">{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(units)="row">
            <input :disabled="!allowEdit()" class="form-control" style="width:80px" type="tel" v-model="row.item.units">
          </template>
          <template v-slot:cell(unitsAdjusted)="row">
            <input class="form-control" style="width:80px" type="tel" v-model="row.item.unitsAdjusted">
          </template>
          <template v-slot:cell(cases)="row">
            <span>{{getCases(row.item)}}</span>
          </template>
          <template v-slot:cell(unitPrice)="row">
            <div style="display:flex">
            $<input :disabled="!allowEdit()" class="form-control" style="display: inline; width:80px" type="tel" v-model="row.item.unitPrice">
            </div>
          </template>
          <template v-slot:cell(totalUnitPrice)="row">
            <span>${{row.item.totalUnitPrice = (+row.item.unitPrice * +row.item.units).toFixed(2)}}</span>
          </template>
          <template v-slot:cell(unitsTransfered)="row">
            <b-button size="sm" variant="link" @click="openTransferModal(row.item)">{{row.item.unitsTransferedTo}}-{{row.item.unitsTransferedFrom}}</b-button>
          </template>
          <template v-slot:cell(unitsShipped)="row">
            <b-button size="sm" variant="link" @click="goToShipment(row.item)">{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" :id="'popover-'+row.item.id">...</b-button>
            <b-popover placement="left" :target="'popover-'+row.item.id" variant="light">
              <div style="width: 340px">
                <b-button :disabled="!allowEdit()" size="sm" @click="deleteItem(row.item)" variant="link">Delete Item</b-button><br/>
                <div style="display:flex;">
                  <b-button :disabled="!allowSave()" size="sm" @click="moveItem(row.item)" variant="link">Move Item</b-button>
                  <b-select style="width: 250px" option-value="id" option-text="name" :list="availableSales" v-model="saleKv"></b-select>
                </div>
              </div>
            </b-popover>
          </template>
        </b-table>
      </b-col>
    </b-row>
    <b-modal centered size="lg" v-model="modalVisible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols="2">
          <label class="top-label">Units Sold:</label>
          <input class="form-control" type="tel" v-model="unitsForSale" placeholder="0">
        </b-col>
        <b-col cols="2">
          <label class="top-label">Unit Price:</label>
          <input class="form-control" type="tel" v-model="unitPrice" placeholder="0.00">
        </b-col>
      </b-row>
      <br/>
      <b-row>
        <b-col>
          <div style="text-align: right;">
            <b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
            <b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
          </div>
        </b-col>
      </b-row>
    </b-modal>
    <div v-if="transferModalVisible">
			<transfer-modal :sale-item-to="saleItem" v-on:saveModal="saveTransferModal"></transfer-modal>
		</div>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite";
import moment from "moment";

export default {
  components: {
    TransferModal: () => import("./TransferModal"),
    UploadFile: () => import("../directives/UploadFile"),
  },
  data() {
    return {
      securite: securite,
      modalVisible: false,
      transferModalVisible: false,
      locked: false,
      sale: {
        saleItems: [],
        customer: {},
        shippingAddress: {},
        freightTerms: {},
        paymentTerms: "",
      },
      customer: {
        addresses: []
      },
      item: {},
      shippingAddress: {},
      availableCustomers: [],
      availableItems: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item", label: "Item", sortable: false },
        { key: "sku", label: "SKU#", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "unitsAdjusted", label: "Adjusted", sortable: false },
        { key: "unitsOnStockRet", label: "Stock(R)", sortable: false },
        { key: "unitsSchedProd", label: "Sched/Prod", sortable: false },
        { key: "unitsTransfered", label: "Trans", sortable: false },
        { key: "unitsShipped", label: "Ship", sortable: false },
        { key: "cases", label: "Case", sortable: false },
        { key: "cost", label: "Cost", sortable: false },
        { key: "unitPrice", label: "Unit Price", sortable: false },
        { key: "totalUnitPrice", label: "Total", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      customerDto: {},
      itemDto: {},
      unitsForSale: null,
      unitPrice: null,
      saleItem: {},
      saleFromIds: [],
      availableFreightTerms: [
        {id: "TPB", name: "TP Bill"},
        {id: "PRP", name: "Pre Paid"},
        {id: "TPO", name: "TP Bill Other"},
        {id: "COL", name: "Collect"},
        {id: "DEL", name: "Delivered"},
        {id: "CPU", name: "Customer Pickup"}
      ],
      availableSales: [],
      saleKv: {},
      availableStatus: [
        {id: 'DRAFT', name: 'Draft'},
        {id: 'PENDING_APPROVAL', name: 'Pending Approval'},
        {id: 'APPROVED', name: 'Pending Schedule'},
        {id: 'PENDING_PROD', name: 'Pending Prod'},
        {id: 'PENDING_SHIPMENT', name: 'Pending Shipment'},
        {id: 'SHIPPED', name: 'Fully Shipped'},
        {id: 'CANCELLED', name: 'Cancelled'},
      ],
    };
  },
  computed: {
      totalPrice(){
          var price = 0;
          this.sale.saleItems.forEach(si=> {
              price += +(si.totalUnitPrice?si.totalUnitPrice:0);
          })
          return price.toFixed(2);
      },
      totalItems(){
          var items = 0;
          this.sale.saleItems.forEach(si=> {
              items ++;
          })
          return items;
      },
      totalUnits(){
          var units = 0;
          this.sale.saleItems.forEach(si=> {
              units += +(si.units?si.units:0);
          })
          return units;
      },
      totalCases(){
          var cases = 0;
          this.sale.saleItems.forEach(si=> {
            var units = si.units?si.units:0;
            var casePack = si.item.casePack?si.item.casePack:0
              cases += +units/+casePack.toFixed(0);
          })
          return cases;
      },
  },
  watch: {
    shippingAddress(new_value, old_value) {
      this.sale.shippingAddress = new_value;
    },
    customerDto(new_value, old_value){
      if(this.sale.customer.id != new_value.id){
        this.sale.freightTerms = "";
        this.sale.paymentTerms = "";
      }
      this.getCustomer(new_value.id);
    },
    itemDto(new_value, old_value){
      if(new_value && new_value.id){
        this.getItem(new_value.id)
      }
    }
  },
  methods: {
    rowClass(item, type) {
        if(item.status == 'SHIPPED'){
          return 'fully-shipped'
        }
        return;
      },
    goToItemReturnList(saleItem){
      var query = { saleId: this.sale.id, itemId: saleItem.item.id };
      router.push({path: "/itemReturnList", query: query});
    },
    closeUpload(attachments){
      this.sale.attachments = attachments;
      this.saveSale();
    },
    getSaveTitle(sale){
      return "Created: "+moment(sale.created).format("YYYY/MM/DD HH:mm")+"\n"+
      "Modified: "+moment(sale.modifiedDate).format("YYYY/MM/DD HH:mm");
    },
    getStatus(statusId){
      var status = this.availableStatus.find(s=> s.id == statusId);
      if(status){
        return status.name
      }
      return "Not Yet Saved";
    },
    allowEdit(){
      return securite.hasRole(["SALE_ADMIN"]);
      // return securite.hasRole(["SALE_ADMIN"]);
    },
    allowSave(){
      return securite.hasRole(["SALE_ADMIN"]);
    },
    allowApprove(){
      return !this.sale.approved && securite.hasRole(["SALE_ADMIN"]);
      // return securite.hasRole(["SALE_ADMIN"]);
    },
    goToScheduled(si) {
      router.push("/scheduleEventList/" + si.item.id + "/sale/" + this.sale.id);
    },
    goToShipment(si){
      var query = { saleId: this.sale.id };
      if(si){
        query.itemId = si.item.id;
      }
      router.push({path: "/shipmentList", query: query})
    },
    openTransferModal(saleItem){
      if(!this.sale.id){
        alert("Please, save sale before adding transfer");
        return;
      }
      this.saleItem = saleItem;
      this.saleItem.saleNumber = this.sale.number
      this.transferModalVisible = true;
    },
    saveTransferModal(saleItem){
      this.saleItem = {},
      this.transferModalVisible = false;
    },
    saveModal(){
      this.closeModal();
    },
    closeModal(){
      this.unitsForSale = null,
      this.unitPrice = null,
      this.modalVisible = false;
    },
    getCustomer(customer_id){
      http.get("/customer/"+customer_id).then(response =>{
        this.customer = response.data;
        // this.shippingAddress = {};
        if(!this.sale.paymentTerms){
          this.sale.paymentTerms = response.data.paymentTerms;
        }
        if(!this.sale.freightTerms){
          this.sale.freightTerms = response.data.freightTerms;
        }
        this.sale.customer = response.data;
      }).catch(e =>{
        console.log("API error: " + e);
      })
    },
    getItem(item_id){
      http.get("/item/"+item_id).then(response =>{
        this.item = response.data;
      }).catch(e =>{
        console.log("API error: " + e);
      })
    },
    getSaleData(id) {
      return http.get("/sale/" + id).then(response => {
        this.sale = response.data;
        this.setSaleFromIds();
        if (response.data.customer) {
          this.customerDto = {
            id: response.data.customer.id,
            value: response.data.customer.name
          }
          this.getAvailableSales();
        }
        if (response.data.shippingAddress) {
          this.shippingAddress = response.data.shippingAddress;
        }
        return response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    setSaleFromIds(){
      this.sale.saleItems.forEach(si => {
        si.transfersTo.forEach(sit => {
          this.saleFromIds.push(sit.saleFromId);
        })
      })
    },
    readySale(){
      this.sale.pendingApproval = true;
      this.saveSale().then(r=> {
        this.getSaleData(r.data.id);
      }).catch(e => {
        this.sale.pendingApproval = false;
      })
    },
    approveSale(){
      if(!this.allowApprove()){
        alert("Don't have permission");
        return;
      }
      this.sale.approved = true;
      this.saveSale().then(r=> {
        this.getSaleData(r.data.id);
      }).catch(e => {
        this.sale.approved = false;
      })
    },
    saveSale() {
      if(!this.allowSave()){
        alert("Don't have permission");
        return Promise.reject();
      }
      if(!this.validate()){
        return Promise.reject();
      }
      this.sale.saleItems.forEach(si=>{
        si.unitsAdjusted = si.unitsAdjusted || 0;
        si.units == si.units || 0;
      })
      this.sale.totalPrice = this.totalPrice;
      if(!this.sale.shippingAddress || !this.sale.shippingAddress.id){
        this.sale.shippingAddress = null;
      }
      return http.post("/sale", this.sale).then(r => {
        this.getSaleData(r.data.id);
        return r;
      }).catch(e => {
        console.log("API error: " + e);
      });
      return r;
    },
    validate(){
      if(!this.sale.number || !this.sale.customer.id){
        alert("Number, Customer required");
        return false;
      }
      return true;
    },
    cancelSale(){
      this.sale.cancelled = !this.sale.cancelled;
      this.saveSale();
    },
    deleteSale() {
      if(this.sale.saleItems.length > 0 ){
        alert("There are existing items. Please, move or delete items first");
        return;
      }
      this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Sale?").then(ok => {
        if(ok){
          http.delete("/sale/"+this.sale.id).then(r => {
            router.push('/saleList/');
          }).catch(e => {
            console.log("API Error: "+e);
          });
            }
        })
    },
    getAvailableCustomers() {
      http
        .get("/customer/kv")
        .then(response => {
          this.availableCustomers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableItems() {
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableSales() {
      http.get("/sale/kv/customer/"+this.sale.customer.id).then(r => {
        this.availableSales = r.data.filter(s => s.id != this.sale.id);
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    moveItem(saleItem){
      http.post("/saleItem/"+saleItem.id+"/move/to/sale/"+this.saleKv.id).then(r => {
        this.saleKv = {};
        this.getSaleData(this.sale.id);
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    addItem() {
      if (!this.item.id) {
        return;
      }
      var item = this.sale.saleItems.find(it => it.item.id == this.item.id);
      if (item) {
        return;
      }
      this.sale.saleItems.push({ 
          units: 0,
          unitPrice: 0.00,
          item: this.item,
          unitsTransferedTo: 0,
          unitsTransferedFrom: 0,
          transfersTo: [],
          transfersFrom: [] 
      });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    gotToItemComponentList(item_id){
        router.push('/itemComponentList/'+item_id);
    },
    deleteItem(si) {
      if(si.unitsScheduled>0 || si.unitsProduced>0 || si.unitsTransferedTo>0 || si.unitsTransferedFrom>0 || si.unitsShipped>0){
        alert("Make sure there is no Schedule, Production, Transfers or Shipment for this item!");
        return false;
      }
      var idx = this.sale.saleItems.findIndex(it => it.id == si.id);
      this.sale.saleItems.splice(idx, 1);
      this.saveSale();
    },
    getCases(si){
      return (+si.units / +si.item.casePack).toFixed(0);
    }
  },
  mounted() {
    var id = this.$route.params.sale_id;
    if (id) {
      this.getSaleData(id);
    }
    this.getAvailableCustomers();
    this.getAvailableItems();
  }   
};
</script>

<style>
.fully-shipped {
  background-color: #b6e6c9;
}
</style>
