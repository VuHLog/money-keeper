package com.vuhlog.money_keeper.controller;

import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.ApiResponse;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
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

    @PostMapping("")
    public ApiResponse<DictionaryBucketPaymentResponse> createDictionaryBucketPayment(@RequestBody DictionaryBucketPaymentRequest request) {
        Users user = usersService.getMyUserInfo();
        return ApiResponse.<DictionaryBucketPaymentResponse>builder()
                .result(dictionaryBucketPaymentService.createDictionaryBucketPayment(request, user.getId()))
                .build();
    }
}
