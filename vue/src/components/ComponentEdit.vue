<template>
  <b-container fluid>
    <b-row>
      <b-col cols=2>
        <label class="top-label">Component #:</label>
        <input class="form-control" maxlength="20" type="tel" v-model="component.number" />
      </b-col>
      <b-col cols=3>
        <label class="top-label">Name/Short Description:</label>
        <input class="form-control" type="tel" maxlength="50" v-model="component.name" />
      </b-col>
      <b-col cols=2>
        <label class="top-label">Category:</label>
        <b-select option-value="id" option-text="name" :list="availableCategories" v-model="category"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Type:</label>
        <b-select option-value="id" option-text="name" :list="availableComponentTypes" v-model="componentType"></b-select>
      </b-col>
      <b-col cols=2 style="margin-top: 5px">
        <upload :on-upload="onUpload" :file-url="getImageUrl()"></upload>
      </b-col>
      <b-col cols=1 style="margin-left: -50px">
        <div style="margin-right: -10px">
          <b-button :disabled="!allowEdit()" size="sm" variant="success" @click="saveComponent()">Save</b-button>
          <b-button :disabled="!allowEdit()" style="margin-left: 3px" size="sm" @click="deleteComponent()">x</b-button>
        </div>
        <br />
        <label class="top-label">
          Stock:
          <b-link role="button" @click="goToReceiving(component.id)">{{component.unitsOnStock}}</b-link>
        </label>
        <br />
        <label class="top-label">Reserved: {{component.unitsLocked}}</label>
      </b-col>
    </b-row>
    <b-row style="margin-top: -60px">
      <b-col cols=12>
        <b-row>
          <b-col cols=3>
            <label class="top-label">Supplier:</label>
            <b-select option-value="id" option-text="name" :list="availableSuppliers" v-model="supplier"></b-select>
          </b-col>
          <b-col cols=2>
            <label class="top-label">Supplier's Stock#:</label>
            <input class="form-control" type="text" v-model="component.supplierStockNumber" />
          </b-col>
          <b-col cols=3>
            <label class="top-label">Dimension (H x W x D):</label>
            <div style="display:flex">
              <input class="form-control" type="number" v-model="component.height" />
              <span style="padding:7px">x</span>
              <input class="form-control" type="number" v-model="component.width" />
              <span style="padding:7px">x</span>
              <input class="form-control" type="number" v-model="component.depth" />
            </div>
          </b-col>
          <b-col cols=1>
            <label class="top-label">Net Weight:</label>
            <input class="form-control" type="number" min="0" v-model="component.weight" />
          </b-col>          
        </b-row>
      </b-col>
    </b-row>
    <b-row>
      <b-col cols=5>
        <label class="top-label">Notes:</label>
        <b-form-textarea type="text" :rows="3" v-model="component.description"></b-form-textarea>
      </b-col>
      <b-col cols=1>
        <label class="top-label">Case Pack:</label>
        <input class="form-control" type="number" min="0" v-model="component.casePack" />
      </b-col>
      <b-col cols=1>
        <label class="top-label">Case Weight:</label>
        <input class="form-control" type="number" min="0" v-model="component.caseWeight" />
      </b-col>
      <b-col cols=2>
        <label class="top-label">Shelf Life:</label>
        <b-select option-value="id" option-text="name" :list="availableShelfs" v-model="shelf"></b-select>
      </b-col>
      <b-col cols=2>
        <label class="top-label">Expiration:</label>
        <input class="form-control" type="date" v-model="component.expiration" />
      </b-col>
    </b-row>
    <hr class="hr-text" data-content="Unit prices/fees are in USD ($)" />
    <b-row>
      <b-col cols="2">
        <label class="top-label">Duty %:</label>
        <input class="form-control" type="number" min="0" v-model="component.dutyPercentage" />
      </b-col>
      <b-col cols="2">
        <label class="top-label">Container Cost $:</label>
        <input class="form-control" type="number" min="0" v-model="component.containerCost" />
      </b-col>
      <b-col cols="2">
        <label class="top-label">Other $:</label>
        <input class="form-control" type="number" min="0" v-model="component.otherCost" />
      </b-col>
      <b-col cols="2">
        <br />
        <label class="top-label">Recent unit price: ${{component.unitCost}}</label>
        <label class="top-label">Average price: ${{component.unitCost}}</label>
        <br />
      </b-col>
      <b-col cols="2">
        <br />
        <label class="top-label">Delivery $: {{deliveryCost}}</label>
        <br />
        <label class="top-label">Duty cost $: {{dutyCost}}</label>
      </b-col>
      <b-col cols="2">
        <br />
        <label class="top-label">Case Cost $: {{caseCost.toFixed(2)}}</label>
        <label class="top-label">Total Landed Cost $: {{totalLandedCost}}</label>
        <br />
      </b-col>
    </b-row>
    <b-row>
      <b-col>
        <label class="top-label">Component is used in following Items:</label>
        <b-table :items="component.itemComponents" :fields="columns">
          <template v-slot:head(units)="row">
            <div>Assembly</div><div class="mpb-head-line">Units per item</div>
          </template>
          <template v-slot:cell(item.number)="row">
            <b-button size="sm" @click="goToItem(row.item.item.id)" variant="link">{{row.item.item.number}}</b-button>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click="deleteItemComponent(row.item.id)">x</b-button>
          </template>
          <template v-slot:cell(unitsSchProd)="row">
            <b-button size="sm" @click="goToItemScheduleList(row.item.item.id)" variant="link">{{row.item.item.unitsScheduled}} / {{row.item.item.unitsProduced}}</b-button>
          </template>
        </b-table>
      </b-col>
    </b-row>
  </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import httpUtils from "../httpUtils";
