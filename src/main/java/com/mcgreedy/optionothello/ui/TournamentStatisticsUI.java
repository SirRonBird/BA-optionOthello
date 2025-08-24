package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveTournamentDTO;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO.GameDetails;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO.MoveDetails;
import com.mcgreedy.optionothello.dtos.SaveTournamentDTO.MoveStatistics;
import com.mcgreedy.optionothello.ui.GameStatisticsUI.MetricRow;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

public class TournamentStatisticsUI {


  public static void showTournamentStatistics(SaveTournamentDTO tournamentDto) {
    AnalyseUi.statisticsPane.getChildren().clear();

    VBox header = new VBox(10);
    header.setPadding(new Insets(10));
    header.setStyle("-fx-background-color: #9f9f9f;");

    Label title = new Label(
        "Statistik für Turnier: " + tournamentDto.getTournament().getTournamentName());
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    header.getChildren().add(title);

    int totalGames = tournamentDto.getTournament().getNumberOfGames();
    int blackWins = tournamentDto.getTournament().getBlackWins();
    int whiteWins = tournamentDto.getTournament().getWhiteWins();

    Label total = new Label("Gesamtspiele: " + totalGames);
    Label blackWinLabel = new Label("Siege Schwarz: " + blackWins);
    Label whiteWinLabel = new Label("Siege Weiß: " + whiteWins);

    HBox tournamentStats = new HBox(10);
    tournamentStats.getChildren().addAll(total, blackWinLabel, whiteWinLabel);

    header.getChildren().add(tournamentStats);

    AnalyseUi.statisticsPane.add(header, 0,0,2,1);

    VBox averageMoveDepthBox = new VBox(10);
    averageMoveDepthBox.setPadding(new Insets(10));

    LineChart<Number,Number> searchDepthsChart = createAverageMoveDepthChart(tournamentDto);
    averageMoveDepthBox.getChildren().add(searchDepthsChart);

    AnalyseUi.statisticsPane.add(averageMoveDepthBox, 0,1);

    VBox averageSearchTimeBox = new VBox(10);
    averageSearchTimeBox.setPadding(new Insets(10));

    LineChart<Number,Number> searchTimesChart = createAverageSearchTimeChart(tournamentDto);
    averageSearchTimeBox.getChildren().add(searchTimesChart);

    AnalyseUi.statisticsPane.add(averageSearchTimeBox, 1,1);

    VBox averageSearchedNodesBox = new VBox(10);
    averageSearchedNodesBox.setPadding(new Insets(10));

    LineChart<Number,Number> searchedNodesChart = createAverageSearchedNodesChart(tournamentDto);

    AnalyseUi.statisticsPane.add(searchedNodesChart, 0,2);

    VBox playerStatsTableBox = new VBox(10);
    playerStatsTableBox.setPadding(new Insets(10));

    TableView<MetricRow> playerStatsTable = createPlayerStatsTableVertical(tournamentDto);
    playerStatsTable.setEditable(false);

    playerStatsTableBox.getChildren().add(playerStatsTable);

    AnalyseUi.statisticsPane.add(playerStatsTableBox, 1,2);

  }

  private static LineChart<Number, Number> createAverageMoveDepthChart(SaveTournamentDTO tournamentDto) {
    List<GameDetails> games = tournamentDto.getTournament().getGames();

    int maxMoveNumber = getMaxMoveNumber(games);

    NumberAxis xAxis = new NumberAxis(0, maxMoveNumber, 1);
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Avg. search depth");
    lineChart.setCreateSymbols(false);

    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(tournamentDto.getTournament().getBlackPlayer().getType().toString());

    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(tournamentDto.getTournament().getWhitePlayer().getType().toString());

    Map<Integer,List<Integer>> blackDepths = new HashMap<>();
    Map<Integer,List<Integer>> whiteDepths = new HashMap<>();

    for(GameDetails game: games) {
      List<MoveDetails> moves = game.getMoves();

      for(int i = 0; i < moves.size(); i++) {
        MoveDetails move = moves.get(i);

        int searchDepth = move.getMoveStatistics() != null ? move.getMoveStatistics().getSearchDepth()
            : move.getSearchDepth();

        if(move.getColor() == PLAYER_COLOR.BLACK){
          blackDepths.computeIfAbsent(i, k-> new ArrayList<>()).add(searchDepth);
        } else if (move.getColor() == PLAYER_COLOR.WHITE) {
          whiteDepths.computeIfAbsent(i, k-> new ArrayList<>()).add(searchDepth);
        }
      }
    }

    for (int i = 0; i <= Math.max(
        blackDepths.keySet().stream().max(Integer::compare).orElse(0),
        whiteDepths.keySet().stream().max(Integer::compare).orElse(0)
    ); i++) {
      if(blackDepths.containsKey(i)) {
        List<Integer> depths = blackDepths.get(i);
        double avg = depths.stream().mapToInt(Integer::intValue).average().orElse(0);
        blackPlayerData.getData().add(new Data<>(i,avg));
      }

      if(whiteDepths.containsKey(i)) {
        List<Integer> depths = whiteDepths.get(i);
        double avg = depths.stream().mapToInt(Integer::intValue).average().orElse(0);
        whitePlayerData.getData().add(new Data<>(i,avg));
      }
    }

    lineChart.getData().addAll(blackPlayerData, whitePlayerData);

    return lineChart;
  }

