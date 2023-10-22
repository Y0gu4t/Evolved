package com.loginov.simulator.Enums;

public enum SimulationState {
    DAY(15.0f),
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
