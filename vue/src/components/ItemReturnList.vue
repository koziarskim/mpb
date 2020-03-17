<template>
    <b-container fluid>
      <b-row style="padding-bottom: 4px; font-size: 12px">
        <b-col cols=2>
          <span style="text-align: left; font-size: 18px; font-weight: bold">Item Returns</span>
        </b-col>
        <b-col cols=3>
          <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
        </b-col>
        <b-col cols=3>
          <b-select option-value="id" option-text="name" :list="availableSales" v-model="saleKv" placeholder="Sale"></b-select>
        </b-col>
        <b-col>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" @click="openItemReturnModal()">New Return</b-button>
          </div>
        </b-col>
      </b-row>
        <b-table :items="itemReturns" :fields="fields" no-local-sorting>
          <template v-slot:cell(item)="row">
            <b-link role="button" @click="goToItem(row.item.item.id)">{{row.item.item.number}}</b-link>
            <span style="font-size: 11px"> ({{row.item.item.name}})</span>
          </template>
          <template v-slot:cell(unitsReturned)="row">
            <b-link v-if="row.item.unitsReceived>row.item.unitsReturned" role="button" @click="openItemReturnModal(row.item.id)"><div style="background-color:#dcb3b3; width: 50px">{{row.item.unitsReturned}}</div></b-link>
            <b-link v-if="row.item.unitsReceived<=row.item.unitsReturned" role="button" @click="openItemReturnModal(row.item.id)">{{row.item.unitsReturned}}</b-link>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteItemReturn(row.item.id)">x</b-button>
          </template>
        </b-table>
        <div style="display: flex">
          <b-pagination size="sm" v-model="pageable.currentPage" :per-page="pageable.perPage" :total-rows="pageable.totalElements" @change="paginationChange"></b-pagination>
          <span style="margin-top: 5px">Total of {{pageable.totalElements}} rows</span>
        </div>
        <div v-if="itemReturnModalVisible">
  		  	<item-return-modal :item-return-id="itemReturnId" :item-id="itemId" v-on:closeModal="closeItemReturnModal"></item-return-modal>
	    	</div>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";

export default {
  components: {
    ItemReturnModal: () => import("./ItemReturnModal")
  },
  data() {
    return {
      pageable: {totalElements: 100, currentPage: 1, perPage: 20, sortBy: 'updated', sortDesc: true},
      fields: [
        { key: 'item', sortable: true, label: 'Item # (Name)'},
        { key: 'unitsReceived', sortable: false, label: 'Received'},
        { key: 'unitsReturned', sortable: false, label: 'Assigned'},
        { key: 'action', sortable: false, label: ''},
      ],
      itemId: null,
      itemReturns: [],
      itemReturnModalVisible: false,
      itemReturnId: null,
      availableItems: [],
      itemKv: {},
      availableSales: [],
      saleKv: {},
    };
  },
  watch: {
    itemKv(oldValue, newValue){
      this.getItemReturns();
    },
    saleKv(oldValue, newValue){
      this.getItemReturns();
    }
  },
  methods: {
    openItemReturnModal(itemReturnId){
      if(!itemReturnId && !this.itemKv.id){
        alert("Please select Item for which you like to create Return");
        return;
      }
      this.itemReturnId = itemReturnId;
      if(itemReturnId){
        http.get("/itemReturn/"+itemReturnId).then(r=> {
          this.itemId = r.data.item.id;
          this.itemReturnModalVisible = true;
        }).catch(e => {
          console.log("API error: " + e)
        })
      }else{
        this.itemId = this.itemKv.id;
        this.itemReturnModalVisible = true;
      }
    },
    closeItemReturnModal(){
      this.itemReturnModalVisible = false;
      this.itemReturnId = null;
      this.getItemReturns();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getItemReturns();
    },
    getItemReturns() {
      var query = {params: {
        pageable: this.pageable, 
        itemId: this.itemKv.id,
        saleId: this.saleKv.id,
      }}
      http.get("/itemReturn", query).then(r=> {
        this.itemReturns = r.data;
        this.pageable.totalElements = r.data.totalElements;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    getAvailableItems() {
      http.get("/item/kv").then(r=> {
        this.availableItems = r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    getAvailableSales() {
      http.get("/sale/kv").then(r=> {
        this.availableSales = r.data;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    deleteItemReturn(itemReturnId) {
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete Return?').then(ok => {
        if(ok){
          http.delete("/itemReturn/"+itemReturnId).then(r => {
            this.getItemReturns();
          }).catch(e => {
            console.log("Error post");
          });
        }
      })
    },
    createItemReturn(){
      //TODO: ItemReturnModal
    },
    goToItem(itemId){
        router.push('/itemEdit/'+itemId);
    },
    goToSale(saleId){
        router.push('/saleEdit/'+saleId);
    },
  },
  mounted() {
    var itemId = this.$route.query.itemId;
    var saleId = this.$route.query.saleId;
    if(itemId){
      this.itemKv = {id: itemId};
    }
    if(saleId){
      this.saleKv = {id: saleId};
    }
    window.history.replaceState({}, document.title, window.location.pathname);
    this.getAvailableItems();
    this.getAvailableSales();
    
    this.getItemReturns();
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
