package com.mcgreedy.optionothello.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OptionsUI {

    GridPane optionsPane;

    public OptionsUI() {
        this.optionsPane = createOptionsPane();
    }

    private GridPane createOptionsPane() {

        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10));
        pane.setHgap(10);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(25);
        column1.setHgrow(Priority.ALWAYS);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(75);
        column2.setHgrow(Priority.ALWAYS);

        pane.getColumnConstraints().addAll(column1, column2);

        // Create the left pane (1/4 of the width)
        VBox leftPane = new VBox();
        leftPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        leftPane.setPrefHeight(700); // Set a preferred height
        Label leftLabel = new Label("List of Options (1/4)");
        leftPane.getChildren().add(leftLabel);

        // Create the right pane (3/4 of the width)
        VBox rightPane = new VBox();
        rightPane.setSpacing(10);
        Label rightLabel = new Label("Option details (3/4)");

        JavaScriptEditor policyEditor = new JavaScriptEditor();
        policyEditor.setCode("// Schreibe hier deine Policy-Funktion\nfunction policy(state, actions) {\n    return 1.0;\n}");

        JavaScriptEditor terminationEditor = new JavaScriptEditor();
        terminationEditor.setCode("// Schreibe hier deine Termination-Funktion\nfunction shouldTerminate(state) {\n    return false;\n}");

        rightPane.getChildren().addAll(rightLabel, new Label("Policy:"), policyEditor, new Label("Termination:"), terminationEditor);



        pane.add(leftPane, 0, 0);
        pane.add(rightPane, 1, 0);

        return pane;
    }


    public GridPane getOptionsPane() {
        return optionsPane;
    }
}
