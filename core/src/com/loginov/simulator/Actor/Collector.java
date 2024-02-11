package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Interfaces.Collect;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.SimulationParams;

import java.util.ArrayList;

public class Collector extends Human implements Collect {
    private Food foodToEat;

    public Collector(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM, 1f);
        satietyLineTexture = createTexture((int) bounds().width, 10, SimulationParams.getHumanColors().get(Collector.class));
    }

    public Collector(Texture texture, float x, float y, Clan clan, float METABOLISM) {
        this(texture, x, y, METABOLISM);
        this.clan = clan;
    }

    @Override
    public void operate(FoodGenerator foodGenerator, HumanGenerator humanGenerator, SimulationState state) {
        defineState(humanGenerator, state);
        switch (this.state) {
            case AT_HOME:
                move(0, 0);
                break;
            case WORK:
                if (foodCount == MAX_FOOD_COUNT) {
                    this.state = HumanState.GO_HOME;
                    break;
                }
                if (!foodGenerator.getFood().contains(foodToEat)) {
                    findFood(foodGenerator.getFood());
                }
                collect(foodGenerator);
                break;
            case GO_HOME:
                foodToEat = null;
                findNearestHome();
                goHome();
                if (SimulatorScreen.getGenerateTime() > SimulationState.NIGHT.getDuration() - 0.5f
                        && state.equals(SimulationState.NIGHT)) {
                    position = new Vector2(home);
                    this.state = HumanState.AT_HOME;
                    setSatiety(-20f);
                }
                break;
        }
        update();
    }

    @Override
    public void findFood(ArrayList<Food> food) {
        if (food.size() > 0) {
            foodToEat = food.get(0);
            float dist = Float.MAX_VALUE;
            for (Food f : food) {
                float tDist = (float) Math.sqrt(Math.pow(getCenterPosition().x - f.getCenterPosition().x, 2) +
                        Math.pow(getCenterPosition().y - f.getCenterPosition().y, 2));
                if (tDist < dist) {
                    foodToEat = f;
                    dist = tDist;
                }
            }
            float dx = foodToEat.getCenterPosition().x - getCenterPosition().x;
            float dy = foodToEat.getCenterPosition().y - getCenterPosition().y;
            move(SPEED * (dx / dist), SPEED * (dy / dist));
        } else state = HumanState.GO_HOME;
    }

    @Override
    public void collect(FoodGenerator foodGenerator) {
        float humanX = getCenterPosition().x;
        float humanY = getCenterPosition().y;
        if (foodToEat != null) {
            float foodX = foodToEat.getCenterPosition().x;
            float foodY = foodToEat.getCenterPosition().y;
            if (Math.sqrt(Math.pow(humanX - foodX, 2) + Math.pow(humanY - foodY, 2))
                    < SimulatorScreen.simulationSpeed) {
                foodCount++;
                foodGenerator.removeFood(foodToEat);
            }
        }
    }

    @Override
    public Food getFoodToEat() {
        return foodToEat;
    }

    @Override
    public void debug(ShapeRenderer shapeRenderer, OrthographicCamera apiCam) {
        shapeRenderer.setProjectionMatrix(apiCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (foodToEat != null) {
            shapeRenderer.setColor(SimulationParams.getHumanColors().get(Collector.class));
            shapeRenderer.rectLine(getCenterPosition(), foodToEat.getCenterPosition(), 2);
        }
        shapeRenderer.end();
    }
}
