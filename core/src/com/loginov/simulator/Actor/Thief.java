package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Interfaces.Collect;
import com.loginov.simulator.Actor.Interfaces.Steal;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.SimulationParams;

import java.util.ArrayList;

public class Thief extends Human implements Collect, Steal {
    private WorldObject goal;
    private final ArrayList<Human> checkedVictims;

    public Thief(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM, 1.2f);
        checkedVictims = new ArrayList<>();
        checkedVictims.add(this);
        satietyLineTexture = createTexture((int) bounds().width, 10, SimulationParams.getHumanColors().get(Thief.class));
    }

    public Thief(Texture texture, float x, float y, Clan clan, float METABOLISM) {
        this(texture, x, y, METABOLISM);
        this.clan = clan;
    }

    @Override
    public void operate(FoodGenerator foodGenerator, HumanGenerator humanGenerator, SimulationState state) {
        defineState(humanGenerator, state);
        switch (this.state) {
            case AT_HOME:
                giveFoodToClan();
                move(0, 0);
                break;

            case WORK:
                if (foodCount == MAX_FOOD_COUNT) {
                    this.state = HumanState.GO_HOME;
                    break;
                }

                find(foodGenerator, humanGenerator);

                if (goal.getClass().equals(Food.class)) {
                    collect(foodGenerator);
                } else {
                    steal(humanGenerator);
                }
                break;

            case GO_HOME:
                goal = null;
                checkedVictims.clear();
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

    private void find(FoodGenerator foodGenerator, HumanGenerator humanGenerator) {
        findActor(humanGenerator.getHumans());
        findFood(foodGenerator.getFood());
        float dist = (float) Math.sqrt(Math.pow(getCenterPosition().x - goal.getCenterPosition().x, 2) +
                Math.pow(getCenterPosition().y - goal.getCenterPosition().y, 2));
        float dx = goal.getCenterPosition().x - getCenterPosition().x;
        float dy = goal.getCenterPosition().y - getCenterPosition().y;
        move(ACCELERATION * SPEED * (dx / dist), ACCELERATION * SPEED * (dy / dist));
    }

    @Override
    public void findFood(ArrayList<Food> food) {
        if (food.size() > 0) {
            float dist = (float) Math.sqrt(Math.pow(getCenterPosition().x - goal.getCenterPosition().x, 2) +
                    Math.pow(getCenterPosition().y - goal.getCenterPosition().y, 2));

            for (Food f : food) {
                float t_dist = (float) Math.sqrt(Math.pow(getCenterPosition().x - f.getCenterPosition().x, 2) +
                        Math.pow(getCenterPosition().y - f.getCenterPosition().y, 2));
                if (t_dist < dist) {
                    goal = f;
                    dist = t_dist;
                }
            }
        }
    }

    @Override
    public void collect(FoodGenerator foodGenerator) {
        float humanX = getCenterPosition().x;
        float humanY = getCenterPosition().y;

        if (goal != null) {
            float foodX = goal.getCenterPosition().x;
            float foodY = goal.getCenterPosition().y;

            if (Math.sqrt(Math.pow(humanX - foodX, 2) + Math.pow(humanY - foodY, 2))
                    < 1 * SimulatorScreen.simulationSpeed) {

                foodCount++;
                foodGenerator.removeFood((Food) goal);
            }
        }
    }

    @Override
    public Food getFoodToEat() {
        return (Food) goal;
    }

    @Override
    public void findActor(ArrayList<Human> humans) {
        if (humans.size() > 0) {
            goal = humans.get(0);
            float dist = Float.MAX_VALUE;

            for (Human h : humans) {
                if (!h.getClan().equals(clan) && h != this &&
                        !checkedVictims.contains(h) && h.getState() != HumanState.AT_HOME &&
                        h.getClass().equals(Collector.class)) {

                    float t_dist = (float) Math.sqrt(Math.pow(getCenterPosition().x - h.getCenterPosition().x, 2) +
                            Math.pow(getCenterPosition().y - h.getCenterPosition().y, 2));

                    if (t_dist < dist) {
                        goal = h;
                        dist = t_dist;
                    }
                }
            }
        }
    }

    @Override
    public void steal(HumanGenerator humanGenerator) {
        float humanX = getCenterPosition().x;
        float humanY = getCenterPosition().y;
        Human humanGoal = (Human) goal;

        if (humanGoal != null && humanGoal.getState() != HumanState.AT_HOME) {
            float humanToStealX = humanGoal.getCenterPosition().x;
            float humanToStealY = humanGoal.getCenterPosition().y;

            if (Math.sqrt(Math.pow(humanX - humanToStealX, 2) + Math.pow(humanY - humanToStealY, 2)) < 30) {
                int foodToSteal;

                if (humanGoal.getClass().equals(Collector.class)) {
                    foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanGoal.getFoodCount());
                    foodCount += foodToSteal;
                    humanGoal.minusFood(foodToSteal);

                } else if (humanGoal.getClass().equals(Warrior.class)) {
                    foodToSteal = Math.min(MAX_FOOD_COUNT - humanGoal.getFoodCount(), foodCount);
                    foodCount -= foodToSteal;
                    humanGoal.addFood(foodToSteal);
                    ((Warrior) humanGoal).addVictim(this);

                } else if (humanGoal.getClass().equals(Thief.class)) {
                    if (MathUtils.randomBoolean(0.5f)) {
                        foodToSteal = Math.min(MAX_FOOD_COUNT - foodCount, humanGoal.getFoodCount());
                        foodCount += foodToSteal;
                        humanGoal.minusFood(foodToSteal);
                    } else {
                        foodToSteal = Math.min(MAX_FOOD_COUNT - humanGoal.getFoodCount(), foodCount);
                        humanGoal.addFood(foodToSteal);
                        foodCount -= foodToSteal;
                    }

                    ((Thief) humanGoal).addVictim(this);
                    float dx = humanGoal.getCenterPosition().x - getCenterPosition().x;
                    float dy = humanGoal.getCenterPosition().y - getCenterPosition().y;
                    float push = 0.1f;
                    position.add(-dx * push, -dy * push);
                    humanGoal.getPosition().add(dx * push, dy * push);
                }
                checkedVictims.add(humanGoal);
            }
        }
    }

    @Override
    public void addVictim(Human human) {
        checkedVictims.add(human);
    }

    @Override
    public void debug(ShapeRenderer shapeRenderer, OrthographicCamera apiCam) {
        if (goal != null) {
            shapeRenderer.setProjectionMatrix(apiCam.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(SimulationParams.getHumanColors().get(Thief.class));
            shapeRenderer.rectLine(getCenterPosition(), goal.getCenterPosition(), 2);
            shapeRenderer.end();
        }
    }
}
