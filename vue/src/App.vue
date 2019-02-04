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
                </b-navbar-nav>
                <!-- Right aligned nav items -->
                <b-navbar-nav class="ml-auto">
                    <b-nav-item-dropdown right>
                        <!-- Using button-content slot -->
                        <template slot="button-content">
                        <em>{{user}}</em>
                        </template>
                        <b-dropdown-item v-on:click="goTo('/')">Profile</b-dropdown-item>
                        <b-dropdown-item href="#">Signout</b-dropdown-item>
                    </b-nav-item-dropdown>
                </b-navbar-nav>
            </b-collapse>
        </b-navbar>
        <router-view />
    </div>
</template>

<script>
import http from "./http-common";
import router from "./router";
export default {
  name: "app",
  data() {
    return {
      user: "Marcin Koziarski",
      customers: []
    };
  },
  methods: {
    /* eslint-disable no-console */
    goTo(view){
        router.push(view)
    },
    retrieveCustomers() {
      http
        .get("/customers")
        .then(response => {
          if (response.data.length > 0) {
            this.user = response.data[0].name;
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
</style>
