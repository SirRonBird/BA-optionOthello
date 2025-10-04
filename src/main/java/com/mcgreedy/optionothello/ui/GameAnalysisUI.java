package com.mcgreedy.optionothello.ui;

import static com.mcgreedy.optionothello.utils.Constants.BOARD_SIZE;
import static com.mcgreedy.optionothello.utils.Constants.CELL_GAP;
import static com.mcgreedy.optionothello.utils.Constants.CELL_SIZE;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.dtos.OptionDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveDetails;
import com.mcgreedy.optionothello.utils.GUIUtils;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

public class GameAnalysisUI {

  private GameAnalysisUI() {
    throw new IllegalStateException("Static class");
  }

  private static final String LABEL_STYLE = "-fx-font-size: 16px; -fx-font-weight: bold;";
  private static final String BORDER_STYLE = "-fx-border-color: #cccccc; -fx-border-width: 1;";
  private static final int PADDING = 10;
  private static final int SPACING = 10;
  private static final int HALF_PADDING = PADDING / 2;
  private static final int HALF_SPACING = SPACING / 2;
  private static final int GAME_VISUALIZER_HEIGHT = 650;
  private static final int NO_MOVES_YET = -1;
  private static final int SLIDER_START_VALUE = -1;
  private static final int SLIDER_MIN_VALUE = -1;

  private static VBox moveDetailsContainer;

  public static GridPane gameBoard;

  static Circle[] markedPieces;

  public static void showGameAnalysis(SaveGameDTO saveGameDTO) {
    AnalyseUi.analysisPane.getChildren().clear();

    //Label
    Label headerLabel = createHeaderLabel(saveGameDTO);
    AnalyseUi.analysisPane.add(headerLabel, 0, 0, 2, 1);

    //Game visualizer
    VBox gameVisualizer = createGameVisualizer(saveGameDTO);
    AnalyseUi.analysisPane.add(gameVisualizer, 0, 1, 1, 1);

    //MoveDetails
    createMoveDetailsContainer();
    AnalyseUi.analysisPane.add(moveDetailsContainer, 1, 1, 1, 1);

  }

  private static Label createHeaderLabel(SaveGameDTO saveGameDTO) {
    Label label = new Label("Analysis for game: " + saveGameDTO.getGame().getGameName());
    label.setStyle(LABEL_STYLE);
    label.setPadding(new Insets(HALF_PADDING));
    return label;
  }

  private static VBox createGameVisualizer(SaveGameDTO saveGameDTO) {
    //Container
    VBox container = new VBox(SPACING);
    container.setPadding(new Insets(PADDING));
    container.setStyle(BORDER_STYLE);
    container.setPrefHeight(GAME_VISUALIZER_HEIGHT);

    //gameBoard
    gameBoard = GUIUtils.createBoardGrid(GUIUtils::markPiece, 1);

    /*GUIUtils.updateBoardGrid(
        saveGameDTO.getGame().getStartBoardBlack(),
        saveGameDTO.getGame().getStartBoardWhite(),
        gameBoard,
        NO_MOVES_YET
    );*/

    int boardSize = BOARD_SIZE * CELL_SIZE + (BOARD_SIZE-1) * CELL_GAP;
    Region border = new Region();
    border.setPrefSize(boardSize, boardSize);
    border.setStyle("-fx-border-color: red; -fx-border-width: 2;");

    StackPane board = new StackPane(border, gameBoard);
    board.setPadding(new Insets(PADDING));

    container.getChildren().add(board);

    //Moveslider
    HBox moveSliderContainer = new HBox(HALF_SPACING);
    moveSliderContainer.setAlignment(Pos.CENTER);

    Slider moveSlider = createMoveSlider(saveGameDTO, gameBoard);

    // back-Button (◀)
    Button backButton = new Button("◀");
    backButton.setOnAction(a -> {
      double current = moveSlider.getValue();
      if (current > moveSlider.getMin()) {
        moveSlider.setValue(current - 1);
      }
    });

    // front-Button (▶)
    Button forwardButton = new Button("▶");
    forwardButton.setOnAction(a -> {
      double current = moveSlider.getValue();
      if (current < moveSlider.getMax()) {
        moveSlider.setValue(current + 1);
      }
    });

    moveSliderContainer.getChildren().addAll(backButton, moveSlider, forwardButton);
    container.getChildren().add(moveSliderContainer);


    Button exportButton = new Button("Export game to png");
    exportButton.setOnAction(a -> {
      SaveGameUtils.saveBoardAsPng(gameBoard, "board.png");
    });

    container.getChildren().add(exportButton);

    return container;
  }

  private static Slider createMoveSlider(SaveGameDTO saveGameDTO, GridPane gameBoard) {
    Slider slider = new Slider(SLIDER_MIN_VALUE, saveGameDTO.getGame().getMoves().size() - 1f,
        SLIDER_START_VALUE);

    slider.setShowTickLabels(false);
    slider.setShowTickMarks(true);
    slider.setMajorTickUnit(1);
    slider.setMinorTickCount(0);
    slider.setSnapToTicks(true);
    HBox.setHgrow(slider, Priority.ALWAYS);
    slider.setMaxWidth(Double.MAX_VALUE);
    slider.valueProperty().addListener((a, b, newVal) -> {
      if (newVal.intValue() == SLIDER_START_VALUE) {
        GUIUtils.updateBoardGrid(
            saveGameDTO.getGame().getStartBoardBlack(),
            saveGameDTO.getGame().getStartBoardWhite(),
            gameBoard,
            NO_MOVES_YET
        );

        showMoveDetails(newVal.intValue(), null);
      } else {
        int currentMove = newVal.intValue();
        SaveGameDTO.MoveDetails moveDetails = saveGameDTO.getGame().getMoves().get(currentMove);

        GUIUtils.updateBoardGrid(
            moveDetails.getBlackBoardAfterMove(),
            moveDetails.getWhiteBoardAfterMove(),
            gameBoard,
            moveDetails.getPosition()
        );
        showMoveDetails(currentMove, moveDetails);
      }
    });
    return slider;
  }

  private static void createMoveDetailsContainer() {
    moveDetailsContainer = new VBox(SPACING);
    moveDetailsContainer.setPadding(new Insets(PADDING));
    moveDetailsContainer.setStyle(BORDER_STYLE);
    moveDetailsContainer.setPrefHeight(GAME_VISUALIZER_HEIGHT);
  }

  private static void showMoveDetails(int moveIndex, MoveDetails moveDetails) {
    moveDetailsContainer.getChildren().clear();

    if (moveDetails != null) {
      int moveNumber = moveIndex + 1;
      Label moveLabel = new Label("Move " + moveNumber);
      moveLabel.setStyle(LABEL_STYLE);
      moveDetailsContainer.getChildren().add(moveLabel);

      int movePosition = moveDetails.getPosition();
      Label movePositionLabel = new Label(movePosition + "");
      movePositionLabel.setStyle("-fx-font-size: 16px;");
      moveDetailsContainer.getChildren().add(movePositionLabel);

      OptionDTO moveOption = moveDetails.getMoveStatistics().getOption();
      Label moveOptionLabel = new Label(moveOption.getName());
      moveOptionLabel.setStyle("-fx-font-size: 16px;");
      moveDetailsContainer.getChildren().add(moveOptionLabel);
    }
  }



}
