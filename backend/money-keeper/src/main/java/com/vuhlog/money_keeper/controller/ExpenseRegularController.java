package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.ExpenseRegularRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRegularResponse;
import com.vuhlog.money_keeper.service.ExpenseRegularService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense-regular")
@RequiredArgsConstructor
public class ExpenseRegularController {
    private final ExpenseRegularService expenseRegularService;

    @GetMapping("")
    public ApiResponse<List<ExpenseRegularResponse>> getAllMyExpenseRegular(@RequestParam(name = "dictionaryBucketPaymentId", required = true) String dictionaryBucketPaymentId) {
        return ApiResponse.<List<ExpenseRegularResponse>>builder().result(expenseRegularService.getAllMyExpenseRegular(dictionaryBucketPaymentId)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ExpenseRegularResponse> getExpenseRegularById(@PathVariable String id) {
        return ApiResponse.<ExpenseRegularResponse>builder().result(expenseRegularService.getExpenseRegularById(id)).build();
    }

    @PostMapping("")
    public ApiResponse<ExpenseRegularResponse> createExpenseRegular(@RequestBody ExpenseRegularRequest req) {
        return ApiResponse.<ExpenseRegularResponse>builder().result(expenseRegularService.createExpenseRegular(req)).build();
    }

    @PutMapping("/{id}")
    public ApiResponse<ExpenseRegularResponse> updateExpenseRegular(@PathVariable String id, @RequestBody ExpenseRegularRequest req) {
        return ApiResponse.<ExpenseRegularResponse>builder().result(expenseRegularService.updateExpenseRegular(id, req)).build();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ApiResponse<String> deleteExpenseRegularById(@PathVariable String id) {
        expenseRegularService.deleteExpenseRegular(id);
        return ApiResponse.<String>builder().result("delete expense regular successfully").build();
    }
}
