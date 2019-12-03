import router from "../router";
import securite from "../securite";

export default {
  selected: 'home',
  baseUrl: process.env.VUE_APP_API_BASE_URL,
  goTo(view) {
    return router.push(view);
  },
  goToItemEdit(item_id) {
    router.push("/itemEdit/" + item_id);
  },
  before(to, from, next){
    if (!to.meta.roles) {
      this.goNext(to, from, next);
      return;
    }
    var foundRole = false;
    foundRole = securite.hasRole(to.meta.roles);
    if (foundRole) {
      this.goNext(to, from, next);
    } else {
      this.goNext(to, from, next, {path: "/AccessDenied"});
    }
  },
  goNext(to, from, next, path){
    this.selected = to.name;
    if(path){
      next(path);
    }else{
      next();
    }
  }
};
