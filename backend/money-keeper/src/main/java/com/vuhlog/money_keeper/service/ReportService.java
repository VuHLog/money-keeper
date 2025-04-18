package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.request.ReportCategoryResponse;
import com.vuhlog.money_keeper.dto.request.TotalExpenseRevenueRequest;
import com.vuhlog.money_keeper.dto.response.TotalExpenseRevenueResponse;
import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseByExpenseLimit;

import java.util.List;

public interface ReportService {
    TotalExpenseRevenueResponse getTotalExpenseRevenueByTimeOption(TotalExpenseRevenueRequest req);

    List<ReportCategoryResponse> getTotalExpenseByTimeOptionAndCategory(TotalExpenseRevenueRequest req);

    List<ReportCategoryResponse> getTotalRevenueByTimeOptionAndCategory(TotalExpenseRevenueRequest req);

    TotalExpenseByExpenseLimit getTotalExpenseByExpenseLimit(String expenseLimitId, String startDate, String endDate);
}
