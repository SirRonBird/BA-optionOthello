package com.mcgreedy.optionothello.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class OptionDTO {

  @JsonProperty("name")
  private String name;

  @JsonProperty("initiationSet")
  private List<BoardMaskDTO> initiationSet;

  @JsonProperty("policy")
  private String policy;

  @JsonProperty("terminationCondition")
  private String terminationCondition;

  public OptionDTO() {}

  public OptionDTO(String name,List<BoardMaskDTO> initiationSet, String policy, String terminationCondition) {
    this.name = name;
    this.initiationSet = initiationSet;
    this.policy = policy;
    this.terminationCondition = terminationCondition;
  }


  public List<BoardMaskDTO> getInitiationSet() {
    return initiationSet;
  }

  public void setInitiationSet(List<BoardMaskDTO> initiationSet) {
    this.initiationSet = initiationSet;
  }

  public String getPolicy() {
    return policy;
  }

  public void setPolicy(String policy) {
    this.policy = policy;
  }

  public String getTerminationCondition() {
    return terminationCondition;
  }

  public void setTerminationCondition(String terminationCondition) {
    this.terminationCondition = terminationCondition;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public static class BoardMaskDTO {
    public long mask;
    public String name;

    public BoardMaskDTO() {}

    public BoardMaskDTO(long mask, String name) {
      this.mask = mask;
      this.name = name;
    }


  }

}
