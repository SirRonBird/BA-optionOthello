package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * CornerOption
 * --------------------------------------------------------------------
 * Ziel: Jede verfügbare Ecke (0,7,56,63) sofort nehmen – Ecken sind
 * in Othello immer stabil. Bei mehreren Ecken wird der Zug gewählt,
 * der danach die gegnerische Mobilität minimiert und (einfach geschätzt)
 * stabile Kantensteine maximiert.
 */

public class CornerOption implements Option {

  private static final Set<Integer> corners = Set.of(0,7,56,63);

  private final String name = "CornerOption";

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return board.generateMovesAsList(playerColor == Constants.PLAYER_COLOR.WHITE,
            0, Constants.PLAYER_TYPE.O_MCTS)
        .stream()
        .anyMatch(m -> corners.contains(m.getPosition()));
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return board.generateMovesAsList(playerColor == PLAYER_COLOR.WHITE, 0,
        PLAYER_TYPE.O_MCTS).stream()
        .noneMatch(move -> corners.contains(move.getPosition()));
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {
    List<Move> cornerMoves  = possibleMoves.stream()
        .filter(move -> corners.contains(move.getPosition()))
        .toList();
    if(cornerMoves.isEmpty()) return possibleMoves.get(new Random().nextInt(possibleMoves.size()));

    PLAYER_COLOR me = cornerMoves.getFirst().getColor();
    PLAYER_COLOR opp = (me == PLAYER_COLOR.WHITE) ? PLAYER_COLOR.BLACK : PLAYER_COLOR.WHITE;

    return cornerMoves.stream()
        .max(Comparator.comparingInt(move -> {
          Board c = board.clone();
          c.updateBoard(move.getPosition(), me == PLAYER_COLOR.WHITE);
          int oppMob = c.generateMovesAsList(opp == PLAYER_COLOR.WHITE, 0 ,
              PLAYER_TYPE.O_MCTS).size();
          int stab = estimateStableEdgeCount(c,me);
          return 1000 + stab -2 * oppMob;// 1000 damit Ecken immer > andere Züge
        }))
        .orElse(possibleMoves.get(new Random().nextInt(possibleMoves.size())));
  }

  private int estimateStableEdgeCount(Board b, Constants.PLAYER_COLOR me) {
    long bits = (me == Constants.PLAYER_COLOR.WHITE) ? b.getWhite() : b.getBlack();
    // einfache Schätzung: für jede Ecke, wenn besetzt, zusammenhängende gleiche Farbe entlang der angrenzenden Kanten zählen
    int count = 0;
    if (((bits) & 1L) != 0) { count += runAlongEdge(bits, 0, +1); count += runAlongEdge(bits, 0, +8); }
    if (((bits >>> 7) & 1L) != 0) { count += runAlongEdge(bits, 7, -1); count += runAlongEdge(bits, 7, +8); }
    if (((bits >>> 56) & 1L) != 0) { count += runAlongEdge(bits, 56, +1); count += runAlongEdge(bits, 56, -8); }
    if (((bits >>> 63) & 1L) != 0) { count += runAlongEdge(bits, 63, -1); count += runAlongEdge(bits, 63, -8); }
    return count;
  }
  private int runAlongEdge(long bits, int start, int step) {
    int cnt = 0;
    int i = start;
    while (i >= 0 && i < 64) {
      if (((bits >>> i) & 1L) == 0) break;
      cnt++;
      int r = i / 8;
      int nr = (i + step) / 8;
      if ((step == 1 || step == -1) && nr != r) break;
      i += step;
    }
    return cnt;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String toString() { return getName(); }
}
