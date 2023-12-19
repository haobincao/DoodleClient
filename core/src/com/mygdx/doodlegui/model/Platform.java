package com.mygdx.doodlegui.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;

public abstract class Platform extends Object {
    private static int IDCounter = 0;

    private static int GenerateID() {
        return ++IDCounter;
    }

    // preload textures for performance
    public static final Texture[] regularPlatformTextures = {
            new Texture(Gdx.files.internal("final/normal/regular_platform.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_regular_platform.png")),
            new Texture(Gdx.files.internal("final/space/space_regular_platform.png"))
    };
    public static final Texture[] breakingPlatformTextures = {
            new Texture(Gdx.files.internal("final/normal/breaking_platform.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_breaking_platform.png")),
            new Texture(Gdx.files.internal("final/space/space_breaking_platform.png"))
    };
    public static final Texture[] brokenPlatformTextures = {
            new Texture(Gdx.files.internal("final/normal/broken_platform.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_broken_platform.png")),
            new Texture(Gdx.files.internal("final/space/space_broken_platform.png"))
    };
    public static final Texture[] movingPlatformTextures = {
            new Texture(Gdx.files.internal("final/normal/moving_platform.png")),
            new Texture(Gdx.files.internal("final/underwater/underwater_moving_platform.png")),
            new Texture(Gdx.files.internal("final/space/space_moving_platform.png"))
    };

    // Unique field, should be replicated
    // among all clients and server
    public final int platformID;

    // empty constructor for derived classes
    public Platform() {
        super();
        platformID = Platform.GenerateID();
    }

    public Platform(Level level) {
        super();

        parentLevel = level;

        collider.width = regularPlatformTextures[0].getWidth();
        collider.height = regularPlatformTextures[0].getHeight();

        platformID = Platform.GenerateID();
    }
}

class RegularPlatform extends Platform {
    public RegularPlatform(Level level) {
        super(level);
    }

    @Override
    public void Draw(Batch batch) {
        batch.draw(regularPlatformTextures[parentLevel.theme], collider.x, collider.y);
    }
}

class MovingPlatform extends Platform {
    public static final float SPEED = 150.f;
    public Vector2 point1;
    public Vector2 point2;
    public boolean goToFirst = false;

    public MovingPlatform(Level level, float x1, float y1, float x2, float y2) {
        super(level);

        point1 = new Vector2(x1, y1);
        point2 = new Vector2(x2, y2);
    }

    @Override
    public void Update(float delta) {
        Vector2 target;
        if (goToFirst)
            target = point1;
        else
            target = point2;

        Vector2 velocity = new Vector2(collider.x, collider.y);
        velocity.x = target.x - velocity.x;
        velocity.y = target.y - velocity.y;
        // velocity now stores vector from platform to target
        velocity = velocity.nor().scl(SPEED * delta);
        // velocity has been scaled to speed

        collider.x += velocity.x;
        collider.y += velocity.y;

        // check if we are at the target yet; if so turn around
        velocity.x = collider.x;
        velocity.y = collider.y;
        if (velocity.dst2(target) < 3.f/* epsilon */)
            goToFirst = !goToFirst;
    }

    @Override
    public void Draw(Batch batch) {
        batch.draw(movingPlatformTextures[parentLevel.theme], collider.x, collider.y);
    }
}

class BreakablePlatform extends Platform {
    private boolean broken; // broken is true if the platform has been used, else false if ready to use

    public BreakablePlatform(Level level) {
        super(level);
        broken = false;
    }

    public boolean getBroken() {
        return broken;
    }

    public void breakPlatform() {
        broken = true;
    }

    @Override
    public void Draw(Batch batch) {
        if (broken)
            batch.draw(brokenPlatformTextures[parentLevel.theme], collider.x, collider.y);
        else
            batch.draw(breakingPlatformTextures[parentLevel.theme], collider.x, collider.y);
    }
}