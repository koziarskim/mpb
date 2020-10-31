<template>
    <b-container fluid>
      <b-row>
        <b-col cols=1 offset=11>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="save()">Save</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>        
        <b-col cols=3 offset=1>
          <label class="top-label">Packaging Name:</label>
          <input class="form-control" type="text" v-model="updatedPackaging.name">
        </b-col>
        <b-col cols=3>
          <label class="top-label">Packaging Type:</label>
          <b-select option-value="id" option-text="name" :list="availableTypes" v-model="type"></b-select>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=3 offset=1>
          <label class="top-label">Case Dimension (H x W x D):</label>
          <div style="display:flex">
            <input class="form-control" v-model="updatedPackaging.caseHeight"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="updatedPackaging.caseWidth"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="updatedPackaging.caseDepth">
          </div>
        </b-col>
        <b-col cols=1>
          <label class="top-label">Case Pack:</label>
          <input class="form-control" type="text" v-model="updatedPackaging.casePack">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=2 offset=1>
          <label class="top-label">TI x HI (pcs):</label>
          <div style="display:flex">
            <input class="form-control" v-model="updatedPackaging.ti" placeholder="0"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="updatedPackaging.hi" placeholder="0">
          </div>
        </b-col>
        <b-col cols=1>
          <label class="top-label">Pallet Weight:</label>
          <input class="form-control" v-model="updatedPackaging.palletWeight">
        </b-col>
        <b-col cols=2>
          <label class="top-label">Units p/ pallet: {{unitsPerPallet}}</label><br/>
          <label class="top-label">Pallet height: {{palletHeight}}</label><br/>
          <label class="top-label">Cases p/p: {{casesPerPallet}}</label>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=2 offset=1>
          <label class="top-label">Warehouse Cost ($): {{warehouseCost}}</label>
        </b-col>
        <b-col cols=2>
          <label class="top-label">Packaging Cost ($): {{packageCost}}</label>
        </b-col>
      </b-row>
    </b-container>
</template>
<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite"
import navigation from "../utils/navigation";

export default {
  name: "PackagingList",
  data() {
    return {
      packaging: {},
      itemName: "",
      updatedPackaging: {},
      availableTypes: [
        {id: "MASTER_CARTON", name: "Master Carton"},
        {id: "PDQ", name: "PDQ"}
      ],
      type: {},

    };
  },
  computed: {
    unitsPerPallet() {
      return +this.updatedPackaging.hi * +this.updatedPackaging.ti * +this.updatedPackaging.casePack;
    },
    palletHeight() {
      return +this.updatedPackaging.caseHeight * +this.updatedPackaging.hi;
    },   
    casesPerPallet() {
      return +this.updatedPackaging.hi * +this.updatedPackaging.ti;
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
  },
  methods: {
    save(){
      this.updatedPackaging.type = this.type.id;
      this.updatedPackaging.label = this.updatedPackaging.name + " ("+this.updatedPackaging.type+")";
      this.$emit("close", this.updatedPackaging);
    },
    close(){
      this.$emit("close");
    },
  },
  mounted() {
    this.updatedPackaging = this.packaging;
    this.type = {id: this.updatedPackaging.type};
  },
};
</script>

<style>
</style>
