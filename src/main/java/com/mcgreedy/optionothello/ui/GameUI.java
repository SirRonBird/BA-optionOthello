package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.HumanPlayer;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.gamemanagement.RandomPlayer;
import com.mcgreedy.optionothello.utils.Constants;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.mcgreedy.optionothello.utils.Constants.*;

public class GameUI {

    //root
    BorderPane gamePane;

    //Gamemanager
    Gamemanager gameManager;
    int tournamentNumberOfGames = 250;
    int lastClickedCell;
    Player playerToMove;
    List<Integer> validMoveCells = new ArrayList<>();

    //UI Elements
    GridPane boardGrid;
    VBox leftPanel;
    VBox rightPanel;
    HBox bottomPanel;
    TitledPane blackPlayerPane;
    TitledPane whitePlayerPane;
    Label standingBlack;
    Label standingWhite;
    Button newGameButton;
    Label gamesPlayed;
    Label blackWins;
    Label whiteWins;

    //Player Settings
    private ComboBox<String> blackPlayerTypeSelector;
    private ComboBox<String> whitePlayerTypeSelector;

    //Logger
    private static final Logger LOGGER = LogManager.getLogger(GameUI.class);

    //Constructor
    public GameUI(Gamemanager gameManager){
        gamePane = createGamePane();
        this.gameManager = gameManager;
    }

    // getters ---------------------------------------------------------------------------
    public BorderPane getGamePane(){
        return gamePane;
    }

    public int getLastClickedCell() {
        return lastClickedCell;
    }

