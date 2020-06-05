<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
          <label class="top-label">Shipment #:</label>
          <input class="form-control" type="tel" v-model="shipment.number">
      </b-col>
      <b-col cols=3>
        <label class="top-label">Shipment Name:</label>
        <input class="form-control" type="tel" v-model="shipment.name">
      </b-col>      
      <b-col cols=2>
        <label class="top-label">Customer:</label>
        <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Select Customer"></b-select>
      </b-col>
      <b-col cols=1>
        <label class="top-label">Est. Cost:</label>
        <input class="form-control" type="tel" v-model="shipment.estimatedCost">
      </b-col>
      <b-col cols=1>
        <label class="top-label">Invoiced Cost:</label>
        <input class="form-control" type="tel" v-model="shipment.invoicedCost">
      </b-col>
      <b-col cols=2>
        <div style="display:flex; margin-left: 75px">
          <upload-file v-if="shipment.id" v-on:header-click="openPdf" v-on:close="closeUpload" :entity-id="shipment.id" header-text="Bill of Lading/Packing Slip (PDF)" type="Shipment" :attachments="shipment.attachments"></upload-file>
          <b-button :disabled="!allowEdit()" :title="getSaveTitle(shipment)" size="sm" style="margin-left: 5px" variant="success" @click="saveShipment()">Save</b-button>
          <b-button style="margin-left: 3px" :disabled="!allowEdit()" size="sm" @click="deleteShipment()">x</b-button>
        </div>
        <div style="display: flex; margin-left: 85px; margin-top: 7px">
            <label class="top-label">Ready To Ship</label>
            <input type="checkbox" style="margin-left: 3px; margin-top: 3px" :disabled="shippedDate?true:false" v-model="shipment.ready">
          </div>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Freight Terms:</label>
        <b-select option-value="id" option-text="name" :list="availableFreightTerms" v-model="shipment.freightTerms" placeholder="Pick Freight"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Load Number:</label>
        <input class="form-control" type="tel" v-model="shipment.loadNumber">
      </b-col>      
       <b-col cols=2>
        <label class="top-label">Via:</label>
        <input class="form-control" type="tel" v-model="shipment.via">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Expected Date:</label>
        <input class="form-control" type="date" v-model="shipment.shippingDate">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Time:</label>
        <input class="form-control" type="time" v-model="shipment.shippingTime">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipped (Actual Load Sent):</label>
        <input @change="changeShippedDate" class="form-control" type="date" v-model="shippedDate">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=3>
        <label class="top-label">Ship To:</label>
        <b-select option-value="id" option-text="name" :list="availableShippingAddresses" v-model="shippingAddress" placeholder="Pick shipping address"></b-select>
      </b-col>
      <b-col cols=3>
        <label class="top-label">Freight Address:<span style="cursor: pointer; color: blue" @click="openModal()"> (Edit/New) </span></label>
        <b-select option-value="id" option-text="name" :list="availableFreightAddresses" v-model="freightAddress" placeholder="Pick freight address"></b-select>
      </b-col>
     <b-col cols=2>
        <label class="top-label">Shipping Window From:</label>
        <input class="form-control" type="date" v-model="shipment.shippingFrom">
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shipping Window To:</label>
        <input class="form-control" type="date" v-model="shipment.shippingTo">
      </b-col>
      <b-col cols=2>
        <label class="top-label">FOB:</label>
        <input class="form-control" type="tel" v-model="shipment.fob">
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=2>
        <label class="top-label">NMFC:</label>
        <input class="form-control" type="tel" v-model="shipment.freightNmfc">
        <label class="top-label">Freight Class:</label>
        <input class="form-control" type="tel" v-model="shipment.freightClass">
      </b-col>
      <b-col cols=4>
        <label class="top-label">Comments (Excluded from BOL):</label>
        <b-form-textarea type="text" :rows="4" v-model="shipment.comments"></b-form-textarea>
      </b-col>
      <b-col cols=6>
        <label class="top-label">Special Notes (Included on BOL):</label>
        <b-form-textarea type="text" :rows="4" v-model="shipment.notes"></b-form-textarea>
      </b-col>
    </b-row>
    <br/>
    <b-row>
      <b-col cols=2>
        <b-button size="sm" variant="primary" @click="openSaleItemPicker()">Pick Sales to Add</b-button>
      </b-col>
      <b-col style="margin-top: 7px">
          <b>Total units:</b>{{unitsShipped}}
      </b-col>
      <b-col style="margin-top: 7px">
          <b>Total cases:</b>{{totalCases}}
      </b-col>
      <b-col>
        <div style="display: flex">
          <b style="margin-top: 7px">Total pallets:</b><input class="form-control" style="width: 60px" type="tel" v-model="totalPalletsCustom" @input="overwrite=true">
        </div>
      </b-col>
      <b-col>    
        <div style="display: flex">
          <b style="margin-top: 7px">Total weight:</b><input class="form-control" style="width: 80px" type="tel" v-model="totalWeightCustom" @input="overwrite=true">
        </div>
      </b-col>
      <b-col cols=2>
        <span style="visibility: hidden">{{totalPallets}}</span>
        <span style="visibility: hidden">{{totalWeight}}</span><br/>
      </b-col>
    </b-row>
    <div style="border: 1px solid #d6d3d3; margin-top: 10px;">
      <b-row>
        <b-col>
          <b-table v-if="shipment.shipmentItems.length>0" :sort-by.sync="sortBy" :sort-desc.sync="sortDesc" :items="shipment.shipmentItems" :fields="columns">
            <template v-slot:cell(item)="row">
              <b-link role="button" @click.stop="goToItem(row.item.saleItem.item.id)">{{row.item.saleItem.item.number}}</b-link>
              <div class="name-md" :title="row.item.name"> {{row.item.saleItem.item.name}}</div>
            </template>
            <template v-slot:cell(sale)="row">
              <b-link @click.stop="goToSale(row.item.saleItem.sale.id)">{{row.item.saleItem.sale.number}}</b-link>
            </template>
            <template v-slot:cell(unitsOnStockRet)="row">
              <span>{{row.item.saleItem.unitsOnStock}} </span><b-link @click.stop="goToItemReturnList(row.item.saleItem)">({{row.item.saleItem.unitsReturned}})</b-link>
            </template>
            <template v-slot:cell(units)="row">
              <input class="form-control" style="width:100px" type="tel" v-model="row.item.units">
            </template>
            <template v-slot:cell(unitsSoldAdj)="row">
              <span>{{row.item.saleItem.units}} {{row.item.saleItem.unitsAdjusted >= 0?'+':''}}{{row.item.saleItem.unitsAdjusted}}</span>
            </template>
            <template v-slot:cell(unitsTransfered)="row">
              <b-button size="sm" variant="link" @click="openTransferModal(row.item.saleItem)">{{row.item.saleItem.unitsTransferedTo}}-{{row.item.saleItem.unitsTransferedFrom}}</b-button>
            </template>
            <template v-slot:cell(unitsSchedProd)="row">
              <span>{{row.item.saleItem.unitsScheduled}}/{{row.item.saleItem.unitsProduced}}</span>
            </template>
            <template v-slot:cell(cases)="row">
              <span>{{row.item.cases = Math.ceil(+row.item.units / +row.item.saleItem.item.casePack)}}</span>
            </template>
            <template v-slot:cell(pallets)="row">
              <span>{{row.item.pallets = getNumberOfPallets(row.item)}}</span>
            </template>
            <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="removeSaleItem(row.item.saleItem.id)">x</b-button>
            </template>
          </b-table>
        </b-col>
      </b-row>
    </div>
    <div v-if="modalVisible">
			<address-modal :address-id="freightAddress.id" address-type="FRG" v-on:closeModal="closeModal"></address-modal>
		</div>
    <div v-if="saleItemPickerVisible">
			<sale-item-picker :customer-id="customer.id" :added-sale-item-ids="getAddedSaleItemsIds()" v-on:closeModal="closeSaleItemPicker"></sale-item-picker>
		</div>
    <div v-if="transferModalVisible">
			<transfer-modal :read-only="true" :sale-item-to="saleItemTransfer" v-on:saveModal="saveTransferModal"></transfer-modal>
		</div>    
  </b-container>
