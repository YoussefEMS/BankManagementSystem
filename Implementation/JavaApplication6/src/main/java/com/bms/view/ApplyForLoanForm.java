package com.bms.view;

import com.bms.domain.controller.LoanApplicationService;
import com.bms.persistence.AuthContext;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * ApplyForLoanForm - UC-09: Customer loan application
 */
public class ApplyForLoanForm {
    private final VBox root;
    private final LoanApplicationService controller;

    private TextField amountField;
    private ComboBox<String> loanTypeCombo;
    private TextField durationField;
    private TextField purposeField;
    private Label statusLabel;

    private Runnable onBack;

    public ApplyForLoanForm() {
        this.controller = new LoanApplicationService();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Apply for a Loan");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        amountField = new TextField();
        amountField.setPromptText("Loan Amount (USD)");

        loanTypeCombo = new ComboBox<>();
        loanTypeCombo.getItems().addAll("PERSONAL", "HOME", "AUTO");
        loanTypeCombo.setPromptText("Loan Type");

        durationField = new TextField();
        durationField.setPromptText("Duration (months)");

        purposeField = new TextField();
        purposeField.setPromptText("Purpose");

        Button submitBtn = new Button("Submit Application");
        submitBtn.setPrefWidth(200);
        submitBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #1976d2; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> handleSubmit());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, amountField, loanTypeCombo,
                durationField, purposeField, submitBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleSubmit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            int duration = Integer.parseInt(durationField.getText().trim());
            String loanType = loanTypeCombo.getValue();
            String purpose = purposeField.getText().trim();

            if (loanType == null) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Please select a loan type.");
                return;
            }

            int customerId = AuthContext.getInstance().getLoggedInCustomerId();

            LoanApplicationService.LoanResult result =
                    controller.applyForLoan(customerId, amount, loanType, duration, purpose);

            if (result.getLoanId() > 0) {
                if ("APPROVED".equalsIgnoreCase(result.getStatus())) {
                    statusLabel.setStyle("-fx-text-fill: green;");
                } else {
                    statusLabel.setStyle("-fx-text-fill: red;");
                }

                statusLabel.setText("Loan " + result.getStatus() + ". Loan ID: " + result.getLoanId());

                amountField.clear();
                durationField.clear();
                purposeField.clear();
                loanTypeCombo.setValue(null);
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: Please check all fields.");
            }
        } catch (NumberFormatException ex) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Amount and duration must be valid numbers.");
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}