<template>
  <b-container fluid>
    <b-row style="padding-bottom: 4px; font-size: 12px">
      <b-col cols=2>
        <input class="form-control" style="font-size: 12px" type="tel" v-model="numberName" @keyup.enter="getItems()" placeholder="Number or Name" />
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv" placeholder="Component"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableBrands" v-model="brandKv" placeholder="Brand"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableCategories" v-model="categoryKv" placeholder="Category"></b-select>
      </b-col>
      <b-col cols=2>
        <b-select option-value="id" option-text="name" :list="availableUnitFilters" v-model="unitsFilter" placeholder="Units"></b-select>
      </b-col>
      <b-col>
        <div style="text-align: right;">
          <b-button size="sm" variant="primary" @click="createNewItem('')">New</b-button>
        </div>
      </b-col>
    </b-row>
    <b-table :items="items" :fields="fields" no-local-sorting @sort-changed="sorted">
      <template v-slot:head(unitsOnStock)="row">
        <div>Stock</div><div class="mpb-head-line">Including Overstock</div>
      </template>
      <template v-slot:head(openSales)="row">
        <div>Sales</div><div class="mpb-head-line">Open only</div>
      </template>
      <template v-slot:cell(name)="row">
        <div style="width:200px; overflow: wrap; font-size: 14px"><b-link role="button" @click="updateItem(row.item.id)">{{row.item.number}}</b-link> {{row.item.name}}</div>
      </template>
      <template v-slot:cell(openSales)="row">
        <b-button size="sm" variant="link" @click="goToItemSaleList(row.item.id)">{{row.item.unitsOpenSale}}</b-button>
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
      numberName: "",
      fields: [
        { key: "name", sortable: true, label: "Item # (Name)" },
        { key: "brand", sortable: true, label: "Brand" },
        { key: "category", sortable: true, label: "Category" },
        { key: "unitsOnStock", sortable: false, label: "Stock" },
        { key: "openSales", sortable: false, label: "Sales" },
      ],
      items: [], //ItemListDto
      availableComponents: [],
      componentKv: {},
      availableBrands: [],
      brandKv: {},
      availableCategories: [],
      categoryKv: {},
      availableUnitFilters: [
        {id: "ON_STOCK", name: "On Stock"},
        {id: "OPEN_SALES", name: "Open Sales"},
        {id: "RFP", name: "RFP"}
      ],
      unitsFilter: {},
      itemDto: {
      },
    };
  },
  watch: {
    componentKv(newValue, oldValue) {
      this.getItems();
    },
    brandKv(newValue, oldValue) {
      this.getItems();
    },
    categoryKv(newValue, oldValue) {
      this.getItems();
    },
    unitsFilter(newValue, oldValue) {
      this.getItems();
    }
  },
  methods: {
    getUnits(itemId){
      http.get("/item/"+itemId+"/dto").then(r => {
        r.data.unitsAdjusted = r.data.unitsAdjusted < 0 ? r.data.unitsAdjusted: '+'+r.data.unitsAdjusted;
        this.itemDto = r.data;
      }).catch(e => {console.log("API error: "+ e)})
    },
    sorted(e) {
      if (!e.sortBy) {
        return;
      }
      this.pageable.sortBy = e.sortBy;
      this.pageable.sortDesc = e.sortDesc;
      this.getItems();
    },
    paginationChange(page) {
      this.pageable.currentPage = page;
      this.getItems();
    },
    getItems() {
      http.get("/item/pageable", {
          params: {
            pageable: this.pageable,
            numberName: this.numberName,
            componentId: this.componentKv.id,
            brandId: this.brandKv.id,
            categoryId: this.categoryKv.id,
            unitsFilter: this.unitsFilter.id
          }
        }).then(response => {
          //ItemListDto
          this.items = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableComponents(){
      http.get("component/kv").then(r => {
        this.availableComponents = r.data;
      }).catch(e => {console.log("API error: "+ e)})
    },
    getAvailableBrands(){
      http.get("brand/kv").then(r => {
        this.availableBrands = r.data;
      }).catch(e => {console.log("API error: "+ e)})
    },
    getAvailableCategories(){
      http.get("category/item/kv").then(r => {
        this.availableCategories = r.data;
      }).catch(e => {console.log("API error: "+ e)})
    },
    createNewItem() {
      router.push("/itemEdit");
    },
    updateItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    gotToInventory(item_id) {
      router.push("/itemComponentList/" + item_id);
    },
    goToItemSaleList(item_id) {
      router.push("/itemSaleList/" + item_id);
    },
    goToItemScheduleList(item_id) {
      router.push("/scheduleEventList/" + item_id);
    },
    goToItemShippedList(itemId) {
      var query = { itemId: itemId };
      router.push({ path: "/shipmentList", query: query });
    }
  },
  mounted() {
    this.getAvailableComponents();
    this.getAvailableCategories();
    this.getAvailableBrands();
  },
  activated(){
    this.getItems();
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
