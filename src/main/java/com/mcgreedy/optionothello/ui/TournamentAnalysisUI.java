package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveTournamentDTO;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO.GameDetails;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO.MoveDetails;
import com.mcgreedy.optionothello.utils.GUIUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TournamentAnalysisUI {
  private TournamentAnalysisUI() {
    throw new IllegalStateException("Static class");
  }

  private static final String LABEL_STYLE = "-fx-font-size: 16px; -fx-font-weight: bold;";
  private static final String HINT_STYLE = "-fx-font-size: 12px; -fx-font-weight: normal;";
  private static final String BORDER_STYLE = "-fx-border-color: #cccccc; -fx-border-width: 1;";
  private static final String GAME_DETAIL_STYLE = " -fx-padding: 5; -fx-border-color: transparent transparent #cccccc transparent; -fx-border-width: 0 0 1 0;";
  private static final String GAME_DETAIL_TITLE_STYLE = "-fx-font-size: 14px; -fx-font-weight: bold;";
  private static final String GAME_DETAIL_PLAYER_BOX_STYLE = "-fx-border-color: #CCCCCC; -fx-border-width: 1;-fx-padding: 10;";
  private static final String WINNER_STYLE = "-fx-text-fill: green;";
  private static final int PADDING = 10;
  private static final int SPACING = 10;
  private static final int HALF_PADDING = PADDING / 2;
  private static final int HALF_SPACING = SPACING / 2;
  private static final int GAME_DETAILS_SPACING = 2;
  private static final int GAME_VISUALIZER_WIDTH = 500;
  private static final int GAME_VISUALIZER_HEIGHT = 650;
  private static final int NO_MOVES_YET = -1;
  private static final int SLIDER_START_VALUE = -1;
  private static final int SLIDER_MIN_VALUE = -1;

  private static final int GAME_SELECTOR_WIDTH = 200;

  private static VBox gameVisualizerContainer;
  private static VBox moveDetailsContainer;

  public static void showTournamentAnalysis(SaveTournamentDTO saveTournamentDTO){
    AnalyseUi.analysisPane.getChildren().clear();

    //Game visualization
    createGameVisualizer();
    AnalyseUi.analysisPane.add(gameVisualizerContainer, 0, 1, 1, 1);

    //Label + game selection
    HBox titleContainer = createTitleContainer(saveTournamentDTO);
    AnalyseUi.analysisPane.add(titleContainer, 0, 0 , 2, 1);

    //Movedetails
    createMoveDetailsContainer();
    AnalyseUi.analysisPane.add(moveDetailsContainer, 1, 1, 1, 1);
  }

  private static void createGameVisualizer() {
    gameVisualizerContainer = new VBox(SPACING);
    gameVisualizerContainer.setPadding(new Insets(PADDING));
    gameVisualizerContainer.setPrefWidth(GAME_VISUALIZER_WIDTH);
    Label placeholder = new Label("Select a Game to analyse");
    placeholder.setStyle("-fx-font-style: italic;");
    gameVisualizerContainer.getChildren().add(placeholder);

  }

  private static HBox createTitleContainer(SaveTournamentDTO saveTournamentDTO){
    HBox container = new HBox(SPACING);
    container.setPadding(new Insets(HALF_PADDING));
    container.setStyle(BORDER_STYLE);
    container.setPrefHeight(GAME_VISUALIZER_HEIGHT);

    Label titleLabel = new Label("Analysis of Tournament: " + saveTournamentDTO.getTournament().getTournamentName());
    titleLabel.setStyle(LABEL_STYLE);
    container.getChildren().add(titleLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    container.getChildren().add(spacer);

    ComboBox<GameDetails> gameSelection = createGameSelection(saveTournamentDTO);
    container.getChildren().add(gameSelection);

    return container;
  }

  private static ComboBox<GameDetails> createGameSelection(SaveTournamentDTO saveTournamentDTO){
    ComboBox<GameDetails> gameSelection = new ComboBox<>();
    gameSelection.setPrefWidth(GAME_SELECTOR_WIDTH);
    gameSelection.getItems().addAll(saveTournamentDTO.getTournament().getGames());

    gameSelection.setCellFactory(_ -> new ListCell<>() {
      @Override
      protected void updateItem(SaveTournamentDTO.GameDetails item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
          setStyle(null);
        } else {
          setStyle("-fx-padding: 0;");

          setGraphic(createGameDetailsItem(item));
        }
      }
    });

    gameSelection.setButtonCell(new ListCell<>() {
      @Override
      protected void updateItem(SaveTournamentDTO.GameDetails item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
        } else {
          String winnerString = item.getWinner() == 0 ? "Black" : "White";
          setText(item.getGameNumber() + " - " + winnerString);
        }
      }
    });

    gameSelection.valueProperty().addListener((_, _, newGame) -> {
      if(newGame != null){
        //update gameAnalysisBox
        updateGameVisualizerContainer(newGame);
      }
    });


    Label gameSelectionLabel = new Label("Game - Winner");
    gameSelectionLabel.setStyle(HINT_STYLE);
    gameSelection.setPromptText("Game - Winner");
    return gameSelection;
  }

  private static VBox createGameDetailsItem(GameDetails game) {
    VBox container = new VBox();
    container.setSpacing(GAME_DETAILS_SPACING);
    container.setStyle(GAME_DETAIL_STYLE);

    Label titleLabel = new Label(String.valueOf(game.getGameNumber()));
    titleLabel.setStyle(GAME_DETAIL_TITLE_STYLE);

    final Label blackPlayerLabel = new Label("Black");
    final Label whitePlayerLabel = new Label("White");

    int winner = game.getWinner();
    if (winner == 0) {
      blackPlayerLabel.setStyle(WINNER_STYLE);
    } else if (winner == 1) {
      whitePlayerLabel.setStyle(WINNER_STYLE);
    }

    HBox playerBox = new HBox(HALF_SPACING);
    playerBox.setAlignment(Pos.CENTER);
    playerBox.setStyle(GAME_DETAIL_PLAYER_BOX_STYLE);

    final Circle blackPiece = new Circle(8, Color.BLACK);
    final Circle whitePiece = new Circle(8, Color.WHITE);
    whitePiece.setStroke(Color.GRAY);
    whitePiece.setStrokeWidth(1);

    playerBox.getChildren().addAll(blackPiece, blackPlayerLabel,
        new Separator(Orientation.VERTICAL), whitePlayerLabel, whitePiece);

    container.getChildren().addAll(titleLabel, playerBox);

    return container;
  }

  private static void createMoveDetailsContainer(){
    moveDetailsContainer = new VBox(SPACING);
    moveDetailsContainer.setPadding(new Insets(PADDING));
    moveDetailsContainer.setStyle(BORDER_STYLE);
    moveDetailsContainer.setPrefHeight(GAME_VISUALIZER_HEIGHT);
  }

  private static void updateGameVisualizerContainer(GameDetails newGame) {
    gameVisualizerContainer.getChildren().clear();
    GridPane gameBoard = GUIUtils.createBoardGrid(null, 1);
    GUIUtils.updateBoardGrid(
        newGame.getStartBoardBlack(),
        newGame.getStartBoardWhite(),
        gameBoard,
        NO_MOVES_YET
    );
    gameVisualizerContainer.getChildren().add(gameBoard);

    //Moveslider
    HBox moveSliderContainer = new HBox(HALF_SPACING);
    moveSliderContainer.setAlignment(Pos.CENTER);

    Slider moveSlider = createMoveSlider(newGame, gameBoard);

    // back-Button (◀)
    Button backButton = new Button("◀");
    backButton.setOnAction(_ -> {
      double current = moveSlider.getValue();
      if (current > moveSlider.getMin()) {
        moveSlider.setValue(current - 1);
      }
    });

    // front-Button (▶)
    Button forwardButton = new Button("▶");
    forwardButton.setOnAction(_ -> {
      double current = moveSlider.getValue();
      if (current < moveSlider.getMax()) {
        moveSlider.setValue(current + 1);
      }
    });

    moveSliderContainer.getChildren().addAll(backButton,moveSlider, forwardButton);

    gameVisualizerContainer.getChildren().add(moveSliderContainer);
  }

  private static Slider createMoveSlider(GameDetails game,GridPane gameBoard){
    Slider slider = new Slider(SLIDER_MIN_VALUE, game.getMoves().size() - 1f, SLIDER_START_VALUE);
    slider.setShowTickLabels(false);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1);
    slider.setMinorTickCount(0);
    slider.setSnapToTicks(true);
    HBox.setHgrow(slider, Priority.ALWAYS);
    slider.setMaxWidth(Double.MAX_VALUE);
    slider.valueProperty().addListener((_, _, newVal) -> {
      if(newVal.intValue() == SLIDER_START_VALUE){
        GUIUtils.updateBoardGrid(
            game.getStartBoardBlack(),
            game.getStartBoardWhite(),
            gameBoard,
            NO_MOVES_YET
        );

        showMoveDetails(newVal.intValue(), null);
      } else {
        int currentMove = newVal.intValue();
        MoveDetails moveDetails = game.getMoves().get(currentMove);

        GUIUtils.updateBoardGrid(
            moveDetails.getBlackBoardAfterMove(),
            moveDetails.getWhiteBoardAfterMove(),
            gameBoard,
            moveDetails.getPosition()
        );
        showMoveDetails(currentMove,moveDetails);
      }
    });
    return slider;
  }

  private static void showMoveDetails(int moveIndex, MoveDetails moveDetails) {
    moveDetailsContainer.getChildren().clear();
    if(moveDetails != null){
      int moveNumber = moveIndex + 1;
      Label moveLabel = new Label("Move " + moveNumber);
      moveLabel.setStyle(LABEL_STYLE);
      moveDetailsContainer.getChildren().add(moveLabel);
    }
  }

}
