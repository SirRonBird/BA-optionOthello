package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import com.mcgreedy.optionothello.engine.Move;
import com.mcgreedy.optionothello.utils.Constants.PLAYER_COLOR;
import java.io.IOException;
import java.util.List;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

public class Option {

  List<Board> initiationSet; // T

  String policy; // pi

  String terminationCondition; // beta

  public Option(List<Board> initiationSet, String policy, String terminationCondition) {
    this.initiationSet = initiationSet;
    this.policy = policy;
    this.terminationCondition = terminationCondition;
  }

  public boolean isBoardInInitiationSet(Board board, PLAYER_COLOR color) {
    boolean result = false;
    for (Board m : initiationSet) {
      result |= board.boardIsHittingMask(m, color);
    }
    return result;
  }

  public double executePolicy(Board board, Move move) {
    try( Context context = Context.newBuilder("js").allowAllAccess(true).build() ) {
      context.getBindings("js").putMember("board", board);
      context.getBindings("js").putMember("move", move);
      context.eval("js",policy);
      Value policyFunction = context.getBindings("js").getMember("policy");
      Value result = policyFunction.execute(board, move);
      //return Double.parseDouble(result.asString());
      System.out.println(result);
      if(result.fitsInDouble()){
        return result.asDouble();
      } else if (result.fitsInInt()) {
        return result.asInt();
      } else {
        return Double.parseDouble(result.asString());
      }
    }
  }

  public boolean checkTermination(Board board) {
    try (Context context = Context.newBuilder("js").allowAllAccess(true).build()) {
      context.getBindings("js").putMember("board", board);
      context.eval("js", terminationCondition);
      Value terminationFunc = context.getBindings("js").getMember("terminationCondition");
      Value result = terminationFunc.execute(board);
      return result.asBoolean();
    }
  }

  public List<Board> getInitiationSet() {
    return initiationSet;
  }

  public String getPolicy() {
    return policy;
  }

  public String getTerminationCondition() {
    return terminationCondition;
  }
}
