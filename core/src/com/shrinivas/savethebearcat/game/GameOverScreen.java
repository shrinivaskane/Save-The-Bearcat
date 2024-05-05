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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class GameOverScreen extends BaseScreen {
    private final Stage stage;
    private final Skin skin;

    public GameOverScreen(final MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(640, 360));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        Texture buttonTexture = new Texture(Gdx.files.internal("button_background.png"));
        NinePatch buttonBackground = new NinePatch(new TextureRegion(buttonTexture), 24, 24, 24, 24);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.up = new NinePatchDrawable(buttonBackground);
        buttonStyle.down = new NinePatchDrawable(buttonBackground);
        buttonStyle.fontColor = Color.WHITE;

        TextButton retry = new TextButton("Retry", buttonStyle);
        TextButton menu = new TextButton("Menu", buttonStyle);

        Image background = new Image(game.getManager().get("nature_background.png", Texture.class));
        Image gameover = new Image(game.getManager().get("gameover.png", Texture.class));
        retry.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resetGame();
                game.setScreen(game.gameScreen);
            }
        });

        menu.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.resetGame();
                game.setScreen(game.menuScreen);
            }
        });

        background.setFillParent(true);
        float gameoverX = (640 - gameover.getWidth()) / 2 + 50;
        float padding = 10;
        float gameoverY = retry.getY() + retry.getHeight() + padding;

        gameover.setPosition(gameoverX, gameoverY);
        gameover.setSize(250, 150);
        retry.setSize(200, 80);
        menu.setSize(200, 80);
        retry.setPosition(60, 50);
        menu.setPosition(380, 50);

        stage.addActor(background);
        stage.addActor(retry);
        stage.addActor(gameover);
        stage.addActor(menu);
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
        skin.dispose();
        stage.dispose();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();
    }
}
