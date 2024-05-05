package com.shrinivas.savethebearcat.game.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class EntityFactory {

    public EntityFactory() {
    }

    public PlayerEntity createPlayer(World world, Vector2 position, Texture texture) {
        return new PlayerEntity(world, texture, position);
    }

    public FloorEntity createFloor(World world, float x, float width, float y, Texture floorTexture, Texture overfloorTexture) {
        return new FloorEntity(world, floorTexture, overfloorTexture, x, width, y);
    }

    public SpikeEntity createSpikes(World world, float x, float y, Texture spikeTexture) {
        return new SpikeEntity(world, spikeTexture, x, y);
    }

    public BonusEntity createBonus(World world, float x, float y, Texture bonusTexture) {
        return new BonusEntity(world, bonusTexture, x, y);
    }
}
