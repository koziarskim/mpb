import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userContext: {
      user: {},
      hasRole(code) {
        var roleFound = false;
        if (!this.user.roles) {
          return false;
        }
        this.user.roles.forEach(role => {
          if (role.code == "ADMIN") {
            roleFound = true;
            return;
          }
          if (role.code === code) {
            roleFound = true;
            return;
          }
        });
        return roleFound;
      },
      hasRoles(roles) {
        var roleFound = false;
        roles.forEach(role => {
          if (this.hasRole(role)) {
            roleFound = true;
            return;
          }
        });
        return roleFound;
      }
    }
  },
  getters: {
    userContext: state => {
      // Vuex's state is not persisted on refresh. So, we need to store in localStorage.
      if (!state.userContext.user.id) {
        state.userContext.user = JSON.parse(
          window.localStorage.getItem("user")
        );
      }
      return state.userContext;
    }
  },
  mutations: {
    CHANGE_USER: (state, user) => {
      state.userContext.user = user;
      window.localStorage.setItem("user", JSON.stringify(user));
    }
  },
  actions: {
    changeUser: (state, user) => {
      state.commit("CHANGE_USER", user);
    }
  }
});
