package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class LoadingScreen extends BaseScreen {

    private final Stage stage;

    private final Skin skin;

    private final Label loading;

    public LoadingScreen(MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(640, 360));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Texture loadingTexture = new Texture(Gdx.files.internal("loadingscreen.png"));
        Image loadingImage = new Image(loadingTexture);
        loadingImage.setFillParent(true);

        stage.addActor(loadingImage);

        loading = new Label("Loading...", skin);
        float labelX = (stage.getWidth() - loading.getWidth()) / 2;
        float labelY = (stage.getHeight() - loading.getHeight()) / 2 - 100;
        loading.setPosition(labelX, labelY);
        stage.addActor(loading);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.getManager().update()) {
            game.finishLoading();
        } else {
            int progress = (int) (game.getManager().getProgress() * 100);
            loading.setText("Loading... " + progress + "%");
        }
        stage.act();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
