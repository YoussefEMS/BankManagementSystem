package com.bms.presentation;

import com.bms.domain.controller.MonthlyInterestHandler;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * MonthlyInterestJob - UC-12: Admin trigger for monthly interest posting
 */
public class MonthlyInterestJob {
    private final VBox root;
    private final MonthlyInterestHandler controller;

    private Label statusLabel;
    private Runnable onBack;

    public MonthlyInterestJob() {
        this.controller = new MonthlyInterestHandler();
        this.root = createLayout();
    }

    private VBox createLayout() {
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(500);

        Label title = new Label("Monthly Interest Posting");
        title.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label description = new Label(
                "Click the button below to calculate and post monthly interest " +
                        "to all eligible Savings and Money Market accounts.");
        description.setWrapText(true);

        Button runBtn = new Button("Post Monthly Interest");
        runBtn.setPrefWidth(250);
        runBtn.setStyle("-fx-font-size: 14px; -fx-background-color: #7b1fa2; -fx-text-fill: white;");
        runBtn.setOnAction(e -> handleRun());

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> {
            if (onBack != null)
                onBack.run();
        });

        statusLabel = new Label();
        statusLabel.setWrapText(true);

        layout.getChildren().addAll(title, description, runBtn, statusLabel, backBtn);
        return layout;
    }

    private void handleRun() {
        int count = controller.postMonthlyInterest();
        statusLabel.setStyle("-fx-text-fill: green;");
        statusLabel.setText("Interest posted to " + count + " account(s).");
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnBack(Runnable callback) {
        this.onBack = callback;
    }
}