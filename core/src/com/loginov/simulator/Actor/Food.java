package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Food extends WorldObject {
    private static float satiety = 20f;
    private static final float FOOD_WIDTH = 15f;
    private static final float FOOD_HEIGHT = 15f;

    public Food(Texture texture, float x, float y) {
        super(texture, x, y, FOOD_WIDTH, FOOD_HEIGHT);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), position.x, position.y, bounds().width, bounds().height);
    }

    public static float getSatiety() {
        return satiety;
    }

    public static float getFoodWidth() {
        return FOOD_WIDTH;
    }

    public static float getFoodHeight() {
        return FOOD_HEIGHT;
    }

    @Override
    public String toString() {
        return "Food{" +
                "satiety=" + satiety +
                '}';
    }
}
