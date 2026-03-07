package com.bms.presentation;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.bms.domain.controller.OverdraftHandler;
import com.bms.domain.entity.OverdraftEvent;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * OverdraftAlertView - UC-13: Admin views overdraft events
 */
public class OverdraftAlertView {
    private final VBox root;
    private final OverdraftHandler controller;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private TableView<OverdraftEvent> table;
    private Label statusLabel;
    private Runnable onBack;

    public OverdraftAlertView() {
        this.controller = new OverdraftHandler();
        this.root = createLayout();
    }

    @SuppressWarnings("unchecked")
    private VBox createLayout() {
        VBox layout = new VBox(12);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Overdraft Alerts");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        table = new TableView<>();
        table.setPrefHeight(300);

        TableColumn<OverdraftEvent, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getOverdraftId()));
        idCol.setPrefWidth(50);

        TableColumn<OverdraftEvent, String> acctCol = new TableColumn<>("Account");
        acctCol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getAccountNumber()));
        acctCol.setPrefWidth(120);

        TableColumn<OverdraftEvent, Number> amtCol = new TableColumn<>("Amount");
        amtCol.setCellValueFactory(d -> new SimpleDoubleProperty(d.getValue().getAmount()));
        amtCol.setPrefWidth(100);

        TableColumn<OverdraftEvent, String> timeCol = new TableColumn<>("Timestamp");
        timeCol.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getTimestamp() != null ? d.getValue().getTimestamp().format(FMT) : ""));
        timeCol.setPrefWidth(140);

        TableColumn<OverdraftEvent, Number> txCol = new TableColumn<>("Txn ID");
        txCol.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getTransactionId()));
        txCol.setPrefWidth(60);

        table.getColumns().addAll(idCol, acctCol, amtCol, timeCol, txCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> loadEvents());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();

        layout.getChildren().addAll(title, table, refreshBtn, statusLabel, backBtn);

        loadEvents();

        return layout;
    }

    private void loadEvents() {
        List<OverdraftEvent> events = controller.getAllOverdraftEvents();
        table.getItems().clear();
        table.getItems().addAll(events);
        statusLabel.setText("Showing " + events.size() + " overdraft event(s).");
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}