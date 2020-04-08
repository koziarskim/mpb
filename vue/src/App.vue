<template>
  <div id="app">
    <div v-if="yearName" class="fade-out">
      <div style="font-size: 20px">You are in the year context</div>
      <div>{{this.yearName}}</div>
    </div>
    <b-navbar toggleable="md" type="dark" variant="dark" style="height:35px">
      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav v-if="showNav">
          <b-nav-item @click="goTo('/home')" :class="navClass('home')">Home</b-nav-item>
          <b-nav-item @click="goTo('/supplierList')" :class="navClass('supplier')">Supplier</b-nav-item>
          <b-nav-item @click="goTo('/componentList')" :class="navClass('component')">Component</b-nav-item>
          <b-nav-item @click="goTo('/itemList')" :class="navClass('item')">Item</b-nav-item>
          <b-nav-item @click="goTo('/customerList')" :class="navClass('customer')">Customer</b-nav-item>
          <b-nav-item @click="goTo('/saleList')" :class="navClass('sale')">Sale</b-nav-item>
          <b-nav-item @click="goTo('/purchaseList')" :class="navClass('purchase')">Purchase</b-nav-item>
          <b-nav-item @click="goTo('/receivingList')" :class="navClass('receiving')">Receiving</b-nav-item>
          <b-nav-item @click="goTo('/shipmentList')" :class="navClass('shipment')">Shipment</b-nav-item>
          <b-nav-item @click="goTo('/shipmentSchedule')" :class="navClass('shipmentSchedule')">Schedule</b-nav-item>
          <b-nav-item @click="goTo('/productionLineList')" :class="navClass('production')">Production/</b-nav-item>
          <b-nav-item @click="goTo('/schedule')" :class="navClass('schedule')" style="margin-left: -17px">Schedule</b-nav-item>
          <b-nav-item @click="goTo('/invoiceList')" :class="navClass('invoice')">Accounting</b-nav-item>
		      
        </b-navbar-nav>
        <b-navbar-nav v-if="showNav" style="margin:0px 0px 0px auto;">
          <!-- <b-nav-item-dropdown right :text="user.season.name">
            <b-dropdown-item v-for="season in availableSeasons" :key="season.id" @click="changeSeason(season)">{{season.name}}</b-dropdown-item>
          </b-nav-item-dropdown> -->
        </b-navbar-nav>
        <b-navbar-nav v-if="showNav">
          <b-nav-item-dropdown right :text="user.year.name">
            <b-dropdown-item v-for="year in availableYears" :key="year.id" @click="changeYear(year)">{{year.name}}</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
        <b-navbar-nav v-if="showNav">
          <b-nav-item-dropdown right :text="user.fullName">
            <b-dropdown-item @click="goTo('/Profile')">Profile</b-dropdown-item>
            <b-dropdown-item v-if="securite.hasRole(['ADMIN'])" @click="goTo('/users')">Manage Users</b-dropdown-item>
            <b-dropdown-item @click="logout()">Signout</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
        <b-navbar-nav v-if="!showNav">
          <img style="height: 37px; margin-top: 5px" src="./assets/mpb-logo.png">
          <span style="color: #d2cdcd; padding: 7px 20px; font-size: 20px">MIMS | Marketplace Inventory Management System</span>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div class="center pagebase" :class="getViewClass()">
      <router-view :key="$route.fullPath"></router-view>
    </div>
  </div>
</template>

<script>
import http from "./http-common";
import router from "./router";
import securite from "./securite";
import navigate from "./utils/navigation";
import { EventBus } from './utils/eventBus.js';

