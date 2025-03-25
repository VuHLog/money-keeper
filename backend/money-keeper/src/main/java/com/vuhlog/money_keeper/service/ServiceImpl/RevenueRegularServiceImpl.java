package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.dao.*;
import com.vuhlog.money_keeper.dao.specification.RevenueRegularSpecification;
import com.vuhlog.money_keeper.dto.request.RevenueRegularRequest;
import com.vuhlog.money_keeper.dto.response.RevenueRegularResponse;
import com.vuhlog.money_keeper.entity.*;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.RevenueRegularMapper;
import com.vuhlog.money_keeper.service.RevenueRegularService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RevenueRegularServiceImpl implements RevenueRegularService {
    private final RevenueRegularRepository revenueRegularRepository;
    private final DictionaryBucketPaymentRepository dictionaryBucketPaymentRepository;
    private final DictionaryRevenueRepository dictionaryRevenueRepository;
    private final TripEventRepository tripEventRepository;
    private final CollectMoneyWhoRepository collectMoneyWhoRepository;
    private final RevenueRegularMapper revenueRegularMapper;

    @Override
    public List<RevenueRegularResponse> getAllMyRevenueRegular(String dictionaryBucketPaymentId) {
        Specification<RevenueRegular> specs = Specification.where(null);
        specs = specs.and(RevenueRegularSpecification.hasDictionaryBucketPaymentId(dictionaryBucketPaymentId));

        return revenueRegularRepository.findAll(specs).stream().map(revenueRegularMapper::toRevenueRegularResponse).toList();
    }

    @Override
    public RevenueRegularResponse createRevenueRegular(RevenueRegularRequest request) {
        RevenueRegular revenueRegular = revenueRegularMapper.toRevenueRegular(request);

        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
        revenueRegular.setDictionaryBucketPayment(dictionaryBucketPayment);

        DictionaryRevenue dictionaryRevenue = dictionaryRevenueRepository.findById(request.getDictionaryRevenueId()).orElse(null);
        revenueRegular.setDictionaryRevenue(dictionaryRevenue);

        TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
        revenueRegular.setTripEvent(tripEvent);

        CollectMoneyWho collectMoneyWho = collectMoneyWhoRepository.findById(request.getCollectMoneyWhoId()).orElse(null);
        revenueRegular.setCollectMoneyWho(collectMoneyWho);

        return revenueRegularMapper.toRevenueRegularResponse(revenueRegularRepository.save(revenueRegular));
    }

    @Override
    public void deleteRevenueRegular(String id) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        revenueRegularRepository.deleteById(id);
    }

    @Override
    public RevenueRegularResponse updateRevenueRegular(String id, RevenueRegularRequest request) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        revenueRegularMapper.updateRevenueRegularFromRequest(request, revenueRegular);
        if(!revenueRegular.getDictionaryBucketPayment().getId().equals(request.getDictionaryBucketPaymentId())){
            DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(request.getDictionaryBucketPaymentId()).orElse(null);
            revenueRegular.setDictionaryBucketPayment(dictionaryBucketPayment);
        }
        if(!revenueRegular.getDictionaryRevenue().getId().equals(request.getDictionaryRevenueId())){
            DictionaryRevenue dictionaryRevenue = dictionaryRevenueRepository.findById(request.getDictionaryRevenueId()).orElse(null);
            revenueRegular.setDictionaryRevenue(dictionaryRevenue);
        }
        if(!revenueRegular.getTripEvent().getId().equals(request.getTripEventId())){
            TripEvent tripEvent = tripEventRepository.findById(request.getTripEventId()).orElse(null);
            revenueRegular.setTripEvent(tripEvent);
        }
        if(!revenueRegular.getCollectMoneyWho().getId().equals(request.getCollectMoneyWhoId())){
            CollectMoneyWho collectMoneyWho = collectMoneyWhoRepository.findById(request.getCollectMoneyWhoId()).orElse(null);
            revenueRegular.setCollectMoneyWho(collectMoneyWho);
        }
        return revenueRegularMapper.toRevenueRegularResponse(revenueRegularRepository.save(revenueRegular));
    }

    @Override
    public RevenueRegularResponse getRevenueRegularById(String id) {
        RevenueRegular revenueRegular = revenueRegularRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.REVENUE_REGULAR_NOT_EXISTED));
        return revenueRegularMapper.toRevenueRegularResponse(revenueRegular);
    }
}
