package com.mygdx.doodlegui.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.doodlegui.DoodleDuelJump;
import com.mygdx.doodlegui.screen.DeadScreen;

public class Doodler extends Object {
    public final float JUMPHEIGHT = 600.0f;
    public final float MOVEDIST = 300.0f;

    // preload textures for performance
    public static final Texture[] doodlerUpTextures = {
            new Texture(Gdx.files.internal("final/normal/doodler_up.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_doodler_up.png")),
            new Texture(Gdx.files.internal("final/space/space_doodler_up.png"))
    };
    public static final Texture[] doodlerDownTextures = {
            new Texture(Gdx.files.internal("final/normal/doodler_down.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_doodler_down.png")),
            new Texture(Gdx.files.internal("final/space/space_doodler_down.png"))
    };
    public static final Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jumpG.mp3"));
    public static final Sound dieSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/ah.mp3"));

    public Vector2 velocity = new Vector2();

    private boolean onGround = false;

    private float highestY = -(Float.MAX_VALUE);
    private float curY = -(Float.MAX_VALUE);
    private boolean firstY = true;

    public Doodler(Level level) {
        parentLevel = level;

        collider.width = doodlerUpTextures[0].getWidth();
        collider.height = 20; // collider should only be at bottom of jumper

        collider.x = DoodleDuelJump.GAME_WIDTH / 2 - collider.width / 2;
        collider.y = 50;
    }

    public float getHighestY() {
        return highestY;
    }

    public float getCurY() {
        return curY;
    }

    @Override
    public void Update(float delta) {
        // --Handle input--

        // Jumps automatically
        if (onGround) {
            velocity.y = JUMPHEIGHT;
        }
        // L/R movement
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            velocity.x = -MOVEDIST;
        else if (Gdx.input.isKeyPressed(Input.Keys.D))
            velocity.x = MOVEDIST;
        else
            velocity.x = 0;

        // --Update physics--

        // Apply gravity, increasing it if we are falling for game feeling
        velocity.y -= parentLevel.gravity * (velocity.y >= 0 ? 1.0f : 2.0f) * delta;

        // Apply velocity
        collider.x += velocity.x * delta;
        collider.y += velocity.y * delta;

        // wraparound movement
        if (collider.x > DoodleDuelJump.GAME_WIDTH)
            collider.x -= DoodleDuelJump.GAME_WIDTH;
        else if (collider.x < 0)
            collider.x += DoodleDuelJump.GAME_WIDTH;

        // check for collisions with platforms, but only if we are falling
        if (velocity.y < 0) {
            Platform hit = parentLevel.platforms.CheckCollisions(collider);

            if (hit != null)
                handlePlatformCollision(hit);
            else
                onGround = false;
        } else
            onGround = false;

        if (onGround) {
            parentLevel.gs.game.connection.sendPlayerScore((int) collider.y);
            velocity.y = 0.0f;
        }

        if (curY < highestY) {
            if (firstY) {
                curY = highestY;
                firstY = false;
            } else {
                curY += ((highestY - curY) * delta);
            }
        }

        if ((curY -(DoodleDuelJump.GAME_HEIGHT / 4) > this.collider.y)) {
            // game over
            // gameOver = true;
            dieSound.play(1.0f);
            parentLevel.gs.game.connection.sendPlayerDeath();
            parentLevel.gs.game.setScreen(new DeadScreen(parentLevel.gs.game));
        }
    }

    private void handlePlatformCollision(Platform platform) {
        if (platform instanceof BreakablePlatform) {
            BreakablePlatform breakablePlatform = (BreakablePlatform) platform;

            if (!breakablePlatform.getBroken()) {
                breakablePlatform.breakPlatform();
                onGround = true;
            } else {
                // remove platform?
                onGround = false;
            }
        } else
            onGround = true;
        if (onGround) {
            if (platform.collider.y > highestY) {
                highestY = platform.collider.y;
            }
            jumpSound.play(1.0f);
        }
    }

    @Override
    public void Draw(Batch batch) {
        Texture texture;

        if (velocity.y < 0)
            texture = doodlerDownTextures[parentLevel.theme];
        else
            texture = doodlerUpTextures[parentLevel.theme];

        batch.draw(texture, collider.x, collider.y);

        // draw a second sprite on other side of screen if at edges
        if (collider.x + texture.getWidth() > DoodleDuelJump.GAME_WIDTH)
            batch.draw(texture, collider.x - DoodleDuelJump.GAME_WIDTH, collider.y);
        else if (collider.x - texture.getWidth() < 0)
            batch.draw(texture, collider.x + DoodleDuelJump.GAME_WIDTH, collider.y);
    }
}
