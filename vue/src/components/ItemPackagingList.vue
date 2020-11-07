<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availablePackagings" v-model="packagingKv" placeholder="Packaging"></b-select>
      </b-col>
    </b-row>
    <b-table :items="itemPackagings" :fields="fields">
      <template v-slot:cell(itemNameNumber)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToItem(row.item.itemId)">{{row.item.itemNumber}}</b-link> {{row.item.itemName}}</div>
      </template>
      <template v-slot:cell(name)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="goToPackaging(row.item.packagingId)">{{row.item.name}}</b-link></div>
      </template>
      <template v-slot:cell(caseDimension)="row">
        {{row.item.caseHeight}}x{{row.item.caseWidth}}x{{row.item.caseDepth}}
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
        <b-link role="button" @click="openScheduleProductionModal(row.item)">{{row.item.unitsScheduled}}</b-link>
      </template>
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>
    <div v-if="scheduleProductionModalVisible">
      <schedule-production-modal :saleItemId="this.saleItemId" :itemId="this.itemId" v-on:close="closeScheduleProductionModal"></schedule-production-modal>
    </div>  
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ItemPackagingList",
	components: {
	  ScheduleProductionModal: () => import("./modals/ScheduleProductionModal")
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
        { key: "itemNameNumber", sortable: true, label: "Item" },
        { key: "name", sortable: true, label: "Packaging" },
        { key: "typeLabel", label: "Type", sortable: false },
        { key: "caseDimension", label: "Case Dimension", sortable: false },
        { key: "casePack", label: "C/P", sortable: false },
        { key: "palletConfig", label: "TixHi", sortable: false },
        { key: "palletWeight", label: "Weight", sortable: false },
        { key: "warehouseCost", label: "Warehouse", sortable: false },
        { key: "packageCost", label: "Package", sortable: false },
        { key: "unitsOnStock", label: "Stock", sortable: false },
        { key: "unitsScheduled", label: "Scheduled", sortable: false },
      ],
      itemPackagings: [], //ItemPackagingListDto
      availableItems: [],
      itemKv: {},
      availablePackagings: [],
      packagingKv: {},
      scheduleProductionModalVisible: false,
      itemId: null,
      saleItemId: null,
    };
  },
  watch: {
    itemKv(newValue, oldValue) {
      this.getItemPackagings();
    },
    packagingKv(newValue, oldValue) {
      this.getItemPackagings();
    },
  },
  methods: {
    openScheduleProductionModal(itemPackagingListDto){
      this.itemId = itemPackagingListDto.itemId;
      this.scheduleProductionModalVisible = true;
    },
    closeScheduleProductionModal(){
      this.scheduleProductionModalVisible = false;
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
        }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableItems(){
      http.get("item/kv").then(r => {
        this.availableItems = r.data;
      }).catch(e => {console.log("API error: "+ e)})
    },
    getAvailablePackagings(){
      http.get("packaging/kv").then(r => {
        this.availablePackagings = r.data;
      }).catch(e => {console.log("API error: "+ e)})
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