</template>

<script>
import http from "../http-common";
import httpUtils from "../httpUtils";
import router from "../router";
import moment from "moment";
import securite from "../securite";

export default {
  components: {
    AddressModal: () => import("./AddressModal"),
    SaleItemPicker: () => import("./SaleItemPicker"),
    UploadFile: () => import("../directives/UploadFile"),
    TransferModal: () => import("./TransferModal"),
  },
  data() {
    return {
      securite: securite,
      transferModalVisible: false,
      saleItemTransfer: {},
      overwrite: false,
      totalPalletsCustom: 0,
      totalWeightCustom: 0,
      selectionVisible: false,
      saleItemPickerVisible: false,
      selected: [],
      modalVisible: false,
      shipment: {
        number: 0, 
        shipmentItems: [],
        shippingDate: null,
        fob: "Elk Grove Village",
        shippingTime: moment.utc().hours("08").minutes("00").format("HH:mm"),
        attachments: [],
      },
      availableCustomers: [],
      availableShippingAddresses: [],
      availableFreightAddresses: [],
      freightAddress: {},
      customer: {},
      shippingAddresses: [],
      shippingAddress: {},
      availableSales: [],
      sale: {},
      availableSaleItems: [],
      saleItem: {},
      saleItems: [],
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item", label: "Item", sortable: false },
        { key: "sale", label: "Sale", sortable: false },
        { key: "unitsSoldAdj", label: "Sold (+/-Adj)", sortable: false },
        { key: "unitsOnStockRet", label: "Stock (R)", sortable: false },
        { key: "unitsSchedProd", label: "Sched/Prod", sortable: false },
        { key: "unitsTransfered", label: "Trans", sortable: false },
        { key: "saleItem.unitsShipped", label: "Shipped", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "saleItem.item.casePack", label: "C/P", sortable: false },
        { key: "cases", label: "Case", sortable: false },
        { key: "pallets", label: "Pallet", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      availableFreightTerms: [
        {id: "TPB", name: "TP Bill"},
        {id: "PRP", name: "Pre Paid"},
        {id: "TPO", name: "TP Bill Other"},
        {id: "COL", name: "Collect"},
        {id: "DEL", name: "Delivered"},
        {id: "CPU", name: "Customer Pickup"}
      ],
      itemText: "",
      shippedDate: null
    };
  },
  computed: {
    unitsShipped() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.units;
      });
      return total;
    },
    totalCases() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.cases;
      });
      return total;
    },
    totalPallets() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        total += +si.pallets;
      });
      if(!this.overwrite){
        this.totalPalletsCustom = total;
      }
      return total;
    },
    totalWeight() {
      var total = 0;
      this.shipment.shipmentItems.forEach(si => {
        var totalPaletWeight = +si.saleItem.item.palletWeight * +si.pallets
        total += (+si.saleItem.item.weight * +si.units) + +totalPaletWeight;
      });
      if(!this.overwrite){
        this.totalWeightCustom = total.toFixed();
      }
      return total.toFixed();
    }
  },
  watch: {
    customer(new_value, old_value) {
      this.shipment.payTerms = null;
      if (!new_value.id || new_value.id == old_value.id) {
        return;
      }
      this.getCustomer(new_value.id).then(c => {
        if(this.shipment.customer && this.shipment.customer.id != new_value.id){
          this.shipment.notes =  c.shipmentNotes;
        }
        this.shipment.freightTerms = c.freightTerms;
      })
      this.getAvailableSaleItems();
      this.getAvailableShippingAddresses(new_value.id)
    },
  },
  methods: {
    openTransferModal(saleItem){
      this.saleItemTransfer = saleItem;
      this.saleItemTransfer.saleNumber = saleItem.sale.number
      this.transferModalVisible = true;
    },    
    saveTransferModal(saleItem){
      this.saleItemTransfer = {},
      this.transferModalVisible = false;
    },
    changeShippedDate(e){
      if(!this.shipment.ready){
        alert("Shipment is not ready!");
        this.shippedDate = null;
        return;
      }
      if(e.target.value){
        this.readyDisabled = true;
      }else{
        this.readyDisabled = false;
        this.shipment.shippedDate = null;
      }
    },
    closeUpload(attachments){
      this.shipment.attachments = attachments;
      this.saveShipment();
    },
    deleteShipment() {
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(ok => {
        if(ok){
          http.delete("/shipment/" + this.shipment.id).then(() => {
            router.push("/shipmentList")
          }).catch(e => {
            console.log("API Error: " + e);
          });
        }
      })
    },
    allowEdit(){
      return securite.hasRole(["SHIPMENT_ADMIN"]) && !this.shipment.shippedDate;
    },
    getAddedSaleItemsIds(){
      var ids = [];
      this.shipment.shipmentItems.forEach(shipItem=> {
        ids.push(shipItem.saleItem.id);
      })
      return ids;
    },
    openSaleItemPicker(){
      this.saleItemPickerVisible = true;
    },
    closeSaleItemPicker(saleItemIds){
      if(!saleItemIds){
        this.saleItemPickerVisible = false;
        return;
      }
      http.get("/saleItem", {params: {ids: saleItemIds.join()}}).then(r => {
        r.data.forEach(saleItem => {
          this.addSaleItem(saleItem);
        })
        this.saleItemPickerVisible = false;
      }).catch(e => {
        console.log("API error: " + e); 
      })
    },
    goToItemReturnList(saleItem){
      var query = { saleId: saleItem.sale.id, itemId: saleItem.item.id };
      router.push({path: "/itemReturnList", query: query});
    },
    getNumberOfPallets(shipmentItem){
      var number = null;
      if(!this.overwrite){
        number = Math.ceil(+shipmentItem.cases / (+shipmentItem.saleItem.item.ti * +shipmentItem.saleItem.item.hi))
      }
      return number;
    },
    keyDown(event){
      this.itemText = event.target.value;
    },
    addSaleItemKv(saleItemIds){
      if(!saleItemIds){
        return;
      }
      http.get("/saleItem/"+saleItemIds).then(r => {
        this.addSaleItem(r.data);
        this.sale = {};
        this.saleItem = {};
        this.getAvailableSaleItems();
        this.$refs.bselect.$children[0].searchText = this.itemText
      }).catch(e => {
        console.log("API error: " + e);
      })
    },
    getSaveTitle(shipment){
      return "Created: "+moment(shipment.created).format("YYYY/MM/DD HH:mm")+"\n"+
        "Modified: "+moment(shipment.modifiedDate).format("YYYY/MM/DD HH:mm");
    },
    openModal(){
			this.modalVisible = true;
		},
		closeModal(address){
      if(address){
        this.shipment.freightAddress = address;
        this.freightAddress = address;
      }
      this.modalVisible=false;
      this.getAvailableFreightAddresses()
		},
    getCustomer(customerId) {
      return http.get("/customer/" + customerId).then(r => {
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
     getShipment(id) {
      http.get("/shipment/" + id).then(response => {
        response.data.shipmentItems.forEach(si => {
          si.existingUnits = si.units;
        });
        if (response.data.customer) {
          this.customer = response.data.customer;
        }
        if (response.data.shippingAddress) {
          this.shippingAddress = response.data.shippingAddress;
        }
        if (response.data.freightAddress){
          this.freightAddress = response.data.freightAddress;
        }
        if(response.data.totalPallets != response.data.totalPalletsCustom){
          this.overwrite = true;
        }
        if(response.data.totalWeight != response.data.totalWeightCustom){
          this.overwrite = true;
        }
        this.totalPalletsCustom = response.data.totalPalletsCustom;
        this.totalWeightCustom = response.data.totalWeightCustom;
        this.shipment = response.data;
        this.shippedDate = response.data.shippedDate;
      }).catch(e => { console.log("API error: " + e); });
    },
    validate(){
      if(!this.shipment.number){
        alert("Shipping Number required.")
        return false;
      }
      if(this.shipment.shippedDate){
        alert("Shipping was already shipped. No modification allowed");
        return false;
      }
      var overStock = false;
      this.shipment.shipmentItems.forEach(si=> {
        if(((+si.units - +si.prevUnits) > +si.saleItem.unitsOnStock)){
          overStock = true;
        }
      })
      if(overStock){
        alert("Cannot ship more that on stock")
        return false;
      }
      return true;
    },
    back(){
      window.history.back();
    },
    saveShipment() {
      if(!securite.validate(["SHIPMENT_ADMIN"]))return;
      if(!this.validate()){
        return;
      }
      this.shipment.customer = this.customer.id?this.customer:null;
      this.shipment.shippingAddress = this.shippingAddress.id?{ id: this.shippingAddress.id }:null;
      this.shipment.freightAddress = this.freightAddress.id?{ id: this.freightAddress.id }:null;
      this.shipment.unitsShipped = this.unitsShipped;
      this.shipment.totalCases = this.totalCases;
      this.shipment.totalPallets = this.totalPallets;
      this.shipment.totalWeight = this.totalWeight;
      this.shipment.totalPalletsCustom = this.totalPalletsCustom;
      this.shipment.totalWeightCustom = this.totalWeightCustom;
      this.shipment.shippedDate = this.shippedDate;
      return http.post("/shipment", this.shipment).then(r => {
        this.shipment = r.data;
        return r.data;
        this.getAvailableSaleItems();
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
    getSaleItems(saleItemIds){
      if(!saleItemIds){
        return;
      }
      return http.get("/saleItem", {params: {ids: saleItemIds}}).then(r => {
        this.customer = {id: r.data[0].sale.customer.id}
        r.data.forEach(saleItem => {
          this.addSaleItem(saleItem);
        })
      }).catch(e => {
        console.log("API error: " + e); 
      })
    },
    getAvailableCustomers() {
      return http
        .get("/customer/kv")
        .then(response => {
          this.availableCustomers = response.data;
        }).catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableFreightAddresses() {
      this.availableFreightAddresses = [];
      return http.get("/address/type/frg").then(r => {
        this.availableFreightAddresses = r.data;
        if(this.shipment.freightAddress){
          this.availableFreightAddresses.push({id: this.shipment.freightAddress.id, name: this.shipment.freightAddress.street});
        }
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableShippingAddresses(customer_id) {
      return http.get("/address/customer/"+customer_id).then(r => {
        this.availableShippingAddresses = r.data;
      })
    },
    getAvailableSaleItems() {
      if(!this.customer.id){
        return Promise.resolve();
      }
      return http
        .get("/saleItem/kv/customer/" + this.customer.id)
        .then(response => {
          this.availableSaleItems = [];
          response.data.forEach(si => {
            var found = this.shipment.shipmentItems.findIndex(shipItem =>  si.id == shipItem.saleItem.id);
            if(found == -1){
              this.availableSaleItems.push(si);
            }
          })
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getShipmentNumber() {
      if(this.shipment.number){
        return
      }
      return http.get("/shipment/number/"+moment.utc().local().format("YYYY-MM-DD")).then(r => {
        this.shipment.number = r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    addSaleItem(saleItem){
      this.shipment.shipmentItems.push(
        {
          shipment: {id: this.shipment.id},
          saleItem: saleItem,
          units: saleItem.unitsOnStock,
          cases: 0,
          pallets: 0
        }
      );
    },
    removeSaleItem(saleItemId) {
      var idx = this.shipment.shipmentItems.findIndex(shipItem => shipItem.saleItem.id == saleItemId);
      this.shipment.shipmentItems.splice(idx, 1)
      this.getAvailableSaleItems();
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
    openPdf(){
      if(this.allowEdit()){
        this.saveShipment().then(shipment => {
          this.shipment.id = shipment.id;
          var url = httpUtils.getUrl("/shipment/" + this.shipment.id + "/pdf");
          window.open(url, "_blank","")
        })
      }else{
        var url = httpUtils.getUrl("/shipment/" + this.shipment.id + "/pdf");
        window.open(url, "_blank","")
      }
    },
  },
  mounted() {
    var id = this.$route.params.shipment_id;
    var saleItemIds = this.$route.query.saleItemIds;
    window.history.replaceState({}, document.title, window.location.pathname);
    if(id!="new"){
      this.getShipment(id);
    }else{
      this.shipment.shippingDate = moment().format("YYYY-MM-DD");
      this.getShipmentNumber();
    }
    this.getAvailableCustomers();
    this.getAvailableFreightAddresses();
    this.getSaleItems(saleItemIds);
  }
};
</script>

<style>
</style>
