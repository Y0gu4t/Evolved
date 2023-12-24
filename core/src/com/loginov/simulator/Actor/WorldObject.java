// Everything is ready
package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/** A class describing any object used in the simulation
 * @author loginov0203@gmail.com */
public abstract class WorldObject {
    protected Vector2 position;
    private final Rectangle bounds;
    protected Texture texture;

    /** Constructs an object with given position and dimensions */
    public WorldObject(Texture texture, float x, float y, float width, float height) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x - width / 2, y - height / 2, width, height);
        this.texture = texture;
    }

    public Vector2 getPosition() {
        return position;
    }

    public abstract Vector2 getCenterPosition();

    public Rectangle getBounds() {
        return bounds;
    }

    public Texture getTexture() {
        return texture;
    }
}
