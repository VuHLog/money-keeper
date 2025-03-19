package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.DictionaryExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DictionaryExpenseRepository extends JpaRepository<DictionaryExpense, String>, JpaSpecificationExecutor<DictionaryExpense> {
}