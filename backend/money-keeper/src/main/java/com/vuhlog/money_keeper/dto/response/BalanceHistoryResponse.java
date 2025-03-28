package com.vuhlog.money_keeper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceHistoryResponse {
    private String id;
    private String transactionId;
    private String transactionType;
    private long prevBalance;
    private long newBalance;
    private String createdAt;
    private DictionaryBucketPaymentResponse bucketPayment;
}
