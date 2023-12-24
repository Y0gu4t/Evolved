package com.loginov.simulator.Actor.Interfaces;

import com.loginov.simulator.Actor.Food;
import com.loginov.simulator.util.FoodGenerator;

import java.util.ArrayList;

public interface Collect {

    public void findFood(ArrayList<Food> food);
    public void collect(FoodGenerator foodGenerator);

    public Food getFoodToEat();
}

