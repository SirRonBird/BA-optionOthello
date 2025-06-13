package com.mcgreedy.optionothello.ai;

import com.mcgreedy.optionothello.engine.Board;
import java.util.List;
import org.graalvm.polyglot.Context;
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

  public String executePolicy(Board board) {
    try( Context context = Context.newBuilder("js").allowAllAccess(true).build() ) {
      context.getBindings("js").putMember("board", board);
      context.eval("js",policy);
      Value policyFunction = context.getBindings("js").getMember("policy");
      Value result = policyFunction.execute(board);
      return result.asString();
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
}
