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
    private Long initialBalance; // so du ban dau
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

    @PrePersist
    public void prePersist() {
        this.haveUse = true;
    }
}
