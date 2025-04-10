package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.RevenueRegular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

public interface RevenueRegularRepository extends JpaRepository<RevenueRegular, String>, JpaSpecificationExecutor<RevenueRegular> {
    @Modifying
    @Transactional
    @Query("UPDATE RevenueRegular e SET e.dictionaryRevenue = NULL WHERE e.dictionaryRevenue.id = :dictionaryRevenueId")
    void unsetDictionaryRevenueInRevenueRegular(@Param("dictionaryRevenueId") String dictionaryRevenueId);

    @Modifying
    @Transactional
    @Query("UPDATE RevenueRegular e SET e.dictionaryBucketPayment = NULL WHERE e.dictionaryBucketPayment.id = :dictionaryBucketPaymentId")
    void unsetDictionaryBucketPaymentInRevenueRegular(@Param("dictionaryBucketPaymentId") String dictionaryBucketPaymentId);

    @Modifying
    @Transactional
    @Query("UPDATE RevenueRegular e SET e.tripEvent = NULL WHERE e.tripEvent.id = :tripEventId")
    void unsetTripEventInRevenueRegular(@Param("tripEventId") String tripEventId);

    @Modifying
    @Transactional
    @Query("UPDATE RevenueRegular e SET e.collectMoneyWho = NULL WHERE e.collectMoneyWho.id = :collectMoneyWhoId")
    void unsetCollectMoneyWhoInRevenueRegular(@Param("collectMoneyWhoId") String collectMoneyWhoId);

    @Modifying
    @Transactional
    @Query("UPDATE RevenueRegular r set r.balance = r.balance + :amount\n" +
            "where r.dictionaryBucketPayment.id = :bucketPaymentId and (:startDate IS NULL OR r.revenueDate > :startDate) and (:endDate IS NULL OR r.revenueDate < :endDate)")
    void updateBalanceByDatetime(
            @Param("bucketPaymentId") String bucketPaymentId,
            @Param("amount") long amount,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );

    @Query(value ="SELECT SUM(rr.amount) total_revenue\n" +
            "FROM revenue_regular rr\n" +
            "JOIN dictionary_bucket_payment dbp ON dbp.id = rr.dictionary_bucket_payment_id\n" +
            "WHERE dbp.user_id = :userId\n" +
            "AND (:startDate IS NULL OR rr.revenue_date >= :startDate) AND (:endDate IS NULL OR rr.revenue_date <= :endDate)\n" +
            "AND (:bucketPaymentId IS NULL OR dbp.id = :bucketPaymentId)", nativeQuery = true)
    Long getTotalRevenueByMonthAndThisYear(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("bucketPaymentId") String bucketPaymentId,
            @Param("userId") String userId
    );
}