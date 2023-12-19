package com.mygdx.doodlegui.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public class Powerup extends Object {
    private static int IDCounter = 0;

    private static int GenerateID() {
        return ++IDCounter;
    }

    public static Texture powerupTexture = new Texture(Gdx.files.internal("powerup.png"));

    // Unique field, should be replicated
    // among all clients and server
    public final int powerupID;

    public Powerup(Level level) {
        super(level, powerupTexture.getWidth(), powerupTexture.getHeight());
        powerupID = Powerup.GenerateID();
    }

    @Override
    public void Update(float delta) {

    }

    @Override
    public void Draw(Batch batch) {
        batch.draw(powerupTexture, collider.x, collider.y);
    }
}
