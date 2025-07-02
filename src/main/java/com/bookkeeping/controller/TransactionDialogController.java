package com.bookkeeping.controller;

import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.Transaction;
import com.bookkeeping.entity.TransactionEntry;
import com.bookkeeping.service.AccountService;
import com.bookkeeping.service.TransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller untuk dialog Transaction (Create/Edit)
 */
public class TransactionDialogController implements Initializable {
    
    @FXML private TextField transactionNumberField;
    @FXML private DatePicker transactionDatePicker;
    @FXML private TextArea descriptionArea;
    @FXML private TextField referenceField;
    
    // Transaction entries table
    @FXML private TableView<TransactionEntry> entriesTable;
    @FXML private TableColumn<TransactionEntry, String> accountColumn;
    @FXML private TableColumn<TransactionEntry, String> debitColumn;
    @FXML private TableColumn<TransactionEntry, String> creditColumn;
    @FXML private TableColumn<TransactionEntry, String> entryDescriptionColumn;
    
    // Entry form
    @FXML private ComboBox<Account> accountComboBox;
    @FXML private TextField debitField;
    @FXML private TextField creditField;
    @FXML private TextField entryDescriptionField;
    
    // Totals
    @FXML private Label totalDebitLabel;
    @FXML private Label totalCreditLabel;
    @FXML private Label balanceLabel;
    @FXML private Label statusLabel;
    
    @FXML private Button addEntryButton;
    @FXML private Button removeEntryButton;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    
    private Stage dialogStage;
    private Transaction transaction;
    private boolean okClicked = false;
    private AccountService accountService;
    private TransactionService transactionService;
    
    private ObservableList<TransactionEntry> entries;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountService = new AccountService();
        transactionService = new TransactionService();
        
        entries = FXCollections.observableArrayList();
        
        // Initialize UI
        initializeEntriesTable();
        initializeAccountComboBox();
        addValidation();
        
        // Set default values
        transactionDatePicker.setValue(LocalDate.now());
        
