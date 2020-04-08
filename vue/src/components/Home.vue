<template>
    <b-container fluid>
        <b-row>
            <b-col cols=2>
                <div style="margin-top: 5px; width: 200px; height: 470px; background-color: #3062ae; text-align: right; color: white;"><span style="padding-right: 15px">Announcements</span></div>
                <div @click="sendMail()">send mail</div>
            </b-col>
            <b-col cols=10>
              <div style="display:flex">
                <div style="background-color: #ffc1074f" class="home-box" @click="goTo('/supplierList')"><span style="padding-right: 15px">Manage Suppliers</span></div>
                <div style="background-color: #f4433638" class="home-box" @click="goTo('/componentList')"><span style="padding-right: 15px">Component Inventory</span></div>
                <div style="background-color: #9c27b04a" class="home-box" @click="goTo('/itemList')"><span style="padding-right: 15px">Item Assembly</span></div>
                <div style="background-color: #ff98006b" class="home-box" @click="goTo('/customerList')"><span style="padding-right: 15px">Customers Info</span></div>
                <div style="background-color: #03a9f440" class="home-box" @click="goTo('/saleList')"><span style="padding-right: 15px">Sale Orders (S.O.)</span></div>
              </div>
              <div style="display:flex">
                <div style="background-color: #4caf507d" class="home-box" @click="goTo('/purchaseList')"><span style="padding-right: 15px">Purchase Orders (P.O.)</span></div>
                <div style="background-color: #607d8b66" class="home-box" @click="goTo('/receivingList')"><span style="padding-right: 15px">Components Received</span></div>
                <div style="background-color: #9e9e9e7d" class="home-box" @click="goTo('/schedule')"><span style="padding-right: 15px">Production Schedule</span></div>
                <div style="background-color: #79554873" class="home-box" @click="goTo('/productionLineList')"><span style="padding-right: 15px">Production Status</span></div>
                <div style="background-color: #cddc3982" class="home-box" @click="goTo('/shipmentList')"><span style="padding-right: 15px">Shipments</span></div>
              </div>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";
import securite from "../securite";

export default {
  data() {
    return {
      securite: securite,
      yearName: "",
    };
  },
  computed: {
  },
  methods: {
    goTo(view) {
      router.push(view);
    },
    sendMail(){
      http.post("/mail").then(r => {
      })
    },
  },
  mounted() {
  }
};
</script>
<style lang="scss">
.home-box {
  cursor: pointer;
  font-weight: bold;
  text-align: right;
  width: 230px;
  height: 230px;
  margin: 5px;
}
</style>
