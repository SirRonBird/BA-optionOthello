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

/**
 * PreventOpponentCornerOption (verbessert)
 * --------------------------------------------------------------------
 * Ziel: Züge vermeiden/abfangen, die dem Gegner im NÄCHSTEN Zug
 * eine Ecke ermöglichen (Corner-Setups via C-/X-Squares).
 * Bewertung: simuliere jeden eigenen Zug und prüfe, ob der Gegner danach
 * eine Ecke hat. Tie-Break über Gegner-Mobilität.
 */
public class PreventOpponentCornerOption implements Option {

  private static final Set<Integer> CORNERS = Set.of(0, 7, 56, 63);

  private static final String NAME = "PreventOpponentCornerOption";

  @Override public boolean isBoardInInitiationSet(Board b, Constants.PLAYER_COLOR c) { return true; }
  @Override public List<Board> initiationSet() { return Collections.emptyList(); }
  @Override public boolean shouldTerminate(Board b, Constants.PLAYER_COLOR c) { return false; }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    Constants.PLAYER_COLOR me = possibleMoves.getFirst().getColor();
    Constants.PLAYER_COLOR opp = (me == Constants.PLAYER_COLOR.WHITE) ? Constants.PLAYER_COLOR.BLACK : Constants.PLAYER_COLOR.WHITE;

    return possibleMoves.stream()
        .max(Comparator.comparingInt(m -> {
          Board c = board.clone();
          c.updateBoard(m.getPosition(), me == Constants.PLAYER_COLOR.WHITE);
          // hat Gegner nach meinem Zug eine Ecke?
          boolean oppHasCorner = c.generateMovesAsList(opp == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS)
              .stream().anyMatch(om -> CORNERS.contains(om.getPosition()));
          int oppMob = c.generateMovesAsList(opp == Constants.PLAYER_COLOR.WHITE, 0, Constants.PLAYER_TYPE.O_MCTS).size();
          return (oppHasCorner ? -1000 : 0) - oppMob; // blocke Corner-Möglichkeit stark
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  @Override public String getName() { return NAME; }

  @Override public String toString() { return NAME; }
}
