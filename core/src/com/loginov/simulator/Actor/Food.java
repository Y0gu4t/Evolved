package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Food extends WorldObject {
    private static float satiety = 20f;
    private static final float FOOD_WIDTH = 10f;
    private static final float FOOD_HEIGHT = 10f;

    public Food(Texture texture, float x, float y) {
        super(texture, x, y, FOOD_WIDTH, FOOD_HEIGHT);
    }

    public void draw(SpriteBatch batch) {
        batch.draw(getTexture(), getPosition().x, getPosition().y, getBounds().width, getBounds().height);
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

    @Override
    public Vector2 getCenterPosition() {
        float x = getPosition().x + FOOD_WIDTH / 2;
        float y = getPosition().y + FOOD_HEIGHT / 2;
        return new Vector2(x, y);
    }
}
