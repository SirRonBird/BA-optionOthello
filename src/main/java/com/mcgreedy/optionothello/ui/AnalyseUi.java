package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Creates and updates the UI for the analysis of games and tournaments.
 */
public class AnalyseUi {

  GridPane analysePane;

  static GridPane analysisPane;
  static GridPane statisticsPane;

  ListView<SaveGameDTO> gamesListView;
  ListView<SaveTournamentDTO> tournamentsListView;

  private static final String WINNER_STYLE = "-fx-text-fill: green;";

  private static final Logger LOGGER = LogManager.getLogger(AnalyseUi.class);

  /**
   * Constructor for the analyseUI. Sets the pane created in createAnalysePane().
   */
  public AnalyseUi() {
    this.analysePane = createAnalysePane();
  }

  /**
   * Returns the AnalysePane to use inside the GUI.
   *
   * @return analysePane GridPane with the Analyse UI.
   */
  public GridPane getAnalysePane() {
    return analysePane;
  }

  private GridPane createAnalysePane() {
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

    TabPane typePane = createSelectionPane();
    pane.add(typePane, 0, 0);

    // Create the right pane (3/4 of the width)
    TabPane analyseTapPane = createRightPane();
    pane.add(analyseTapPane, 1, 0);

    return pane;
  }

  private static TabPane createRightPane() {
    TabPane analyseTapPane = new TabPane();
    analyseTapPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    analyseTapPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
    analyseTapPane.setStyle(
        "-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");

    analysisPane = analysisPane();
    statisticsPane = statisticsPane();

    Tab statistics = new Tab("Statistics", statisticsPane);
    analyseTapPane.getTabs().add(statistics);

    Tab tournamentAnalysisTab = new Tab("Analysis", analysisPane);
    analyseTapPane.getTabs().add(tournamentAnalysisTab);

    return analyseTapPane;
  }

  private static GridPane analysisPane() {

    GridPane pane = new GridPane();

    ColumnConstraints column1 = new ColumnConstraints();
    column1.setPercentWidth(60);

    ColumnConstraints column2 = new ColumnConstraints();
    column2.setPercentWidth(40);

    pane.getColumnConstraints().addAll(column1, column2);

    RowConstraints row1 = new RowConstraints();
    row1.setPercentHeight(5);

    RowConstraints row2 = new RowConstraints();
    row2.setPercentHeight(95);

    pane.getRowConstraints().addAll(row1, row2);

    return pane;
  }

  private static GridPane statisticsPane() {

    // Show statistics depending on selected game or tournament with different stats

    return new GridPane();
  }

  private TabPane createSelectionPane() {
    TabPane typePane = new TabPane();
    typePane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    typePane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
    typePane.setStyle(
        "-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-width: 1;");
    typePane.widthProperty().addListener((_, _, newVal) -> {
      double halfWidth = (newVal.doubleValue() / 2) - 22;
      typePane.setTabMinWidth(halfWidth);
      typePane.setTabMaxWidth(halfWidth);
    });

    Tab gamesTab = createGameSelectionTab();
    typePane.getTabs().add(gamesTab);

    Tab tournamentTab = createTournamentSelectionTab();
    typePane.getTabs().add(tournamentTab);

    typePane.getSelectionModel().selectedItemProperty().addListener((_, _, newTab) -> {
      if (newTab == gamesTab) {
        updateGamesListView();
      } else if (newTab == tournamentTab) {
        updateTournamentsListView();
      } else {
        LOGGER.error("Unknown tab selected");
      }
    });
    return typePane;
  }

