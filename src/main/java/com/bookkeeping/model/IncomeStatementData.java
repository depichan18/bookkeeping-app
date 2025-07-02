package com.bookkeeping.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Model untuk data Income Statement (Profit & Loss)
 */
public class IncomeStatementData {
    
    private LocalDate startDate;
    private LocalDate endDate;
    private List<IncomeStatementItem> revenues;
    private List<IncomeStatementItem> costOfGoodsSold;
    private List<IncomeStatementItem> expenses;
    private BigDecimal totalRevenue;
    private BigDecimal totalCostOfGoodsSold;
    private BigDecimal grossProfit;
    private BigDecimal totalExpenses;
    private BigDecimal netIncome;
    
    public IncomeStatementData() {
        this.revenues = new ArrayList<>();
        this.costOfGoodsSold = new ArrayList<>();
        this.expenses = new ArrayList<>();
        this.totalRevenue = BigDecimal.ZERO;
        this.totalCostOfGoodsSold = BigDecimal.ZERO;
        this.grossProfit = BigDecimal.ZERO;
        this.totalExpenses = BigDecimal.ZERO;
        this.netIncome = BigDecimal.ZERO;
    }
    
    public void addRevenue(IncomeStatementItem item) {
        this.revenues.add(item);
    }
    
    public void addCostOfGoodsSold(IncomeStatementItem item) {
        this.costOfGoodsSold.add(item);
    }
    
    public void addExpense(IncomeStatementItem item) {
        this.expenses.add(item);
    }
    
    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    
    public List<IncomeStatementItem> getRevenues() {
        return revenues;
    }
    
    public void setRevenues(List<IncomeStatementItem> revenues) {
        this.revenues = revenues;
    }
    
    public List<IncomeStatementItem> getCostOfGoodsSold() {
        return costOfGoodsSold;
    }
    
    public void setCostOfGoodsSold(List<IncomeStatementItem> costOfGoodsSold) {
        this.costOfGoodsSold = costOfGoodsSold;
    }
    
    public List<IncomeStatementItem> getExpenses() {
        return expenses;
    }
    
    public void setExpenses(List<IncomeStatementItem> expenses) {
        this.expenses = expenses;
    }
    
    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    
    public BigDecimal getTotalCostOfGoodsSold() {
        return totalCostOfGoodsSold;
    }
    
    public void setTotalCostOfGoodsSold(BigDecimal totalCostOfGoodsSold) {
        this.totalCostOfGoodsSold = totalCostOfGoodsSold;
    }
    
    public BigDecimal getGrossProfit() {
        return grossProfit;
    }
    
    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }
    
    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }
    
    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
    
    public BigDecimal getNetIncome() {
        return netIncome;
    }
    
    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }
    
    public String getPeriodDisplay() {
        return "For the period from " + startDate + " to " + endDate;
    }
    
    /**
     * Inner class untuk item Income Statement
     */
    public static class IncomeStatementItem {
        private String accountCode;
        private String accountName;
        private BigDecimal amount;
        
        public IncomeStatementItem() {
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
