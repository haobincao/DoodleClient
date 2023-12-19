package com.mygdx.doodlegui;

import java.util.Vector;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.mygdx.doodlegui.screen.*;

public class DoodleDuelJump extends Game {
	public static final int GAME_WIDTH = 1000;
	public static final int TOTAL_WIDTH = 1600;
	public static final int GAME_HEIGHT = 1200;

	public SpriteBatch batch;
	public ShapeRenderer shape;
	public FreeTypeFontGenerator paraFont; // Used for all small fonts
	public FreeTypeFontGenerator titleFont; // Used for all large fonts
	public GlyphLayout glo;
	private String client_username;
	private String client_nickname;
	public ClientConnection connection;
	public int screenID;
	public Vector<String[]> leaderboard;
	public int playerLeaderboardRanking;
	public float playerLeaderboardScore;
	public int pBest = 0;

	// can add more fonts if needed...

	private Music menuMusic;
	private boolean playingMusic = false;
	private String curBgPath;
	private String ip = "3.12.73.132";
	// private String ip = "localhost";

	@Override
	public void create() {
		this.batch = new SpriteBatch();
		FreeTypeFontGenerator.setMaxTextureSize(2048);
		this.paraFont = new FreeTypeFontGenerator(Gdx.files.internal("SpongeMeetsVanilla.ttf"));
		this.titleFont = new FreeTypeFontGenerator(Gdx.files.internal("FlowersSunday.otf")); // update with title font
		this.shape = new ShapeRenderer();
		this.glo = new GlyphLayout();

		this.connection = new ClientConnection(this, ip, 8567);
		this.setScreen(new MainMenuScreen(this));
	}

	public void startMusic(String pathname) {
		if (!playingMusic) {
			curBgPath = pathname;
			menuMusic = Gdx.audio.newMusic(Gdx.files.internal(pathname));
			menuMusic.setLooping(true);
			menuMusic.play();
			playingMusic = true;
		} else {
			if (pathname.compareToIgnoreCase(curBgPath) != 0) {
				stopMusic();
				curBgPath = pathname;
				menuMusic = Gdx.audio.newMusic(Gdx.files.internal(pathname));
				menuMusic.setLooping(true);
				menuMusic.play();
				playingMusic = true;
			}
		}
	}

	public void stopMusic() {
		menuMusic.stop();
		playingMusic = false;
	}

	public void setUsername(String un) {
		this.client_username = un;
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		batch.dispose();
		shape.dispose();
		paraFont.dispose();
		titleFont.dispose();
		getScreen().dispose();

		if (this.screenID == 7) {
			connection.sendDismissRoom();
		} else if (this.screenID == 9) {
			connection.sendLeaveLobby();
		}
		connection.sendSignOut();
		connection.sendClientClosed();
	}

	// Returns [width, height]
	public float[] getTextSize(BitmapFont font, String str) {
		glo.setText(font, str);
		float fontWidth = glo.width;
		float fontHeight = glo.height;
		float[] fontdims = { fontWidth, fontHeight };
		return fontdims;
	}

	public String getUsername() {
		return this.client_username;
	}

	public int getScreenID() {
		return this.screenID;
	}

	public String getNickname() {
		return client_nickname;
	}

	public void setNickname(String client_nickname) {
		this.client_nickname = client_nickname;
	}
}
