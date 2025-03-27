package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.constants.DictionaryBucketPaymentType;
import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.dao.BankRepository;
import com.vuhlog.money_keeper.dao.DictionaryBucketPaymentRepository;
import com.vuhlog.money_keeper.dao.UsersRepository;
import com.vuhlog.money_keeper.dao.specification.DictionaryBucketPaymentSpecification;
import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRevenueRegularResponse;
import com.vuhlog.money_keeper.entity.DictionaryBucketPayment;
import com.vuhlog.money_keeper.entity.ExpenseRegular;
import com.vuhlog.money_keeper.entity.RevenueRegular;
import com.vuhlog.money_keeper.exception.AppException;
import com.vuhlog.money_keeper.exception.ErrorCode;
import com.vuhlog.money_keeper.mapper.DictionaryBucketPaymentMapper;
import com.vuhlog.money_keeper.model.PeriodOfTime;
import com.vuhlog.money_keeper.service.DictionaryBucketPaymentService;
import com.vuhlog.money_keeper.util.TimestampUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PersistenceContext
    private EntityManager em;

    @Override
    public DictionaryBucketPaymentResponse createDictionaryBucketPayment(DictionaryBucketPaymentRequest request, String userId) {
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentMapper.toDictionaryBucketPayment(request);
        if(dictionaryBucketPayment.getAccountType().equalsIgnoreCase(DictionaryBucketPaymentType.BANK.getType())
        || dictionaryBucketPayment.getAccountType().equalsIgnoreCase(DictionaryBucketPaymentType.CREDIT_DEBIT_CARD.getType())){
            dictionaryBucketPayment.setBank(bankRepository.findById(request.getBankId()).orElseThrow(() -> new AppException(ErrorCode.BANK_NOT_EXISTED)));
        }
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
    public DictionaryBucketPaymentResponse getDictionaryBucketPaymentById(String id) {
        DictionaryBucketPayment dictionaryBucketPayment = dictionaryBucketPaymentRepository.findById(id).orElse(null);
        return dictionaryBucketPaymentMapper.toDictionaryBucketResponse(dictionaryBucketPayment);
    }

    @Override
    public List<DictionaryBucketPaymentResponse> getAllDictionaryBucketPayment(String userId) {
        Specification<DictionaryBucketPayment> specs = Specification.where(null);
        specs = specs.and(DictionaryBucketPaymentSpecification.filterByUserId(userId));

        Sort sortable = Sort.by("accountName").ascending();
        return dictionaryBucketPaymentRepository.findAll(specs, sortable).stream().map(dictionaryBucketPaymentMapper::toDictionaryBucketResponse).toList();
    }

    @Override
    public Page<ExpenseRevenueRegularResponse> getAllExpenseRevenueRegularsByDate(String userId, Pageable pageable, Integer pageNumber, Integer pageSize, String sort, String timeOption, String startDate, String endDate) {
        PeriodOfTime periodOfTime = new PeriodOfTime();
        if(timeOption.equalsIgnoreCase(TimeOptionType.FULL.getType())){
            periodOfTime = null;
        }else if (timeOption.equalsIgnoreCase(TimeOptionType.OPTIONAL.getType())){
            periodOfTime.setStartDate(TimestampUtil.stringToTimestamp(startDate));
            periodOfTime.setEndDate(TimestampUtil.stringToTimestamp(endDate));
        }else {
            periodOfTime = TimestampUtil.getPeriodOfTime(timeOption);
        }

        return null;
    }

    @Override
   public Long getTotalExpenseByBucketPaymentId(String bucketPaymentId, String timeOption) {
       CriteriaBuilder cb = em.getCriteriaBuilder();
       CriteriaQuery<Long> cq = cb.createQuery(Long.class);
       Root<DictionaryBucketPayment> root = cq.from(DictionaryBucketPayment.class);
       Join<DictionaryBucketPayment, ExpenseRegular> expenseRegularJoin = root.join("expenseRegulars", JoinType.INNER);
       cq.select(cb.sum(expenseRegularJoin.get("amount")));
       cq.where(cb.equal(root.get("id"), bucketPaymentId));

       PeriodOfTime periodOfTime = TimestampUtil.getPeriodOfTime(timeOption);

       Long result = em.createQuery(cq).getSingleResult();
       return result != null ? result : 0;
   }

    @Override
    public Long getTotalRevenueByBucketPaymentId(String bucketPaymentId, String timeOption) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DictionaryBucketPayment> root = cq.from(DictionaryBucketPayment.class);
        Join<DictionaryBucketPayment, RevenueRegular> revenueRegularJoin = root.join("revenueRegulars", JoinType.INNER);
        cq.select(cb.sum(revenueRegularJoin.get("amount")));
        cq.where(cb.equal(root.get("id"), bucketPaymentId));

        PeriodOfTime periodOfTime = TimestampUtil.getPeriodOfTime(timeOption);

        Long result = em.createQuery(cq).getSingleResult();
        return result != null ? result : 0;
    }

    @Override
    public Long getBalanceByBucketPaymentId(String bucketPaymentId) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DictionaryBucketPayment> root = cq.from(DictionaryBucketPayment.class);
        cq.select(root.get("balance"));
        cq.where(cb.equal(root.get("id"), bucketPaymentId));
        Long result = em.createQuery(cq).getSingleResult();
        return result != null ? result : 0;
    }
}
