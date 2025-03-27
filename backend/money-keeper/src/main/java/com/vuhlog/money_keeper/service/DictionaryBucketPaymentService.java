package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRevenueRegularResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DictionaryBucketPaymentService {
    DictionaryBucketPaymentResponse createDictionaryBucketPayment(DictionaryBucketPaymentRequest request, String userId);

    DictionaryBucketPaymentResponse updateDictionaryBucketPayment(String id, DictionaryBucketPaymentRequest request);

    void deleteDictionaryBucketPayment(String id);

    DictionaryBucketPaymentResponse getDictionaryBucketPaymentById(String id);

    List<DictionaryBucketPaymentResponse> getAllDictionaryBucketPayment(String userId);

    Page<ExpenseRevenueRegularResponse> getAllExpenseRevenueRegularsByDate(String userId, Pageable pageable, Integer pageNumber,Integer pageSize, String sort, String timeOption, String startDate, String endDate);

    Long getTotalExpenseByBucketPaymentId(String bucketPaymentId, String timeOption);
    Long getTotalRevenueByBucketPaymentId(String bucketPaymentId, String timeOption);
    Long getBalanceByBucketPaymentId(String bucketPaymentId);
}
