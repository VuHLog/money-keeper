package com.vuhlog.money_keeper.constants;

import lombok.Getter;

@Getter
public enum TimeOptionCurrentFinanceType {
    PRESENT("Hiện tại"),
    MONTH("Tháng"),
    QUARTER("Quý"),
    YEAR("Năm"),
    OPTIONAL("Tùy chọn")
    ;

    private final String type;

    TimeOptionCurrentFinanceType(String type) {
        this.type = type;
    }
}
