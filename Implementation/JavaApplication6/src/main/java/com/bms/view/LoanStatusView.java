package com.bms.view;

import java.util.List;

import com.bms.domain.controller.LoanStatusController;
import com.bms.domain.entity.Loan;
import com.bms.persistence.AuthContext;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * LoanStatusView - UC-11: Customer views their loan statuses
 */
public class LoanStatusView {
    private final VBox root;
    private final LoanStatusController controller;

    private TableView<Loan> loanTable;
    private ComboBox<String> filterCombo;
    private Label statusLabel;

    private Runnable onBack;

    public LoanStatusView() {
        this.controller = new LoanStatusController();
        this.root = createLayout();
    }

    @SuppressWarnings("unchecked")
    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("My Loan Applications");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        // Filter
        filterCombo = new ComboBox<>();
        filterCombo.getItems().addAll("All", "PENDING", "APPROVED", "REJECTED");
        filterCombo.setValue("All");

        Button refreshBtn = new Button("Load Loans");
        refreshBtn.setOnAction(e -> loadLoans());

        HBox filterRow = new HBox(10, new Label("Filter:"), filterCombo, refreshBtn);
        filterRow.setAlignment(Pos.CENTER);

        // Table
        loanTable = new TableView<>();
        loanTable.setPrefHeight(300);

        TableColumn<Loan, Number> idCol = new TableColumn<>("Loan ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getLoanId()));
        idCol.setPrefWidth(60);

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
        statusCol.setPrefWidth(90);

        TableColumn<Loan, String> purposeCol = new TableColumn<>("Purpose");
        purposeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPurpose()));
        purposeCol.setPrefWidth(150);

        loanTable.getColumns().addAll(idCol, amtCol, typeCol, durCol, statusCol, purposeCol);

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, filterRow, loanTable, statusLabel, backBtn);

        loadLoans();

        return layout;
    }

    private void loadLoans() {
        int customerId = AuthContext.getInstance().getLoggedInCustomerId();
        String filter = filterCombo.getValue();
        List<Loan> loans = controller.getLoansForCustomer(customerId, filter);
        loanTable.getItems().clear();
        loanTable.getItems().addAll(loans);

        if (loans.isEmpty()) {
            statusLabel.setText("No loans found.");
        } else {
            statusLabel.setText("Showing " + loans.size() + " loan(s).");
        }
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}