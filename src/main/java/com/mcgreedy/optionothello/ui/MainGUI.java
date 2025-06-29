package com.mcgreedy.optionothello.ui;

import static javafx.application.Platform.exit;

import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * MainGUI is the primary graphical user interface for the application. It manages the interactions
 * between the user and the game, including setting up the game board, handling player settings, and
 * initiating new games or tournaments. This class extends javafx.application.Application and serves
 * as the entry point for launching the GUI.
 */
public class MainGUI extends Application {


  static GameUI gameUI;
  static OptionsUI optionsUI;
  static AnalyseUi analyseUI;

  //GameManager
  private static Gamemanager gameManager;


  static SingleSelectionModel<Tab> selectionModel;

  private static final Logger LOGGER = LogManager.getLogger(MainGUI.class);
  static Preferences preferences = Preferences.userNodeForPackage(MainGUI.class);

  public static void setGameManagerName(Gamemanager manager) {
    gameManager = manager;
  }

  @Override
  public void start(Stage primaryStage) {



    LOGGER.info("Starting MainGUI");
    LOGGER.info("Game manager: {}", gameManager);

    //create gameUI
    gameUI = new GameUI(gameManager);

    //create OptionUI
    optionsUI = new OptionsUI();

    //create AnalyseUI
    analyseUI = new AnalyseUi();

    //create Tab pane
    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    tabPane.setTabDragPolicy(TabPane.TabDragPolicy.FIXED);
    tabPane.setTabMinWidth(100);
    // set tabs
    Tab gameTab = new Tab("Game", gameUI.getGamePane());
    tabPane.getTabs().add(gameTab);
    Tab optionsTab = new Tab("Options", optionsUI.getOptionsPane());
    tabPane.getTabs().add(optionsTab);
    Tab analyseTab = new Tab("Analyse", analyseUI.getAnalysePane());
    tabPane.getTabs().add(analyseTab);
    selectionModel = tabPane.getSelectionModel();

    selectionModel.selectedItemProperty().addListener((observable, oldTab, newTab) -> {
      if (newTab == gameTab) {
        LOGGER.info("Opening game tab");
        SaveGameUtils.loadSaveOptions();
      } else if (newTab == optionsTab) {
        LOGGER.info("Opening options tab");
        SaveGameUtils.loadSaveOptions();
        optionsUI.updateOptionListView();
      } else if (newTab == analyseTab) {
        LOGGER.info("Opening analyse tab");
        SaveGameUtils.loadSaveGames();
        SaveGameUtils.loadSaveTournaments();
        analyseUI.updateGamesListView();
        analyseUI.updateTournamentsListView();
      } else {
        LOGGER.error("Unknown tab selected");
      }
    });

    // Create the scene
    Scene scene = new Scene(tabPane, 1280, 720);
    primaryStage.setTitle("Reversi Game");
    primaryStage.setResizable(false);
    primaryStage.setScene(scene);
    primaryStage.setX(preferences.getDouble("WindowX", 0));
    primaryStage.setY(preferences.getDouble("WindowY", 0));

    primaryStage.show();



    // Set up shutdown hook to clean up executor
    primaryStage.setOnCloseRequest(e -> {
      LOGGER.info("Shutting down application");
      preferences.putDouble("WindowX", primaryStage.getScene().getWindow().getX());
      preferences.putDouble("WindowY", primaryStage.getScene().getWindow().getY());
      exit();
    });


  }

  public static int getLastClickedCell() {
    return gameUI.getLastClickedCell();
  }

  public static void updatedBoardGrid(long black, long white, int movePosition) {
    gameUI.updateBoardGrid(black, white, movePosition);
  }

  public static void showPossibleMoves(long possibleMoves) {
    gameUI.showPossibleMoves(possibleMoves);
  }

  public static void updateGameStandings(int whiteScore, int blackScore) {
    gameUI.updateGameStandings(whiteScore, blackScore);
  }

  public static void gameOver(int winner) {
    gameUI.gameOver(winner);
  }

  public static void tournamentOver(int winner, int gamesPlayed, int blackScore, int whiteScore) {
    gameUI.tournamentOver(winner, gamesPlayed, blackScore, whiteScore);
  }

  public static void setPlayerToMove(Player player) {
    gameUI.setPlayerToMove(player);
  }

  public static void updateScoreBoard(int blackScore, int whiteScore, int gamesPlayed) {
    gameUI.updateScoreBoard(blackScore, whiteScore, gamesPlayed);
  }

  public static void goToOptionsTab(){
    selectionModel.select(1);
  }
}
