<template>
    <b-container fluid>
        <b-row>
            <b-col cols=2>
                <div style="width: 200px; height: 490px; background-color: #3062ae; text-align: right; color: white;"><span style="padding-right: 15px">Announcements</span></div>
            </b-col>
            <b-col cols=10>
                <b-row style="width: 1500px">
                    <b-col cols=2>
                        <div class="home-box-green" @click="goTo('/supplierList')"><span style="padding-right: 15px">Manage Suppliers</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-orange" @click="goTo('/componentList')"><span style="padding-right: 15px">Components</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-green" @click="goTo('/itemList')"><span style="padding-right: 15px">Item List</span></div>
                    </b-col>
                    <b-col cols=2>
						<div class="home-box-navy" @click="goTo('/schedule')"><span style="padding-right: 15px">Schedule Production</span></div>
                    </b-col>        
                </b-row>
                <b-row style="padding-top: 10px; width: 1500px">
                    <b-col cols=2>
                        <div class="home-box-green" @click="goTo('/customerList')"><span style="padding-right: 15px">Customers</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-blue" @click="goTo('/saleList')"><span style="padding-right: 15px">Sale Orders</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-navy" @click="goTo('/saleList')"><span style="padding-right: 15px">Purchase Orders</span></div>
                    </b-col>
                    <b-col cols=2>
                        <div class="home-box-orange" @click="goTo('/productionLineList')"><span style="padding-right: 15px">Daily Production Status</span></div>
                    </b-col>        
                </b-row>
            </b-col>
        </b-row>
    </b-container>
</template>

<script>
import http from "../http-common";
import router from "../router";

export default {
  data() {
    return {
    };
  },
  computed: {
  },
  methods: {
    goTo(view) {
      router.push(view);
    },
  }
};
</script>
<style lang="scss">
.home-box-green {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #549e58;
}
.home-box-blue {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #3062ae;
}
.home-box-navy {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #106f87;
}
.home-box-orange {
  color: white;
  text-align: right;
  width: 240px;
  height: 240px;
  background-color: #f0a40b;
}
.home-box-orange {
  color: white;
  text-align: right;
  text-align: right;
  width: 240px;
  height: 240px;
}
</style>
