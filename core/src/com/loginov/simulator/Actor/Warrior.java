package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Interfaces.Steal;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;

public class Warrior extends Human implements Steal {
    private final ArrayList<Human> checkedVictims;
    private Human humanToSteal;

    public Warrior(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM,1.5f);
        checkedVictims = new ArrayList<>();
        checkedVictims.add(this);
    }

    public Warrior(Texture texture, float x, float y, Clan clan, float METABOLISM) {
        this(texture, x, y, METABOLISM);
        this.clan = clan;
    }

    @Override
    public void operate(FoodGenerator foodGenerator, HumanGenerator humanGenerator, SimulationState state) {
        defineState(humanGenerator, state);
        switch (this.state) {
            case AT_HOME:
                int requiredAmountOfFood = Math.min(foodCount, (int)((MAX_SATIETY - satiety)/Food.getSatiety()));
                this.setSatiety(requiredAmountOfFood * Food.getSatiety());
                foodCount -= requiredAmountOfFood;
                move(0,0);
                break;
            case WORK:
                if (foodCount == MAX_FOOD_COUNT){
                    this.setState(HumanState.GO_HOME);
                    break;
                }
                if (satiety < 20 && foodCount > 0){
                    setSatiety(Food.getSatiety());
                    foodCount--;
                }
                if (SimulatorScreen.getGenerateTime() > SimulationState.DAY.getDuration()/8){
                    this.findActor(humanGenerator.getHumans());
                    this.steal(humanGenerator);
                }
                break;
            case GO_HOME:
                checkedVictims.clear();
                humanToSteal = null;
                goHome();
                if (SimulatorScreen.getGenerateTime() > SimulationState.NIGHT.getDuration() + 0.5f
                        && state.equals(SimulationState.NIGHT)){
                    position = new Vector2(home);
                    setState(HumanState.AT_HOME);
                    setSatiety(-30f);
                }
                break;
        }
        this.update();
    }

    @Override
    protected Human giveBirth(Texture texture) {
        agesAfterChildbirth = 0;
        return new Warrior(texture, position.x, position.y,
                Math.min(Math.max(this.METABOLISM * (MathUtils.random() + 0.5f), 0.5f),2f));
    }

    @Override
    public void findActor(ArrayList<Human> humans) {
        if (humans.size() > 0) {
            humanToSteal = humans.get(0);
            float dist = Float.MAX_VALUE;
            for (Human h : humans) {
                if (!checkedVictims.contains(h) && h.getState() != HumanState.AT_HOME && h != this) {
                    float t_dist = (float) Math.sqrt(Math.pow(position.x - h.position.x, 2) +
                            Math.pow(position.y - h.getPosition().y, 2));
                    if (t_dist < dist) {
                        humanToSteal = h;
                        dist = t_dist;
                    }
                    float dx = humanToSteal.getPosition().x - position.x;
                    float dy = humanToSteal.getPosition().y - position.y;
                    move(ACCELERATION * SPEED * (dx / dist), ACCELERATION * SPEED * (dy / dist));
                }
            }
        }
    }

    @Override
    public void steal(HumanGenerator humanGenerator) {
        float humanX = position.x;
        float humanY = position.y;
        if(humanToSteal != null && humanToSteal.getState() != HumanState.AT_HOME) {
            float humanToStealX = humanToSteal.getPosition().x;
            float humanToStealY = humanToSteal.getPosition().y;
            if (Math.sqrt(Math.pow(humanX - humanToStealX, 2) + Math.pow(humanY - humanToStealY, 2)) < 30) {
                int foodToSteal;
                if (humanToSteal.getClass().equals(Collector.class)) {
                    foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                    foodCount += foodToSteal;
                    humanToSteal.minusFood(foodToSteal);
                } else if (humanToSteal.getClass().equals(Warrior.class)){
                    if (MathUtils.randomBoolean(0.5f)){
                        foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                        foodCount += foodToSteal;
                        humanToSteal.minusFood(foodToSteal);
                    } else {
                        foodToSteal = Math.min(MAX_FOOD_COUNT - humanToSteal.getFoodCount(), foodCount);
                        humanToSteal.addFood(foodToSteal);
                        foodCount -= foodToSteal;
                    }
                    ((Warrior) humanToSteal).addVictim(this);
                    float dx = humanToSteal.getPosition().x - position.x;
                    float dy = humanToSteal.getPosition().y - position.y;
                    float push = 0.3f;
                    position.add(-dx*push, -dy*push);
                    humanToSteal.getPosition().add(dx*push, dy*push);
                } else if (humanToSteal.getClass().equals(Thief.class)){
                    foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                    foodCount += foodToSteal;
                    humanToSteal.minusFood(foodToSteal);
                    ((Thief) humanToSteal).addVictim(this);
                }
                checkedVictims.add(humanToSteal);
            }
        }
    }

    public Human getHumanToSteal() {
        return humanToSteal;
    }

    @Override
    public void addVictim(Human human) {
        checkedVictims.add(human);
    }

    @Override
    public void debug(ShapeRenderer shapeRenderer, OrthographicCamera apiCam) {
        if (humanToSteal != null){
            shapeRenderer.setProjectionMatrix(apiCam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.RED);
            shapeRenderer.rectLine(this.getCenterPosition(), humanToSteal.getCenterPosition(), 2);
            shapeRenderer.end();
        }
    }
}
