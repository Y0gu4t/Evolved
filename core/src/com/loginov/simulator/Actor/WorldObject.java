// Everything is ready
package com.loginov.simulator.Actor;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * A class describing any object used in the simulation
 *
 * @author loginov0203@gmail.com
 */
public abstract class WorldObject {
    protected Vector2 position;
    private final Rectangle bounds;
    protected Texture texture;

    /**
     * Constructs an object with given position and dimensions
     */
    public WorldObject(Texture texture, float x, float y, float width, float height) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x - width / 2, y - height / 2, width, height);
        this.texture = texture;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(float x, float y) {
        position = new Vector2(x, y);
    }

    public void setPosition(Vector2 position) {
        this.position = new Vector2(position);
    }

    public Vector2 getCenterPosition() {
        return new Vector2(position.x + bounds.width / 2, position.y + bounds.height / 2);
    }

    public Rectangle bounds() {
        return bounds;
    }

    public Texture getTexture() {
        return texture;
    }
}
