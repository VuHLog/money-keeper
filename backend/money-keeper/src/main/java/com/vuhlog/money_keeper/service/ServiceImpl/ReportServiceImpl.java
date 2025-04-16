package com.vuhlog.money_keeper.service.ServiceImpl;

import com.vuhlog.money_keeper.common.UserCommon;
import com.vuhlog.money_keeper.constants.TimeOptionType;
import com.vuhlog.money_keeper.dao.ExpenseRegularRepository;
import com.vuhlog.money_keeper.dao.ReportExpenseRevenueRepository;
import com.vuhlog.money_keeper.dao.RevenueRegularRepository;
import com.vuhlog.money_keeper.dto.request.ExpenseRevenueHistoryRequest;
import com.vuhlog.money_keeper.dto.request.ReportCategoryResponse;
import com.vuhlog.money_keeper.dto.request.TotalExpenseRevenueRequest;
import com.vuhlog.money_keeper.dto.response.*;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

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
        List<String> categoriesId = req.getCategoriesId();
        String categoriesIdJoin = categoriesId != null ? String.join(",", categoriesId) : null;
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        PeriodOfTime periodOfTime = TimestampUtil.handleTimeOption(timeOption, startDate, endDate);
        TotalExpenseRevenueResponse totalExpenseRevenueResponse = new TotalExpenseRevenueResponse();
        if(timeOption.equals(TimeOptionType.TODAY.getType()) || timeOption.equals(TimeOptionType.THIS_WEEK.getType())){
            Long totalExpense = expenseRegularRepository.getTotalExpenseByMonthAndThisYear(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, categoriesIdJoin, userId);
            totalExpenseRevenueResponse.setTotalExpense(totalExpense == null ? 0 : totalExpense);
            Long totalRevenue = revenueRegularRepository.getTotalRevenueByMonthAndThisYear(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, categoriesIdJoin, userId);
            totalExpenseRevenueResponse.setTotalRevenue(totalRevenue == null ? 0 : totalRevenue);
            return totalExpenseRevenueResponse;
        }else if (timeOption.equals(TimeOptionType.THIS_MONTH.getType()) || timeOption.equals(TimeOptionType.THIS_QUARTER.getType()) || timeOption.equals(TimeOptionType.THIS_YEAR.getType())){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(periodOfTime.getStartDate());
            int startMonth = calendar.get(Calendar.MONTH) + 1;
            calendar.setTime(periodOfTime.getEndDate());
            int endMonth = calendar.get(Calendar.MONTH) + 1;
            int year = calendar.get(Calendar.YEAR);
            Object[] result = reportExpenseRevenueRepository.getTotalExpenseRevenueByMonthAndThisYear(startMonth, endMonth, year, userId, bucketPaymentIdsJoin, categoriesIdJoin);
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

    @Override
    public List<ReportCategoryResponse> getTotalExpenseByTimeOptionAndCategory(TotalExpenseRevenueRequest req) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        List<String> bucketPaymentIds = req.getBucketPaymentIds();
        if (req.getIsSelectAllBucketPayment() != null && req.getIsSelectAllBucketPayment()) {
            bucketPaymentIds = null;
        }
        String bucketPaymentIdsJoin = bucketPaymentIds != null ? String.join(",", bucketPaymentIds) : null;
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        PeriodOfTime periodOfTime = TimestampUtil.handleTimeOption(timeOption, startDate, endDate);
//        if(timeOption.equals(TimeOptionType.TODAY.getType()) || timeOption.equals(TimeOptionType.THIS_WEEK.getType())){
//            List<Object[]> results = expenseRegularRepository.getTotalExpenseByTimeAndCategory(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
//            return convertToReportCategoryResponse(results);
//        }else if (timeOption.equals(TimeOptionType.THIS_MONTH.getType()) || timeOption.equals(TimeOptionType.THIS_QUARTER.getType()) || timeOption.equals(TimeOptionType.THIS_YEAR.getType())){
//            List<Object[]> results = expenseRegularRepository.getTotalExpenseByTimeAndCategory(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
//            return convertToReportCategoryResponse(results);
//        }
        List<Object[]> results = expenseRegularRepository.getTotalExpenseByTimeAndCategory(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
        return convertToReportCategoryResponse(results);
    }

    @Override
    public List<ReportCategoryResponse> getTotalRevenueByTimeOptionAndCategory(TotalExpenseRevenueRequest req) {
        String timeOption = req.getTimeOption();
        String startDate = req.getStartDate();
        String endDate = req.getEndDate();
        List<String> bucketPaymentIds = req.getBucketPaymentIds();
        if (req.getIsSelectAllBucketPayment() != null && req.getIsSelectAllBucketPayment()) {
            bucketPaymentIds = null;
        }
        String bucketPaymentIdsJoin = bucketPaymentIds != null ? String.join(",", bucketPaymentIds) : null;
        Users user = userCommon.getMyUserInfo();
        String userId = user.getId();
        PeriodOfTime periodOfTime = TimestampUtil.handleTimeOption(timeOption, startDate, endDate);
        List<Object[]> results = revenueRegularRepository.getTotalRevenueByTimeAndCategory(periodOfTime.getStartDate(), periodOfTime.getEndDate(), bucketPaymentIdsJoin, userId);
        return convertToReportCategoryResponse(results);
    }

    private List<ReportCategoryResponse> convertToReportCategoryResponse(List<Object[]> list) {
        return list.stream().map(obj ->
                    new ReportCategoryResponse(
                            ((Number) obj[0]).longValue(), // total
                            (String) obj[1], //type
                            (String) obj[2], // category id
                            (String) obj[3], // name
                            (String) obj[4] // icon url
                    )
        ).collect(Collectors.toList());
    }
}
