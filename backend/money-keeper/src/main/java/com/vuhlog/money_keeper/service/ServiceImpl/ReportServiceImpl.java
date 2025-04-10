package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.common.UserCommon;
import com.vuhlog.money_keeper.dao.ReportExpenseRevenueRepository;
import com.vuhlog.money_keeper.dto.response.ReportExpenseRevenueResponse;
import com.vuhlog.money_keeper.entity.ReportExpenseRevenue;
import com.vuhlog.money_keeper.entity.Users;
import com.vuhlog.money_keeper.mapper.ReportExpenseRevenueMapper;
import com.vuhlog.money_keeper.service.ReportService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserCommon userCommon;
    private final ReportExpenseRevenueRepository reportExpenseRevenueRepository;
    private final ReportExpenseRevenueMapper reportExpenseRevenueMapper;

    @PersistenceContext
    private EntityManager em;

    @Override
    public ReportExpenseRevenueResponse getTotalExpenseRevenueByMonthOrYear(Integer month, Integer year) {
        Users user = userCommon.getMyUserInfo();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ReportExpenseRevenue> cq = cb.createQuery(ReportExpenseRevenue.class);
        Root<ReportExpenseRevenue> root = cq.from(ReportExpenseRevenue.class);
        List<Predicate> predicates = new ArrayList<>();
        if(month != null) {
            Predicate predicate = cb.equal(root.get("month"), month);
            predicates.add(predicate);
        }
        if(year != null) {
            Predicate predicate = cb.equal(root.get("year"), year);
            predicates.add(predicate);
        }
        predicates.add(cb.equal(root.get("user").get("id"), user.getId()));

        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        ReportExpenseRevenue reportExpenseRevenue = em.createQuery(cq).getSingleResult();
        return reportExpenseRevenueMapper.toReportExpenseRevenueResponse(reportExpenseRevenue);
    }
}
