package com.loginov.simulator.Clan;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Shape2D;
import com.badlogic.gdx.math.Vector2;

public class Sector implements Shape2D {
    public float x, y;
    public float radius;
    public float start;
    public float degree;

    public Color color;

    public Sector(float x, float y, float radius, float start, float degree) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        if (start < 0) {
            start += MathUtils.PI2;
        }
        this.start = start;
        this.degree = Math.abs(degree);
    }

    public Sector(float x, float y, float radius, float start, float degree, Color color) {
        this(x, y, radius, start, degree);
        this.color = color;
    }

    public float getEnd(){
        return start + degree;
    }

    @Override
    public boolean contains(Vector2 point) {
        return contains(point.x, point.y);
    }

    @Override
    public boolean contains(float x, float y) {
        x = this.x - x;
        y = this.y - y;
        float angle = MathUtils.atan2(x, y);
        if (angle < 0) {
            angle += MathUtils.PI2;
        }
        return x * x + y * y <= radius * radius && angle > start + degree && angle < start;
    }
}
