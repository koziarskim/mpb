<template>
    <b-container fluid>
        <b-row style="font-size: 12px">
          <input style="width: 150px; margin-left: 15px; font-size: 12px" class="form-control" type="tel" v-model="numberName" @keyup.enter="getSaleItems()" placeholder="Sale"/>
          <b-select style="width: 200px; margin-left: 15px" option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availablePackagings" v-model="packagingKv" placeholder="Package"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableCustomers" v-model="customer" placeholder="Customer"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableStatus" v-model="statusKv" placeholder="Status"></b-select>
          <b-select style="width: 150px; margin-left: 15px" option-value="id" option-text="name" :list="availableUnitsFilters" v-model="unitsFilter" placeholder="Units"></b-select>
          <div style="margin-left: 15px">
            <label class="top-label">Show All</label><br/>
            <input type="checkbox" style="margin-left: 20px" v-model="showAll">
          </div>
          <div style="margin-left: 15px">
            <b-button id="totalsMenu" size="sm" @click="toggleShowTotals()">Totals</b-button>
            <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
              <div style="width: 300px; font-size: 16px">
                <div>Total of {{pageable.totalElements}} rows</div>
                <div>Sold & Adj: {{totalSoldAdj.toLocaleString()}}</div>
                <div>Produced: {{totalProduced.toLocaleString()}}</div>
                <div>Assigned: {{totalAssigned.toLocaleString()}}</div>
                <div>Not Assigned: {{(+totalSoldAdj - +totalAssigned).toLocaleString()}}</div>
                <div>Shipped: {{totalShipped.toLocaleString()}}</div>
                <div>Pending Ship: {{(+totalSoldAdj - +totalShipped).toLocaleString()}}</div>
              </div>
            </b-popover>
            <b-button style="margin-left: 15px" size="sm" variant="primary" @click="newShipment()">Ship ({{selectedSaleItemIds.length}})</b-button>
          </div>
        </b-row>
        <b-table :items="saleItems" :fields="fields" no-local-sorting @sort-changed="sorted">
          <template v-slot:cell(number)="row">
              <b-link role="button" @click=goToSale(row.item.saleId)>{{row.item.saleNumber}}</b-link>
          </template>
          <template v-slot:cell(itemNumber)="row">
            <div><b-link role="button" @click="goToItem(row.item.itemId)">{{row.item.itemNumber}}</b-link><span style="width:200px; overflow: wrap; font-size: 12px"> {{row.item.itemName}}</span></div>
          </template>
          <template v-slot:cell(packagingLabel)="row">
            <div style="width:150px; overflow: wrap; font-size: 12px">{{row.item.packagingLabel}}</div>
          </template>
          <template v-slot:cell(unitsSoldAdj)="row">
              <span>{{+row.item.unitsSold + +row.item.unitsAdjusted}}</span>
          </template>
          <template v-slot:cell(unitsShipped)="row">
              <b-button size="sm" @click=goToShipment(row.item.itemId,row.item.saleId) variant="link">{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(unitsSchProd)="row">
            <b-button size="sm" variant="link" @click="goToScheduleEventList(row.item)">{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(status)="row">
              <span v-if="row.item.status != 'APPROVED'">{{getStatus(row.item.status)}}</span>
              <b-button v-if="row.item.status == 'APPROVED'" style="margin-left: -18px" size="sm" @click=openScheduleEventModal(row.item) variant="link">{{getStatus(row.item.status)}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <input type="checkbox" v-model="selectedSaleItemIds" :value="row.item.id" @change="checkboxSelected(row.item)" :disabled="checkboxDisabled(row.item)">
          </template>
        </b-table>
        <div style="display: flex">
		      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
          <span style="margin-top: 5px">Total of {{pageable.totalElements}} Items Sold</span>
        </div>
        <div v-if="scheduleEventModalVisible">
          <schedule-event-modal :saleItemId="this.saleItemId" :itemId="this.itemId" v-on:close="closeScheduleEventModal"></schedule-event-modal>
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
	  ScheduleEventModal: () => import("./modals/ScheduleEventModal")
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
      availablePackagings: [],
      packagingKv: {},
      availableCustomers: [],
      customer: {},
      selectedCustomerId: null,
      fields: [
        { key: "number", label: "Sale #", sortable: false },
        { key: "itemNumber", label: "Item", sortable: false },
        { key: "packagingLabel", label: "Packaging", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "unitsSoldAdj", label: "Sold&Adj", sortable: false },
        { key: "unitsSchProd", label: "Sch/Prod", sortable: false },
        { key: "unitsAssigned", label: "Assigned", sortable: false },
        { key: "unitsShipped", label: "Ship", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      sales: [],
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
      statusKv: {},
      availableUnitsFilters: [
        {id: "PENDING_APPROVAL", name: "Pending Approval"},
        {id: "PENDING_SCHEDULE", name: "Pending Schedule"},
        {id: "PENDING_PRODUCTION", name: "Pending Production"},
        {id: "PENDING_ASSIGNMENT", name: "Pending Assignment"},
        {id: "PENDING_SHIPMENT", name: "Pending Shipment"},
        {id: "PENDING_PAYMENT", name: "Pending Payment"},
      ],
      unitsFilter: {},
      showTotalsMenu: false,
      totalSoldAdj: 0,
      totalProduced: 0,
      totalAssigned: 0,
      totalShipped: 0,
      showAll: false,
      scheduleEventModalVisible: false,
    };
  },
  watch: {
    itemKv(newValue, oldValue){
      this.getAvailablePackagings();
      this.getSaleItems();
    },
    packagingKv(newValue, oldValue){
      this.getSaleItems();
    },
    customer(newValue, oldValue){
      this.getSaleItems();
    },
    statusKv(new_value, old_value){
      if(new_value.id == "PAID" || new_value.id == "CANCELED"){
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
    openScheduleEventModal(saleItemDto){
      this.saleItemId = saleItemDto.id;
      this.itemId = saleItemDto.itemId;
      this.scheduleEventModalVisible = true;
    },
    closeScheduleEventModal(){
      this.scheduleEventModalVisible = false;
      this.getSaleItems();
    },
    toggleShowTotals(){
      this.getSaleItems(true);
      this.showTotalsMenu = !this.showTotalsMenu;
    },    
    getStatus(statusId){
      var statusKv = this.availableStatus.find(stat => stat.id == statusId)
      if(!statusKv){
        console.log("Status not found: " +status)
      }
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
        packagingId: this.packagingKv.id,
        status: this.statusKv.id,
        unitsFilter: this.unitsFilter.id,
        showAll: this.showAll
      }}).then(r => {
      if(totals){
        this.totalSoldAdj = r.data.content[0][0];
        this.totalProduced = r.data.content[0][1];
        this.totalAssigned = r.data.content[0][2];
        this.totalShipped = r.data.content[0][3];
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
  getAvailablePackagings() {
    http.get("/packaging/kv", {params: {itemId: this.itemKv.id}}).then(r => {
      this.availablePackagings = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
  newShipment(){
    var query = { saleItemIds: this.selectedSaleItemIds.join(",") };
    router.push({ path: "/shipmentEdit/new", query: query })
  },
    goToScheduleEventList(saleItemDto) {
      router.push({path: "/ScheduleEventList", query: {itemId: saleItemDto.itemId, saleId: saleItemDto.saleId}});
    },
  goToShipment(itemId, saleId){
    var query = { itemId: itemId, saleId: saleId };
    router.push({path: "/shipmentList", query: query})
  },
    gotToItem(itemId){
      router.push("/itemEdit/"+itemId);
    },
    goToSale(saleId){
      router.push('/saleEdit/'+saleId);
    },
    goToItem(itemId){
      router.push('/itemEdit/'+itemId);
    },
  },
  mounted() {
    var itemId = this.$route.query.itemId;
    var packagingId = this.$route.query.packagingId;
    var statusId = this.$route.query.statusId;
    if(itemId){
      this.itemKv = {id: itemId};
    }
    if(packagingId){
      this.packagingKv = {id: packagingId};
    }
    if(statusId){
      this.statusKv = {id: statusId}
    }
    // window.history.replaceState({}, document.title, window.location.pathname);
    // this.$router.push(this.$route.path)
    // router.replace({'query': null});
    this.getAvailableCustomers();
    this.getAvailableItems();
    this.getAvailablePackagings();
  },
  activated(){
    this.getSaleItems();
  },
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
