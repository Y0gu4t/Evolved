package com.loginov.simulator.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;

import java.util.List;
import java.util.TreeMap;

public class DiagramDrawer {
    private TreeMap<Integer, List<Integer>> pointsMap;

    public DiagramDrawer() {
        pointsMap = new TreeMap<>();
    }

    public void drawDiagram(ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, ResourceManager resourceManager, Group group) {
        drawCoordinateScaling(shapeRenderer, group);
        if (pointsMap.isEmpty()) {
            return;
        }
        Vector2 scaling = findScaling(group.getWidth(), group.getHeight() / 2);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < SimulationParams.getClanCount(); i++) {
            float lastPointX = group.getX();
            float lastPointY = group.getHeight() / 2 + pointsMap.get(0).get(i) * scaling.y;

            shapeRenderer.setColor(SimulationParams.getClanList().get(i).getDiagramColor());
            for (int j = 0; j < pointsMap.size(); j++) {
                float x2 = group.getX() + j * scaling.x;
                float y2 = group.getHeight() / 2 + pointsMap.get(j).get(i) * scaling.y;
                shapeRenderer.rectLine(lastPointX, lastPointY, x2, y2, 2);
                lastPointX = x2;
                lastPointY = y2;
            }
        }

        shapeRenderer.end();
        drawDescriptionOfGraph(spriteBatch, resourceManager, group, scaling);
    }

    public void addPoint(int key, List<Integer> list) {
        pointsMap.put(key, list);
    }

    private void drawCoordinateScaling(ShapeRenderer shapeRenderer, Group group) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rectLine(group.getX(), group.getHeight() / 2, group.getX() + group.getWidth(), group.getHeight() / 2, 2);
        shapeRenderer.rectLine(group.getX(), group.getHeight() / 2, group.getX(), group.getHeight(), 2);
        float triangleAbscissaX = group.getX() + group.getWidth();
        float triangleAbscissaY = group.getHeight() / 2;
        float triangleOrdinateX = group.getX();
        float triangleOrdinateY = group.getHeight();
        shapeRenderer.triangle(triangleAbscissaX, triangleAbscissaY, triangleAbscissaX - 15f, triangleAbscissaY + 7f, triangleAbscissaX - 15f, triangleAbscissaY - 7f);
        shapeRenderer.triangle(triangleOrdinateX, triangleOrdinateY, triangleOrdinateX + 7f, triangleOrdinateY - 15f, triangleOrdinateX - 7f, triangleOrdinateY - 15f);
        shapeRenderer.end();
    }

    private void drawDescriptionOfGraph(SpriteBatch spriteBatch, ResourceManager resourceManager, Group group, Vector2 scaling) {
        Integer maxY = pointsMap.get(0).get(0);
        for (int i = 0; i < pointsMap.size(); i++) {
            for (Integer v : pointsMap.get(i)) {
                if (v > maxY) {
                    maxY = v;
                }
            }
        }

        spriteBatch.begin();

        BitmapFont font = resourceManager.toolSkin.getFont("button");

        font.draw(spriteBatch, "days", group.getX() + group.getWidth() / 2, group.getHeight() / 2 - 10);
        font.draw(spriteBatch, "m\ne\nm\nb\ne\nr\ns", group.getX() - 20, 7 * group.getHeight() / 8);
        font.draw(spriteBatch, pointsMap.lastKey().toString(), group.getX() + group.getWidth() - 50, group.getHeight() / 2 - 10);
        font.draw(spriteBatch, maxY.toString(), group.getX() + 20, group.getHeight() - 10);
        spriteBatch.end();
    }

    private Vector2 findScaling(float groupW, float groupH) {
        float maxX = pointsMap.lastKey();
        float maxY = pointsMap.get(0).get(0);
        for (int i = 0; i < pointsMap.size(); i++) {
            for (Integer v : pointsMap.get(i)) {
                if (v > maxY) {
                    maxY = v;
                }
            }
        }
        return new Vector2((groupW - 20) / maxX, (groupH - 30) / maxY);
    }
}
