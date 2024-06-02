package com.loginov.simulator.actor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.states.HumanState;
import com.loginov.simulator.states.SimulationState;
import com.loginov.simulator.screen.SimulatorScreen;
import com.loginov.simulator.actor.actions.Steal;
import com.loginov.simulator.clan.Clan;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.SimulationParams;

import java.util.ArrayList;

public class Warrior extends Human implements Steal {
    private final ArrayList<Human> checkedVictims;
    private Human humanToSteal;

    public Warrior(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM, 1.5f);
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
                move(0, 0);
                break;
            case WORK:
                if (foodCount == MAX_FOOD_COUNT) {
                    this.state = HumanState.GO_HOME;
                    break;
                }
                if (SimulatorScreen.getGenerateTime() > SimulationState.DAY.getDuration() / 8f) {
                    findActor(humanGenerator.getHumans());
                    steal(humanGenerator);
                }
                break;
            case GO_HOME:
                checkedVictims.clear();
                humanToSteal = null;
                findNearestHome();
                goHome();
                if (SimulatorScreen.getGenerateTime() > SimulationState.NIGHT.getDuration() - 0.5f
                        && state.equals(SimulationState.NIGHT)) {
                    position = new Vector2(home);
                    this.state = HumanState.AT_HOME;
                    setSatiety(SimulationParams.getDeltaSatiety());
                }
                break;
        }
        update();
    }

    @Override
    public void findActor(ArrayList<Human> humans) {
        if (humans.size() > 0) {
            humanToSteal = null;
            float dist = Float.MAX_VALUE;
            for (Human h : humans) {
                if (!h.getClan().equals(clan) && h != this &&
                        !checkedVictims.contains(h) && h.getState() != HumanState.AT_HOME &&
                        !h.getClass().equals(Warrior.class)) {
                    float t_dist = (float) Math.sqrt(Math.pow(getCenterPosition().x - h.getCenterPosition().x, 2) +
                            Math.pow(getCenterPosition().y - h.getCenterPosition().y, 2));
                    if (t_dist < dist) {
                        humanToSteal = h;
                        dist = t_dist;
                    }
                    float dx = humanToSteal.getCenterPosition().x - getCenterPosition().x;
                    float dy = humanToSteal.getCenterPosition().y - getCenterPosition().y;
                    move(ACCELERATION * SPEED * (dx / dist), ACCELERATION * SPEED * (dy / dist));
                }
            }
            if (humanToSteal == null) {
                state = HumanState.GO_HOME;
            }
        }
    }

    @Override
    public void steal(HumanGenerator humanGenerator) {
        float humanX = getCenterPosition().x;
        float humanY = getCenterPosition().y;
        if (humanToSteal != null && humanToSteal.getState() != HumanState.AT_HOME) {
            float humanToStealX = humanToSteal.getCenterPosition().x;
            float humanToStealY = humanToSteal.getCenterPosition().y;
            if (Math.sqrt(Math.pow(humanX - humanToStealX, 2) + Math.pow(humanY - humanToStealY, 2)) < Human.getHumanWidth()) {
                int foodToSteal;
                if (humanToSteal.getClass().equals(Collector.class)) {
                    foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                    foodCount += foodToSteal;
                    humanToSteal.minusFood(foodToSteal);
                } else if (humanToSteal.getClass().equals(Warrior.class)) {
                    if (MathUtils.randomBoolean(0.5f)) {
                        foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                        foodCount += foodToSteal;
                        humanToSteal.minusFood(foodToSteal);
                    } else {
                        foodToSteal = Math.min(MAX_FOOD_COUNT - humanToSteal.getFoodCount(), foodCount);
                        humanToSteal.addFood(foodToSteal);
                        foodCount -= foodToSteal;
                    }
                    ((Warrior) humanToSteal).addVictim(this);
                    float dx = humanToSteal.getCenterPosition().x - humanX;
                    float dy = humanToSteal.getCenterPosition().y - humanY;
                    float push = 0.1f;
                    position.add(-dx * push, -dy * push);
                    humanToSteal.getPosition().add(dx * push, dy * push);
                } else if (humanToSteal.getClass().equals(Thief.class)) {
                    foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanToSteal.getFoodCount());
                    foodCount += foodToSteal;
                    humanToSteal.minusFood(foodToSteal);
                    ((Thief) humanToSteal).addVictim(this);
                }
                checkedVictims.add(humanToSteal);
            }
        } else {
            humanToSteal = null;
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
        if (humanToSteal != null) {
            shapeRenderer.setProjectionMatrix(apiCam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(SimulationParams.getHumanColors().get(Warrior.class));
            shapeRenderer.rectLine(getCenterPosition(), humanToSteal.getCenterPosition(), 2);
            shapeRenderer.end();
        }
    }
}
