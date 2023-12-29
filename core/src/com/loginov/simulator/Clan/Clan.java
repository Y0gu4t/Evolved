package com.loginov.simulator.Clan;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.loginov.simulator.Actor.Human;

import java.util.ArrayList;

public class Clan {
    private ArrayList<Human> members;
    private int foodStorage;
    private Sector territory;

    public Clan(ArrayList<Human> members, int foodStorage, Sector territory) {
        this.members = members;
        this.foodStorage = foodStorage;
        this.territory = territory;
    }

    public Clan (Sector territory) {
        members = new ArrayList<>();
        foodStorage = 0;
        this.territory = territory;
    }

    public void addMember(Human human) {
        members.add(human);
    }

    public ArrayList<Human> getMembers() {
        return members;
    }

    public int getFoodStorage() {
        return foodStorage;
    }

    public Sector getTerritory() {
        return territory;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(territory.color);
        shapeRenderer.arc(territory.x, territory.y, territory.radius, territory.start, territory.degree);

        shapeRenderer.end();
    }
}