  private static LineChart<Number,Number> createAverageSearchTimeChart(SaveTournamentDTO tournamentDto) {
    List<GameDetails> games = tournamentDto.getTournament().getGames();
    int maxMoveNumber = getMaxMoveNumber(games);
    NumberAxis xAxis = new NumberAxis(0, maxMoveNumber, 1);
    NumberAxis yAxis = new NumberAxis();

    LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Avg. search time");
    lineChart.setCreateSymbols(false);

    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(tournamentDto.getTournament().getBlackPlayer().getType().toString());
    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(tournamentDto.getTournament().getWhitePlayer().getType().toString());
    Map<Integer,List<Long>> blackSearchTimes = new HashMap<>();
    Map<Integer,List<Long>> whiteSearchTimes = new HashMap<>();

    for(GameDetails game: games) {
      List<MoveDetails> moves = game.getMoves();
      for(int i = 0; i < moves.size(); i++) {
        MoveDetails move = moves.get(i);
        long searchTime = move.getMoveStatistics().getSearchTime();
        if(move.getColor() == PLAYER_COLOR.BLACK){
          blackSearchTimes.computeIfAbsent(i, k-> new ArrayList<>()).add(searchTime);
        }
        if(move.getColor() == PLAYER_COLOR.WHITE){
          whiteSearchTimes.computeIfAbsent(i, k-> new ArrayList<>()).add(searchTime);
        }
      }
    }
    for (int i = 0; i <= Math.max(
        blackSearchTimes.keySet().stream().max(Integer::compare).orElse(0),
        whiteSearchTimes.keySet().stream().max(Integer::compare).orElse(0)
    ); i++){
      if(blackSearchTimes.containsKey(i)) {
        List<Long> searchTimes = blackSearchTimes.get(i);
        double avg = searchTimes.stream().mapToInt(Long::intValue).average().orElse(0);
        blackPlayerData.getData().add(new Data<>(i,avg));
      }
      if(whiteSearchTimes.containsKey(i)) {
        List<Long> searchTimes = whiteSearchTimes.get(i);
        double avg = searchTimes.stream().mapToInt(Long::intValue).average().orElse(0);
        whitePlayerData.getData().add(new Data<>(i,avg));
      }
    }

    lineChart.getData().addAll(blackPlayerData, whitePlayerData);
    return lineChart;
  }

  private static LineChart<Number,Number> createAverageSearchedNodesChart(SaveTournamentDTO tournamentDto) {
    List<GameDetails> games = tournamentDto.getTournament().getGames();
    int maxMoveNumber = getMaxMoveNumber(games);
    NumberAxis xAxis = new NumberAxis(0, maxMoveNumber, 1);
    NumberAxis yAxis = new NumberAxis();
    LineChart<Number,Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Avg. searched nodes");
    lineChart.setCreateSymbols(false);
    Series<Number,Number> blackPlayerData = new Series<>();
    blackPlayerData.setName(tournamentDto.getTournament().getBlackPlayer().getType().toString());
    Series<Number,Number> whitePlayerData = new Series<>();
    whitePlayerData.setName(tournamentDto.getTournament().getWhitePlayer().getType().toString());
    Map<Integer,List<Integer>> blackSearchedNodes = new HashMap<>();
    Map<Integer,List<Integer>> whiteSearchedNodes = new HashMap<>();

    for(GameDetails game: games) {
      List<MoveDetails> moves = game.getMoves();
      for(int i = 0; i < moves.size(); i++) {
        MoveDetails move = moves.get(i);
        int searchedNodes = move.getMoveStatistics().getSearchedNodes();
        if(move.getColor() == PLAYER_COLOR.BLACK){
          blackSearchedNodes.computeIfAbsent(i,k->new ArrayList<>()).add(searchedNodes);
        }
        if(move.getColor() == PLAYER_COLOR.WHITE){
          whiteSearchedNodes.computeIfAbsent(i,k-> new ArrayList<>()).add(searchedNodes);
        }
      }
    }

    for (int i = 0; i <= Math.max(
        blackSearchedNodes.keySet().stream().max(Integer::compare).orElse(0),
        whiteSearchedNodes.keySet().stream().max(Integer::compare).orElse(0)
    );i++){
      if(blackSearchedNodes.containsKey(i)) {
        List<Integer> searchedNodes = blackSearchedNodes.get(i);
        double avg = searchedNodes.stream().mapToInt(Integer::intValue).average().orElse(0);
        blackPlayerData.getData().add(new Data<>(i,avg));
      }
      if(whiteSearchedNodes.containsKey(i)) {
        List<Integer> searchedNodes = whiteSearchedNodes.get(i);
        double avg = searchedNodes.stream().mapToInt(Integer::intValue).average().orElse(0);
        whitePlayerData.getData().add(new Data<>(i,avg));
      }
    }
    lineChart.getData().addAll(blackPlayerData, whitePlayerData);
    return lineChart;
  }

