<template>
  <div>
    <div style="cursor: pointer" @click="openModal()">
        <img src="../assets/pdf-download.png" width="23px">
    </div>
    <b-modal centered v-model="modalVisible" size="sm" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=1 offset=8>
          <b-button :disabled="uploadProgress" size="sm" @click="closeModal()">Close</b-button>
        </b-col>
      </b-row>
      {{attachmentIds}}
      <br/>
      <br/>
      <b-row>
        <b-col cols=3 offset=7>
          <b-button :disabled="uploadProgress" style="width:65px" size="sm" @click="addFile()">Add <b-spinner v-if="uploadProgress" small></b-spinner></b-button>
        </b-col>
      </b-row>
    </b-modal>
    </div>
</template>
<script>
import axios from "axios";
import http from "../http-common";
import httpUtils from "../httpUtils";

export default {
  props: {
    attachmentIds: {type: Array},
    uploadUrl: {type: String, required: true},
  },
  data() {
    return {
      modalVisible: false,
      uploadProgress: false,
    };
  },
  computed: {
  },
  methods: {
    addFile() {
      this.inputElement = document.createElement("input");
      this.inputElement.type = "file";
      this.inputElement.onchange = this.uploadFile;
	    this.inputElement.accept = "image/png, image/jpeg, application/pdf";
      this.inputElement.click();
    },  
    openModal(){
      this.modalVisible = true;
    },
    closeModal(){
      this.modalVisible = false;
    },
    uploadFile(file){
      this.uploadProgress = true;
      var file = this.inputElement.files[0];
      var formData = new FormData();
      formData.append("file", file);
      // formData.append("jsonComponent", JSON.stringify(this.component));
      var headers = {headers: {"Content-Type": "multipart/form-data"}}
      axios.post(httpUtils.baseUrl + this.uploadUrl, formData, headers).then(r =>{
        this.attachmentIds.push(r.data.id);
        this.uploadProgress = false;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
  }
};
</script>