package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class MainGame extends Game {

    private AssetManager manager;
    public BaseScreen loadingScreen, menuScreen, gameScreen, gameOverScreen, creditsScreen, rulesScreen, leaderboardScreen;

    @Override
    public void create() {
        manager = new AssetManager();

        manager.load("nature_background.png", Texture.class);
        manager.load("nature_level1.jpg", Texture.class);
        manager.load("nature_level2.jpg", Texture.class);
        manager.load("nature_level3.jpg", Texture.class);
        manager.load("play_button.png", Texture.class);
        manager.load("leaderboard_button.png", Texture.class);
        manager.load("rules_button.png", Texture.class);
        manager.load("credits_button.png", Texture.class);
        manager.load("savethebearcat.png", Texture.class);
        manager.load("bearcat.png", Texture.class);
        manager.load("gameover.png", Texture.class);
        manager.load("player.png", Texture.class);
        manager.load("audio/player_dead.ogg", Sound.class);
        manager.load("audio/player_jump.ogg", Sound.class);
        manager.load("audio/background_sound.ogg", Music.class);
        manager.load("nature_spike.png", Texture.class);
        manager.load("nature_floor.png", Texture.class);
        manager.load("nature_overfloor.png", Texture.class);
        manager.load("bonus.png", Texture.class);


        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
    }

    public void finishLoading() {
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        rulesScreen = new RulesScreen(this);
        gameOverScreen = new GameOverScreen(this);
        creditsScreen = new CreditsScreen(this);
        leaderboardScreen = new LeaderboardScreen(this);
        setScreen(menuScreen);
    }

    public AssetManager getManager() {
        return manager;
    }

    public void resetGame() {
        if (menuScreen != null) {
            menuScreen.dispose();
        }
        if (gameScreen != null) {
            gameScreen.dispose();
        }
        if (gameOverScreen != null) {
            gameOverScreen.dispose();
        }
        if (creditsScreen != null) {
            creditsScreen.dispose();
        }
        if (leaderboardScreen != null) {
            leaderboardScreen.dispose();
        }

        loadingScreen = new LoadingScreen(this);
        menuScreen = new MenuScreen(this);
        gameScreen = new GameScreen(this);
        gameOverScreen = new GameOverScreen(this);
        creditsScreen = new CreditsScreen(this);
        leaderboardScreen = new LeaderboardScreen(this);

        setScreen(menuScreen);
    }

}
