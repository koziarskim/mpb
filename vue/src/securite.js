import store from "./store.js";

export default {
  hasRole(roles) {
    var found = false;
    roles.forEach(role => {
      if (store.getters.userContext.hasRole(role)) {
        found = true;
      }
    });
    return found;
  },
  getUser() {
    return store.getters.userContext.user;
  }
};
