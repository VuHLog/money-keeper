package com.vuhlog.money_keeper.constants;

import lombok.Getter;

@Getter
public enum TransferType {
    NORMAL("Thông thường"),
    TRANSFER("Chuyển khoản"),
    ;

    private final String type;

    TransferType(String type) {
        this.type = type;
    }
}
