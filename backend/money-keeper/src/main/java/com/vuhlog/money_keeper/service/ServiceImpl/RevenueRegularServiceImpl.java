package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.constants.TransactionType;
import com.vuhlog.money_keeper.dao.*;
import com.vuhlog.money_keeper.dao.specification.RevenueRegularSpecification;
import com.vuhlog.money_keeper.dto.request.RevenueRegularRequest;
import com.vuhlog.money_keeper.dto.response.RevenueRegularResponse;
import com.vuhlog.money_keeper.entity.*;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.RevenueRegularMapper;
import com.vuhlog.money_keeper.model.PeriodOfTime;
import com.vuhlog.money_keeper.service.RevenueRegularService;
import com.vuhlog.money_keeper.util.TimestampUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueRegularServiceImpl implements RevenueRegularService {
    private final RevenueRegularRepository revenueRegularRepository;
    private final ExpenseRegularRepository expenseRegularRepository;
    private final DictionaryBucketPaymentRepository dictionaryBucketPaymentRepository;
    private final DictionaryRevenueRepository dictionaryRevenueRepository;
    private final TripEventRepository tripEventRepository;
    private final CollectMoneyWhoRepository collectMoneyWhoRepository;
    private final TransactionHistoryRepository transactionHistoryRepository;
    private final RevenueRegularMapper revenueRegularMapper;

    @Override
    public List<RevenueRegularResponse> getAllMyRevenueRegular(String dictionaryBucketPaymentId) {
        Specification<RevenueRegular> specs = Specification.where(null);
        specs = specs.and(RevenueRegularSpecification.hasDictionaryBucketPaymentId(dictionaryBucketPaymentId));

        return revenueRegularRepository.findAll(specs).stream().map(revenueRegularMapper::toRevenueRegularResponse).toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RevenueRegularResponse createRevenueRegular(RevenueRegularRequest request) {
        //save revenue
        RevenueRegular revenueRegular = revenueRegularMapper.toRevenueRegular(request);
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
        revenueRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        long oldBalance = dictionaryBucketPayment.getBalance();
        long newBalance = oldBalance + request.getAmount();
        revenueRegular.setBalance(newBalance);
        DictionaryRevenue dictionaryRevenue = dictionaryRevenueRepository.findById(request.getDictionaryRevenueId()).orElse(null);
        revenueRegular.setDictionaryRevenue(dictionaryRevenue);
        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        revenueRegular.setTripEvent(tripEvent);
        CollectMoneyWho collectMoneyWho = collectMoneyWhoRepository.findById(request.getCollectMoneyWhoId()).orElse(null);
        revenueRegular.setCollectMoneyWho(collectMoneyWho);
        revenueRegular = revenueRegularRepository.save(revenueRegular);

        //update balance for account table
        dictionaryBucketPayment.setBalance(newBalance);
        dictionaryBucketPaymentRepository.save(dictionaryBucketPayment);

        return revenueRegularMapper.toRevenueRegularResponse(revenueRegular);
    }

    @Override
    public void deleteRevenueRegular(String id) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        revenueRegularRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RevenueRegularResponse updateRevenueRegular(String id, RevenueRegularRequest request) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        PeriodOfTime updateTimeLimit = TimestampUtil.getPeriodOfTime(TimeOptionType.LAST_30_DAYS.getType());
        if(revenueRegular.getRevenueDate().before(updateTimeLimit.getStartDate()) || revenueRegular.getRevenueDate().after(updateTimeLimit.getEndDate())){
            throw new AppException(ErrorCode.UPDATE_TIME_LIMIT);
        }
        long oldAmount = revenueRegular.getAmount();
        long newAmount = request.getAmount();
        String oldCategoryId = revenueRegular.getDictionaryRevenue().getId();
        String newCategoryId = request.getDictionaryRevenueId();
        revenueRegularMapper.updateRevenueRegularFromRequest(request, revenueRegular);
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
        revenueRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        if(!revenueRegular.getDictionaryRevenue().getId().equals(request.getDictionaryRevenueId())){
            DictionaryRevenue dictionaryRevenue = dictionaryRevenueRepository.findById(request.getDictionaryRevenueId()).orElse(null);
            revenueRegular.setDictionaryRevenue(dictionaryRevenue);
        }
        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        revenueRegular.setTripEvent(tripEvent);
        CollectMoneyWho collectMoneyWho = collectMoneyWhoRepository.findById(request.getCollectMoneyWhoId()).orElse(null);
        revenueRegular.setCollectMoneyWho(collectMoneyWho);
        revenueRegular = revenueRegularRepository.save(revenueRegular);

        //create transaction history
        TransactionHistory transactionHistory = TransactionHistory.builder()
                .transactionId(revenueRegular.getId())
                .transactionType(TransactionType.REVENUE.getType())
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
            long newBalance = oldBalance + (newAmount - oldAmount);
            dictionaryBucketPayment.setBalance(newBalance);
            dictionaryBucketPaymentRepository.save(dictionaryBucketPayment);

            //update expense,revenue balance after this expense
            expenseRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), (newAmount - oldAmount), revenueRegular.getRevenueDate());
            revenueRegularRepository.updateBalanceGreaterThanDatetime(dictionaryBucketPayment.getId(), (newAmount - oldAmount), revenueRegular.getRevenueDate());
        }
        return revenueRegularMapper.toRevenueRegularResponse(revenueRegular);
    }

    @Override
    public RevenueRegularResponse getRevenueRegularById(String id) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        return revenueRegularMapper.toRevenueRegularResponse(revenueRegular);
    }
}
