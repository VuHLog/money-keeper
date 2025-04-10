package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.response.ReportExpenseRevenueResponse;

public interface ReportService {
    ReportExpenseRevenueResponse getTotalExpenseRevenueByMonthOrYear(Integer month, Integer year);
}
