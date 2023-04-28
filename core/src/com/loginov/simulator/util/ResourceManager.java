package com.loginov.simulator.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ResourceManager {
    private AssetManager assetManager;
    private TextureAtlas textureAtlas;
    public Texture humanTexture;
    public Texture foodTexture;
    public Skin toolSkin;
    private final static String TEXTURE_ATLAS_PATH = "flat-earth/skin/flat-earth-ui.atlas";
    private final static String SKIN_PATH = "flat-earth/skin/flat-earth-ui.json";

    public ResourceManager(){
        assetManager = new AssetManager();
        // Atlas
        assetManager.load("flat-earth/skin/flat-earth-ui.atlas", TextureAtlas.class);
        // Images
        assetManager.load("human.png", Texture.class);
        assetManager.load("food.png", Texture.class);

        assetManager.finishLoading();
        // get our resources
        textureAtlas = assetManager.get("flat-earth/skin/flat-earth-ui.atlas", TextureAtlas.class);
        humanTexture = assetManager.get("human.png");
        foodTexture = assetManager.get("food.png");

        // Skin
        toolSkin = new Skin();
        toolSkin.addRegions(textureAtlas);
        toolSkin.load(Gdx.files.internal(SKIN_PATH));
    }



    public void dispose() {
        assetManager.dispose();
        textureAtlas.dispose();
        humanTexture.dispose();
        foodTexture.dispose();
    }
}
