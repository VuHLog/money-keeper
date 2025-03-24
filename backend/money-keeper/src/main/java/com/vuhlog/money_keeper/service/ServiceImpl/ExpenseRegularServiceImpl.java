package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.dao.*;
import com.vuhlog.money_keeper.dao.specification.ExpenseRegularSpecification;
import com.vuhlog.money_keeper.dto.request.ExpenseRegularRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseRegularResponse;
import com.vuhlog.money_keeper.entity.*;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.ExpenseRegularMapper;
import com.vuhlog.money_keeper.service.ExpenseRegularService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ExpenseRegularServiceImpl implements ExpenseRegularService {
    private final ExpenseRegularRepository expenseRegularRepository;
    private final DictionaryBucketPaymentRepository dictionaryBucketPaymentRepository;
    private final DictionaryExpenseRepository dictionaryExpenseRepository;
    private final TripEventRepository tripEventRepository;
    private final BeneficiaryRepository beneficiaryRepository;
    private final ExpenseRegularMapper expenseRegularMapper;

    @Override
    public List<ExpenseRegularResponse> getAllMyExpenseRegular(String dictionaryBucketPaymentId) {
        Specification<ExpenseRegular> specs = Specification.where(null);
        specs = specs.and(ExpenseRegularSpecification.hasDictionaryBucketPaymentId(dictionaryBucketPaymentId));

        return expenseRegularRepository.findAll(specs).stream().map(expenseRegularMapper::toExpenseRegularResponse).toList();
    }

    @Override
    public ExpenseRegularResponse createExpenseRegular(ExpenseRegularRequest request) {
        ExpenseRegular expenseRegular = expenseRegularMapper.toExpenseRegular(request);

        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
        expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);

        DictionaryExpense dictionaryExpense = dictionaryExpenseRepository.findById(request.getDictionaryExpenseId()).orElse(null);
        expenseRegular.setDictionaryExpense(dictionaryExpense);

        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        expenseRegular.setTripEvent(tripEvent);

        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        expenseRegular.setBeneficiary(beneficiary);

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegularRepository.save(expenseRegular));
    }

    @Override
    public void deleteExpenseRegular(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        expenseRegularRepository.deleteById(id);
    }

    @Override
    public ExpenseRegularResponse updateExpenseRegular(String id, ExpenseRegularRequest request) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        expenseRegularMapper.updateExpenseRegularFromRequest(request, expenseRegular);
        if(!expenseRegular.getDictionaryBucketPayment().getId().equals(request.getDictionaryBucketPaymentId())){
            DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
            expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        }
        if(!expenseRegular.getDictionaryExpense().getId().equals(request.getDictionaryExpenseId())){
            DictionaryExpense dictionaryExpense = dictionaryExpenseRepository.findById(request.getDictionaryExpenseId()).orElse(null);
            expenseRegular.setDictionaryExpense(dictionaryExpense);
        }
        if(!expenseRegular.getTripEvent().getId().equals(request.getTripEventId())){
            TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
            expenseRegular.setTripEvent(tripEvent);
        }
        if(!expenseRegular.getBeneficiary().getId().equals(request.getBeneficiaryId())){
            Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
            expenseRegular.setBeneficiary(beneficiary);
        }
        return expenseRegularMapper.toExpenseRegularResponse(expenseRegularRepository.save(expenseRegular));
    }

    @Override
    public ExpenseRegularResponse getExpenseRegularById(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }
}