export default {
  name: "app",
  data() {
    return {
      securite: securite,
      navigate: navigate,
      yearName: null,
      availableSeasons: [],
      availableYears: [],
      user: {
        season: {},
        year: {}
      },
      
    };
  },
  computed: {
    showNav(){
      if(this.user == null || this.user.id == null){
        return false;
      }
      return true;
    }
  },
  methods: {
    getViewClass(){
      return navigate.viewClass;
    },
    getUser() {
      if(this.securite.getUser().id){
        this.user = this.securite.getUser();
        if(this.availableSeasons.length < 1 || this.availableYears.length < 1){
          this.getAvailableSeasons();
          this.getAvailableYears();
        }
        this.showYearContextPop();
      }
      if(!this.user || !this.user.id){
        this.goTo("/login");
      }
    },
    getAvailableSeasons() {
      http.get("/season").then(response => {
        this.availableSeasons = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    getAvailableYears() {
      http.get("/year").then(response => {
        this.availableYears = response.data;
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    updateUserSession() {
      http.post("/user", this.user).then(r => {
        this.securite.setUser(this.user);
        router.go();
      }).catch(e => {
        console.log("API error: " + e);
      });
    },
    changeSeason(season){
      this.user.season = season;
      this.updateUserSession();
      //TODO: eventBus emit changeSeason to update any data like itemList
    },
    changeYear(year){
      this.user.year = year;
      this.updateUserSession();
      //TODO: eventBus emit changeYear to update any data like itemList
    },
    navClass(navName){
      return navigate.selected == navName?'highlight':'';
    },
    goTo(view) {
      router.push(view);
    },
    logout() {
      document.cookie = "SID=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      this.user = {};
      this.securite.setUser({})
      this.yearName = null;
      this.goTo("/login");
    },
    userChangedListener(user){
      this.getUser();
    },
    showYearContextPop(){
      this.yearName = this.user.year.name;
    }
  },
  mounted(){
    EventBus.$on('userChanged', this.userChangedListener);
    this.getUser();
  }
};
</script>
<style lang="scss">
#app {
  font-family: "Avenir", Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
#nav {
  padding: 30px;
  a {
    font-weight: bold;
    color: #2c3e50;
    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
// Custom MPB
.pagebase {
  padding-left: 15px !important; 
  padding-right: 15px !important; 
  min-height: 580px
}
.view-default {
  background-color: #0000001f;
}
.view-supplier {
  background-color: #0000001f;
}
.view-component {
  background-color: #0000001f;
}
.view-item {
  background-color: #0000001f;
}
.view-customer {
  background-color: #0000001f;
}
.view-sale {
  background-color: #0000001f;
}
.view-purchase {
  background-color: #0000001f;
}
.view-receiving {
  background-color: #0000001f;
}
.view-schedule {
  background-color: #0000001f;
}
.view-production {
  background-color: #0000001f;
}
.view-shipment {
  background-color: #0000001f;
}
.view-item-return {
  background-color: #0000001f;
}
.view-invoice {
  background-color: #0000001f;
}
.page-link {
  background-color: transparent !important;
}
.popover {
  max-width: 1000px !important;
}
.highlight a {
  color: #ffc44c !important;
}
.center {
  margin: auto;
  width: 95%;
  padding-top: 40px;
}
.container-fluid{
  padding-left: 0px !important;
  padding-right: 0px !important;
}
.navbar {
  position: fixed !important;
  z-index: 1000;
  width: 100%;
}
.hr-text {
  margin-top: 0;
  margin-bottom: 0;
  line-height: 1em;
  position: relative;
  outline: 0;
  border: 0;
  color: black;
  text-align: center;
  height: 1.5em;
  opacity: 0.5;
  &:before {
    content: "";
    // use the linear-gradient for the fading effect
    // use a solid background color for a solid bar
    background: linear-gradient(to right, transparent, #818078, transparent);
    position: absolute;
    left: 0;
    top: 50%;
    width: 100%;
    height: 1px;
  }
  &:after {
    content: attr(data-content);
    position: relative;
    display: inline-block;
    color: black;

    padding: 0 0.5em;
    line-height: 1.5em;
    // this is really the only tricky part, you need to specify the background color of the container element...
    color: #818078;
    background-color: #fcfcfa;
  }
}
// Overwrides BV
.row {
  text-align: left;
}
.invalid {
  border-color: red !important;
}
.top-label {
  margin-bottom: 0px;
  font-size: 12px;
}
#nprogress .spinner {
    padding-left: 50%;
    padding-right: 50%;
    padding-top: 10%;
    padding-bottom: 30%;
}
#nprogress .spinner-icon {
  width: 40px !important;
  height: 40px !important;
}
#nprogress .bar {
    height: 5px !important;
}
.status-red {
  color: #b91b02f7 !important
}
.status-blue {
  color: #0219b9f7 !important
}
.status-green {
  color: #0eb902f7 !important
}
.status-yellow {
  color: #9db902f7 !important
}
.status-black {
  color: #383a32f7 !important
}
.status-purple {
  color: #e42f9cf7 !important
}
.name-sm {
  font-size: 11px; 
  margin-top: -5px;
  text-overflow: ellipsis; 
  width: 100px; 
  overflow: hidden; 
  white-space: nowrap;
}
.name-md {
  font-size: 11px; 
  margin-top: -5px;
  text-overflow: ellipsis; 
  width: 150px; 
  overflow: hidden; 
  white-space: nowrap;
}
.name-lg {
  font-size: 11px; 
  margin-top: -5px;
  text-overflow: ellipsis; 
  width: 200px; 
  overflow: hidden; 
  white-space: nowrap;
}
.fade-out {
  position: absolute;
  color: #c15a1d;
  font-weight: bold;
  font-size: 40px;
  top: 150px;
  left: 41%;
  text-align: center;
    -webkit-animation: fadeinout 8s linear forwards;
    animation: fadeinout 8s linear forwards;
}
@-webkit-keyframes fadeinout {
  0%,100% { opacity: 0; }
  50% { opacity: 1; }
}

@keyframes fadeinout {
  0%,100% { opacity: 0; }
  50% { opacity: 1; }
}
</style>
