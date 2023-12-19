package com.mygdx.doodlegui.model;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

public abstract class Object {
    // Stores position and size information for collisions and drawing
    public Rectangle collider = new Rectangle();

    public Level parentLevel;

    public Object() {
    }

    // this constructor should be largely unused!
    public Object(Level level, int width, int height) {
        parentLevel = level;

        collider.width = width;
        collider.height = height;
    }

    public void Update(float delta) {
    };

    public abstract void Draw(Batch batch);
}
