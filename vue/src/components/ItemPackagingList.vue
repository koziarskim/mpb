<template>
  <b-container fluid>
    <div class="mpb-page-info">Item > Packages by Item</div>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availablePackagings" v-model="packagingKv" placeholder="Package"></b-select>
      </b-col>
    </b-row>
    <b-table :items="itemPackagings" :fields="fields">
      <template v-slot:cell(itemNameNumber)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToItem(row.item.itemId)">{{row.item.itemNumber}}</b-link> {{row.item.itemName}}</div>
      </template>
      <template v-slot:cell(label)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToPackaging(row.item.packagingId)">{{row.item.label}}</b-link></div>
      </template>
      <template v-slot:cell(caseDimension)="row">
        {{row.item.caseLength}}x{{row.item.caseWidth}}x{{row.item.caseHeight}}
      </template>
      <template v-slot:cell(palletConfig)="row">
        {{row.item.ti}}x{{row.item.hi}}
      </template>
      <template v-slot:cell(warehouseCost)="row">
        ${{row.item.warehouseCost}}
      </template>
      <template v-slot:cell(packageCost)="row">
        ${{row.item.packageCost}}
      </template>
      <template v-slot:cell(unitsScheduled)="row">
        <div style="display: flex">
          <b-button size="sm" variant="link" @click="goToScheduleEventList(row.item)">{{+row.item.unitsScheduled - +row.item.unitsProduced}}</b-button>
          <b-button size="sm" style="margin-left:-15px" variant="link" @click="openScheduleEventModal(row.item)">+</b-button>
        </div>
      </template>
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>
    <div v-if="scheduleEventModalVisible">
      <schedule-event-modal :saleItemId="this.saleItemId" :itemId="this.itemId" v-on:close="closeScheduleEventModal"></schedule-event-modal>
    </div>  
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ItemPackagingList",
	components: {
	  ScheduleEventModal: () => import("./modals/ScheduleEventModal")
  },  
  data() {
    return {
      pageable: {
        totalElements: 100,
        currentPage: 1,
        perPage: 25,
        sortBy: "updated",
        sortDesc: true
      },
      fields: [
        { key: "itemNameNumber", sortable: false, label: "Item" },
        { key: "label", sortable: false, label: "Packaging" },
        { key: "caseDimension", label: "Case Dimension", sortable: false },
        { key: "casePack", label: "C/P", sortable: false },
        { key: "palletConfig", label: "TixHi", sortable: false },
        { key: "caseWeight", label: "Case Weight", sortable: false },
        { key: "palletWeight", label: "Pallet Weight", sortable: false },
        { key: "warehouseCost", label: "Warehouse", sortable: false },
        { key: "packageCost", label: "Package", sortable: false },
        { key: "unitsOnFloor", label: "Floor", sortable: false },
        { key: "unitsOnStock", label: "Stock", sortable: false },
        { key: "unitsScheduled", label: "Pen Prod", sortable: false },
      ],
      itemPackagings: [], //ItemPackagingListDto
      availableItems: [],
      itemKv: {},
      availablePackagings: [],
      packagingKv: {},
      scheduleEventModalVisible: false,
      itemId: null,
      saleItemId: null,
    };
  },
  watch: {
    itemKv(newValue, oldValue) {
      this.getItemPackagings();
      this.getAvailablePackagings();
    },
    packagingKv(newValue, oldValue) {
      this.getItemPackagings();
    },
  },
  methods: {
    goToScheduleEventList(itemPackagingListDto) {
      router.push({path: "/ScheduleEventList", query: {
        itemId: itemPackagingListDto.itemId,
        packagingId: itemPackagingListDto.packagingId}});
    },
    openScheduleEventModal(itemPackagingListDto){
      this.itemId = itemPackagingListDto.itemId;
      this.scheduleEventModalVisible = true;
    },
    closeScheduleEventModal(){
      this.scheduleEventModalVisible = false;
      this.getItemPackagings();
    },
    paginationChange(page) {
      this.pageable.currentPage = page;
      this.getItemPackagings();
    },    
    getItemPackagings() {
      http.get("/itemPackaging/pageable", {
          params: {
            pageable: this.pageable,
            itemId: this.itemKv.id,
            packagingId: this.packagingKv.id,
          }
        }).then(response => {
          this.itemPackagings = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        });
    },
    getAvailableItems(){
      http.get("/item/kv").then(r => {
        this.availableItems = r.data;
      });
    },
    getAvailablePackagings(){
      http.get("/packaging/kv", {params: {itemId: this.itemKv.id}}).then(r => {
        this.availablePackagings = r.data;
      });
    },
    goToItem(itemId) {
      router.push("/itemEdit/" + itemId);
    },
    goToPackaging(packagingId) {
      router.push("/packagingEdit/" + packagingId);
    },
  },
  mounted() {
    this.getAvailableItems();
    this.getAvailablePackagings();
  },
  activated(){
    this.getItemPackagings();
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
