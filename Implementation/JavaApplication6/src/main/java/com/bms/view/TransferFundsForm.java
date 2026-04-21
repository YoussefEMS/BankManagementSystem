package com.bms.view;

import com.bms.domain.controller.AuthenticationController;
import com.bms.domain.controller.FundsTransferProcessor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * TransferFundsForm - UC-07: Transfer funds between accounts
 */
public class TransferFundsForm {
    private final VBox root;
    private final FundsTransferProcessor controller;
    private final AuthenticationController authController;

    private TextField sourceField;
    private TextField destinationField;
    private TextField amountField;
    private Label statusLabel;

    private Runnable onBack;

    public TransferFundsForm() {
        this.controller = new FundsTransferProcessor();
        this.authController = new AuthenticationController();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Transfer Funds");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        sourceField = new TextField();
        sourceField.setPromptText("Source Account Number");

        destinationField = new TextField();
        destinationField.setPromptText("Destination Account Number");

        amountField = new TextField();
        amountField.setPromptText("Amount (USD)");

        Button submitBtn = new Button("Transfer");
        submitBtn.setPrefWidth(200);
        submitBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #1976d2; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> handleSubmit());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, sourceField, destinationField,
                amountField, submitBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleSubmit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            int customerId = authController.getLoggedInCustomerId();

            String refCode = controller.transferFunds(
                    customerId,
                    sourceField.getText().trim(),
                    destinationField.getText().trim(),
                    amount);

            if (refCode != null) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Transfer successful! Reference: " + refCode);
                amountField.clear();
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Transfer failed. Check accounts, balances, and amount.");
            }
        } catch (NumberFormatException ex) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Please enter a valid number for amount.");
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}