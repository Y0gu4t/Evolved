package com.loginov.simulator.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;

public abstract class UnitGenerator {
    protected float minAreaSize;
    protected float maxAreaSize;
    protected float minDistance;
    protected int amountArea;
    protected Group group;
    protected ArrayList<Circle> areas;

    protected UnitGenerator(Group group) {
        this.group = group;
        areas = new ArrayList<>();
    }

    /**
     * create several (amountArea) circular area
     * with random coordinates and size
     * in the group
     *
     * @param group - simulation screen area
     */
    public void generateAreas(Group group) {
        if (amountArea == 1) {
            generateArea(group);
            return;
        }
        for (int i = 0; i < amountArea; i++) {
            // randomize new area
            float areaSize = MathUtils.random(minAreaSize, maxAreaSize);
            Circle circleArea = new Circle();
            circleArea.setRadius(areaSize);
            circleArea.setPosition(MathUtils.random(group.getX() + circleArea.radius, group.getWidth() - circleArea.radius),
                    MathUtils.random(group.getY() + circleArea.radius, group.getHeight() - circleArea.radius));

            boolean isValid = false;
            // TODO: check that the new area is at the right distance from the rest
            while (!isValid) {
                isValid = true;
                for (Circle area : areas) {
                    float distance = (float) Math.sqrt(Math.pow(circleArea.x - area.x, 2) +
                            Math.pow(circleArea.y - area.y, 2));
                    if (distance < minDistance) {
                        isValid = false;
                        circleArea.setPosition(MathUtils.random(group.getX() + circleArea.radius, group.getWidth() - circleArea.radius),
                                MathUtils.random(group.getY() + circleArea.radius, group.getHeight() - circleArea.radius));
                        break;
                    }
                }
            }
            areas.add(circleArea);
        }
    }

    /**
     * Create one large circular area in the center of the group
     *
     * @param group - simulation screen area
     */
    private void generateArea(Group group) {
        float radius = Math.min(group.getWidth(), group.getHeight()) / 2f;
        Circle circleArea = new Circle();
        circleArea.setRadius(radius);
        circleArea.setPosition((group.getX() + group.getWidth() / 2), (group.getY() + group.getHeight() / 2));
        areas.add(circleArea);
    }

    public void generate(int count, ResourceManager resourceManager) {
        for (int i = 0; i < count; i++) {
            defineArea(resourceManager);
        }
    }

    protected abstract void defineArea(ResourceManager resourceManager);

    public ArrayList<Circle> getAreas() {
        return areas;
    }
}
