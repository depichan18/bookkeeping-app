package com.bookkeeping.service;

import com.bookkeeping.dao.AccountDAO;
import com.bookkeeping.dao.TransactionEntryDAO;
import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.entity.TransactionEntry;
import com.bookkeeping.model.BalanceSheetData;
import com.bookkeeping.model.IncomeStatementData;
import com.bookkeeping.model.TrialBalanceData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service class untuk menghasilkan laporan keuangan
 */
public class ReportService {
    
    private final AccountDAO accountDAO;
    private final TransactionEntryDAO entryDAO;
    
    public ReportService() {
        this.accountDAO = new AccountDAO();
        this.entryDAO = new TransactionEntryDAO();
    }
    
    /**
     * Generate Trial Balance
     */
    public TrialBalanceData generateTrialBalance(LocalDate asOfDate) {
        List<Account> accounts = accountDAO.findActiveAccounts();
        TrialBalanceData trialBalance = new TrialBalanceData();
        trialBalance.setAsOfDate(asOfDate);
        
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;
        
        for (Account account : accounts) {
            BigDecimal debitTotal = entryDAO.calculateTotalDebitForAccount(account.getId());
            BigDecimal creditTotal = entryDAO.calculateTotalCreditForAccount(account.getId());
            
            TrialBalanceData.TrialBalanceItem item = new TrialBalanceData.TrialBalanceItem();
            item.setAccountCode(account.getAccountCode());
            item.setAccountName(account.getAccountName());
            item.setDebitBalance(debitTotal);
            item.setCreditBalance(creditTotal);
            
            // Calculate net balance based on account type
            if (account.getAccountType().isDebitNormal()) {
                BigDecimal netBalance = debitTotal.subtract(creditTotal);
                if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
                    item.setDebitBalance(netBalance);
                    item.setCreditBalance(BigDecimal.ZERO);
                    totalDebits = totalDebits.add(netBalance);
                } else {
                    item.setDebitBalance(BigDecimal.ZERO);
                    item.setCreditBalance(netBalance.abs());
                    totalCredits = totalCredits.add(netBalance.abs());
                }
            } else {
                BigDecimal netBalance = creditTotal.subtract(debitTotal);
                if (netBalance.compareTo(BigDecimal.ZERO) > 0) {
                    item.setCreditBalance(netBalance);
                    item.setDebitBalance(BigDecimal.ZERO);
                    totalCredits = totalCredits.add(netBalance);
                } else {
                    item.setCreditBalance(BigDecimal.ZERO);
                    item.setDebitBalance(netBalance.abs());
                    totalDebits = totalDebits.add(netBalance.abs());
                }
            }
            
            trialBalance.addItem(item);
        }
        
        trialBalance.setTotalDebits(totalDebits);
        trialBalance.setTotalCredits(totalCredits);
        
