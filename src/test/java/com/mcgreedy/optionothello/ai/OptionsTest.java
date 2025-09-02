package com.mcgreedy.optionothello.ai;


import com.mcgreedy.optionothello.ai.options.CenterControlOption;
import com.mcgreedy.optionothello.ai.options.CornerOption;
import com.mcgreedy.optionothello.ai.options.DiagonalControlOption;
import com.mcgreedy.optionothello.ai.options.FrontierControlOption;
import com.mcgreedy.optionothello.ai.options.HeatmapOption;
import com.mcgreedy.optionothello.ai.options.MaxFlipsOption;
import com.mcgreedy.optionothello.ai.options.MobilityOption;
import com.mcgreedy.optionothello.ai.options.ParityOption;
import com.mcgreedy.optionothello.ai.options.PotentialMobilityOption;
import com.mcgreedy.optionothello.ai.options.PreventOpponentCornerOption;
import com.mcgreedy.optionothello.ai.options.StableDiscsOption;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OptionsTest {

  Option cornerOption = new CornerOption();
  Option centerControlOption = new CenterControlOption();
  Option diagonalControlOption = new DiagonalControlOption();
  Option frontierControlOption = new FrontierControlOption();
  Option heatMapOption = new HeatmapOption();
  Option maxFlipsOption = new MaxFlipsOption();
  Option mobilityOption = new MobilityOption();
  Option parityOption = new ParityOption();
  Option potentialMobilityOption = new PotentialMobilityOption();
  Option preventOpponentCornerOption = new PreventOpponentCornerOption();
  Option stableDiscsOption = new StableDiscsOption();



  @Test
  void testBestMoves(){
    Board board = new Board(
        421591296998472L,
        69264736256L
    );

    List<Move> moves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move cornerOptionMove = cornerOption.getBestMove(board,moves);
    Move centerControlOptionMove = centerControlOption.getBestMove(board,moves);
    Move diagonalControlOptionMove = diagonalControlOption.getBestMove(board,moves);
    Move frontierControlOptionMove = frontierControlOption.getBestMove(board,moves);
    Move heatMapOptionMove = heatMapOption.getBestMove(board,moves);
    Move maxFlipsOptionMove = maxFlipsOption.getBestMove(board,moves);
    Move mobilityOptionMove = mobilityOption.getBestMove(board,moves);
    Move parityOptionMove = parityOption.getBestMove(board,moves);
    Move potentialMobilityOptionMove = potentialMobilityOption.getBestMove(board,moves);
    Move preventOpponentCornerOptionMove = preventOpponentCornerOption.getBestMove(board,moves);
    Move stabelDiscsOptionMove = stableDiscsOption.getBestMove(board,moves);

    List<Move> optionsMoves = List.of(
        cornerOptionMove,
        centerControlOptionMove,
        diagonalControlOptionMove,
        frontierControlOptionMove,
        heatMapOptionMove,
        maxFlipsOptionMove,
        mobilityOptionMove,
        parityOptionMove,
        potentialMobilityOptionMove,
        preventOpponentCornerOptionMove,
        stabelDiscsOptionMove
    );


    Move bestMove = new Move(PLAYER_COLOR.WHITE, 4, -1, PLAYER_TYPE.O_MCTS);

    for (int i = 0; i < optionsMoves.size(); i++) {
      if(optionsMoves.get(i).getPosition() == bestMove.getPosition()){
        System.out.println(i);
      }
    }
  }

}
