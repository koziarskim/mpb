import router from "./router";
import securite from "./securite";

export default {
  baseUrl: process.env.VUE_APP_API_BASE_URL,
  goTo: function(view) {
    return router.push(view);
  },
  getUrl(path){
    var query = "?yearContext=y"+securite.getUser().year.name;
    return decodeURI(this.baseUrl + path + query);
  }
};
