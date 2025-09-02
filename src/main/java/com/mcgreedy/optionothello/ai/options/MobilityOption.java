package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * MobilityOption
 * --------------------------------------------------------------------
 * Ziel: relative Mobilität maximieren: (eigene Züge - gegnerische Züge)
 * nach dem Zug. Phasenbewusst: im Midgame stärker gewichtet.
 */
public class MobilityOption implements Option {

  private static final String NAME = "MobilityOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return false; }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    int discs = board.getWhiteCount() + board.getBlackCount();
    double phase = (discs < 20) ? 0.6 : (discs < 50 ? 1.2 : 0.5); // Midgame > Early/End

    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    Constants.PLAYER_COLOR opp = (me == Constants.PLAYER_COLOR.WHITE) ? Constants.PLAYER_COLOR.BLACK : Constants.PLAYER_COLOR.WHITE;

    return possibleMoves.stream()
        .max(Comparator.comparingDouble(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          int myMob  = c.generateMovesAsList(me  == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS).size();
          int opMob  = c.generateMovesAsList(opp == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS).size();
          return phase * (myMob - opMob);
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
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
