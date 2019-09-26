import moment from "moment";

export default {
  secondsToTime(seconds) {
    var duration = moment.duration(seconds, "seconds");
    var hours = duration.hours() + duration.days() * 24;
    var mins = duration.minutes();
    var secs = duration.seconds();
    return this.pad(hours) + ":" + this.pad(mins) + ":" + this.pad(secs);
  },
  pad(number) {
    return number.toString().length == 1 ? "0" + number : number;
  }
};
