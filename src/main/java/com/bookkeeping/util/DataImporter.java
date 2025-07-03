package com.bookkeeping.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.service.AccountService;
import com.bookkeeping.service.TransactionService;

/**
 * Utility untuk import data dari file CSV
 */
public class DataImporter {
    
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public DataImporter() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
    }
    
    /**
     * Import accounts dari file CSV
     * Format CSV: code,name,type,balance,active,description
     */
    public void importAccountsFromCSV(String filePath) {
        System.out.println("Starting accounts import from: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int importedCount = 0;
            
            while ((line = reader.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 6) {
                        String accountCode = parts[0].trim();
                        String accountName = parts[1].trim();
                        AccountType accountType = AccountType.valueOf(parts[2].trim().toUpperCase());
                        BigDecimal balance = new BigDecimal(parts[3].trim());
                        boolean active = Boolean.parseBoolean(parts[4].trim());
                        String description = parts[5].trim();
                        
                        // Check if account already exists
                        Optional<Account> existingAccount = accountService.findAccountByCode(accountCode);
                        if (existingAccount.isEmpty()) {
                            // Create new account using service
                            Account account = accountService.createAccount(accountCode, accountName, accountType, description);
                            
                            // Set balance and active status if needed
                            if (balance.compareTo(BigDecimal.ZERO) != 0) {
                                accountService.updateAccountBalance(account.getId(), balance);
                            }
                            if (!active) {
                                accountService.toggleAccountStatus(account.getId());
                            }
                            
                            importedCount++;
                            System.out.println("✓ Imported account: " + accountCode + " - " + accountName);
                        } else {
                            System.out.println("⚠ Account already exists: " + accountCode);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing line: " + line);
                    System.err.println("Error: " + e.getMessage());
                }
            }
            
            System.out.println("✅ Accounts import completed! Imported: " + importedCount + " accounts");
            
        } catch (IOException e) {
            System.err.println("❌ Error reading accounts file: " + e.getMessage());
        }
    }
    
    /**
     * Import transactions dari file CSV
     * Format CSV: date,description,reference,account_code,debit_amount,credit_amount,entry_description
     */
    public void importTransactionsFromCSV(String filePath) {
        System.out.println("Starting transactions import from: " + filePath);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            int importedCount = 0;
            String currentTransactionRef = null;
            List<TransactionService.TransactionEntryData> currentEntries = new ArrayList<>();
            LocalDate currentDate = null;
            String currentDescription = null;
            String currentReference = null;
            
            while ((line = reader.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    String[] parts = line.split(",");
                    if (parts.length >= 7) {
                        String date = parts[0].trim();
                        String description = parts[1].trim();
                        String reference = parts[2].trim();
                        String accountCode = parts[3].trim();
                        String debitAmountStr = parts[4].trim();
                        String creditAmountStr = parts[5].trim();
                        String entryDescription = parts[6].trim();
                        
                        // Parse amounts
                        BigDecimal debitAmount = null;
                        BigDecimal creditAmount = null;
                        
                        if (!debitAmountStr.isEmpty() && !debitAmountStr.equals("0")) {
                            debitAmount = new BigDecimal(debitAmountStr);
                        }
                        if (!creditAmountStr.isEmpty() && !creditAmountStr.equals("0")) {
                            creditAmount = new BigDecimal(creditAmountStr);
                        }
                        
                        // Check if this is a new transaction
                        if (currentTransactionRef == null || !currentTransactionRef.equals(reference)) {
                            // Save previous transaction if exists
                            if (currentTransactionRef != null && !currentEntries.isEmpty()) {
                                saveTransaction(currentDate, currentDescription, currentReference, currentEntries);
                                importedCount++;
                            }
                            
                            // Start new transaction
                            currentTransactionRef = reference;
                            currentDate = LocalDate.parse(date, dateFormatter);
                            currentDescription = description;
                            currentReference = reference;
                            currentEntries = new ArrayList<>();
                        }
                        
                        // Find account
                        Optional<Account> accountOpt = accountService.findAccountByCode(accountCode);
                        if (accountOpt.isPresent()) {
                            Account account = accountOpt.get();
                            TransactionService.TransactionEntryData entryData = 
                                new TransactionService.TransactionEntryData(
                                    account.getId(), debitAmount, creditAmount, entryDescription);
                            currentEntries.add(entryData);
                        } else {
                            System.err.println("⚠ Account not found: " + accountCode);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error processing transaction line: " + line);
                    System.err.println("Error: " + e.getMessage());
                }
            }
            
            // Save the last transaction
            if (currentTransactionRef != null && !currentEntries.isEmpty()) {
                saveTransaction(currentDate, currentDescription, currentReference, currentEntries);
                importedCount++;
            }
            
            System.out.println("✅ Transactions import completed! Imported: " + importedCount + " transactions");
            
        } catch (IOException e) {
            System.err.println("❌ Error reading transactions file: " + e.getMessage());
        }
    }
    
    /**
     * Save transaction dengan entries
     */
    private void saveTransaction(LocalDate date, String description, String reference, 
                                List<TransactionService.TransactionEntryData> entries) {
        try {
            transactionService.createCompleteTransaction(description, date, reference, entries);
            System.out.println("✓ Imported transaction: " + reference + " - " + description);
        } catch (Exception e) {
            System.err.println("❌ Error saving transaction: " + reference);
            System.err.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Import semua data
     */
    public void importAllData() {
        System.out.println("🚀 Starting full data import...\n");
        
        // Import accounts first
        importAccountsFromCSV("accounts.csv");
        System.out.println();
        
        // Import transactions
        importTransactionsFromCSV("transactions.csv");
        System.out.println();
        
        System.out.println("🎉 All data import completed successfully!");
    }
    
    /**
     * Test method to check database content
     */
    public void testDatabaseContent() {
        System.out.println("=== DATABASE CONTENT TEST ===");
        
        // Test accounts
        List<Account> accounts = accountService.getAllAccounts();
        System.out.println("Total accounts in database: " + accounts.size());
        
        for (Account account : accounts) {
            System.out.println("Account: " + account.getAccountCode() + " - " + account.getAccountName() + 
                             " (Type: " + account.getAccountType() + ", Balance: " + account.getBalance() + 
                             ", Active: " + account.getIsActive() + ")");
        }
        
        // Test transactions
        List<com.bookkeeping.entity.Transaction> transactions = transactionService.getAllTransactions();
        System.out.println("\nTotal transactions in database: " + transactions.size());
        
        for (com.bookkeeping.entity.Transaction transaction : transactions) {
            System.out.println("Transaction: " + transaction.getTransactionNumber() + " - " + 
                             transaction.getDescription() + " (" + transaction.getTransactionDate() + ")");
        }
        
        System.out.println("=== END DATABASE TEST ===");
    }

    /**
     * Main method untuk menjalankan import
     */
    public static void main(String[] args) {
        DataImporter importer = new DataImporter();
        
        try {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "accounts":
                        importer.importAccountsFromCSV("accounts.csv");
                        break;
                    case "transactions":
                        importer.importTransactionsFromCSV("transactions.csv");
                        break;
                    case "test":
                        importer.testDatabaseContent();
                        break;
                    case "all":
                    default:
                        importer.importAllData();
                        break;
                }
            } else {
                importer.importAllData();
            }
        } catch (Exception e) {
            System.err.println("❌ Import failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Tutup database connection
            DatabaseUtil.closeEntityManagerFactory();
        }
    }
}