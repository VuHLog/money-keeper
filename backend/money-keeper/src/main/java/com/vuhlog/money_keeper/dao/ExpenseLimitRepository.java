package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.ExpenseLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpenseLimitRepository extends JpaRepository<ExpenseLimit, String>, JpaSpecificationExecutor<ExpenseLimit> {
}