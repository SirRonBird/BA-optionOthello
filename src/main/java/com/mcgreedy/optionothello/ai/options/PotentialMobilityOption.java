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
import java.util.Set;
import java.util.jar.Attributes.Name;

/**
 * PotentialMobilityOption (verbessert)
 * --------------------------------------------------------------------
 * Ziel: potenzielle Mobilität FÜR MICH nach dem Zug maximieren:
 * Anzahl leerer Felder, die an Gegnersteine grenzen (-> nächste Züge).
 * Zusätzlich werden Situationen, die dem Gegner Corner-Moves eröffnen,
 * hart bestraft.
 */
public class PotentialMobilityOption implements Option {

  private static final String NAME = "PotentialMobilityOption";

  private static final Set<Integer> CORNERS = Set.of(0, 7, 56, 63);


  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return b.isGameOver(); }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me  = possibleMoves.getFirst().getColor();
    Constants.PLAYER_COLOR opp = (me == Constants.PLAYER_COLOR.WHITE) ? Constants.PLAYER_COLOR.BLACK : Constants.PLAYER_COLOR.WHITE;

    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);

          // harte Strafe, wenn Gegner danach eine Ecke hat
          boolean oppHasCorner = c.generateMovesAsList(opp == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS)
              .stream().anyMatch(om -> CORNERS.contains(om.getPosition()));
          int penalty = oppHasCorner ? -1000 : 0;

          int myPot = potentialMobility(c, me);
          return penalty + myPot;
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }


  private int potentialMobility(Board b, Constants.PLAYER_COLOR me) {
    long opp = (me == Constants.PLAYER_COLOR.WHITE) ? b.getBlack() : b.getWhite();
    long empty = ~(b.getWhite() | b.getBlack());
    int cnt = 0;
    for (int i = 0; i < 64; i++) {
      if (((empty >>> i) & 1L) == 0) continue;
      int r = i/8, c = i%8;
      boolean nearOpp = false;
      for (int dr = -1; dr <= 1 && !nearOpp; dr++) {
        for (int dc = -1; dc <= 1 && !nearOpp; dc++) {
          if (dr == 0 && dc == 0) continue;
          int nr = r+dr, nc = c+dc;
          if (nr < 0 || nr > 7 || nc < 0 || nc > 7) continue;
          int n = nr*8+nc;
          if (((opp >>> n) & 1L) != 0) nearOpp = true;
        }
      }
      if (nearOpp) cnt++;
    }
    return cnt;
  }

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public String toString(){
    return NAME;
  }
}
