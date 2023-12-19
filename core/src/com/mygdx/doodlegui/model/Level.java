package com.mygdx.doodlegui.model;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.doodlegui.screen.GameScreen;

public class Level {
    enum State {
        START, PLAY, PAUSE
    }

    public State state = State.START;

    public static final int NORMAL = 0;
    public static final int UNDERWATER = 1;
    public static final int SPACE = 2;
    // keep track of theme game-wide
    public int theme;

    // Pixels per second
    public final float gravity = 600.0f;

    public Doodler jumper;
    public PlatformManager platforms;
    public GameScreen gs;

    public Level(GameScreen gs) {
        jumper = new Doodler(this);
        this.gs = gs;
        Random RNG = new Random(Long.parseLong(gs.seed, 36));
        theme = RNG.nextInt(3);
        if (this.theme == 0) {
            this.gs.texture = new Texture(Gdx.files.internal("final/normal/normal_background.png"));
        } else if (this.theme == 1) {
            this.gs.texture = new Texture(Gdx.files.internal("final/underwater/underwater_background.png"));
        } else if (this.theme == 2) {
            this.gs.texture = new Texture(Gdx.files.internal("final/space/space_background.png"));
        }
        platforms = new PlatformManager(this, RNG);
    }

    public void UpdateObjects(float delta) {
        platforms.Update(delta);
        jumper.Update(delta);
    }

    public void DrawObjects(Batch batch) {

        platforms.DrawPlatforms(batch);
        jumper.Draw(batch);
    }
}