import securite from "../securite";

export default {
  name: "add-component",
  data() {
    return {
      component: {
        unitCost: 0,
        dutyPercentage: 0,
        deliveryCost: 0,
        containerCost: 0,
        otherCost: 0,
        casePack: 1,
        unitsPerContainer: 1,
        totalLandedCost: 0,
        itemComponents: [],
        category: {},
        componentType: {}
      },
      uploadedFile: null,
      dimension: "",
      supplier: {},
      availableSuppliers: [],
      availableCategories: [],
      category: {},
      availableItems: [],
      item: {},
      icUnits: 0,
      sortBy: "id",
      sortDesc: false,
      columns: [
        { key: "item.number", label: "Item #", sortable: false },
        { key: "item.name", label: "Name", sortable: false },
        { key: "units", label: "Assembly", sortable: false },
        { key: "item.brand.name", label: "Brand", sortable: false },
        { key: "item.unitsReadyProd", label: "RFP", sortable: false },
        { key: "unitsSchProd", label: "Sch/Prod", sortable: false }
      ],
      availableComponentTypes: [],
      componentType: {},
      availableShelfs: [
        {id: "6_MONTHS", name: "6 Months"},
        {id: "9_MONTHS", name: "9 Months"},
        {id: "12_MONTHS", name: "12 Months"},
        {id: "24_MONTHS", name: "24 Months"},
        {id: "36_MONTHS", name: "36 Months"},
      ],
      shelf: {},

    };
  },
  computed: {
    totalLandedCost() {
      return (
        +this.component.unitCost +
        +this.component.deliveryCost +
        +this.component.otherCost
      ).toFixed(4);
    },
    deliveryCost() {
      return (
        +this.component.containerCost
      ).toFixed(2);
    },
    dutyCost: function() {
      return +this.component.unitCost * +this.component.dutyPercentage;
    },
    caseCost() {
      return +this.component.unitCost * +this.component.casePack;
    }
  },
  watch: {
    supplier(newValue, oldValue) {
      this.component.supplier = newValue;
    },
    deliveryCost(newValue, oldValue) {
      this.component.deliveryCost = newValue;
    },
    totalLandedCost(newValue, oldValue) {
      this.component.totalLandedCost = newValue;
    }, 
    category(newValue, oldValue) {
      this.component.category = newValue;
      this.component.componentType = {};
      this.getAvailableComponentTypes();
    },
    componentType(newValue, oldValue) {
      this.component.componentType = newValue;
    },
    shelf(newValue, oldValue) {
      this.component.shelf = newValue.id;
    }
  },
  methods: {
    allowEdit() {
      return securite.hasRole(["STANDARD_ADMIN"]);
    },
    goToReceiving(componentId) {
      var query = { component_id: componentId };
      router.push({ path: "/receivingList", query: query });
    },
    goToItemScheduleList(itemId) {
      router.push("/scheduleEventList/" + itemId);
    },
    addItem() {
      if (!this.item.id) {
        return;
      }
      var found = this.component.itemComponents.find(
        it => it.item.id == this.item.id
      );
      if (found) {
        alert("Item already added");
        return;
      }
      this.getItem(this.item.id).then(item => {
        var ic = {
          item: item,
          units: this.icUnits
        };
        this.component.itemComponents.push(ic);
      });
    },
    onUpload(file) {
      this.uploadedFile = file;
    },
    deleteComponent() {
      this.$bvModal
        .msgBoxConfirm("Are you sure you want to delete Component?")
        .then(ok => {
          if (ok) {
            http
              .delete("/component/" + this.component.id)
              .then(response => {
                router.push("/componentList");
              })
              .catch(e => {
                console.log("API Error: " + e);
              });
          }
        });
    },
    getComponentData(component_id) {
      return http
        .get("/component/" + component_id)
        .then(r => {
          this.component = r.data;
          if (r.data.supplier) {
            this.supplier = r.data.supplier;
          }
          if(r.data.category){
            this.category = r.data.category;
          }
          if (r.data.componentType){
            this.componentType = r.data.componentType;
          }
          if (r.data.shelf){
            this.shelf = {id: r.data.shelf};
          }
          return r.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableSuppliers() {
      http
        .get("/supplier/kv")
        .then(response => {
          this.availableSuppliers = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getAvailableCategories() {
      http.get("/category/type/CMP").then(response => {
        this.availableCategories = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableComponentTypes() {
      var query = {params: {categoryId: this.category.id}}
      http.get("/registery/componentType/kv", query).then(response => {
        this.availableComponentTypes = response.data;
      }).catch(e => {console.log("API error: " + e);});
    },
    getAvailableItems() {
      http
        .get("/item/kv")
        .then(response => {
          this.availableItems = response.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    getItem(item_id) {
      return http
        .get("/item/" + item_id)
        .then(r => {
          return r.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    validate(){
      if(!this.component.name || !this.component.number || !this.component.supplier || !this.component.category.id || !this.component.componentType.id){
        alert("Number, Name, Supplier, Category and Type are required");
        return false;
      }
      return true;
    },
    saveComponent() {
      if (!this.validate()) {return false}
      var formData = new FormData();
      formData.append("image", this.uploadedFile);
      formData.append("jsonComponent", JSON.stringify(this.component));
      var headers = { headers: { "Content-Type": "multipart/form-data" } };
      return http
        .post("/component", formData, headers)
        .then(r => {
          this.getComponentData(r.data.id);
          return r.data;
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    deleteItemComponent(ic_id) {
      var idx = this.component.itemComponents.findIndex(ic => ic.id == ic_id);
      http
        .delete("/itemComponent/" + ic_id)
        .then(response => {
          this.component.itemComponents.splice(idx, 1);
        })
        .catch(e => {
          console.log("API error: " + e);
        });
    },
    goToItem(item_id) {
      router.push("/itemEdit/" + item_id);
    },
    getImageUrl() {
      if (this.component.attachment) {
        return httpUtils.getUrl(
          "/file/attachment/" + this.component.attachment.id
        );
      }
      return null;
    }
  },
  mounted() {
    var component_id = this.$route.params.component_id;
    if (component_id) {
      this.getComponentData(component_id);
    }
    this.getAvailableSuppliers();
    this.getAvailableCategories();
    this.getAvailableItems();
  }
};
</script>

<style>
</style>
