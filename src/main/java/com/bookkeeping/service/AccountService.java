package com.bookkeeping.service;

import com.bookkeeping.dao.AccountDAO;
import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service class untuk mengelola Account business logic
 */
public class AccountService {
    
    private final AccountDAO accountDAO;
    
    public AccountService() {
        this.accountDAO = new AccountDAO();
    }
    
    /**
     * Membuat account baru
     */
    public Account createAccount(String accountCode, String accountName, AccountType accountType, String description) {
        // Validasi account code sudah ada
        if (accountDAO.existsByAccountCode(accountCode)) {
            throw new IllegalArgumentException("Account code already exists: " + accountCode);
        }
        
        Account account = new Account(accountCode, accountName, accountType);
        account.setDescription(description);
        
        return accountDAO.save(account);
    }
    
    /**
     * Update account
     */
    public Account updateAccount(Long id, String accountCode, String accountName, AccountType accountType, String description) {
        Optional<Account> existingAccount = accountDAO.findById(id);
        if (existingAccount.isEmpty()) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        
        Account account = existingAccount.get();
        
        // Jika account code berubah, cek apakah yang baru sudah ada
        if (!account.getAccountCode().equals(accountCode) && accountDAO.existsByAccountCode(accountCode)) {
            throw new IllegalArgumentException("Account code already exists: " + accountCode);
        }
        
        account.setAccountCode(accountCode);
        account.setAccountName(accountName);
        account.setAccountType(accountType);
        account.setDescription(description);
        
        return accountDAO.save(account);
    }
    
    /**
     * Menghapus account
     */
    public boolean deleteAccount(Long id) {
        Optional<Account> account = accountDAO.findById(id);
        if (account.isEmpty()) {
            return false;
        }
        
        // Cek apakah account memiliki transaksi
        if (!account.get().getTransactionEntries().isEmpty()) {
            throw new IllegalStateException("Cannot delete account with existing transactions");
        }
        
        return accountDAO.delete(id);
    }
    
    /**
     * Mencari account berdasarkan ID
     */
    public Optional<Account> findAccountById(Long id) {
        return accountDAO.findById(id);
    }
    
    /**
     * Mencari account berdasarkan account code
     */
    public Optional<Account> findAccountByCode(String accountCode) {
        return accountDAO.findByAccountCode(accountCode);
    }
    
    /**
     * Mendapatkan semua accounts
     */
    public List<Account> getAllAccounts() {
        return accountDAO.findAll();
    }
    
    /**
     * Mendapatkan accounts berdasarkan type
     */
    public List<Account> getAccountsByType(AccountType accountType) {
        return accountDAO.findByAccountType(accountType);
    }
    
    /**
     * Mendapatkan accounts yang aktif
     */
    public List<Account> getActiveAccounts() {
        return accountDAO.findActiveAccounts();
    }
    
    /**
     * Mencari accounts berdasarkan nama
     */
    public List<Account> searchAccountsByName(String name) {
        return accountDAO.findByAccountNameContaining(name);
    }
    
    /**
     * Update balance account
     */
    public void updateAccountBalance(Long accountId, BigDecimal newBalance) {
        accountDAO.updateBalance(accountId, newBalance);
    }
    
    /**
     * Activate/Deactivate account
     */
    public Account toggleAccountStatus(Long id) {
        Optional<Account> existingAccount = accountDAO.findById(id);
        if (existingAccount.isEmpty()) {
            throw new IllegalArgumentException("Account not found with ID: " + id);
        }
        
        Account account = existingAccount.get();
        account.setIsActive(!account.getIsActive());
        
        return accountDAO.save(account);
    }
    
