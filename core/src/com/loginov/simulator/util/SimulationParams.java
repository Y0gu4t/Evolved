package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Color;

public class SimulationParams {

    private static int foodCount = 30;
    private static int foodAdd = 10;
    private static int foodAreas = 1;
    private static int minFoodAreaSize = 50;
    private static int maxFoodAreaSize = 200;
    private static int minFoodAreaDistance = 300;
    private static int clanCount = 2;
    private static int humanCount = 20;
    private static int collectorCount = 15;
    private static int warriorCount = 5;
    private static int thiefCount = 10;
    private static int humanAreas = 1;
    private static int minHumanAreaSize = 50;
    private static int maxHumanAreaSize = 200;
    private static int minHumanAreaDistance = 500;
    private static int deltaSatiety = -10;
    private static final float METABOLISM = (float) (Math.random() + 0.5f);

    public static int getFoodCount() {
        return foodCount;
    }

    public static void setFoodCount(int foodCount) {
        SimulationParams.foodCount = foodCount;
    }

    public static int getFoodAdd() {
        return foodAdd;
    }

    public static void setFoodAdd(int foodAdd) {
        SimulationParams.foodAdd = foodAdd;
    }

    public static int getFoodAreas() {
        return foodAreas;
    }

    public static void setFoodAreas(int foodAreas) {
        SimulationParams.foodAreas = foodAreas;
    }

    public static int getMinFoodAreaSize() {
        return minFoodAreaSize;
    }

    public static void setMinFoodAreaSize(int minFoodAreaSize) {
        SimulationParams.minFoodAreaSize = minFoodAreaSize;
    }

    public static int getMaxFoodAreaSize() {
        return maxFoodAreaSize;
    }

    public static void setMaxFoodAreaSize(int maxFoodAreaSize) {
        SimulationParams.maxFoodAreaSize = maxFoodAreaSize;
    }

    public static int getMinFoodAreaDistance() {
        return minFoodAreaDistance;
    }

    public static void setMinFoodAreaDistance(int minFoodAreaDistance) {
        SimulationParams.minFoodAreaDistance = minFoodAreaDistance;
    }

    public static int getClanCount() {
        return clanCount;
    }

    public static void setClanCount(int clanCount) {
        SimulationParams.clanCount = clanCount;
    }

    public static int getHumanCount() {
        return humanCount;
    }

    public static void setHumanCount(int humanCount) {
        SimulationParams.humanCount = humanCount;
    }

    public static int getCollectorCount() {
        return collectorCount;
    }

    public static void setCollectorCount(int collectorCount) {
        SimulationParams.collectorCount = collectorCount;
    }

    public static int getWarriorCount() {
        return warriorCount;
    }

    public static void setWarriorCount(int warriorCount) {
        SimulationParams.warriorCount = warriorCount;
    }

    public static int getThiefCount() {
        return thiefCount;
    }

    public static void setThiefCount(int thiefCount) {
        SimulationParams.thiefCount = thiefCount;
    }

    public static int getHumanAreas() {
        return humanAreas;
    }

    public static void setHumanAreas(int humanAreas) {
        SimulationParams.humanAreas = humanAreas;
    }

    public static int getMinHumanAreaSize() {
        return minHumanAreaSize;
    }

    public static void setMinHumanAreaSize(int minHumanAreaSize) {
        SimulationParams.minHumanAreaSize = minHumanAreaSize;
    }

    public static int getMaxHumanAreaSize() {
        return maxHumanAreaSize;
    }

    public static void setMaxHumanAreaSize(int maxHumanAreaSize) {
        SimulationParams.maxHumanAreaSize = maxHumanAreaSize;
    }

    public static int getMinHumanAreaDistance() {
        return minHumanAreaDistance;
    }

    public static void setMinHumanAreaDistance(int minHumanAreaDistance) {
        SimulationParams.minHumanAreaDistance = minHumanAreaDistance;
    }

    public static int getDeltaSatiety() {
        return deltaSatiety;
    }

    public static void setDeltaSatiety(int deltaSatiety) {
        SimulationParams.deltaSatiety = deltaSatiety;
    }

    public static float getMETABOLISM() {
        return METABOLISM;
    }

}
