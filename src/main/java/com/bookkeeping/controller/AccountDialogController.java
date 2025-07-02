package com.bookkeeping.controller;

import com.bookkeeping.entity.Account;
import com.bookkeeping.entity.AccountType;
import com.bookkeeping.service.AccountService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller untuk dialog Account (Create/Edit)
 */
public class AccountDialogController implements Initializable {
    
    @FXML private TextField accountCodeField;
    @FXML private TextField accountNameField;
    @FXML private ComboBox<AccountType> accountTypeComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private TextField balanceField;
    @FXML private CheckBox isActiveCheckBox;
    @FXML private Button okButton;
    @FXML private Button cancelButton;
    
    private Stage dialogStage;
    private Account account;
    private boolean okClicked = false;
    private AccountService accountService;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        accountService = new AccountService();
        
        // Initialize ComboBox
        accountTypeComboBox.setItems(FXCollections.observableArrayList(AccountType.values()));
        
        // Set default values
        isActiveCheckBox.setSelected(true);
        balanceField.setText("0.00");
        
        // Add input validation
        addValidation();
    }
    
    private void addValidation() {
        // Account code validation (only alphanumeric and limited length)
        accountCodeField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 10) {
                accountCodeField.setText(oldVal);
            }
        });
        
        // Balance field validation (only numbers and decimal)
        balanceField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.matches("\\d*\\.?\\d*")) {
                balanceField.setText(oldVal);
            }
        });
        
        // Account name validation (limited length)
        accountNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal.length() > 100) {
                accountNameField.setText(oldVal);
            }
        });
    }
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    public void setAccount(Account account) {
        this.account = account;
        
        if (account != null) {
            // Edit mode
            accountCodeField.setText(account.getAccountCode());
            accountNameField.setText(account.getAccountName());
            accountTypeComboBox.setValue(account.getAccountType());
            descriptionArea.setText(account.getDescription());
            balanceField.setText(account.getBalance().toString());
            isActiveCheckBox.setSelected(account.getIsActive());
        } else {
            // Create mode
            accountCodeField.setText("");
            accountNameField.setText("");
            accountTypeComboBox.setValue(null);
            descriptionArea.setText("");
            balanceField.setText("0.00");
            isActiveCheckBox.setSelected(true);
        }
    }
    
    public boolean isOkClicked() {
        return okClicked;
    }
    
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            try {
                if (account == null) {
                    // Create new account
                    accountService.createAccount(
                        accountCodeField.getText().trim(),
                        accountNameField.getText().trim(),
                        accountTypeComboBox.getValue(),
                        descriptionArea.getText().trim()
                    );
                } else {
                    // Update existing account
                    accountService.updateAccount(
                        account.getId(),
                        accountCodeField.getText().trim(),
                        accountNameField.getText().trim(),
                        accountTypeComboBox.getValue(),
                        descriptionArea.getText().trim()
                    );
                    
                    // Update balance if changed
                    BigDecimal newBalance = new BigDecimal(balanceField.getText().trim());
                    if (newBalance.compareTo(account.getBalance()) != 0) {
                        accountService.updateAccountBalance(account.getId(), newBalance);
                    }
                    
                    // Update active status
                    if (isActiveCheckBox.isSelected() != account.getIsActive()) {
                        accountService.toggleAccountStatus(account.getId());
                    }
                }
                
                okClicked = true;
                dialogStage.close();
                
            } catch (Exception e) {
                showErrorAlert("Error saving account", e.getMessage());
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
    
    private boolean isInputValid() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (accountCodeField.getText() == null || accountCodeField.getText().trim().isEmpty()) {
            errorMessage.append("Account code is required.\n");
        }
        
        if (accountNameField.getText() == null || accountNameField.getText().trim().isEmpty()) {
            errorMessage.append("Account name is required.\n");
        }
        
        if (accountTypeComboBox.getValue() == null) {
            errorMessage.append("Account type is required.\n");
        }
        
        try {
            BigDecimal balance = new BigDecimal(balanceField.getText().trim());
            if (balance.compareTo(BigDecimal.ZERO) < 0) {
                errorMessage.append("Balance cannot be negative.\n");
            }
        } catch (NumberFormatException e) {
            errorMessage.append("Balance must be a valid number.\n");
        }
        
        // Check if account code already exists (only for new accounts or when code changed)
        if (account == null || !accountCodeField.getText().trim().equals(account.getAccountCode())) {
            if (accountService.findAccountByCode(accountCodeField.getText().trim()).isPresent()) {
                errorMessage.append("Account code already exists.\n");
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
}
