package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Enums.HumanState;
import com.loginov.simulator.Enums.SimulationState;
import com.loginov.simulator.Screen.SimulatorScreen;
import com.loginov.simulator.util.FoodGenerator;
import com.loginov.simulator.util.HumanGenerator;
import com.loginov.simulator.util.SimulationParams;

/**
 * Main class considered in simulation
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
    protected Clan clan;
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
        satietyLineTexture = createTexture((int) bounds().width, 10, SimulationParams.getHumanColors().get(Warrior.class));
        foodLineTexture = createTexture((int) bounds().width, 10, Color.ORANGE);
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
                    this.state = HumanState.GO_HOME;
                /*else if (this.giveBirthOpportunity()) {
                    humanGenerator.prepareChild(giveBirth(texture));
                    agesAfterChildbirth = 0;
                }*/
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
        velocity = new Vector2(dx, dy);
    }

    /**
     * update the human's position considering satiety, metabolism,
     * and simulation's speed values
     * y = -ax^2 + bx
     */
    public void update() {
        float param = SimulatorScreen.simulationSpeed * METABOLISM * 4;
        float speed = (float) (-Math.pow((1 - satiety / MAX_SATIETY), 2) + (1 - satiety / MAX_SATIETY));
        speed = state == HumanState.GO_HOME ? speed + 0.1f : speed;
        position.add(velocity.x * param * speed, velocity.y * param * speed);
    }

    public void findNearestHome() {
        float x = position.x - clan.getTerritory().center.x;
        float y = position.y - clan.getTerritory().center.y;
        float angleToPoint = MathUtils.atan2(y, x) * MathUtils.radDeg;
        if (angleToPoint < 0 && clan.getTerritory().getStart() > 0) { angleToPoint += 360f; }
        float angle = MathUtils.clamp(angleToPoint,
                clan.getTerritory().getStart() + 5f,
                clan.getTerritory().getEnd() - 5f) % 360f;
        float xHome = clan.getTerritory().center.x +
                (clan.getTerritory().radius - Human.getHumanWidth() / 2) * MathUtils.cosDeg(angle);
        float yHome = clan.getTerritory().center.y +
                (clan.getTerritory().radius - Human.getHumanHeight() / 2) * MathUtils.sinDeg(angle);
        home = new Vector2(xHome, yHome);
    }

    public void goHome() {
        if (Math.sqrt(Math.pow(position.x - home.x, 2) + Math.pow(position.y - home.y, 2)) < 3 * SimulatorScreen.simulationSpeed) {
            state = HumanState.AT_HOME;
            giveFoodToClan();
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

    protected Texture createTexture(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fillRectangle(0, 0, width, height);
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }

    protected void giveFoodToClan() {
        clan.addFood(foodCount);
        foodCount = 0;
    }

    public void eat(int foodFromClan) {
        int requiredAmountOfFood = Math.min(foodFromClan, (int) ((MAX_SATIETY - satiety) / Food.getSatiety()));
        this.setSatiety(requiredAmountOfFood * Food.getSatiety());
        clan.takeFood(requiredAmountOfFood);
    }

    public void joinClan(){
        clan.addMember(this);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(satietyLineTexture, position.x, position.y + bounds().height + 4,
                bounds().width * (satiety / MAX_SATIETY), 5);
        batch.draw(foodLineTexture, position.x, position.y + bounds().height + 9,
                bounds().width * ((float) foodCount / (float) MAX_FOOD_COUNT), 4);
        batch.draw(texture, position.x, position.y, bounds().width, bounds().height);
    }

    public float getPolarAngle() {
        float angle = MathUtils.atan2(getCenterPosition().y - clan.getTerritory().center.y,
                getCenterPosition().x - clan.getTerritory().center.x);
        if (angle < 0) {
            angle += MathUtils.PI2;
        }
        return angle;
    }

    public float getPolarAngleDeg() {
        return getPolarAngle() * MathUtils.radDeg;
    }

    public float getSatiety() {
        return satiety;
    }

    public void setSatiety(float satiety) {
        this.satiety = Math.min(this.satiety + satiety, MAX_SATIETY);
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

    public void setAgesAfterChildbirth(int agesAfterChildbirth) {
        this.agesAfterChildbirth = agesAfterChildbirth;
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

    public Clan getClan() {
        return clan;
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
