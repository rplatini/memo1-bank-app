package com.aninfo.model;

import javax.persistence.*;

@Entity
public class Transaction {
    public enum TransactionType {
        DEPOSIT,
        WITHDRAW,
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double sum;
    private TransactionType type;
    @ManyToOne
    @JoinColumn(name = "cbu")
    private Account account;
    public Transaction(){
    }

    public Transaction(Double sum, TransactionType type) {
        this.sum = sum;
        this.type = type;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() { return id; }

    public Double getSum() { return sum; }

    public TransactionType getType() {
        return type;
    }
}
