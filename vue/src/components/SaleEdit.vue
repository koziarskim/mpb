<template>
  <b-container fluid>
    <b-row>
      <b-col cols=6>
        <b-row>
          <b-col cols=4>
            <label class="top-label">Sale Number:</label>
            <input :disabled="disableEditSale()" maxlength="18" class="form-control" type="text" v-model="sale.number" placeholder="Number">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Sale Date:</label>
            <input :disabled="disableEditSale()" class="form-control" type="date" v-model="sale.date" placeholder="Date">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Expected Date:</label>
            <input :disabled="disableEditSale()" class="form-control" type="date" v-model="sale.expectedDate" placeholder="Date">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=6>
            <label class="top-label">Customer:</label>
            <b-select :isDisabled="disableEditSale()" option-value="id" option-text="value" :list="availableCustomers" v-model="customerDto" placeholder="Customer"></b-select>
          </b-col>
          <b-col cols=4>
            <label class="top-label">Pay Terms:</label>
            <input :disabled="disableEditSale()" class="form-control" type="tel" v-model="sale.paymentTerms">
          </b-col>
          <b-col cols=2>
            <label class="top-label">PCR:</label><br/>
            <span>{{pcr==true?'Ready':'Not Ready'}}</span>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Notes:</label>
        <b-form-textarea :disabled="disableEditSale()" type="text" :rows="4" v-model="sale.notes"></b-form-textarea>
      </b-col>
      <b-col cols=2>
        <div style="display: flex; text-align: right">
          <upload-file v-if="sale.id" v-on:close="closeUpload" :entity-id="sale.id" type="Sale" :attachments="sale.attachments"></upload-file>
          <b-button v-if="!sale.approved && sale.pendingApproval" style="margin-left: 3px" size="sm" variant="success" @click="approveSale()">Approve</b-button>
          <b-button v-if="!sale.pendingApproval" style="margin-left: 3px" size="sm" variant="success" @click="readySale()">Ready</b-button>
          <b-button :title="getSaveTitle(sale)" style="margin-left: 3px" :disabled="!allowSave()" size="sm" variant="success" @click="saveSale()">Save</b-button>
          <b-button size="sm" :id="'popover-menu'+sale.id" style="margin-left: 3px">...</b-button>
            <b-popover placement="bottomleft" :target="'popover-menu'+sale.id" variant="secondary">
              <div style="width: 240px">
              <b-button style="margin-left: 3px" :disabled="disableEditSale()" size="sm" @click="cancelSale()">Cancel</b-button>
              <b-button style="margin-left: 3px" :disabled="disableEditSale()" size="sm" @click="copySale()">Copy</b-button>
              <b-button style="margin-left: 3px" :disabled="disableEditSale()" size="sm" @click="deleteSale()">Delete</b-button>
              </div>
            </b-popover>
        </div>
        <input type="checkbox" style="margin-top: 10px" v-model="sale.paidInFull"><span style="font-size: 14px"> Paid in Full</span>
        <br/>
        <span style="font-weight: bold">{{getStatus(sale.status)}}</span><br/>
        <label class="top-label">Shed/Prod: {{sale.unitsScheduled}}/{{sale.unitsProduced}}</label><br/>
        <label class="top-label">Sold: {{sale.unitsSold}},&nbsp;&nbsp;</label>
        <label class="top-label">Shipped: <b-link role="button" @click="goToShipment()">{{sale.unitsShipped}}</b-link></label>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Shipment Information">
    <b-row>
      <b-col cols=4>
        <label class="top-label">Shipping to Address:</label>
        <b-select :isDisabled="disableEditSale()" option-value="id" option-text="label" :list="customer.addresses" v-model="shippingAddress" placeholder="shipping to address"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Window From:</label>
        <input :disabled="disableEditSale()" class="form-control" type="date" v-model="sale.shippingFrom" placeholder="Date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Window To:</label>
        <input :disabled="disableEditSale()" class="form-control" type="date" v-model="sale.shippingTo" placeholder="Date">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Freight Terms:</label>
        <b-select :isDisabled="disableEditSale()" option-value="id" option-text="name" :list="availableFreightTerms" v-model="sale.freightTerms"></b-select>
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Sale Items">
    <b-row>
      <b-col cols=4>
        <label class="top-label">Available Items:</label>
        <b-select :isDisabled="disableEditSale()" option-value="id" option-text="name" :list="availableItems" v-model="itemDto" placeholder="Items"></b-select>
      </b-col>
      <b-col cols=4>
        <label class="top-label">Available Packaging:</label>
        <b-select :isDisabled="disableEditSale()" option-value="id" option-text="label" :list="item.itemPackagings" v-model="itemPackaging" placeholder="Packaging"></b-select>
      </b-col>
      <b-col cols=1 style="margin-top: 30px">
        <b-button :disabled="disableEditSale()" variant="link" @click="addItem()">(+)</b-button>
      </b-col>
      <b-col cols=3 style="margin-top: 20px;">
          <span style="font-weight: bold">Items #: </span>{{totalItems}}, <span style="font-weight: bold">Units: </span>{{totalUnits}}<br/>
          <span style="font-weight: bold">Cases: </span>{{Math.ceil(totalCases)}}, <span style="font-weight: bold">Total: </span>${{totalPrice}}
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label"></label>
        <b-table v-if="sale.saleItems.length>0" :tbody-tr-class="rowClass" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="sale.saleItems" :fields="columns">
          <template v-slot:cell(item)="row">
            <b-link role="button" @click.stop="goToItem(row.item.itemPackaging.item.id)">{{row.item.itemPackaging.item.number}}</b-link>
            <div class="name-sm" :title="row.item.itemPackaging.item.name"> ({{row.item.itemPackaging.item.name}})</div>
          </template>
          <template v-slot:cell(packaging)="row">
            <div style="width:100px; overflow: wrap; font-size: 11px">{{row.item.itemPackaging.packaging.name}}</div>
          </template>
          <template v-slot:cell(unitsAssigned)="row">
            <input :disabled="disableEditItem(row.item)" class="form-control" style="width:80px" type="tel" v-model="row.item.unitsAssigned">
          </template>
          <template v-slot:cell(cost)="row">
            <span>${{(+row.item.itemPackaging.item.totalCost + +row.item.itemPackaging.packaging.totalPackagingCost).toFixed(2)}}</span>
          </template>
          <template v-slot:cell(unitsSchedProd)="row">
            <b-button size="sm" variant="link" @click="goToScheduled(row.item)">{{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(units)="row">
            <input :disabled="disableEditItem(row.item)" class="form-control" style="width:80px" type="tel" v-model="row.item.units">
          </template>
          <template v-slot:cell(unitsAdjusted)="row">
            <input class="form-control" style="width:80px" type="tel" v-model="row.item.unitsAdjusted">
          </template>
          <template v-slot:cell(cases)="row">
            <span>{{getCases(row.item).toFixed(0)}}</span>
          </template>
          <template v-slot:cell(unitPrice)="row">
            <div style="display:flex">
            $<input :disabled="disableEditItem(row.item)" class="form-control" style="display: inline; width:80px" type="tel" v-model="row.item.unitPrice">
            </div>
          </template>
          <template v-slot:cell(totalUnitPrice)="row">
            <span>${{getTotalUnitPrice(row.item)}}</span>
          </template>
          <template v-slot:cell(invoicedAmount)="row">
            <span>${{parseFloat(row.item.invoicedAmount).toLocaleString('en-US',{minimumFractionDigits: 2})}}</span>
          </template>
          <template v-slot:cell(unitsShipped)="row">
            <b-button size="sm" variant="link" @click="goToShipment(row.item)">{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" :id="'popover-menu'+row.item.id" @click="openActionMenu(row.item)">...</b-button>
            <b-popover placement="bottomleft" :target="'popover-menu'+row.item.id" variant="secondary">
                <b-row>
                  <b-col cols=4>
                    <label class="top-label">SKU: </label>
                    <input class="form-control" type="tel" v-model="row.item.sku">
                  </b-col>
                  <b-col cols=4>
                    <label class="top-label">DEPT: </label>
                    <input class="form-control" type="tel" v-model="row.item.department">
                  </b-col>
                  <b-col cols=4>
                    <label class="top-label">Best By: </label>
                    <input class="form-control" type="date" v-model="row.item.expiration">
                  </b-col>
                </b-row>
                <br/>
                <b-row>
                  <b-col cols=5>
                    <div style="display: flex">
                      <div>
                        <b-button style="" size="sm" variant="link" @click="downloadCarton(row.item)">Carton Label</b-button><br/>
                        <label style="margin-left: 10px" class="top-label">Source: {{getCartonSource(row.item)}}</label>
                      </div>
                      <input class="form-control" style="font-size: 12px; width: 60px; height: 30px" type="tel" v-model="pageFromCarton">-
                      <input class="form-control" style="font-size: 12px; width: 60px; height: 30px" type="tel" v-model="pageToCarton">
                    </div>
                  </b-col>
                  <b-col cols=5>
                    <div style="display: flex">
                      <b-button style="" size="sm" variant="link" @click="downloadTag(row.item)">Pallet Tag</b-button>
                      <input class="form-control" style="font-size: 12px; width: 60px; height: 30px" type="tel" v-model="pageFromTag">-
                      <input class="form-control" style="font-size: 12px; width: 60px; height: 30px" type="tel" v-model="pageToTag">
                    </div>
                  </b-col>
                  <b-col cols=2>
                    <div style="display: flex">
                      <label class="top-label" style="margin-top:7px">PCR: </label>
                      <input style="width: 20px; height: 20px; margin-top:7px; margin-left: 5px" type="checkbox" v-model="row.item.pcr">
                    </div>
                  </b-col>
                </b-row>
                <b-row>
                  <b-col cols=12 style="text-align: right;">
                    <b-button :disabled="disableEditItem(row.item)" size="sm" variant="link" @click="deleteItem(row.item)">Delete Item</b-button>
                  </b-col>
                </b-row>
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
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite";
import moment from "moment";
import httpUtils from "../httpUtils";

export default {
  components: {
    UploadFile: () => import("../directives/UploadFile"),
  },
  data() {
    return {
      securite: securite,
      modalVisible: false,
      locked: false,
      sale: {
        saleItems: [],
        customer: {},
        shippingAddress: {},
        freightTerms: {},
        paymentTerms: "",
        paidInFull: false,
      },
      customer: {
        addresses: []
      },
      item: {
        itemPackagings: []
      },
      shippingAddress: {},
      availableCustomers: [],
      availableItems: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item", label: "Item", sortable: false },
        { key: "packaging", label: "Package", sortable: false },
        { key: "itemPackaging.unitsOnStock", label: "Stock", sortable: false },
        { key: "units", label: "Sold", sortable: false },
        { key: "unitsAdjusted", label: "Adjusted", sortable: false },
        { key: "unitsAssigned", label: "Assigned", sortable: false },
        { key: "unitsSchedProd", label: "Prod", sortable: false },
        { key: "unitsShipped", label: "Ship", sortable: false },
        { key: "cases", label: "Cases", sortable: false },
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
      itemPackaging: {
        item: {},
      },
      pageFromCarton: 1,
      pageToCarton: 1,
      pageFromTag: 1,
      pageToTag: 1,
      availableStatus: [
        {id: 'DRAFT', name: 'Draft'},
        {id: 'READY', name: 'Ready'},
        {id: 'APPROVED', name: 'Approved'},
        {id: 'SCHEDULED', name: 'Scheduled'},
        {id: 'PRODUCED', name: 'Produced'},
        {id: 'ASSIGNED', name: 'Assigned'},
        {id: 'SHIPPED', name: 'Shipped'},
        {id: 'PAID', name: 'Paid'},
        {id: 'CANCELED', name: 'Canceled'},
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
            var casePack = si.itemPackaging.packaging.casePack?si.itemPackaging.packaging.casePack:0
              cases += +units/+casePack.toFixed(0);
          })
          return cases;
      },
      pcr(){
        return this.sale.saleItems.find(si=> si.pcr == false) == null;
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
    getCartonSource(si){
      var source = "MIMS";
      if(this.sale.customer.labelType == "UCC_128"){
        source = "EDI";
      }
      if(!this.sale.customer.cartonLabel){
        source = "None"
      }
      return source;
    },
    openActionMenu(saleItem){
      this.pageFromCarton = 1;
      this.pageToCarton = this.getCases(saleItem);
      this.pageFromTag = 1;
      this.pageToTag = this.getPallets(saleItem);
    },
    downloadTag(si){
      this.saveSale().then(r=> {
        if(!si.expiration){
          alert("Best By is not set");
          return false;
        }
        if(this.pageFromTag > this.getPallets(si) || this.pageToTag > this.getPallets(si)){
          alert("Page from/to are too large");
          return false;
        }
        var url = httpUtils.getUrl("/saleItem/" + si.id + "/tag/pdf", "&pageFrom="+this.pageFromTag+"&pageTo="+this.pageToTag);
        window.open(url, "_blank","")
      })
    },
    downloadCarton(si){
      this.saveSale().then(r=> {
        if(!si.expiration){
          alert("Best By is not set");
          return false;
        }
        if(this.pageFromCarton > this.getCases(si) || this.pageToCarton > this.getCases(si)){
          alert("Page from/to are too large");
          return false;
        }
        var url = httpUtils.getUrl("/saleItem/" + si.id + "/carton/pdf", "&pageFrom="+this.pageFromCarton+"&pageTo="+this.pageToCarton);
        window.open(url, "_blank","")
      })
    },
    getTotalUnitPrice(saleItem){
      saleItem.totalUnitPrice = saleItem.totalUnitPrice = (+saleItem.unitPrice * +saleItem.units);
      return saleItem.totalUnitPrice.toLocaleString('en-US',{minimumFractionDigits: 2});
    },
    rowClass(item, type) {
      var klass = "";
      if(item.status == 'SHIPPED'){
        klass = 'fully-shipped'
      }
      if((+item.units + +item.unitsAdjusted) < +item.unitsShipped){
        klass = 'over-shipped';
      }
      return klass;
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
    disableEditSale(){
      return !securite.hasRole(["SALE_ADMIN"]) || this.sale.approved;
    },
    disableEditItem(si){
      return si.status == "SHIPPED" || this.sale.approved;
      return securite.hasRole(["SALE_ADMIN"]);
    },
    allowSave(){
      return securite.hasRole(["SALE_ADMIN"]) && !this.sale.cancelled;
    },
    allowApprove(){
      return !this.sale.approved && securite.hasRole(["SALE_ADMIN"]);
      // return securite.hasRole(["SALE_ADMIN"]);
    },
    goToScheduled(si) {
      router.push("/itemSheduleEventList/" + si.itemPackaging.item.id + "/sale/" + this.sale.id);
    },
    goToShipment(si){
      var query = { saleId: this.sale.id };
      if(si){
        query.itemId = si.item.id;
      }
      router.push({path: "/shipmentList", query: query})
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
        response.data.saleItems.forEach(si => {
          si.prevUnitsAssigned = si.unitsAssigned;
          si.unitsOnStock = 0;
        })
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
      if(!this.pcr){
        alert("Production Compliance is Not Ready!");
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
      this.sale.totalPrice = this.totalPrice;
      if(!this.sale.shippingAddress || !this.sale.shippingAddress.id){
        this.sale.shippingAddress = null;
      }
      this.sale.pcr = this.pcr;
      return http.post("/sale", this.sale).then(r => {
        this.getSaleData(r.data.id);
        return r;
      }).catch(e => {
        console.log("API error: " + e);
      });
      return r;
    },
    copySale() {
      this.saveSale().then(r => {
        http.post("/sale/"+r.data.id+"/duplicate").then(r => {
          router.push("/saleEdit/"+r.data.id);
      }).catch(e => {console.log("API error: " + e);});
      })
    },
    validate(){
      if(!this.sale.number || !this.sale.customer.id){
        alert("Number, Customer required");
        return false;
      }
      var tooManyAssignedItem = null;
      var tooManyShippedItem = null;
      var alreadyShipped = false;
      this.sale.saleItems.forEach(si=>{
        if(this.sale.status == "SHIPPED"){
          alreadyShipped = true;
        }
        si.unitsAdjusted = si.unitsAdjusted || 0;
        si.units == si.units || 0;
        var unitsAssignedDiff = +si.unitsAssigned - +si.prevUnitsAssigned;
        if(si.unitsAssigned < 0 || ((+si.itemPackaging.unitsOnStock - +unitsAssignedDiff) < 0) || (si.unitsAssigned > (+si.units + +si.unitsAdjusted))){
          tooManyAssignedItem = si.itemPackaging.item.number;
        }
        if(si.unitsShipped > (+si.units + si.unitsAdjusted)){
          tooManyShippedItem = si.itemPackaging.item.number;
        }
      })
      if(tooManyAssignedItem){
        alert("Item: "+tooManyAssignedItem+" - Units Assigned more that Stock or (Sold + Adjusted)");
        return false;
      }
      if(tooManyShippedItem){
        alert("Item: "+tooManyShippedItem+" - Units Shipped more that (Sold + Adjusted)");
        return false;
      }
      return true;
    },
    cancelSale(){
      if(this.sale.unitsShipped > 0){
        alert("This sale was already shipped! Cannot cancel.");
        return;
      }
      this.sale.cancelled = !this.sale.cancelled;
      if(this.sale.cancelled){
        this.sale.saleItems.forEach(si => {
          si.unitsAssigned = 0;
        })
      }
      this.saveSale();
    },
    deleteSale() {
      if(this.sale.unitsShipped > 0){
        alert("This sale was already shipped! Cannot cancel.");
        return;
      }
      // if(this.sale.saleItems.length > 0 ){
      //   alert("There are existing items. Please, move or delete items first");
      //   return;
      // }
      this.$bvModal.msgBoxConfirm("Are you sure you want to delete this Sale?").then(ok => {
        if(ok){
          this.sale.saleItems = [];
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
        this.showItemMenu = false;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    addItem() {
      if (!this.item.id) {
        return;
      }
      var item = this.sale.saleItems.find(si => si.itemPackaging.item.id == this.item.id);
      if (item) {
        return;
      }
      this.itemPackaging.item = {id: this.item.id, name: this.item.name, number: this.item.number, totalCost: this.item.totalCost};
      this.sale.saleItems.unshift({ 
          units: 0,
          unitPrice: 0.00,
          prevUnitsAssigned: 0,
          unitsAssigned: 0,
          itemPackaging: this.itemPackaging,
      });
      this.itemDto = {},
      this.itemPackaging = {}
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    deleteItem(si) {
      if(si.unitsShipped>0){
        alert("Make sure there is no Schedule or Shipment for this item!");
        return false;
      }
      var idx = this.sale.saleItems.findIndex(it => it.id == si.id);
      this.sale.saleItems.splice(idx, 1);
      // this.saveSale();
    },
    getCases(si){
      return Math.ceil(+si.units / +si.itemPackaging.packaging.casePack);
    },
    getPallets(si){
      var number = Math.ceil(+this.getCases(si) / (+si.itemPackaging.packaging.ti * +si.itemPackaging.packaging.hi))
      return number;
    },

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
.table th, .table td {
  padding-right: 0px !important;
  padding-left: 0px !important;
}
.fully-shipped {
  background-color: #b6e6c9;
}
.over-shipped {
  background-color: #cea0a0;
}
</style>
