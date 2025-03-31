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
    @Query("UPDATE RevenueRegular r set r.balance = r.balance + :amount where r.dictionaryBucketPayment.id = :bucketPaymentId and r.revenueDate > :datetime")
    void updateBalanceGreaterThanDatetime(
            @Param("bucketPaymentId") String bucketPaymentId,
            @Param("amount") long amount,
            @Param("datetime") Timestamp datetime
    );
}