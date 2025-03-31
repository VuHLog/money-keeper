package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.constants.TransactionType;
import com.vuhlog.money_keeper.constants.TransferType;
import com.vuhlog.money_keeper.dao.*;
import com.vuhlog.money_keeper.dao.specification.ExpenseRegularSpecification;
import com.vuhlog.money_keeper.dto.request.ExpenseRegularRequest;
import com.vuhlog.money_keeper.dto.request.TransferRequest;
import com.vuhlog.money_keeper.dto.response.ExpenseRegularResponse;
import com.vuhlog.money_keeper.entity.*;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.ExpenseRegularMapper;
import com.vuhlog.money_keeper.model.PeriodOfTime;
import com.vuhlog.money_keeper.service.ExpenseRegularService;
import com.vuhlog.money_keeper.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
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
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final ExpenseRegularMapper expenseRegularMapper;
    private final RevenueRegularRepository revenueRegularRepository;

    @Override
    public List<ExpenseRegularResponse> getAllMyExpenseRegular(String dictionaryBucketPaymentId) {
        Specification<ExpenseRegular> specs = Specification.where(null);
        specs = specs.and(ExpenseRegularSpecification.hasDictionaryBucketPaymentId(dictionaryBucketPaymentId));

        return expenseRegularRepository.findAll(specs).stream().map(expenseRegularMapper::toExpenseRegularResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseRegularResponse createExpenseRegular(ExpenseRegularRequest request) {
        //save expense
        ExpenseRegular expenseRegular = expenseRegularMapper.toExpenseRegular(request);
        expenseRegular.setTransferType(TransferType.NORMAL.getType());
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        long oldBalance = dictionaryBucketPayment.getBalance();
        long newBalance = oldBalance - request.getAmount();
        expenseRegular.setBalance(newBalance);
        DictionaryExpense dictionaryExpense = dictionaryExpenseRepository.findById(request.getDictionaryExpenseId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setDictionaryExpense(dictionaryExpense);
        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        expenseRegular.setTripEvent(tripEvent);
        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        expenseRegular.setBeneficiary(beneficiary);
        expenseRegular = expenseRegularRepository.save(expenseRegular);

        //update balance for account table
        dictionaryBucketPayment.setBalance(newBalance);
        dictionaryBucketPaymentRepository.save(dictionaryBucketPayment);

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }

    @Override
    public ExpenseRegularResponse createExpenseRegularFromTransferRequest(TransferRequest request) {
        ExpenseRegular expenseRegular = expenseRegularMapper.toExpenseRegularFromTransferRequest(request);

        expenseRegular.setTransferType(TransferType.TRANSFER.getType());

        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
        expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);

        DictionaryBucketPayment receivedAccount = dictionaryBucketPaymentRepository.findById(request.getReceivedAccountId()).orElse(null);
        expenseRegular.setReceivedAccount(receivedAccount);

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegularRepository.save(expenseRegular));
    }

    @Override
    public void deleteExpenseRegular(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        expenseRegularRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseRegularResponse updateExpenseRegular(String id, ExpenseRegularRequest request) {
        //save update expense
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        PeriodOfTime updateTimeLimit = TimestampUtil.getPeriodOfTime(TimeOptionType.LAST_30_DAYS.getType());
        if(expenseRegular.getExpenseDate().before(updateTimeLimit.getStartDate()) || expenseRegular.getExpenseDate().after(updateTimeLimit.getEndDate())){
            throw new AppException(ErrorCode.UPDATE_TIME_LIMIT);
        }
        long oldAmount = expenseRegular.getAmount();
        long newAmount = request.getAmount();
        String oldCategoryId = expenseRegular.getDictionaryExpense().getId();
        String newCategoryId = request.getDictionaryExpenseId();
        expenseRegularMapper.updateExpenseRegularFromRequest(request, expenseRegular);
        expenseRegular.setBalance(expenseRegular.getBalance() - (newAmount - oldAmount));
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        if(!expenseRegular.getDictionaryExpense().getId().equals(request.getDictionaryExpenseId())){
            DictionaryExpense dictionaryExpense = dictionaryExpenseRepository.findById(request.getDictionaryExpenseId()).orElse(null);
            expenseRegular.setDictionaryExpense(dictionaryExpense);
        }
        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        expenseRegular.setTripEvent(tripEvent);
        Beneficiary beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        expenseRegular.setBeneficiary(beneficiary);
        expenseRegular = expenseRegularRepository.save(expenseRegular);

        //create transaction history
        TransactionHistory transactionHistory = TransactionHistory.builder()
                .transactionId(expenseRegular.getId())
                .transactionType(TransactionType.EXPENSE.getType())
                .oldAmount(oldAmount)
                .newAmount(newAmount)
                .oldCategoryId(oldCategoryId)
                .newCategoryId(newCategoryId)
                .bucketPayment(dictionaryBucketPayment)
                .build();
        transactionHistoryRepository.save(transactionHistory);

        if(oldAmount != newAmount){
            //update balance for account table
            long oldBalance = dictionaryBucketPayment.getBalance();
            long newBalance = oldBalance - (newAmount - oldAmount);
            dictionaryBucketPayment.setBalance(newBalance);
            dictionaryBucketPaymentRepository.save(dictionaryBucketPayment);

            //update expense,revenue balance after this expense
            expenseRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), -(newAmount - oldAmount), expenseRegular.getExpenseDate());
            revenueRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), -(newAmount - oldAmount), expenseRegular.getExpenseDate());
        }

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }

    @Override
    public ExpenseRegularResponse getExpenseRegularById(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }
}
