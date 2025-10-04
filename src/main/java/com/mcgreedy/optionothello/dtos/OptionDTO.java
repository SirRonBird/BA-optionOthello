package com.mcgreedy.optionothello.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcgreedy.optionothello.ai.Option;
import com.mcgreedy.optionothello.utils.SaveGameUtils;
import java.util.ArrayList;
import java.util.List;

public class OptionDTO {

  @JsonProperty("name")
  private String name;

  @JsonProperty("initiationSet")
  private List<BoardMaskDTO> initiationSet;

  public OptionDTO() {}

  public OptionDTO(String name,List<BoardMaskDTO> initiationSet) {
    this.name = name;
    this.initiationSet = initiationSet;
  }


  public List<BoardMaskDTO> getInitiationSet() {
    return initiationSet;
  }

  public void setInitiationSet(List<BoardMaskDTO> initiationSet) {
    this.initiationSet = initiationSet;
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

  public static OptionDTO fromOption(Option option) {

    if (option == null){
      return new OptionDTO(
          "noOption",
          new ArrayList<>()
      );
    } else {

      return new OptionDTO(
          option.getName(),
          SaveGameUtils.toMaskDTO(option.initiationSet())
      );
    }
  }

}
