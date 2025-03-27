package com.vuhlog.money_keeper.mapper;

import com.vuhlog.money_keeper.dto.request.ExpenseRegularRequest;
import com.vuhlog.money_keeper.dto.request.TransferRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseRegularResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRevenueRegularResponse;
import com.vuhlog.money_keeper.entity.ExpenseRegular;
import com.vuhlog.money_keeper.util.TimestampUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.sql.Timestamp;

@Mapper(componentModel = "spring")
public interface ExpenseRegularMapper {
    @Named("stringToTimestamp")
    @Mapping(target = "expenseDate", expression = "java(stringToTimestamp(request.getExpenseDate()))")
    @Mapping(target = "dictionaryBucketPayment", ignore = true)
    @Mapping(target = "dictionaryExpense", ignore = true)
    @Mapping(target = "tripEvent", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    ExpenseRegular toExpenseRegular(ExpenseRegularRequest request);

    @Named("stringToTimestamp")
    @Mapping(target = "expenseDate", expression = "java(stringToTimestamp(request.getExpenseDate()))")
    @Mapping(target = "dictionaryBucketPayment", ignore = true)
    @Mapping(target = "receivedAccount", ignore = true)
    ExpenseRegular toExpenseRegularFromTransferRequest(TransferRequest request);

    @Named("timestampToString")
    @Mapping(target = "expenseDate", expression = "java(timestampToString(expenseRegular.getExpenseDate()))")
    ExpenseRegularResponse toExpenseRegularResponse(ExpenseRegular expenseRegular);

    @Named("timestampToString")
    @Mapping(target = "expenseDate", expression = "java(timestampToString(expenseRegular.getExpenseDate()))")
    @Mapping(target = "type", expression = "java('expense')")
    ExpenseRevenueRegularResponse toExpenseRevenueRegularResponse(ExpenseRegular expenseRegular);

    @Named("stringToTimestamp")
    @Mapping(target = "expenseDate", expression = "java(stringToTimestamp(request.getExpenseDate()))")
    @Mapping(target = "dictionaryBucketPayment", ignore = true)
    @Mapping(target = "dictionaryExpense", ignore = true)
    @Mapping(target = "tripEvent", ignore = true)
    @Mapping(target = "beneficiary", ignore = true)
    void updateExpenseRegularFromRequest(ExpenseRegularRequest request, @MappingTarget ExpenseRegular expenseRegular);

    default Timestamp stringToTimestamp(String dateString) {
        return TimestampUtil.stringToTimestamp(dateString);
    }

    default String timestampToString(Timestamp timestamp) {
        return TimestampUtil.timestampToString(timestamp);
    }
}
