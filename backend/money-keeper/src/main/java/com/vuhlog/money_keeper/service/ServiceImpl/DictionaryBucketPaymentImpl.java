package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.dao.BankRepository;
import com.vuhlog.money_keeper.dao.DictionaryBucketPaymentRepository;
import com.vuhlog.money_keeper.dao.UsersRepository;
import com.vuhlog.money_keeper.dao.specification.DictionaryBucketPaymentSpecification;
import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
import com.vuhlog.money_keeper.entity.DictionaryBucketPayment;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.DictionaryBucketPaymentMapper;
import com.vuhlog.money_keeper.service.DictionaryBucketPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictionaryBucketPaymentImpl implements DictionaryBucketPaymentService {
    private final DictionaryBucketPaymentRepository dictionaryBucketPaymentRepository;
    private final BankRepository bankRepository;
    private final UsersRepository usersRepository;
    private final DictionaryBucketPaymentMapper dictionaryBucketPaymentMapper;

    @Override
    public DictionaryBucketPaymentResponse createDictionaryBucketPayment(DictionaryBucketPaymentRequest request, String userId) {
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentMapper.toDictionaryBucketPayment(request);
        dictionaryBucketPayment.setBank(bankRepository.findById(request.getBankId()).orElseThrow(() -> new AppException(ErrorCode.BANK_NOT_EXISTED)));
        dictionaryBucketPayment.setUser(usersRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED)));
        return dictionaryBucketPaymentMapper.toDictionaryBucketResponse(dictionaryBucketPaymentRepository.save(dictionaryBucketPayment));
    }

    @Override
    public DictionaryBucketPaymentResponse updateDictionaryBucketPayment(String id, DictionaryBucketPaymentRequest request) {
        return null;
    }

    @Override
    public void deleteDictionaryBucketPayment(String id) {

    }

    @Override
    public DictionaryBucketPaymentResponse getDictionaryBucketPayment(String id) {
        return null;
    }

    @Override
    public List<DictionaryBucketPaymentResponse> getAllDictionaryBucketPayment(String userId) {
        Specification<DictionaryBucketPayment> specs = Specification.where(null);
        specs = specs.and(DictionaryBucketPaymentSpecification.filterByUserId(userId));

        Sort sortable = Sort.by("accountName").ascending();
        return dictionaryBucketPaymentRepository.findAll(specs, sortable).stream().map(dictionaryBucketPaymentMapper::toDictionaryBucketResponse).toList();
    }
}
