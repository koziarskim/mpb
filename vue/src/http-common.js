import axios from "axios";
import httpUtils from "./httpUtils";

export default axios.create({
  baseURL: httpUtils.baseUrl,
  headers: {
    "Content-type": "application/json",
    "Access-Control-Allow-Origin": "http://localhost:8081"
  }
});
