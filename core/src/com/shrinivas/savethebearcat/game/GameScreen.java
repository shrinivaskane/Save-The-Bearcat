package com.shrinivas.savethebearcat.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.shrinivas.savethebearcat.game.entities.BonusEntity;
import com.shrinivas.savethebearcat.game.entities.EntityFactory;
import com.shrinivas.savethebearcat.game.entities.FloorEntity;
import com.shrinivas.savethebearcat.game.entities.PlayerEntity;
import com.shrinivas.savethebearcat.game.entities.SpikeEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class GameScreen extends BaseScreen {
    private final Stage stage;
    private final World world;
    private PlayerEntity player;
    private final List<FloorEntity> floorList = new ArrayList<>();
    private final List<SpikeEntity> spikeList = new ArrayList<>();
    private final Sound jumpSound;
    private final Sound dieSound;
    private final Music backgroundMusic;
    private final Vector3 position;
    //    private final Image background;
    private float lastSpawnPosition = 0;
    private final EntityFactory entityFactory;
    private final TextButton scoreButton;
    private int score = 0;
    private TextButton levelButton;
    private TextButton wonLabel;

    //private int currentLevel = 1;

    // Add a field to track the current level
    private int currentLevel = 1;
    // Add a field to track the time elapsed
    private float elapsedTime = 0;
    // Add a field to track if the game is won
    private boolean gameWon = false;
    private Image backgroundLevel1;
    private Image backgroundLevel2;
    private Image backgroundLevel3;
    private Texture starTexture;
    private final List<BonusEntity> bonusList = new ArrayList<>();

    Random rand;

    public GameScreen(MainGame game) {
        super(game);
        stage = new Stage(new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        position = new Vector3(stage.getCamera().position);
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));

        world = new World(new Vector2(0, -10), true);
        world.setContactListener(new GameContactListener());

        jumpSound = game.getManager().get("audio/player_jump.ogg");
        dieSound = game.getManager().get("audio/player_dead.ogg");
        backgroundMusic = game.getManager().get("audio/background_sound.ogg");

        Texture backgroundLevel1Texture = game.getManager().get("nature_level1.jpg", Texture.class);
        Texture backgroundLevel2Texture = game.getManager().get("nature_level2.jpg", Texture.class);
        Texture backgroundLevel3Texture = game.getManager().get("nature_level3.jpg", Texture.class);

        // Create images for each background
        backgroundLevel1 = new Image(backgroundLevel1Texture);
        backgroundLevel2 = new Image(backgroundLevel2Texture);
        backgroundLevel3 = new Image(backgroundLevel3Texture);

        // Add them to the stage initially
        stage.addActor(backgroundLevel1);
        stage.addActor(backgroundLevel2);
        stage.addActor(backgroundLevel3);

        Texture buttonTexture = new Texture(Gdx.files.internal("button_background.png"));
        NinePatch buttonBackground = new NinePatch(new TextureRegion(buttonTexture), 24, 24, 24, 24);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.up = new NinePatchDrawable(buttonBackground);
        buttonStyle.down = new NinePatchDrawable(buttonBackground);
        buttonStyle.fontColor = Color.BLACK;

        scoreButton = new TextButton("Score: 0", buttonStyle);
        scoreButton.getLabel().setFontScale(2);
        scoreButton.setSize(500, 300);
        scoreButton.setPosition(Gdx.graphics.getWidth() - scoreButton.getWidth() - 30, Gdx.graphics.getHeight() - scoreButton.getHeight() - 30);
        stage.addActor(scoreButton);
        //stage.addActor(scoreButton);

        // Initialize the "Won the game" label
        wonLabel = new TextButton("Won the game!", buttonStyle);
        wonLabel.getLabel().setFontScale(2);
        wonLabel.setSize(600, 400);
        wonLabel.setPosition(stage.getWidth() / 2 - wonLabel.getWidth() / 2, stage.getHeight() / 2 - wonLabel.getHeight() / 2);
        wonLabel.setVisible(false); // Initially invisible
        stage.addActor(wonLabel);

        starTexture = game.getManager().get("bonus.png", Texture.class);


        levelButton = new TextButton("Level: " + currentLevel, buttonStyle);
        levelButton.getLabel().setFontScale(2);
        levelButton.setSize(500, 300);
        levelButton.setPosition(30, Gdx.graphics.getHeight() - levelButton.getHeight() - 30);
        stage.addActor(levelButton);
        rand = new Random();

        this.entityFactory = new EntityFactory();
        setupGameEntities();
    }

    private void setupGameEntities() {
        Texture bearcatTexture = game.getManager().get("player.png", Texture.class);
        player = entityFactory.createPlayer(world, new Vector2(1.5f, 1.5f), bearcatTexture);
        stage.addActor(player);

        Texture natureFloorTexture = game.getManager().get("nature_floor.png", Texture.class);
        Texture overfloorTexture = game.getManager().get("nature_overfloor.png", Texture.class);
        FloorEntity floor = entityFactory.createFloor(world, 0, 1000, 2, natureFloorTexture, overfloorTexture);
        stage.addActor(floor);

        spawnObstacles();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        stage.getCamera().position.set(position);
        stage.getCamera().update();

        backgroundMusic.setVolume(0.75f);
        backgroundMusic.play();
        backgroundLevel1.setSize(stage.getWidth(), stage.getHeight());
        backgroundLevel2.setSize(stage.getWidth(), stage.getHeight());
        backgroundLevel3.setSize(stage.getWidth(), stage.getHeight());
    }

    @Override
    public void hide() {
        stage.clear();
        player.detach();
        for (FloorEntity floor : floorList)
            floor.detach();
        for (SpikeEntity spike : spikeList)
            spike.detach();
        floorList.clear();
        spikeList.clear();
    }

    @Override
    public void render(float delta) {
        // Set initial visibility based on the current level
        updateBackgroundVisibility();
        elapsedTime += delta;

        if (elapsedTime >= 10 && !gameWon) {
            currentLevel++;

            if (currentLevel <= 3) {
                // Update level button text
                levelButton.setText("Level: " + currentLevel);
                // Reset elapsed time
                elapsedTime = 0;
            } else {
                // Display "Won the game" message and proceed to game over screen
                displayWonMessage();
                gameWon = true; // Set game won flag

                // Proceed to game over screen after a delay
                stage.addAction(
                        Actions.sequence(
                                Actions.delay(2.0f), // Adjust delay as needed
                                Actions.run(() -> {
                                    game.setScreen(game.gameOverScreen);
                                })
                        )
                );
            }
        }


        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        world.step(delta, 6, 2);

        if (player.isAlive()) {
            updateCamera();
        }
        backgroundLevel1.setPosition(stage.getCamera().position.x - stage.getWidth() / 2, 0);
        backgroundLevel2.setPosition(stage.getCamera().position.x - stage.getWidth() / 2, 0);
        backgroundLevel3.setPosition(stage.getCamera().position.x - stage.getWidth() / 2, 0);
        stage.draw();

        if (player.isAlive()) {
            score++;
            scoreButton.setText("Score: " + score);
            //levelButton.setText("Level: " + currentLevel);
        }

        float spawnThreshold = 10;
        if (player.getBody().getPosition().x > lastSpawnPosition + spawnThreshold) {
            spawnObstacles();
        }
        removeOffScreenObstacles();
    }


    private void displayWonMessage() {
        wonLabel.setVisible(true);
    }

    private void spawnObstacles() {
        int offset = 6;
        lastSpawnPosition = 0;
        float obstacleSpacing = 0;
        switch (currentLevel) {
            case 1:
                obstacleSpacing = 12;
                break;
            case 2:
                obstacleSpacing = 8;
                offset = 5;
//                offset = 3;
                break;
            case 3:
                obstacleSpacing = 5;
//                offset = 2;
                offset = 3;
                break;
        }


        Vector2 playerPosition = player.getBody().getPosition();

        Texture natureFloorTexture = game.getManager().get("nature_floor.png", Texture.class);
        Texture overfloorTexture = game.getManager().get("nature_overfloor.png", Texture.class);
        Texture natureSpikeTexture = game.getManager().get("nature_spike.png", Texture.class);
        Texture starTexture = game.getManager().get("bonus.png", Texture.class);

        for (int i = 1; i <= 4; i++) {
            if (rand.nextBoolean()) {
                if (playerPosition.x + offset + i * obstacleSpacing > lastSpawnPosition) {
                    lastSpawnPosition = playerPosition.x + offset + i * obstacleSpacing;
                }
                floorList.add(entityFactory.createFloor(world, playerPosition.x + offset + i * obstacleSpacing, 5 + rand.nextInt(5) + 4, 3, natureFloorTexture, overfloorTexture));
            }
            for (FloorEntity floor : floorList) {
                stage.addActor(floor);
            }
//            floorList.add(entityFactory.createFloor(world, playerPosition.x + i * obstacleSpacing * 2, rand.nextInt(3) + 3, 3, natureFloorTexture, overfloorTexture));
        }
        floorList.clear();

        boolean additionalFloorCreated = false;


        for (int i = 1; i <= 3; i++) {
            if (rand.nextBoolean()) {
                additionalFloorCreated = true;
                float additionalFloorHeight = 4;
                float additionalFloorWidth = 3 + rand.nextInt(6);
                if (lastSpawnPosition > playerPosition.x + offset + rand.nextInt(2) + i)
                    lastSpawnPosition = playerPosition.x + offset + rand.nextInt(2) + i;
                FloorEntity additionalFloor = entityFactory.createFloor(world, playerPosition.x + offset + rand.nextInt(2) + i * obstacleSpacing, additionalFloorWidth, additionalFloorHeight, natureFloorTexture, overfloorTexture);
//                floorList.add(additionalFloor);
                stage.addActor(additionalFloor);
            }
        }

        for (int i = 1; i <= 3 + rand.nextInt(3); i++) {
            float xPosition = playerPosition.x + 10 + rand.nextInt(offset) + (i * obstacleSpacing);

            lastSpawnPosition = xPosition;
            SpikeEntity spike;
            if (rand.nextBoolean()) {
                spike = entityFactory.createSpikes(world, xPosition, 4 - 0.15f, natureSpikeTexture);
            } else {
                spike = entityFactory.createSpikes(world, xPosition, 3 - 0.15f, natureSpikeTexture);
            }
            stage.addActor(spike);
            spikeList.add(spike);

        }
        if (additionalFloorCreated) {
            if (rand.nextBoolean()) {
                lastSpawnPosition = playerPosition.x + offset + obstacleSpacing;
                SpikeEntity spike;
                spike = entityFactory.createSpikes(world, playerPosition.x + offset + obstacleSpacing, 4 - 0.15f, natureSpikeTexture);
                stage.addActor(spike);
                spikeList.add(spike);
            }
        }

        if (currentLevel == 2 || currentLevel == 3) {


            for (int i = 1; i <= 2 + rand.nextInt(3); i++) {
                float xPosition = playerPosition.x + 15 + rand.nextInt(offset) + (i * obstacleSpacing);

                if (rand.nextFloat() < 0.8f) {
                    lastSpawnPosition = xPosition;
                    BonusEntity bonus;
                    if (rand.nextBoolean()) {
                        bonus = entityFactory.createBonus(world, lastSpawnPosition, 4 - 0.15f, starTexture);
                    } else {
                        bonus = entityFactory.createBonus(world, lastSpawnPosition, 3 - 0.15f, starTexture);
                    }

                    stage.addActor(bonus);
                    bonusList.add(bonus);
                }

            }
        }
    }

    private void updateCamera() {
        if (!player.isAlive()) {
            return;
        }

        Camera camera = stage.getCamera();
        Vector3 cameraPosition = camera.position;

        float targetX = player.getX() + player.getWidth() / 2;
        float targetY = player.getY() + player.getHeight() / 2;

        float minX = camera.viewportWidth / 2f;
        float maxX = Float.MAX_VALUE;
        float minY = camera.viewportHeight / 2f;
        float maxY = stage.getHeight() - camera.viewportHeight / 2f;

        targetX = Math.max(minX, Math.min(targetX, maxX));
        targetY = Math.max(minY, Math.min(targetY, maxY));

        float lerpFactor = 0.5f;
        cameraPosition.x += (targetX - cameraPosition.x) * lerpFactor;
        cameraPosition.y += (targetY - cameraPosition.y) * lerpFactor;

        camera.position.set(cameraPosition);
        camera.update();

        scoreButton.setPosition(camera.position.x + camera.viewportWidth / 2 - scoreButton.getWidth() - 30, camera.position.y + camera.viewportHeight / 2 - scoreButton.getHeight() - 30);
        levelButton.setPosition(camera.position.x - camera.viewportWidth / 2 + 30, camera.position.y + camera.viewportHeight / 2 - levelButton.getHeight() - 30);
//        // Center the camera on the "Won the game" label
//        Camera camera = stage.getCamera();
//        Vector3 labelPosition = new Vector3(wonLabel.getX() + wonLabel.getWidth() / 2, wonLabel.getY() + wonLabel.getHeight() / 2, 0);
//        camera.position.set(labelPosition);
//        camera.update();
        // Inside the updateCamera method, after updating the position of the score button
// Update the position of the won label if it's visible
//        if (wonLabel.isVisible()) {
        wonLabel.setPosition(camera.position.x - wonLabel.getWidth() / 2, camera.position.y - wonLabel.getHeight() / 2);
//        }

    }

    private void removeOffScreenObstacles() {
        float offScreenLeft = stage.getCamera().position.x - stage.getViewport().getWorldWidth() / 2 - 50;
        Iterator<FloorEntity> floorIterator = floorList.iterator();
        while (floorIterator.hasNext()) {
            FloorEntity floor = floorIterator.next();
            if (floor.getX() + floor.getWidth() < offScreenLeft) {
                floor.detach();
                stage.getRoot().removeActor(floor);
                floorIterator.remove();
            }
        }

        Iterator<SpikeEntity> spikeIterator = spikeList.iterator();
        while (spikeIterator.hasNext()) {
            SpikeEntity spike = spikeIterator.next();
            if (spike.getX() + spike.getWidth() < offScreenLeft) {
                spike.detach();
                stage.getRoot().removeActor(spike);
                spikeIterator.remove();
            }
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        world.dispose();
    }

    private class GameContactListener implements ContactListener {

        private boolean areCollided(Contact contact, Object userB) {
//            Object userDataA = contact.getFixtureA().getUserData();
//            Object userDataB = contact.getFixtureB().getUserData();
//
//            if (userDataA == null || userDataB == null) {
//                return false;
//            }
//
//            return (userDataA.equals("player") && userDataB.equals(userB)) ||
//                    (userDataA.equals(userB) && userDataB.equals("player"));

            Fixture fixtureA = contact.getFixtureA();
            Fixture fixtureB = contact.getFixtureB();

            if (fixtureA == null || fixtureB == null) {
                return false;
            }

            Object userDataA = fixtureA.getUserData();
            Object userDataB = fixtureB.getUserData();

            if (userDataA == null || userDataB == null) {
                return false;
            }

            return (userDataA.equals("player") && userDataB.equals(userB)) ||
                    (userDataA.equals(userB) && userDataB.equals("player"));
        }

        @Override
        public void beginContact(Contact contact) {
            if (areCollided(contact, "floor")) {
                player.setJumping(false);

                if (Gdx.input.isTouched()) {
                    jumpSound.play();
                    player.setMustJump(true);
                }
            }

            if (areCollided(contact, "spike")) {

                if (player.isAlive()) {
                    player.setAlive(false);
                    backgroundMusic.stop();
                    dieSound.play();

                    saveScore(score);

                    stage.addAction(
                            Actions.sequence(
                                    Actions.delay(1.5f),
                                    Actions.run(() -> game.setScreen(game.gameOverScreen))
                            )
                    );
                }
            }

            // Check collision with bonus points
            if (areCollided(contact, "bonus")) {
                // Increment score by 100
                if (currentLevel == 2) {
                    score += 100;
                } else {
                    score += 200;
                }
                scoreButton.setText("Score: " + score);
//                for (BonusEntity bonus : bonusList) {
//                    stage.getRoot().removeActor(bonus);
//                }
                // Remove the collided bonus entity
                for (BonusEntity bonus : bonusList) {
                    if ((contact.getFixtureA().getBody() == bonus.getBody() && contact.getFixtureB().getBody() == player.getBody()) ||
                            (contact.getFixtureB().getBody() == bonus.getBody() && contact.getFixtureA().getBody() == player.getBody())) {
                        bonus.detach(); // Remove the Box2D body of the bonus entity
                        bonus.remove(); // Remove the actor from the stage
                        bonusList.remove(bonus); // Remove the bonus entity from the list
                        break; // Exit the loop after removing the bonus entity
                    }
                }

                // Remove bonus points

//                    if ((contact.getFixtureA().getBody() == bonus.getBody() && contact.getFixtureB().getBody() == player.getBody()) ||
//                            (contact.getFixtureB().getBody() == bonus.getBody() && contact.getFixtureA().getBody() == player.getBody())) {
//                        bonus.detach();
//                        bonusList.remove(bonus);
//                        break;
//                    }
//                    bonus.detach();
            }
        }


        @Override
        public void endContact(Contact contact) {
            if (areCollided(contact, "floor")) {
                if (player.isAlive()) {
                    jumpSound.play();
                }
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }

    private void saveScore(int score) {
        Preferences prefs = Gdx.app.getPreferences("MyGamePreferences");
        List<Integer> scores = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            scores.add(prefs.getInteger("score" + i, 0));
        }

        scores.add(score);
        scores.sort(Collections.reverseOrder());

        for (int i = 0; i < 10 && i < scores.size(); i++) {
            prefs.putInteger("score" + i, scores.get(i));
        }

        prefs.flush();
    }

    // Inside the show method
    private void updateBackgroundVisibility() {
        // Hide all backgrounds first
        backgroundLevel1.setVisible(false);
        backgroundLevel2.setVisible(false);
        backgroundLevel3.setVisible(false);

        // Then show the appropriate background based on the current level
        switch (currentLevel) {
            case 1:
                backgroundLevel1.setVisible(true);
                break;
            case 2:
                backgroundLevel2.setVisible(true);
                break;
            case 3:
                backgroundLevel3.setVisible(true);
                break;
            // Add more cases if you have more levels
        }
    }


}
