package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.ExpenseRegularRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseRegularResponse;

import java.util.List;

public interface ExpenseRegularService {
    List<ExpenseRegularResponse> getAllMyExpenseRegular(String dictionaryBucketPaymentId);
    ExpenseRegularResponse createExpenseRegular(ExpenseRegularRequest request);
    void deleteExpenseRegular(String id);
    ExpenseRegularResponse updateExpenseRegular(String id, ExpenseRegularRequest request);
    ExpenseRegularResponse getExpenseRegularById(String id);
}