        return trialBalance;
    }
    
    /**
     * Generate Balance Sheet
     */
    public BalanceSheetData generateBalanceSheet(LocalDate asOfDate) {
        BalanceSheetData balanceSheet = new BalanceSheetData();
        balanceSheet.setAsOfDate(asOfDate);
        
        // Assets
        List<Account> assetAccounts = accountDAO.findByAccountType(AccountType.ASSET);
        BigDecimal totalAssets = BigDecimal.ZERO;
        
        for (Account account : assetAccounts) {
            BigDecimal balance = calculateAccountBalance(account.getId(), asOfDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetData.BalanceSheetItem item = new BalanceSheetData.BalanceSheetItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                balanceSheet.addAsset(item);
                totalAssets = totalAssets.add(balance);
            }
        }
        balanceSheet.setTotalAssets(totalAssets);
        
        // Liabilities
        List<Account> liabilityAccounts = accountDAO.findByAccountType(AccountType.LIABILITY);
        BigDecimal totalLiabilities = BigDecimal.ZERO;
        
        for (Account account : liabilityAccounts) {
            BigDecimal balance = calculateAccountBalance(account.getId(), asOfDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetData.BalanceSheetItem item = new BalanceSheetData.BalanceSheetItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                balanceSheet.addLiability(item);
                totalLiabilities = totalLiabilities.add(balance);
            }
        }
        balanceSheet.setTotalLiabilities(totalLiabilities);
        
        // Equity
        List<Account> equityAccounts = accountDAO.findByAccountType(AccountType.EQUITY);
        BigDecimal totalEquity = BigDecimal.ZERO;
        
        for (Account account : equityAccounts) {
            BigDecimal balance = calculateAccountBalance(account.getId(), asOfDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                BalanceSheetData.BalanceSheetItem item = new BalanceSheetData.BalanceSheetItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                balanceSheet.addEquity(item);
                totalEquity = totalEquity.add(balance);
            }
        }
        balanceSheet.setTotalEquity(totalEquity);
        balanceSheet.setTotalLiabilitiesAndEquity(totalLiabilities.add(totalEquity));
        
        return balanceSheet;
    }
    
    /**
     * Generate Income Statement (Profit & Loss)
     */
    public IncomeStatementData generateIncomeStatement(LocalDate startDate, LocalDate endDate) {
        IncomeStatementData incomeStatement = new IncomeStatementData();
        incomeStatement.setStartDate(startDate);
        incomeStatement.setEndDate(endDate);
        
        // Revenue
        List<Account> revenueAccounts = accountDAO.findByAccountType(AccountType.REVENUE);
        BigDecimal totalRevenue = BigDecimal.ZERO;
        
        for (Account account : revenueAccounts) {
            BigDecimal balance = calculateAccountBalanceForPeriod(account.getId(), startDate, endDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                IncomeStatementData.IncomeStatementItem item = new IncomeStatementData.IncomeStatementItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                incomeStatement.addRevenue(item);
                totalRevenue = totalRevenue.add(balance);
            }
        }
        incomeStatement.setTotalRevenue(totalRevenue);
        
        // Cost of Goods Sold
        List<Account> cogsAccounts = accountDAO.findByAccountType(AccountType.COST_OF_GOODS_SOLD);
        BigDecimal totalCOGS = BigDecimal.ZERO;
        
        for (Account account : cogsAccounts) {
            BigDecimal balance = calculateAccountBalanceForPeriod(account.getId(), startDate, endDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                IncomeStatementData.IncomeStatementItem item = new IncomeStatementData.IncomeStatementItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                incomeStatement.addCostOfGoodsSold(item);
                totalCOGS = totalCOGS.add(balance);
            }
        }
        incomeStatement.setTotalCostOfGoodsSold(totalCOGS);
        incomeStatement.setGrossProfit(totalRevenue.subtract(totalCOGS));
        
        // Expenses
        List<Account> expenseAccounts = accountDAO.findByAccountType(AccountType.EXPENSE);
        BigDecimal totalExpenses = BigDecimal.ZERO;
        
        for (Account account : expenseAccounts) {
            BigDecimal balance = calculateAccountBalanceForPeriod(account.getId(), startDate, endDate);
            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                IncomeStatementData.IncomeStatementItem item = new IncomeStatementData.IncomeStatementItem();
                item.setAccountCode(account.getAccountCode());
                item.setAccountName(account.getAccountName());
                item.setAmount(balance);
                incomeStatement.addExpense(item);
                totalExpenses = totalExpenses.add(balance);
            }
        }
        incomeStatement.setTotalExpenses(totalExpenses);
        incomeStatement.setNetIncome(incomeStatement.getGrossProfit().subtract(totalExpenses));
        
        return incomeStatement;
    }
    
    /**
     * Calculate account balance as of specific date
     */
    private BigDecimal calculateAccountBalance(Long accountId, LocalDate asOfDate) {
        List<TransactionEntry> entries = entryDAO.findByAccountIdAndDateRange(accountId, 
            LocalDate.of(1900, 1, 1), asOfDate);
        
        BigDecimal totalDebits = entries.stream()
            .filter(entry -> entry.getDebitAmount() != null)
            .map(TransactionEntry::getDebitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCredits = entries.stream()
            .filter(entry -> entry.getCreditAmount() != null)
            .map(TransactionEntry::getCreditAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) return BigDecimal.ZERO;
        
        if (account.getAccountType().isDebitNormal()) {
            return totalDebits.subtract(totalCredits);
        } else {
            return totalCredits.subtract(totalDebits);
        }
    }
    
    /**
     * Calculate account balance for specific period
     */
    private BigDecimal calculateAccountBalanceForPeriod(Long accountId, LocalDate startDate, LocalDate endDate) {
        List<TransactionEntry> entries = entryDAO.findByAccountIdAndDateRange(accountId, startDate, endDate);
        
        BigDecimal totalDebits = entries.stream()
            .filter(entry -> entry.getDebitAmount() != null)
            .map(TransactionEntry::getDebitAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalCredits = entries.stream()
            .filter(entry -> entry.getCreditAmount() != null)
            .map(TransactionEntry::getCreditAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Account account = accountDAO.findById(accountId).orElse(null);
        if (account == null) return BigDecimal.ZERO;
        
        if (account.getAccountType().isDebitNormal()) {
            return totalDebits.subtract(totalCredits);
        } else {
            return totalCredits.subtract(totalDebits);
        }
    }
}
