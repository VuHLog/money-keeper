package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRevenueHistory;
import com.vuhlog.money_keeper.entity.Users;
import com.vuhlog.money_keeper.service.DictionaryBucketPaymentService;
import com.vuhlog.money_keeper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dictionary-bucket-payment")
@RequiredArgsConstructor
public class DictionaryBucketPaymentController {
    private final DictionaryBucketPaymentService dictionaryBucketPaymentService;
    private final UserService usersService;

    @GetMapping("")
    public ApiResponse<List<DictionaryBucketPaymentResponse>> getAllDictionaryBucketPayment() {
        Users user = usersService.getMyUserInfo();
        return ApiResponse.<List<DictionaryBucketPaymentResponse>>builder()
                .result(dictionaryBucketPaymentService.getAllDictionaryBucketPayment(user.getId()))
                .build();
    }

    @GetMapping("/{id}")
    public ApiResponse<DictionaryBucketPaymentResponse> getDictionaryBucketPaymentById(@PathVariable(name = "id") String id) {
        return ApiResponse.<DictionaryBucketPaymentResponse>builder()
                .result(dictionaryBucketPaymentService.getDictionaryBucketPaymentById(id))
                .build();
    }

    @GetMapping("/{id}/total-expense")
    public ApiResponse<Long> getTotalExpenseById(@PathVariable(name = "id") String id,
                                                 @RequestParam(name = "timeOption", required = false) String timeOption
    ) {
        return ApiResponse.<Long>builder()
                .result(dictionaryBucketPaymentService.getTotalExpenseByBucketPaymentId(id, timeOption))
                .build();
    }

    @GetMapping("/{id}/total-revenue")
    public ApiResponse<Long> getTotalRevenueById(@PathVariable(name = "id") String id,
                                                 @RequestParam(name = "timeOption", required = false) String timeOption

    ) {
        return ApiResponse.<Long>builder()
                .result(dictionaryBucketPaymentService.getTotalRevenueByBucketPaymentId(id, timeOption))
                .build();
    }

    @GetMapping("/{id}/balance")
    public ApiResponse<Long> getAmountById(@PathVariable(name = "id") String id) {
        return ApiResponse.<Long>builder()
                .result(dictionaryBucketPaymentService.getBalanceByBucketPaymentId(id))
                .build();
    }

    @GetMapping("/{id}/transaction-history")
    public ApiResponse<List<ExpenseRevenueHistory>> getTransactionHistory(
            @PathVariable(name = "id") String id,
            @RequestParam(name = "field", required = false, defaultValue = "startDate") String field,
            @RequestParam(name = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(name = "pageSize", required = false, defaultValue = "5") Integer pageSize,
            @RequestParam(name = "sort", required = false, defaultValue = "ASC") String sort,
            @RequestParam(name = "timeOption", required = false, defaultValue = "Toàn bộ thời gian") String timeOption,
            @RequestParam(name = "startDate", required = false, defaultValue = "") String startDate,
            @RequestParam(name = "endDate", required = false, defaultValue = "") String endDate
    ) {
        return ApiResponse.<List<ExpenseRevenueHistory>>builder()
                .result(dictionaryBucketPaymentService.getAllExpenseRevenueRegularsByIdAndDate(id, pageNumber, pageSize,sort,timeOption,startDate,endDate))
                .build();
    }


    @PostMapping("")
    public ApiResponse<DictionaryBucketPaymentResponse> createDictionaryBucketPayment(@RequestBody DictionaryBucketPaymentRequest request) {
        Users user = usersService.getMyUserInfo();
        return ApiResponse.<DictionaryBucketPaymentResponse>builder()
                .result(dictionaryBucketPaymentService.createDictionaryBucketPayment(request, user.getId()))
                .build();
    }
}
