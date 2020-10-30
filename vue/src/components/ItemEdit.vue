<template>
  <b-container fluid>
        <b-row>
          <b-col cols=2>
            <label class="top-label">Item Number:</label>
            <input class="form-control" type="text" v-model="item.number">
          </b-col>
          <b-col cols=4>
            <label class="top-label">Item Name:</label>
            <input class="form-control" type="text" v-model="item.name">
          </b-col>
          <b-col cols=2>
            <label class="top-label">UPC Number:</label>
            <b-select option-value="id" style="width: 165px" option-text="name" :list="availableUpc" v-model="upc" placeholder="UPC"></b-select>
          </b-col>
          <b-col cols=2 style="margin-top:3px">
            <upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
          </b-col>
          <b-col cols=2 style="margin-top: 10px">
            <div style="text-align: right">
              <b-button :disabled="!allowEdit()" size="sm" variant="success" @click="saveItem()">Save</b-button>
              <b-button :disabled="!allowEdit()" style="margin-left: 3px" size="sm" @click="deleteItem()">x</b-button>
            </div>
            <label class="top-label">Stock: {{item.unitsOnStock}}</label><br/>
            <label class="top-label">Sch/Pro: <b-link role="button" @click="goToItemScheduleList()">{{item.unitsScheduled}}/{{item.unitsProduced}}</b-link></label><br/>
            <label class="top-label">Sold: <b-link role="button" @click="goToItemSaleList()">{{item.unitsSold}}</b-link>&nbsp;</label><label class="top-label" :class="getReturnClass()"><b-link role="button" @click="goToItemReturnList()">Ret: {{item.unitsReturned}}</b-link></label><br/>
            <label class="top-label">Shipped: <b-link role="button" @click="goToItemShippedList()">{{item.unitsShipped}}</b-link></label>
          </b-col>
        </b-row>
        <b-row style="margin-top: -75px">
          <b-col cols=4>
            <label class="top-label">Notes:</label>
            <b-form-textarea type="text" :rows="4" v-model="item.description"></b-form-textarea>
          </b-col>
          <b-col cols=2>
            <label class="top-label">Brand:</label>
            <b-select option-value="id" option-text="name" :list="availableBrands" v-model="item.brand" placeholder="Brand"></b-select>
            <label class="top-label">Category:</label>
            <b-select option-value="id" option-text="value" :list="availableItemCategories" v-model="item.category" placeholder="Category"></b-select>
          </b-col>
          <!-- <b-col cols=2>
            <img style="margin-top: 27px; width: 165px; height: 40px" :src="upcUrl" fluid>
            <img style="margin-top: 5px; width: 165px; height: 64px" :src="caseUrl" fluid>
          </b-col> -->
          <b-col cols=2 style="margin-top: 70px;">
            <label class="top-label">Season:</label>
            <b-select option-value="id" option-text="name" :list="availableSeasons" v-model="item.season" placeholder="Season"></b-select>
          </b-col>
        </b-row>
        <b-row>
        </b-row>
    <b-row>
    </b-row>
    <b-row style="margin-top: 10px">
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <!-- Units Section -->
        <hr class="hr-text" data-content="Unit dimenstion">
        <b-row>
          <b-col cols=12>
            <label class="top-label">Item Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.height"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.width"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.depth">
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=5>
            <label class="top-label">Weight (lbs):</label>
            <input class="form-control" v-model="item.weight">
          </b-col>
          <b-col cols=5>
            <br/>
            <label class="top-label">Cubic (ft): {{itemCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <hr class="hr-text" data-content="Case dimenstion">
        <b-row>
          <b-col cols=12>
            <label class="top-label">Case Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.caseHeight"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.caseWidth"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.caseDepth">
            </div>
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=4>
            <label class="top-label">Case Pack:</label>
            <input class="form-control" v-model="item.casePack">
          </b-col>
          <b-col cols=8>
            <br/>
            <label class="top-label">Case weight: {{caseWeight}}</label><br/>
            <label class="top-label">Case cubic: {{caseCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3 style="border-right: 1px solid #c5c5c5">
        <hr class="hr-text" data-content="Pallet dimenstion">
        <b-row>
          <b-col cols=6>
            <label class="top-label">TI x HI (pcs):</label>
            <div style="display:flex">
              <input class="form-control" v-model="item.ti" placeholder="0"><span style="margin-top: 7px">x</span>
              <input class="form-control" v-model="item.hi" placeholder="0">
            </div>
          </b-col>
          <b-col cols=6>
            <label class="top-label">Pallet Weight:</label>
            <input class="form-control" v-model="item.palletWeight">
          </b-col>
        </b-row>
        <b-row>
          <b-col cols=6>
            <br/>
            <label class="top-label">Units p/ pallet: {{unitsPerPallet}}</label><br/>
            <label class="top-label">Pallet height: {{palletHeight}}</label>
          </b-col>
          <b-col cols=6>
            <br/>
            <label class="top-label">Cases p/p: {{casesPerPallet}}</label><br/>
            <label class="top-label">Pallet cubic: {{palletCubic}}</label>
          </b-col>
        </b-row>
      </b-col>
      <b-col cols=3>
        <hr class="hr-text" data-content="Prices are in USD">
        <b-row>
          <b-col cols=6>
            <br/>
            <label class="top-label">Warehouse ($): {{warehouseCost}}</label><br/>
            <label class="top-label">Package/mat. ($): {{packageCost}}</label>
          </b-col>
          <b-col cols=6>
            <br/>
            <label class="top-label">Total Cost: {{totalCost}}</label>
          </b-col>
        </b-row>
      </b-col>
    </b-row>
    <!-- Item Components -->
    <div style="border: 1px solid #d6d3d3; margin-top: 10px;">
      <b-row>
        <b-col cols=6>
          <label class="top-label">Components:</label>
          <b-select option-value="id" option-text="name" :list="availableComponents" v-model="componentKv"></b-select>
        </b-col>
        <b-col cols=1>
          <b-button size="sm" style="margin-top: 30px;" variant="primary" @click="addComponent()">Add &#x25BC;</b-button>
        </b-col>
        <b-col cols=4>
          <label class="top-label">Possible Packages:
            <span style="cursor: pointer; color: blue" @click="addPackaging()">(Add)</span>
            <span style="cursor: pointer; color: blue" @click="deletePackaging()">(Delete)</span>
        </label>
        <b-select option-value="label" option-text="label" :list="item.itemPackagings" v-model="itemPackaging"></b-select>
      </b-col>
      </b-row>
      <br>
      <b-row>
        <b-col>
          <b-table v-if="item.itemComponents.length>0" :items="item.itemComponents" :fields="columns" sort-by="component.category.name">
            <template v-slot:head(units)="row">
              <div>Assembly</div><div class="mpb-head-line">Units per item</div>
            </template>
            <template v-slot:cell(component)="row">
              <b-link role="button" @click="goToComponent(row.item.component.id)">{{row.item.component.number}}</b-link>
              <span style="font-size:11px"> {{row.item.component.name}}</span>
            </template>
            <template v-slot:cell(units)="row">
              <input class="form-control" style="width:70px" type="tel" v-model="row.item.units">
            </template>
            <template v-slot:cell(unitsOnStock)="row">
              <b-link role="button" @click="goToReceiving(row.item.component.id)">{{row.item.component.unitsOnStock}}</b-link>
            </template>
            <template v-slot:cell(action)="row">
              <b-button size="sm" @click.stop="removeItemComponent(row.item.id)">x</b-button>
            </template>
          </b-table>
        </b-col>
      </b-row>
    </div>
    <div v-if="packagingModalVisible">
      <packaging-modal :itemPackagings="item.itemPackagings" :item-name="item.number+' - '+item.name" v-on:close="closePackagingModal"></packaging-modal>
    </div>    
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import navigation from "../utils/navigation";
import securite from "../securite";

export default {
  components: {
    PackagingModal: () => import("./modals/PackagingModal"),
  },
  data() {
    return {
      navigation: navigation,
      availableComp: [],
      query: "",
      selectedComp: null,
      item: {
        itemComponents: [],
        brand: {},
        category: {},
        season: {},
        upc:{},
        // caseUpc: {},
        palletWeight: 60,
        itemPackagings: []
      },
      image: "",
      httpUtils: httpUtils,
      componentKv: {},
      availableBrands: [],
      availableComponents: [],
      availableItemCategories: [],
      availableCompCategories: [],
    availableSeasons: [],
    availableUpc: [],
    upc: {},
    // caseUpc: {},
    // upcUrl: "",
    // caseUrl: "",
    uploadedFile: null,
    columns: [
        { key: "component", label: "Component", sortable: false },
        { key: "component.category.name", label: "Category", sortable: false },
        { key: "units", label: "Units", sortable: false },
        { key: "component.totalLandedCost", label: "Cost", sortable: false },
        { key: "unitsOnStock", label: "Stock", sortable: false },
        { key: "component.unitsLocked", label: "Reserved", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
      packagingModalVisible: false,
      itemPackaging: {},

    };
  },
  computed: {
    totalCost() {
      var totalCost = 0;
      this.item.itemComponents.forEach(ic => {
        totalCost = +totalCost + +ic.component.totalLandedCost * +ic.units;
      });
      totalCost =
        +totalCost +
        +this.warehouseCost +
        +this.packageCost;
      return totalCost?totalCost.toFixed(2):0.00;
    },

    palletHeight() {
      return +this.item.caseHeight * +this.item.hi;
    },
    itemCubic() {
      return (
        (+this.item.height * +this.item.width * +this.item.depth) /
        1728
      ).toFixed(2);
    },
    caseCubic() {
      return (
        (+this.item.caseHeight * +this.item.caseWidth * +this.item.caseDepth) /
        1728
      ).toFixed(2);
    },
    palletCubic() {
      return (+this.item.hi * +this.item.ti * +this.caseCubic).toFixed(2);
    },
    unitsPerPallet() {
      return +this.item.hi * +this.item.ti * +this.item.casePack;
    },
    casesPerPallet() {
      return +this.item.hi * +this.item.ti;
    },
    caseWeight() {
      return (+this.item.casePack * +this.item.weight).toFixed(2);
    },
    warehouseCost() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
    packageCost() {
      var cost = 12 / +this.unitsPerPallet;
      return cost.toFixed(2);
    },
  },
  watch: {
    // upc(newValue, oldValue){
      // this.setUpcUrl(newValue.id);
      // this.setCaseUrl(newValue.id);
    // },
  },
  methods: {
    addPackaging(){
      this.packagingModalVisible = true;
    },
    deletePackaging(){
      var idx = this.item.itemPackagings.findIndex(
        a => a.id == this.itemPackaging.id
      );
      if (idx > -1) {
        this.item.itemPackagings.splice(idx, 1);
      }
      this.itemPackaging = {};
    },
    closePackagingModal(packagings){
      this.item.itemPackagings = [];
      if (packagings) {
        packagings.forEach(p => {
          this.item.itemPackagings.push({item: {id: this.item.id}, packaging: {id: p.id}, label: p.name+' ('+p.type+')'});
        })
      }
      this.packagingModalVisible = false;
      this.packaging = {};
    },
    // setUpcUrl(upcId){
    //   if(upcId){
    //     this.upcUrl = httpUtils.getUrl("/upc/image/" + upcId);
    //   }else{
    //     this.upcUrl = "";
    //   }
    // },
    // setCaseUrl(upcId){
    //   if(upcId){
    //     this.caseUrl = httpUtils.getUrl("/upc/case/image/" + upcId);
    //   }else{
    //     this.caseUrl = "";
    //   }
    // },
    allowEdit(){
      return securite.hasRole(["STANDARD_ADMIN"]);
    },
    goToItemReturnList(){
      var query = { itemId: this.item.id };
      router.push({path: "/itemReturnList", query: query});
    },
    goToReceiving(componentId){
      var query = { component_id: componentId };
      router.push({ path: "/receivingList", query: query });
    },
    addComponent(){
      if(!this.componentKv){
        alert("Pick Component to Add");
        return;
      }
      this.getComponent(this.componentKv.id).then(c=> {
        this.item.itemComponents.push({
          units: 1,
          component: c
        })
        this.getAvailableComponents();
      })
    },
    getComponent(componentId){
      return http.get("/component/" + componentId).then(r => {
        return r.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    goToItemSaleList(){
        router.push('/itemSaleList/'+this.item.id);
    },
    goToItemScheduleList(){
        router.push('/scheduleEventList/'+this.item.id);
    },
    goToItemShippedList(){
      var query = { itemId: this.item.id};
      router.push({path: "/shipmentList", query: query})
    },
    onUpload(file){
      this.uploadedFile = file;
    },
    getComponentsById(id) {
      return this.item.itemComponents.filter(ic => {
        return ic.component.category.id == id;
      });
    },
    getAvailableComponents() {
      http.get("/component/kv").then(response => {
        this.availableComponents = [];
        response.data.forEach(c => {
          var found = this.item.itemComponents.findIndex(ic =>  ic.component.id == c.id);
          if(found == -1){
            this.availableComponents.push(c);
          }
        })
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getItem(item_id) {
      return http.get("/item/" + item_id).then(response => {
        this.item = response.data;
        if(response.data.upc){
          this.upc = {id: response.data.upc.id};
          // this.setUpcUrl(response.data.upc.id);
        }
        return response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableBrands() {
      http.get("/brand").then(response => {
        this.availableBrands = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableCategories() {
      http.get("/category/item/kv").then(response => {
        response.data.forEach(category => {
          this.availableItemCategories.push(category);
        });
      }).catch(e => {
        console.log("API error: " + e);
      });
      http.get("/category/component/kv").then(response => {
        response.data.forEach(category => {
          this.availableCompCategories.push(category);
        });
      }).catch(e => {
        console.log("API error: " + e);
        });
    },
    getAvailableSeasons() {
      http.get("/season").then(response => {
        this.availableSeasons = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    validate(){
        if(!this.item.name || !this.item.number || !this.item.season.id || !this.item.brand.id || !this.item.category.id){
          alert("Item Name, Number, Season, Brand and Category are required");
          return false;
        }

        return true;
    },
    saveItem() {
        if(!this.validate()){
            return Promise.reject();
        }
      this.item.totalCost = this.totalCost;
      if(this.upc.id){
        this.item.upc = {id: this.upc.id}
      }
      // if(this.caseUpc.id){
      //   this.item.caseUpc = {id: this.caseUpc.id}
      // }
      var formData = new FormData();
      formData.append("image", this.uploadedFile);
      formData.append("jsonItem", JSON.stringify(this.item));
      var headers = {"Content-Type": "multipart/form-data"};
      return http.post("/item", formData, headers).then(r => {
        this.getItem(r.data.id)
        return r.data;
      }).catch(e => {
         console.log("API error: " + e);
      });
    },
    goToComponent(componentId) {
      router.push('/componentEdit/'+componentId);
    },
    removeItemComponent(ic_id) {
      var idx = this.item.itemComponents.findIndex(ic => ic.id == ic_id);
      this.item.itemComponents.splice(idx, 1);
	},
	getImageUrl(){
		if(this.item.attachment){
        	return httpUtils.getUrl("/file/attachment/" + this.item.attachment.id);
		}
		return null;
  },
  getReturnClass(){
    if(this.item.unitsReceived > this.item.unitsReturned){
      return "return-red";
    }
    return "";
  },
  deleteItem() {
    this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(ok => {
      if(ok){
        http.delete("/item/"+this.item.id).then(response => {
            router.push("/itemList")
          }).catch(e => {
            console.log("Error post");
          });
        }
    })
  },
  getAvailableUpc(){
    http.get("/upc/kv").then(r=> {
      this.availableUpc = r.data;
    }).catch(e=> {console.log("API error: " + e);})
  }
  },
  mounted() {
    this.getAvailableUpc();
    this.getAvailableComponents();
    var item_id = this.$route.params.item_id;
    if (item_id) {
      this.getItem(item_id);
    }
    this.getAvailableBrands();
    this.getAvailableCategories();
    this.getAvailableSeasons();
  }
};
</script>

<style>
.return-red {
  background-color: red;
}
</style>
