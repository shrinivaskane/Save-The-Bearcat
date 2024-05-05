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

public class CreditsScreen extends BaseScreen {
    private final Stage stage;
    private final Skin skin;
    private final Image background;

    public CreditsScreen(final MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(640, 360));
        Texture backgroundTexture = game.getManager().get("nature_background.png", Texture.class);
        background = new Image(backgroundTexture);
        background.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(background);

        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        Label credits = new Label("Save The Bearcat v1.0.0\n" +
                "Developed by Shrinivas Nilesh Pai Kane\n" +
                "Feedback: shrinivaskane@gmail.com \n\n", skin);

        Texture buttonTexture = new Texture(Gdx.files.internal("button_background.png"));
        NinePatch buttonBackground = new NinePatch(new TextureRegion(buttonTexture), 24, 24, 24, 24);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = new BitmapFont();
        buttonStyle.up = new NinePatchDrawable(buttonBackground);
        buttonStyle.down = new NinePatchDrawable(buttonBackground);
        buttonStyle.fontColor = Color.WHITE;

        TextButton back = new TextButton("Back", buttonStyle);
        back.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });
        back.setSize(200, 80);
        back.setPosition((stage.getWidth() - back.getWidth()) / 2, 50);

        credits.setPosition(20, 340 - credits.getHeight());
        stage.addActor(back);
        stage.addActor(credits);
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
