package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveDetails;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveStatistics;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GameStatisticsUI {

  private GameStatisticsUI() {
    throw new IllegalStateException("Utility class");
  }

  public static void showGameStatistics(SaveGameDTO saveGame) {
    AnalyseUi.statisticsPane.getChildren().clear();

    VBox header = new VBox(10);
    header.setPadding(new Insets(10));
    header.setStyle("-fx-background-color: #9f9f9f;");

    Label title = new Label("Statistik für Spiel: " + saveGame.getGame().getGameName());
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    header.getChildren().add(title);

    Label moves = new Label("Anzahl Züge: " + saveGame.getGame().getMoves().size());
    Label winner = new Label(
        "Sieger: " + (saveGame.getGame().getWinner() == 0 ? "Schwarz" : "Weiß"));

    HBox gameStats = new HBox(10);
    gameStats.getChildren().addAll(moves, winner);

    header.getChildren().addAll(gameStats);

    AnalyseUi.statisticsPane.add(header,0,0,2,1);

    VBox moveDepthChartBox = new VBox(10);
    moveDepthChartBox.setPadding(new Insets(10));

    LineChart<Number,Number> moveDepthChart = createMoveDepthChart(saveGame);
    moveDepthChartBox.getChildren().add(moveDepthChart);

    AnalyseUi.statisticsPane.add(moveDepthChartBox, 0, 1);

    VBox searchTimeChartBox = new VBox(10);
    searchTimeChartBox.setPadding(new Insets(10));

    LineChart<Number,Number> searchTimeChart = createSearchTimeChart(saveGame);
    searchTimeChartBox.getChildren().add(searchTimeChart);

    AnalyseUi.statisticsPane.add(searchTimeChartBox, 1, 1);

    VBox searchedNodesChartBox = new VBox(10);
    searchedNodesChartBox.setPadding(new Insets(10));

    LineChart<Number,Number> searchedNodesChart = createSearchedNodesChart(saveGame);
    searchedNodesChartBox.getChildren().add(searchedNodesChart);

    AnalyseUi.statisticsPane.add(searchedNodesChartBox, 0, 2);

    VBox playerStatsBox = new VBox(10);
    playerStatsBox.setPadding(new Insets(10));

    TableView<MetricRow> playerStatsTable = createPlayerStatsTableVertical(saveGame);
    playerStatsTable.setEditable(false);
    playerStatsBox.getChildren().add(playerStatsTable);

    AnalyseUi.statisticsPane.add(playerStatsBox, 1, 2);

  }


  private static LineChart<Number,Number> createMoveDepthChart(SaveGameDTO saveGame){
    List<MoveDetails> moves = saveGame.getGame().getMoves();

    NumberAxis xAxis = new NumberAxis(0,moves.size(),1);
    NumberAxis yAxis = new NumberAxis();


    LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Search depth");
    lineChart.setCreateSymbols(false);

    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(saveGame.getGame().getBlackPlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.BLACK){
        blackPlayerData.getData().add(new Data<>(i,statistics.getSearchDepth()));
      }
    }
    lineChart.getData().add(blackPlayerData);

    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(saveGame.getGame().getWhitePlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.WHITE){
        whitePlayerData.getData().add(new Data<>(i,statistics.getSearchDepth()));
      }
    }
    lineChart.getData().add(whitePlayerData);

    return lineChart;
  }

  private static LineChart<Number,Number> createSearchTimeChart(SaveGameDTO saveGame){
    List<MoveDetails> moves = saveGame.getGame().getMoves();

    NumberAxis xAxis = new NumberAxis(0,moves.size(),1);
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Search time");
    lineChart.setCreateSymbols(false);

    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(saveGame.getGame().getBlackPlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.BLACK){
        blackPlayerData.getData().add(new Data<>(i,statistics.getSearchTime()));
      }
    }
    lineChart.getData().add(blackPlayerData);

    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(saveGame.getGame().getWhitePlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.WHITE){
        whitePlayerData.getData().add(new Data<>(i,statistics.getSearchTime()));
      }
    }
    lineChart.getData().add(whitePlayerData);

    return lineChart;
  }

  private static LineChart<Number,Number> createSearchedNodesChart(SaveGameDTO saveGame){
    List<MoveDetails> moves = saveGame.getGame().getMoves();
    NumberAxis xAxis = new NumberAxis(0,moves.size(),1);
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Searched nodes");
    lineChart.setCreateSymbols(false);
    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(saveGame.getGame().getBlackPlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.BLACK){
        blackPlayerData.getData().add(new Data<>(i,statistics.getSearchedNodes()));
      }
    }
    lineChart.getData().add(blackPlayerData);

    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(saveGame.getGame().getWhitePlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      MoveStatistics statistics = move.getMoveStatistics();
      if(move.getColor() == PLAYER_COLOR.WHITE){
        whitePlayerData.getData().add(new Data<>(i,statistics.getSearchedNodes()));
      }
    }
    lineChart.getData().add(whitePlayerData);
    return lineChart;
  }

  public static TableView<MetricRow> createPlayerStatsTableVertical(SaveGameDTO saveGameDTO) {
    TableView<MetricRow> tableView = new TableView<>();

    TableColumn<MetricRow, String> metricColumn = new TableColumn<>("Metrik");
    metricColumn.setCellValueFactory(new PropertyValueFactory<>("metric"));

    TableColumn<MetricRow, String> whiteColumn = new TableColumn<>(saveGameDTO.getGame().getWhitePlayer().getType().toString());
    whiteColumn.setCellValueFactory(new PropertyValueFactory<>("whiteValue"));

    TableColumn<MetricRow, String> blackColumn = new TableColumn<>(saveGameDTO.getGame().getBlackPlayer().getType().toString());
    blackColumn.setCellValueFactory(new PropertyValueFactory<>("blackValue"));

    tableView.getColumns().addAll(metricColumn, whiteColumn, blackColumn);

    PlayerStats blackStats = calculatePlayerStats(Constants.PLAYER_COLOR.BLACK, saveGameDTO);
    PlayerStats whiteStats = calculatePlayerStats(Constants.PLAYER_COLOR.WHITE, saveGameDTO);

    ObservableList<MetricRow> data = FXCollections.observableArrayList(
        new MetricRow("Max Search Depth", whiteStats.maxSearchDepth, blackStats.maxSearchDepth),
        new MetricRow("Max Searched Nodes", whiteStats.maxSearchedNodes, blackStats.maxSearchedNodes),
        new MetricRow("Max Search Time (ms)", whiteStats.maxSearchTime, blackStats.maxSearchTime),
        new MetricRow("Avg. Search Depth",whiteStats.getAverageSearchDepth(), blackStats.getAverageSearchDepth()),
        new MetricRow("Avg. Searched Nodes (ms)", whiteStats.getAverageSearchedNodes(), blackStats.getAverageSearchedNodes())
    );

    tableView.setItems(data);

    return tableView;
  }

  private static class PlayerStats{
    public int maxSearchDepth = 0;
    public long maxSearchTime = 0;
    public int maxSearchedNodes = 0;

    public int totalSearchDepth = 0;
    public int totalSearchedNodes = 0;
    public int moveCount = 0;

    public double getAverageSearchDepth() {
      if (moveCount == 0) return 0;
      double avg = (double) totalSearchDepth / moveCount;
      return Math.round(avg * 10.0) / 10.0;
    }

    public double getAverageSearchedNodes() {
      if (moveCount == 0) return 0;
      double avg = (double) totalSearchedNodes / moveCount;
      return Math.round(avg * 10.0) / 10.0;
    }
  }

  private static PlayerStats calculatePlayerStats(PLAYER_COLOR color,SaveGameDTO saveGame){
    PlayerStats playerStats = new PlayerStats();

    for(MoveDetails move: saveGame.getGame().getMoves()) {
      MoveStatistics moveStatistics = move.getMoveStatistics();
      if(playerStats == null) continue;

      if(move.getColor() == color) {
        playerStats.maxSearchDepth = Math.max(playerStats.maxSearchDepth, moveStatistics.getSearchDepth());
        playerStats.maxSearchedNodes = Math.max(playerStats.maxSearchedNodes, moveStatistics.getSearchedNodes());
        playerStats.maxSearchTime = Math.max(playerStats.maxSearchTime, moveStatistics.getSearchTime());

        playerStats.totalSearchDepth += moveStatistics.getSearchDepth();
        playerStats.totalSearchedNodes += moveStatistics.getSearchedNodes();
        playerStats.moveCount++;
      }
    }
    return playerStats;
  }

  public static class MetricRow {
    private final String metric;
    private final String whiteValue;
    private final String blackValue;

    public MetricRow(String metric, Object whiteValue, Object blackValue) {
      this.metric = metric;
      this.whiteValue = whiteValue != null ? whiteValue.toString() : "-";
      this.blackValue = blackValue != null ? blackValue.toString() : "-";
    }

    public String getMetric() {
      return metric;
    }

    public String getWhiteValue() {
      return whiteValue;
    }

    public String getBlackValue() {
      return blackValue;
    }
  }
}
