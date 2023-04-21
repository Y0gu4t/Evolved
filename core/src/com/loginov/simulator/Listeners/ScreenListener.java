package com.loginov.simulator.Listeners;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.loginov.simulator.Evolved;


public class ScreenListener  extends ClickListener { // доработать до общего листенера
    private Evolved api;
    private Screen thisScreen;
    private Screen newScreen;

    public ScreenListener(Screen thisScreen,Screen newScreen, final Evolved api){
        this.thisScreen = thisScreen;
        this.api = api;
        this.newScreen = newScreen;
    }
    @Override
    public void clicked(InputEvent event, float x, float y) {
        thisScreen.dispose();
        System.out.println("clicked");
        api.setScreen(newScreen);

    }


}

