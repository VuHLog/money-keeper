package com.vuhlog.money_keeper.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SpendingAnalysisReportRequest {
    private String timeOption;
    private String bucketPaymentIds;
    private String categoriesId;
    private String startDate;
    private String endDate;
    private String startMonth;
    private String endMonth;
    private Integer startYear;
    private Integer endYear;
}
