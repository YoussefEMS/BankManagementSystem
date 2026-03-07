package com.bms.presentation;

import com.bms.domain.controller.WithdrawCashController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * WithdrawCashForm - UC-06: Process a cash withdrawal
 */
public class WithdrawCashForm {
    private final VBox root;
    private final WithdrawCashController controller;

    private TextField accountNoField;
    private TextField amountField;
    private TextField descriptionField;
    private Label statusLabel;

    private Runnable onBack;

    public WithdrawCashForm() {
        this.controller = new WithdrawCashController();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Process Withdrawal");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        accountNoField = new TextField();
        accountNoField.setPromptText("Account Number");

        amountField = new TextField();
        amountField.setPromptText("Amount (USD)");

        descriptionField = new TextField();
        descriptionField.setPromptText("Description (optional)");

        Button submitBtn = new Button("Process Withdrawal");
        submitBtn.setPrefWidth(200);
        submitBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #d32f2f; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> handleSubmit());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, accountNoField, amountField,
                descriptionField, submitBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleSubmit() {
        try {
            double amount = Double.parseDouble(amountField.getText().trim());
            int result = controller.withdrawCash(
                    accountNoField.getText().trim(),
                    amount,
                    descriptionField.getText().trim());

            if (result > 0) {
                statusLabel.setStyle("-fx-text-fill: green;");
                statusLabel.setText("Withdrawal processed! Transaction ID: " + result);
                amountField.clear();
                descriptionField.clear();
            } else if (result == -1) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: Invalid amount.");
            } else if (result == -2) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: Account not found or not active.");
            } else if (result == -3) {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: Insufficient funds.");
            } else {
                statusLabel.setStyle("-fx-text-fill: red;");
                statusLabel.setText("Error: Withdrawal failed.");
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