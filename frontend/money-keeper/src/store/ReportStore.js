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
    async getTotalExpenseRevenueByBucketPaymentIdAndTimeOption(id, timeOption, startDate, endDate){
      let response = null;
      const request = {
        bucketPaymentId: id,
        timeOption: timeOption,
        startDate: formatDate(startDate),
        endDate: formatDate(endDate)
      }
      await base.get("/report/total-expense-revenue", request).then((res) => {
        response = res.result;
      })
      return response;
    },
  },
});
