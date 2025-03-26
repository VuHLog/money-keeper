import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode";
import {base} from "@/apis/ApiService.js"
import TokenService from "@/service/TokenService.js"

const token = TokenService.getSessionAccessToken();
const tokenDecoded = token? jwtDecode(token): {};

export const useDictionaryBucketPaymentStore = defineStore("dictionaryBucketPayment", {
  state: () => {
    return {
      totalExpense: 0,
      totalRevenue: 0,
    };
  },
  getters: {
  },
  actions: {
    async getBucketPaymentById(id){
      let response = null;
      await base.get("/dictionary-bucket-payment/"+ id).then((res) => {
        response = res.result;
      })
      .catch((error) => console.log(error)
      )
      return response;
    },
    async getTotalExpenseByBucketPaymentId(id, timeOption){
      let response = null;
      await base.get("/dictionary-bucket-payment/"+ id + "/total-expense?timeOption=" + timeOption).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getTotalRevenueByBucketPaymentId(id, timeOption){
      let response = null;
      await base.get("/dictionary-bucket-payment/"+ id + "/total-revenue?timeOption=" + timeOption).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getBalanceByBucketPaymentId(id, timeOption){
      let response = null;
      await base.get("/dictionary-bucket-payment/"+ id + "/balance?timeOption=" + timeOption).then((res) => {
        response = res.result;
      })
      return response;
    },
  },
});
