package com.bms.presentation;

import java.util.List;

import com.bms.domain.controller.LoanDecisionHandler;
import com.bms.domain.entity.Loan;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoanReviewForm - UC-10: Admin reviews pending loans
 */
public class LoanReviewForm {
    private final VBox root;
    private final LoanDecisionHandler controller;

    private TableView<Loan> loanTable;
    private Label statusLabel;

    private Runnable onBack;

    public LoanReviewForm() {
        this.controller = new LoanDecisionHandler();
        this.root = createLayout();
    }

    @SuppressWarnings("unchecked")
    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Loan Review — Pending Applications");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Table
        loanTable = new TableView<>();
        loanTable.setPrefHeight(300);

        TableColumn<Loan, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLoanId()));
        idCol.setPrefWidth(50);

        TableColumn<Loan, Number> custCol = new TableColumn<>("Customer");
        custCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCustomerId()));
        custCol.setPrefWidth(80);

        TableColumn<Loan, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getAmount()));
        amtCol.setPrefWidth(100);

        TableColumn<Loan, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLoanType()));
        typeCol.setPrefWidth(80);

        TableColumn<Loan, Number> durCol = new TableColumn<>("Months");
        durCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDurationMonths()));
        durCol.setPrefWidth(70);

        TableColumn<Loan, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));
        statusCol.setPrefWidth(80);

        loanTable.getColumns().addAll(idCol, custCol, amtCol, typeCol, durCol, statusCol);

        // Buttons
        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadPendingLoans());

        Button approveBtn = new Button("Approve");
        approveBtn.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white;");
        approveBtn.setOnAction(e -> handleDecision("APPROVED"));

        Button rejectBtn = new Button("Reject");
        rejectBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white;");
        rejectBtn.setOnAction(e -> handleDecision("REJECTED"));

        HBox buttonRow = new HBox(10, refreshBtn, approveBtn, rejectBtn);
        buttonRow.setAlignment(Pos.CENTER);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, loanTable, buttonRow, statusLabel, backBtn);

        // Load data on init
        loadPendingLoans();

        return layout;
    }

    private void loadPendingLoans() {
        List<Loan> pending = controller.getPendingLoans();
        loanTable.getItems().clear();
        loanTable.getItems().addAll(pending);
        statusLabel.setText("Loaded " + pending.size() + " pending loan(s).");
        statusLabel.setStyle("-fx-text-fill: #333;");
    }

    private void handleDecision(String decision) {
        Loan selected = loanTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Please select a loan from the table.");
            return;
        }

        boolean success = controller.decideLoan(selected.getLoanId(), decision);
        if (success) {
            statusLabel.setStyle("-fx-text-fill: green;");
            statusLabel.setText("Loan #" + selected.getLoanId() + " " + decision);
            loadPendingLoans();
        } else {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Failed to update loan.");
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}