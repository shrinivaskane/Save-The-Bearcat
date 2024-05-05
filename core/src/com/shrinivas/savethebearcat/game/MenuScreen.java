package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen extends BaseScreen {
    private final Stage stage;
    private Image background, logo, mascot;
    private ImageButton playButton, leaderboardButton, rulesButton, creditsButton;

    public MenuScreen(final MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Gdx.input.setInputProcessor(stage);

        loadAssets();
        setupLayout();
        addButtonListeners();
    }

    private void loadAssets() {
        background = new Image(new Texture("nature_background.png"));
        logo = new Image(new Texture("savethebearcat.png"));
        mascot = new Image(new Texture("bearcat.png"));
        playButton = createButton("play_button.png");
        leaderboardButton = createButton("leaderboard_button.png");
        rulesButton = createButton("rules_button.png");
        creditsButton = createButton("credits_button.png");
    }

    private void setupLayout() {
        background.setFillParent(true);
        stage.addActor(background);

        float screenWidth = stage.getWidth();
        float screenHeight = stage.getHeight();

        float logoWidth = screenWidth * 0.6f;
        float logoHeight = logoWidth * 0.20f;
        logo.setSize(logoWidth, logoHeight);
        logo.setPosition((screenWidth - logoWidth) / 2, screenHeight - logoHeight - 20);

        float mascotSize = screenWidth * 0.1f;
        mascot.setSize(mascotSize, mascotSize);
        mascot.setPosition((screenWidth - mascotSize) / 2, logo.getY() - mascotSize - 10);

        float buttonSize = screenWidth * 0.13f;
        float spacing = 30;

        float buttonStartX = (screenWidth - 2 * buttonSize - spacing) / 2;
        float buttonStartY = mascot.getY() - mascotSize - spacing;

        setupButton(playButton, buttonStartX, buttonStartY, buttonSize);
        setupButton(leaderboardButton, buttonStartX + buttonSize + spacing, buttonStartY, buttonSize);
        setupButton(rulesButton, buttonStartX, buttonStartY - buttonSize - spacing, buttonSize);
        setupButton(creditsButton, buttonStartX + buttonSize + spacing, buttonStartY - buttonSize - spacing, buttonSize);

        stage.addActor(logo);
        stage.addActor(mascot);
    }

    private void setupButton(ImageButton button, float x, float y, float size) {
        button.setSize(size, size);
        button.setPosition(x, y);
        stage.addActor(button);
    }

    private ImageButton createButton(String upImage) {
        Texture upTexture = new Texture(Gdx.files.internal(upImage));
        ImageButton.ImageButtonStyle buttonStyle = new ImageButton.ImageButtonStyle();
        buttonStyle.up = new TextureRegionDrawable(upTexture);
        buttonStyle.down = new TextureRegionDrawable(upTexture).tint(Color.LIGHT_GRAY);
        return new ImageButton(buttonStyle);
    }

    private void addButtonListeners() {
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.gameScreen);
            }
        });

        leaderboardButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.leaderboardScreen);
            }
        });

        rulesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.rulesScreen);
            }
        });

        creditsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.creditsScreen);
            }
        });
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
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }
}