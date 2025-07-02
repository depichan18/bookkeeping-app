package com.bookkeeping.controller;

import com.bookkeeping.service.AccountService;
import com.bookkeeping.service.TransactionService;
import com.bookkeeping.service.ReportService;
import com.bookkeeping.service.PDFReportService;
import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.entity.Transaction;
import com.bookkeeping.model.BalanceSheetData;
import com.bookkeeping.model.IncomeStatementData;
import com.bookkeeping.model.TrialBalanceData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Main Controller untuk aplikasi bookkeeping
 */
public class MainController implements Initializable {
    
    // Services
    private AccountService accountService;
    private TransactionService transactionService;
    private ReportService reportService;
    private PDFReportService pdfReportService;
    
    private Stage stage;
    
    // FXML Components - Chart of Accounts Tab
    @FXML private TabPane mainTabPane;
    @FXML private TableView<Account> accountsTable;
    @FXML private TableColumn<Account, String> accountCodeColumn;
    @FXML private TableColumn<Account, String> accountNameColumn;
    @FXML private TableColumn<Account, AccountType> accountTypeColumn;
    @FXML private TableColumn<Account, BigDecimal> balanceColumn;
    @FXML private TableColumn<Account, String> descriptionColumn;
    @FXML private TextField accountSearchField;
    @FXML private ComboBox<AccountType> accountTypeFilter;
    
    // FXML Components - Transactions Tab
    @FXML private TableView<Transaction> transactionsTable;
    @FXML private TableColumn<Transaction, String> transactionNumberColumn;
    @FXML private TableColumn<Transaction, LocalDate> transactionDateColumn;
    @FXML private TableColumn<Transaction, String> transactionDescriptionColumn;
    @FXML private TableColumn<Transaction, BigDecimal> totalAmountColumn;
    @FXML private DatePicker transactionDateFilter;
    @FXML private TextField transactionSearchField;
    
    // FXML Components - Reports Tab
    @FXML private DatePicker reportStartDate;
    @FXML private DatePicker reportEndDate;
    @FXML private DatePicker balanceSheetDate;
    @FXML private VBox reportContentArea;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        accountService = new AccountService();
        transactionService = new TransactionService();
        reportService = new ReportService();
        pdfReportService = new PDFReportService();
        
        // Initialize UI components
        initializeAccountsTable();
        initializeTransactionsTable();
        initializeFilters();
        
