package com.bookkeeping;

import java.io.IOException;

import com.bookkeeping.controller.MainController;
import com.bookkeeping.service.AccountService;
import com.bookkeeping.util.DatabaseUtil;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Main JavaFX Application Class
 */
public class BookkeepingApplication extends Application {
    
    @Override
    public void start(Stage stage) throws IOException {
        // Initialize database and default Chart of Accounts
        initializeApplication();
        
        FXMLLoader fxmlLoader = new FXMLLoader(BookkeepingApplication.class.getResource("/fxml/main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 650);
        
        // Add CSS
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("Bookkeeping Application - Accounting System");
        stage.getIcons().add(new Image(getClass().getResource("/img/icon.png").toString()));
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        stage.centerOnScreen();
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
