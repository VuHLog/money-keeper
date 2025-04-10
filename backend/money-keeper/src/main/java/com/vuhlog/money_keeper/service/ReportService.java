package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.response.TotalExpenseRevenueResponse;

public interface ReportService {
    TotalExpenseRevenueResponse getTotalExpenseRevenueByTimeOption(ExpenseRevenueHistoryRequest req);
}
