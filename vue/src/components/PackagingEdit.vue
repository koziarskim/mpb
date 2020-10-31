<template>
    <b-container fluid>
      <b-row>
        <b-col cols=2 offset=10>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="save()">Save</b-button>
            <b-button style="margin-left: 3px" size="sm" type="reset" variant="secondary" @click="deletePackaging()">X</b-button>
          </div>
        </b-col>
      </b-row>
      <b-row>        
        <b-col cols=3 offset=1>
          <label class="top-label">Packaging Name:</label>
          <input class="form-control" type="text" v-model="packaging.name">
        </b-col>
        <b-col cols=3>
          <label class="top-label">Packaging Type:</label>
          <b-select option-value="id" option-text="name" :list="availableTypes" v-model="typeKv"></b-select>
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=3 offset=1>
          <label class="top-label">Case Dimension (H x W x D):</label>
          <div style="display:flex">
            <input class="form-control" v-model="packaging.caseHeight"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="packaging.caseWidth"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="packaging.caseDepth">
          </div>
        </b-col>
        <b-col cols=1>
          <label class="top-label">Case Pack:</label>
          <input class="form-control" type="text" v-model="packaging.casePack">
        </b-col>
      </b-row>
      <b-row>
        <b-col cols=2 offset=1>
          <label class="top-label">TI x HI (pcs):</label>
          <div style="display:flex">
            <input class="form-control" v-model="packaging.ti" placeholder="0"><span style="margin-top: 7px">x</span>
            <input class="form-control" v-model="packaging.hi" placeholder="0">
          </div>
        </b-col>
        <b-col cols=1>
          <label class="top-label">Pallet Weight:</label>
          <input class="form-control" v-model="packaging.palletWeight">
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
      packaging: {},
      availableTypes: [
        {id: "MASTER_CARTON", name: "Master Carton"},
        {id: "PDQ", name: "PDQ"}
      ],
      typeKv: {},

    };
  },
  computed: {
    unitsPerPallet() {
      return +this.packaging.hi * +this.packaging.ti * +this.packaging.casePack;
    },
    palletHeight() {
      return +this.packaging.caseHeight * +this.packaging.hi;
    },   
    casesPerPallet() {
      return +this.packaging.hi * +this.packaging.ti;
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
    getPackaging(id){
      http.get("/packaging/"+id).then(r => {
        this.packaging = r.data;
        this.typeKv = {id: r.data.type};
      }).catch(e => {
        console.log("API error: " + e);
      });      
    },
    save(){
      this.packaging.type = this.typeKv.id;
      http.post("/packaging", this.packaging).then(r => {
        router.back();
      }).catch(e => {
        console.log("API error: " + e);
      });      
    },
    deletePackaging(){
      this.$bvModal.msgBoxConfirm('Are you sure you want to delete?').then(ok => {
        if(ok){
          http.delete("/packaging/"+this.packaging.id).then(r => {
            router.back();
          }).catch(e => {
            console.log("API error: " + e);
          });
          }
      })
    },
  },
  mounted() {
    var id = this.$route.params.packaging_id;
    if (id) {
      this.getPackaging(id);
    }
  },
};
</script>

<style>
</style>
