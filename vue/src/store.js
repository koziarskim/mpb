import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    userContext: {}
  },
  getters: {
    userContext: state => {
      // Vuex's state is not persisted on refresh. So, we need to store in localStorage.
      if (!state.userContext.id) {
        state.userContext = JSON.parse(
          window.localStorage.getItem("userContext")
        );
      }
      return state.userContext;
    }
  },
  mutations: {
    CHANGE_USER_CONTEXT: (state, userContext) => {
      state.userContext = userContext;
      window.localStorage.setItem("userContext", JSON.stringify(userContext));
    }
  },
  actions: {
    changeUserContext: (state, userContext) => {
      state.commit("CHANGE_USER_CONTEXT", userContext);
    }
  }
});
