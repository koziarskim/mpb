import router from "./router";

export default {
  baseUrl: "http://marcin/mpb-api/api/", //This has to in sync with domain for CORS and Cookie to work.
  goTo: function(view) {
    return router.push(view);
  }
};
