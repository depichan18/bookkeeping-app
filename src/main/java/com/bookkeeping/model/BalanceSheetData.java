package com.bookkeeping.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk data Balance Sheet
 */
public class BalanceSheetData {
    
    private LocalDate asOfDate;
    private List<BalanceSheetItem> assets;
    private List<BalanceSheetItem> liabilities;
    private List<BalanceSheetItem> equity;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal totalEquity;
    private BigDecimal totalLiabilitiesAndEquity;
    
    public BalanceSheetData() {
        this.assets = new ArrayList<>();
        this.liabilities = new ArrayList<>();
        this.equity = new ArrayList<>();
        this.totalAssets = BigDecimal.ZERO;
        this.totalLiabilities = BigDecimal.ZERO;
        this.totalEquity = BigDecimal.ZERO;
        this.totalLiabilitiesAndEquity = BigDecimal.ZERO;
    }
    
    public void addAsset(BalanceSheetItem item) {
        this.assets.add(item);
    }
    
    public void addLiability(BalanceSheetItem item) {
        this.liabilities.add(item);
    }
    
    public void addEquity(BalanceSheetItem item) {
        this.equity.add(item);
    }
    
    // Getters and Setters
    public LocalDate getAsOfDate() {
        return asOfDate;
    }
    
    public void setAsOfDate(LocalDate asOfDate) {
        this.asOfDate = asOfDate;
    }
    
    public List<BalanceSheetItem> getAssets() {
        return assets;
    }
    
    public void setAssets(List<BalanceSheetItem> assets) {
        this.assets = assets;
    }
    
    public List<BalanceSheetItem> getLiabilities() {
        return liabilities;
    }
    
    public void setLiabilities(List<BalanceSheetItem> liabilities) {
        this.liabilities = liabilities;
    }
    
    public List<BalanceSheetItem> getEquity() {
        return equity;
    }
    
    public void setEquity(List<BalanceSheetItem> equity) {
        this.equity = equity;
    }
    
    public BigDecimal getTotalAssets() {
        return totalAssets;
    }
    
    public void setTotalAssets(BigDecimal totalAssets) {
        this.totalAssets = totalAssets;
    }
    
    public BigDecimal getTotalLiabilities() {
        return totalLiabilities;
    }
    
    public void setTotalLiabilities(BigDecimal totalLiabilities) {
        this.totalLiabilities = totalLiabilities;
    }
    
    public BigDecimal getTotalEquity() {
        return totalEquity;
    }
    
    public void setTotalEquity(BigDecimal totalEquity) {
        this.totalEquity = totalEquity;
    }
    
    public BigDecimal getTotalLiabilitiesAndEquity() {
        return totalLiabilitiesAndEquity;
    }
    
    public void setTotalLiabilitiesAndEquity(BigDecimal totalLiabilitiesAndEquity) {
        this.totalLiabilitiesAndEquity = totalLiabilitiesAndEquity;
    }
    
    public boolean isBalanced() {
        return totalAssets.compareTo(totalLiabilitiesAndEquity) == 0;
    }
    
    /**
     * Inner class untuk item Balance Sheet
     */
    public static class BalanceSheetItem {
        private String accountCode;
        private String accountName;
        private BigDecimal amount;
        
        public BalanceSheetItem() {
            this.amount = BigDecimal.ZERO;
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
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getAccountDisplay() {
            return accountCode + " - " + accountName;
        }
    }
}
