package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.dto.response.responseinterface.TotalExpenseRevenueForExpenseRevenueSituation;
import com.vuhlog.money_keeper.entity.ReportExpenseRevenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReportExpenseRevenueRepository extends JpaRepository<ReportExpenseRevenue, String> {
    Optional<ReportExpenseRevenue> findByMonthAndYearAndBucketPaymentIdAndCategoryIdAndType(int month, int year, String bucketPaymentId, String categoryId, String type);


    @Query(value = "SELECT SUM(rer.total_expense) AS total_expense, SUM(rer.total_revenue) AS total_revenue\n" +
            "FROM report_expense_revenue rer\n" +
            "WHERE rer.user_id = :userId\n" +
            "AND (:bucketPaymentIdsJoin IS NULL OR FIND_IN_SET(rer.bucket_payment_id, :bucketPaymentIdsJoin))\n" +
            "AND (:categoriesIdJoin IS NULL OR FIND_IN_SET(rer.category_id, :categoriesIdJoin))" +
            "AND (:startMonth IS NULL OR rer.month >= :startMonth) AND (:endMonth IS NULL OR rer.month <= :endMonth)\n" +
            "AND (:year IS NULL OR YEAR = :year)\n", nativeQuery = true)
    Object[] getTotalExpenseRevenueByMonthAndThisYear(
            @Param("startMonth")int startMonth,
            @Param("endMonth") int endMonth,
            @Param("year") int year,
            @Param("userId") String userId,
            @Param("bucketPaymentIdsJoin") String bucketPaymentIdsJoin,
            @Param("categoriesIdJoin") String categoriesIdJoin
    );

    @Query(value = "SELECT rer.month as unit, SUM(rer.total_expense) as totalExpense, SUM(rer.total_revenue) as totalRevenue\n" +
            "from report_expense_revenue rer\n" +
            "WHERE YEAR = :year\n" +
            "AND user_id = :userId\n" +
            "AND (:bucketPaymentIds IS NULL OR FIND_IN_SET(rer.bucket_payment_id, :bucketPaymentIds))\n" +
            "GROUP BY rer.month\n" +
            "ORDER BY rer.month asc", nativeQuery = true)
    List<TotalExpenseRevenueForExpenseRevenueSituation> getReportExpenseRevenueByYearOrderByMonth(
            @Param("userId") String userId,
            @Param("bucketPaymentIds") String bucketPaymentIds,
            @Param("year") Integer year
    );

    @Query(value = "SELECT rer.year as unit, SUM(rer.total_expense) AS totalExpense, SUM(rer.total_revenue) AS totalRevenue\n" +
            "from report_expense_revenue rer\n" +
            "WHERE (:endYear IS NULL OR  rer.year <= :endYear) AND (:startYear IS NULL OR rer.year >= :startYear )\n" +
            "AND user_id = :userId\n" +
            "AND (:bucketPaymentIds IS NULL OR FIND_IN_SET(rer.bucket_payment_id, :bucketPaymentIds))\n" +
            "GROUP BY rer.year\n" +
            "ORDER BY rer.year asc ", nativeQuery = true)
    List<TotalExpenseRevenueForExpenseRevenueSituation> getReportExpenseRevenueByYearOrderByYear(
            @Param("userId") String userId,
            @Param("bucketPaymentIds") String bucketPaymentIds,
            @Param("startYear") Integer startYear,
            @Param("endYear") Integer endYear
    );

    @Query(value = "SELECT CEIL(rer.month / 3) AS unit, SUM(rer.total_expense) AS totalExpense, SUM(rer.total_revenue) AS totalRevenue\n" +
            "from report_expense_revenue rer\n" +
            "WHERE YEAR = :year\n" +
            "AND user_id = :userId\n" +
            "AND (:bucketPaymentIds IS NULL OR FIND_IN_SET(rer.bucket_payment_id, :bucketPaymentIds))\n" +
            "GROUP BY CEIL(rer.month / 3)\n" +
            "ORDER BY unit asc", nativeQuery = true)
    List<TotalExpenseRevenueForExpenseRevenueSituation> getReportExpenseRevenueByYearOrderByQuarter(
            @Param("userId") String userId,
            @Param("bucketPaymentIds") String bucketPaymentIds,
            @Param("year") Integer year
    );
}