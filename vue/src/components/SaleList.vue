<template>
    <b-container fluid>
      <div class="mpb-page-info">Sale > Sale List</div>
      <b-row style="font-size: 12px">
          <input style="width: 150px; margin-left: 7px; font-size: 12px" class="form-control" type="tel" v-model="saleNumber" @keyup.enter="getSales()" placeholder="Sale"/>
          <b-select style="width: 200px; margin-left: 7px" option-value="id" option-text="name" :list="availableItems" v-model="itemKv" placeholder="Item"></b-select>
          <b-select style="width: 200px; margin-left: 7px" option-value="id" option-text="name" :list="availableCustomers" v-model="customerKv" placeholder="Customer"></b-select>
          <b-select style="width: 150px; margin-left: 7px" option-value="id" option-text="name" :list="availableStatus" v-model="statusKv" placeholder="Status"></b-select>
          <b-select style="width: 150px; margin-left: 7px" option-value="id" option-text="name" :list="availableCustomFilters" v-model="customFilterKv" placeholder="Custom Filter"></b-select>
          <div style="margin-left: 7px">
            <label class="top-label">Show All</label><br/>
            <input type="checkbox" style="margin-left: 20px" v-model="showAll">
          </div>
          <div style="margin-left: 7px">
            <b-button id="totalsMenu" size="sm" @click="toggleTotals()">Totals</b-button>
            <b-popover :show="showTotalsMenu" placement="bottom" target="totalsMenu" variant="secondary">
              <div style="width: 300px; font-size: 16px">
                <div>Total of {{pageable.totalElements}} rows</div>
                <div>Total Sold & Adj: {{totalSold}}</div>
                <div>Total amount: ${{totalAmount.toLocaleString('en-US',{minimumFractionDigits: 2})}}</div>
              </div>
            </b-popover>
          </div>
          <div style="margin-left:3px">
            <b-button id="sortMenu" size="sm">Sort</b-button>
            <b-popover :show="showSortMenu" @click="showSortMenu = !showSortMenu" placement="bottom" target="sortMenu" variant="secondary">
              <div style="width: 300px; font-size: 16px">
                <b-button variant="link" @click="toggleSortTotals('shippingFrom', false)">Ship latest first</b-button><br/>
                <b-button variant="link" @click="toggleSortTotals('shippingFrom', true)">Ship earliest first</b-button>
              </div>
            </b-popover>
          </div>
          <div>
            <b-button style="margin-left:3px" type="submit" variant="primary" size="sm" @click="goToSale('')">New</b-button>
            <b-button style="margin-left: 3px" type="submit" variant="primary" size="sm" @click="exportXls()">Export</b-button>
            <b-dropdown style="width:50px; margin-left:3px" right size="sm" :text="selectedSales.length.toString()">
              <b-dropdown-item-button @click="setFullyPaid()">Set Fully Paid</b-dropdown-item-button>
            </b-dropdown>
          </div>
      </b-row>
      <b-table :items="sales" :fields="fields" no-local-sorting @sort-changed="sorted">
        <!-- <template v-slot:head(action)="row">
          <div style="display: flex; width: 20px; margin-left: -25px">
            <b-button size="sm" @click="triggerAll(false)" variant="link">(-)</b-button><b-button size="sm" @click="triggerAll(true)" variant="link">(+)</b-button>
          </div>
        </template> -->
        <template v-slot:cell(number)="row">
          <b-button size="sm" @click="goToSale(row.item.id)" variant="link">{{row.item.number}}</b-button>
        </template>
        <template v-slot:cell(dc)="row">
          <div style="width:150px; overflow: wrap; font-size: 12px">{{row.item.dc}}</div>
        </template>
        <template v-slot:cell(unitsSoldAdj)="row">
            <span>{{+row.item.unitsSold + +row.item.unitsAdjusted}}</span>
        </template>
        <template v-slot:cell(unitsSchProd)="row">
            <span>{{row.item.unitsScheduled}}/{{row.item.unitsProduced}}</span>
        </template>
        <template v-slot:cell(unitsShipped)="row">
            <b-button size="sm" @click=goToShipment(row.item.id) variant="link">{{row.item.unitsShipped}}</b-button>
        </template>
        <template v-slot:cell(status)="row">
            <b :class="getStatusClass(row.item.status)">{{getStatus(row.item.status)}}</b>
        </template>
        <template v-slot:cell(action)="row">
          <input type="checkbox" v-model="selectedSales" @input="toggleSaleSelected($event, row.item)" :value="row.item">
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
  name: "SaleList",
  data() {
    return {
      securite: securite,
      navigation: navigation,
      pageable: {totalElements: 100, currentPage: 1, perPage: 25, sortBy: 'shippingFrom', sortDesc: false},
      saleNumber: null,
      availableItems: [],
      itemKv: {},
      availableCustomers: [],
      customerKv: {},
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
      availableCustomFilters: [
        {id: 'NOT_PAID', name: 'Not Paid'},
      ],
      customFilterKv: {},
      fields: [
        { key: "number", label: "Sale #", sortable: false },
        { key: "customerName", label: "Customer", sortable: true },
        { key: "dc", label: "DC (State)", sortable: false },
        { key: "shippingWindow", label: "Ship", sortable: false },
        { key: "unitsSoldAdj", label: "Sold&Adj", sortable: false },
        { key: "unitsSchProd", label: "Sch/Prod", sortable: false },
        { key: "unitsAssigned", label: "Assigned", sortable: false },
        { key: "unitsShipped", label: "Shipped", sortable: false },
        { key: "status", label: "Status", sortable: false },
        { key: "action", label: "", sortable: false}
      ],
      sales: [], //SaleListDto
      selectedSales: [],
      showTotalsMenu: false,
      showSortMenu: false,
      totalSold: 0,
      totalAmount: 0,
      showAll: false
    };
  },
  watch: {
    itemKv(new_value, old_value){
      this.getSales();      
    },
    customerKv(new_value, old_value){
      this.getSales();      
    },
    statusKv(new_value, old_value){
      if(new_value.id == "PAID" || new_value.id == "CANCELLED"){
        this.showAll = true;
      }
      this.getSales();      
    },
    customFilterKv(new_value, old_value){
      this.getSales();      
    },
    showAll(new_value, old_value){
      this.getSales();      
    },
  },
  methods: {
    toggleSaleSelected(e, sale){
      if(sale.status != "SHIPPED"){
        e.target.checked = false;
      }
    },
    setFullyPaid(){
      if(this.selectedSales.length == 0){
        return;
      }
      this.$bvModal.msgBoxConfirm("Are you sure you want to update all Sales as Paid?").then(ok => {
        if(ok){
          var saleIds = [];
          this.selectedSales.forEach(sale=> {
            saleIds.push(sale.id);
          })
          http.put("/sale/paid", saleIds).then(r => {
            this.getSales();
            this.selectedSales = [];
          }).catch(e => {console.log("API error: "+e);});  
        }
      })
    },
    toggleTotals(){
      this.getSales(true);
      this.showTotalsMenu = !this.showTotalsMenu;
    },
    toggleSortTotals(sortBy, sortDesc){
      this.pageable.sortBy = sortBy;
      this.pageable.sortDesc = sortDesc;
      this.getSales();
      this.showSortMenu = false;
    },
    triggerAll(add){
      this.sales.forEach(sale => {
        if(add){
          var idx = this.selectedSales.findIndex(ss => ss.id == sale.id);
          if(idx == -1){
            this.selectedSales.push(sale);
          }
        }else{
          this.selectedSales = [];
        }
      })
    },
    getFilterParams(totals){
      var params = {
        pageable: this.pageable,
        totals: totals, 
        saleNumber: this.saleNumber, 
        itemId: this.itemKv.id,
        customerId: this.customerKv.id,
        status: this.statusKv.id,
        customFilter: this.customFilterKv.id,
        showAll: this.showAll
      }
      return params;
    },
    exportXls(){
      var pageable = this.pageable;
      pageable.currentPage = 1;
      pageable.perPage = pageable.totalElements;
      var params = this.getFilterParams(false);
      params.pageable = pageable;
      http.get("/sale/xls", { responseType: 'blob', params: params}).then(r => {
        const url = URL.createObjectURL(new Blob([r.data], { type: r.headers['content-type']}))
        const link = document.createElement('a')
        link.href = url
        link.setAttribute("download", r.headers['file-name'])
        document.body.appendChild(link)
        link.click()
        this.pageable.perPage = 25;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
    getStatusClass(statusId){
        if(statusId == 'APPROVED'){ return "status-black"}
        if(statusId == 'PENDING_PROD'){ return "status-blue"}
        if(statusId == 'PENDING_SHIPMENT'){ return "status-purple"}
        if(statusId == 'SHIPPED'){ return "status-green"}
        return "status-red"
    },
    getStatus(statusId){
      var statusKv = this.availableStatus.find(stat => stat.id == statusId)
      return statusKv.name
    },
    showPopover(saleDto){
      this.getSale(saleDto.id).then(sale => {
        saleDto.saleItems = sale.saleItems;
      })
    },
	  sorted(e){
      // if(!e.sortBy){ return }
      // this.pageable.sortBy = e.sortBy;
      // this.pageable.sortDesc = e.sortDesc;
      this.getSales();
    },
    paginationChange(page){
        this.pageable.currentPage = page;
        this.getSales();
    },
	getSales(totals) {
    this.showTotalsMenu = false;
    var params = this.getFilterParams(totals);
    var query = {params: params}
    http.get("/sale/pageable", query).then(r => {
      if(totals){
        this.totalSold = r.data.content[0][0].toLocaleString();
        this.totalAmount = parseFloat(r.data.content[0][1]);
      }else{
        this.sales = r.data.content;
        this.pageable.totalElements = r.data.totalElements;
      }
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
    // this.getSales();
  },
  activated(){
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
