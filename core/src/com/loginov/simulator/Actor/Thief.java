package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Interfaces.Collect;
import com.loginov.simulator.Actor.Interfaces.Steal;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;

public class Thief extends Human implements Collect, Steal {
    private WorldObject goal;
    private final ArrayList<Human> checkedVictims;

    public Thief(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, METABOLISM, 1.2f);
        checkedVictims = new ArrayList<>();
        checkedVictims.add(this);
        satietyLineTexture = createTexture((int) getBounds().width, 10, Color.YELLOW);
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

                this.find(foodGenerator, humanGenerator);

                if (goal.getClass().equals(Food.class)) {
                    this.collect(foodGenerator);
                } else {
                    this.steal(humanGenerator);
                }
                break;

            case GO_HOME:
                goal = null;
                checkedVictims.clear();
                goHome();
                if (SimulatorScreen.getGenerateTime() > SimulationState.NIGHT.getDuration() - 0.5f
                        && state.equals(SimulationState.NIGHT)) {
                    position = new Vector2(home);
                    setState(HumanState.AT_HOME);
                    setSatiety(-25f);
                }
                break;
        }
        this.update();
    }

    private void find(FoodGenerator foodGenerator, HumanGenerator humanGenerator) {
        findActor(humanGenerator.getHumans());
        findFood(foodGenerator.getFood());
        float dist = (float) Math.sqrt(Math.pow(position.x - goal.position.x, 2) +
                Math.pow(position.y - goal.getPosition().y, 2));
        float dx = goal.getPosition().x - position.x;
        float dy = goal.getPosition().y - position.y;
        move(ACCELERATION * SPEED * (dx / dist), ACCELERATION * SPEED * (dy / dist));
    }

    @Override
    protected Human giveBirth(Texture texture) {
        agesAfterChildbirth = 0;
        return new Thief(texture, position.x, position.y,
                Math.min(Math.max(this.METABOLISM * (MathUtils.random() + 0.5f), 0.5f), 2f));
    }

    @Override
    public void findFood(ArrayList<Food> food) {
        if (food.size() > 0) {
            float dist = (float) Math.sqrt(Math.pow(getPosition().x - goal.getPosition().x, 2) +
                    Math.pow(getPosition().y - goal.getPosition().y, 2));

            for (Food f : food) {
                float t_dist = (float) Math.sqrt(Math.pow(getPosition().x - f.getPosition().x, 2) +
                        Math.pow(getPosition().y - f.getPosition().y, 2));
                if (t_dist < dist) {
                    goal = f;
                    dist = t_dist;
                }
            }
        }
    }

    @Override
    public void collect(FoodGenerator foodGenerator) {
        float humanX = getPosition().x;
        float humanY = getPosition().y;

        if (goal != null) {
            float foodX = goal.getPosition().x;
            float foodY = goal.getPosition().y;

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
                if (!checkedVictims.contains(h) && h.getState() != HumanState.AT_HOME && h != this) {

                    float t_dist = (float) Math.sqrt(Math.pow(position.x - h.position.x, 2) +
                            Math.pow(position.y - h.getPosition().y, 2));

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
        float humanX = position.x;
        float humanY = position.y;
        Human humanGoal = (Human) goal;

        if (humanGoal != null && humanGoal.getState() != HumanState.AT_HOME) {
            float humanToStealX = humanGoal.getPosition().x;
            float humanToStealY = humanGoal.getPosition().y;

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
                    float dx = humanGoal.getPosition().x - position.x;
                    float dy = humanGoal.getPosition().y - position.y;
                    float push = 0.3f;
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
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rectLine(this.getCenterPosition(), goal.getCenterPosition(), 2);
            shapeRenderer.end();
        }
    }
}
