package com.mcgreedy.optionothello.ui;

import static com.mcgreedy.optionothello.utils.Constants.BOARD_SIZE;
import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.dtos.OptionDTO;
import com.mcgreedy.optionothello.dtos.OptionDTO.BoardMaskDTO;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.utils.GUIUtils;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class OptionsUI {

    GridPane optionsPane;
    Button saveButton;
    TextField optionName;

    ListView<OptionDTO> optionListView;

    JavaScriptEditor policyEditor;
    JavaScriptEditor terminationEditor;

    final String standardPolicy = "// Schreibe hier deine Policy-Funktion\nfunction policy(state, move) {\n    return 1.0;\n}";
    final String standardTerminationCondition = "// Schreibe hier deine Termination-Funktion\nfunction shouldTerminate(state) {\n    return false;\n}";

    int currentPanelIndex = 0;
    Pane[] panels;

    Board maskBoard;
    List<Board> maskBoards;
    ListView<Board> boardsList;

    TextField maskNameTextField;

    int selectedMask = -1;
    GridPane maskBoardGrid;

    public OptionsUI() {
        this.optionsPane = createOptionsPane();
    }

    private GridPane createOptionsPane() {

        GridPane root = setupContainer();

        VBox optionList = createOptionList();

        BorderPane optionDetails = createOptionDetails();

        root.add(optionList, 0, 0);
        root.add(optionDetails, 1, 0);

        return root;
    }

    public GridPane getOptionsPane() {
        return optionsPane;
    }

    private GridPane setupContainer() {
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
        return pane;
    }

    private VBox createOptionList(){
        // Create the left pane (1/4 of the width)
        VBox pane = new VBox();
        pane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
        pane.setPrefHeight(700); // Set a preferred height

        VBox optionContainer = new VBox(5);

        TitledPane optionListTitlePane = new TitledPane("Options", optionContainer);
        optionListTitlePane.setCollapsible(false);
        optionListTitlePane.setExpanded(true);


        optionListView = createOptionListView();
        optionContainer.getChildren().add(optionListView);


        Button newOptionButton = new Button("New Option");
        newOptionButton.setPrefWidth(Double.MAX_VALUE);
        optionContainer.getChildren().add(newOptionButton);

        newOptionButton.setOnAction(e -> {
            newOption();
        });

        pane.getChildren().add(optionListTitlePane);
        return pane;
    }

    private void newOption() {
        //reset View
        maskBoard = new Board("default", true);
        maskBoards.clear();
        updateBoardsListView();
        for (Node node : maskBoardGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                // Entferne alle Circles aus dem StackPane
                cell.getChildren().removeIf(Circle.class::isInstance);
            }
        }
        policyEditor.setCode(standardPolicy);
        terminationEditor.setCode(standardTerminationCondition);
        optionListView.getSelectionModel().clearSelection();
        System.out.println(maskBoard);
    }

    private ListView<OptionDTO> createOptionListView(){
        ListView<OptionDTO> listView = new ListView<>();
        listView.setPrefHeight(650);

        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(OptionDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setStyle("-fx-padding: 5;");
                    setGraphic(createOptionDetailItem(item));
                }
            }
        });
        listView.getSelectionModel().selectedItemProperty().addListener(
            (_,_,option) -> {
                if(option != null) {
                    //get information from dto to show in GUI
                    maskBoards.clear();
                    maskBoards.addAll(SaveGameUtils.fromMaskDTO(option.getInitiationSet()));
                    updateBoardsListView();
                    policyEditor.setCode(option.getPolicy());
                    terminationEditor.setCode(option.getTerminationCondition());
                }
            }
        );


        return listView;
    }

    private Label createOptionDetailItem(OptionDTO item) {
        Label titleLabel = new Label(item.getName());
        titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        return titleLabel;
    }

    private BorderPane createOptionDetails(/*OptionDTO option */){
        // Create the right pane (3/4 of the width)


        VBox initiationSetPane = createInitiationSetPane();

        VBox policyPane = createPolicyPane();

        VBox terminationPane = createTerminationPane();

        VBox overviewPane = createOverviewPane();

        panels = new Pane[]{initiationSetPane,policyPane, terminationPane, overviewPane};

        StackPane container = new StackPane();
        container.getChildren().addAll(panels);
        saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            saveOption();
        });
        saveButton.setVisible(false);

        optionName = new TextField();
        optionName.setPromptText("Option-Name");
        optionName.setPrefWidth(200);
        optionName.setVisible(false);

        updateVisiblePanel();

        // Buttons
        Button btnBack = new Button("back");
        Button btnNext = new Button("next");

        // Button-Events
        btnBack.setOnAction(e -> {
            if (currentPanelIndex > 0) {
                currentPanelIndex--;
                updateVisiblePanel();
            }
        });

        btnNext.setOnAction(e -> {
            if (currentPanelIndex < panels.length - 1) {
                currentPanelIndex++;
                updateVisiblePanel();
            }
        });



        // Layout für Buttons
        HBox buttonBox = new HBox(10, btnBack, btnNext,optionName, saveButton);

        // Hauptlayout
        BorderPane root = new BorderPane();
        root.setCenter(container);
        root.setBottom(buttonBox);

        return root;
    }

    private VBox createOverviewPane(){
        VBox pane = new VBox();
        pane.setStyle("-fx-background-color: #f0f0f0;");
        pane.setPrefHeight(700);
        pane.setPadding(new Insets(10));

        Label heading = new Label("Overview");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 18");



        pane.getChildren().addAll(heading);

        return pane;
    }

    private VBox createTerminationPane(/* terminationConditionDTO */) {
        VBox pane = new VBox();
        pane.setStyle("-fx-background-color: #f0f0f0;");
        pane.setPrefHeight(700);
        pane.setPadding(new Insets(10));

        Label heading = new Label("Termination Condition");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 18");

        terminationEditor = new JavaScriptEditor();
        terminationEditor.setCode(standardTerminationCondition);

        pane.getChildren().addAll(heading, terminationEditor);

        return pane;
    }

    private VBox createPolicyPane(/* PolicyDTO ?*/) {
        VBox pane = new VBox();
        pane.setStyle("-fx-background-color: #f0f0f0;");
        pane.setPrefHeight(700);
        pane.setPadding(new Insets(10));

        Label heading = new Label("Policy");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 18");

        policyEditor = new JavaScriptEditor();
        policyEditor.setCode(standardPolicy);

        pane.getChildren().addAll(heading, policyEditor);

        return pane;
    }

    private VBox createInitiationSetPane(/*InitiationSetDTO ?*/) {
        VBox pane = new VBox();
        pane.setStyle("-fx-background-color: #f0f0f0;");
        pane.setPrefHeight(700);
        pane.setPadding(new Insets(10));

        Label heading = new Label("Initiation Set");
        heading.setStyle("-fx-font-weight: bold; -fx-font-size: 18");

        VBox boardCreationBox = createBoardPane();
        maskBoard = new Board("default",true);

        pane.getChildren().addAll(heading, boardCreationBox);

        return pane;
    }

    private void handleCellClick(int r, int c){
        int position = r * BOARD_SIZE +c;
        maskBoard.updateMask(position);
        GUIUtils.updateMaskGrid(maskBoard.mask, maskBoardGrid);
    }

    private VBox createBoardPane(){
        VBox boardCreationBox = new VBox();
        boardCreationBox.setSpacing(10);

        HBox boardBox = new HBox();
        boardBox.setSpacing(10);
        maskBoardGrid = GUIUtils.createBoardGrid(this::handleCellClick,1);
        maskBoards = new ArrayList<>();

        VBox boardControls = new VBox();
        boardControls.setSpacing(10);
        boardControls.setPadding(new Insets(10));
        boardControls.setAlignment(Pos.BASELINE_CENTER);
        boardControls.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");


        maskNameTextField = new TextField();
        maskNameTextField.setPromptText("Mask-Name");
        maskNameTextField.setPrefWidth(100);

        boardControls.getChildren().addAll(maskNameTextField);

        Button saveBoardButton = new Button("Save Mask");
        saveBoardButton.setPrefWidth(100);

        saveBoardButton.setOnAction(e -> saveMask());

        boardControls.getChildren().addAll(saveBoardButton);

        Button newBoardButton = new Button("New Mask");
        newBoardButton.setPrefWidth(100);

        newBoardButton.setOnAction(e -> newBoard());

        boardControls.getChildren().addAll(newBoardButton);

        Button deleteBoardButton = new Button("Delete Mask");
        deleteBoardButton.setPrefWidth(100);

        deleteBoardButton.setOnAction(e -> deleteMask());

        boardControls.getChildren().addAll(deleteBoardButton);

        boardBox.getChildren().addAll(maskBoardGrid, boardControls);
        boardsList = new ListView<>();
        boardsList.setOrientation(Orientation.HORIZONTAL);
        boardsList.setPrefHeight(115);
        boardsList.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Board item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle(null);
                } else {
                    setStyle("-fx-padding: 2;");
                    setGraphic(createMaskBoardItems(item));
                }
            }
        });

        boardsList.getSelectionModel().selectedItemProperty().addListener((r,c, mask) -> {
            selectedMask = boardsList.getSelectionModel().getSelectedIndex();
            if(selectedMask != -1){
                maskBoard = maskBoards.get(selectedMask);
                GUIUtils.updateMaskGrid(maskBoard.mask, maskBoardGrid);
                maskNameTextField.setText(maskBoard.name);
                }
            }
        );

        boardCreationBox.getChildren().addAll(boardBox,boardsList);

        return boardCreationBox;
    }

    private void deleteMask() {
        if(selectedMask != -1 ){
            maskBoards.remove(selectedMask);
            updateBoardsListView();
            newBoard();
        }
    }

    private VBox createMaskBoardItems(Board item) {
        VBox container = new VBox();
        container.setSpacing(1);
        container.setPadding(new Insets(2));

        Label label = new Label(item.name);

        StackPane maskBox = new StackPane();
        maskBox.setStyle("-fx-background-color: rgba(41,108,41,0.72); -fx-border-color: rgba(9,120,4,0);");
        maskBox.setMinSize(64,64);

        maskBox.getChildren().add(createMiniMaskBoard(item.mask));

        container.getChildren().addAll(label,maskBox);

        return container;
    }

    private GridPane createMiniMaskBoard(long mask) {
        int size = 8;
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setMinSize(64,64);
        grid.setAlignment(Pos.CENTER);
        grid.setMaxSize(64,64);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                // Berechne Bit-Index: MSB bei [0][0], LSB bei [7][7]
                int bitIndex = 63 - (row * 8 + col);
                boolean isOne = ((mask >> (63 - bitIndex)) & 1L) == 1;

                StackPane cell = createCell(isOne);
                grid.add(cell, col, row);
            }
        }

        return grid;
    }

    private StackPane createCell(boolean isBlack) {
        int cellSize = 9;

        StackPane cell = new StackPane();
        cell.setPrefSize(cellSize, cellSize);

        if(isBlack){
            cell.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));
        } else {
            cell.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        }

        return cell;
    }

    private void saveMask(){
        Board saveBoard = maskBoard.clone();
        //set name of saveBoard
        saveBoard.name = Objects.equals(maskNameTextField.getText(), "") ? "New Board" : maskNameTextField.getText();
        if(selectedMask == -1) {
            maskBoards.add(saveBoard);
        } else {
            maskBoards.set(selectedMask, saveBoard);
        }
        maskBoard.mask = saveBoard.mask;
        updateBoardsListView();
    }

    private void updateBoardsListView() {
        boardsList.getItems().clear();
        boardsList.getItems().addAll(maskBoards);
    }

    private void newBoard(){
        maskBoard.clearMask();
        for (Node node : maskBoardGrid.getChildren()) {
            if (node instanceof StackPane cell) {
                // Entferne alle Circles aus dem StackPane
                cell.getChildren().removeIf(Circle.class::isInstance);
            }
        }
    }

    private void updateVisiblePanel(){
        for(int i = 0; i < panels.length; i++){
            panels[i].setVisible(i == currentPanelIndex);
            saveButton.setVisible(currentPanelIndex == panels.length - 1);
            optionName.setVisible(currentPanelIndex == panels.length - 1);
        }
    }

    private void saveOption(){
        Option newOption = new Option(
          maskBoards, policyEditor.getCode(), terminationEditor.getCode()
        );
        String name = Objects.equals(optionName.getText(), "") ? "New Option" : optionName.getText();
        SaveGameUtils.saveOption(newOption,name);
        updateOptionListView();
    }

    public void updateOptionListView() {
        optionListView.getItems().clear();
        SaveGameUtils.getSaveOptions().forEach(option ->
            optionListView.getItems().add(option)
        );
    }
}
