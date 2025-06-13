package com.mcgreedy.optionothello.ai;

import static org.junit.jupiter.api.Assertions.*;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.ui.OptionsUI;
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

    String testPolicy = "function policy(board) {\n"
        + "    if (board.getWhiteCount() == board.getBlackCount()) {\n"
        + "        return \"Same\";\n"
        + "    } else {\n"
        + "        return \"NotSame\";\n"
        + "    }\n"
        + "}";

    String testTerminationCondition = "function terminationCondition(board) {\n"
        + "    return board.isGameOver();\n"
        + "}";

    Option testOption = new Option(initSet, testPolicy, testTerminationCondition);

    String policyReturn = testOption.executePolicy(testStartBoard);
    assertEquals(policyReturn, "Same");

    boolean terminationReturn = testOption.checkTermination(new Board(-1L, 0L));
    assertEquals(terminationReturn, true);


  }

}