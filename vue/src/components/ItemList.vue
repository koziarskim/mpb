<template>
    <b-container fluid>
        <b-row style="padding-bottom: 4px;">
            <b-col cols="2">
                <span style="text-align: left; font-size: 18px; font-weight: bold">Items</span>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="searchItem" @click="searchComponent = ''" @keyup.enter="getItems('item')" placeholder="Search Number, Name, Brand or Category"/>
            </b-col>
            <b-col cols="3">
                <input class="form-control" type="tel" v-model="searchComponent" @click="searchItem = ''" @keyup.enter="getItems('component')" placeholder="Search Component Number or Name"/>
            </b-col>
            <b-col>
                <div style="text-align: right;">
                <b-button type="submit" variant="primary" @click="createNewItem('')">New Item</b-button>
                </div>
            </b-col>
        </b-row>
        <b-table :items="items" :fields="fields" no-local-sorting @sort-changed="sorted">
          <template v-slot:cell(name)="row">
              <b-button size="sm" variant="link" @click.stop="updateItem(row.item.id)">{{row.item.number}} ({{row.item.name}})</b-button>
          </template>
          <template v-slot:cell(unitsSold)="row">
              <b-button size="sm" variant="link" @click.stop="goToItemSaleList(row.item.id)">{{row.item.unitsSold}}</b-button>
          </template>
          <template v-slot:cell(unitsScheduled)="row">
              <b-button size="sm" variant="link" @click.stop="goToItemScheduleList(row.item.id)">{{row.item.unitsScheduled}} / {{row.item.unitsProduced}}</b-button>
          </template>
          <template v-slot:cell(unitsShipped)="row">
              <b-button size="sm" variant="link" @click.stop="goToItemShippedList(row.item.id)">{{row.item.unitsShipped}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="gotToInventory(row.item.id)">view</b-button>
              <b-button size="sm" style="margin-left: 3px" @click.stop="deleteItem(row.item.id)">x</b-button>
          </template>
        </b-table>
        <b-pagination v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'name', sortDesc: false},
      searchItem: "",
      searchComponent: "",
      fields: [
        { key: 'name', sortable: true, label: 'Item # (Name)'},
        { key: 'brand', sortable: true, label: 'Brand'},
        { key: 'category', sortable: true, label: 'Category'},
        { key: 'unitsOnStock', sortable: false, label: 'Stock'},
        { key: 'unitsSold', sortable: false, label: 'Sold'},
        { key: 'unitsScheduled', sortable: false, label: 'Sched/Produced'},
        { key: 'unitsShipped', sortable: false, label: 'Shipped'},
        { key: 'action', sortable: false, label: 'Inventory'},
      ],
      items: [] //ItemListDto
    };
  },
  methods: {
    sorted(e){
        if(!e.sortBy){ return }
        this.pageable.sortBy = e.sortBy;
        this.pageable.sortDesc = e.sortDesc;
        this.getItems();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getItems();
    },
    getItems(type) {
      var searchKey = type=="item"?this.searchItem:this.searchComponent;
      http
        .get("/item/pageable", {params: {pageable: this.pageable, searchKey: searchKey, searchType: type}})
        .then(response => {
          //ItemListDto
          this.items = response.data.content;
          this.pageable.totalElements = response.data.totalElements;
        })
        .catch(e => {
          console.log("API error: "+e);
        });
    },
    deleteItem(item_id) {
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete Item?').then(ok => {
        if(ok){
          http.delete("/item/"+item_id).then(response => {
              this.getItems();
            }).catch(e => {
              console.log("Error post");
            });
          }
      })
    },
    createNewItem(){
        http
        .post("/item")
        .then(response => {
            router.push('/itemEdit/'+response.data.id);
        })
        .catch(e =>{
            console.log("Error post" + e)
        })
    },
    updateItem(item_id){
        router.push('./itemEdit/'+item_id);
    },
    gotToInventory(item_id){
        router.push('./itemComponentList/'+item_id);
    },
    goToItemSaleList(item_id){
        router.push('./itemSaleList/'+item_id);
    },
    goToItemScheduleList(item_id){
        router.push('./scheduleEventList/'+item_id);
    },
    goToItemShippedList(item_id){
        // router.push('./scheduleEventList/'+item_id);
    }
  },
  mounted() {
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
