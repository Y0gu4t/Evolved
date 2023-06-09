package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;

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
    private final int MIN_AGES_TO_GIVE_BIRTH = 8;
    private final int MAX_AGES_TO_GIVE_BIRTH = 25;
    public static final int MAX_AGES_OF_LIFE = 35;
    private final int YEARS_BETWEEN_BIRTHS = 5;
    private Food foodToEat;
    private Texture satietyLineTexture;
    private float satiety = 70f;
    private int age;
    private int agesAfterChildbirth = 0;

    public Human(Texture texture, float x, float y, float METABOLISM) {
        super(texture, x, y, HUMAN_WIDTH, HUMAN_HEIGHT);
        age = 0;
        createTexture((int) getBounds().width, 10, Color.RED);
        this.METABOLISM = METABOLISM;
    }
    
    // set velocity of body
    public void move(float dx, float dy) {
        this.setVelocity(new Vector2(dx, dy));
    }

    // update the human's position considering satiety, metabolism,
    // and simulation's speed values
    public void update() {
        // y = -ax^2 + bx + c
        float param = SimulatorScreen.simulationSpeed*METABOLISM*4;
        float speed = (float)(-Math.pow((1 - satiety / MAX_SATIETY), 2) + (1 - satiety/ MAX_SATIETY));
        this.getPosition().add((float)(getVelocity().x*param*speed), getVelocity().y*param*speed);
        /*this.getPosition().add(getVelocity().x * (1 - satiety / MAX_SATIETY) * METABOLISM * SimulatorScreen.simulationSpeed,
                getVelocity().y * (1 - satiety / MAX_SATIETY) * METABOLISM * SimulatorScreen.simulationSpeed);*/
    }

    public void findFood(ArrayList<Food> foods) { // использование сортировки не оптимально
        if (foods.size() > 0) {
            Collections.sort(foods, new Comparator<Food>() {
                @Override
                public int compare(Food f1, Food f2) {
                    return Double.compare(Math.sqrt(Math.pow(getPosition().x - f1.getPosition().x, 2) + Math.pow(getPosition().y - f1.getPosition().y, 2)),
                            Math.sqrt(Math.pow(getPosition().x - f2.getPosition().x, 2) + Math.pow(getPosition().y - f2.getPosition().y, 2)));
                }
            });
            Food food = foods.get(0);
            foodToEat = food;
            float dist = (float) Math.sqrt(Math.pow(getPosition().x - food.getPosition().x, 2) + Math.pow(getPosition().y - food.getPosition().y, 2));
            float dx = food.getPosition().x - getPosition().x;
            float dy = food.getPosition().y - getPosition().y;
            move(SPEED * (dx / dist), SPEED * (dy / dist));
        } else move(0, 0);
    }

    public void isEaten(FoodGenerator foodGenerator) {
        float humanX = getPosition().x;
        float humanY = getPosition().y;
        float foodX = getFoodToEat().getPosition().x;
        float foodY = getFoodToEat().getPosition().y;
        if (Math.sqrt(Math.pow(humanX - foodX, 2) + Math.pow(humanY - foodY, 2)) < 1) {
            setSatiety(getFoodToEat().getSatiety() * METABOLISM);
            foodGenerator.removeFood(getFoodToEat());
        }
    }

    public boolean giveBirthOpportunity() {
        return ((age == MIN_AGES_TO_GIVE_BIRTH || (age > MIN_AGES_TO_GIVE_BIRTH && age <= MAX_AGES_TO_GIVE_BIRTH &&
                agesAfterChildbirth >= YEARS_BETWEEN_BIRTHS)) && satiety >= MAX_SATIETY / 2);
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
