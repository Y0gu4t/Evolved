package com.loginov.simulator.actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/** The class describing dynamic objects used in the simulation
 * @author loginov0203@gmail.com
 * */
public abstract class DynamicWorldObject extends WorldObject {
    protected Vector2 velocity;

    public DynamicWorldObject(Texture texture, float x, float y, float width, float height) {
        super(texture, x, y, width, height);
        velocity = new Vector2(0, 0);
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public DynamicWorldObject velocity(Vector2 velocity) {
        this.velocity = velocity;
        return this;
    }
}
