package com.vuhlog.money_keeper.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vuhlog.money_keeper.entity.DictionaryBucketPayment;

public interface DictionaryBucketPaymentRepository
        extends JpaRepository<DictionaryBucketPayment, String>, JpaSpecificationExecutor<DictionaryBucketPayment> {
    @Query(
            value =
                    "SELECT er.id, er.balance, er.expense_date AS date, amount,de.icon_url, de.name AS category_name, 'expense' AS TYPE, interpretation FROM expense_regular er\n"
                            + "JOIN dictionary_expense de ON de.id = er.dictionary_expense_id\n"
                            + "WHERE er.dictionary_bucket_payment_id = :bucketPaymentId\n"
                            + "AND (:startDate IS NULL OR er.expense_date >= :startDate)\n "
                            + "AND (:endDate IS NULL OR er.expense_date <= :endDate)\n "
                            + "UNION\n"
                            + "SELECT rr.id,rr.balance, rr.revenue_date AS date,amount, dr.icon_url, dr.name AS category_name, 'revenue' AS TYPE, interpretation FROM revenue_regular rr\n"
                            + "JOIN dictionary_revenue dr ON dr.id = rr.dictionary_revenue_id\n"
                            + "WHERE rr.dictionary_bucket_payment_id = :bucketPaymentId\n"
                            + "AND (:startDate IS NULL OR rr.revenue_date >= :startDate)\n "
                            + "AND (:endDate IS NULL OR rr.revenue_date <= :endDate)\n "
                            + "ORDER BY date desc",
            nativeQuery = true)
    List<Object[]> getAllExpenseRevenueHistoryByBucketPaymentId(
            @Param("bucketPaymentId") String bucketPaymentId,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate);

    @Query("select dbp.balance from DictionaryBucketPayment dbp where dbp.id = :bucketPaymentId")
    long getBalanceByBucketPaymentId(@Param("bucketPaymentId") String bucketPaymentId);
}
