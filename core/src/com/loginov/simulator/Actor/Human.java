package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;

/**
 * Main class considered in simulation
 * <p>
 * Each human has a satiety level, metabolism, movement speed,
 * minimum and maximum age for reproduction, breeding interval, lifespan value
 *
 * @author loginov0203@gmail.com
 */
public abstract class Human extends DynamicWorldObject {
    private static final float HUMAN_WIDTH = 30f;
    private static final float HUMAN_HEIGHT = 30f;
    protected final float MAX_SATIETY = 100f;
    protected final float METABOLISM;
    protected final float SPEED = 1f;
    private final int MIN_AGES_TO_GIVE_BIRTH = 3;
    private final int MAX_AGES_TO_GIVE_BIRTH = 8;
    public static final int MAX_AGES_OF_LIFE = 15;
    private final int YEARS_BETWEEN_BIRTHS = 3;
    protected final float ACCELERATION;
    public final int MAX_FOOD_COUNT = 5;
    protected int foodCount = 0;
    protected Texture satietyLineTexture;
    protected Texture foodLineTexture;
    protected float satiety = 70f;
    private int age;
    protected Vector2 home;
    protected HumanState state;
    protected int agesAfterChildbirth = 0;

    public Human(Texture texture, float x, float y, float METABOLISM, float ACCELERATION) {
        super(texture, x, y, HUMAN_WIDTH, HUMAN_HEIGHT);
        age = 0;
        home = new Vector2(x, y);
        state = HumanState.WORK;
        satietyLineTexture = createTexture((int) getBounds().width, 10, Color.RED);
        foodLineTexture = createTexture((int) getBounds().width, 10, Color.ORANGE);
        this.METABOLISM = METABOLISM;
        this.ACCELERATION = ACCELERATION;
    }

    public abstract void operate(FoodGenerator foodGenerator, HumanGenerator humanGenerator, SimulationState state);

    protected void defineState(HumanGenerator humanGenerator, SimulationState state) {
        switch (state) {
            case DAY:
                break;
            case NIGHT:
                if (this.state != HumanState.AT_HOME)
                    setState(HumanState.GO_HOME);
                else if (this.giveBirthOpportunity()) {
                    humanGenerator.prepareChildren(giveBirth(texture));
                    agesAfterChildbirth = 0;
                }
                break;
        }
    }

    /**
     * set velocity of body
     *
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
        float param = SimulatorScreen.simulationSpeed * METABOLISM * 4;
        float speed = (float) (-Math.pow((1 - satiety / MAX_SATIETY), 2) + (1 - satiety / MAX_SATIETY));
        speed = state == HumanState.GO_HOME ? speed + 0.1f : speed;
        position.add(velocity.x * param * speed, velocity.y * param * speed);
    }

    public void goHome() {
        if (Math.sqrt(Math.pow(position.x - home.x, 2) + Math.pow(position.y - home.y, 2)) < 3 * SimulatorScreen.simulationSpeed) {
            setState(HumanState.AT_HOME);
        } else {
            float dist = (float) Math.sqrt(Math.pow(position.x - home.x, 2) + Math.pow(position.y - home.y, 2));
            float dx = home.x - position.x;
            float dy = home.y - position.y;
            move(SPEED * (dx / dist), SPEED * (dy / dist));
        }
    }

    public boolean giveBirthOpportunity() {
        return ((age >= MIN_AGES_TO_GIVE_BIRTH && age <= MAX_AGES_TO_GIVE_BIRTH &&
                agesAfterChildbirth >= YEARS_BETWEEN_BIRTHS)) && satiety >= MAX_SATIETY / 2;
    }

    protected abstract Human giveBirth(Texture texture);

    protected Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(satietyLineTexture, position.x, position.y + getBounds().height + 2,
                getBounds().width * (satiety / MAX_SATIETY), 5);
        batch.draw(foodLineTexture, position.x, position.y + getBounds().height + 7,
                getBounds().width * ((float) foodCount / (float) MAX_FOOD_COUNT), 2);
        batch.draw(texture, position.x, position.y, getBounds().width, getBounds().height);
    }

    @Override
    public Vector2 getCenterPosition() {
        float x = getPosition().x + HUMAN_WIDTH / 2;
        float y = getPosition().y + HUMAN_HEIGHT / 2;
        return new Vector2(x, y);
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

    public Vector2 getHome() {
        return home;
    }

    public void setHome(Vector2 home) {
        this.home = home;
    }

    public int getFoodCount() {
        return foodCount;
    }

    public void addFood(int foodCount) {
        this.foodCount += foodCount;
    }

    public void minusFood(int foodCount) {
        this.foodCount -= foodCount;
    }

    public float getACCELERATION() {
        return ACCELERATION;
    }

    public abstract void debug(ShapeRenderer shapeRenderer, OrthographicCamera apiCam);

    @Override
    public String toString() {
        return "Human{" +
                ", METABOLISM=" + METABOLISM +
                ", satiety=" + satiety +
                ", age=" + age +
                ", agesAfterChildbirth=" + agesAfterChildbirth +
                '}';
    }
}
