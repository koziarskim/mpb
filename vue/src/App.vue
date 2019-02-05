<template>
    <div id="app">
        <b-navbar toggleable="md" type="dark" variant="dark">
            <b-navbar-toggle target="nav_collapse"></b-navbar-toggle>
            <b-navbar-brand href="#" v-on:click="goTo('/home')">Home</b-navbar-brand>
            <b-collapse is-nav id="nav_collapse">
                <b-navbar-nav>
                    <b-nav-item v-on:click="goTo('/about')">About</b-nav-item>
                    <b-nav-item v-on:click="goTo('/customers')">Customers</b-nav-item>
                    <b-nav-item v-on:click="goTo('/add')">Add</b-nav-item>
                    <b-nav-item v-on:click="goTo('/search')">Search</b-nav-item>
                    <b-nav-item v-on:click="goTo('/editItem')">Items</b-nav-item>
                    <b-nav-item v-on:click="goTo('/editComponent')">Components</b-nav-item>
                </b-navbar-nav>
                <!-- Right aligned nav items -->
                <b-navbar-nav class="ml-auto">
                    <b-nav-item-dropdown right>
                        <!-- Using button-content slot -->
                        <template slot="button-content">
                        <em>{{userName}}</em>
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
      customers: []
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
    retrieveCustomers() {
      http
        .get("/customers")
        .then(response => {
          if (response.data.length > 0) {
            this.changeUserName(response.data[0].name);
          } else {
            this.changeUserName("Admin");
          }
          console.log(response.data);
        })
        .catch(e => {
          console.log(e);
        });
    },
    refreshList() {
      this.retrieveCustomers();
    }
    /* eslint-enable no-console */
  },
  updated() {
    this.retrieveCustomers();
  },
  mounted() {
    this.retrieveCustomers();
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
.center {
  margin: auto;
  width: 75%;
  padding: 10px;
}
// Overwrides BV
.row{
    padding: 4px;
    text-align: left;
}
</style>
