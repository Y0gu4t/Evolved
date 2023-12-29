package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Interfaces.Collect;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;

public class Collector extends Human implements Collect {
    private Food foodToEat;

    public Collector(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM, 1f);
        satietyLineTexture = createTexture((int) getBounds().width, 10, Color.GREEN);
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
                int requiredAmountOfFood = Math.min(foodCount, (int) ((MAX_SATIETY - this.getSatiety()) / Food.getSatiety()));
                this.setSatiety(requiredAmountOfFood * Food.getSatiety());
                foodCount -= requiredAmountOfFood;
                move(0, 0);
                break;
            case WORK:
                if (foodCount == MAX_FOOD_COUNT) {
                    this.setState(HumanState.GO_HOME);
                    break;
                }
                if (satiety < 20 && foodCount > 0) {
                    setSatiety(Food.getSatiety());
                    foodCount--;
                }
                if (!foodGenerator.getFood().contains(this.getFoodToEat())) {
                    this.findFood(foodGenerator.getFood());
                }
                this.collect(foodGenerator);
                break;
            case GO_HOME:
                foodToEat = null;
                goHome();
                if (SimulatorScreen.getGenerateTime() > SimulationState.NIGHT.getDuration() - 0.5f
                        && state.equals(SimulationState.NIGHT)) {
                    position = new Vector2(home);
                    setState(HumanState.AT_HOME);
                    setSatiety(-20f);
                }
                break;
        }
        this.update();
    }

    @Override
    public void findFood(ArrayList<Food> foods) {
        if (foods.size() > 0) {
            foodToEat = foods.get(0);
            float dist = Float.MAX_VALUE;
            for (Food f : foods) {
                float t_dist = (float) Math.sqrt(Math.pow(getPosition().x - f.getPosition().x, 2) +
                        Math.pow(getPosition().y - f.getPosition().y, 2));
                if (t_dist < dist) {
                    foodToEat = f;
                    dist = t_dist;
                }
            }
            float dx = foodToEat.getPosition().x - getPosition().x;
            float dy = foodToEat.getPosition().y - getPosition().y;
            move(SPEED * (dx / dist), SPEED * (dy / dist));
        } else this.setState(HumanState.GO_HOME);
    }

    @Override
    public void collect(FoodGenerator foodGenerator) {
        float humanX = position.x;
        float humanY = position.y;
        if (foodToEat != null) {
            float foodX = foodToEat.getPosition().x;
            float foodY = foodToEat.getPosition().y;
            if (Math.sqrt(Math.pow(humanX - foodX, 2) + Math.pow(humanY - foodY, 2))
                    < 1 * SimulatorScreen.simulationSpeed) {
                foodCount++;
                foodGenerator.removeFood(foodToEat);
            }
        }
    }

    @Override
    protected Human giveBirth(Texture texture) {
        agesAfterChildbirth = 0;
        return new Collector(texture, position.x, position.y,
                Math.min(Math.max(this.METABOLISM * (MathUtils.random() + 0.5f), 0.5f), 2f));
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
            shapeRenderer.setColor(Color.GREEN);
            shapeRenderer.rectLine(this.getCenterPosition(), foodToEat.getCenterPosition(), 2);
        }
        shapeRenderer.end();
    }
}
