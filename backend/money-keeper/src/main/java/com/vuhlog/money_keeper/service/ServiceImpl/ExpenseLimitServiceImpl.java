package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.common.UserCommon;
import com.vuhlog.money_keeper.dao.DictionaryBucketPaymentRepository;
import com.vuhlog.money_keeper.dao.DictionaryExpenseRepository;
import com.vuhlog.money_keeper.dao.ExpenseLimitRepository;
import com.vuhlog.money_keeper.dao.specification.ExpenseLimitSpecification;
import com.vuhlog.money_keeper.dto.request.ExpenseLimitRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseLimitResponse;
import com.vuhlog.money_keeper.entity.DictionaryBucketPayment;
import com.vuhlog.money_keeper.entity.ExpenseLimit;
import com.vuhlog.money_keeper.entity.Users;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.DictionaryBucketPaymentMapper;
import com.vuhlog.money_keeper.mapper.DictionaryExpenseMapper;
import com.vuhlog.money_keeper.mapper.ExpenseLimitMapper;
import com.vuhlog.money_keeper.service.ExpenseLimitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseLimitServiceImpl implements ExpenseLimitService {
    private final UserCommon userCommon;
    private final ExpenseLimitRepository expenseLimitRepository;
    private final DictionaryBucketPaymentRepository dictionaryBucketPaymentRepository;
    private final DictionaryExpenseRepository dictionaryExpenseRepository;
    private final ExpenseLimitMapper expenseLimitMapper;
    private final DictionaryExpenseMapper dictionaryExpenseMapper;
    private final DictionaryBucketPaymentMapper dictionaryBucketPaymentMapper;

    @Override
    public ExpenseLimitResponse getExpenseLimitById(String id) {
        ExpenseLimit expenseLimit = expenseLimitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.EXPENSE_LIMIT_NOT_EXISTED));
        ExpenseLimitResponse expenseLimitResponse = expenseLimitMapper.toExpenseLimitResponse(expenseLimit);
        return convertToResponse(expenseLimitResponse, expenseLimit.getBucketPaymentIds(), expenseLimit.getCategoriesId());
    }

    @Override
    public List<ExpenseLimitResponse> getAllExpenseLimit() {
        Specification<ExpenseLimit> specs = Specification.where(null);
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        specs = specs.and(ExpenseLimitSpecification.filterByUserId(userId));
        List<ExpenseLimit> expenseLimits = expenseLimitRepository.findAll(specs);
        return expenseLimits.stream().map(expenseLimit -> {
            ExpenseLimitResponse expenseLimitResponse = expenseLimitMapper.toExpenseLimitResponse(expenseLimit);
            return convertToResponse(expenseLimitResponse, expenseLimit.getBucketPaymentIds(), expenseLimit.getCategoriesId());
        }).collect(Collectors.toList());
    }

    @Override
    public ExpenseLimitResponse createExpenseLimit(ExpenseLimitRequest request) {
        ExpenseLimit expenseLimit = expenseLimitMapper.toExpenseLimit(request);
        expenseLimit.setUser(userCommon.getMyUserInfo());
        expenseLimitRepository.save(expenseLimit);
        return convertToResponse(expenseLimitMapper.toExpenseLimitResponse(expenseLimit), request.getBucketPaymentIds(), request.getCategoriesId());
    }

    @Override
    public ExpenseLimitResponse updateExpenseLimit(String id, ExpenseLimitRequest request) {
        ExpenseLimit expenseLimit = expenseLimitRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.EXPENSE_LIMIT_NOT_EXISTED));
        expenseLimitMapper.updateExpenseLimit(expenseLimit, request);
        expenseLimitRepository.save(expenseLimit);
        return convertToResponse(expenseLimitMapper.toExpenseLimitResponse(expenseLimit), request.getBucketPaymentIds(), request.getCategoriesId());
    }

    @Override
    public void deleteExpenseLimit(String id) {

    }

    private ExpenseLimitResponse convertToResponse(ExpenseLimitResponse res, String bucketPaymentIds, String categoriesId) {
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        if (bucketPaymentIds != null && !bucketPaymentIds.isEmpty()) {
            List<DictionaryBucketPayment> bucketPayments = dictionaryBucketPaymentRepository.findAllByIdIn(bucketPaymentIds, userId);
            res.setBucketPayments(bucketPayments.stream().map(dictionaryBucketPaymentMapper::toDictionaryBucketResponse).collect(Collectors.toList()));
        }
        if (categoriesId != null && !categoriesId.isEmpty()) {
            res.setCategories(dictionaryExpenseRepository.findAllByIdIn(categoriesId, userId));
        }
        return res;

    }
}
