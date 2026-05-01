package com.bms.view;

import java.time.LocalDate;

import com.bms.domain.controller.TransactionHistoryController;
import com.bms.service.CustomerAuthenticationService;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * TransactionHistoryScreen - UC-04 View Transaction History
 * Displays transaction history with optional filters
 * If no transactions found, shows empty table with optional "No results" label
 */
public class TransactionHistoryScreen {
    private VBox root;
    private TransactionHistoryController historyController;
    private CustomerAuthenticationService authController;

    // UI Components
    private ComboBox<String> accountNumberCombo;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private ComboBox<String> typeFilterCombo;
    private Button searchButton;
    private TableView<String[]> transactionTable;
    private Label noResultLabel;

    public TransactionHistoryScreen() {
        this.historyController = new TransactionHistoryController();
        this.authController = new CustomerAuthenticationService();
        this.root = createUI();
        loadUserAccounts();
    }

    /**
     * Create the UI for transaction history view
     */
    private VBox createUI() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(20));
        container.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 12;");

        // Title
        Label titleLabel = new Label("View Transaction History (UC-04)");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        // Filter section
        VBox filterSection = createFilterSection();

        // Results section
        VBox resultsSection = createResultsSection();

        container.getChildren().addAll(
            titleLabel,
            new Separator(),
            filterSection,
            new Separator(),
            resultsSection
        );

        return container;
    }

    /**
     * Create filter section with inputs for search criteria
     */
    private VBox createFilterSection() {
        VBox section = new VBox(10);

        // Account number row
        VBox accountBox = new VBox(5);
        Label accountLabel = new Label("Select Account:");
        accountLabel.setStyle("-fx-font-weight: bold;");
        accountNumberCombo = new ComboBox<>();
        accountNumberCombo.setPromptText("Select an account");
        accountNumberCombo.setPrefWidth(400);
        accountNumberCombo.setStyle("-fx-font-size: 12;");
        accountBox.getChildren().addAll(accountLabel, accountNumberCombo);

        // Filters row
        HBox filtersBox = new HBox(20);
        filtersBox.setAlignment(Pos.TOP_LEFT);

        // Start date
        VBox startDateBox = new VBox(5);
        Label startDateLabel = new Label("Start Date (Optional):");
        startDateLabel.setStyle("-fx-font-weight: bold;");
        startDatePicker = new DatePicker();
        startDatePicker.setPrefWidth(150);
        startDateBox.getChildren().addAll(startDateLabel, startDatePicker);

        // End date
        VBox endDateBox = new VBox(5);
        Label endDateLabel = new Label("End Date (Optional):");
        endDateLabel.setStyle("-fx-font-weight: bold;");
        endDatePicker = new DatePicker();
        endDatePicker.setPrefWidth(150);
        endDateBox.getChildren().addAll(endDateLabel, endDatePicker);

        // Type filter
        VBox typeBox = new VBox(5);
        Label typeLabel = new Label("Transaction Type (Optional):");
        typeLabel.setStyle("-fx-font-weight: bold;");
        typeFilterCombo = new ComboBox<>();
        typeFilterCombo.setItems(FXCollections.observableArrayList(
            "All", "Deposit", "Withdrawal", "TransferDebit", "TransferCredit", "InterestPosting"
        ));
        typeFilterCombo.setValue("All");
        typeFilterCombo.setPrefWidth(150);
        typeBox.getChildren().addAll(typeLabel, typeFilterCombo);

        filtersBox.getChildren().addAll(startDateBox, endDateBox, typeBox);

        // Search button
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        searchButton = new Button("Search");
        searchButton.setPrefWidth(100);
        searchButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        searchButton.setOnAction(e -> handleSearch());

        // Allow Enter key to trigger search
        accountNumberCombo.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleSearch();
            }
        });

        Button clearButton = new Button("Clear Filters");
        clearButton.setPrefWidth(100);
        clearButton.setStyle("-fx-font-size: 12; -fx-padding: 8;");
        clearButton.setOnAction(e -> clearFilters());

        buttonBox.getChildren().addAll(searchButton, clearButton);

        section.getChildren().addAll(accountBox, filtersBox, buttonBox);
        return section;
    }

    /**
     * Load user's accounts into the combo box
     */
    private void loadUserAccounts() {
        String[] accountNumbers = authController.getLoggedInCustomerAccountNumbers();
        accountNumberCombo.setItems(FXCollections.observableArrayList(accountNumbers));
    }

    /**
     * Refresh the accounts list (call this when screen becomes visible after login)
     */
    public void refreshAccounts() {
        loadUserAccounts();
    }

    /**
     * Create results section with transaction table
     */
    private VBox createResultsSection() {
        VBox section = new VBox(10);

        Label resultsLabel = new Label("Transaction Results:");
        resultsLabel.setStyle("-fx-font-weight: bold;");

        // No results label (initially hidden)
        noResultLabel = new Label("No transactions found. Please search or check your account number.");
        noResultLabel.setStyle("-fx-text-fill: #666666; -fx-font-style: italic; -fx-padding: 20;");
        noResultLabel.setVisible(false);

        // Create transaction table
        transactionTable = createTransactionTable();

        section.getChildren().addAll(resultsLabel, noResultLabel, transactionTable);
        VBox.setVgrow(transactionTable, javafx.scene.layout.Priority.ALWAYS);

        return section;
    }

    /**
     * Create the transaction table with columns
     */
    @SuppressWarnings("unchecked")
    private TableView<String[]> createTransactionTable() {
        TableView<String[]> table = new TableView<>();
        table.setPrefHeight(400);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Timestamp column (index 0)
        TableColumn<String[], String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setPrefWidth(150);
        timestampCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()[0]));

        // Type column (index 1)
        TableColumn<String[], String> typeCol = new TableColumn<>("Type");
        typeCol.setPrefWidth(120);
        typeCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()[1]));

        // Amount column (index 2)
        TableColumn<String[], String> amountCol = new TableColumn<>("Amount");
        amountCol.setPrefWidth(100);
        amountCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()[2]));

        // Note column (index 3)
        TableColumn<String[], String> noteCol = new TableColumn<>("Note");
        noteCol.setPrefWidth(200);
        noteCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()[3]));

        // Balance After column (index 4)
        TableColumn<String[], String> balanceCol = new TableColumn<>("Balance After");
        balanceCol.setPrefWidth(120);
        balanceCol.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(cellData.getValue()[4]));

        table.getColumns().addAll(timestampCol, typeCol, amountCol, noteCol, balanceCol);
        return table;
    }

    /**
     * Handle search button click
     */
    private void handleSearch() {
        String accountNo = accountNumberCombo.getValue();

        if (accountNo == null || accountNo.trim().isEmpty()) {
            noResultLabel.setText("Please select an account.");
            noResultLabel.setVisible(true);
            transactionTable.setItems(FXCollections.observableArrayList());
            return;
        }

        try {
            // Get filter values
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String typeFilter = typeFilterCombo.getValue();

            // Call controller to get transactions as string arrays
            String[][] transactionsData = historyController.getTransactionHistoryAsStrings(
                accountNo,
                startDate,
                endDate,
                typeFilter
            );

            if (transactionsData.length == 0) {
                noResultLabel.setText("No transactions found for the selected criteria.");
                noResultLabel.setVisible(true);
                transactionTable.setItems(FXCollections.observableArrayList());
            } else {
                noResultLabel.setVisible(false);
                // Convert 2D array to observable list of arrays
                java.util.List<String[]> transactionsList = new java.util.ArrayList<>();
                for (String[] row : transactionsData) {
                    transactionsList.add(row);
                }
                transactionTable.setItems(FXCollections.observableArrayList(transactionsList));
            }
        } catch (Exception e) {
            noResultLabel.setText("Error retrieving transactions. Please try again.");
            noResultLabel.setVisible(true);
            transactionTable.setItems(FXCollections.observableArrayList());
            e.printStackTrace();
        }
    }

    /**
     * Clear all filters and results
     */
    private void clearFilters() {
        accountNumberCombo.setValue(null);
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        typeFilterCombo.setValue("All");
        transactionTable.setItems(FXCollections.observableArrayList());
        noResultLabel.setVisible(false);
    }

    /**
     * Get the root VBox for this screen
     */
    public VBox getRoot() {
        return root;
    }
}
