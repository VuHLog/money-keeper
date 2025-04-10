package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.ReportExpenseRevenue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportExpenseRevenueRepository extends JpaRepository<ReportExpenseRevenue, String> {
    Optional<ReportExpenseRevenue> findByMonthAndYearAndBucketPaymentId(int month, int year, String bucketPaymentId);
}