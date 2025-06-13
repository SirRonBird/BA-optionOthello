package com.mcgreedy.optionothello.ui;

import com.mcgreedy.optionothello.dtos.SaveTournamentDTO;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class TournamentStatisticsUI {

  public static void showTournamentStatistics(SaveTournamentDTO tournamentDto) {
    AnalyseUi.statisticsPane.getChildren().clear();

    Label title = new Label(
        "Statistik für Turnier: " + tournamentDto.getTournament().getTournamentName());
    title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    int totalGames = tournamentDto.getTournament().getNumberOfGames();
    int blackWins = tournamentDto.getTournament().getBlackWins();
    int whiteWins = tournamentDto.getTournament().getWhiteWins();

    Label total = new Label("Gesamtspiele: " + totalGames);
    Label blackWinLabel = new Label("Siege Schwarz: " + blackWins);
    Label whiteWinLabel = new Label("Siege Weiß: " + whiteWins);

    VBox box = new VBox(10, title, total, blackWinLabel, whiteWinLabel);
    box.setPadding(new Insets(10));



    AnalyseUi.statisticsPane.add(box, 0, 0);
  }

}
