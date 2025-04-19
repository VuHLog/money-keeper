import { defineStore } from "pinia";
import { jwtDecode } from "jwt-decode";
import {base} from "@/apis/ApiService.js"
import { formatDate } from "@/utils/DateUtil.js";
import { AccountType } from "@/constants/AccountType.js";

export const useNotificationStore = defineStore("notification", {
  state: () => {
    return {
        pageNumber: 1,
        pageSize: 4,
        totalElements: 0,
        totalPages: 0,
        notifications: [],
        countNewNotifications: 0,
    };
  },
  getters: {},
  actions: {
    async changPageNumber(pageNumber) {
        this.pageNumber = pageNumber;
        await this.getNotifications();
    },
    async changePageSize(pageSize) {
        this.pageSize = pageSize;
        await this.getNotifications();
    },
    async getNotifications() {
      let response = null;
      await base
        .get("/notification?pageNumber=" + (this.pageNumber - 1) + "&pageSize=" + this.pageSize)
        .then((res) => {
            response = res.result;
            this.totalPages = response.totalPages;
            this.totalElements = response.totalElements;
            this.notifications = response.content;
            this.pageSize = response.pageable.pageSize;
            this.pageNumber = response.pageable.pageNumber + 1;
        })
        .catch((err) => {
          console.log(err);
        });
    },
  },
});
