package com.mcgreedy.optionothello.engine;

import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.ai.Option_js;

public class MoveStatistics {
  private int searchDepth;
  private Option option;
  private int searchedNodes;
  private long searchTime;

  public MoveStatistics() {
    searchDepth = 0;
    searchedNodes = 0;
    searchTime = 0;
    option = null;
  }

  public MoveStatistics(int searchDepth, Option option, int searchedNodes, long searchTime) {
    this.searchDepth = searchDepth;
    this.option = option;
    this.searchedNodes = searchedNodes;
    this.searchTime = searchTime;
  }

  public int getSearchDepth() {
    return searchDepth;
  }

  public void setSearchDepth(int searchDepth) {
    this.searchDepth = searchDepth;
  }

  public Option getOption() {
    return option;
  }

  public void setOption(Option option) {
    this.option = option;
  }

  public int getSearchedNodes() {
    return searchedNodes;
  }

  public void setSearchedNodes(int searchedNodes) {
    this.searchedNodes = searchedNodes;
  }

  public long getSearchTime() {
    return searchTime;
  }

  public void setSearchTime(long searchTime) {
    this.searchTime = searchTime;
  }
}