  private Tab createGameSelectionTab() {
    VBox gamesPane = new VBox();
    gamesPane.setPadding(new Insets(5));
    gamesPane.setPrefHeight(700);
    gamesPane.setStyle("-fx-background-color: #f0f0f0;");

    gamesListView = new ListView<>();
    gamesListView.setPrefHeight(700);
    gamesListView.setCellFactory(_ -> new ListCell<>() {
      @Override
      protected void updateItem(SaveGameDTO item, boolean empty) {
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
    updateGamesListView();
    gamesListView.getSelectionModel().selectedItemProperty()
        .addListener((_, _, newGame) -> {
          if (newGame != null) {
            GameAnalysisUI.showGameAnalysis(newGame);
            GameStatisicsUI.showGameStatistics(newGame);
          }
        });

    gamesPane.getChildren().add(gamesListView);

    return new Tab("Games", gamesPane);
  }

  private Tab createTournamentSelectionTab() {
    VBox tournamentPane = new VBox();
    tournamentPane.setPadding(new Insets(5));
    tournamentPane.setPrefHeight(700);
    tournamentPane.setStyle("-fx-background-color: #f0f0f0;");

    tournamentsListView = new ListView<>();
    tournamentsListView.setPrefHeight(700);
    tournamentsListView.setCellFactory(_ -> new ListCell<>() {

      @Override
      protected void updateItem(SaveTournamentDTO item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
          setStyle(null);
        } else {
          setStyle("-fx-padding: 0;");

          setGraphic(createTournamentDetailsItem(item));
        }
      }
    });
    updateTournamentsListView();

    tournamentsListView.getSelectionModel().selectedItemProperty()
        .addListener((_, _, newTournament) -> {
          if (newTournament != null) {
            TournamentAnalysisUI.showTournamentAnalysis(newTournament);
            TournamentStatisticsUI.showTournamentStatistics(newTournament);
          }
        });

    tournamentPane.getChildren().add(tournamentsListView);
    return new Tab("Tournaments", tournamentPane);
  }

  private VBox createTournamentDetailsItem(SaveTournamentDTO tournament) {
    Label titleLabel = new Label(tournament.getTournament().getTournamentName());
    titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

    Label numberOfGameLabel = new Label(tournament.getTournament().getNumberOfGames() + " games");
    numberOfGameLabel.setStyle("-fx-font-size: 14px;");

    BorderPane titleBox = new BorderPane();
    titleBox.setPadding(new Insets(5));
    titleBox.setLeft(titleLabel);
    titleBox.setRight(numberOfGameLabel);

    String blackType = formatPlayerType(tournament.getTournament().getBlackPlayer().getType());
    String whiteType = formatPlayerType(tournament.getTournament().getWhitePlayer().getType());

    Label blackPlayerLabel = new Label(blackType);
    Label whitePlayerLabel = new Label(whiteType);

    int winner = tournament.getTournament().getWinner();
    if (winner == 0) {
      blackPlayerLabel.setStyle(WINNER_STYLE);
    } else if (winner == 1) {
      whitePlayerLabel.setStyle(WINNER_STYLE);
    }

    HBox playerBox = new HBox(10);
    playerBox.setAlignment(Pos.CENTER);
    playerBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;-fx-padding: 10;");

    final Circle blackPiece = new Circle(8, Color.BLACK);
    final Circle whitePiece = new Circle(8, Color.WHITE);
    whitePiece.setStroke(Color.GRAY);
    whitePiece.setStrokeWidth(1);

    final Label blackPlayerScore = new Label(String.valueOf(
        tournament.getTournament().getBlackWins()));
    final Label whitePlayerScore = new Label(String.valueOf(
        tournament.getTournament().getWhiteWins()));
    final Separator sep = new Separator(Orientation.VERTICAL);

    playerBox.getChildren().addAll(
        blackPlayerScore, blackPiece, blackPlayerLabel, sep, whitePlayerLabel, whitePiece,
        whitePlayerScore);

    VBox labelBox = new VBox(titleBox, playerBox);
    labelBox.setSpacing(2);
    labelBox.setStyle("""
            -fx-padding: 5;
            -fx-border-color: transparent transparent #cccccc transparent;
            -fx-border-width: 0 0 1 0;
        """);

    return labelBox;
  }

  private VBox createGameDetailsItem(SaveGameDTO game) {
    Label titleLabel = new Label(game.getGame().getGameName());
    titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

    String blackType = formatPlayerType(game.getGame().getBlackPlayer().getType());
    String whiteType = formatPlayerType(game.getGame().getWhitePlayer().getType());

    Label blackPlayerLabel = new Label(blackType);
    Label whitePlayerLabel = new Label(whiteType);


    int winner = game.getGame().getWinner();
    if (winner == 0) {
      blackPlayerLabel.setStyle(WINNER_STYLE);
    } else if (winner == 1) {
      whitePlayerLabel.setStyle(WINNER_STYLE);
    }

    HBox playerBox = new HBox(10);
    playerBox.setAlignment(Pos.CENTER);
    playerBox.setStyle("-fx-border-color: #CCCCCC; -fx-border-width: 1;-fx-padding: 10;");

    Circle blackPiece = new Circle(8, Color.BLACK);
    Circle whitePiece = new Circle(8, Color.WHITE);
    whitePiece.setStroke(Color.GRAY);
    whitePiece.setStrokeWidth(1);

    playerBox.getChildren()
        .addAll(blackPiece, blackPlayerLabel, new Separator(Orientation.VERTICAL), whitePlayerLabel,
            whitePiece);

    VBox labelBox = new VBox(titleLabel, playerBox);
    labelBox.setSpacing(2);
    labelBox.setStyle("""
            -fx-padding: 5;
            -fx-border-color: transparent transparent #cccccc transparent;
            -fx-border-width: 0 0 1 0;
        """);

    return labelBox;
  }

  private String formatPlayerType(Constants.PLAYER_TYPE type) {
    return switch (type) {
      case HUMAN -> "Human";
      case RANDOM_AI -> "Random";
      case MCTS -> "MCTS";
      case O_MCTS -> "O-MCTS";
    };
  }

  /**
   * Updates the games list.
   */
  public void updateGamesListView() {
    gamesListView.getItems().clear();
    SaveGameUtils.getSaveGames().forEach(game ->
      gamesListView.getItems().add(game)
    );
  }

  /**
   * Updates the tournament list.
   */
  public void updateTournamentsListView() {
    tournamentsListView.getItems().clear();
    SaveGameUtils.getSaveTournaments().forEach(tournament ->
        tournamentsListView.getItems().add(tournament)
    );
  }

}
