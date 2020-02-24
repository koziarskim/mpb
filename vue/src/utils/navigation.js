import router from "../router";
import securite from "../securite";

export default {
  selected: "home",
  viewClass: "",
  baseUrl: process.env.VUE_APP_API_BASE_URL,
  getSeason() {
    var season = JSON.parse(window.localStorage.getItem("season"));
    if (!season) {
      season = { id: 1, name: "Christmas" };
    }
    return season;
  },
  getYear() {
    var year = JSON.parse(window.localStorage.getItem("year"));
    if (!year) {
      year = { id: 1, name: "2020" };
    }
    return year;
  },
  setSeason(season) {
    window.localStorage.setItem("season", JSON.stringify(season));
  },
  setYear(year) {
    window.localStorage.setItem("year", JSON.stringify(year));
  },
  goTo(view) {
    return router.push(view);
  },
  goToItemEdit(item_id) {
    router.push("/itemEdit/" + item_id);
  },
  before(to, from, next) {
    if (!to.meta.roles) {
      this.goNext(to, from, next);
      return;
    }
    var foundRole = false;
    foundRole = securite.hasRole(to.meta.roles);
    if (foundRole) {
      this.goNext(to, from, next);
    } else {
      this.goNext(to, from, next, { path: "/AccessDenied" });
    }
  },
  goNext(to, from, next, path) {
    this.selected = to.meta.group;
    this.viewClass = to.meta.viewClass;
    if (path) {
      next(path);
    } else {
      next();
    }
  }
};
