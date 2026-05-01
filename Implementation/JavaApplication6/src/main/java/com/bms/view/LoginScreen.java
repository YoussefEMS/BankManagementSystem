package com.bms.view;

import com.bms.service.CustomerAuthenticationService;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * LoginScreen - Login UI for customer authentication
 * Provides email/password input and handles authentication
 */
public class LoginScreen {
    private BorderPane root;
    private TextField emailField;
    private PasswordField passwordField;
    private Label statusLabel;
    private Runnable onLoginSuccess;
    private CustomerAuthenticationService authController;

    public LoginScreen() {
        this.authController = new CustomerAuthenticationService();
        initializeUI();
    }

    /**
     * Initialize the login UI
     */
    private void initializeUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.setPadding(new Insets(20));

        // Center: Login form
        VBox loginForm = createLoginForm();
        root.setCenter(loginForm);

        // Bottom: Footer
        Label footerLabel = new Label("Bank Management System");
        footerLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 12;");
        BorderPane.setAlignment(footerLabel, Pos.CENTER);
        root.setBottom(footerLabel);
        BorderPane.setMargin(footerLabel, new Insets(20, 0, 0, 0));
    }

    /**
     * Create the login form
     */
    private VBox createLoginForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: white;");
        form.setPadding(new Insets(40));
        form.setMaxWidth(400);
        form.setMaxHeight(350);

        // Title
        Label titleLabel = new Label("Welcome");
        titleLabel.setFont(new Font("Arial", 28));
        titleLabel.setStyle("-fx-font-weight: bold;");

        Label subtitleLabel = new Label("Sign in to your account");
        subtitleLabel.setStyle("-fx-text-fill: #999; -fx-font-size: 14;");

        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setStyle("-fx-font-weight: bold;");
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setStyle("-fx-font-weight: bold;");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setStyle("-fx-padding: 10; -fx-font-size: 14;");

        // Status label
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #d32f2f; -fx-font-size: 12;");

        // Login button
        Button loginButton = new Button("Sign In");
        loginButton.setPrefWidth(Double.MAX_VALUE);
        loginButton.setPrefHeight(45);
        loginButton.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-background-color: #1976d2; -fx-text-fill: white;");
        loginButton.setOnAction(event -> handleLogin());

        // Handle Enter key
        passwordField.setOnAction(event -> handleLogin());

        // Register link
        Label registerLabel = new Label("Don't have an account? Register here");
        registerLabel.setStyle("-fx-text-fill: #1976d2; -fx-cursor: hand;");
        registerLabel.setOnMouseClicked(event -> onRegisterClick());

        form.getChildren().addAll(
                titleLabel,
                subtitleLabel,
                new Separator(),
                emailLabel,
                emailField,
                passwordLabel,
                passwordField,
                statusLabel,
                loginButton,
                registerLabel
        );

        return form;
    }

    /**
     * Handle login button click
     */
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validation
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }

        // Attempt authentication using controller
        boolean isAuthenticated = authController.authenticate(email, password);
        if (isAuthenticated) {
            // Clear fields
            emailField.clear();
            passwordField.clear();
            statusLabel.setText("");
            
            // Notify success
            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            showError("Invalid email or password");
            passwordField.clear();
        }
    }

    /**
     * Handle register click (stub for now)
     */
    private void onRegisterClick() {
        showInfo("Registration feature coming soon!");
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #d32f2f;");
    }

    /**
     * Show info message
     */
    private void showInfo(String message) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: #1976d2;");
    }

    /**
     * Get the root UI element
     */
    public BorderPane getRoot() {
        return root;
    }

    /**
     * Set success callback
     */
    public void setOnLoginSuccess(Runnable callback) {
        this.onLoginSuccess = callback;
    }
}
