package com.vuhlog.money_keeper.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "dictionary_bucket_payment")
public class DictionaryBucketPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private Long balance; // so du ban dau
    private Long creditLimit; // han muc tin dung
    private String accountName;
    private String accountType;
    private String interpretation; // dien giai
    private boolean haveUse;

    @ManyToOne
    @JoinColumn(name = "bank_id")
    private Bank bank;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @OneToMany(mappedBy = "dictionaryBucketPayment")
    private Set<ExpenseRegular> expenseRegulars;

    @OneToMany(mappedBy = "receivedAccount")
    private Set<ExpenseRegular> expenseRegularsForReceivedAccount;

    @OneToMany(mappedBy = "dictionaryBucketPayment")
    private Set<RevenueRegular> revenueRegulars;

    @OneToMany(mappedBy = "bucketPayment")
    private Set<BalanceHistory> balanceHistories;

    @OneToMany(mappedBy = "bucketPayment")
    private Set<TransactionHistory> transactionHistories;

    @PrePersist
    public void prePersist() {
        this.haveUse = true;
    }
}
