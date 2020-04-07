<template>
  <div>
    <div style="cursor: pointer" @click="openModal()">
        <img src="../assets/pdf-download.png" width="23px">
    </div>
    <b-modal centered v-model="modalVisible" size="lg" :hide-header="true" :hide-footer="true">
      <div style="font-size: 12px;">
        <b-row>
          <b style="margin-left: 25px">Upload/Download documents</b>
        </b-row>
        <b-row>
          <b-col cols=7 style="margin-top: 10px; margin-left: 10px">
            <b-link @click="headerClick()" role="botton">{{headerText}}</b-link>
          </b-col>
          <b-col cols=2 style="margin-left: 100px">
            <div style="display:flex">
              <b-button style="margin-left: 40px" :disabled="uploadProgress" size="sm" variant="info" @click="addFile()">+ <b-spinner v-if="uploadProgress" small></b-spinner></b-button>
              <b-button style="margin-left: 3px" variant="success" :disabled="uploadProgress" size="sm" @click="saveModal()">Save</b-button>
              <b-button style="margin-left: 3px" :disabled="uploadProgress" size="sm" @click="closeModal()">Close</b-button>
            </div>
          </b-col>
        </b-row>
        <br/>
        <b-table :items="newAttachments" :fields="columns" thead-class="hidden_header">
          <template v-slot:cell(name)="row">
            <b-link role="button" @click="downloadFile(row.item)">{{row.item.name}}</b-link>
          </template>
          <template v-slot:cell(updated)="row">
            <span>{{formatDate(row.item.updated)}}</span>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click="deleteAttachment(row.item)">x</b-button>
          </template>
        </b-table>
      </div>
    </b-modal>
    </div>
</template>
<script>
import http from "../http-common";
import httpUtils from "../httpUtils";
import moment from "moment";

export default {
  props: {
    attachments: {type: Array, required: true},
    type: {type: String, required: true},
    entityId: {type: Number, required: true},
    headerText: {type: String, required: false},
  },
  data() {
    return {
      newAttachments: [],
      modalVisible: false,
      uploadProgress: false,
      columns: [
        { key: "name", label: "File", sortable: false },
        { key: "updated", label: "Uploaded", sortable: false },
        { key: "action", label: "", sortable: false }
      ],
    };
  },
  computed: {
  },
  methods: {
    formatDate(date){
      return moment(date).format("YYYY-MM-DD");
    },
    deleteAttachment(attachment){
      var idx = this.newAttachments.findIndex(a => a.id == attachment.id);
      this.newAttachments.splice(idx, 1);
    },
    addFile() {
      if(this.newAttachments.length>4){
        alert("Maximum 5 files allowed.");
        return;
      }
      this.inputElement = document.createElement("input");
      this.inputElement.type = "file";
      this.inputElement.onchange = this.uploadFile;
	    this.inputElement.accept = "image/png, image/jpeg, application/pdf";
      this.inputElement.click();
    },  
    openModal(){
      this.newAttachments = JSON.parse(JSON.stringify(this.attachments));
      this.modalVisible = true;
    },
    saveModal(){
      this.$emit("close", this.newAttachments);
      this.modalVisible = false;
    },
    closeModal(){
      this.modalVisible = false;
    },
    headerClick(){
      this.$emit("header-click");
    },
    downloadFile(attachment){
      var url = httpUtils.getUrl("/file/attachment/"+attachment.id);
       window.open(url, "_blank","")
    },
    uploadFile(file){
      this.uploadProgress = true;
      var file = this.inputElement.files[0];
      console.log("Size: "+file.size);
      if(file.size > 10485760){
        alert("Maximum 10MB file allowed");
        return;
      }
      var formData = new FormData();
      formData.append("file", file);
      formData.append("type", this.type);
      formData.append("entityId", this.entityId);
      var headers = {headers: {"Content-Type": "multipart/form-data"}}
      http.post("/file/upload", formData, headers).then(r =>{
        this.newAttachments.push(r.data);
        this.uploadProgress = false;
      }).catch(e => {
        console.log("API error: "+e);
      });
    },
  },
  mounted() {
  }
};
</script>
<style>
.hidden_header {
  display: none;
}
</style>
