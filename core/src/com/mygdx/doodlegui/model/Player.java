package com.mygdx.doodlegui.model;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Player {

    private int score; // player's score (vertial height)
    private float x, y; // horizontal and vertical position of the player
    private boolean isClimbing;
    private String username;
    private Skin playerSkin;
    private boolean isAlive;
    private Level level;

    // private Jumper jumper;
    // private PlatformManager platformManager;

    public Player(String username, Level level) {

        this.username = username;
        this.score = 0;
        this.x = 0.0f;
        this.y = 0.0f;
        this.isClimbing = false;
        this.isAlive = true;

        this.level = level;
    }

    public void Update(float delta) {

        level.UpdateObjects(delta);
        setX(level.jumper.collider.x);
        setY(level.jumper.collider.y);
        setScore((int) this.y);

    }

    public boolean isOnBrokenPlatform() {
        Platform currPlatform = level.platforms.CheckCollisions(level.jumper.collider);
        return (currPlatform instanceof BreakablePlatform && ((BreakablePlatform) currPlatform).getBroken());
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getY() {
        return y;
    }

    public void setIsClimbing(boolean isClimbing) {
        this.isClimbing = isClimbing;
    }

    public boolean getIsClimbing() {
        return isClimbing;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setIsAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void setPlayerSkin(String skinName) {
        Texture newSkinTexture = new Texture(skinName + ".png");
        this.playerSkin.add(skinName, newSkinTexture);
    }

    public Texture getPlayerSkinTexture() {
        return playerSkin.get("skinName", Texture.class);
    }

}