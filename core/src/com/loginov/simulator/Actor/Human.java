package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Main class considered in simulation
 *
 * Each human has a satiety level, metabolism, movement speed,
 * minimum and maximum age for reproduction, breeding interval, lifespan value
 * @author loginov0203@gmail.com
 */

public class Human extends DynamicWorldObject {
    private static final float HUMAN_WIDTH = 30f;
    private static final float HUMAN_HEIGHT = 30f;
    private final float MAX_SATIETY = 100f;
    private final float METABOLISM;
    private final float SPEED = 1f;
    private final int MIN_AGES_TO_GIVE_BIRTH = 3;
    private final int MAX_AGES_TO_GIVE_BIRTH = 8;
    public static final int MAX_AGES_OF_LIFE = 15;
    private final int YEARS_BETWEEN_BIRTHS = 3;
    private Food foodToEat;
    private Texture satietyLineTexture;
    private float satiety = 70f;
    private int age;
    private Vector2 home;

    private HumanState state;
    private int agesAfterChildbirth = 0;


    public Human(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, HUMAN_WIDTH, HUMAN_HEIGHT);
        age = 0;
        home = new Vector2(x,y);
        state = HumanState.FIND_FOOD;
        createTexture((int) getBounds().width, 10, Color.RED);
        this.METABOLISM = METABOLISM;
    }

    public void operate(FoodGenerator foodGenerator, HumanGenerator humanGenerator, SimulationState state){
        defineState(humanGenerator, state);
        switch (this.state) {
            case AT_HOME:
                move(0,0);
                break;
            case FIND_FOOD:
                if (!foodGenerator.getFood().contains(this.getFoodToEat())) {
                    this.findFood(foodGenerator.getFood());
                }
                if (this.isEaten(foodGenerator)){
                    this.setState(HumanState.GO_HOME);
                }
                break;
            case GO_HOME:
                goHome();
                break;
        }
        this.update();
    }

    private void defineState(HumanGenerator humanGenerator, SimulationState state){
        switch (state){
            case DAY:
                break;
            case NIGHT:
                if(this.state != HumanState.AT_HOME)
                    setState(HumanState.GO_HOME);
                else
                    if (this.giveBirthOpportunity()) {
                        humanGenerator.prepareChildren(this.giveBirth(this.getTexture()));
                        agesAfterChildbirth = 0;
                    }
                break;
        }
    }

    /**
     * set velocity of body
     * @param dx - delta x,
     * @param dy - delta y
      */
    public void move(float dx, float dy) {
        this.setVelocity(new Vector2(dx, dy));
    }

    /**
     * update the human's position considering satiety, metabolism,
     * and simulation's speed values
      */
    public void update() {
        // y = -ax^2 + bx
        float param = SimulatorScreen.simulationSpeed*METABOLISM*4;
        float speed = (float)(-Math.pow((1 - satiety / MAX_SATIETY), 2) + (1 - satiety/ MAX_SATIETY));
        speed = getState() == HumanState.GO_HOME ?  speed + 0.1f : speed;
        this.getPosition().add(getVelocity().x*param*speed, getVelocity().y*param*speed);
    }

    public void goHome() {
        if (Math.sqrt(Math.pow(getPosition().x - home.x, 2) + Math.pow(getPosition().y - home.y, 2)) < 1*SimulatorScreen.simulationSpeed) {
            setState(HumanState.AT_HOME);
        }
        else {
            float dist = (float) Math.sqrt(Math.pow(getPosition().x - home.x, 2) + Math.pow(getPosition().y - home.y, 2));
            float dx = home.x - getPosition().x;
            float dy = home.y - getPosition().y;
            move(SPEED * (dx/dist), SPEED * (dy/dist));
        }
    }

    public void findFood(ArrayList<Food> foods) {
        if (foods.size() > 0) {
            foodToEat = foods.get(0);
            float dist = Float.MAX_VALUE;
            for (Food f: foods){
                float t_dist = (float) Math.sqrt(Math.pow(getPosition().x - f.getPosition().x, 2) +
                        Math.pow(getPosition().y - f.getPosition().y, 2));
                if (t_dist < dist){
                    foodToEat = f;
                    dist = t_dist;
                }
            }
            float dx = foodToEat.getPosition().x - getPosition().x;
            float dy = foodToEat.getPosition().y - getPosition().y;
            move(SPEED * (dx / dist), SPEED * (dy / dist));
        } else move(0, 0);
    }

    public boolean isEaten(FoodGenerator foodGenerator) {
        float humanX = getPosition().x;
        float humanY = getPosition().y;
        // TODO: think about optimize
        if(getFoodToEat() != null) {
            float foodX = getFoodToEat().getPosition().x;
            float foodY = getFoodToEat().getPosition().y;
            if (Math.sqrt(Math.pow(humanX - foodX, 2) + Math.pow(humanY - foodY, 2)) < 1*SimulatorScreen.simulationSpeed) {
                setSatiety(getFoodToEat().getSatiety() * METABOLISM);
                foodGenerator.removeFood(getFoodToEat());
                return true;
            }
        }
        return false;
    }

    public boolean giveBirthOpportunity() {
        return ((age >= MIN_AGES_TO_GIVE_BIRTH && age <= MAX_AGES_TO_GIVE_BIRTH &&
                agesAfterChildbirth >= YEARS_BETWEEN_BIRTHS)) && satiety >= MAX_SATIETY / 2;
    }

    public Human giveBirth(Texture texture) {
        agesAfterChildbirth = 0;
        Human human = new Human(texture, this.getPosition().x, this.getPosition().y, Math.min(Math.max(this.METABOLISM * (float) (Math.random() + 0.5f), 0.5f),2f));

        return human;
    }

    private void createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        satietyLineTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public void draw(SpriteBatch batch) {
        batch.draw(satietyLineTexture, getPosition().x, getPosition().y + getBounds().height + 5, getBounds().width * (satiety / MAX_SATIETY), 5);
        batch.draw(getTexture(), getPosition().x, getPosition().y, getBounds().width, getBounds().height);
    }

    public float getSatiety() {
        return satiety;
    }

    public void setSatiety(float satiety) {
        this.satiety = Math.min(this.satiety + satiety, MAX_SATIETY);
    }

    public Human satiety(float satiety) {
        this.satiety = Math.min(this.satiety + satiety, MAX_SATIETY);
        return this;
    }

    public static float getHumanWidth() {
        return HUMAN_WIDTH;
    }

    public static float getHumanHeight() {
        return HUMAN_HEIGHT;
    }

    public Food getFoodToEat() {
        return foodToEat;
    }

    public float getMetabolism() {
        return METABOLISM;
    }


    public int getAge() {
        return age;
    }

    public void updateAge() {
        age++;
    }

    public int getAgesAfterChildbirth() {
        return agesAfterChildbirth;
    }

    public void updateAgesAfterChildbirth() {
        agesAfterChildbirth++;
    }

    public HumanState getState() {
        return state;
    }

    public void setState(HumanState state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Human{" +
                ", METABOLISM=" + METABOLISM +
                ", foodToEat=" + foodToEat +
                ", satiety=" + satiety +
                ", age=" + age +
                ", agesAfterChildbirth=" + agesAfterChildbirth +
                '}';
    }
}
