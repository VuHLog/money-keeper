package com.vuhlog.money_keeper.dao;

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
}