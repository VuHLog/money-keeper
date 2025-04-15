package com.vuhlog.money_keeper.mapper;

import com.vuhlog.money_keeper.dto.request.ExpenseLimitRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseLimitResponse;
import com.vuhlog.money_keeper.entity.ExpenseLimit;
import com.vuhlog.money_keeper.util.TimestampUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface ExpenseLimitMapper {
    @Named("stringToTimestamp")
    @Mapping(target = "startDate", expression = "java(stringToTimestamp(req.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(stringToTimestamp(req.getEndDate()))")
    ExpenseLimit toExpenseLimit(ExpenseLimitRequest req);

    @Named("stringToTimestamp")
    @Mapping(target = "startDate", expression = "java(stringToTimestamp(req.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(stringToTimestamp(req.getEndDate()))")
    void updateExpenseLimit(@MappingTarget ExpenseLimit expenseLimit, ExpenseLimitRequest req);

    @Named("timestampToString")
    @Mapping(target = "startDate", expression = "java(timestampToString(expenseLimit.getStartDate()))")
    @Mapping(target = "endDate", expression = "java(timestampToString(expenseLimit.getEndDate()))")
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "bucketPayments", ignore = true)
    ExpenseLimitResponse toExpenseLimitResponse(ExpenseLimit expenseLimit);

    default Timestamp stringToTimestamp(String dateString) {
        return TimestampUtil.stringToTimestamp(dateString);
    }

    default String timestampToString(Timestamp timestamp) {
        return TimestampUtil.timestampToString(timestamp);
    }
}
