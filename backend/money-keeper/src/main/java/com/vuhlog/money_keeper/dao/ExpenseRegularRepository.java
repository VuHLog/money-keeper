package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.ExpenseRegular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.sql.Timestamp;

public interface ExpenseRegularRepository extends JpaRepository<ExpenseRegular, String>, JpaSpecificationExecutor<ExpenseRegular> {
    @Modifying
    @Transactional
    @Query("UPDATE ExpenseRegular e SET e.dictionaryExpense = NULL WHERE e.dictionaryExpense.id = :dictionaryExpenseId")
    void unsetDictionaryExpenseInExpenseRegular(@Param("dictionaryExpenseId") String dictionaryExpenseId);

    @Modifying
    @Transactional
    @Query("UPDATE ExpenseRegular e SET e.dictionaryBucketPayment = NULL WHERE e.dictionaryBucketPayment.id = :dictionaryBucketPaymentId")
    void unsetDictionaryBucketPaymentInExpenseRegular(@Param("dictionaryBucketPaymentId") String dictionaryBucketPaymentId);

    @Modifying
    @Transactional
    @Query("UPDATE ExpenseRegular e SET e.tripEvent = NULL WHERE e.tripEvent.id = :tripEventId")
    void unsetTripEventInExpenseRegular(@Param("tripEventId") String tripEventId);

    @Modifying
    @Transactional
    @Query("UPDATE ExpenseRegular e SET e.beneficiary = NULL WHERE e.beneficiary.id = :beneficiaryId")
    void unsetBeneficiaryInExpenseRegular(@Param("beneficiaryId") String beneficiaryId);

    @Modifying
    @Transactional
    @Query("UPDATE ExpenseRegular e set e.balance = e.balance + :amount\n" +
            "where e.dictionaryBucketPayment.id = :bucketPaymentId and (:startDate IS NULL OR e.expenseDate > :startDate) and (:endDate IS NULL OR e.expenseDate < :endDate)")
    void updateBalanceByDatetime(
            @Param("bucketPaymentId") String bucketPaymentId,
            @Param("amount") long amount,
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate
    );


    @Query(value ="SELECT SUM(er.amount) total_expense\n" +
            "FROM expense_regular er\n" +
            "JOIN dictionary_bucket_payment dbp ON dbp.id = er.dictionary_bucket_payment_id\n" +
            "WHERE dbp.user_id = :userId\n" +
            "AND (:startDate IS NULL OR er.expense_date >= :startDate) AND (:endDate IS NULL OR er.expense_date <= :endDate)\n" +
            "AND (:bucketPaymentIdsJoin IS NULL OR FIND_IN_SET(dbp.id, :bucketPaymentIdsJoin))", nativeQuery = true)
    Long getTotalExpenseByMonthAndThisYear(
            @Param("startDate") Timestamp startDate,
            @Param("endDate") Timestamp endDate,
            @Param("bucketPaymentIdsJoin") String bucketPaymentIdsJoin,
            @Param("userId") String userId
    );
}