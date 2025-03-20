package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.DictionaryBucketPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictionaryBucketPaymentRepository extends JpaRepository<DictionaryBucketPayment, String>, JpaSpecificationExecutor<DictionaryBucketPayment> {
}