package com.mcgreedy.optionothello.ui;

import static com.mcgreedy.optionothello.utils.Constants.BOARD_SIZE;
import static com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import static com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import static com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPES;

import com.mcgreedy.optionothello.ai.MCTSSettings;
import com.mcgreedy.optionothello.ai.MCTSPlayer;
import com.mcgreedy.optionothello.ai.OMCTSSettings;
import com.mcgreedy.optionothello.ai.OMCTSPlayer;
import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.ai.RandomPlayer;
import com.mcgreedy.optionothello.ai.options.AntiDiagControlOption;
import com.mcgreedy.optionothello.ai.options.BottomLeftCornerXOption;
import com.mcgreedy.optionothello.ai.options.BottomLeftStableCornerOption;
import com.mcgreedy.optionothello.ai.options.BottomRightCornerXOption;
import com.mcgreedy.optionothello.ai.options.BottomRightStableCornerOption;
import com.mcgreedy.optionothello.ai.options.CenterControlOption;
import com.mcgreedy.optionothello.ai.options.CornerOption;
import com.mcgreedy.optionothello.ai.options.DiagonalControlOption;
import com.mcgreedy.optionothello.ai.options.FrontierControlOption;
import com.mcgreedy.optionothello.ai.options.HeatmapOption;
import com.mcgreedy.optionothello.ai.options.MainDiagControlOption;
import com.mcgreedy.optionothello.ai.options.MaxFlipsOption;
import com.mcgreedy.optionothello.ai.options.MaximizeMobilityOption;
import com.mcgreedy.optionothello.ai.options.MinimizeEnemyMobilityOption;
import com.mcgreedy.optionothello.ai.options.MobilityOption;
import com.mcgreedy.optionothello.ai.options.ParityOption;
import com.mcgreedy.optionothello.ai.options.PotentialMobilityOption;
import com.mcgreedy.optionothello.ai.options.PreventOpponentCornerOption;
import com.mcgreedy.optionothello.ai.options.QuietMoveOption;
import com.mcgreedy.optionothello.ai.options.StableDiscsOption;
import com.mcgreedy.optionothello.ai.options.TopLeftCornerXOption;
import com.mcgreedy.optionothello.ai.options.TopLeftStableCornerOption;
import com.mcgreedy.optionothello.ai.options.TopRightCornerXOption;
import com.mcgreedy.optionothello.ai.options.TopRightStableCornerOption;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.HumanPlayer;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.GUIUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  Button blackSkipMove;
  Button whiteSkipMove;
  ListView<Option> whiteOptionsList;
  ListView<Option> blackOptionsList;

  //Player Settings
  private ComboBox<String> blackPlayerTypeSelector;
  private ComboBox<String> whitePlayerTypeSelector;

  // MCTS Parameters
  TextField whiteMctsExplorationParameter;
  TextField blackMctsExplorationParameter;
  boolean isWhiteMctsRaveEnabled = false;
  boolean isWhiteMctsMastEnabled = false;
  boolean isBlackMctsRaveEnabled = false;
  boolean isBlackMctsMastEnabled = false;
  CheckBox whiteMctsRAVEEnabledCheckBox;
  CheckBox whiteMctsMASTEnabledCheckBox;
  CheckBox blackMctsRAVEEnabledCheckBox;
  CheckBox blackMctsMASTEnabledCheckBox;

  // OMCTS Parameters
  TextField whiteOmctsExplorationParameter;
  TextField blackOmctsExplorationParameter;
  TextField blackOmctsDiscountFactorTextBox;
  TextField whiteOmctsDiscountFactorTextBox;
  CheckBox blackOmctsRAVEEnabledCheckBox;
  CheckBox whiteOmctsRAVEEnabledCheckBox;
  CheckBox blackOmctsMASTEnabledCheckBox;
  CheckBox whiteOmctsMASTEnabledCheckBox;
  boolean isBlackOmctsRaveEnabled = false;
  boolean isWhiteOmctsRaveEnabled = false;
  boolean isBlackOmctsMastEnabled = false;
  boolean isWhiteOmctsMastEnabled = false;
  List<Option> options = List.of(
      new TopLeftCornerXOption(),
      new TopRightCornerXOption(),
      new BottomLeftCornerXOption(),
      new BottomRightCornerXOption(),
      new TopLeftStableCornerOption(),
      new TopRightStableCornerOption(),
      new BottomLeftStableCornerOption(),
      new BottomRightStableCornerOption(),
      new QuietMoveOption(),
      new MaximizeMobilityOption(),
      new MinimizeEnemyMobilityOption(),
      new CenterControlOption(),
      new MainDiagControlOption(),
      new AntiDiagControlOption(),
      new MaxFlipsOption(),
      new PreventOpponentCornerOption()
  );

  // Game Parameter
  long searchTime=0;

  //Logger
  private static final Logger LOGGER = LogManager.getLogger(GameUI.class);

  //Constructor
  public GameUI(Gamemanager gameManager) {
    gamePane = createGamePane();
    this.gameManager = gameManager;
  }

  // getters ---------------------------------------------------------------------------
  public BorderPane getGamePane() {
    return gamePane;
  }

  public int getLastClickedCell() {
    return lastClickedCell;
  }

  // utility
  public void updateBoardGrid(long black, long white, int movePosition) {
    GUIUtils.updateBoardGrid(black, white, boardGrid, movePosition);
  }

  private StackPane getCellFromGrid(GridPane grid, int row, int col) {
    for (Node node : grid.getChildren()) {
      if (GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
        return (StackPane) node;
      }
    }
    throw new IllegalArgumentException("Cell not found at (" + row + ", " + col + ")");
  }

  public void showPossibleMoves(long possibleMoves) {
    validMoveCells.clear();
    if (possibleMoves == 0L) {
      if (gameManager.getCurrentPlayer().getColor() == PLAYER_COLOR.BLACK) {
        blackSkipMove.setStyle("-fx-background-color: #c82f2f;");
      } else if (gameManager.getCurrentPlayer().getColor() == PLAYER_COLOR.WHITE) {
        whiteSkipMove.setStyle("-fx-background-color: #c82f2f;");
      }
      return;
    }

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

  public void updateGameStandings(int whiteScore, int blackScore) {
    standingWhite.setText(String.valueOf(whiteScore));
    standingBlack.setText(String.valueOf(blackScore));
  }

  public void gameOver(int winner) {
    showPossibleMoves(0L);
    showGameOverDialog(winner);
  }

  public void tournamentOver(int winner, int gamesPlayed, int blackScore, int whiteScore) {
    showPossibleMoves(0L);
    showTournamentOverDialog(winner, gamesPlayed, blackScore, whiteScore);
  }

  public void setPlayerToMove(Player player) {
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

  public void updateScoreBoard(int blackScore, int whiteScore, int playedGames) {
    blackWins.setText(String.valueOf(blackScore));
    whiteWins.setText(String.valueOf(whiteScore));
    gamesPlayed.setText(String.valueOf(playedGames));
  }

  // UI creation ----------------------------------------------------------------------
  public BorderPane createGamePane() {
    BorderPane root = new BorderPane();
    root.setPadding(new Insets(10));

    //create game board
    boardGrid = GUIUtils.createBoardGrid(this::handleCellClick, 0);
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

  private void handleCellClick(int r, int c) {
    lastClickedCell = r * BOARD_SIZE + c;
    LOGGER.debug("Clicked on field {},{} ({})", r, c, r * BOARD_SIZE + c);
    LOGGER.debug("Player moves: {}", playerToMove);
    if (validMoveCells.contains(lastClickedCell)
        && playerToMove.getType() == Constants.PLAYER_TYPE.HUMAN) {
      LOGGER.debug("Valid move");
      playerToMove.getMove(gameManager.getCurrentGame().board);
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
    settingsBox.getChildren().add(getSettingsBoxFor(initialPlayerType, playerColor));

    settingPane.setContent(settingsBox);

    playerContent.getChildren().addAll(playerTypeLabel, playerSelector, settingPane, settingsBox);

    playerSelector.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
      try {
        PLAYER_TYPE playerType = PLAYER_TYPE.valueOf(newVal);
        settingsBox.getChildren().clear();
        settingsBox.getChildren().add(getSettingsBoxFor(playerType, playerColor));
      } catch (IllegalArgumentException e) {
        System.err.println(e.getMessage());
        System.err.println("Ungültiger PlayerType: " + newVal);
      }
    });

    playerPane.setContent(playerContent);
    panel.getChildren().add(playerPane);

    return panel;
  }

  private VBox getSettingsBoxFor(PLAYER_TYPE playerType, Constants.PLAYER_COLOR playerColor) {
    VBox box = new VBox(10);

    switch (playerType) {
      case HUMAN -> {
        box.setAlignment(Pos.CENTER);
        if (playerColor == PLAYER_COLOR.BLACK) {
          blackSkipMove = new Button("Skip Move");
          blackSkipMove.setPrefWidth(150);
          blackSkipMove.setOnAction(e -> {
            if (playerToMove.getType() == Constants.PLAYER_TYPE.HUMAN) {
              LOGGER.debug("Skip move");
              playerSkipMove(playerColor);
            }
          });
          box.getChildren().add(blackSkipMove);
        } else if (playerColor == PLAYER_COLOR.WHITE) {
          whiteSkipMove = new Button("Skip Move");
          whiteSkipMove.setPrefWidth(150);
          whiteSkipMove.setOnAction(e -> {
            if (playerToMove.getType() == Constants.PLAYER_TYPE.HUMAN) {
              LOGGER.debug("Skip move");
              playerSkipMove(playerColor);
            }
          });
          box.getChildren().add(whiteSkipMove);
        }
      }
      case MCTS -> {
        HBox mctsExplorationParameterBox = new HBox(10);
        mctsExplorationParameterBox.setAlignment(Pos.CENTER_LEFT);
        Label mctsExplorationParameterLabel = new Label("Exploration:");
        mctsExplorationParameterBox.getChildren().add(mctsExplorationParameterLabel);

        VBox mctsImprovementsBox = new VBox(5);
        mctsImprovementsBox.setAlignment(Pos.CENTER_LEFT);
        Label mctsImprovementsLabel = new Label("UCT Improvements:");
        mctsImprovementsBox.getChildren().add(mctsImprovementsLabel);

        if(playerColor == PLAYER_COLOR.WHITE) {
          //Exploration Parameter
          whiteMctsExplorationParameter = new TextField("1.41");
          whiteMctsExplorationParameter.setEditable(true);
          whiteMctsExplorationParameter.setPromptText("Exploration Parameter");
          mctsExplorationParameterBox.getChildren().add(whiteMctsExplorationParameter);
          //UCT Improvements
          whiteMctsMASTEnabledCheckBox = new CheckBox("MAST");
          whiteMctsMASTEnabledCheckBox.setSelected(false);
          whiteMctsMASTEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isWhiteMctsMastEnabled = newVal;
          });
          whiteMctsRAVEEnabledCheckBox = new CheckBox("RAVE");
          whiteMctsRAVEEnabledCheckBox.setSelected(false);
          whiteMctsRAVEEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isWhiteMctsRaveEnabled = newVal;
          });
          mctsImprovementsBox.getChildren().addAll(whiteMctsMASTEnabledCheckBox,
              whiteMctsRAVEEnabledCheckBox);
        } else {
          //Exploration Parameter
          blackMctsExplorationParameter = new TextField("1.41");
          blackMctsExplorationParameter.setEditable(true);
          blackMctsExplorationParameter.setPromptText("Exploration Parameter");
          mctsExplorationParameterBox.getChildren().add(blackMctsExplorationParameter);
          //UCT Improvements
          blackMctsMASTEnabledCheckBox = new CheckBox("MAST");
          blackMctsMASTEnabledCheckBox.setSelected(false);
          blackMctsMASTEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isBlackMctsMastEnabled = newVal;
          });
          blackMctsRAVEEnabledCheckBox = new CheckBox("RAVE");
          blackMctsRAVEEnabledCheckBox.setSelected(false);
          blackMctsRAVEEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isBlackMctsRaveEnabled = newVal;
          });
          mctsImprovementsBox.getChildren().addAll(blackMctsMASTEnabledCheckBox,
              blackMctsRAVEEnabledCheckBox);
        }


        box.getChildren().addAll(mctsExplorationParameterBox,mctsImprovementsBox);
      }
      case RANDOM_AI -> {
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(new Label("Random AI needs no Parameters. Its random."));
      }
      case O_MCTS -> {
        HBox omctsExplorationParameterBox = new HBox(10);
        omctsExplorationParameterBox.setAlignment(Pos.CENTER);
        Label omctsExplorationParameterLabel = new Label("Exploration:");
        omctsExplorationParameterBox.getChildren().add(omctsExplorationParameterLabel);

        HBox omctsDiscountFactorBox = new HBox(10);
        omctsDiscountFactorBox.setAlignment(Pos.CENTER);
        Label omctsDiscountFactorLabel = new Label("DiscountFactor:");
        omctsDiscountFactorBox.getChildren().addAll(omctsDiscountFactorLabel);

        VBox improvementsBox = new VBox(5);
        Label improvementsLabel = new Label("UCT Improvements:");
        improvementsBox.getChildren().add(improvementsLabel);

        VBox optionBox = new VBox(5);
        optionBox.setAlignment(Pos.CENTER);
        Label optionLabel = new Label("Options:");
        optionBox.getChildren().add(optionLabel);

        if(playerColor == PLAYER_COLOR.WHITE){
          //Exploration Parameter
          whiteOmctsExplorationParameter = new TextField("1.41");
          whiteOmctsExplorationParameter.setEditable(true);
          whiteOmctsExplorationParameter.setPromptText("Exploration Parameter");
          omctsExplorationParameterBox.getChildren().add(whiteOmctsExplorationParameter);
          //DiscountFactor
          whiteOmctsDiscountFactorTextBox = new TextField();
          whiteOmctsDiscountFactorTextBox.setEditable(true);
          whiteOmctsDiscountFactorTextBox.setPromptText("Omcts Discount");
          whiteOmctsDiscountFactorTextBox.setText("0.4");
          omctsDiscountFactorBox.getChildren().addAll(whiteOmctsDiscountFactorTextBox);
          //Improvements to UCT
          whiteOmctsMASTEnabledCheckBox = new CheckBox("MAST Enabled");
          whiteOmctsMASTEnabledCheckBox.setSelected(false);
          whiteOmctsRAVEEnabledCheckBox = new CheckBox("RAVE Enabled");
          whiteOmctsRAVEEnabledCheckBox.setSelected(false);
          whiteOmctsMASTEnabledCheckBox.selectedProperty().addListener((_, _, newVal) ->
              isWhiteOmctsMastEnabled = newVal);
          whiteOmctsRAVEEnabledCheckBox.selectedProperty().addListener((_, _, newVal)
              -> isWhiteOmctsRaveEnabled = newVal);
          improvementsBox.getChildren().addAll(whiteOmctsMASTEnabledCheckBox,
              whiteOmctsRAVEEnabledCheckBox);
          //Option list
          whiteOptionsList = createOptionsList();
          optionBox.getChildren().addAll(whiteOptionsList);
        } else {
          //Exploration Parameter
          blackOmctsExplorationParameter = new TextField("1.41");
          blackOmctsExplorationParameter.setEditable(true);
          blackOmctsExplorationParameter.setPromptText("Exploration Parameter");
          omctsExplorationParameterBox.getChildren().add(blackOmctsExplorationParameter);
          //DiscountFactor
          blackOmctsDiscountFactorTextBox = new TextField();
          blackOmctsDiscountFactorTextBox.setEditable(true);
          blackOmctsDiscountFactorTextBox.setPromptText("Omcts Discount");
          blackOmctsDiscountFactorTextBox.setText("0.4");
          omctsDiscountFactorBox.getChildren().add(blackOmctsDiscountFactorTextBox);
          //Improvements to UCT
          blackOmctsMASTEnabledCheckBox = new CheckBox("MAST Enabled");
          blackOmctsMASTEnabledCheckBox.setSelected(false);
          blackOmctsRAVEEnabledCheckBox = new CheckBox("RAVE Enabled");
          blackOmctsRAVEEnabledCheckBox.setSelected(false);
          blackOmctsMASTEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isBlackOmctsMastEnabled = newVal;
          });
          blackOmctsRAVEEnabledCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            isBlackOmctsRaveEnabled = newVal;
          });
          improvementsBox.getChildren().addAll(blackOmctsMASTEnabledCheckBox,
              blackOmctsRAVEEnabledCheckBox);
          //Option list
          blackOptionsList = createOptionsList();
          optionBox.getChildren().addAll(blackOptionsList);
        }

        box.getChildren().addAll(omctsExplorationParameterBox,omctsDiscountFactorBox,improvementsBox, optionBox);
      }
    }

    return box;
  }

  private ListView<Option> createOptionsList(){
    ObservableList<Option> observableOptions = FXCollections.observableArrayList(options);

    whiteOptionsList = new ListView<>(observableOptions);
    whiteOptionsList.setCellFactory(listView -> new ListCell<>() {
      @Override
      protected void updateItem(Option option, boolean empty) {
        super.updateItem(option, empty);
        if (empty || option == null) {
          setText(null);
        } else {
          setText(option.getName());
        }
      }
    });

    whiteOptionsList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

    return whiteOptionsList;
  }

  private void playerSkipMove(PLAYER_COLOR playerColor) {
    HumanPlayer player = (HumanPlayer) gameManager.getCurrentPlayer();
    if (player.getColor() == playerColor) {
      player.skipMove();
    }
  }

  private HBox createControlPanel() {
    HBox panel = new HBox();
    panel.setAlignment(Pos.CENTER);
    panel.setPadding(new Insets(10));
    panel.setStyle(
        "-fx-background-color: #F0F0F0; -fx-border-color: #CCCCCC; -fx-border-width: 0.5 0 0 0;");
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
    standingBlack = new Label("0");
    standingWhite = new Label("0");

    standingBlackBox.getChildren().addAll(standingBlackPiece, standingBlack);
    standingWhiteBox.getChildren().addAll(standingWhitePiece, standingWhite);

    gameStandingBox.getChildren().addAll(gameStandingsLabel, standingBlackBox, standingWhiteBox);
    gameInfoBox.getChildren().addAll(gameStandingBox);

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
    blackWins = new Label("0");
    whiteWins = new Label("0");

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


  private void showNewGameDialog() {

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Start new game");
    dialog.setHeaderText("Would you like to start a new game with this players/settings?");

    // Buttons
    ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(startButtonType, cancelButtonType);

    Player blackPlayer = getBlackPlayer();
    Player whitePlayer = getWhitePlayer();

    VBox contentBox = new VBox(10);
    contentBox.setPadding(new Insets(10));
    assert blackPlayer != null;
    assert whitePlayer != null;

    contentBox.getChildren().addAll(
        new Label("Black: " + blackPlayer.toString()),
        new Label("White: " + whitePlayer.toString())
    );

    contentBox.getChildren().add(new Separator(Orientation.HORIZONTAL));

    // --- Limitation Settings ---
    VBox limitationSettingsBox = new VBox(10);
    ToggleGroup limitationTypeGroup = new ToggleGroup();
    RadioButton simulationLimitToggle = new RadioButton("Simulation Limit");
    RadioButton timeLimitToggle = new RadioButton("Time Limit");
    simulationLimitToggle.setToggleGroup(limitationTypeGroup);
    timeLimitToggle.setToggleGroup(limitationTypeGroup);

    VBox limitationParameterBox = new VBox(10);
    limitationParameterBox.setPadding(new Insets(10));
    limitationParameterBox.setMaxWidth(Double.MAX_VALUE);

    // Spinner für Simulation Limit
    Spinner<Integer> simulationSpinner = new Spinner<>(100, 5000, 500);
    simulationSpinner.setEditable(true);
    Label simulationLabel = new Label("Simulationen pro Zug");

    // Slider für Time Limit
    Label searchTimeLabel = new Label("Search time 1s + ");
    Label sLabel = new Label("s");
    Slider searchTimeSlider = new Slider(0, 59, 0);
    searchTimeSlider.setShowTickLabels(true);
    searchTimeSlider.setShowTickMarks(true);
    searchTimeSlider.setSnapToTicks(true);
    searchTimeSlider.setMajorTickUnit(5);
    searchTimeSlider.setMinorTickCount(1);
    searchTimeSlider.setPrefWidth(200);

    // Live-Label für Sliderwert
    Label sliderValueLabel = new Label("0");
    sliderValueLabel.setPrefWidth(30);
    HBox searchTimeBox = new HBox(10, searchTimeLabel, searchTimeSlider, sliderValueLabel, sLabel);
    searchTimeBox.setAlignment(Pos.CENTER_LEFT);

    final long[] searchTime = {0};
    searchTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      searchTime[0] = newVal.longValue();
      sliderValueLabel.setText(String.valueOf(searchTime[0]));
    });

    // Listener für Toggle-Änderung
    limitationTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
      limitationParameterBox.getChildren().clear();
      if (newToggle == simulationLimitToggle) {
        limitationParameterBox.getChildren().addAll(simulationLabel, simulationSpinner);
      } else if (newToggle == timeLimitToggle) {
        limitationParameterBox.getChildren().add(searchTimeBox);
      }
    });

    limitationSettingsBox.getChildren().addAll(simulationLimitToggle, timeLimitToggle, limitationParameterBox);
    contentBox.getChildren().add(limitationSettingsBox);

    // ScrollPane sorgt dafür, dass Buttons sichtbar bleiben
    ScrollPane scrollPane = new ScrollPane(contentBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefViewportHeight(250); // Startgröße
    dialog.getDialogPane().setContent(scrollPane);

    Optional<ButtonType> result = dialog.showAndWait();
    result.ifPresent(button -> {
      if (button == startButtonType) {
        // Werte auslesen
        if (simulationLimitToggle.isSelected()) {
          int simulationsPerMove = simulationSpinner.getValue();
          LOGGER.info("Simulationen pro Zug: {}", simulationsPerMove);
          blackPlayer.setSimulationLimit(simulationsPerMove);
          whitePlayer.setSimulationLimit(simulationsPerMove);
          blackPlayer.setSearchTimeLimit(-1);
          whitePlayer.setSearchTimeLimit(-1);
        } else if (timeLimitToggle.isSelected()) {
          long timePerMove = searchTime[0];
          LOGGER.info("Search time: {}s", timePerMove);
          blackPlayer.setSearchTimeLimit(timePerMove);
          whitePlayer.setSearchTimeLimit(timePerMove);
          blackPlayer.setSimulationLimit(-1);
          whitePlayer.setSimulationLimit(-1);
        }

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


  private void showNewTournamentDialog() {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Start new tournament");
    dialog.setHeaderText("Setup new tournament");

    // Buttons
    ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialog.getDialogPane().getButtonTypes().addAll(startButtonType, cancelButtonType);

    // Spieler
    Player blackPlayer = getBlackPlayer();
    Player whitePlayer = getWhitePlayer();

    // --- Tournament Settings ---
    VBox tournamentSettingsBox = new VBox(10);
    tournamentSettingsBox.setPadding(new Insets(10));

    // Anzahl Spiele
    Label numberOfGamesLabel = new Label("Number of Games:");
    Spinner<Integer> numberOfGamesSpinner = new Spinner<>(1, 5000, 250, 10);
    numberOfGamesSpinner.setEditable(true);
    numberOfGamesSpinner.valueProperty().addListener((obs, oldValue, newValue) ->
        tournamentNumberOfGames = newValue.intValue()
    );

    // --- Limitation Settings ---
    VBox limitationSettingsBox = new VBox(10);
    limitationSettingsBox.setPadding(new Insets(10));

    ToggleGroup limitationTypeGroup = new ToggleGroup();
    RadioButton simulationLimitToggle = new RadioButton("Simulation Limit");
    RadioButton timeLimitToggle = new RadioButton("Time Limit");
    simulationLimitToggle.setToggleGroup(limitationTypeGroup);
    timeLimitToggle.setToggleGroup(limitationTypeGroup);

    // Container für dynamische Eingabe (Spinner oder Slider)
    VBox limitationParameterBox = new VBox(10);
    limitationParameterBox.setPadding(new Insets(10));
    limitationParameterBox.setMaxWidth(Double.MAX_VALUE);

    // --- Spinner für Simulation Limit ---
    Spinner<Integer> simulationSpinner = new Spinner<>(100, 5000, 500);
    simulationSpinner.setEditable(true);
    Label simulationLabel = new Label("Simulationen pro Zug");
    simulationLabel.setMaxWidth(Double.MAX_VALUE);

    // --- Slider für Time Limit ---
    Label searchTimeLabel = new Label("Search time 1s + ");
    Label sLabel = new Label("s");
    Slider searchTimeSlider = new Slider(0, 59, 0);
    searchTimeSlider.setShowTickLabels(true);
    searchTimeSlider.setShowTickMarks(true);
    searchTimeSlider.setSnapToTicks(true);
    searchTimeSlider.setMajorTickUnit(5);
    searchTimeSlider.setMinorTickCount(1);
    searchTimeSlider.setPrefWidth(200);

    // Live Label für Sliderwert
    Label sliderValueLabel = new Label("0");
    sliderValueLabel.setPrefWidth(30);
    HBox searchTimeBox = new HBox(10, searchTimeLabel, searchTimeSlider, sliderValueLabel, sLabel);
    searchTimeBox.setAlignment(Pos.CENTER_LEFT);

    final long[] searchTime = {0};
    searchTimeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      searchTime[0] = newVal.longValue();
      sliderValueLabel.setText(String.valueOf(searchTime[0]));
    });

    // Listener für Toggle-Änderung
    limitationTypeGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
      limitationParameterBox.getChildren().clear();
      if (newToggle == simulationLimitToggle) {
        limitationParameterBox.getChildren().addAll(simulationLabel, simulationSpinner);
      } else if (newToggle == timeLimitToggle) {
        limitationParameterBox.getChildren().add(searchTimeBox);
      }
    });

    limitationSettingsBox.getChildren().addAll(simulationLimitToggle, timeLimitToggle, limitationParameterBox);

    tournamentSettingsBox.getChildren().addAll(numberOfGamesLabel, numberOfGamesSpinner, limitationSettingsBox);

    // Separator
    Separator separator = new Separator();
    separator.setOrientation(Orientation.HORIZONTAL);

    // Content Box (Spieler + Settings)
    VBox contentBox = new VBox(10);
    contentBox.setPadding(new Insets(10));
    contentBox.getChildren().addAll(
        new Label("Black: " + blackPlayer.toString()),
        new Label("White: " + whitePlayer.toString()),
        separator,
        tournamentSettingsBox
    );

    // ScrollPane sorgt dafür, dass Buttons sichtbar bleiben
    ScrollPane scrollPane = new ScrollPane(contentBox);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefViewportHeight(300); // Startgröße
    dialog.getDialogPane().setContent(scrollPane);

    // Start Button Action
    Optional<ButtonType> result = dialog.showAndWait();
    result.ifPresent(button -> {
      if (button == startButtonType) {
        if (simulationLimitToggle.isSelected()) {
          int simulationsPerMove = simulationSpinner.getValue();
          LOGGER.info("Simulationen pro Zug: {}", simulationsPerMove);
          blackPlayer.setSimulationLimit(simulationsPerMove);
          whitePlayer.setSimulationLimit(simulationsPerMove);
          blackPlayer.setSearchTimeLimit(-1);
          whitePlayer.setSearchTimeLimit(-1);
        } else if (timeLimitToggle.isSelected()) {
          long timePerMove = searchTime[0];
          LOGGER.info("Search time: {}s", timePerMove);
          blackPlayer.setSearchTimeLimit(timePerMove);
          whitePlayer.setSearchTimeLimit(timePerMove);
          blackPlayer.setSimulationLimit(-1);
          whitePlayer.setSimulationLimit(-1);
        }
        LOGGER.info("Start new tournament with {} games", tournamentNumberOfGames);
        gameManager.newTournament(blackPlayer, whitePlayer, tournamentNumberOfGames);
      } else {
        LOGGER.info("Cancel new tournament dialog");
      }
    });
  }


  private Player getBlackPlayer() {
    PLAYER_TYPE blackPlayerType = PLAYER_TYPE.valueOf(blackPlayerTypeSelector.getValue());
    switch (blackPlayerType) {
      case HUMAN -> {
        return new HumanPlayer(
            PLAYER_COLOR.BLACK,
            PLAYER_TYPE.HUMAN,
            gameManager
        );
      }
      case MCTS -> {
        MCTSSettings mctsSettings = new MCTSSettings(
            Double.parseDouble(blackMctsExplorationParameter.getText()),
            isBlackMctsMastEnabled,
            isBlackMctsRaveEnabled
        );
        return new MCTSPlayer(
            PLAYER_COLOR.BLACK,
            PLAYER_TYPE.MCTS,
            gameManager,
            mctsSettings
        );
      }
      case RANDOM_AI -> {
        return new RandomPlayer(
            PLAYER_COLOR.BLACK,
            PLAYER_TYPE.RANDOM_AI,
            gameManager
        );
      }
      case O_MCTS -> {
        List<Option> selectedOptions = new ArrayList<>(blackOptionsList.getSelectionModel().getSelectedItems());

        OMCTSSettings omctsSettings = new OMCTSSettings(
            Double.parseDouble(blackOmctsDiscountFactorTextBox.getText()),
            Double.parseDouble(blackOmctsExplorationParameter.getText()),
            selectedOptions,
            isBlackOmctsMastEnabled,
            isBlackOmctsRaveEnabled

        );

        return new OMCTSPlayer(
            PLAYER_COLOR.BLACK,
            PLAYER_TYPE.O_MCTS,
            gameManager,
            omctsSettings
        );
      }
      default -> {
        return null;
      }
    }
  }

  private Player getWhitePlayer() {
    PLAYER_TYPE whitePlayerType = PLAYER_TYPE.valueOf(whitePlayerTypeSelector.getValue());
    switch (whitePlayerType) {
      case HUMAN -> {
        return new HumanPlayer(
            PLAYER_COLOR.WHITE,
            PLAYER_TYPE.HUMAN,
            gameManager
        );
      }
      case MCTS -> {
        MCTSSettings mctsSettings = new MCTSSettings(
            Double.parseDouble(whiteMctsExplorationParameter.getText()),
            isWhiteMctsMastEnabled,
            isWhiteMctsRaveEnabled
        );
        return new MCTSPlayer(
            PLAYER_COLOR.WHITE,
            PLAYER_TYPE.MCTS,
            gameManager,
            mctsSettings
        );
      }
      case RANDOM_AI -> {
        return new RandomPlayer(
            PLAYER_COLOR.WHITE,
            PLAYER_TYPE.RANDOM_AI,
            gameManager
        );
      }
      case O_MCTS -> {
        List<Option> selectedOptions = new ArrayList<>(whiteOptionsList.getSelectionModel().getSelectedItems());

        OMCTSSettings omctsSettings = new OMCTSSettings(
            Double.parseDouble(whiteOmctsDiscountFactorTextBox.getText()),
            Double.parseDouble(whiteOmctsExplorationParameter.getText()),
            selectedOptions,
            isWhiteOmctsMastEnabled,
            isWhiteOmctsRaveEnabled
        );
        return new OMCTSPlayer(
            PLAYER_COLOR.WHITE,
            PLAYER_TYPE.O_MCTS,
            gameManager,
            omctsSettings
        );
      }
      default -> {
        return null;
      }
    }
  }

  private void showGameOverDialog(int winner) {
    String winnerString = winner == 0 ? "Black" : "White";

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Game Over");
    dialog.setHeaderText("Game Over... " + winnerString + " wins!");

    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(okButtonType);

    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
    dialog.getDialogPane().getButtonTypes().addAll(saveButtonType);

    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    Label savegameNameLabel = new Label("Savegame Name:");
    TextField saveGameName = new TextField();

    content.getChildren().addAll(savegameNameLabel, saveGameName);

    dialog.getDialogPane().setContent(content);

    LOGGER.info("Game Over Dialog: {} wins!", winnerString);
    Optional<ButtonType> result = dialog.showAndWait();
    result.ifPresent(button -> {
      if (button == saveButtonType) {
        LOGGER.info("Save Game");
        gameManager.saveGame(saveGameName.getText());
      } else {
        LOGGER.info("Close Game");
      }
    });
  }

  private void showTournamentOverDialog(int winner, int gamesPlayed, int blackScore,
      int whiteScore) {
    String winnerString = "";
    if (winner == 0) {
      winnerString = "Black";
    } else if (winner == 1) {
      winnerString = "White";
    } else if (winner == 2) {
      winnerString = "Nobody";
    }

    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Tournament Over");
    dialog.setHeaderText("Tournament Over... " + winnerString + " wins!");

    VBox content = new VBox(10);
    content.setPadding(new Insets(10));
    Label gamesPlayedLabel = new Label("Games played: " + gamesPlayed);
    Label blackWinsLabel = new Label("Black Wins: " + blackScore);
    Label whiteWinsLabel = new Label("White Wins: " + whiteScore);

    Label savegameNameLabel = new Label("Savegame Name:");
    TextField saveTournamentName = new TextField();

    content.getChildren()
        .addAll(gamesPlayedLabel, blackWinsLabel, whiteWinsLabel, savegameNameLabel,
            saveTournamentName);

    ButtonType okButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
    ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);

    dialog.getDialogPane().getButtonTypes().addAll(okButtonType, saveButtonType);
    dialog.getDialogPane().setContent(content);

    Optional<ButtonType> result = dialog.showAndWait();
    result.ifPresent(button -> {
      if (button == saveButtonType) {
        LOGGER.info("Save Tournament");
        gameManager.saveTournament(saveTournamentName.getText());
      } else {
        LOGGER.info("Close Tournament");
      }
    });
  }
}

