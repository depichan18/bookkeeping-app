package com.bookkeeping.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity untuk transaksi (Journal Entry)
 */
@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Transaction number cannot be blank")
    @Size(max = 20, message = "Transaction number cannot exceed 20 characters")
    @Column(name = "transaction_number", unique = true, nullable = false)
    private String transactionNumber;
    
    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "reference")
    private String reference;
    
    @Column(name = "total_amount", precision = 15, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TransactionEntry> entries = new ArrayList<>();

    // Constructors
    public Transaction() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Transaction(String transactionNumber, LocalDate transactionDate, String description) {
        this();
        this.transactionNumber = transactionNumber;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TransactionEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TransactionEntry> entries) {
        this.entries = entries;
    }

    // Business Methods
    
    /**
     * Menambah entry ke transaksi
     */
    public void addEntry(TransactionEntry entry) {
        entries.add(entry);
        entry.setTransaction(this);
        calculateTotalAmount();
    }
    
    /**
     * Menghapus entry dari transaksi
     */
    public void removeEntry(TransactionEntry entry) {
        entries.remove(entry);
        entry.setTransaction(null);
        calculateTotalAmount();
    }
    
    /**
     * Menghitung total amount dari semua entries
     */
    public void calculateTotalAmount() {
        this.totalAmount = entries.stream()
                .filter(entry -> entry.getDebitAmount() != null)
                .map(TransactionEntry::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Validasi apakah transaksi balance (debit = credit)
     */
    public boolean isBalanced() {
        BigDecimal totalDebit = entries.stream()
                .filter(entry -> entry.getDebitAmount() != null)
                .map(TransactionEntry::getDebitAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalCredit = entries.stream()
                .filter(entry -> entry.getCreditAmount() != null)
                .map(TransactionEntry::getCreditAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        return totalDebit.compareTo(totalCredit) == 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return transactionNumber + " - " + description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transaction)) return false;
        Transaction that = (Transaction) o;
        return transactionNumber != null && transactionNumber.equals(that.transactionNumber);
    }

    @Override
    public int hashCode() {
        return transactionNumber != null ? transactionNumber.hashCode() : 0;
    }
}
