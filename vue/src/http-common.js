import axios from "axios";
import httpUtils from "./httpUtils";
import router from "./router";
import NProgress from 'nprogress';

const http = axios.create({
  baseURL: httpUtils.baseUrl
});

http.interceptors.request.use(
  config => {
    NProgress.start();
    return config;
  },
  error => {
    alert("There was an error in request!\n" + error+"\n"+error.response.data.message);
    return Promise.reject(error);
  }
);

http.interceptors.response.use(
  response => {
    NProgress.done();
    return response;
  },
  error => {
    if (error.response.status === 401) { //Authorization and authentication.
      router.push("/login");
    } else if (error.response.status === 409){ //Conflict, validation, etc.
      alert(error.response.data.message);
    } else {
      alert("There was an error in response!\n" + error+"\n"+error.response.data.message);
    }
    NProgress.done();
    return Promise.reject(error);
  }
);

export default http;
