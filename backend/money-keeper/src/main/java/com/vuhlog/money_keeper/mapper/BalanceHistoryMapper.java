package com.vuhlog.money_keeper.mapper;

import com.vuhlog.money_keeper.dto.response.BalanceHistoryResponse;
import com.vuhlog.money_keeper.entity.BalanceHistory;
import com.vuhlog.money_keeper.util.TimestampUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface BalanceHistoryMapper {
    @Named("timestampToString")
    @Mapping(target = "createdAt", expression = "java(timestampToString(balanceHistory.getCreatedAt()))")
    BalanceHistoryResponse toBalanceHistoryResponse(BalanceHistory balanceHistory);

    default Timestamp stringToTimestamp(String dateString) {
        return TimestampUtil.stringToTimestamp(dateString);
    }

    default String timestampToString(Timestamp timestamp) {
        return TimestampUtil.timestampToString(timestamp);
    }
}
