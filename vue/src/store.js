import Vue from "vue";
import Vuex from "vuex";

Vue.use(Vuex);

export default new Vuex.Store({
  state: {
    user_name: ""
  },
  getters: {
    getUserName: state => {
      return state.user_name;
    }
  },
  mutations: {
    CHANGE_USER_NAME: (state, user_name) => {
      state.user_name = user_name;
    }
  },
  actions: {
    changeUserName: (context, user_name) => {
      context.commit("CHANGE_USER_NAME", user_name);
    }
  }
});
