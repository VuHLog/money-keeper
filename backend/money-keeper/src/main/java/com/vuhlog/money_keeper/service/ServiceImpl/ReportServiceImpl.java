package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.common.UserCommon;
import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.dao.ExpenseRegularRepository;
import com.vuhlog.money_keeper.dao.ReportExpenseRevenueRepository;
import com.vuhlog.money_keeper.dao.RevenueRegularRepository;
import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.request.TotalExpenseRevenueRequest;
import com.vuhlog.money_keeper.dto.response.ReportExpenseRevenueResponse;
import com.vuhlog.money_keeper.dto.response.TotalExpenseRevenueResponse;
import com.vuhlog.money_keeper.entity.ReportExpenseRevenue;
import com.vuhlog.money_keeper.entity.Users;
import com.vuhlog.money_keeper.mapper.ReportExpenseRevenueMapper;
import com.vuhlog.money_keeper.model.PeriodOfTime;
import com.vuhlog.money_keeper.service.ReportService;
import com.vuhlog.money_keeper.util.TimestampUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final UserCommon userCommon;
    private final ExpenseRegularRepository expenseRegularRepository;
    private final RevenueRegularRepository revenueRegularRepository;
    private final ReportExpenseRevenueRepository reportExpenseRevenueRepository;
    private final ReportExpenseRevenueMapper reportExpenseRevenueMapper;

    @PersistenceContext
    private EntityManager em;

    @Override
    public TotalExpenseRevenueResponse getTotalExpenseRevenueByTimeOption(TotalExpenseRevenueRequest req) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        List<String> bucketPaymentIds = req.getBucketPaymentIds();
        String bucketPaymentIdsJoin = bucketPaymentIds != null ? String.join(",", bucketPaymentIds) : null;
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        PeriodOfTime periodOfTime = TimestampUtil.handleTimeOption(timeOption, startDate, endDate);
        TotalExpenseRevenueResponse totalExpenseRevenueResponse = new TotalExpenseRevenueResponse();
        if(timeOption.equals(TimeOptionType.TODAY.getType()) || timeOption.equals(TimeOptionType.THIS_WEEK.getType())){
            Long totalExpense = expenseRegularRepository.getTotalExpenseByMonthAndThisYear(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
            totalExpenseRevenueResponse.setTotalExpense(totalExpense == null ? 0 : totalExpense);
            Long totalRevenue = revenueRegularRepository.getTotalRevenueByMonthAndThisYear(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
            totalExpenseRevenueResponse.setTotalRevenue(totalRevenue == null ? 0 : totalRevenue);
            return totalExpenseRevenueResponse;
        }else if (timeOption.equals(TimeOptionType.THIS_MONTH.getType()) || timeOption.equals(TimeOptionType.THIS_QUARTER.getType()) || timeOption.equals(TimeOptionType.THIS_YEAR.getType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(periodOfTime.getStartDate());
            int startMonth = calendar.get(Calendar.MONTH) + 1;
            calendar.setTime(periodOfTime.getEndDate());
            int endMonth = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            Object[] result = reportExpenseRevenueRepository.getTotalExpenseRevenueByMonthAndThisYear(startMonth, endMonth, year, userId, bucketPaymentIdsJoin);
            if(result != null && result.length > 0) {
                Object[] object = (Object[]) result[0];
                if(object != null && object.length >= 2 && object[0] != null && object[1] != null){
                    totalExpenseRevenueResponse = new TotalExpenseRevenueResponse(
                            ((Number) object[0]).longValue(),
                            ((Number) object[1]).longValue()
                    );
                    return totalExpenseRevenueResponse;
                }else {
                    totalExpenseRevenueResponse.setTotalExpense(0);
                    totalExpenseRevenueResponse.setTotalRevenue(0);
                }
            } else {
                totalExpenseRevenueResponse.setTotalExpense(0);
                totalExpenseRevenueResponse.setTotalRevenue(0);
            }
        }
        return totalExpenseRevenueResponse;
    }
}
