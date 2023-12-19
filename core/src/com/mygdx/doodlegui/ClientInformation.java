package com.mygdx.doodlegui;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector2;

// Serializable data struct for communicating player information between multiplayer games
public class ClientInformation implements Serializable {
    public Vector2 position;
    // normally null; if powerup was just picked up instead give its ID
    Integer powerupID;
    // normally null; if broken platform was just stepped on instead give its ID
    Integer brokenPlatformID;

    public ClientInformation() {
    }

    public ClientInformation(Vector2 p) {
        position = p;
        powerupID = null;
        brokenPlatformID = null;
    }
}
