package com.vuhlog.money_keeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "expense_limit")
@Entity
public class ExpenseLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String categoriesId;
    private String bucketPaymentIds;
    private String repeatTime;
    private Timestamp startDate;
    private Timestamp endDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
}
