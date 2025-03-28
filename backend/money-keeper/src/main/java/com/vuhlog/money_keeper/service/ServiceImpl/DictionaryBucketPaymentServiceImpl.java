package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.constants.DictionaryBucketPaymentType;
import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.dao.BankRepository;
import com.vuhlog.money_keeper.dao.DictionaryBucketPaymentRepository;
import com.vuhlog.money_keeper.dao.UsersRepository;
import com.vuhlog.money_keeper.dao.specification.DictionaryBucketPaymentSpecification;
import com.vuhlog.money_keeper.dto.request.DictionaryBucketPaymentRequest;
import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.response.DictionaryBucketPaymentResponse;
import com.vuhlog.money_keeper.dto.response.ExpenseRevenueHistory;
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
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictionaryBucketPaymentServiceImpl implements DictionaryBucketPaymentService {
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
    public List<ExpenseRevenueHistory> getAllExpenseRevenueRegularsByIdAndDate(ExpenseRevenueHistoryRequest req, Integer pageNumber, Integer pageSize, String sort) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        String bucketPaymentId = req.getBucketPaymentId();
        PeriodOfTime periodOfTime = handleTimeOption(timeOption, startDate, endDate);

        List<Object[]> results = dictionaryBucketPaymentRepository.getAllExpenseRevenueHistoryByBucketPaymentId(bucketPaymentId, periodOfTime != null ? periodOfTime.getStartDate() : null, periodOfTime != null ? periodOfTime.getEndDate() : null);
        return convertToExpenseRevenueHistory(results);
    }

    @Override
    public Long getTotalExpenseByBucketPaymentId(ExpenseRevenueHistoryRequest req) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        String bucketPaymentId = req.getBucketPaymentId();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DictionaryBucketPayment> root = cq.from(DictionaryBucketPayment.class);
        Join<DictionaryBucketPayment, ExpenseRegular> expenseRegularJoin = root.join("expenseRegulars", JoinType.INNER);
        cq.select(cb.sum(expenseRegularJoin.get("amount")));
        PeriodOfTime periodOfTime = handleTimeOption(timeOption, startDate, endDate);
        Predicate lessThanDate = null;
        Predicate greaterThanDate = null;
        List<Predicate> predicates = new ArrayList<>();
        if (periodOfTime != null) {
            if(periodOfTime.getStartDate() != null){
                greaterThanDate = cb.greaterThanOrEqualTo(expenseRegularJoin.get("expenseDate"), periodOfTime.getStartDate());
                predicates.add(greaterThanDate);
            }
            if(periodOfTime.getEndDate() != null){
                lessThanDate = cb.lessThanOrEqualTo(expenseRegularJoin.get("expenseDate"), periodOfTime.getEndDate());
                predicates.add(lessThanDate);
            }
        }
        Predicate equalToBucketPaymentId = cb.equal(root.get("id"), bucketPaymentId);
        predicates.add(equalToBucketPaymentId);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        Long result = em.createQuery(cq).getSingleResult();
        return result != null ? result : 0;
   }

    @Override
    public Long getTotalRevenueByBucketPaymentId(ExpenseRevenueHistoryRequest req) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        String bucketPaymentId = req.getBucketPaymentId();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<DictionaryBucketPayment> root = cq.from(DictionaryBucketPayment.class);
        Join<DictionaryBucketPayment, RevenueRegular> revenueRegularJoin = root.join("revenueRegulars", JoinType.INNER);
        cq.select(cb.sum(revenueRegularJoin.get("amount")));
        PeriodOfTime periodOfTime = handleTimeOption(timeOption, startDate, endDate);
        Predicate lessThanDate = null;
        Predicate greaterThanDate = null;
        List<Predicate> predicates = new ArrayList<>();
        if (periodOfTime != null) {
            if(periodOfTime.getStartDate() != null){
                greaterThanDate = cb.greaterThanOrEqualTo(revenueRegularJoin.get("revenueDate"), periodOfTime.getStartDate());
                predicates.add(greaterThanDate);
            }
            if(periodOfTime.getEndDate() != null){
                lessThanDate = cb.lessThanOrEqualTo(revenueRegularJoin.get("revenueDate"), periodOfTime.getEndDate());
                predicates.add(lessThanDate);
            }
        }
        Predicate equalToBucketPaymentId = cb.equal(root.get("id"), bucketPaymentId);
        predicates.add(equalToBucketPaymentId);
        cq.where(cb.and(predicates.toArray(new Predicate[0])));

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

    private List<ExpenseRevenueHistory> convertToExpenseRevenueHistory(List<Object[]> list) {
        return list.stream().map(obj ->
                new ExpenseRevenueHistory(
                        (String) obj[0], // id
                        (String) TimestampUtil.timestampToString((Timestamp) obj[1]), // date
                        ((Number) obj[2]).longValue(), // amount
                        (String) obj[3], // iconUrl
                        (String) obj[4], // categoryName
                        (String) obj[5], // type
                        (String) obj[6]  // interpretation
                )
        ).collect(Collectors.toList());
    }

    public PeriodOfTime handleTimeOption(String timeOption, String startDate, String endDate) {
        PeriodOfTime periodOfTime = new PeriodOfTime();
        if (timeOption != null && !timeOption.isEmpty()) {
            if (timeOption.equalsIgnoreCase(TimeOptionType.FULL.getType())) {
                return null;
            } else if (timeOption.equalsIgnoreCase(TimeOptionType.OPTIONAL.getType())) {
                if (startDate != null && !startDate.isEmpty()) {
                    periodOfTime.setStartDate(TimestampUtil.stringToTimestamp(startDate));
                }
                if (endDate != null && !endDate.isEmpty()) {
                    // time 23:59:59
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(TimestampUtil.stringToTimestamp(endDate));
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 0);
                    periodOfTime.setEndDate(new Timestamp(calendar.getTimeInMillis()));
                }
                return periodOfTime;
            } else {
                return TimestampUtil.getPeriodOfTime(timeOption);
            }
        }
        return null;
    }
}
