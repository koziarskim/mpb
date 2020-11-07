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
    </b-table>
    <div style="display: flex">
      <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
      <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
    </div>
  </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  name: "ItemList",
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
      ],
      itemPackagings: [], //ItemPackagingListDto
      availableItems: [],
      itemKv: {},
      availablePackagings: [],
      packagingKv: {},
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
