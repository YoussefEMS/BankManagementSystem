package com.bms.presentation;

import com.bms.domain.controller.AuthenticationController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * AccountSelectionScreen - Shows customer's accounts and allows selection
 * Displays account list with balances and account type
 */
public class AccountSelectionScreen {
    private BorderPane root;
    private ListView<String> accountListView;
    private Label welcomeLabel;
    private Runnable onAccountSelected;
    private Runnable onLogout;
    private AuthenticationController authController;
    private int selectedAccountIndex = -1;

    public AccountSelectionScreen() {
        this.authController = new AuthenticationController();
        initializeUI();
    }

    /**
     * Initialize the account selection UI
     */
    private void initializeUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Top: Header with customer info
        BorderPane top = createHeader();
        root.setTop(top);

        // Center: Account list
        VBox center = createAccountList();
        root.setCenter(center);

        // Bottom: Action buttons
        HBox bottom = createButtonBar();
        root.setBottom(bottom);
    }

    /**
     * Refresh the accounts list (call this when screen becomes visible)
     */
    public void refreshAccounts() {
        loadAccounts();
    }

    /**
     * Create header with customer info
     */
    private BorderPane createHeader() {
        BorderPane header = new BorderPane();
        header.setStyle("-fx-background-color: #1976d2; -fx-padding: 20;");

        // Welcome label
        String customerName = authController.getLoggedInCustomerName();
        welcomeLabel = new Label("Welcome, " + customerName);
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18; -fx-font-weight: bold;");
        header.setLeft(welcomeLabel);

        // Logout button
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle("-fx-padding: 8 15; -fx-font-size: 12;");
        logoutButton.setOnAction(event -> handleLogout());
        header.setRight(logoutButton);

        return header;
    }

    /**
     * Create account list view
     */
    private VBox createAccountList() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Your Accounts");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Account list
        accountListView = new ListView<>();
        accountListView.setPrefHeight(400);
        accountListView.setMinHeight(300);
        accountListView.setStyle("-fx-font-size: 13; -fx-control-inner-background: #f9f9f9;");
        accountListView.setCellFactory(param -> new AccountListCell());
        accountListView.getSelectionModel().selectedIndexProperty().addListener(
            (obs, oldVal, newVal) -> selectedAccountIndex = newVal.intValue()
        );

        container.getChildren().addAll(titleLabel, accountListView);
        VBox.setVgrow(accountListView, javafx.scene.layout.Priority.ALWAYS);

        return container;
    }

    /**
     * Create button bar with actions
     */
    private HBox createButtonBar() {
        HBox buttonBar = new HBox(10);
        buttonBar.setPadding(new Insets(15));
        buttonBar.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-width: 1 0 0 0;");
        buttonBar.setAlignment(Pos.CENTER_RIGHT);

        Button viewDetailsButton = new Button("View Details");
        viewDetailsButton.setStyle("-fx-padding: 8 15; -fx-font-size: 12;");
        viewDetailsButton.setOnAction(event -> handleSelectAccount());

        buttonBar.getChildren().add(viewDetailsButton);

        return buttonBar;
    }

    /**
     * Load accounts for logged-in customer
     */
    private void loadAccounts() {
        String[][] accountsData = authController.getLoggedInCustomerAccountsAsStrings();
        accountListView.getItems().clear();
        
        for (String[] accountInfo : accountsData) {
            // Format: AccountNumber | Type | Balance Currency | Status
            String displayText = String.format("%s | %s | $%s %s | %s",
                accountInfo[0], accountInfo[1], accountInfo[2], accountInfo[3], accountInfo[4]);
            accountListView.getItems().add(displayText);
        }
    }

    /**
     * Handle account selection
     */
    private void handleSelectAccount() {
        String selectedAccount = accountListView.getSelectionModel().getSelectedItem();
        if (selectedAccount == null) {
            showAlert("Please select an account", "Select Account");
            return;
        }
        if (onAccountSelected != null) {
            onAccountSelected.run();
        }
    }

    /**
     * Handle logout
     */
    private void handleLogout() {
        authController.logout();
        if (onLogout != null) {
            onLogout.run();
        }
    }

    /**
     * Get the selected account number
     */
    public String getSelectedAccountNumber() {
        return authController.getLoggedInCustomerAccountNumber(selectedAccountIndex);
    }

    /**
     * Get the root UI element
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Set account selected callback
     */
    public void setOnAccountSelected(Runnable callback) {
        this.onAccountSelected = callback;
    }

    /**
     * Set logout callback
     */
    public void setOnLogout(Runnable callback) {
        this.onLogout = callback;
    }

    /**
     * Show alert dialog
     */
    private void showAlert(String message, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Custom cell renderer for Account list
     */
    private static class AccountListCell extends ListCell<String> {
        @Override
        protected void updateItem(String displayText, boolean empty) {
            super.updateItem(displayText, empty);

            if (empty || displayText == null) {
                setText(null);
                setGraphic(null);
                setPrefHeight(0);
            } else {
                // Parse the display text: AccountNumber | Type | Balance Currency | Status
                String[] parts = displayText.split(" \\| ");
                
                VBox cellContent = new VBox(5);
                cellContent.setPadding(new Insets(12));
                cellContent.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0; -fx-background-color: white;");
                cellContent.setMinHeight(80);

                // Account Number
                Label accountNumberLabel = new Label(parts.length > 0 ? parts[0] : "");
                accountNumberLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #1976d2;");

                // Account Type
                Label accountTypeLabel = new Label("Type: " + (parts.length > 1 ? parts[1] : ""));
                accountTypeLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12;");

                // Balance and Currency
                Label balanceLabel = new Label("Balance: " + (parts.length > 2 ? parts[2] : ""));
                balanceLabel.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold; -fx-font-size: 12;");

                // Status
                Label statusLabel = new Label("Status: " + (parts.length > 3 ? parts[3] : ""));
                statusLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 10;");

                cellContent.getChildren().addAll(accountNumberLabel, accountTypeLabel, balanceLabel, statusLabel);

                setGraphic(cellContent);
                setText(null);
                setPrefHeight(USE_COMPUTED_SIZE);
            }
        }
    }
}
