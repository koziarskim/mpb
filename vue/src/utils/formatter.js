import moment from "moment";

export default {
  secondsToTime(seconds) {
		  var secs = moment.duration(seconds, 'seconds');
      return moment().startOf('day').seconds(secs).format('HH:mm:ss');
  }
};