    // utility
    public void updateBoardGrid(long black, long white){
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int index = row * BOARD_SIZE + col;
                StackPane cell = getCellFromGrid(boardGrid, row, col);

                cell.getChildren().removeIf(node -> node instanceof Circle);

                long mask = 1L << index;

                if((black & mask) != 0){
                    Circle circle = createDisc(Color.BLACK);
                    cell.getChildren().add(circle);
                } else if((white & mask) != 0){
                    Circle circle = createDisc(Color.WHITE);
                    cell.getChildren().add(circle);
                }
            }
        }
    }

    private StackPane getCellFromGrid(GridPane grid, int row, int col) {
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                return (StackPane) node;
            }
        }
        throw new IllegalArgumentException("Cell not found at (" + row + ", " + col + ")");
    }

    private static Circle createDisc(Color color) {
        Circle disc = new Circle(CELL_SIZE * 0.4); // etwas kleiner als Zelle
        disc.setFill(color);
        return disc;
    }

    public void showPossibleMoves(long possibleMoves){
        validMoveCells.clear();
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                int index = row * BOARD_SIZE + col;
                StackPane cell = getCellFromGrid(boardGrid, row, col);

                long mask = 1L << index;

                if ((possibleMoves & mask) != 0) {
                    validMoveCells.add(index);
                    cell.setBorder(new Border(
                            new BorderStroke(
                                    Color.GRAY,
                                    BorderStrokeStyle.SOLID,
                                    new CornerRadii(1),
                                    new BorderWidths(2)
                            )
                    ));
                } else {
                    cell.setBorder(Border.EMPTY);
                }

            }
        }
    }

    public void updateGameStandings(int whiteScore, int blackScore){
        standingWhite.setText(String.valueOf(whiteScore));
        standingBlack.setText(String.valueOf(blackScore));
    }

    public void gameOver(int winner){
        showPossibleMoves(0L);
        showGameOverDialog(winner);
    }

    public void tournamentOver(int winner, int gamesPlayed, int blackScore, int whiteScore){
        showPossibleMoves(0L);
        showTournamentOverDialog(winner, gamesPlayed, blackScore, whiteScore);
    }

    public void setPlayerToMove(Player player){
        playerToMove = player;
        if (playerToMove.getColor() == PLAYER_COLOR.BLACK) {
            blackPlayerPane.setBorder(
                    new Border(
                            new BorderStroke(
                                    Color.INDIANRED,
                                    BorderStrokeStyle.SOLID,
                                    new CornerRadii(1),
                                    new BorderWidths(2)
                            )
                    )
            );
            whitePlayerPane.setBorder(Border.EMPTY);
        } else if (playerToMove.getColor() == PLAYER_COLOR.WHITE) {
            whitePlayerPane.setBorder(
                    new Border(
                            new BorderStroke(
                                    Color.INDIANRED,
                                    BorderStrokeStyle.SOLID,
                                    new CornerRadii(1),
                                    new BorderWidths(2)
                            )
                    )
            );
            blackPlayerPane.setBorder(Border.EMPTY);
        }
    }

    // UI creation ----------------------------------------------------------------------
    public BorderPane createGamePane(){
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        //create game board
        boardGrid = createBoardGrid();
        root.setCenter(boardGrid);

        //create left panel for Black Player
        leftPanel = createPlayerPanel(PLAYER_COLOR.BLACK);
        root.setLeft(leftPanel);

        //create right panel for white Player
        rightPanel = createPlayerPanel(PLAYER_COLOR.WHITE);
        root.setRight(rightPanel);

        //create bottom panel for controls
        bottomPanel = createControlPanel();
        root.setBottom(bottomPanel);


        return root;
    }

    private GridPane createBoardGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        grid.setStyle("-fx-background-color: #2E8B57;");


        // Create the cells
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                StackPane cell = createBoardCell(row, col);
                grid.add(cell, col, row);
            }
        }
        return grid;
    }

    private StackPane createBoardCell(int row, int col) {
        StackPane cell = new StackPane();
        cell.setPrefSize(CELL_SIZE, CELL_SIZE);

        final int r = row;
        final int c = col;

        cell.setOnMouseClicked(e -> handleCellClick(r, c));

        if (row == 1 && col == 1) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 5 0;");
        } else if (row == 1 && col == 2) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 0 5;");
        } else if (row == 2 && col == 1) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 5 0 0;");
        } else if (row == 2 && col == 2) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 5 0 0 0;");
        } else if (row == 1 && col == 5) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 5 0;");
        } else if (row == 1 && col == 6) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 0 5;");
        } else if (row == 2 && col == 6) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 5 0 0 0;");
        } else if (row == 2 && col == 5) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 5 0 0;");
        } else if (row == 5 && col == 1) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 5 0;");
        } else if (row == 5 && col == 2) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 0 5;");
        } else if (row == 6 && col == 2) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 5 0 0 0;");
        } else if (row == 6 && col == 1) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 5 0 0;");
        } else if (row == 5 && col == 5) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 5 0;");
        } else if (row == 5 && col == 6) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 0 0 5;");
        } else if (row == 6 && col == 6) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 5 0 0 0;");
        } else if (row == 6 && col == 5) {
            cell.setStyle("-fx-background-color: #3CB371; -fx-background-radius: 0 5 0 0;");
        } else {
            cell.setStyle("-fx-background-color: #3CB371;");
        }


        return cell;
    }

    private void handleCellClick(int r, int c) {
        lastClickedCell = r * BOARD_SIZE + c;
        LOGGER.debug("Clicked on field {},{} ({})", r, c, r * BOARD_SIZE + c);
        //TODO: Check wich players move it is.
        LOGGER.debug("Player moves: {}", playerToMove);
        if(validMoveCells.contains(lastClickedCell) && playerToMove.getType() == Constants.PLAYER_TYPE.HUMAN){
            LOGGER.debug("Valid move");
            playerToMove.makeMove();
        } else {
            LOGGER.debug("Invalid move");
        }
        lastClickedCell = -1;
    }

    private VBox createPlayerPanel(Constants.PLAYER_COLOR playerColor) {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #F0F0F0;");

        //Player Info
        TitledPane playerPane = new TitledPane();
        playerPane.setCollapsible(false);
        playerPane.setText("Player " + playerColor);


        VBox playerContent = new VBox(10);
        playerContent.setPadding(new Insets(10));
        VBox.setVgrow(playerContent, Priority.ALWAYS);

        //Player Type Selector
        Label playerTypeLabel = new Label("Player Type:");

        ComboBox<String> playerSelector = new ComboBox<>(PLAYER_TYPES);
        playerSelector.getSelectionModel().select(0);
        playerSelector.setMaxWidth(Double.MAX_VALUE);

        if (playerColor == PLAYER_COLOR.BLACK) {
            blackPlayerTypeSelector = playerSelector;
            blackPlayerPane = playerPane;
        } else {
            whitePlayerTypeSelector = playerSelector;
            whitePlayerPane = playerPane;
        }

        TitledPane settingPane = new TitledPane();
        settingPane.setCollapsible(false);
        settingPane.setText("Settings");


        VBox settingsBox = new VBox(5);
        settingsBox.setPadding(new Insets(5));
        PLAYER_TYPE initialPlayerType = PLAYER_TYPE.valueOf(playerSelector.getValue());
        settingsBox.getChildren().add(getSettingsBoxFor(initialPlayerType));

        settingPane.setContent(settingsBox);

        playerContent.getChildren().addAll(playerTypeLabel, playerSelector, settingPane, settingsBox);

        playerSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            try {
                PLAYER_TYPE playerType = PLAYER_TYPE.valueOf(newVal);
                settingsBox.getChildren().clear();
                settingsBox.getChildren().add(getSettingsBoxFor(playerType));
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
                System.err.println("Ungültiger PlayerType: " + newVal);
            }
        });

        playerPane.setContent(playerContent);
        panel.getChildren().add(playerPane);

        return panel;
    }

    private VBox getSettingsBoxFor(PLAYER_TYPE playerType) {
        VBox box = new VBox(10);

        switch (playerType) {
            case HUMAN -> {
                Button skipMove = new Button("Skip Move");
                skipMove.setOnAction(e -> {
                    //TODO: real logic
                    System.out.println("Skip Move");
                });
                skipMove.setPrefWidth(150);
                box.setAlignment(Pos.CENTER);
                box.getChildren().add(skipMove);
            }
            case MCTS -> {
                //TODO: needed Parameters
                HBox mctsMaxSearchDepthBox = new HBox(10);
                Label mctsMaxSearchDepthLabel = new Label("Max Search Depth:");
                Label mctsMaxDepth = new Label("50");
                Slider maxDepthSlider = new Slider(1, 100, 50);
                maxDepthSlider.setShowTickLabels(true);
                maxDepthSlider.setShowTickMarks(true);
                maxDepthSlider.setMajorTickUnit(1);
                maxDepthSlider.setMinorTickCount(0);
                maxDepthSlider.setSnapToTicks(true);
                maxDepthSlider.setMaxWidth(Double.MAX_VALUE);
                maxDepthSlider.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        mctsMaxDepth.textProperty().setValue(String.valueOf(newValue.intValue()));

                    }
                });
                mctsMaxSearchDepthBox.getChildren().addAll(mctsMaxSearchDepthLabel, mctsMaxDepth);

                HBox mctsExplorationParameterBox = new HBox(10);
                mctsExplorationParameterBox.setAlignment(Pos.CENTER);
                Label mctsExplorationParameterLabel = new Label("Exploration:");
                TextField mctsExplorationParameter = new TextField("1.41");
                mctsExplorationParameter.setEditable(true);
                mctsExplorationParameter.setPromptText("Exploration Parameter");

                mctsExplorationParameterBox.getChildren().addAll(mctsExplorationParameterLabel, mctsExplorationParameter);

                box.getChildren().addAll(mctsMaxSearchDepthBox, maxDepthSlider, mctsExplorationParameterBox);
            }
            case RANDOM_AI -> {
                box.setAlignment(Pos.CENTER);
                box.getChildren().add(new Label("Random AI needs no Parameters. Its random."));
            }
            case O_MCTS -> {
                HBox omctsExplorationParameterBox = new HBox(10);
                omctsExplorationParameterBox.setAlignment(Pos.CENTER);
                Label omctsExplorationParameterLabel = new Label("Exploration:");
                TextField omctsExplorationParameter = new TextField("1.41");
                omctsExplorationParameter.setEditable(true);
                omctsExplorationParameter.setPromptText("Exploration Parameter");

                omctsExplorationParameterBox.getChildren().addAll(omctsExplorationParameterLabel, omctsExplorationParameter);

                //TODO: Option List and possibility to create Options
                VBox optionBox = new VBox(5);
                optionBox.setAlignment(Pos.CENTER);
                Label optionLabel = new Label("Options:");
                ListView<String> options = new ListView<>();
                options.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
                ObservableList<String> items = FXCollections.observableArrayList();
                options.setItems(items);

                Button addOptionButton = new Button("Add Option");
                addOptionButton.setPrefWidth(200);
                addOptionButton.setOnAction(e -> {
                    //TODO: switch to Options Tab
                    //selectionModel.select(1);
                });

                optionBox.getChildren().addAll(optionLabel, options, addOptionButton);



                box.getChildren().addAll(omctsExplorationParameterBox, optionBox);

            }

        }

        return box;
    }

    private HBox createControlPanel() {
        HBox panel = new HBox();
        panel.setAlignment(Pos.CENTER);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #F0F0F0; -fx-border-color: #CCCCCC; -fx-border-width: 0.5 0 0 0;");
        panel.setSpacing(10);
        panel.setPrefHeight(150);

        // Game info section (LEFT)
        VBox gameInfoBox = new VBox(20);
        gameInfoBox.setAlignment(Pos.TOP_CENTER);
        gameInfoBox.setPadding(new Insets(10));
        gameInfoBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-padding: 10;");
        gameInfoBox.setPrefWidth(520); // Set a preferred width

        HBox gameStandingBox = new HBox(10);
        gameStandingBox.setAlignment(Pos.TOP_CENTER);
        gameStandingBox.setPadding(new Insets(10));
        gameStandingBox.setSpacing(10);
        HBox.setHgrow(gameStandingBox, Priority.ALWAYS);

        Label gameStandingsLabel = new Label("Game Standing: ");
        gameStandingsLabel.setStyle("-fx-font-weight: bold;");
        HBox standingBlackBox = new HBox(10);
        standingBlackBox.setAlignment(Pos.CENTER);
        HBox standingWhiteBox = new HBox(10);
        standingWhiteBox.setAlignment(Pos.CENTER);
        Circle standingBlackPiece = new Circle(8, Color.BLACK);
        Circle standingWhitePiece = new Circle(8, Color.WHITE);
        standingWhitePiece.setStroke(Color.GRAY);
        standingWhitePiece.setStrokeWidth(1);
        standingBlack = new Label("110");
        standingWhite = new Label("110");

        standingBlackBox.getChildren().addAll(standingBlackPiece, standingBlack);
        standingWhiteBox.getChildren().addAll(standingWhitePiece, standingWhite);

        gameStandingBox.getChildren().addAll(gameStandingsLabel, standingBlackBox, standingWhiteBox);

        HBox moveHistoryBox = new HBox(10);


        //TODO: make data model
        TableView<String> blackMoveTableView = new TableView<>();
        TableColumn<String, Integer> blackMoveNumberColumn = new TableColumn<>("#");
        TableColumn<String, String> blackMoveColumn = new TableColumn<>("Move");
        TableColumn<String, Integer> blackMoveSearchDepthColumn = new TableColumn<>("Search Depth");
        blackMoveTableView.getColumns().addAll(blackMoveNumberColumn, blackMoveColumn, blackMoveSearchDepthColumn);

        TableView<String> whiteMoveTableView = new TableView<>();
        TableColumn<String, Integer> whiteMoveNumberColumn = new TableColumn<>("#");
        TableColumn<String, String> whiteMoveColumn = new TableColumn<>("Move");
        TableColumn<String, Integer> whiteMoveSearchDepthColumn = new TableColumn<>("Search Depth");
        whiteMoveTableView.getColumns().addAll(whiteMoveNumberColumn, whiteMoveColumn, whiteMoveSearchDepthColumn);

        moveHistoryBox.getChildren().addAll(blackMoveTableView, whiteMoveTableView);

        gameInfoBox.getChildren().addAll(gameStandingBox, moveHistoryBox);

        // Game controls section (CENTER)
        VBox controlsBox = new VBox(15);
        controlsBox.setAlignment(Pos.CENTER);
        controlsBox.setPadding(new Insets(10, 0, 0, 0));
        controlsBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-padding: 10;");
        HBox.setHgrow(controlsBox, Priority.ALWAYS); // This makes it take available space
        controlsBox.setPrefWidth(200);

        newGameButton = new Button("New Game");
        newGameButton.setPrefWidth(150);
        newGameButton.setStyle("-fx-font-weight: Bold;");
        newGameButton.setOnAction(e -> {
            showNewGameDialog();
        });

        Button newTournamentButton = new Button("New Tournament");
        newTournamentButton.setPrefWidth(150);
        newTournamentButton.setStyle("-fx-font-weight: Bold;");
        newTournamentButton.setOnAction(e -> {
            //TODO: Start new Tournament
            showNewTournamentDialog();
        });

        controlsBox.getChildren().addAll(newGameButton, newTournamentButton);

        // Tournament info section (RIGHT)
        HBox tournamentInfoBox = new HBox(15);
        tournamentInfoBox.setAlignment(Pos.CENTER);
        tournamentInfoBox.setPadding(new Insets(10, 0, 0, 0));
        tournamentInfoBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-padding: 10;");
        tournamentInfoBox.setPrefWidth(520); // Set a preferred width

        VBox tournamentScoreBox = new VBox(5);
        tournamentScoreBox.setAlignment(Pos.CENTER);
        tournamentScoreBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1; -fx-padding: 10;");
        Label tournamentLabel = new Label("Scoreboard");
        tournamentLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        VBox score = new VBox(5);
        score.setAlignment(Pos.CENTER_LEFT);
        HBox blackWinsBox = new HBox(10);
        blackWinsBox.setAlignment(Pos.CENTER);
        HBox whiteWinsBox = new HBox(10);
        whiteWinsBox.setAlignment(Pos.CENTER);
        Circle blackWinsPiece = new Circle(8, Color.BLACK);
        Circle whiteWinsPiece = new Circle(8, Color.WHITE);
        whiteWinsPiece.setStroke(Color.GRAY);
        whiteWinsPiece.setStrokeWidth(1);
        blackWins = new Label("110");
        whiteWins = new Label("110");

        Label gamesPlayedLabel = new Label("Games played");
        gamesPlayed = new Label("0");

        HBox gamesPlayedBox = new HBox(5);

        gamesPlayedBox.getChildren().addAll(gamesPlayedLabel, gamesPlayed);

        blackWinsBox.getChildren().addAll(blackWinsPiece, blackWins);
        whiteWinsBox.getChildren().addAll(whiteWinsPiece, whiteWins);

        score.getChildren().addAll(gamesPlayedBox, blackWinsBox, whiteWinsBox);


        tournamentScoreBox.getChildren().addAll(tournamentLabel, score);


        tournamentInfoBox.getChildren().add(tournamentScoreBox);

        panel.getChildren().addAll(gameInfoBox, controlsBox, tournamentInfoBox);

        return panel;
    }

    private void showNewGameDialog(){

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Start new game");
        dialog.setHeaderText("Would you like to start a new game with this players/settings?");


        ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(startButtonType, cancelButtonType);

        Player blackPlayer = getBlackPlayer();
        Player whitePlayer = getWhitePlayer();

        // Placeholder
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        assert blackPlayer != null;
        assert whitePlayer != null;
        content.getChildren().addAll(
                new Label(blackPlayer.toString()),
                new Label(whitePlayer.toString())
        );

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(button -> {
            if (button == startButtonType) {
                gameManager.newGame(
                        Objects.requireNonNull(blackPlayer),
                        Objects.requireNonNull(whitePlayer)
                );
                newGameButton.setText("Reset Game");
            } else {
                LOGGER.info("Cancel new game dialog");
            }
        });
    }

    private void showNewTournamentDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Start new tournament");
        dialog.setHeaderText("Setup new tournament");

        ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(startButtonType, cancelButtonType);

        Player blackPlayer = getBlackPlayer();
        Player whitePlayer = getWhitePlayer();

        TitledPane tournamentSettingsPane = new TitledPane();
        tournamentSettingsPane.setPadding(new Insets(10));
        tournamentSettingsPane.setCollapsible(false);
        tournamentSettingsPane.setText("Tournament Settings");

        VBox tournamentSettingsBox = new VBox(10);
        tournamentSettingsBox.setPadding(new Insets(10));
        VBox.setVgrow(tournamentSettingsBox, Priority.ALWAYS);

        Label numberOfGamesLabel = new Label("Number of Games:");

        Spinner<Integer> numberOfGamesSpinner = new Spinner<>(1, 1000, 250,10);
        numberOfGamesSpinner.setEditable(true);
        numberOfGamesSpinner.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                tournamentNumberOfGames = newValue.intValue();
            }
        });

        tournamentSettingsBox.getChildren().addAll(numberOfGamesLabel, numberOfGamesSpinner);


        Separator separator = new Separator();
        separator.setOrientation(Orientation.HORIZONTAL);
        VBox content = new VBox(10);
        //TODO: Tournament Settings
        assert blackPlayer != null;
        assert whitePlayer != null;
        content.getChildren().addAll(
                new Label(blackPlayer.toString()),
                new Label(whitePlayer.toString()),
                separator,
                tournamentSettingsBox
        );

        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(button -> {
            if (button == startButtonType) {
                System.out.println("Start new tournament");
                LOGGER.info("Start new tournament with {} games", tournamentNumberOfGames);
                gameManager.newTournament(blackPlayer, whitePlayer, tournamentNumberOfGames);
            } else {
                System.out.println("Cancel");
            }
        });

    }

    private Player getBlackPlayer(){
        PLAYER_TYPE blackPlayerType = PLAYER_TYPE.valueOf(blackPlayerTypeSelector.getValue());
        switch (blackPlayerType){
            case HUMAN -> {
                return new HumanPlayer(
                        PLAYER_COLOR.BLACK,
                        PLAYER_TYPE.HUMAN,
                        gameManager
                );
            }
            case MCTS -> {
                //TODO: create MCTS Player
                return null;
            }
            case RANDOM_AI -> {
                //TODO: create Random AI Player
                return new RandomPlayer(
                        PLAYER_COLOR.BLACK,
                        PLAYER_TYPE.RANDOM_AI,
                        gameManager
                );
            }
            case O_MCTS -> {
                //TODO: create O_MCTS Player
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    private Player getWhitePlayer(){
        PLAYER_TYPE whitePlayerType = PLAYER_TYPE.valueOf(whitePlayerTypeSelector.getValue());
        switch (whitePlayerType){
            case HUMAN -> {
                return new HumanPlayer(
                        PLAYER_COLOR.WHITE,
                        PLAYER_TYPE.HUMAN,
                        gameManager
                );
            }
            case MCTS -> {
                //TODO: create MCTS Player
                return null;
            }
            case RANDOM_AI -> {
                //TODO: create Random AI Player
                return new RandomPlayer(
                        PLAYER_COLOR.WHITE,
                        PLAYER_TYPE.RANDOM_AI,
                        gameManager
                );
            }
            case O_MCTS -> {
                //TODO: create O_MCTS Player
                return null;
            }
            default -> {
                return null;
            }
        }
    }

    private void showGameOverDialog(int winner){
        String winnerString = winner == 0 ? "Black":"White";

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Game Over");
        dialog.setHeaderText("Game Over... " + winnerString + " wins!");

        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType);

        VBox content = new VBox(10);

        dialog.getDialogPane().setContent(content);

        LOGGER.info("Game Over Dialog: {} wins!", winnerString);
        Optional<ButtonType> result = dialog.showAndWait();
        result.ifPresent(button -> {
            if (button == saveButtonType) {
                LOGGER.info("Save Game");
                gameManager.saveGame();
            } else {
                LOGGER.info("Close Game");
            }
        });
    }

    private void showTournamentOverDialog(int winner, int gamesPlayed, int blackScore, int whiteScore){
        String winnerString = winner == 0 ? "Black":"White";

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Tournament Over");
        dialog.setHeaderText("Tournament Over... " + winnerString + " wins!");

        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        Label gamesPlayedLabel = new Label("Games played: " + gamesPlayed);
        Label blackWinsLabel = new Label("Black Wins: " + blackScore);
        Label whiteWinsLabel = new Label("White Wins: " + whiteScore);

        content.getChildren().addAll(gamesPlayedLabel, blackWinsLabel, whiteWinsLabel);


        ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);


        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, saveButtonType);
        dialog.getDialogPane().setContent(content);

        Optional<ButtonType> result = dialog.showAndWait();
    }
}

