<template>
    <div id="app">
        <b-navbar toggleable="md" type="dark" variant="dark">
            <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
            <b-navbar-brand href="#" v-on:click="goTo('/home')">Home</b-navbar-brand>
            <b-collapse is-nav id="nav_collapse">
                <b-navbar-nav>
                    <b-nav-item v-on:click="goTo('/componentList')">Component</b-nav-item>
                    <b-nav-item v-on:click="goTo('/itemList')">Item</b-nav-item>
                    <b-nav-item v-on:click="goTo('/purchaseList')">Purchase</b-nav-item>
                </b-navbar-nav>
                <!-- Right aligned nav items -->
                <b-navbar-nav class="ml-auto">
                    <b-nav-item-dropdown right>
                        <!-- Using button-content slot -->
                        <template slot="button-content">
                        <!-- <em>{{userName}}</em> -->
                        <em>Marcin Koziarski</em>
                        </template>
                        <b-dropdown-item v-on:click="goTo('/')">Profile</b-dropdown-item>
                        <b-dropdown-item href="#">Signout</b-dropdown-item>
                    </b-nav-item-dropdown>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <div class="center">
            <router-view />
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
    return {
    };
  },
  computed: {
    userName: function() {
      return this.$store.getters.getUserName;
    }
  },
  methods: {
    changeUserName: function(user_name) {
      this.$store.dispatch("changeUserName", user_name);
    },
    /* eslint-disable no-console */
    goTo(view) {
      router.push(view);
    },
    /* eslint-enable no-console */
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
  opacity: .5;
  &:before {
    content: '';
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

    padding: 0 .5em;
    line-height: 1.5em;
    // this is really the only tricky part, you need to specify the background color of the container element...
    color: #818078;
    background-color: #fcfcfa;
  }
}
// Overwrides BV
.row{
    text-align: left;
}
.invalid{
    border-color: red !important;
}
.top-label{
    margin-bottom: 0px;
    font-size: 12px;
}
</style>
