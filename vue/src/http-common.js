import axios from "axios";
import httpUtils from "./httpUtils";

// export default axios.create({
//   baseURL: httpUtils.baseUrl,
//   headers: {
//     "Content-type": "application/json",
//     "Access-Control-Allow-Origin": "http://localhost:8081"
//   }
// });

const http = axios.create({
  baseURL: httpUtils.baseUrl,
  headers: {
    "Content-type": "application/json",
    "Access-Control-Allow-Origin": "http://localhost:8081"
  }
});

http.interceptors.request.use(
  config => {
    console.log("interceptor request config...");
    // const token = store.token;
    // if (token) config.headers.Authorization = `Bearer ${token}`;
    return config;
  },
  error => {
    console.log("interceptor request error...");
    return Promise.reject(error);
  }
);

http.interceptors.response.use(
  response => {
    console.log("interceptor response success...");
    return response;
  },
  error => {
    console.log("interceptor response error...");
    return Promise.reject(error);
  }
);

export default http;
