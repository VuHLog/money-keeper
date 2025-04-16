package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.ExpenseLimitRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseLimitResponse;
import com.vuhlog.money_keeper.service.ExpenseLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/expense-limit")
@RequiredArgsConstructor
public class ExpenseLimitController {
    private final ExpenseLimitService expenseLimitService;

    @GetMapping("/{id}")
    public ApiResponse<ExpenseLimitResponse> getExpenseLimitById(@PathVariable String id) {
        return ApiResponse.<ExpenseLimitResponse>builder()
                .result(expenseLimitService.getExpenseLimitById(id))
                .build();
    }

    @PostMapping()
    public ApiResponse<ExpenseLimitResponse> createExpenseLimit(@RequestBody ExpenseLimitRequest request) {
        return ApiResponse.<ExpenseLimitResponse>builder()
                .result(expenseLimitService.createExpenseLimit(request))
                .build();
    }
}
