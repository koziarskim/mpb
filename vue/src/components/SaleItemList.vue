<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px; font-size: 12px">
            <b-col cols=2>
              <input style="font-size: 12px" class="form-control" type="tel" v-model="numberName" @keyup.enter="getSaleItems()" placeholder="Sale"/>
            </b-col>
            <b-col cols=2>
              <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
            </b-col>
            <b-col cols=2>
              <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
            </b-col>
            <b-col cols=2>
              <b-select option-value="id" option-text="name" :list="availableStatus" v-model="statusKv" placeholder="Status"></b-select>
            </b-col>
            <b-col cols=1>
              <b-select option-value="id" option-text="name" :list="availableUnitsFilters" v-model="unitsFilter" placeholder="Units"></b-select>
            </b-col>
            <b-col cols=1>
              <label class="top-label">Show All</label><br/>
              <input type="checkbox" style="margin-left: 20px" v-model="showAll">
            </b-col>
            <b-col cols=1>
              <div style="display: flex">
                <b-button id="totalsMenu" size="sm" @click="toggleShowTotals()">Totals</b-button>
                <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
                  <div style="width: 300px; font-size: 16px">
                    <div>Total of {{pageable.totalElements}} rows</div>
                    <div>Total sold: {{totalSold}}</div>
                  </div>
                </b-popover>
              </div>
            </b-col>  
            <b-col cols=1>
                <div style="text-align: right;">
                <b-button type="submit" size="sm" variant="primary" @click="newShipment()">Ship ({{selectedSaleItemIds.length}})</b-button>
                </div>
            </b-col>
        </b-row>
        <b-table :items="saleItems" :fields="fields" no-local-sorting @sort-changed="sorted">
          <template v-slot:cell(number)="row">
              <b-link role="button" @click=goToSale(row.item.saleId)>{{row.item.saleNumber}}</b-link>
          </template>
          <template v-slot:cell(itemNumber)="row">
              <b-link role="button" @click=goToItem(row.item.itemId)>{{row.item.itemNumber}} ({{row.item.itemName}})</b-link>
          </template>
          <template v-slot:cell(unitsSoldAdj)="row">
              <span>{{+row.item.unitsSold + +row.item.unitsAdjusted}}</span>
          </template>
          <template v-slot:cell(unitsTrasfered)="row">
              <span>{{row.item.unitsTransferedTo}}-{{row.item.unitsTranferedFrom}}</span>
          </template>
          <template v-slot:cell(unitsStockShip)="row">
              <b-button size="sm" @click=goToShipment(row.item.itemId,row.item.saleId) variant="link">{{row.item.unitsOnStock}}/{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(unitsSchProd)="row">
              <span>{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</span>
          </template>
          <template v-slot:cell(status)="row">
              <span v-if="row.item.unitsSold == row.item.unitsScheduled">{{getStatus(row.item.status)}}</span>
              <b-button v-if="row.item.unitsSold != row.item.unitsScheduled" style="margin-left: -18px" size="sm" @click=openScheduleProductionModal(row.item) variant="link">{{getStatus(row.item.status)}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <input type="checkbox" v-model="selectedSaleItemIds" :value="row.item.id" @change="checkboxSelected(row.item)" :disabled="checkboxDisabled(row.item)">
          </template>
        </b-table>
        <div style="display: flex">
		      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
          <span style="margin-top: 5px">Total of {{pageable.totalElements}} Items Sold</span>
        </div>
        <div v-if="scheduleProductionModalVisible">
          <schedule-production-modal :saleItemId="this.saleItemId" :itemId="this.itemId" v-on:close="closeScheduleProductionModal"></schedule-production-modal>
        </div>  
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import navigation from "../utils/navigation";

export default {
  name: "SaleItemList",
	components: {
	  ScheduleProductionModal: () => import("./modals/ScheduleProductionModal")
  },  
  data() {
    return {
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'id', sortDesc: false},
      searchSale: "",
      searchItem: "",
      selectedSaleItemIds: [],
      saleItems: [], //SaleItemDto
      saleItemId: null,
      itemId: null,
      numberName: "",
      availableItems: [],
      itemKv: {},
      availableCustomers: [],
      customer: {},
      selectedCustomerId: null,
      fields: [
        { key: "number", label: "Sale #", sortable: false },
        { key: "itemNumber", label: "Item", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "dc", label: "DC (State)", sortable: false },
        { key: "unitsSoldAdj", label: "Sold", sortable: false },
        { key: "unitsSchProd", label: "Sch/Prod", sortable: false },
        { key: "unitsTrasfered", label: "Transf", sortable: false },
        { key: "unitsStockShip", label: "Stock/Ship", sortable: false },
        // { key: "unitsReadyProd", label: "RFP", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      sales: [],
      availableStatus: [
        {id: 'DRAFT', name: 'Draft'},
        {id: 'PENDING_APPROVAL', name: 'Pending Approval'},
        {id: 'APPROVED', name: 'Pending Schedule'},
        {id: 'PENDING_PROD', name: 'Pending Prod'},
        {id: 'PENDING_SHIPMENT', name: 'Pending Shipment'},
        {id: 'SHIPPED', name: 'Fully Shipped'},
        {id: 'INVOICED_FULL', name: 'Fully Invoiced'},
        {id: 'CANCELLED', name: 'Cancelled'},
        {id: 'PAID', name: 'Paid In Full'},
      ],
      statusKv: {},
      availableUnitsFilters: [
        {id: "ON_STOCK", name: "On Stock"},
        {id: "RFP_ONLY", name: "RFP Only"}
      ],
      unitsFilter: {},
      showTotalsMenu: false,
      totalSold: 0,
      showAll: false,
      scheduleProductionModalVisible: false,
    };
  },
  watch: {
    itemKv(newValue, oldValue){
      this.getSaleItems();
    },
    customer(newValue, oldValue){
      this.getSaleItems();
    },
    statusKv(new_value, old_value){
      if(new_value.id == "PAID" || new_value.id == "CANCELLED"){
        this.showAll = true;
      }
      this.getSaleItems();      
    },
    unitsFilter(new_value, old_value){
      this.getSaleItems();      
    },
    showAll(new_value, old_value){
      this.getSaleItems();      
    },
  },
  methods: {
    openScheduleProductionModal(saleItemDto){
      this.saleItemId = saleItemDto.id;
      this.itemId = saleItemDto.itemId;
      this.scheduleProductionModalVisible = true;
    },
    closeScheduleProductionModal(si){
      this.scheduleProductionModalVisible = false;
      this.getSaleItems();
    },
    toggleShowTotals(){
      this.getSaleItems(true);
      this.showTotalsMenu = !this.showTotalsMenu;
    },    
    getStatus(statusId){
      var statusKv = this.availableStatus.find(stat => stat.id == statusId)
      return statusKv.name
    },
    checkboxSelected(saleItem){
      if(!this.selectedCustomerId){
        this.selectedCustomerId = saleItem.customerId;
      }
      if(this.selectedSaleItemIds.length==0){
        this.selectedCustomerId = null;
      }
    },
    checkboxDisabled(saleItem){
      return this.selectedCustomerId && !this.selectedSaleItemIds.includes(saleItem.id) && this.selectedCustomerId != saleItem.customerId;
    },
	sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getSaleItems();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSaleItems();
    },
  getSaleItems(totals){
    http.get("/saleItem/pageable", {params: {
        pageable: this.pageable,  
        totals: totals, 
        numberName: this.numberName, 
        customerId: this.customer.id, 
        itemId: this.itemKv.id, 
        status: this.statusKv.id,
        unitsFilter: this.unitsFilter.id,
        showAll: this.showAll
      }}).then(r => {
      if(totals){
        this.totalSold = r.data.content[0][0].toLocaleString();
      }else{
        this.saleItems = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  getAvailableCustomers() {
    http.get("/customer/kv").then(r => {
      this.availableCustomers = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  getAvailableItems() {
    http.get("/item/kv").then(r => {
      this.availableItems = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  newShipment(){
    var query = { saleItemIds: this.selectedSaleItemIds.join(",") };
    router.push({ path: "/shipmentEdit/new", query: query })
  },
  goToShipment(itemId, saleId){
    var query = { itemId: itemId, saleId: saleId };
    router.push({path: "/shipmentList", query: query})
  },
    gotToItem(itemId){
      router.push("/itemEdit/"+itemId);
    },
    goToSale(saleId){
        if(!saleId){
            http
            .post("/sale")
            .then(response =>{
                router.push('/saleEdit/'+response.data.id);
            })
            .catch(e =>{
                console.log("API Error: "+e);
            })
        }else{
            router.push('/saleEdit/'+saleId);
        }
    },
  },
  mounted() {
    //  this.getSaleItems();
     this.getAvailableCustomers();
     this.getAvailableItems();
  },
  activated(){
    this.getSaleItems();
  }
};
</script>

<style>
.table td {
   text-align: left;   
}
.table th {
   text-align: left;   
}
</style>
