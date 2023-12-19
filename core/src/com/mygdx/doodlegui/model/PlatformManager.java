package com.mygdx.doodlegui.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.PooledLinkedList;
import com.mygdx.doodlegui.DoodleDuelJump;

import java.util.Random;

public class PlatformManager {
    public Level parentLevel;

    /*
     * Linked list stores highest platforms last and lowest platforms first
     * Add to back and remove from front as if it was a queue
     * iteration example:
     * platforms.iter();
     * while((cur_platform = platforms.next()) != null) cur_platform.doStuff();
     * Pooled to take advantage of cache
     */
    public PooledLinkedList<Platform> platforms;
    // cached for iteration
    private Platform cur_platform;

    // For platform generation
    private Random RNG;
    // vertical height between platforms
    private final int PLATFORM_DIST = 75;
    // height where newest platform should be generated
    private int newest_height = 0;

    public PlatformManager(Level level, Random rand) {
        parentLevel = level;
        platforms = new PooledLinkedList<Platform>(20 /* may be lowered */);

        RNG = rand;

        Platform newPlatform = new RegularPlatform(parentLevel);

        newPlatform.collider.x = DoodleDuelJump.GAME_WIDTH / 2 - newPlatform.collider.width / 2;
        newPlatform.collider.y = newest_height;
        newest_height += PLATFORM_DIST;

        platforms.add(newPlatform);
    }

    public void Update(float delta) {
        // Create new platforms as necessary
        createNewPlatforms();
        // Remove lower platforms as necessary
        removeOffScreenPlatforms();

        platforms.iter();
        while ((cur_platform = platforms.next()) != null)
            cur_platform.Update(delta);
    }

    public void removeOffScreenPlatforms() {
        platforms.iter();
        cur_platform = platforms.next();
        while (cur_platform != null) {
            if (parentLevel.jumper.collider.y - DoodleDuelJump.GAME_HEIGHT > cur_platform.collider.y) {
                platforms.remove();
                cur_platform = platforms.next();
            } else
                break;
        }
    }

    public void createNewPlatforms() {
        while (parentLevel.jumper.collider.y + DoodleDuelJump.GAME_HEIGHT > newest_height) {
            CreatePlatform();
        }
    }

    private void CreatePlatform() {
        int x = RNG.nextInt(DoodleDuelJump.GAME_WIDTH - 150 /* platform width */);
        int y = newest_height;
        newest_height += PLATFORM_DIST;

        Platform newPlatform;
        // regular platforms twice as likely to spawn
        switch (RNG.nextInt(4)) {
            default:
            case 0:
                newPlatform = new RegularPlatform(parentLevel);
                break;
            case 2:
                int x2;
                // add some variability
                int offset = (200 + RNG.nextInt(300)) * (RNG.nextInt(2) == 0 ? 1 : -1);
                if (x + offset <= DoodleDuelJump.GAME_WIDTH - 150 && x + offset >= -150)
                    x2 = x + offset;
                else
                    x2 = x - offset;
                int y2 = y;
                newPlatform = new MovingPlatform(parentLevel, x, y, x2, y2);
                break;
            case 3:
                newPlatform = new BreakablePlatform(parentLevel);
        }
        newPlatform.collider.x = x;
        newPlatform.collider.y = y;

        platforms.add(newPlatform);
    }

    public void DrawPlatforms(Batch batch) {
        platforms.iter();
        while ((cur_platform = platforms.next()) != null)
            cur_platform.Draw(batch);
    }

    // Checks for collisions bw the given collider and platforms
    // If collision, and the platform is an unbroken breakable platform, break it
    // and return it
    // otherwise returns null
    public Platform CheckCollisions(Rectangle collider) {
        platforms.iter(); // iterator for each platform to check for collisions
        while ((cur_platform = platforms.next()) != null) {
            // collision detected
            if (collider.overlaps(cur_platform.collider)) {
                return cur_platform; // return platform that was collided with
            }
        }
        return null; // no collisions detected
    }
}
