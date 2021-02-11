<template>
  <b-container fluid>
    <div class="mpb-page-info">Production > Schedule List</div>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Pick Item"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availablePackagings" v-model="packagingKv" placeholder="Pick Package"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Pick Sale"></b-select>
      </b-col>
      <b-col cols=1>
        <div style="margin-left: 15px">
          <b-button id="totalsMenu" size="sm" @click="toggleTotals()">Totals</b-button>
          <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
            <div style="width: 300px; font-size: 16px">
              <div>Total of {{pageable.totalElements}} rows</div>
              <div>Scheduled: {{totalScheduled.toLocaleString()}}</div>
              <div>Produced: {{totalProduced.toLocaleString()}}</div>
              <div>Pending Prod: {{(+totalScheduled - +totalProduced).toLocaleString()}}</div>
            </div>
          </b-popover>
        </div>
      </b-col>
    </b-row>
    <b-row>
      <b-table :items="scheduleEvents" :fields="columns">
        <template v-slot:cell(scheduleDate)="row">
          <span>{{row.item.scheduleDate | formatDate}}</span>
        </template>
        <template v-slot:cell(unitsProduced)="row">
          <b-button size="sm" @click="goToProduction(row.item)" variant="link">{{row.item.unitsProduced}}</b-button>
        </template>
        <template v-slot:cell(unitsScheduled)="row">
          <b-button size="sm" @click=openScheduleEventModal(row.item) variant="link">{{row.item.unitsScheduled}}</b-button>
        </template>
      </b-table>
      <div style="display: flex">
        <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
        <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
      </div>
    </b-row>
    <div v-if="scheduleEventModalVisible">
      <schedule-event-modal :scheduleEventId="this.scheduleEventId" :saleItemId="this.saleItemId" :itemId="this.itemId" v-on:close="closeScheduleEventModal"></schedule-event-modal>
    </div>  

  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ScheduleEventList",
	components: {
	  ScheduleEventModal: () => import("./modals/ScheduleEventModal")
  },  
  data() {
    return {
      scheduleEventModalVisible: false,
      scheduleEventId: null,
      saleItemId: null,
      itemId: null,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'updated', sortDesc: false},
      scheduleEvents: [], //ScheduleEventListDto
      availableSales: [],
      saleKv: {},
      availableItems: [],
      itemKv: {},
      availablePackagings: [],
      packagingKv: {},
      columns: [
        { key: "scheduleDate", label: "Date", sortable: false },
        { key: "lineId", label: "Line", sortable: false },
        { key: "itemNumber", label: "Item", sortable: false },
        { key: "packagingLabel", label: "Package", sortable: false },
        { key: "saleNumber", label: "Sale", sortable: false },
        { key: "startTime", label: "Started", sortable: false },
        { key: "finishTime", label: "Finished", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
        { key: "unitsProduced", label: "Produced", sortable: false },
        { key: "action", label: "", sortable: false },
      ],
      showTotalsMenu: false,
      totalSoldAdj: 0,
      totalScheduled: 0,
      totalProduced: 0,
      totalAssigned: 0,
      item: {},
    };
  },

  computed: {},
  watch: {
    itemKv(newValue, oldValue){
      this.getScheduleEvents();
      this.getAvailablePackagins();
    },
    packagingKv(newValue, oldValue){
      this.getScheduleEvents();
    },
    saleKv(newValue, oldValue){
      this.getScheduleEvents();
    }
  },
  methods: {
    toggleTotals(){
      this.getScheduleEvents(true);
      this.showTotalsMenu = !this.showTotalsMenu;
    },      
    openScheduleEventModal(se){
      this.scheduleEventId = se.id;
      this.saleItemId = se.saleItemId;
      this.itemId = se.itemId;
      this.scheduleEventModalVisible = true;
    },
    closeScheduleEventModal(){
      this.scheduleEventModalVisible = false;
      this.getScheduleEvents();
    },    
    goToProduction(se) {
      var query = { date: se.scheduleDate, seId: se.id };
      router.push({ path: "/productionLine/"+se.lineId, query: query } );
    },    
    paginationChange(page){
      this.pageable.currentPage = page;
      this.getScheduleEvents();
    },
    getScheduledClass(se){
      if(se.unitsScheduled > se.unitsProduced){
        return "schedule-red";
      }
      if(se.unitsScheduled < se.unitsProduced){
        return "schedule-yellow"
      }
      return "";
    },
    getEfficiencyStyle(eff){
      var style = "color: ";
      style += eff>=100?"#28a745":"#e25454";
      return style;
    },
    sortCompare(a, b, key) {
      if (key === 'sale') {
        return  a.saleItem.sale.number.localeCompare(b.saleItem.sale.number);
      }
    },
    editScheduleEvent(se){
      se.edit = true;
    },
    saveScheduleEvent(se){
      if(se.unitsScheduled > se.saleItem.units){
        alert("Cannot schedule more that sold");
        return;
      }
      if(se.unitsScheduled < se.unitsProduced){
        alert("Cannot schedule less than produced");
        return;
      }
      http.post("/scheduleEvent", se).then(response => {
        this.getScheduleEvents(this.item.id)
      });
      se.edit = false;
    },
    deleteDisabled(se){
      return se.unitsProduced > 0;
    },
    deleteScheduleEvent(se_id){
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(value => {
        if(value){
          http.delete("/scheduleEvent/" + se_id).then(response => {
            this.getScheduleEvents(this.item.id);
          });
        }
      })
    },
    close() {
        window.history.back();
    },
    getScheduleEvents(totals){
      this.showTotalsMenu = false;
      var query = {params: {
        pageable: this.pageable,
        totals: totals, 
        saleId: this.saleKv.id, 
        itemId: this.itemKv.id,
        packagingId: this.packagingKv.id,
      }}
      http.get("/scheduleEvent/pageable", query).then(r => {
        if(totals){
          this.totalScheduled = parseFloat(r.data.content[0][0]);
          this.totalProduced = parseFloat(r.data.content[0][1]);
        }else{
         this.scheduleEvents = r.data.content;
        }
        this.pageable.totalElements = r.data.totalElements;
      });
    },
    getAvailableItems() {
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      });
    },
    getAvailablePackagins() {
      http.get("/packaging/kv", {params: {itemId: this.itemKv.id}}).then(r => {
        this.availablePackagings = r.data;
      });
    },
    getAvailableSales() {
      http.get("/sale/kv").then(r => {
        r.data.unshift({id: 0, name: '----'})
        this.availableSales = r.data;
      });
    },
    goToSale(sale_id) {
      router.push("/saleEdit/" + sale_id);
    },
    goToItem(itemId){
      router.push("/itemEdit/"+itemId);
    },
    goToGraph() {
      router.push("/itemGraph/" + this.item.id);
    },
  },
  mounted() {
    var itemId = this.$route.query.itemId;
    var saleId = this.$route.query.saleId;
    var packagingId = this.$route.query.packagingId;
    if(itemId){
      this.itemKv = {id: itemId}
    }
    if(saleId != null){
      this.saleKv = {id: saleId}
    }
    if(packagingId){
      this.packagingKv = {id: packagingId}
    }
    this.getAvailableItems();
    this.getAvailablePackagins();
    this.getAvailableSales();
  },
  activated(){
    this.getScheduleEvents();
  }
};
</script>

<style>
.schedule-red {
  background-color: #ff00004d !important;
}
.schedule-yellow {
  background-color: #e4d171 !important;
}
</style>
