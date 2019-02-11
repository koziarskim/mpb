import router from "./router";

export default {
  goTo: function(view) {
    return router.push(view);
  }
};
