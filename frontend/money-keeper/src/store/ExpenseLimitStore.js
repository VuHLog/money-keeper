import { defineStore } from "pinia";
import {base} from "@/apis/ApiService.js"
import { formatDate } from "@/utils/DateUtil.js";

export const useExpenseLimitStore = defineStore("expenseLimit", {
  state: () => {
    return {
      expenseLimit: null,
    };
  },
  getters: {
  },
  actions: {
    async getExpenseLimitByExpenseLimitId(expenseLimitId){
      let response = null;
      await base.get(`/expense-limit/${expenseLimitId}`).then((res) => {
        response = res.result;
      })
      return response;
    },
  },
});
