package com.vuhlog.money_keeper.dto.response;

import com.vuhlog.money_keeper.entity.DictionaryExpense;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseRegularResponse {
    private String id;
    private Long amount;
    private String location;
    private String interpretation;
    private String expenseDate;
    private String createdDate;
    private String modifiedDate;
    private DictionaryExpenseResponse dictionaryBucketPayment;
    private DictionaryExpense dictionaryExpense;
    private TripEventResponse tripEvent;
    private BeneficiaryResponse beneficiary;
}