  public static TableView<MetricRow> createPlayerStatsTableVertical(SaveTournamentDTO tournamentDto) {
    TableView<MetricRow> tableView = new TableView<>();

    TableColumn<MetricRow, String> metricColumn = new TableColumn<>("Metrik");
    metricColumn.setCellValueFactory(new PropertyValueFactory<>("metric"));

    TableColumn<MetricRow, String> whiteColumn = new TableColumn<>(tournamentDto.getTournament().getWhitePlayer().getType().toString());
    whiteColumn.setCellValueFactory(new PropertyValueFactory<>("whiteValue"));

    TableColumn<MetricRow, String> blackColumn = new TableColumn<>(tournamentDto.getTournament().getBlackPlayer().getType().toString());
    blackColumn.setCellValueFactory(new PropertyValueFactory<>("blackValue"));

    tableView.getColumns().addAll(metricColumn, whiteColumn, blackColumn);

    PlayerStats blackStats = calculatePlayerStats(Constants.PLAYER_COLOR.BLACK, tournamentDto);
    PlayerStats whiteStats = calculatePlayerStats(Constants.PLAYER_COLOR.WHITE, tournamentDto);

    ObservableList<MetricRow> data = FXCollections.observableArrayList(
        new MetricRow("Max Search Depth", whiteStats.maxSearchDepth, blackStats.maxSearchDepth),
        new MetricRow("Max Searched Nodes", whiteStats.maxSearchedNodes, blackStats.maxSearchedNodes),
        new MetricRow("Max Search Time (ms)", whiteStats.maxSearchTime, blackStats.maxSearchTime),
        new MetricRow("Avg. Search Depth",whiteStats.getAverageSearchDepth(), blackStats.getAverageSearchDepth()),
        new MetricRow("Avg. Searched Nodes", whiteStats.getAverageSearchedNodes(), blackStats.getAverageSearchedNodes())
    );

    tableView.setItems(data);

    return tableView;
  }

  private static int getMaxMoveNumber(List<GameDetails> games) {
    int maxMoveNumber = 0;
    for (GameDetails game : games) {
      int moveNumber = game.getMoves().size();
      if (moveNumber > maxMoveNumber) {
        maxMoveNumber = moveNumber;
      }
    }
    return maxMoveNumber;
  }

  private static class PlayerStats{
    public int maxSearchDepth = 0;
    public int maxSearchedNodes = 0;
    public long maxSearchTime = 0;

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

  private static PlayerStats calculatePlayerStats(PLAYER_COLOR color, SaveTournamentDTO tournamentDto) {
    PlayerStats stats = new PlayerStats();

    for(GameDetails game: tournamentDto.getTournament().getGames()) {
      for(MoveDetails move: game.getMoves()) {
        MoveStatistics moveStatistics = move.getMoveStatistics();
        if(stats == null) continue;

        if(move.getColor() == color) {
          stats.maxSearchDepth = Math.max(stats.maxSearchDepth, moveStatistics.getSearchDepth());
          stats.maxSearchedNodes = Math.max(stats.maxSearchedNodes, moveStatistics.getSearchedNodes());
          stats.maxSearchTime = Math.max(stats.maxSearchTime, moveStatistics.getSearchTime());

          stats.totalSearchDepth += moveStatistics.getSearchDepth();
          stats.totalSearchedNodes += moveStatistics.getSearchedNodes();
          stats.moveCount++;
        }
      }
    }

    return stats;
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
