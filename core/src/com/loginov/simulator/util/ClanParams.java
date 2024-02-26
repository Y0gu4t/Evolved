package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Color;

public class ClanParams {
    private int collectorCount;
    private int thiefCount;
    private int warriorCount;
    private Color territoryColor;
    private Color diagramColor;
    private String textAreaStyle;

    public ClanParams(int collectorCount, int thiefCount, int warriorCount, Color territoryColor, Color diagramColor, String textAreaStyle) {
        this.collectorCount = collectorCount;
        this.thiefCount = thiefCount;
        this.warriorCount = warriorCount;
        this.territoryColor = territoryColor;
        this.diagramColor = diagramColor;
        this.textAreaStyle = textAreaStyle;
    }

    public int getCollectorCount() {
        return collectorCount;
    }

    public void setCollectorCount(int collectorCount) {
        this.collectorCount = collectorCount;
    }

    public int getThiefCount() {
        return thiefCount;
    }

    public void setThiefCount(int thiefCount) {
        this.thiefCount = thiefCount;
    }

    public int getWarriorCount() {
        return warriorCount;
    }

    public void setWarriorCount(int warriorCount) {
        this.warriorCount = warriorCount;
    }

    public Color getTerritoryColor() {
        return territoryColor;
    }

    public void setTerritoryColor(Color territoryColor) {
        this.territoryColor = territoryColor;
    }

    public Color getDiagramColor() {
        return diagramColor;
    }

    public void setDiagramColor(Color diagramColor) {
        this.diagramColor = diagramColor;
    }

    public String getTextAreaStyle() {
        return textAreaStyle;
    }

    public void setTextAreaStyle(String textAreaStyle) {
        this.textAreaStyle = textAreaStyle;
    }
}
