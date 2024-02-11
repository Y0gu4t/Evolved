package com.loginov.simulator.Clan;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;
import com.loginov.simulator.Actor.Human;

public final class Sector implements Shape2D {
    public Vector2 center;
    public float radius;
    private final float middle;
    public float angle;

    public Color color;

    public Sector(float x, float y, float radius, float middle, float angle) {
        center = new Vector2(x, y);
        this.radius = radius;
        if (middle < 0f) {
            middle += 360f;
        }
        this.middle = middle;
        this.angle = Math.abs(angle);
    }

    public Sector(float x, float y, float radius, float start, float degree, Color color) {
        this(x, y, radius, start, degree);
        this.color = color;
    }

    public float getEnd() {
        return getStart() + angle;
    }

    public float getStart() {
        float start = middle - getHalfAngle();
        if (start < 0f) {
            start += 360f;
        }
        return start;
    }

    public float getMiddle() {
        return middle;
    }

    public float getAngle() {
        return angle;
    }

    public float getHalfAngle() {
        return angle / 2.0f;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public Vector2 getStartCoordinates() {
        float x = center.x + radius * MathUtils.cosDeg(getStart());
        float y = center.y + radius * MathUtils.sinDeg(getEnd());
        return new Vector2(x, y);
    }

    public Vector2 getEndCoordinates() {
        float x = center.x + radius * MathUtils.cosDeg(getEnd());
        float y = center.y + radius * MathUtils.sinDeg(getEnd());
        return new Vector2(x, y);
    }

    @Override
    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    @Override
    public boolean contains(float x, float y) {
        x = x - center.x;
        y = y - center.y;
        float angle = MathUtils.atan2(y, x);
        if (angle < 0) {
            angle += MathUtils.PI2;
        }
        angle = angle * MathUtils.radDeg;
        return x * x + y * y <= radius * radius && angle - 1.0f < getEnd() && angle + 1.0f > getStart();
    }

    public boolean contains(Human human) {
        float angle = human.getPolarAngleDeg();
        float x = human.getPosition().x - center.x;
        float y = human.getPosition().y - center.y;
        if (angle < 0) {
            angle += 360.0f;
        }
        return x * x + y * y <= radius * radius && angle - 1.0f < getEnd() && angle + 1.0f > getStart();
    }
}
