package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.gamemanagement.Player;
import com.mcgreedy.optionothello.gamemanagement.Gamemanager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * MainGUI is the primary graphical user interface for the application. It manages the interactions
 * between the user and the game, including setting up the game board, handling player settings,
 * and initiating new games or tournaments.
 * This class extends javafx.application.Application and serves as the entry point for launching the GUI.
 */
public class MainGUI extends Application {


    static GameUI gameUI;

    //GameManager
    private static Gamemanager gameManager;

    SingleSelectionModel<Tab> selectionModel;

    private static final Logger LOGGER = LogManager.getLogger(MainGUI.class);

    public static void setGameManagerName(Gamemanager manager) {
        gameManager = manager;
    }

    @Override
    public void start(Stage primaryStage){

        LOGGER.info("Starting MainGUI");
        LOGGER.info("Game manager: {}", gameManager);

        //create gameUI
        gameUI = new GameUI(gameManager);

        //create Tab pane
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);
        tabPane.setTabMinWidth(100);
        // set tabs
        Tab gameTab = new Tab("Game", gameUI.getGamePane());
        tabPane.getTabs().add(gameTab);
        Tab optionsTab = new Tab("Options");
        tabPane.getTabs().add(optionsTab);
        Tab analyseTab = new Tab("Analyse");
        tabPane.getTabs().add(analyseTab);
        selectionModel = tabPane.getSelectionModel();

        // Create the scene
        Scene scene = new Scene(tabPane, 1280, 720);
        primaryStage.setTitle("Reversi Game");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Set up shutdown hook to clean up executor
        primaryStage.setOnCloseRequest(e -> {
            System.out.println("It was nice to play with you!!");
        });

    }

    public static int getLastClickedCell(){
        return gameUI.getLastClickedCell();
    }

    public static void updatedBoardGrid(long black, long white) {
        gameUI.updateBoardGrid(black, white);
    }

    public static void showPossibleMoves(long possibleMoves) {
        gameUI.showPossibleMoves(possibleMoves);
    }

    public static void updateGameStandings(int whiteScore, int blackScore){
        gameUI.updateGameStandings(whiteScore, blackScore);
    }

    public static void gameOver(int winner){
        gameUI.gameOver(winner);
    }

    public static void tournamentOver(int winner, int gamesPlayed, int blackScore, int whiteScore){
        gameUI.tournamentOver(winner, gamesPlayed, blackScore, whiteScore);
    }

    public static void setPlayerToMove(Player player) {
        gameUI.setPlayerToMove(player);
    }
}
