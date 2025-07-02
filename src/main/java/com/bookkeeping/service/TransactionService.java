package com.bookkeeping.service;

import com.bookkeeping.dao.TransactionDAO;
import com.bookkeeping.dao.TransactionEntryDAO;
import com.bookkeeping.dao.AccountDAO;
import com.bookkeeping.entity.Transaction;
import com.bookkeeping.entity.TransactionEntry;
import com.bookkeeping.entity.Account;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service class untuk mengelola Transaction business logic
 */
public class TransactionService {
    
    private final TransactionDAO transactionDAO;
    private final TransactionEntryDAO entryDAO;
    private final AccountDAO accountDAO;
    
    public TransactionService() {
        this.transactionDAO = new TransactionDAO();
        this.entryDAO = new TransactionEntryDAO();
        this.accountDAO = new AccountDAO();
    }
    
    /**
     * Membuat transaksi baru
     */
    public Transaction createTransaction(String description, LocalDate transactionDate, String reference) {
        String transactionNumber = transactionDAO.generateTransactionNumber();
        
        Transaction transaction = new Transaction(transactionNumber, transactionDate, description);
        transaction.setReference(reference);
        
        return transactionDAO.save(transaction);
    }
    
    /**
     * Menambah entry ke transaksi
     */
    public void addTransactionEntry(Long transactionId, Long accountId, BigDecimal debitAmount, BigDecimal creditAmount, String description) {
        Optional<Transaction> transactionOpt = transactionDAO.findById(transactionId);
        Optional<Account> accountOpt = accountDAO.findById(accountId);
        
        if (transactionOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found with ID: " + transactionId);
        }
        
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Account not found with ID: " + accountId);
        }
        
        // Validasi hanya boleh ada debit atau credit, tidak keduanya
        if ((debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) && 
            (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0)) {
            throw new IllegalArgumentException("Entry cannot have both debit and credit amounts");
        }
        
        if ((debitAmount == null || debitAmount.compareTo(BigDecimal.ZERO) <= 0) && 
            (creditAmount == null || creditAmount.compareTo(BigDecimal.ZERO) <= 0)) {
            throw new IllegalArgumentException("Entry must have either debit or credit amount");
        }
        
        Transaction transaction = transactionOpt.get();
        Account account = accountOpt.get();
        
        TransactionEntry entry = new TransactionEntry(account, debitAmount, creditAmount, description);
        transaction.addEntry(entry);
        
        transactionDAO.save(transaction);
        
        // Update account balance
        updateAccountBalance(account, debitAmount, creditAmount);
    }
    
    /**
     * Update account balance berdasarkan entry
     */
    private void updateAccountBalance(Account account, BigDecimal debitAmount, BigDecimal creditAmount) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance = currentBalance;
        
        if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (account.getAccountType().isDebitNormal()) {
                newBalance = currentBalance.add(debitAmount);
            } else {
                newBalance = currentBalance.subtract(debitAmount);
            }
        }
        
        if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (account.getAccountType().isCreditNormal()) {
                newBalance = currentBalance.add(creditAmount);
            } else {
                newBalance = currentBalance.subtract(creditAmount);
            }
        }
        
        account.setBalance(newBalance);
        accountDAO.save(account);
    }
    
    /**
     * Membuat transaksi lengkap dengan entries
     */
    public Transaction createCompleteTransaction(String description, LocalDate transactionDate, 
                                               String reference, List<TransactionEntryData> entries) {
        Transaction transaction = createTransaction(description, transactionDate, reference);
        
        for (TransactionEntryData entryData : entries) {
            addTransactionEntry(transaction.getId(), entryData.accountId, 
                              entryData.debitAmount, entryData.creditAmount, entryData.description);
        }
        
        // Validasi transaksi harus balance
        transaction = transactionDAO.findById(transaction.getId()).orElseThrow();
        if (!transaction.isBalanced()) {
            // Rollback transaction
            transactionDAO.delete(transaction.getId());
            throw new IllegalArgumentException("Transaction is not balanced. Total debits must equal total credits.");
        }
        
        return transaction;
    }
    
    /**
     * Update transaksi
     */
    public Transaction updateTransaction(Long id, String description, LocalDate transactionDate, String reference) {
        Optional<Transaction> existingTransaction = transactionDAO.findById(id);
        if (existingTransaction.isEmpty()) {
            throw new IllegalArgumentException("Transaction not found with ID: " + id);
        }
        
        Transaction transaction = existingTransaction.get();
        transaction.setDescription(description);
        transaction.setTransactionDate(transactionDate);
        transaction.setReference(reference);
        
        return transactionDAO.save(transaction);
    }
    
    /**
     * Menghapus transaksi
     */
    public boolean deleteTransaction(Long id) {
        Optional<Transaction> transaction = transactionDAO.findById(id);
        if (transaction.isEmpty()) {
            return false;
        }
        
        // Reverse account balances
        Transaction trans = transaction.get();
        for (TransactionEntry entry : trans.getEntries()) {
            reverseAccountBalance(entry.getAccount(), entry.getDebitAmount(), entry.getCreditAmount());
        }
        
        return transactionDAO.delete(id);
    }
    
    /**
     * Reverse account balance saat menghapus transaksi
     */
    private void reverseAccountBalance(Account account, BigDecimal debitAmount, BigDecimal creditAmount) {
        BigDecimal currentBalance = account.getBalance();
        BigDecimal newBalance = currentBalance;
        
        if (debitAmount != null && debitAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (account.getAccountType().isDebitNormal()) {
                newBalance = currentBalance.subtract(debitAmount);
            } else {
                newBalance = currentBalance.add(debitAmount);
            }
        }
        
        if (creditAmount != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            if (account.getAccountType().isCreditNormal()) {
                newBalance = currentBalance.subtract(creditAmount);
            } else {
                newBalance = currentBalance.add(creditAmount);
            }
        }
        
        account.setBalance(newBalance);
        accountDAO.save(account);
    }
    
    /**
     * Mencari transaksi berdasarkan ID
     */
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionDAO.findById(id);
    }
    
    /**
     * Mendapatkan semua transaksi
     */
    public List<Transaction> getAllTransactions() {
        return transactionDAO.findAll();
    }
    
    /**
     * Mendapatkan transaksi berdasarkan periode
     */
    public List<Transaction> getTransactionsByDateRange(LocalDate startDate, LocalDate endDate) {
        return transactionDAO.findByDateRange(startDate, endDate);
    }
    
    /**
     * Mencari transaksi berdasarkan description
     */
    public List<Transaction> searchTransactionsByDescription(String description) {
        return transactionDAO.findByDescriptionContaining(description);
    }
    
    /**
     * Mendapatkan transaksi terbaru
     */
    public List<Transaction> getRecentTransactions(int limit) {
        return transactionDAO.findRecentTransactions(limit);
    }
    
    /**
     * Data class untuk transaction entry
     */
    public static class TransactionEntryData {
        public Long accountId;
        public BigDecimal debitAmount;
        public BigDecimal creditAmount;
        public String description;
        
        public TransactionEntryData(Long accountId, BigDecimal debitAmount, BigDecimal creditAmount, String description) {
            this.accountId = accountId;
            this.debitAmount = debitAmount;
            this.creditAmount = creditAmount;
            this.description = description;
        }
    }
}
