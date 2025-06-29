package com.mcgreedy.optionothello.ai;

import static org.junit.jupiter.api.Assertions.*;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.ui.OptionsUI;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_TYPE;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;

class OptionTest {

  @Test
  void testOptionExecution() {
    Board testStartBoard = new Board();

    List<Board> initSet = new ArrayList<>();
    initSet.add(testStartBoard);

    String testPolicy = "function policy(board, move) {\n"
        + "    if (board.getWhiteCount() == board.getBlackCount()) {\n"
        + "       if(move.getPosition() == 10){ \n"
        + "         return \"1.0\";\n }"
        + "       else {\n"
        + "         return \"0.5\";\n }"
        + "    } else {\n"
        + "        return \"0.0\";\n"
        + "    }\n"
        + "}";

    String testTerminationCondition = "function terminationCondition(board) {\n"
        + "    return board.isGameOver();\n"
        + "}";

    Option testOption = new Option(initSet, testPolicy, testTerminationCondition);
    Move testMove = new Move(PLAYER_COLOR.BLACK,10,-1, PLAYER_TYPE.O_MCTS);
    double policyReturn = testOption.executePolicy(testStartBoard, testMove);
    assertEquals(policyReturn, 1.0);

    boolean terminationReturn = testOption.checkTermination(new Board(-1L, 0L));
    assertEquals(terminationReturn, true);


  }

  @Test
  void testBasicPolicy(){
    Board testStartBoard = new Board();
    List<Board> initSet = new ArrayList<>();
    initSet.add(testStartBoard);
    String testPolicy = "function policy(board, move) { return 1.0;}";
    Option testOption = new Option(initSet, testPolicy, "");

    Move testMove = new Move(PLAYER_COLOR.BLACK,10,-1, PLAYER_TYPE.O_MCTS);
    double policyReturn = testOption.executePolicy(testStartBoard, testMove);
    assertEquals(policyReturn, 1.0);
  }

}