<template>
  <div id="app">
    <b-navbar toggleable="md" type="dark" variant="dark">
      <b-collapse is-nav id="nav_collapse">
        <b-navbar-nav v-if="!hideNavBar()">
          <b-nav-item v-on:click="goTo('/home')">Home</b-nav-item>
          <b-nav-item v-on:click="goTo('/supplierList')">Supplier</b-nav-item>
          <b-nav-item v-on:click="goTo('/componentList')">Component</b-nav-item>
          <b-nav-item v-on:click="goTo('/itemList')">Item</b-nav-item>
          <b-nav-item v-on:click="goTo('/customerList')">Customer</b-nav-item>
          <b-nav-item v-on:click="goTo('/saleList')">Sale</b-nav-item>
          <b-nav-item v-on:click="goTo('/purchaseList')">Purchase</b-nav-item>
          <b-nav-item v-on:click="goTo('/receivingList')">Receiving</b-nav-item>
          <b-nav-item v-on:click="goTo('/shipmentList')">Shipment</b-nav-item>
          <b-nav-item v-on:click="goTo('/schedule')">Schedule</b-nav-item>
		      <b-nav-item v-on:click="goTo('/productionLineList')">Production</b-nav-item>
        </b-navbar-nav>
        <b-navbar-nav class="ml-auto">
          <span v-if="hideNavBar()" style="color: white; margin-right: 455px; padding-top: 7px;">Marketplace Brands</span>
          <img v-if="hideNavBar()" style="width: 40px; height: 40px; margin-right: 650px" src="./assets/mpb-logo.jpg">
          <b-nav-item-dropdown v-if="!hideNavBar()" right>
            <template slot="button-content">
              <em>{{getUserContext().user.fullName}}</em>
            </template>
            <b-dropdown-item @click="goTo('/')">Profile</b-dropdown-item>
            <b-dropdown-item v-if="hasRole(['ADMIN'])" @click="goTo('/users')">Manage Users</b-dropdown-item>
            <b-dropdown-item @click="logout()">Signout</b-dropdown-item>
          </b-nav-item-dropdown>
        </b-navbar-nav>
      </b-collapse>
    </b-navbar>
    <div class="center">
      <router-view :key="$route.fullPath"></router-view>
    </div>
  </div>
</template>

<script>
import http from "./http-common";
import router from "./router";
import store from "./store.js";

export default {
  name: "app",
  data() {
    return {};
  },
  computed: {},
  methods: {
    hideNavBar() {
      return this.$store.getters.userContext.user.id == null;
    },
    getUserContext() {
      return this.$store.getters.userContext
	},
	hasRole(roles){
		var found = false;
		roles.forEach(role=>{
			if(this.$store.getters.userContext.hasRole(role)){
				found = true;
			}
		})
		return found;
	},
    goTo(view) {
      router.push(view);
    },
    logout() {
      document.cookie = "SID=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      this.$store.dispatch("changeUser", {});
      this.goTo("/login");
    }
  },
  mounted(){
      if(!this.getUserContext().user){
          this.goTo("/login");
      }
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
.center {
  margin: auto;
  width: 95%;
  padding: 10px;
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
</style>
