package com.mcgreedy.optionothello.ai.options;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.List;
import java.util.Random;

/**
 * Option to minimize the possible moves for the enemy
 */

public class MinimizeEnemyMobilityOption implements Option {

  private static final String NAME = "MinimizeEnemyMobilityOption";

  private static final Random rand = new Random();

  @Override
  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR playerColor) {
    return true;
  }

  @Override
  public List<Board> initiationSet() {
    return List.of();
  }

  @Override
  public boolean shouldTerminate(Board board, PLAYER_COLOR playerColor) {
    return true;
  }

  @Override
  public Move getBestMove(Board board, List<Move> possibleMoves) {

    boolean isWhite = possibleMoves.getFirst().getColor() == PLAYER_COLOR.WHITE;
    int leastEnemyMovesPossible = Integer.MAX_VALUE;
    Move bestMove = null;

    for(Move move : possibleMoves) {
      Board clone = board.clone();
      clone.updateBoard(move.getPosition(),isWhite);

      int enemyMovesPossible = clone.generateMovesAsList(!isWhite,-1, PLAYER_TYPE.O_MCTS).size();
      if( enemyMovesPossible < leastEnemyMovesPossible){
        bestMove = move;
        leastEnemyMovesPossible = enemyMovesPossible;
      }
    }
    if(bestMove == null){
      return possibleMoves.get(rand.nextInt(possibleMoves.size()));
    }
    return bestMove;
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
