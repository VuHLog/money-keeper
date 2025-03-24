package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.ExpenseRegular;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

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
}