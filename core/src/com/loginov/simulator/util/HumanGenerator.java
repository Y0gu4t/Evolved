package com.loginov.simulator.util;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.Actor.Collector;
import com.loginov.simulator.Actor.Human;
import com.loginov.simulator.Actor.Thief;
import com.loginov.simulator.Actor.Warrior;
import com.loginov.simulator.Clan.Clan;
import com.loginov.simulator.Clan.Sector;

import java.util.ArrayList;

public class HumanGenerator extends UnitGenerator {
    private ArrayList<Human> humans;

    private ArrayList<Human> children;

    public HumanGenerator(Group group) {
        super(group);
        this.minAreaSize = SimulationParams.getMinHumanAreaSize();
        this.maxAreaSize = SimulationParams.getMaxHumanAreaSize();
        this.minDistance = SimulationParams.getMinHumanAreaDistance();
        this.amountArea = SimulationParams.getHumanAreas();
        humans = new ArrayList<>();
        children = new ArrayList<>();
        generateAreas(group);
    }

    @Override
    protected void defineArea(ResourceManager resourceManager) {

    }

    /**
     * select a random area from the available ones and
     * position the person at the edge of this area
     */
    private void defineArea(ResourceManager resourceManager, String human) {
        int areaId = MathUtils.random(areas.size() - 1);
        float randomAngle = MathUtils.random(-MathUtils.PI, MathUtils.PI);
        float radius = areas.get(areaId).radius;
        float x = areas.get(areaId).x - Human.getHumanWidth() / 2 + (radius - Human.getHumanHeight()) * MathUtils.cos(randomAngle);
        float y = areas.get(areaId).y - Human.getHumanHeight() / 2 + (radius - Human.getHumanHeight()) * MathUtils.sin(randomAngle);

        switch (human) {
            case "collector":
                humans.add(new Collector(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
            case "warrior":
                humans.add(new Warrior(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
            case "thief":
                humans.add(new Thief(resourceManager.humanTexture, x, y, SimulationParams.getMETABOLISM()));
                break;
        }
    }

    private void defineArea(Clan clan, ResourceManager resourceManager, String human) {
        Sector territory = clan.getTerritory();
        float randomAngle = MathUtils.random(territory.start * MathUtils.degreesToRadians,
                territory.getEnd() * MathUtils.degreesToRadians);
        float x = territory.x - Human.getHumanWidth() / 2 + (territory.radius - Human.getHumanHeight()) * MathUtils.cos(randomAngle);
        float y = territory.y - Human.getHumanHeight() / 2 + (territory.radius - Human.getHumanHeight()) * MathUtils.sin(randomAngle);

        switch (human) {
            case "collector":
                humans.add(new Collector(resourceManager.humanTexture, x, y, clan, SimulationParams.getMETABOLISM()));
                break;
            case "warrior":
                humans.add(new Warrior(resourceManager.humanTexture, x, y, clan, SimulationParams.getMETABOLISM()));
                break;
            case "thief":
                humans.add(new Thief(resourceManager.humanTexture, x, y, clan, SimulationParams.getMETABOLISM()));
                break;
        }
    }

    public void generate(ClanFactory clanFactory, ResourceManager resourceManager) {
        for (Clan clan : clanFactory.getClans()) {
            for (int i = 0; i < SimulationParams.getWarriorCount(); i++) {
                defineArea(clan, resourceManager, "warrior");
            }
            for (int i = 0; i < SimulationParams.getCollectorCount(); i++) {
                defineArea(clan, resourceManager, "collector");
            }
            for (int i = 0; i < SimulationParams.getThiefCount(); i++) {
                defineArea(clan, resourceManager, "thief");
            }
        }
    }


    public ArrayList<Human> getHumans() {
        return humans;
    }

    public void remove(Human human) {
        humans.remove(human);
    }

    public void add(ArrayList<Human> humans) {
        this.humans.addAll(humans);
    }

    public void add(Human human) {
        humans.add(human);
    }

    public void prepareChildren(Human human) {
        Circle area = null;

        for (Circle circle : areas) {
            if (circle.contains(human.getPosition())) {
                area = new Circle(circle);
                break;
            }
        }

        if (area == null) {
            area = new Circle(areas.get(0));
        }

        float randomAngle = MathUtils.random(MathUtils.PI / 8, MathUtils.PI / 2) *
                (MathUtils.randomBoolean(0.5f) ? 1 : -1);
        float angle = MathUtils.acos(
                (human.getHome().x - area.x + Human.getHumanWidth() / 2)
                        / (area.radius - Human.getHumanHeight())) + randomAngle;
        float x = area.x - Human.getHumanWidth() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.cos(angle);
        float y = area.y - Human.getHumanHeight() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.sin(angle);
        human.setHome(new Vector2(x, y));
        children.add(human);
    }

    public void addChildren() {
        humans.addAll(children);
        children.clear();
    }

    public boolean children() {
        return children.size() > 0;
    }
}
