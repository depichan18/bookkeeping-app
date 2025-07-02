package com.bookkeeping;

import com.bookkeeping.controller.MainController;
import com.bookkeeping.service.AccountService;
import com.bookkeeping.util.DatabaseUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main JavaFX Application Class
 */
public class BookkeepingApplication extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database and default Chart of Accounts
        initializeApplication();
        
        FXMLLoader fxmlLoader = new FXMLLoader(BookkeepingApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        
        // Add CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("Bookkeeping Application - Complete Accounting System");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
        
        // Set controller reference
        MainController controller = fxmlLoader.getController();
        controller.setStage(stage);
    }
    
    private void initializeApplication() {
        try {
            // Initialize database
            if (DatabaseUtil.isInitialized()) {
                // Initialize default Chart of Accounts if database is empty
                AccountService accountService = new AccountService();
                accountService.initializeDefaultChartOfAccounts();
                System.out.println("Application initialized successfully.");
            }
        } catch (Exception e) {
            System.err.println("Error initializing application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Close database connections
        DatabaseUtil.closeEntityManagerFactory();
    }
    
    public static void main(String[] args) {
        launch();
    }
}
