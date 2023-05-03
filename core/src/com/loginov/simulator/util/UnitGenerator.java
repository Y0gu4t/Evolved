package com.loginov.simulator.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.ArrayList;

public abstract class UnitGenerator {
    protected float minAreaSize;
    protected float maxAreaSize;
    protected float minDistance;
    protected int amountArea;
    protected Group group;
    protected ArrayList<Rectangle> areas;

    protected UnitGenerator(Group group){
        this.group = group;
        areas = new ArrayList<Rectangle>();
    }

    public void generateAreas(com.badlogic.gdx.scenes.scene2d.Group group){
        for(int i=0; i < amountArea; i++){
            // randomize new area
            float areaSize = MathUtils.random(minAreaSize, maxAreaSize);
            Rectangle newArea = new Rectangle();
            newArea.setSize(areaSize);
            newArea.setCenter(MathUtils.random(group.getX() + newArea.getWidth()/2, group.getWidth() - newArea.getWidth()/2),
                    MathUtils.random(group.getY() + newArea.getHeight()/2, group.getHeight() - newArea.getHeight()/2));

            boolean isValid = false;
            // check that the new area is at the right distance from the rest
            while (!isValid) {
                isValid = true;
                for (Rectangle area : areas) {
                    Vector2 tmp = new Vector2();
                    float distance = (float) Math.sqrt(Math.pow(newArea.getCenter(tmp).x - area.getCenter(tmp).x, 2) +
                            Math.pow(newArea.getCenter(tmp).y - area.getCenter(tmp).y, 2));
                    if (distance < minDistance) {
                        isValid = false;
                        newArea.setCenter(MathUtils.random(group.getX() + newArea.getWidth()/2, group.getWidth() - newArea.getWidth()/2),
                                MathUtils.random(group.getY() + newArea.getHeight()/2, group.getHeight() - newArea.getHeight()/2));
                        break;
                    }
                }
            }
            areas.add(newArea);
        }
    }

    public void generate(int count, ResourceManager resourceManager){
        for(int i=0; i<count; i++){
            defineArea(resourceManager);
        }
    }

    protected void defineArea(ResourceManager resourceManager){

    }

    public ArrayList<Rectangle> getAreas() {
        return areas;
    }
}
