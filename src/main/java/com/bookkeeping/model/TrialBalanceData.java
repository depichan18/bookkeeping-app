package com.bookkeeping.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk data Trial Balance
 */
public class TrialBalanceData {
    
    private LocalDate asOfDate;
    private List<TrialBalanceItem> items;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    
    public TrialBalanceData() {
        this.items = new ArrayList<>();
        this.totalDebits = BigDecimal.ZERO;
        this.totalCredits = BigDecimal.ZERO;
    }
    
    public void addItem(TrialBalanceItem item) {
        this.items.add(item);
    }
    
    // Getters and Setters
    public LocalDate getAsOfDate() {
        return asOfDate;
    }
    
    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }
    
    public List<TrialBalanceItem> getItems() {
        return items;
    }
    
    public void setItems(List<TrialBalanceItem> items) {
        this.items = items;
    }
    
    public BigDecimal getTotalDebits() {
        return totalDebits;
    }
    
    public void setTotalDebits(BigDecimal totalDebits) {
        this.totalDebits = totalDebits;
    }
    
    public BigDecimal getTotalCredits() {
        return totalCredits;
    }
    
    public void setTotalCredits(BigDecimal totalCredits) {
        this.totalCredits = totalCredits;
    }
    
    public boolean isBalanced() {
        return totalDebits.compareTo(totalCredits) == 0;
    }
    
    /**
     * Inner class untuk item Trial Balance
     */
    public static class TrialBalanceItem {
        private String accountCode;
        private String accountName;
        private BigDecimal debitBalance;
        private BigDecimal creditBalance;
        
        public TrialBalanceItem() {
            this.debitBalance = BigDecimal.ZERO;
            this.creditBalance = BigDecimal.ZERO;
        }
        
        // Getters and Setters
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
        
        public BigDecimal getDebitBalance() {
            return debitBalance;
        }
        
        public void setDebitBalance(BigDecimal debitBalance) {
            this.debitBalance = debitBalance;
        }
        
        public BigDecimal getCreditBalance() {
            return creditBalance;
        }
        
        public void setCreditBalance(BigDecimal creditBalance) {
            this.creditBalance = creditBalance;
        }
        
        public String getAccountDisplay() {
            return accountCode + " - " + accountName;
        }
    }
}
