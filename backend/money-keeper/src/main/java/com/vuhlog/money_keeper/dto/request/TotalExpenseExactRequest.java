package com.vuhlog.money_keeper.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TotalExpenseExactRequest {
    private String timeOption;
    private String bucketPaymentIds;
    private String categoriesId;
    private String date;
    private Integer month;
    private Integer year;
}
