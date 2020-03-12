import session from "./utils/session";

export default {
  hasRole(codes) {
    var found = false;
    if (!this.getUser().roles) {
      return false;
    }
    this.getUser().roles.forEach(ur => {
      if (ur.code == "ADMIN") {
        found = true;
        return;
      }
      codes.forEach(code => {
        if (ur.code == code) {
          found = true;
          return;
        }
      });
    });
    return found;
  },
  validate(roles) {
    if (!this.hasRole(roles)) {
      alert("You have no permission for this action!");
      return false;
    }
    return true;
  },
  getUser() {
    return session.getUser();
  },
  setUser(user){
    session.setUser(user);
  }
};
