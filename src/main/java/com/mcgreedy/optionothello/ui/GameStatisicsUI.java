package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveGameDTO;
import com.mcgreedy.optionothello.dtos.SaveGameDTO.MoveDetails;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class GameStatisicsUI {

  public static void showGameStatistics(SaveGameDTO saveGame) {
    AnalyseUi.statisticsPane.getChildren().clear();

    Label title = new Label("Statistik für Spiel: " + saveGame.getGame().getGameName());
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    Label moves = new Label("Anzahl Züge: " + saveGame.getGame().getMoves().size());
    Label winner = new Label(
        "Sieger: " + (saveGame.getGame().getWinner() == 0 ? "Schwarz" : "Weiß"));

    VBox box = new VBox(10, title, moves, winner);
    box.setPadding(new Insets(10));

    LineChart<Number,Number> moveDepthChart = createMoveDepthChart(saveGame);
    box.getChildren().add(moveDepthChart);

    AnalyseUi.statisticsPane.add(box, 0, 0);
  }


  private static LineChart<Number,Number> createMoveDepthChart(SaveGameDTO saveGame){
    List<MoveDetails> moves = saveGame.getGame().getMoves();

    NumberAxis xAxis = new NumberAxis(0,moves.size(),1);
    NumberAxis yAxis = new NumberAxis();


    LineChart<Number, Number> lineChart = new LineChart<>(xAxis,yAxis);
    lineChart.setTitle("Move depth");
    lineChart.setCreateSymbols(false);

    XYChart.Series blackPlayerData = new XYChart.Series();
    blackPlayerData.setName(saveGame.getGame().getBlackPlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      if(move.getColor() == PLAYER_COLOR.BLACK){
        blackPlayerData.getData().add(new XYChart.Data(i,move.getSearchDepth()));
      }
    }
    lineChart.getData().add(blackPlayerData);

    XYChart.Series whitePlayerData = new XYChart.Series();
    whitePlayerData.setName(saveGame.getGame().getWhitePlayer().getType().toString());
    for(int i = 0; i < moves.size(); i++){
      MoveDetails move = moves.get(i);
      if(move.getColor() == PLAYER_COLOR.WHITE){
        whitePlayerData.getData().add(new XYChart.Data(i,move.getSearchDepth()));
      }
    }
    lineChart.getData().add(whitePlayerData);

    return lineChart;
  }

}
