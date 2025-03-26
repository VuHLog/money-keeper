package com.vuhlog.money_keeper.service;

import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;

import java.util.List;

public interface DictionaryBucketPaymentService {
    DictionaryBucketPaymentResponse createDictionaryBucketPayment(DictionaryBucketPaymentRequest request, String userId);

    DictionaryBucketPaymentResponse updateDictionaryBucketPayment(String id, DictionaryBucketPaymentRequest request);

    void deleteDictionaryBucketPayment(String id);

    DictionaryBucketPaymentResponse getDictionaryBucketPaymentById(String id);

    List<DictionaryBucketPaymentResponse> getAllDictionaryBucketPayment(String userId);

    Long getTotalExpenseByBucketPaymentId(String bucketPaymentId, String timeOption);
    Long getTotalRevenueByBucketPaymentId(String bucketPaymentId, String timeOption);
    Long getBalanceByBucketPaymentId(String bucketPaymentId);
}
