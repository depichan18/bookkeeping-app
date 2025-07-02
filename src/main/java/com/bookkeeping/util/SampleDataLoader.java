package com.bookkeeping.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.service.AccountService;
import com.bookkeeping.service.TransactionService;

/**
 * Utility untuk mengisi data sample ke database
 */
public class SampleDataLoader {
    
    private AccountService accountService;
    private TransactionService transactionService;
    
    public SampleDataLoader() {
        this.accountService = new AccountService();
        this.transactionService = new TransactionService();
    }
    
    /**
     * Load semua data sample
     */
    public void loadAllSampleData() {
        System.out.println("Loading sample data...");
        
        // 1. Create Chart of Accounts
        createChartOfAccounts();
        
        // 2. Create sample transactions
        createSampleTransactions();
        
        System.out.println("Sample data loaded successfully!");
    }
    
    /**
     * Membuat Chart of Accounts (CoA) standard
     */
    private void createChartOfAccounts() {
        System.out.println("Creating Chart of Accounts...");
        
        List<Account> accounts = new ArrayList<>();
        
        // ASSETS (1000-1999)
        accounts.add(new Account("1001", "Kas", AccountType.ASSET));
        accounts.add(new Account("1002", "Bank BCA", AccountType.ASSET));
        accounts.add(new Account("1003", "Bank Mandiri", AccountType.ASSET));
        accounts.add(new Account("1101", "Piutang Usaha", AccountType.ASSET));
        accounts.add(new Account("1201", "Persediaan Barang", AccountType.ASSET));
        accounts.add(new Account("1301", "Peralatan Kantor", AccountType.ASSET));
        accounts.add(new Account("1302", "Akumulasi Penyusutan Peralatan", AccountType.ASSET));
        accounts.add(new Account("1401", "Kendaraan", AccountType.ASSET));
        accounts.add(new Account("1402", "Akumulasi Penyusutan Kendaraan", AccountType.ASSET));
        
        // LIABILITIES (2000-2999)
        accounts.add(new Account("2001", "Hutang Usaha", AccountType.LIABILITY));
        accounts.add(new Account("2002", "Hutang Bank", AccountType.LIABILITY));
        accounts.add(new Account("2101", "Hutang Gaji", AccountType.LIABILITY));
        accounts.add(new Account("2102", "Hutang PPh 21", AccountType.LIABILITY));
        accounts.add(new Account("2103", "Hutang PPN", AccountType.LIABILITY));
        
        // EQUITY (3000-3999)
        accounts.add(new Account("3001", "Modal Pemilik", AccountType.EQUITY));
        accounts.add(new Account("3002", "Laba Ditahan", AccountType.EQUITY));
        accounts.add(new Account("3003", "Prive", AccountType.EQUITY));
        
        // REVENUE (4000-4999)
        accounts.add(new Account("4001", "Pendapatan Penjualan", AccountType.REVENUE));
        accounts.add(new Account("4002", "Pendapatan Jasa", AccountType.REVENUE));
        accounts.add(new Account("4003", "Pendapatan Lain-lain", AccountType.REVENUE));
        
        // EXPENSES (5000-5999)
        accounts.add(new Account("5001", "Beban Pokok Penjualan", AccountType.EXPENSE));
        accounts.add(new Account("5101", "Beban Gaji", AccountType.EXPENSE));
        accounts.add(new Account("5102", "Beban Listrik", AccountType.EXPENSE));
        accounts.add(new Account("5103", "Beban Telepon", AccountType.EXPENSE));
        accounts.add(new Account("5104", "Beban Sewa", AccountType.EXPENSE));
        accounts.add(new Account("5105", "Beban Penyusutan", AccountType.EXPENSE));
        accounts.add(new Account("5106", "Beban Administrasi", AccountType.EXPENSE));
        accounts.add(new Account("5107", "Beban Transportasi", AccountType.EXPENSE));
        
        // Save accounts
        for (Account account : accounts) {
            try {
                accountService.createAccount(
                    account.getAccountCode(), 
                    account.getAccountName(), 
                    account.getAccountType(), 
                    account.getDescription()
                );
                System.out.println("Created account: " + account.getAccountCode() + " - " + account.getAccountName());
            } catch (Exception e) {
                System.out.println("Account already exists: " + account.getAccountCode());
            }
        }
    }
    
