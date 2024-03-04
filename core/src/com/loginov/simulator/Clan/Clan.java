package com.loginov.simulator.Clan;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.loginov.simulator.Actor.Collector;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Actor.Thief;
import com.loginov.simulator.Actor.Warrior;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clan {
    private ArrayList<Human> members;
    private int foodStorage;
    private Sector territory;
    private Clan rightEnemy;
    private Clan leftEnemy;
    private Map<Class<? extends Human>, Float> humansRatioMap;

    public Clan(ArrayList<Human> members, int foodStorage, Sector territory) {
        this.members = members;
        this.foodStorage = foodStorage;
        this.territory = territory;
    }

    public Clan(Sector territory) {
        members = new ArrayList<>();
        humansRatioMap = new HashMap<>();
        foodStorage = 0;
        this.territory = territory;
    }

    public Clan getRightEnemy() {
        return rightEnemy;
    }

    public void setRightEnemy(Clan rightEnemy) {
        this.rightEnemy = rightEnemy;
    }

    public Clan getLeftEnemy() {
        return leftEnemy;
    }

    public void setLeftEnemy(Clan leftEnemy) {
        this.leftEnemy = leftEnemy;
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

    public void addFood(int foodAmount) {
        foodStorage += foodAmount;
    }

    public void takeFood(int foodAmount) {
        foodStorage -= foodAmount;
    }

    public void feedMembers() {
        if (members.size() > 0) {
            int foodForEach = foodStorage / members.size();
            for (Human human : members) {
                human.eat(foodForEach);
            }

            for (Human human :
                    members) {
                if (foodStorage > 0) {
                    if (human.getSatiety() < 50f) {
                        human.eat(1);
                    }
                } else break;
            }
        }
    }

    public List<Integer> getMembersTypeCount() {
        int collectorCount = 0;
        int thiefCount = 0;
        int warriorCount = 0;
        for (Human h : members) {
            if (h.getClass().equals(Collector.class)) {
                collectorCount++;
            } else if (h.getClass().equals(Warrior.class)) {
                warriorCount++;
            } else if (h.getClass().equals(Thief.class)) {
                thiefCount++;
            }
        }
        return Arrays.asList(collectorCount, thiefCount, warriorCount);
    }

    public void definePeopleRatio(float collectorRatio, float thiefRatio, float warriorRatio) {
        collectorRatio = (float) (Math.round(collectorRatio * Math.pow(10, 2)) / Math.pow(10, 2));
        thiefRatio = (float) (Math.round(thiefRatio * Math.pow(10, 2)) / Math.pow(10, 2));
        warriorRatio = (float) (Math.round(warriorRatio * Math.pow(10, 2)) / Math.pow(10, 2));
        humansRatioMap.put(Collector.class, collectorRatio);
        humansRatioMap.put(Thief.class, thiefRatio);
        humansRatioMap.put(Warrior.class, warriorRatio);
    }

    public Map<Class<? extends Human>, Integer> classifyHumansByType(int totalMembers) {
        Map<Class<? extends Human>, Integer> humansByTypesMap = new HashMap<>();
        int collectorMembers = (int) (humansRatioMap.get(Collector.class) * totalMembers);
        int thiefMembers = (int) (humansRatioMap.get(Thief.class) * totalMembers);
        int warriorMembers = (int) (humansRatioMap.get(Warrior.class) * totalMembers);

        int remainingMembers = totalMembers - (collectorMembers + thiefMembers + warriorMembers);
        float randomChance = MathUtils.random();
        float collectorChance = humansRatioMap.get(Collector.class);
        float thiefChance = humansRatioMap.get(Thief.class);

        for (int i = 0; i < remainingMembers; i++) {
            if (randomChance < collectorChance) {
                collectorMembers++;
            } else if (randomChance < collectorChance + thiefChance) {
                thiefMembers++;
            } else {
                warriorMembers++;
            }
        }

        humansByTypesMap.put(Collector.class, collectorMembers);
        humansByTypesMap.put(Thief.class, thiefMembers);
        humansByTypesMap.put(Warrior.class, warriorMembers);

        return humansByTypesMap;
    }

    public void remove(Human human) {
        members.remove(human);
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(territory.color);
        shapeRenderer.arc(territory.center.x, territory.center.y, territory.radius, territory.getStart(), territory.angle);
        shapeRenderer.end();
    }
}
