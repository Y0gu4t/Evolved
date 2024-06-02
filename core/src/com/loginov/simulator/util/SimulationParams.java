package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Color;
import com.loginov.simulator.actor.Collector;
import com.loginov.simulator.actor.Human;
import com.loginov.simulator.actor.Thief;
import com.loginov.simulator.actor.Warrior;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimulationParams {

    private static int foodCount = 100;
    private static int foodAdd = 40;
    private static int foodAreas = 1;
    private static int minFoodAreaSize = 50;
    private static int maxFoodAreaSize = 200;
    private static int minFoodAreaDistance = 300;
    private static int clanCount = 2;
    private static final int humanCount = 20;
    private static final int collectorCount = 5;
    private static final int warriorCount = 2;
    private static final int thiefCount = 3;
    private static int humanAreas = 1;
    private static int minHumanAreaSize = 50;
    private static int maxHumanAreaSize = 200;
    private static int minHumanAreaDistance = 500;
    private static int deltaSatiety = -5;
    private static final float METABOLISM = (float) (Math.random() + 0.5f);

    private static final List<ClanParams> clanList = Arrays.asList(
            new ClanParams(5,3,2,
                    new Color(0x80e8e2ff),
                    new Color(0x20b2aaff),
                    "clan-blue" ),
            new ClanParams(5,3,2,
                    new Color(0xf7bfbfff),
                    new Color(0xf08080ff),
                    "clan-coral" ));

    private static final Map<Class<? extends Human>, Color> humanColors =
            new HashMap<Class<? extends Human>, Color>() {{
                put(Collector.class, new Color(0x5dbb63ff));
                put(Thief.class, new Color(0xfcf4a3ff));
                put(Warrior.class, new Color(0xb90e0aff));
            }};

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

    public static List<ClanParams> getClanList() {
        return clanList;
    }

    public static Map<Class<? extends Human>, Color> getHumanColors() {
        return humanColors;
    }
}
