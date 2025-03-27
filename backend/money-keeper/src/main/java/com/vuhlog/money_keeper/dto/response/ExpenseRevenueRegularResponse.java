package com.vuhlog.money_keeper.dto.response;

import com.vuhlog.money_keeper.entity.DictionaryExpense;
import com.vuhlog.money_keeper.entity.DictionaryRevenue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpenseRevenueRegularResponse {
    private String id;
    private Long amount;
    private String location;
    private String interpretation;
    private String expenseDate;
    private String revenueDate;
    private String createdDate;
    private String modifiedDate;
    private String transferType;
    private DictionaryBucketPaymentResponse receivedAccount;
    private DictionaryBucketPaymentResponse dictionaryBucketPayment;
    private DictionaryExpense dictionaryExpense;
    private DictionaryRevenue dictionaryRevenue;
    private String type;
    private TripEventResponse tripEvent;
    private BeneficiaryResponse beneficiary;
    private CollectMoneyWhoResponse collectMoneyWho;
}
