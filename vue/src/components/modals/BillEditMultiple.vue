<template>
  <b-container fluid>
    <b-modal centered size="md" v-model="visible" :hide-header="true" :hide-footer="true">
      <b-row>
        <b-col cols=6>
          <label class="top-label">Invoice #</label>
          <input class="form-control" type="text" v-model="invoiceNumber">
        </b-col>
        <b-col cols=6>
          <div style="text-align: right;">
            <b-button size="sm" type="reset" variant="success" @click="saveModal()">Save</b-button>
            <b-button size="sm" style="margin-left: 3px" type="reset" variant="secondary" @click="closeModal()">Close</b-button>
          </div>
        </b-col>
      </b-row>
    </b-modal>
  </b-container>
</template>

<script>
import http from "../../http-common";
import router from "../../router";
import state from "../../data/state";
import moment from "moment";

export default {
  props: {
    receivingIds: {
      type: Array,
      required: true
    },
  },
  data() {
    return {
      visible: true,
      invoiceNumber: null,
    };
  },
  computed: {
  },
  watch: {},
  methods: {
    save() {
      return http.post("/receiving/invoice/ids/"+this.receivingIds, {}, {params: {invoiceNumber: this.invoiceNumber}}).then(r => {
        return Promise.resolve();
      });
    },
    closeModal(){
      this.$emit("close");
    },
    saveModal() {
      this.save().then(r => {
        this.closeModal();
      });
    },
  },
  mounted() {
  }
};
</script>

<style>
.modal-lg{
  max-width: 90%;
}
</style>
