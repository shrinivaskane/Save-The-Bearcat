package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardScreen extends BaseScreen {
    private final Stage stage;
    private final Skin skin;
    private final Texture bearcatTexture;
    private final Texture backgroundTexture;
    private final TextButton backButton;

    public LeaderboardScreen(MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(800, 480));
        skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        bearcatTexture = new Texture(Gdx.files.internal("bearcat_score.png"));
        backgroundTexture = new Texture(Gdx.files.internal("leaderboard_background.png"));
        BitmapFont scoreFont = skin.getFont("default-font").newFontCache().getFont();
        scoreFont.getData().setScale(2f);

        Texture buttonTexture = new Texture(Gdx.files.internal("button_background.png"));
        NinePatch buttonBackground = new NinePatch(new TextureRegion(buttonTexture), 24, 24, 24, 24);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.up = new NinePatchDrawable(buttonBackground);
        buttonStyle.down = new NinePatchDrawable(buttonBackground);
        buttonStyle.fontColor = Color.WHITE;

        backButton = new TextButton("Back", buttonStyle);


    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        setupBackground();
        setupBackButton();
        setupUI();

    }

    private void setupBackground() {
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);
    }

    private void setupUI() {
        Table leaderboardTable = new Table();
        leaderboardTable.top();

        Label.LabelStyle headerLabelStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        headerLabelStyle.font.getData().setScale(2f);

        Label.LabelStyle labelStyle = new Label.LabelStyle(skin.getFont("default-font"), Color.WHITE);
        labelStyle.font.getData().setScale(2f);

        leaderboardTable.add(new Label("Rank", labelStyle)).align(Align.left);
        leaderboardTable.add(new Label("", headerLabelStyle)).align(Align.center);
        leaderboardTable.add(new Label("Score", headerLabelStyle)).expand().align(Align.right);
        leaderboardTable.row();

        List<Integer> scores = getTopScores();
        for (int i = 0; i < scores.size(); i++) {
            leaderboardTable.add(new Label(Integer.toString(i + 1), labelStyle)).align(Align.left);
            Image bearcatImage = new Image(bearcatTexture);
            leaderboardTable.add(bearcatImage).size(50, 50).align(Align.center);
            leaderboardTable.add(new Label(Integer.toString(scores.get(i)), labelStyle)).align(Align.right);
            leaderboardTable.row();
        }

        ScrollPane scrollPane = new ScrollPane(leaderboardTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        float scrollPaneHeight = stage.getHeight() - backButton.getHeight() - 20;
        scrollPane.setSize(stage.getWidth(), scrollPaneHeight);
        scrollPane.setPosition(0, backButton.getHeight());

        stage.addActor(scrollPane);
    }

    private List<Integer> getTopScores() {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        List<Integer> scores = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int score = prefs.getInteger("score" + i, 0);
            if (score != 0) {
                scores.add(score);
            }
        }

        return scores;
    }

    private void setupBackButton() {
        Table buttonTable = new Table();
        buttonTable.bottom().padBottom(10);
        buttonTable.setFillParent(true);
        backButton.setSize(200, 80);
        backButton.getLabel().setFontScale(2f);
        buttonTable.add(backButton).size(backButton.getWidth(), backButton.getHeight()).padTop(10).center();
        backButton.addCaptureListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(game.menuScreen);
            }
        });

        stage.addActor(buttonTable);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        stage.clear();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        bearcatTexture.dispose();
        backgroundTexture.dispose();
    }
}
