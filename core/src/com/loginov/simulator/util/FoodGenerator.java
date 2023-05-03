package com.loginov.simulator.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Food;

import java.util.ArrayList;

public class FoodGenerator extends UnitGenerator{
    private ArrayList<Food> food;

    public FoodGenerator(Group group){
        super(group);
        this.minAreaSize = SimulationParams.getMinFoodAreaSize();
        this.maxAreaSize = SimulationParams.getMaxFoodAreaSize();
        this.minDistance = SimulationParams.getMinFoodAreaDistance();
        this.amountArea = SimulationParams.getFoodAreas();
        food = new ArrayList<>();
        generateAreas(group);
    }

    @Override
    protected void defineArea(ResourceManager resourceManager){
        int areaId = MathUtils.random(areas.size()-1);
        float minX = areas.get(areaId).x + Food.getFoodWidth()/2;
        float maxX = minX + areas.get(areaId).width - Food.getFoodWidth();
        float minY = areas.get(areaId).y + Food.getFoodHeight()/2;
        float maxY = minY + areas.get(areaId).height - Food.getFoodHeight();
        food.add(new Food(resourceManager.foodTexture, MathUtils.random(minX, maxX), MathUtils.random(minY, maxY)));
    }

    public void removeFood(Food f){
        food.remove(f);
    }

    public ArrayList<Food> getFood() {
        return food;
    }
}
