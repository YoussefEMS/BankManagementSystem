package com.bms.view;

import java.util.List;
import java.util.Locale;

import com.bms.domain.controller.LoanCatalogController;
import com.bms.domain.entity.LoanComparisonScenario;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * UC: Loan Product Catalog and Comparison.
 */
public class LoanCatalogComparisonView {
    private final VBox root;
    private final LoanCatalogController controller;

    private TextField amountField;
    private TextField durationField;
    private TableView<LoanComparisonScenario> comparisonTable;
    private TextArea productDetailsArea;
    private Label statusLabel;
    private Runnable onBack;

    public LoanCatalogComparisonView() {
        this.controller = new LoanCatalogController();
        this.root = createLayout();
        loadComparisonDefaults();
    }

    @SuppressWarnings("unchecked")
    private VBox createLayout() {
        VBox layout = new VBox(14);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Loan Product Catalog and Comparison");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));

        Label subtitle = new Label(
                "Compare shared loan products side by side. Amount and duration are scenario inputs; product terms are flyweights.");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(900);
        subtitle.setStyle("-fx-text-fill: #555;");

        amountField = new TextField("50000");
        amountField.setPromptText("Loan Amount (USD)");

        durationField = new TextField("24");
        durationField.setPromptText("Duration (months)");

        Button compareBtn = new Button("Compare Offers");
        compareBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #1565c0; -fx-text-fill: white;");
        compareBtn.setOnAction(e -> handleCompare());

        HBox inputRow = new HBox(10,
                new Label("Amount:"), amountField,
                new Label("Months:"), durationField,
                compareBtn);
        inputRow.setAlignment(Pos.CENTER);

        comparisonTable = new TableView<>();
        comparisonTable.setPrefHeight(280);
        comparisonTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<LoanComparisonScenario, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getProduct().getDisplayName()));

        TableColumn<LoanComparisonScenario, Number> rateCol = new TableColumn<>("Rate %");
        rateCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAnnualRate()));

        TableColumn<LoanComparisonScenario, String> monthlyCol = new TableColumn<>("Est. Monthly");
        monthlyCol.setCellValueFactory(data -> new SimpleStringProperty(formatCurrency(data.getValue().getMonthlyInstallment())));

        TableColumn<LoanComparisonScenario, String> totalRepaymentCol = new TableColumn<>("Total Repayment");
        totalRepaymentCol.setCellValueFactory(
                data -> new SimpleStringProperty(formatCurrency(data.getValue().getTotalRepayment())));

        TableColumn<LoanComparisonScenario, String> totalInterestCol = new TableColumn<>("Total Interest");
        totalInterestCol.setCellValueFactory(
                data -> new SimpleStringProperty(formatCurrency(data.getValue().getTotalInterest())));

        comparisonTable.getColumns().addAll(productCol, rateCol, monthlyCol, totalRepaymentCol, totalInterestCol);
        comparisonTable.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> showScenarioDetails(newVal));

        productDetailsArea = new TextArea();
        productDetailsArea.setEditable(false);
        productDetailsArea.setWrapText(true);
        productDetailsArea.setPrefRowCount(7);

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        layout.getChildren().addAll(
                title,
                subtitle,
                inputRow,
                comparisonTable,
                productDetailsArea,
                statusLabel,
                backBtn);
        return layout;
    }

    private void loadComparisonDefaults() {
        handleCompare();
    }

    private void handleCompare() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            int durationMonths = Integer.parseInt(durationField.getText().trim());

            List<LoanComparisonScenario> scenarios = controller.compareProducts(amount, durationMonths);
            comparisonTable.getItems().setAll(scenarios);

            if (scenarios.isEmpty()) {
                productDetailsArea.clear();
                statusLabel.setStyle("-fx-text-fill: #c62828;");
                statusLabel.setText("Enter a valid amount and a duration between 1 and 360 months.");
                return;
            }

            comparisonTable.getSelectionModel().selectFirst();
            statusLabel.setStyle("-fx-text-fill: #2e7d32;");
            statusLabel.setText("Showing " + scenarios.size() + " loan products for the current scenario.");
        } catch (NumberFormatException ex) {
            comparisonTable.getItems().clear();
            productDetailsArea.clear();
            statusLabel.setStyle("-fx-text-fill: #c62828;");
            statusLabel.setText("Amount and duration must be valid numbers.");
        }
    }

    private void showScenarioDetails(LoanComparisonScenario scenario) {
        if (scenario == null) {
            productDetailsArea.clear();
            return;
        }

        String details = "Product: " + scenario.getProduct().getDisplayName() + "\n"
                + "Loan Type: " + scenario.getProduct().getLoanType() + "\n"
                + "Description: " + scenario.getProduct().getDescription() + "\n"
                + "Rate Policy: " + scenario.getProduct().getRatePolicyLabel() + "\n"
                + "Eligibility Notes: " + scenario.getProduct().getEligibilityNotes() + "\n"
                + "Scenario Amount: " + formatCurrency(scenario.getAmount()) + "\n"
                + "Scenario Duration: " + scenario.getDurationMonths() + " months\n"
                + "Estimated Monthly Installment: " + formatCurrency(scenario.getMonthlyInstallment());

        productDetailsArea.setText(details);
    }

    private String formatCurrency(double amount) {
        return String.format(Locale.US, "$%,.2f", amount);
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}
