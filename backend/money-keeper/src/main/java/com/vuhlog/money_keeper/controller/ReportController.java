package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.ReportExpenseRevenueResponse;
import com.vuhlog.money_keeper.dto.response.TotalExpenseRevenueResponse;
import com.vuhlog.money_keeper.service.ReportService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/total-expense-revenue")
    public ApiResponse<TotalExpenseRevenueResponse> getTotalExpenseRevenueByTimeOption(ExpenseRevenueHistoryRequest req) {
        return ApiResponse.<TotalExpenseRevenueResponse>builder()
                .result(reportService.getTotalExpenseRevenueByTimeOption(req))
                .build();
    }
}
