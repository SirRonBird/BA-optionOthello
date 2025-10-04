package com.mcgreedy.optionothello.ai;

import java.util.List;

public record OMCTSSettings(
    double discountFactor,
    double explorationConstant,
    List<Option> optionList,
    boolean useMast,
    boolean useRave,
    double tau,
    double k
) {


  @Override
  public String toString() {
    return "OMCTSSettings{" +
        "discountFactor=" + discountFactor +
        ", explorationConstant=" + explorationConstant +
        ", optionList=" + optionList +
        ", useMast=" + useMast +
        ", useRave=" + useRave +
        '}';
  }
}
