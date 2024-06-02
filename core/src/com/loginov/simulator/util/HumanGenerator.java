package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.loginov.simulator.actor.Collector;
import com.loginov.simulator.actor.Human;
import com.loginov.simulator.actor.Thief;
import com.loginov.simulator.actor.Warrior;
import com.loginov.simulator.clan.Clan;
import com.loginov.simulator.clan.Sector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HumanGenerator extends UnitGenerator {
    private final ArrayList<Human> humans;
    private final ArrayList<Human> children;

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
     * Select a random area from the available ones and
     * position the person at the edge of this area
     */
    private void defineArea(Clan clan, Texture texture, String humanType) {
        Sector territory = clan.getTerritory();
        float randomAngle = MathUtils.random(territory.getStart() * MathUtils.degreesToRadians,
                territory.getEnd() * MathUtils.degreesToRadians);
        float x = territory.center.x - Human.getHumanWidth() / 2 + (territory.radius - Human.getHumanHeight()) * MathUtils.cos(randomAngle);
        float y = territory.center.y - Human.getHumanHeight() / 2 + (territory.radius - Human.getHumanHeight()) * MathUtils.sin(randomAngle);
        Human human;
        switch (humanType) {
            case "collector":
                human = new Collector(texture, x, y, clan, SimulationParams.getMETABOLISM());
                break;
            case "warrior":
                human = new Warrior(texture, x, y, clan, SimulationParams.getMETABOLISM());
                break;
            case "thief":
                human = new Thief(texture, x, y, clan, SimulationParams.getMETABOLISM());
                break;
            default:
                human = null;
        }
        if (human != null) {
            humans.add(human);
            human.joinClan();
        } else {
            System.err.println("It was not possible to create a human because he is null");
        }
    }

    public void generate(ClanFactory clanFactory, ResourceManager resourceManager) {
        int clanCounter = 0;
        for (Clan clan : clanFactory.getClans()) {
            int warriorCount = SimulationParams.getClanList().get(clanCounter).getWarriorCount();
            int collectorCount = SimulationParams.getClanList().get(clanCounter).getCollectorCount();
            int thiefCount = SimulationParams.getClanList().get(clanCounter).getThiefCount();
            for (int i = 0; i < warriorCount; i++) {
                defineArea(clan, resourceManager.getClanTexture(clan), "warrior");
            }
            for (int i = 0; i < collectorCount; i++) {
                defineArea(clan, resourceManager.getClanTexture(clan), "collector");
            }
            for (int i = 0; i < thiefCount; i++) {
                defineArea(clan, resourceManager.getClanTexture(clan), "thief");
            }
            float allClanMembers = warriorCount + collectorCount + thiefCount;
            clan.definePeopleRatio(collectorCount / allClanMembers,
                    thiefCount / allClanMembers,
                    warriorCount / allClanMembers);
            clanCounter++;
        }
    }

    public void prepareChild(ResourceManager resourceManager, Clan clan, Class<? extends Human> humanClass) {
        Circle area = areas.get(0);
        Human human = null;
        Sector territory = clan.getTerritory();
        float randomAngle = MathUtils.random(0, territory.getHalfAngle()) * MathUtils.randomSign();
        float angle = territory.getMiddle() + randomAngle;
        float x = area.x - Human.getHumanWidth() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.cosDeg(angle);
        float y = area.y - Human.getHumanHeight() / 2 + (area.radius - Human.getHumanHeight()) * MathUtils.sinDeg(angle);

        if (humanClass.equals(Collector.class)) {
            human = new Collector(resourceManager.getClanTexture(clan), x, y, clan, SimulationParams.getMETABOLISM());
        } else if (humanClass.equals(Thief.class)) {
            human = new Thief(resourceManager.getClanTexture(clan), x, y, clan, SimulationParams.getMETABOLISM());
        } else if (humanClass.equals(Warrior.class)) {
            human = new Warrior(resourceManager.getClanTexture(clan), x, y, clan, SimulationParams.getMETABOLISM());
        }
        if (human != null) {
            children.add(human);
            human.joinClan();
        } else {
            System.err.println("It was not possible to create a child because he is null");
        }
    }

    @Deprecated
    public void addChildren() {
        humans.addAll(children);
        children.clear();
    }

    public void addChildren(ResourceManager resourceManager, Clan clan, Map<Class<? extends Human>, Integer> humanByTypeMap) {
        for (Class<? extends Human> humanClass :
                humanByTypeMap.keySet()) {
            for (int i = 0; i < humanByTypeMap.get(humanClass); i++) {
                prepareChild(resourceManager, clan, humanClass);
            }
        }
        humans.addAll(children);
        children.clear();
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

    public boolean children() {
        return children.size() > 0;
    }
}
