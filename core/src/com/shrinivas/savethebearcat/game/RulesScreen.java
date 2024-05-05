package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RulesScreen extends BaseScreen {

    private final Stage stage;
    private final Skin skin;
    private final Image background;

    public RulesScreen(final MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(640, 360));
        Texture backgroundTexture = game.getManager().get("nature_background.png", Texture.class);
        background = new Image(backgroundTexture);
        stage.addActor(background);
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Label rulesLabel = new Label("Gameplay Mechanics:\n" +
                "● Character: Players control a bearcat, an adaptable and resilient mascot, emblematic of agility and strength.\n" +
                "● Objective: The primary goal is to jump over obstacles to survive as long as possible.\n" +
                "● Scoring System: The score is a reflection of the bearcat’s survival time, with points incrementing for each second the bearcat avoids obstacles.\n", skin);
        rulesLabel.setWrap(true);
        rulesLabel.setColor(Color.WHITE);
        float margin = 40; // Horizontal margin
        rulesLabel.setWidth(stage.getWidth() - 2 * margin);
        float requiredHeight = rulesLabel.getPrefHeight();
        rulesLabel.setHeight(requiredHeight);

        float yPos = stage.getHeight() - requiredHeight - margin;
        rulesLabel.setPosition(margin, yPos);

        Texture buttonTexture = new Texture(Gdx.files.internal("button_background.png"));
        NinePatch buttonBackground = new NinePatch(new TextureRegion(buttonTexture), 24, 24, 24, 24);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.up = new NinePatchDrawable(buttonBackground);
        buttonStyle.down = new NinePatchDrawable(buttonBackground);
        buttonStyle.fontColor = skin.getColor("white");

        TextButton backButton = new TextButton("Back", buttonStyle);
        backButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });

        backButton.setSize(200, 80);
        backButton.setPosition((stage.getWidth() - backButton.getWidth()) / 2, 50);
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(backButton);
        stage.addActor(rulesLabel);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }

    @Override
    public void render(float delta) {
        background.setPosition(stage.getCamera().position.x - stage.getWidth() / 2, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}