package com.vuhlog.money_keeper.constants;

import lombok.Getter;

@Getter
public enum TimeOptionSpendingAnalysisType {
    DAY("Ngày"),
    MONTH("Tháng"),
    YEAR("Năm")
    ;

    private final String type;

    TimeOptionSpendingAnalysisType(String type) {
        this.type = type;
    }
}
