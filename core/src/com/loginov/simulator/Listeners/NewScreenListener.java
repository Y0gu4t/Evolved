package com.loginov.simulator.Listeners;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.loginov.simulator.Evolved;
import com.loginov.simulator.Screen.InputApiAdapter;


public class NewScreenListener<T extends InputApiAdapter> extends ClickListener { // доработать до общего листенера
    private Evolved api;
    private Screen thisScreen;
    private T newScreen;

    public NewScreenListener(Screen thisScreen,T newScreen, Evolved api){
        this.thisScreen = thisScreen;
        this.api = api;
        this.newScreen = newScreen;
    }
    @Override
    public void clicked(InputEvent event, float x, float y) {
        api.setScreen(newScreen);
        thisScreen.dispose();
    }


}
