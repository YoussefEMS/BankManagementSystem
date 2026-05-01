package com.bms.view;

import com.bms.persistence.AuthContext;
import com.bms.service.AccountManagementService;

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
 * UpdateAccountStatusForm - UC-08: Admin form to change account status
 */
public class UpdateAccountStatusForm {
    private final VBox root;
    private final AccountManagementService controller;

    private TextField accountNoField;
    private ComboBox<String> statusCombo;
    private Label statusLabel;

    private Runnable onBack;

    public UpdateAccountStatusForm() {
        this.controller = new AccountManagementService();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Update Account Status");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        accountNoField = new TextField();
        accountNoField.setPromptText("Account Number");

        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("ACTIVE", "FROZEN", "CLOSED");
        statusCombo.setPromptText("Select New Status");

        Button submitBtn = new Button("Update Status");
        submitBtn.setPrefWidth(200);
        submitBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #f57c00; -fx-text-fill: white;");
        submitBtn.setOnAction(e -> handleSubmit());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, accountNoField, statusCombo,
                submitBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleSubmit() {
        String accountNo = accountNoField.getText().trim();
        String newStatus = statusCombo.getValue();

        if (accountNo.isEmpty() || newStatus == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        int adminId = AuthContext.getInstance().getLoggedInCustomerId();
        boolean success = controller.updateAccountStatus(adminId, accountNo, newStatus);

        if (success) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Account " + accountNo + " status updated to " + newStatus);
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Failed. Account may not exist or already has that status.");
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}
