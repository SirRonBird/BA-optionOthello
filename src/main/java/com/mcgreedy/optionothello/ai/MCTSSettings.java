package com.mcgreedy.optionothello.ai;

public record MCTSSettings(
    double explorationConstant,
    boolean useMast,
    boolean useRave
) {


    @Override
    public String toString() {
        return "MCTSSettings{" +
            "explorationConstant=" + explorationConstant +
            ", useMast=" + useMast +
            ", useRave=" + useRave +
            '}';
    }
}
