package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.ExpenseLimitRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseLimitResponse;

import java.util.List;

public interface ExpenseLimitService {
    ExpenseLimitResponse getExpenseLimitById(String id);
    List<ExpenseLimitResponse> getAllExpenseLimit();
    ExpenseLimitResponse createExpenseLimit(ExpenseLimitRequest request);
    ExpenseLimitResponse updateExpenseLimit(String id, ExpenseLimitRequest request);
    void deleteExpenseLimit(String id);
}
