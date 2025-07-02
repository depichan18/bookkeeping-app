package com.bookkeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Entity untuk detail transaksi (Journal Entry Lines)
 */
@Entity
@Table(name = "transaction_entries")
public class TransactionEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Transaction is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;
    
    @NotNull(message = "Account is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;
    
    @Column(name = "debit_amount", precision = 15, scale = 2)
    private BigDecimal debitAmount;
    
    @Column(name = "credit_amount", precision = 15, scale = 2)
    private BigDecimal creditAmount;
    
    @Column(name = "description")
    private String description;

    // Constructors
    public TransactionEntry() {}

    public TransactionEntry(Account account, BigDecimal debitAmount, BigDecimal creditAmount) {
        this.account = account;
        this.debitAmount = debitAmount;
        this.creditAmount = creditAmount;
    }

    public TransactionEntry(Account account, BigDecimal debitAmount, BigDecimal creditAmount, String description) {
        this(account, debitAmount, creditAmount);
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public BigDecimal getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(BigDecimal debitAmount) {
        this.debitAmount = debitAmount;
        if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.creditAmount = null;
        }
    }

    public BigDecimal getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(BigDecimal creditAmount) {
        this.creditAmount = creditAmount;
        if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            this.debitAmount = null;
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Business Methods
    
    /**
     * Mendapatkan amount (debit atau credit)
     */
    public BigDecimal getAmount() {
        return debitAmount != null ? debitAmount : 
               (creditAmount != null ? creditAmount : BigDecimal.ZERO);
    }
    
    /**
     * Mengecek apakah entry ini adalah debit
     */
    public boolean isDebit() {
        return debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Mengecek apakah entry ini adalah credit
     */
    public boolean isCredit() {
        return creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    /**
     * Mendapatkan tipe entry (Debit/Credit)
     */
    public String getEntryType() {
        return isDebit() ? "Debit" : "Credit";
    }

    @Override
    public String toString() {
        String accountInfo = account != null ? account.getAccountCode() + " - " + account.getAccountName() : "No Account";
        String amountInfo = isDebit() ? "Dr. " + debitAmount : "Cr. " + creditAmount;
        return accountInfo + " " + amountInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransactionEntry)) return false;
        TransactionEntry that = (TransactionEntry) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
