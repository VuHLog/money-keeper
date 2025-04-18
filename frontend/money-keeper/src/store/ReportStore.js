import { defineStore } from "pinia";
import {base} from "@/apis/ApiService.js"
import { formatDate } from "@/utils/DateUtil.js";

export const useReportStore = defineStore("report", {
  state: () => {
    return {
      totalExpense: 0,
      totalRevenue: 0,
    };
  },
  getters: {
  },
  actions: {
    async getTotalExpenseRevenueByBucketPaymentIdAndTimeOption(bucketPaymentIds, timeOption, startDate, endDate){
      let response = null;
      const request = {
        bucketPaymentIds: bucketPaymentIds,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
      }
      await base.get("/report/total-expense-revenue", request).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getTotalExpenseByBucketPaymentIdAndTimeOptionAndCategory(bucketPaymentIds, timeOption, startDate, endDate, isSelectAllBucketPayment = false){
      let response = null;
      const request = {
        bucketPaymentIds: bucketPaymentIds,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
        isSelectAllBucketPayment: isSelectAllBucketPayment
      }
      await base.get("/report/total-expense-by-category", request).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getTotalRevenueByBucketPaymentIdAndTimeOptionAndCategory(bucketPaymentIds, timeOption, startDate, endDate, isSelectAllBucketPayment = false){
      let response = null;
      const request = {
        bucketPaymentIds: bucketPaymentIds,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate),
        isSelectAllBucketPayment: isSelectAllBucketPayment
      }
      await base.get("/report/total-revenue-by-category", request).then((res) => {
        response = res.result;
      })
      return response;
    },
    async getTotalExpenseByExpenseLimit(expenseLimitId, startDate, endDate){
      let response = null;
      await base.get("/report/total-expense-by-expense-limit?startDate=" + formatDate(startDate) + "&endDate=" + formatDate(endDate) + "&expenseLimitId=" + expenseLimitId).then((res) => {
        response = res.result;
      })
      return response;
    }
  },
});
