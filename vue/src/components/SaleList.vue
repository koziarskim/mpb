<template>
    <b-container fluid>
      <b-row style="padding-bottom: 4px; font-size: 12px">
        <b-col cols=2>
          <b-form-checkbox size="sm" v-model="itemView">Item View</b-form-checkbox>
        </b-col>
        <b-col cols=2 style="margin-left: -85px">
          <input class="form-control" style="font-size: 12px" type="tel" v-model="saleNumber" @keyup.enter="getSales()" placeholder="Sale"/>
        </b-col>
        <b-col cols="2">
          <b-select option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
        </b-col>
        <b-col cols="2">
          <b-select option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv" placeholder="Customer"></b-select>
        </b-col>
        <b-col cols="2">
          <b-select option-value="id" option-text="name" :list="availableStatus" v-model="statusKv" placeholder="Status"></b-select>
        </b-col>
        <b-col>
          <div style="text-align: right;">
          <b-button type="submit" variant="primary" size="sm" @click="goToSale('')">New S.O.</b-button>
          </div>
        </b-col>
      </b-row>
      <b-table :items="sales" :fields="fields" no-local-sorting @sort-changed="sorted">
        <template v-slot:cell(number)="row">
            <b-link role="button" :id="'popover-button-variant'+row.item.id" @click="showPopover(row.item)">{{row.item.number}}</b-link>
            <b-popover placement="bottomright" :target="'popover-button-variant'+row.item.id" triggers="focus" variant="primary">
              <template v-slot:title>
                <b-button size="sm" @click="goToSale(row.item.id)" variant="link">View/Edit Details</b-button>
              </template>
              <div v-for="si in row.item.saleItems" :key="si.id">
                <div>{{si.item.number}} - {{si.item.name}}, Sold: {{si.units}}, Produced: {{si.unitsProduced}}, Price: ${{si.unitPrice}}</div>
              </div>
            </b-popover>
        </template>
        <template v-slot:cell(unitsSoldAdj)="row">
            <span>{{+row.item.unitsSold + +row.item.unitsAdjusted}}</span>
        </template>
        <template v-slot:cell(unitsSchProd)="row">
            <span>{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</span>
        </template>
        <template v-slot:cell(unitsTransfered)="row">
            <span>{{row.item.unitsTransferedTo}}-{{row.item.unitsTransferedFrom}}</span>
        </template>
        <template v-slot:cell(unitsStockShip)="row">
            <b-button size="sm" @click=goToShipment(row.item.id) variant="link">{{row.item.unitsOnStock}}/{{row.item.unitsShipped}}</b-button>
        </template>
        <template v-slot:cell(status)="row">
            <span>{{getStatus(row.item.status)}}</span>
        </template>
        <template v-slot:cell(action)="row">
            <b-button size="sm" @click.stop="deleteSale(row.item)">x</b-button>
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
import securite from "../securite"
import navigation from "../utils/navigation";

export default {
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 7, sortBy: 'updated', sortDesc: true},
      saleNumber: null,
      availableItems: [],
      itemKv: {},
      availableCustomers: [],
      customerKv: {},
      availableStatus: [
        {id: 'PENDING_APPROVAL', name: 'Pending Approval'},
        {id: 'APPROVED', name: 'Approved'},
        {id: 'PENDING_PROD', name: 'Pending Prod'},
        {id: 'PENDING_SHIPPMENT', name: 'Pending Shippment'},
        {id: 'SHIPPED', name: 'Fully Shipped'}
      ],
      statusKv: {},
      itemView: false,
      fields: [
        { key: "number", label: "Sale #", sortable: false },
        { key: "customerName", label: "Customer", sortable: false },
        { key: "dc", label: "DC (State)", sortable: false },
        { key: "date", label: "Date", sortable: false },
        { key: "unitsSoldAdj", label: "Sold", sortable: false },
        { key: "unitsSchProd", label: "Sch/Prod", sortable: false },
        { key: "unitsTransfered", label: "Transf", sortable: false },
        { key: "unitsStockShip", label: "Stock/Ship", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      sales: [] //SaleListDto
    };
  },
  watch: {
    itemKv(old_value, new_value){
      this.getSales();      
    },
    customerKv(old_value, new_value){
      this.getSales();      
    },
    itemView(newValue, oldValue){
      if(newValue==true){
        navigation.goTo("/saleItemList/")
      }
    },
    statusKv(old_value, new_value){
      this.getSales();      
    },
  },
  methods: {
    getStatus(statusId){
      var statusKv = this.availableStatus.find(stat => stat.id == statusId)
      return statusKv.name
    },
    showPopover(saleDto){
      // this.sales.forEach(sale => sale.show = false)
      this.getSale(saleDto.id).then(sale => {
        saleDto.saleItems = sale.saleItems;
      })
    },
	  sorted(e){
      if(!e.sortBy){ return }
      this.pageable.sortBy = e.sortBy;
      this.pageable.sortDesc = e.sortDesc;
      this.getSales();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSales();
    },
	getSales() {
    var query = {params: {
      pageable: this.pageable, 
      saleNumber: this.saleNumber, 
      itemId: this.itemKv.id,
      customerId: this.customerKv.id,
      status: this.statusKv.id 
    }}
    http.get("/sale/pageable", query).then(r => {
      this.sales = r.data.content;
      this.pageable.totalElements = r.data.totalElements;
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
	getAvailableCustomers() {
    http.get("/customer/kv").then(r => {
      this.availableCustomers = r.data;
    }).catch(e => {
      console.log("API error: "+e);
    });
  },
    getSale(saleId){
      return http.get("/sale/"+saleId).then(r => {
        return r.data;        
      });  
    },
    getItem(component_id){
        var component;
        var found = this.sales.some(function(element) {
           if(element.id === component_id){
                component = element;
           }
        });
        return component;
    },
    deleteSale(sale) {
      if(!securite.hasRole(["ADMIN"])){
        alert("Don't have permission to delete sale");
        return;
      }
      if(sale.unitsTransferedTo != 0 || sale.unitsTransferedFrom != 0){
        alert("There are Transfered Sale(s). Please, remove any transfers.");
        return;
      }
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete this Sale? '+
      'This will also delete all Schedules and Productions associated with this Sale').then(ok => {
        if(ok){
          http.delete("/sale/"+sale.id).then(response => {
            this.getSales();
          }).catch(e => {
            console.log("API Error: "+e);
          });
            }
        })
    },
    goToSale(id){
        if(!id){
            router.push('/saleEdit');
        }else{
            router.push('/saleEdit/'+id);
        }
    },
    goToShipment(saleId){
      var query = {saleId: saleId };
      router.push({path: "/shipmentList", query: query})
    },
  },
  mounted() {
    this.getAvailableItems();
    this.getAvailableCustomers();
    this.getSales();
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
