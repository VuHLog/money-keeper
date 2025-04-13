package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.request.ReportCategoryResponse;
import com.vuhlog.money_keeper.dto.request.TotalExpenseRevenueRequest;
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

import java.util.List;

@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/total-expense-revenue")
    public ApiResponse<TotalExpenseRevenueResponse> getTotalExpenseRevenueByTimeOption(TotalExpenseRevenueRequest req) {
        return ApiResponse.<TotalExpenseRevenueResponse>builder()
                .result(reportService.getTotalExpenseRevenueByTimeOption(req))
                .build();
    }

    @GetMapping("/total-expense-by-category")
    public ApiResponse<List<ReportCategoryResponse>> getTotalExpenseByTimeOptionAndCategory(TotalExpenseRevenueRequest req) {
        return ApiResponse.<List<ReportCategoryResponse>>builder()
                .result(reportService.getTotalExpenseByTimeOptionAndCategory(req))
                .build();
    }

    @GetMapping("/total-revenue-by-category")
    public ApiResponse<List<ReportCategoryResponse>> getTotalRevenueByTimeOptionAndCategory(TotalExpenseRevenueRequest req) {
        return ApiResponse.<List<ReportCategoryResponse>>builder()
                .result(reportService.getTotalRevenueByTimeOptionAndCategory(req))
                .build();
    }
}