        // Load initial data
        loadAccountsData();
        loadTransactionsData();
    }
    
    private void initializeAccountsTable() {
        accountCodeColumn.setCellValueFactory(new PropertyValueFactory<>("accountCode"));
        accountNameColumn.setCellValueFactory(new PropertyValueFactory<>("accountName"));
        accountTypeColumn.setCellValueFactory(new PropertyValueFactory<>("accountType"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("balance"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Format balance column
        balanceColumn.setCellFactory(column -> new TableCell<Account, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(String.format("Rp %,.2f", item));
                }
            }
        });
        
        // Double click to edit
        accountsTable.setRowFactory(tv -> {
            TableRow<Account> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editAccount(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void initializeTransactionsTable() {
        transactionNumberColumn.setCellValueFactory(new PropertyValueFactory<>("transactionNumber"));
        transactionDateColumn.setCellValueFactory(new PropertyValueFactory<>("transactionDate"));
        transactionDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        
        // Format amount column
        totalAmountColumn.setCellFactory(column -> new TableCell<Transaction, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(String.format("Rp %,.2f", item));
                }
            }
        });
        
        // Double click to edit
        transactionsTable.setRowFactory(tv -> {
            TableRow<Transaction> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    editTransaction(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void initializeFilters() {
        // Account type filter
        accountTypeFilter.setItems(FXCollections.observableArrayList(AccountType.values()));
        accountTypeFilter.getItems().add(0, null); // Add "All" option
        accountTypeFilter.getSelectionModel().selectFirst();
        
        // Set default dates
        reportStartDate.setValue(LocalDate.now().withDayOfMonth(1)); // First day of current month
        reportEndDate.setValue(LocalDate.now()); // Today
        balanceSheetDate.setValue(LocalDate.now()); // Today
        
        // Add listeners for filters
        accountSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
        accountTypeFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterAccounts());
        transactionSearchField.textProperty().addListener((obs, oldVal, newVal) -> filterTransactions());
        transactionDateFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterTransactions());
    }
    
    private void loadAccountsData() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            ObservableList<Account> accountsList = FXCollections.observableArrayList(accounts);
            accountsTable.setItems(accountsList);
        } catch (Exception e) {
            showErrorAlert("Error loading accounts", e.getMessage());
        }
    }
    
    private void loadTransactionsData() {
        try {
            List<Transaction> transactions = transactionService.getAllTransactions();
            ObservableList<Transaction> transactionsList = FXCollections.observableArrayList(transactions);
            transactionsTable.setItems(transactionsList);
        } catch (Exception e) {
            showErrorAlert("Error loading transactions", e.getMessage());
        }
    }
    
    private void filterAccounts() {
        try {
            String searchText = accountSearchField.getText();
            AccountType selectedType = accountTypeFilter.getValue();
            
            List<Account> allAccounts = accountService.getAllAccounts();
            List<Account> filteredAccounts = allAccounts.stream()
                .filter(account -> {
                    boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                        account.getAccountName().toLowerCase().contains(searchText.toLowerCase()) ||
                        account.getAccountCode().toLowerCase().contains(searchText.toLowerCase());
                    
                    boolean matchesType = selectedType == null || account.getAccountType() == selectedType;
                    
                    return matchesSearch && matchesType;
                })
                .toList();
            
            ObservableList<Account> accountsList = FXCollections.observableArrayList(filteredAccounts);
            accountsTable.setItems(accountsList);
        } catch (Exception e) {
            showErrorAlert("Error filtering accounts", e.getMessage());
        }
    }
    
    private void filterTransactions() {
        try {
            String searchText = transactionSearchField.getText();
            LocalDate selectedDate = transactionDateFilter.getValue();
            
            List<Transaction> allTransactions = transactionService.getAllTransactions();
            List<Transaction> filteredTransactions = allTransactions.stream()
                .filter(transaction -> {
                    boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                        transaction.getDescription().toLowerCase().contains(searchText.toLowerCase()) ||
                        transaction.getTransactionNumber().toLowerCase().contains(searchText.toLowerCase());
                    
                    boolean matchesDate = selectedDate == null || 
                        transaction.getTransactionDate().equals(selectedDate);
                    
                    return matchesSearch && matchesDate;
                })
                .toList();
            
            ObservableList<Transaction> transactionsList = FXCollections.observableArrayList(filteredTransactions);
            transactionsTable.setItems(transactionsList);
        } catch (Exception e) {
            showErrorAlert("Error filtering transactions", e.getMessage());
        }
    }
    
    // Event Handlers - Account Management
    @FXML
    private void handleNewAccount() {
        showAccountDialog(null);
    }
    
    @FXML
    private void handleEditAccount() {
        Account selectedAccount = accountsTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            editAccount(selectedAccount);
        } else {
            showWarningAlert("No Selection", "Please select an account to edit.");
        }
    }
    
    @FXML
    private void handleDeleteAccount() {
        Account selectedAccount = accountsTable.getSelectionModel().getSelectedItem();
        if (selectedAccount != null) {
            if (showConfirmationAlert("Delete Account", 
                "Are you sure you want to delete account: " + selectedAccount.getAccountName() + "?")) {
                try {
                    accountService.deleteAccount(selectedAccount.getId());
                    loadAccountsData();
                    showInfoAlert("Success", "Account deleted successfully.");
                } catch (Exception e) {
                    showErrorAlert("Error deleting account", e.getMessage());
                }
            }
        } else {
            showWarningAlert("No Selection", "Please select an account to delete.");
        }
    }
    
    // Event Handlers - Transaction Management
    @FXML
    private void handleNewTransaction() {
        showTransactionDialog(null);
    }
    
    @FXML
    private void handleEditTransaction() {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            editTransaction(selectedTransaction);
        } else {
            showWarningAlert("No Selection", "Please select a transaction to edit.");
        }
    }
    
    @FXML
    private void handleDeleteTransaction() {
        Transaction selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction != null) {
            if (showConfirmationAlert("Delete Transaction", 
                "Are you sure you want to delete transaction: " + selectedTransaction.getTransactionNumber() + "?")) {
                try {
                    transactionService.deleteTransaction(selectedTransaction.getId());
                    loadTransactionsData();
                    loadAccountsData(); // Refresh accounts to update balances
                    showInfoAlert("Success", "Transaction deleted successfully.");
                } catch (Exception e) {
                    showErrorAlert("Error deleting transaction", e.getMessage());
                }
            }
        } else {
            showWarningAlert("No Selection", "Please select a transaction to delete.");
        }
    }
    
    // Event Handlers - Reports
    @FXML
    private void handleGenerateTrialBalance() {
        try {
            LocalDate asOfDate = balanceSheetDate.getValue();
            TrialBalanceData data = reportService.generateTrialBalance(asOfDate);
            
            // Show PDF save dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Trial Balance PDF");
            fileChooser.setInitialFileName("trial_balance_" + asOfDate.toString() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                pdfReportService.generateTrialBalancePDF(data, file.getAbsolutePath());
                showInfoAlert("Success", "Trial Balance PDF generated successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Error generating Trial Balance", e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateBalanceSheet() {
        try {
            LocalDate asOfDate = balanceSheetDate.getValue();
            BalanceSheetData data = reportService.generateBalanceSheet(asOfDate);
            
            // Show PDF save dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Balance Sheet PDF");
            fileChooser.setInitialFileName("balance_sheet_" + asOfDate.toString() + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                pdfReportService.generateBalanceSheetPDF(data, file.getAbsolutePath());
                showInfoAlert("Success", "Balance Sheet PDF generated successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Error generating Balance Sheet", e.getMessage());
        }
    }
    
    @FXML
    private void handleGenerateIncomeStatement() {
        try {
            LocalDate startDate = reportStartDate.getValue();
            LocalDate endDate = reportEndDate.getValue();
            
            if (startDate.isAfter(endDate)) {
                showWarningAlert("Invalid Date Range", "Start date must be before end date.");
                return;
            }
            
            IncomeStatementData data = reportService.generateIncomeStatement(startDate, endDate);
            
            // Show PDF save dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Income Statement PDF");
            fileChooser.setInitialFileName("income_statement_" + startDate + "_to_" + endDate + ".pdf");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
            
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                pdfReportService.generateIncomeStatementPDF(data, file.getAbsolutePath());
                showInfoAlert("Success", "Income Statement PDF generated successfully!");
            }
        } catch (Exception e) {
            showErrorAlert("Error generating Income Statement", e.getMessage());
        }
    }
    
    @FXML
    private void handleRefreshData() {
        loadAccountsData();
        loadTransactionsData();
        showInfoAlert("Success", "Data refreshed successfully!");
    }
    
    @FXML
    private void handleExit() {
        Platform.exit();
    }
    
    // Helper methods
    private void editAccount(Account account) {
        showAccountDialog(account);
    }
    
    private void editTransaction(Transaction transaction) {
        showTransactionDialog(transaction);
    }
    
    private void showAccountDialog(Account account) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/account-dialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(account == null ? "New Account" : "Edit Account");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            dialogStage.setScene(new Scene(loader.load()));
            
            AccountDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setAccount(account);
            
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadAccountsData();
            }
        } catch (IOException e) {
            showErrorAlert("Error opening account dialog", e.getMessage());
        }
    }
    
    private void showTransactionDialog(Transaction transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/transaction-dialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setTitle(transaction == null ? "New Transaction" : "Edit Transaction");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(stage);
            dialogStage.setScene(new Scene(loader.load()));
            
            TransactionDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setTransaction(transaction);
            
            dialogStage.showAndWait();
            
            if (controller.isOkClicked()) {
                loadTransactionsData();
                loadAccountsData(); // Refresh accounts to update balances
            }
        } catch (IOException e) {
            showErrorAlert("Error opening transaction dialog", e.getMessage());
        }
    }
    
    // Alert methods
    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
