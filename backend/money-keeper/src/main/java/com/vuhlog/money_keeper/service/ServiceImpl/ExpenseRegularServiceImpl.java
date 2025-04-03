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
import com.vuhlog.money_keeper.mapper.RevenueRegularMapper;
import com.vuhlog.money_keeper.model.NearestTransaction;
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
import java.util.stream.Collectors;

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
    private final RevenueRegularMapper revenueRegularMapper;

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
        long oldBalance =  getOldBalanceWhenCreate(dictionaryBucketPayment, expenseRegular.getExpenseDate());
        long newBalance = oldBalance - request.getAmount();
        expenseRegular.setBalance(newBalance);
        DictionaryExpense dictionaryExpense = dictionaryExpenseRepository.findById(request.getDictionaryExpenseId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setDictionaryExpense(dictionaryExpense);
        TripEvent tripEvent = null;
        if(request.getTripEventId() != null && !request.getTripEventId().isEmpty()) {
            tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        }
        expenseRegular.setTripEvent(tripEvent);
        Beneficiary beneficiary = null;
        if(request.getBeneficiaryId() != null && !request.getBeneficiaryId().isEmpty()) {
            beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        }
        expenseRegular.setBeneficiary(beneficiary);
        expenseRegular = expenseRegularRepository.save(expenseRegular);

        //update balance
        updateBalance(dictionaryBucketPayment, request.getAmount(), expenseRegular.getExpenseDate());

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseRegularResponse createExpenseRegularFromTransferRequest(TransferRequest request) {
        // save expense
        ExpenseRegular expenseRegular = expenseRegularMapper.toExpenseRegularFromTransferRequest(request);
        expenseRegular.setTransferType(TransferType.TRANSFER.getType());
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        long oldBalance =  getOldBalanceWhenCreate(dictionaryBucketPayment, expenseRegular.getExpenseDate());
        long newBalance = oldBalance - request.getAmount();
        expenseRegular.setBalance(newBalance);

        DictionaryBucketPayment beneficiaryAccount = dictionaryBucketPaymentRepository.findById(request.getBeneficiaryAccountId()).orElseThrow(() -> new AppException(ErrorCode.BUCKET_PAYMENT_NOT_EXISTED));
        expenseRegular.setBeneficiaryAccount(beneficiaryAccount);
        expenseRegular = expenseRegularRepository.save(expenseRegular);

        //save revenue for received account
        RevenueRegular revenueRegular = revenueRegularMapper.toRevenueRegularFromTransferRequest(request);
        revenueRegular.setDictionaryBucketPayment(beneficiaryAccount);
        revenueRegular.setSenderAccount(dictionaryBucketPayment);
        revenueRegular.setTransferType(TransferType.TRANSFER.getType());
        long oldBalanceRevenue =  getOldBalanceWhenCreate(beneficiaryAccount, expenseRegular.getExpenseDate());
        long newBalanceRevenue = oldBalanceRevenue + request.getAmount();
        revenueRegular.setBalance(newBalanceRevenue);
        revenueRegularRepository.save(revenueRegular);

        //update balance for sender accocunt
        updateBalance(dictionaryBucketPayment, request.getAmount(), expenseRegular.getExpenseDate());

        //update balance for beneficiary account
        updateBalance(beneficiaryAccount, -request.getAmount(), expenseRegular.getExpenseDate());

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
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
        TripEvent tripEvent = null;
        if(request.getTripEventId() != null && !request.getTripEventId().isEmpty()) {
            tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        }
        expenseRegular.setTripEvent(tripEvent);
        Beneficiary beneficiary = null;
        if(request.getBeneficiaryId() != null && !request.getBeneficiaryId().isEmpty()) {
            beneficiary = beneficiaryRepository.findById(request.getBeneficiaryId()).orElse(null);
        }
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
            updateBalance(dictionaryBucketPayment, (newAmount - oldAmount), expenseRegular.getExpenseDate());
        }

        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }

    @Override
    public ExpenseRegularResponse getExpenseRegularById(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        return expenseRegularMapper.toExpenseRegularResponse(expenseRegular);
    }

    @Transactional(rollbackFor = Exception.class)
    protected void updateBalance(DictionaryBucketPayment dictionaryBucketPayment, long amount, Timestamp date) {
        //update balance for account table
        long oldBalance = dictionaryBucketPayment.getBalance();
        long newBalance = oldBalance - amount;
        dictionaryBucketPayment.setBalance(newBalance);
        dictionaryBucketPaymentRepository.save(dictionaryBucketPayment);

        //update expense,revenue balance after this expense
        expenseRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), -amount, date);
        revenueRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), -amount, date);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpenseRegular(String id) {
        ExpenseRegular expenseRegular = expenseRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.EXPENSE_REGULAR_NOT_EXISTED));
        //delete record in transaction history table
        transactionHistoryRepository.deleteAllByTransactionId(expenseRegular.getId());

        expenseRegularRepository.deleteById(id);

        //update balance
        DictionaryBucketPayment dictionaryBucketPayment = expenseRegular.getDictionaryBucketPayment();
        updateBalance(dictionaryBucketPayment, -expenseRegular.getAmount(), expenseRegular.getExpenseDate());
    }

    private long getOldBalanceWhenCreate(DictionaryBucketPayment dictionaryBucketPayment, Timestamp date) {
        Long oldBalance = dictionaryBucketPaymentRepository.getNearestTransactionByBucketPaymentIdAndLessThanDate(dictionaryBucketPayment.getId(), date);
        if(oldBalance != null){
            return oldBalance;
        }else{
            Object[] result = dictionaryBucketPaymentRepository.getNearestTransactionByBucketPaymentIdAndGreaterThanDate(dictionaryBucketPayment.getId(), date);
            if(result != null && result.length > 0){
                Object[] object = (Object[]) result[0];
                if(object.length >=4){
                    NearestTransaction nearestTransaction = new NearestTransaction(
                            object[0].toString(),  //id
                            ((Number) object[1]).longValue(),  //balance
                            ((Number) object[2]).longValue(),  //amount
                            (String)object[3] //type
                    );
                    if(nearestTransaction.getType().equals(TransactionType.EXPENSE.getType())){
                        return nearestTransaction.getBalance() + nearestTransaction.getAmount();
                    }else if(nearestTransaction.getType().equals(TransactionType.REVENUE.getType())){
                        return nearestTransaction.getBalance() - nearestTransaction.getAmount();
                    }
                }
            }else
                return dictionaryBucketPayment.getBalance();
        }
        return 0;
    }
}
