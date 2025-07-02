package com.bookkeeping.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Entity untuk Chart of Accounts (CoA)
 */
@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Account code cannot be blank")
    @Size(max = 10, message = "Account code cannot exceed 10 characters")
    @Column(name = "account_code", unique = true, nullable = false)
    private String accountCode;
    
    @NotBlank(message = "Account name cannot be blank")
    @Size(max = 100, message = "Account name cannot exceed 100 characters")
    @Column(name = "account_name", nullable = false)
    private String accountName;
    
    @NotNull(message = "Account type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType;
    
    @Column(name = "parent_id")
    private Long parentId;
    
    @Column(name = "balance", precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionEntry> transactionEntries = new ArrayList<>();

    // Constructors
    public Account() {}

    public Account(String accountCode, String accountName, AccountType accountType) {
        this.accountCode = accountCode;
        this.accountName = accountName;
        this.accountType = accountType;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<TransactionEntry> getTransactionEntries() {
        return transactionEntries;
    }

    public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
        this.transactionEntries = transactionEntries;
    }

    // Business Methods
    
    /**
     * Menambah balance akun
     */
    public void addToBalance(BigDecimal amount) {
        if (amount != null) {
            this.balance = this.balance.add(amount);
        }
    }
    
    /**
     * Mengurangi balance akun
     */
    public void subtractFromBalance(BigDecimal amount) {
        if (amount != null) {
            this.balance = this.balance.subtract(amount);
        }
    }
    
    /**
     * Mendapatkan balance normal (debit/credit)
     */
    public String getNormalBalance() {
        return accountType.isDebitNormal() ? "Debit" : "Credit";
    }

    @Override
    public String toString() {
        return accountCode + " - " + accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account account = (Account) o;
        return accountCode != null && accountCode.equals(account.accountCode);
    }

    @Override
    public int hashCode() {
        return accountCode != null ? accountCode.hashCode() : 0;
    }
}
