package com.bms.view;

import com.bms.domain.controller.CustomerProfileController;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * CreateCustomerProfileForm - UC-03: Admin form to create new customer profiles
 */
public class CreateCustomerProfileForm {
    private final VBox root;
    private final CustomerProfileController controller;

    private TextField fullNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField addressField;
    private TextField nationalIdField;
    private Label statusLabel;

    private Runnable onBack;

    public CreateCustomerProfileForm() {
        this.controller = new CustomerProfileController();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Create Customer Profile");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        fullNameField = new TextField();
        fullNameField.setPromptText("Full Name *");

        emailField = new TextField();
        emailField.setPromptText("Email Address *");

        phoneField = new TextField();
        phoneField.setPromptText("Mobile Phone *");

        addressField = new TextField();
        addressField.setPromptText("Address");

        nationalIdField = new TextField();
        nationalIdField.setPromptText("National ID");

        Button submitBtn = new Button("Create Customer");
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

        layout.getChildren().addAll(title, fullNameField, emailField, phoneField,
                addressField, nationalIdField, submitBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleSubmit() {
        int result = controller.createCustomerProfile(
                fullNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText(),
                nationalIdField.getText());

        if (result > 0) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Customer created successfully! ID: " + result);
            clearFields();
        } else if (result == -2) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Email already exists.");
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: Please fill in all required fields (*).");
        }
    }

    private void clearFields() {
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        addressField.clear();
        nationalIdField.clear();
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}