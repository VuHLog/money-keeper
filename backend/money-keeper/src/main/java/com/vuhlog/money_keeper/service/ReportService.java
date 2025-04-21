package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.ExpenseRevenueSituation;
import com.vuhlog.money_keeper.dto.request.ReportCategoryResponse;
import com.vuhlog.money_keeper.dto.request.TotalExpenseRevenueRequest;
import com.vuhlog.money_keeper.dto.response.TotalExpenseRevenueResponse;
import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseByExpenseLimit;
import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseRevenueByOptional;
import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseRevenueByPresent;
import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseRevenueForExpenseRevenueSituation;

import java.util.List;

public interface ReportService {
    TotalExpenseRevenueResponse getTotalExpenseRevenueByTimeOption(TotalExpenseRevenueRequest req);

    List<TotalExpenseRevenueForExpenseRevenueSituation> getTotalExpenseRevenueForExpenseRevenueSituation(ExpenseRevenueSituation req);

    List<ReportCategoryResponse> getTotalExpenseByTimeOptionAndCategory(TotalExpenseRevenueRequest req);

    List<ReportCategoryResponse> getTotalRevenueByTimeOptionAndCategory(TotalExpenseRevenueRequest req);

    TotalExpenseByExpenseLimit getTotalExpenseByExpenseLimit(String expenseLimitId, String startDate, String endDate);

    TotalExpenseRevenueByPresent getReportExpenseRevenueByPresent(ExpenseRevenueSituation req);

    TotalExpenseRevenueByOptional getReportExpenseRevenueByOptional(ExpenseRevenueSituation req);
}
