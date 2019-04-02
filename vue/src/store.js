import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    user_name: "",
    pageContext: {}
  },
  getters: {
    getUserName: state => {
      return state.user_name;
    },
    pageContext: state => {
      return state.pageContext;
    }
  },
  mutations: {
    CHANGE_USER_NAME: (state, user_name) => {
      state.user_name = user_name;
    },
    CHANGE_PAGE_CONTEXT: (state, pageContext) => {
      state.pageContext = pageContext;
    }
  },
  actions: {
    changeUserName: (state, user_name) => {
      state.commit("CHANGE_USER_NAME", user_name);
    },
    changePageContext: (state, pageContext) => {
      state.commit("CHANGE_PAGE_CONTEXT", pageContext);
    }
  }
});