        // Update totals when entries change
        entries.addListener((javafx.collections.ListChangeListener.Change<? extends TransactionEntry> change) -> updateTotals());
    }
    
    private void initializeEntriesTable() {
        accountColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAccount().getAccountCode() + " - " + 
                                   cellData.getValue().getAccount().getAccountName()));
        
        entryDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        debitColumn.setCellValueFactory(cellData -> {
            BigDecimal debit = cellData.getValue().getDebitAmount();
            return new SimpleStringProperty(debit != null ? String.format("Rp %,.2f", debit) : "");
        });
        
        creditColumn.setCellValueFactory(cellData -> {
            BigDecimal credit = cellData.getValue().getCreditAmount();
            return new SimpleStringProperty(credit != null ? String.format("Rp %,.2f", credit) : "");
        });
        
        entriesTable.setItems(entries);
        
        // Double click to remove entry
        entriesTable.setRowFactory(tv -> {
            TableRow<TransactionEntry> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    removeEntry(row.getItem());
                }
            });
            return row;
        });
    }
    
    private void initializeAccountComboBox() {
        List<Account> accounts = accountService.getActiveAccounts();
        accountComboBox.setItems(FXCollections.observableArrayList(accounts));
        
        accountComboBox.setConverter(new StringConverter<Account>() {
            @Override
            public String toString(Account account) {
                return account != null ? account.getAccountCode() + " - " + account.getAccountName() : "";
            }
            
            @Override
            public Account fromString(String string) {
                return null;
            }
        });
    }
    
    private void addValidation() {
        // Numeric validation for amount fields
        debitField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                debitField.setText(oldVal);
            }
            // Clear credit when debit is entered
            if (newVal != null && !newVal.isEmpty()) {
                creditField.setText("");
            }
        });
        
        creditField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                creditField.setText(oldVal);
            }
            // Clear debit when credit is entered
            if (newVal != null && !newVal.isEmpty()) {
                debitField.setText("");
            }
        });
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        
        if (transaction != null) {
            // Edit mode
            transactionNumberField.setText(transaction.getTransactionNumber());
            transactionNumberField.setEditable(false); // Don't allow editing transaction number
            transactionDatePicker.setValue(transaction.getTransactionDate());
            descriptionArea.setText(transaction.getDescription());
            referenceField.setText(transaction.getReference());
            
            // Load existing entries
            entries.addAll(transaction.getEntries());
        } else {
            // Create mode
            transactionNumberField.setText("(Auto-generated)");
            transactionNumberField.setEditable(false);
            transactionDatePicker.setValue(LocalDate.now());
            descriptionArea.setText("");
            referenceField.setText("");
        }
        
        updateTotals();
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    private void handleAddEntry() {
        if (isEntryInputValid()) {
            Account selectedAccount = accountComboBox.getValue();
            String description = entryDescriptionField.getText().trim();
            
            BigDecimal debitAmount = null;
            BigDecimal creditAmount = null;
            
            try {
                if (!debitField.getText().trim().isEmpty()) {
                    debitAmount = new BigDecimal(debitField.getText().trim());
                }
                if (!creditField.getText().trim().isEmpty()) {
                    creditAmount = new BigDecimal(creditField.getText().trim());
                }
                
                TransactionEntry entry = new TransactionEntry(selectedAccount, debitAmount, creditAmount, description);
                entries.add(entry);
                
                // Clear entry form
                accountComboBox.setValue(null);
                debitField.setText("");
                creditField.setText("");
                entryDescriptionField.setText("");
                
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid amount", "Please enter a valid amount.");
            }
        }
    }
    
    @FXML
    private void handleRemoveEntry() {
        TransactionEntry selectedEntry = entriesTable.getSelectionModel().getSelectedItem();
        if (selectedEntry != null) {
            removeEntry(selectedEntry);
        } else {
            showWarningAlert("No Selection", "Please select an entry to remove.");
        }
    }
    
    private void removeEntry(TransactionEntry entry) {
        entries.remove(entry);
    }
    
    @FXML
    private void handleOk() {
        if (isTransactionInputValid()) {
            try {
                // Prepare entry data
                List<TransactionService.TransactionEntryData> entryDataList = new ArrayList<>();
                for (TransactionEntry entry : entries) {
                    entryDataList.add(new TransactionService.TransactionEntryData(
                        entry.getAccount().getId(),
                        entry.getDebitAmount(),
                        entry.getCreditAmount(),
                        entry.getDescription()
                    ));
                }
                
                if (transaction == null) {
                    // Create new transaction
                    transactionService.createCompleteTransaction(
                        descriptionArea.getText().trim(),
                        transactionDatePicker.getValue(),
                        referenceField.getText().trim(),
                        entryDataList
                    );
                } else {
                    // Update existing transaction
                    // For simplicity, we'll delete and recreate the transaction
                    // In a real application, you might want more sophisticated update logic
                    transactionService.deleteTransaction(transaction.getId());
                    transactionService.createCompleteTransaction(
                        descriptionArea.getText().trim(),
                        transactionDatePicker.getValue(),
                        referenceField.getText().trim(),
                        entryDataList
                    );
                }
                
                okClicked = true;
                dialogStage.close();
                
            } catch (Exception e) {
                showErrorAlert("Error saving transaction", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    private void updateTotals() {
        BigDecimal totalDebit = BigDecimal.ZERO;
        BigDecimal totalCredit = BigDecimal.ZERO;
        
        for (TransactionEntry entry : entries) {
            if (entry.getDebitAmount() != null) {
                totalDebit = totalDebit.add(entry.getDebitAmount());
            }
            if (entry.getCreditAmount() != null) {
                totalCredit = totalCredit.add(entry.getCreditAmount());
            }
        }
        
        totalDebitLabel.setText(String.format("Rp %,.2f", totalDebit));
        totalCreditLabel.setText(String.format("Rp %,.2f", totalCredit));
        
        boolean isBalanced = totalDebit.compareTo(totalCredit) == 0;
        if (isBalanced && totalDebit.compareTo(BigDecimal.ZERO) > 0) {
            statusLabel.setText("✓ Balanced");
            statusLabel.setStyle("-fx-text-fill: green;");
        } else if (totalDebit.compareTo(BigDecimal.ZERO) == 0 && totalCredit.compareTo(BigDecimal.ZERO) == 0) {
            statusLabel.setText("No entries");
            statusLabel.setStyle("-fx-text-fill: gray;");
        } else {
            statusLabel.setText("✗ Not balanced");
            statusLabel.setStyle("-fx-text-fill: red;");
        }
    }
    
    private boolean isEntryInputValid() {
        if (accountComboBox.getValue() == null) {
            showWarningAlert("Invalid Entry", "Please select an account.");
            return false;
        }
        
        boolean hasDebit = !debitField.getText().trim().isEmpty();
        boolean hasCredit = !creditField.getText().trim().isEmpty();
        
        if (!hasDebit && !hasCredit) {
            showWarningAlert("Invalid Entry", "Please enter either debit or credit amount.");
            return false;
        }
        
        if (hasDebit && hasCredit) {
            showWarningAlert("Invalid Entry", "Entry cannot have both debit and credit amounts.");
            return false;
        }
        
        try {
            if (hasDebit) {
                BigDecimal debit = new BigDecimal(debitField.getText().trim());
                if (debit.compareTo(BigDecimal.ZERO) <= 0) {
                    showWarningAlert("Invalid Entry", "Debit amount must be greater than zero.");
                    return false;
                }
            }
            if (hasCredit) {
                BigDecimal credit = new BigDecimal(creditField.getText().trim());
                if (credit.compareTo(BigDecimal.ZERO) <= 0) {
                    showWarningAlert("Invalid Entry", "Credit amount must be greater than zero.");
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            showWarningAlert("Invalid Entry", "Please enter a valid amount.");
            return false;
        }
        
        return true;
    }
    
    private boolean isTransactionInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (descriptionArea.getText() == null || descriptionArea.getText().trim().isEmpty()) {
            errorMessage.append("Transaction description is required.\n");
        }
        
        if (transactionDatePicker.getValue() == null) {
            errorMessage.append("Transaction date is required.\n");
        }
        
        if (entries.isEmpty()) {
            errorMessage.append("Transaction must have at least one entry.\n");
        } else {
            // Check if transaction is balanced
            BigDecimal totalDebit = BigDecimal.ZERO;
            BigDecimal totalCredit = BigDecimal.ZERO;
            
            for (TransactionEntry entry : entries) {
                if (entry.getDebitAmount() != null) {
                    totalDebit = totalDebit.add(entry.getDebitAmount());
                }
                if (entry.getCreditAmount() != null) {
                    totalCredit = totalCredit.add(entry.getCreditAmount());
                }
            }
            
            if (totalDebit.compareTo(totalCredit) != 0) {
                errorMessage.append("Transaction is not balanced. Total debits must equal total credits.\n");
            }
        }
        
        if (errorMessage.length() == 0) {
            return true;
        } else {
            showErrorAlert("Invalid input", errorMessage.toString());
            return false;
        }
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.initOwner(dialogStage);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
