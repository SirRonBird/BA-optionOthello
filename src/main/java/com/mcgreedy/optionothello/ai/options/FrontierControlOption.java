package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * FrontierControlOption
 * --------------------------------------------------------------------
 * Ziel: eigene Frontier-Discs minimieren UND gegnerische maximieren
 * (Differenz). Am stärksten im Midgame gewichtet; Early/Endgame schwächer.
 */
public class FrontierControlOption implements Option {

  private static final String NAME = "FrontierControlOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return b.isGameOver(); }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    int discs = board.getWhiteCount() + board.getBlackCount();
    double phase = (discs < 20) ? 0.6 : (discs < 50 ? 1.4 : 0.7);

    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    Constants.PLAYER_COLOR opp = (me == Constants.PLAYER_COLOR.WHITE) ? Constants.PLAYER_COLOR.BLACK : Constants.PLAYER_COLOR.WHITE;

    return possibleMoves.stream()
        .max(Comparator.comparingDouble(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          int myF  = frontierCount(c, me);
          int opF  = frontierCount(c, opp);
          return phase * (opF - myF);
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }


  private int frontierCount(Board b, Constants.PLAYER_COLOR who) {
    long me = (who == Constants.PLAYER_COLOR.WHITE) ? b.getWhite() : b.getBlack();
    long empty = ~(b.getWhite() | b.getBlack());
    int cnt = 0;
    for (int i = 0; i < 64; i++) {
      if (((me >>> i) & 1L) != 0 && hasEmptyNeighbor(empty, i)) cnt++;
    }
    return cnt;
  }
  private boolean hasEmptyNeighbor(long empty, int i) {
    int r = i/8, c = i%8;
    for (int dr = -1; dr <= 1; dr++) {
      for (int dc = -1; dc <= 1; dc++) {
        if (dr == 0 && dc == 0) continue;
        int nr = r+dr, nc = c+dc;
        if (nr < 0 || nr > 7 || nc < 0 || nc > 7) continue;
        int idx = nr*8+nc;
        if (((empty >>> idx) & 1L) != 0) return true;
      }
    }
    return false;
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
