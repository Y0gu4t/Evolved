package com.loginov.simulator.Enums;

public enum SimulationState {
    // TODO: FIX THE BUG WHEN PEOPLE GO OUTSIDE THE MAP WHEN CHANGING FROM NIGHT TO DAY
    DAY(16f),
    NIGHT(DAY.duration/2),
    ;

    private float duration;
    SimulationState(float generatedTime) {
        this.duration = generatedTime;
    }

    public float getDuration() {
        return duration;
    }
}
