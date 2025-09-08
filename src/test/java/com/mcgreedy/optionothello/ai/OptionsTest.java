package com.mcgreedy.optionothello.ai;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.mcgreedy.optionothello.ai.options.AntiDiagControlOption;
import com.mcgreedy.optionothello.ai.options.BottomLeftCornerXOption;
import com.mcgreedy.optionothello.ai.options.BottomRightCornerXOption;
import com.mcgreedy.optionothello.ai.options.CenterControlOption;
import com.mcgreedy.optionothello.ai.options.CornerOption;
import com.mcgreedy.optionothello.ai.options.DiagonalControlOption;
import com.mcgreedy.optionothello.ai.options.FrontierControlOption;
import com.mcgreedy.optionothello.ai.options.HeatmapOption;
import com.mcgreedy.optionothello.ai.options.MainDiagControlOption;
import com.mcgreedy.optionothello.ai.options.MaxFlipsOption;
import com.mcgreedy.optionothello.ai.options.MinimizeEnemyMobilityOption;
import com.mcgreedy.optionothello.ai.options.MobilityOption;
import com.mcgreedy.optionothello.ai.options.ParityOption;
import com.mcgreedy.optionothello.ai.options.PotentialMobilityOption;
import com.mcgreedy.optionothello.ai.options.PreventOpponentCornerOption;
import com.mcgreedy.optionothello.ai.options.QuietMoveOption;
import com.mcgreedy.optionothello.ai.options.StableDiscsOption;
import com.mcgreedy.optionothello.ai.options.TopLeftCornerXOption;
import com.mcgreedy.optionothello.ai.options.TopRightCornerXOption;
import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import com.oracle.truffle.api.dsl.SpecializationStatistics.AlwaysEnabled;
import com.oracle.truffle.js.nodes.access.LocalVarIncNode.Op;
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

  @Test
  void topLeftCornerXOptionTest(){
    Option topLeftCornerXOption = new TopLeftCornerXOption();

    //Board which not need a second move -> direct corner take
    Board board = new Board(
        403440128L,
        120326193152L
    );

    assertTrue(topLeftCornerXOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));
    List<Move> moves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove = topLeftCornerXOption.getBestMove(board,moves);

    assertEquals(0, calculatedMove.getPosition());

    Board endBoard = new Board(
        268959744L,
        120460673537L
    );

    assertTrue(topLeftCornerXOption.shouldTerminate(endBoard,PLAYER_COLOR.WHITE));

    //board which needs a move before the corner can be taken
    Board secondBoard = new Board(
        17695668175360L,
        17246978048L
    );
    assertTrue(topLeftCornerXOption.isBoardInInitiationSet(secondBoard,PLAYER_COLOR.WHITE));

    List<Move> secondMoves = secondBoard.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove2 = topLeftCornerXOption.getBestMove(secondBoard,secondMoves);
    System.out.println(calculatedMove2);
    List<Move> possibleMoves = List.of(
      new Move(PLAYER_COLOR.WHITE,10,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.WHITE,20,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.WHITE,29,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.WHITE,37,-1, PLAYER_TYPE.O_MCTS)
    );

    assertTrue(possibleMoves.contains(calculatedMove2));



  }

  @Test
  void topRightCornerXOptionTest(){
    Option topRightCornerXOption = new TopRightCornerXOption();
    Board board = new Board(
        34495528960L,
        68990038016L
    );


    assertTrue(topRightCornerXOption.isBoardInInitiationSet(board,PLAYER_COLOR.BLACK));

    List<Move> moves = board.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove = topRightCornerXOption.getBestMove(board,moves);
    assertEquals(7, calculatedMove.getPosition());

    Board endBoard = new Board(
        34766078080L,
        68719489024L
    );

    assertTrue(topRightCornerXOption.shouldTerminate(endBoard,PLAYER_COLOR.BLACK));

    Board newBoard = new Board(
        206829780992L,
        8830723309568L
    );

    assertTrue(topRightCornerXOption.isBoardInInitiationSet(newBoard,PLAYER_COLOR.BLACK));

    List<Move> secondMoves = newBoard.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove2 = topRightCornerXOption.getBestMove(board,secondMoves);

    System.out.println(calculatedMove2);

    List<Move> possibleMoves = List.of(
        new Move(PLAYER_COLOR.BLACK,13,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,20,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,19,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,34,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,51,-1, PLAYER_TYPE.O_MCTS)
    );

    assertTrue(possibleMoves.contains(calculatedMove2));


    Board newBoard3 = new Board(
        389099779311272967L,
        0xf811204c0c360300L
    );

    assertFalse(topRightCornerXOption.isBoardInInitiationSet(newBoard3,PLAYER_COLOR.WHITE));
  }

  @Test
  void bottomLeftCornerXOptionTest(){
    Option bottomLeftCornerXOption = new BottomLeftCornerXOption();

    Board board = new Board(
        26457266978816L,
        3945082214416384L
    );

    assertTrue(bottomLeftCornerXOption.isBoardInInitiationSet(board,PLAYER_COLOR.BLACK));

    List<Move> moves = board.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove = bottomLeftCornerXOption.getBestMove(board,moves);

    assertEquals(56, calculatedMove.getPosition());

    Board endBoard = new Board(
        72651433664577536L,
        3377699854745600L
    );

    assertTrue(bottomLeftCornerXOption.shouldTerminate(endBoard,PLAYER_COLOR.BLACK));

    Board newBoard = new Board(
        578730144303153152L,
        1702148018536448L
    );

    assertTrue(bottomLeftCornerXOption.isBoardInInitiationSet(newBoard,PLAYER_COLOR.BLACK));

    List<Move> newMoves = newBoard.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove2 = bottomLeftCornerXOption.getBestMove(newBoard,newMoves);
    System.out.println(calculatedMove2);

    List<Move> possibleMoves = List.of(
        new Move(PLAYER_COLOR.BLACK,20,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,19,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,26,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,33,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,41,-1, PLAYER_TYPE.O_MCTS)
    );

    assertTrue(possibleMoves.contains(calculatedMove2));

  }

  @Test
  void bottomRightCornerXOptionTest(){
    Option bottomRightCornerXOption = new BottomRightCornerXOption();

    Board board = new Board(
        18049789308436480L,
        26422773022720L
    );

    assertTrue(bottomRightCornerXOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> moves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove = bottomRightCornerXOption.getBestMove(board,moves);
    System.out.println(calculatedMove);

    assertEquals(63, calculatedMove.getPosition());

    Board endBoard = new Board(
        137707388928L,
        -9205295962480705536L
    );

    assertTrue(bottomRightCornerXOption.shouldTerminate(endBoard,PLAYER_COLOR.WHITE));

    Board newBoard = new Board(
        87995558395904L,
        18049651735265280L
    );

    assertTrue(bottomRightCornerXOption.isBoardInInitiationSet(newBoard,PLAYER_COLOR.BLACK));

    List<Move> newMoves = newBoard.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove2 = bottomRightCornerXOption.getBestMove(newBoard,newMoves);
    System.out.println(calculatedMove2);

    List<Move> possibleMoves = List.of(
        new Move(PLAYER_COLOR.BLACK,19,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,26,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.BLACK,37,-1, PLAYER_TYPE.O_MCTS)
    );

    assertTrue(possibleMoves.contains(calculatedMove2));
  }

  @Test
  void stableDiscOptionTest(){
    Option stableDiscsOption = new StableDiscsOption();

    Board board = new Board(
        -9223319156814249984L,
        4629771060558954496L
    );

    assertTrue(stableDiscsOption.isBoardInInitiationSet(board,PLAYER_COLOR.BLACK));

    List<Move> moves = board.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove = stableDiscsOption.getBestMove(board,moves);
    System.out.println(calculatedMove);

    assertEquals(61, calculatedMove.getPosition());


    Board newBoard = new Board(
        -256581550943485888L,
        153968149790848L
    );

    List<Move> newMoves = newBoard.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove2 = stableDiscsOption.getBestMove(newBoard,newMoves);
    System.out.println(calculatedMove2);

    assertEquals(5, calculatedMove2.getPosition());

    Board newBoard2 = new Board(
        -256581894001913856L,
        154861500907744L
    );

    List<Move> newMoves2 = newBoard2.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);

    Move calculatedMove3 = stableDiscsOption.getBestMove(newBoard2,newMoves2);
    System.out.println(calculatedMove3);

    assertEquals(4, calculatedMove3.getPosition());

  }

  @Test
  void quietMoveOptionTest(){
    Board board = new Board(
        34762915840L,
        68719476736L
    );

    Option quietMoveOption = new QuietMoveOption();

    assertTrue(quietMoveOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> moves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move calculatedMove = quietMoveOption.getBestMove(board,moves);
    System.out.println(calculatedMove);

    assertEquals(18, calculatedMove.getPosition());

    Board newBoard = new Board(
        193514550079488L,
        9890780678144L
    );

    List<Move> newMoves = newBoard.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);
    Move calculatedMove2 = quietMoveOption.getBestMove(newBoard,newMoves);
    System.out.println(calculatedMove2);
    assertEquals(46, calculatedMove2.getPosition());
  }

  @Test
  void minimizeEnemyMobilityTest(){
    Board board = new Board(
        34762915840L,
        68719476736L
    );

    Option minimizeEnemyMobilityOption = new MinimizeEnemyMobilityOption();

    assertTrue(minimizeEnemyMobilityOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> possibleMoves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = minimizeEnemyMobilityOption.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    assertEquals(18, generatedMove.getPosition());

  }

  @Test
  void centerControlTest(){
    Board board = new Board(
        17695266308096L,
        35322751090688L
    );

    Option centerControlOption = new CenterControlOption();

    assertTrue(centerControlOption.isBoardInInitiationSet(board,PLAYER_COLOR.BLACK));

    List<Move> possibleMoves = board.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = centerControlOption.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    assertEquals(18, generatedMove.getPosition());

  }

  @Test
  void mainDiagonalControlTest(){
    Board board = new Board(
        35253494743040L,
        51539607552L
    );

    Option mainDiagonalControlOption = new MainDiagControlOption();

    assertTrue(mainDiagonalControlOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> possibleMoves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = mainDiagonalControlOption.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    List<Move> bestMoves = List.of(
        new Move(PLAYER_COLOR.WHITE,11,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.WHITE,20,-1, PLAYER_TYPE.O_MCTS),
        new Move(PLAYER_COLOR.WHITE,37,-1, PLAYER_TYPE.O_MCTS)
    );

    assertTrue(bestMoves.contains(generatedMove));

    Board secondBoard = new Board(
        52845680787456L,
        188978561024L
    );

    assertTrue(mainDiagonalControlOption.isBoardInInitiationSet(secondBoard,PLAYER_COLOR.WHITE));

    List<Move> possibleMoves2 = secondBoard.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove2 = mainDiagonalControlOption.getBestMove(secondBoard,possibleMoves2);
    System.out.println(generatedMove2);

    assertEquals(53, generatedMove2.getPosition());
  }

  @Test
  void antiDiagonalControlTest(){
    Board board = new Board(
        4450057977856L,
        68720525312L
    );

    Option antiDiagonalControlOption = new AntiDiagControlOption();
    assertTrue(antiDiagonalControlOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> possibleMoves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = antiDiagonalControlOption.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    assertEquals(22, generatedMove.getPosition());

    Board secondBoard = new Board(
        0x80818b170f000000L,
        34467663651405824L
    );

    assertTrue(antiDiagonalControlOption.isBoardInInitiationSet(secondBoard,PLAYER_COLOR.BLACK));
    List<Move> possibleMoves2 = secondBoard.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove2 = antiDiagonalControlOption.getBestMove(secondBoard,possibleMoves2);
    System.out.println(possibleMoves2);
    System.out.println(generatedMove2);
  }

  @Test
  void maxFlipsTest(){
    Board board = new Board(
        9060010374463488L,
        86168829952L
    );

    Option maxFlipsOption = new MaxFlipsOption();

    assertTrue(maxFlipsOption.isBoardInInitiationSet(board,PLAYER_COLOR.WHITE));

    List<Move> possibleMoves = board.generateMovesAsList(true,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = maxFlipsOption.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    assertEquals(18, generatedMove.getPosition());
  }

  @Test
  void preventOpponentCornerTest(){
    Board board = new Board(
        34359869954L,
        69123964928L
    );

    Option preventOpponentCorner = new PreventOpponentCornerOption();

    assertTrue(preventOpponentCorner.isBoardInInitiationSet(board,PLAYER_COLOR.BLACK));

    List<Move> possibleMoves = board.generateMovesAsList(false,-1, PLAYER_TYPE.O_MCTS);
    Move generatedMove = preventOpponentCorner.getBestMove(board,possibleMoves);
    System.out.println(generatedMove);

    assertEquals(45, generatedMove.getPosition());
  }
}