    /**
     * Inisialisasi Chart of Accounts default
     */
    public void initializeDefaultChartOfAccounts() {
        // Cek apakah sudah ada accounts
        if (!accountDAO.findAll().isEmpty()) {
            return; // Sudah ada data
        }
        
        // ASSETS
        createAccount("1000", "Current Assets", AccountType.ASSET, "Current Assets");
        createAccount("1100", "Cash", AccountType.ASSET, "Cash and Cash Equivalents");
        createAccount("1200", "Accounts Receivable", AccountType.ASSET, "Money owed by customers");
        createAccount("1300", "Inventory", AccountType.ASSET, "Goods for sale");
        createAccount("1400", "Prepaid Expenses", AccountType.ASSET, "Expenses paid in advance");
        
        createAccount("1500", "Fixed Assets", AccountType.ASSET, "Fixed Assets");
        createAccount("1510", "Equipment", AccountType.ASSET, "Office and business equipment");
        createAccount("1520", "Accumulated Depreciation - Equipment", AccountType.ASSET, "Depreciation on equipment");
        createAccount("1530", "Vehicles", AccountType.ASSET, "Company vehicles");
        createAccount("1540", "Accumulated Depreciation - Vehicles", AccountType.ASSET, "Depreciation on vehicles");
        
        // LIABILITIES
        createAccount("2000", "Current Liabilities", AccountType.LIABILITY, "Current Liabilities");
        createAccount("2100", "Accounts Payable", AccountType.LIABILITY, "Money owed to suppliers");
        createAccount("2200", "Accrued Expenses", AccountType.LIABILITY, "Expenses incurred but not yet paid");
        createAccount("2300", "Unearned Revenue", AccountType.LIABILITY, "Revenue received in advance");
        
        createAccount("2500", "Long-term Liabilities", AccountType.LIABILITY, "Long-term Liabilities");
        createAccount("2510", "Bank Loan", AccountType.LIABILITY, "Long-term bank loans");
        
        // EQUITY
        createAccount("3000", "Owner's Equity", AccountType.EQUITY, "Owner's Equity");
        createAccount("3100", "Capital", AccountType.EQUITY, "Owner's initial investment");
        createAccount("3200", "Retained Earnings", AccountType.EQUITY, "Accumulated profits");
        createAccount("3300", "Owner's Drawings", AccountType.EQUITY, "Owner's withdrawals");
        
        // REVENUE
        createAccount("4000", "Revenue", AccountType.REVENUE, "Revenue");
        createAccount("4100", "Sales Revenue", AccountType.REVENUE, "Revenue from sales");
        createAccount("4200", "Service Revenue", AccountType.REVENUE, "Revenue from services");
        createAccount("4300", "Other Income", AccountType.REVENUE, "Miscellaneous income");
        
        // EXPENSES
        createAccount("5000", "Operating Expenses", AccountType.EXPENSE, "Operating Expenses");
        createAccount("5100", "Salaries Expense", AccountType.EXPENSE, "Employee salaries");
        createAccount("5200", "Rent Expense", AccountType.EXPENSE, "Office rent");
        createAccount("5300", "Utilities Expense", AccountType.EXPENSE, "Electricity, water, internet");
        createAccount("5400", "Office Supplies Expense", AccountType.EXPENSE, "Office supplies");
        createAccount("5500", "Depreciation Expense", AccountType.EXPENSE, "Asset depreciation");
        createAccount("5600", "Marketing Expense", AccountType.EXPENSE, "Marketing and advertising");
        createAccount("5700", "Professional Fees", AccountType.EXPENSE, "Legal and professional services");
        createAccount("5800", "Travel Expense", AccountType.EXPENSE, "Business travel");
        createAccount("5900", "Miscellaneous Expense", AccountType.EXPENSE, "Other expenses");
        
        // COST OF GOODS SOLD
        createAccount("6000", "Cost of Goods Sold", AccountType.COST_OF_GOODS_SOLD, "Cost of Goods Sold");
        createAccount("6100", "Purchases", AccountType.COST_OF_GOODS_SOLD, "Inventory purchases");
        createAccount("6200", "Freight In", AccountType.COST_OF_GOODS_SOLD, "Shipping costs on purchases");
    }
}
