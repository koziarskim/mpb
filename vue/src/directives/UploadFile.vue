<template>
  <div>
    <div style="cursor: pointer" @click="openModal()">
        <img src="../assets/pdf-download.png" width="23px">
    </div>
    <b-modal centered v-model="modalVisible" size="lg" :hide-header="true" :hide-footer="true">
      <div style="font-size: 12px">
        <b-row>
          <b-col cols=1 offset=9>
            <div style="display:flex">
              <b-button style="margin-left: 40px" variant="success" :disabled="uploadProgress" size="sm" @click="saveModal()">Save</b-button>
              <b-button style="margin-left: 3px" :disabled="uploadProgress" size="sm" @click="closeModal()">Close</b-button>
            </div>
          </b-col>
        </b-row>
        <b-table :items="newAttachments" :fields="columns">
          <template v-slot:cell(name)="row">
            <b-link role="button">{{row.item.name}}</b-link>
          </template>
          <template v-slot:cell(action)="row">
            <b-button size="sm" @click="deleteAttachment(row.item)">x</b-button>
          </template>
        </b-table>
        <b-row>
          <b-col cols=1 offset=10>
            <b-button :disabled="uploadProgress" size="sm" variant="success" @click="addFile()">+ <b-spinner v-if="uploadProgress" small></b-spinner></b-button>
          </b-col>
        </b-row>
      </div>
    </b-modal>
    </div>
</template>
<script>
import axios from "axios";
import http from "../http-common";
import httpUtils from "../httpUtils";

export default {
  props: {
    attachments: {type: Array, required: true},
    type: {type: String, required: true},
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
    deleteAttachment(attachment){
      var idx = this.newAttachments.findIndex(a => a.id == attachment.id);
      this.newAttachments.splice(idx, 1);
    },
    addFile() {
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
      this.$emit("close", newAttachments);
      this.modalVisible = false;
    },
    closeModal(){
      this.modalVisible = false;
    },
    uploadFile(file){
      this.uploadProgress = true;
      var file = this.inputElement.files[0];
      var formData = new FormData();
      formData.append("file", file);
      formData.append("type", this.type);
      var headers = {headers: {"Content-Type": "multipart/form-data"}}
      axios.post(httpUtils.baseUrl + "/file", formData, headers).then(r =>{
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