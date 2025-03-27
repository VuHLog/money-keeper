package com.vuhlog.money_keeper.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BucketPaymentHistoryResponse {
    private String date;
    private List<ExpenseRevenueRegularResponse> expenseRevenueRegulars;
}
