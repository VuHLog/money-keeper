import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode";
import {base} from "@/apis/ApiService.js"
import { formatDate } from "@/utils/DateUtil.js";
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
    async getTotalExpenseByBucketPaymentId(id, timeOption, startDate, endDate){
      let response = null;
      const request = {
        bucketPaymentId: id,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
      }
      await base.get("/dictionary-bucket-payment/"+ id + "/total-expense", request).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getTotalRevenueByBucketPaymentId(id, timeOption, startDate, endDate){
      let response = null;
      const request = {
        bucketPaymentId: id,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
      }
      await base.get("/dictionary-bucket-payment/"+ id + "/total-revenue", request).then((res) => {
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
    async getTransactionHistoryByBucketPaymentId(id, timeOption, startDate, endDate){
      let response = null;
      const request = {
        bucketPaymentId: id,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
      }
      await base.get("/dictionary-bucket-payment/"+ id + "/transaction-history", request).then((res) => {
        response = res.result;
      })
      return response;
    },
  },
});
