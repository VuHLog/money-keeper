package com.vuhlog.money_keeper.dao;

import com.vuhlog.money_keeper.entity.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, String> {
}