<template>
  <b-container fluid>
    <b-modal centered size="lg" v-model="visible" :hide-header="true" :hide-footer="true">
		<b-row>
			<b-col cols=6>
				<label class="top-label">Customer Name</label>
				<input class="form-control" maxlength="20" type="text" v-model="palletTag.customerName">
			</b-col>
			<b-col>
				<div style="text-align: right; margin-top: 20px">
					<b-button style="margin: 0 2px 0 2px" @click="closeModal()">Close</b-button>
					<b-button style="margin: 0 2px 0 2px" @click="saveModal()" variant="success">Save</b-button>
				</div>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=6>
				<label class="top-label">Location ID:</label>
				<input class="form-control" type="text" v-model="palletTag.locationName"/>
			</b-col>
			<b-col cols=4>
				<label class="top-label">Distribution Center/Location Name</label>
				<input class="form-control" maxlength="20" type="text" v-model="palletTag.dc">
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=4>
				<label class="top-label">Street:</label>
				<input class="form-control" type="text" v-model="palletTag.street"/>
			</b-col>
			<b-col cols=4>
				<label class="top-label">Line 1:</label>
				<input class="form-control" type="text" v-model="palletTag.line1"/>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=6>
				<label class="top-label">City:</label>
				<input class="form-control" type="text" v-model="palletTag.city"/>
			</b-col>
			<b-col cols=2>
				<label class="top-label">State:</label>
				<input class="form-control" type="text" v-model="palletTag.state"/>
			</b-col>
			<b-col cols=4>
				<label class="top-label">Zip:</label>
				<input class="form-control" type="text" v-model="palletTag.zip"/>
			</b-col>
		</b-row>		
		<b-row>
			<b-col cols=5>
				<label class="top-label">Sale Number:</label>
				<input class="form-control" type="text" v-model="palletTag.saleNumber"/>
			</b-col>
			<b-col cols=7>
				<label class="top-label">Item Number/Name:</label>
				<input class="form-control" type="text" v-model="palletTag.itemNumber"/>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=12>
				<label class="top-label">SKU:</label>
				<input class="form-control" type="text" v-model="palletTag.sku"/>
			</b-col>
		</b-row>
		<b-row>
			<b-col cols=3>
				<label class="top-label">Case Pack:</label>
				<input class="form-control" type="number" v-model="palletTag.casePack"/>
			</b-col>
			<b-col cols=3>
				<label class="top-label">Best By:</label>
				<input class="form-control" type="date" v-model="palletTag.expiration"/>
			</b-col>
			<b-col cols=3>
				<label class="top-label">Cases:</label>
				<input class="form-control" type="text" v-model="palletTag.cases"/>
			</b-col>
			<b-col cols=3>
				<label class="top-label">Pallet (from/to):</label>
				<div style="display: flex">
					<input class="form-control" type="text" v-model="palletTag.pageFrom"/>
					<input class="form-control" style="margin-left: 3px;" type="tel" v-model="palletTag.pageTo"/>
				</div>
			</b-col>
		</b-row>		
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import moment from "moment";
import httpUtils from "../../httpUtils";
import axios from "axios";

export default {
  props: {
	palletTag: {
      type: Object,
      required: true
    },
  },
  data() {
    return {
		visible: true,
		loaderActive: false,
    };
  },
  computed: {
  },
  watch:{},
  methods: {
    validate() {
		if(!this.palletTag.dc){
			alert("Required: DC Name, Street, City, Zip, State");
			return false;
		}
		if(!this.palletTag.expiration){
          alert("Best By is not set");
        	return false;
		}
        if(this.palletTag.pageFrom > this.palletTag.pageTo){
          alert("Page from/to are too large");
          return false;
		}
        // if(!this.sale.shippingAddress || !this.sale.billingAddress){
        //   alert("Sale has no shipping or billing address selection required")
        //   return false;
        // }		
      return true;
    },
    saveModal() {
     	if (!this.validate()) {
        	return;
		}
		var url = httpUtils.getUrl("/shipmentItem/" + this.palletTag.shipItemId 
			+ "/tag/pdf?pageFrom="+this.palletTag.pageFrom+"&pageTo="+this.palletTag.pageTo, "");
		this.loaderActive = true;
		http.put(url, this.palletTag, { responseType: 'blob'}).then(r => {
        	const url = URL.createObjectURL(new Blob([r.data], { type: r.headers['content-type']}))
        	const link = document.createElement('a')
        	link.href = url
        	link.setAttribute("download", r.headers['file-name'])
        	document.body.appendChild(link)
        	link.click()
      	});


        // axios({
        //   url: url,
        //   method: 'GET',
        //   responseType: 'blob',
        // }).then((response) => {
        //     var fileURL = window.URL.createObjectURL(new Blob([response.data]));
        //     var fileLink = document.createElement('a');
        //     fileLink.href = fileURL;
        //     var fileName = response.headers["file-name"];
        //     fileLink.setAttribute('download', fileName);
        //     document.body.appendChild(fileLink);
        //     fileLink.click();
        //     this.loaderActive = false;
		// 	fileLink.remove();
		// 	this.closeModal();
        // });
    },
    closeModal() {
      this.$emit("closeModal");
    }
  },
  mounted() {
  }
};
</script>

<style>
</style>
