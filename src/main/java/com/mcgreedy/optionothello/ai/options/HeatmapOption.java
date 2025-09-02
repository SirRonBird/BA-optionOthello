package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * HeatmapOption (phasenabhängig)
 * --------------------------------------------------------------------
 * Ziel: Positionsbewertung über Gewichtungsmatrizen.
 * - Early: klassische Anti-X/C-Map, Ecken stark, Zentrum leicht +
 * - Mid: reduzierte Gewichte, Fokus Richtung Mobilität
 * - End: fast neutral; hier zählt eher Disk-Differenz/Parity
 */
public class HeatmapOption implements Option {

  private static final int[] EARLY = {
      120,-20, 20,  5,  5, 20,-20,120,
      -20,-40, -5, -5, -5, -5,-40,-20,
      20, -5, 15,  3,  3, 15, -5, 20,
      5, -5,  3,  2,  2,  3, -5,  5,
      5, -5,  2,  2,  2,  2, -5,  5,
      20, -5, 15,  3,  3, 15, -5, 20,
      -20,-40, -5, -5, -5, -5,-40,-20,
      120,-20, 20,  5,  5, 20,-20,120
  };

  private static final int[] MID = Arrays.stream(EARLY).map(v -> (int)Math.round(v * 0.6)).toArray();
  private static final int[] END = new int[64]; // nahezu neutral


  private static final String NAME = "HeatmapOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return false; }


  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    int discs = board.getWhiteCount() + board.getBlackCount();
    int[] map = (discs < 20) ? EARLY : (discs < 50 ? MID : END);

    Constants.PLAYER_COLOR me = possibleMoves.get(0).getColor();

    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          return eval(c, me, map);
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int eval(Board b, Constants.PLAYER_COLOR me, int[] map) {
    long bits = (me == Constants.PLAYER_COLOR.WHITE) ? b.getWhite() : b.getBlack();
    int s = 0;
    for (int i = 0; i < 64; i++) if (((bits >>> i) & 1L) != 0) s += map[i];
    return s;
  }
  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString() {
    return NAME;
  }
}