    /**
     * Membuat transaksi sample
     */
    private void createSampleTransactions() {
        System.out.println("Creating sample transactions...");
        
        // Get accounts for transactions
        List<Account> allAccounts = accountService.getAllAccounts();
        
        // Transaction 1: Modal awal
        createTransaction(
            "Modal awal pemilik",
            LocalDate.now().minusMonths(2),
            "INV-001",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("1001"), // Kas
                    new BigDecimal("50000000"), // Debit
                    null,
                    "Setoran modal awal"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("3001"), // Modal Pemilik
                    null,
                    new BigDecimal("50000000"), // Credit
                    "Modal awal pemilik"
                )
            )
        );
        
        // Transaction 2: Pembelian peralatan
        createTransaction(
            "Pembelian peralatan kantor",
            LocalDate.now().minusMonths(2).plusDays(5),
            "PO-001",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("1301"), // Peralatan Kantor
                    new BigDecimal("5000000"), // Debit
                    null,
                    "Pembelian komputer dan printer"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("1001"), // Kas
                    null,
                    new BigDecimal("5000000"), // Credit
                    "Pembayaran peralatan kantor"
                )
            )
        );
        
        // Transaction 3: Penjualan
        createTransaction(
            "Penjualan barang tunai",
            LocalDate.now().minusMonths(1),
            "SO-001",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("1001"), // Kas
                    new BigDecimal("15000000"), // Debit
                    null,
                    "Penjualan tunai"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("4001"), // Pendapatan Penjualan
                    null,
                    new BigDecimal("15000000"), // Credit
                    "Pendapatan penjualan barang"
                )
            )
        );
        
        // Transaction 4: Beban gaji
        createTransaction(
            "Pembayaran gaji karyawan",
            LocalDate.now().minusWeeks(2),
            "PAY-001",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("5101"), // Beban Gaji
                    new BigDecimal("8000000"), // Debit
                    null,
                    "Gaji bulan ini"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("1002"), // Bank BCA
                    null,
                    new BigDecimal("8000000"), // Credit
                    "Transfer gaji via bank"
                )
            )
        );
        
        // Transaction 5: Beban listrik
        createTransaction(
            "Pembayaran listrik",
            LocalDate.now().minusWeeks(1),
            "UTIL-001",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("5102"), // Beban Listrik
                    new BigDecimal("1500000"), // Debit
                    null,
                    "Tagihan listrik bulan ini"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("1001"), // Kas
                    null,
                    new BigDecimal("1500000"), // Credit
                    "Bayar listrik tunai"
                )
            )
        );
        
        // Transaction 6: Penjualan kredit
        createTransaction(
            "Penjualan kredit",
            LocalDate.now().minusDays(5),
            "SO-002",
            List.of(
                new TransactionService.TransactionEntryData(
                    getAccountId("1101"), // Piutang Usaha
                    new BigDecimal("12000000"), // Debit
                    null,
                    "Penjualan kredit"
                ),
                new TransactionService.TransactionEntryData(
                    getAccountId("4001"), // Pendapatan Penjualan
                    null,
                    new BigDecimal("12000000"), // Credit
                    "Pendapatan penjualan kredit"
                )
            )
        );
    }
    
    /**
     * Helper method untuk membuat transaksi
     */
    private void createTransaction(String description, LocalDate date, String reference, 
                                 List<TransactionService.TransactionEntryData> entries) {
        try {
            transactionService.createCompleteTransaction(description, date, reference, entries);
            System.out.println("Created transaction: " + description);
        } catch (Exception e) {
            System.out.println("Failed to create transaction: " + description + " - " + e.getMessage());
        }
    }
    
    /**
     * Helper method untuk mendapatkan ID account berdasarkan code
     */
    private Long getAccountId(String accountCode) {
        return accountService.getAllAccounts().stream()
            .filter(account -> account.getAccountCode().equals(accountCode))
            .findFirst()
            .map(Account::getId)
            .orElse(null);
    }
    
    /**
     * Method untuk menghapus semua data (untuk testing)
     */
    public void clearAllData() {
        System.out.println("Clearing all data...");
        
        // Delete all transactions first (due to foreign key constraints)
        transactionService.getAllTransactions().forEach(transaction -> {
            try {
                transactionService.deleteTransaction(transaction.getId());
            } catch (Exception e) {
                System.out.println("Failed to delete transaction: " + e.getMessage());
            }
        });
        
        // Delete all accounts
        accountService.getAllAccounts().forEach(account -> {
            try {
                accountService.deleteAccount(account.getId());
            } catch (Exception e) {
                System.out.println("Failed to delete account: " + e.getMessage());
            }
        });
        
        System.out.println("All data cleared!");
    }
    
    /**
     * Main method untuk menjalankan data loader
     */
    public static void main(String[] args) {
        SampleDataLoader loader = new SampleDataLoader();
        
        if (args.length > 0 && args[0].equals("--clear")) {
            loader.clearAllData();
        } else {
            loader.loadAllSampleData();
        }
        
        // Close database connection
        DatabaseUtil.closeEntityManagerFactory();
    }
}
