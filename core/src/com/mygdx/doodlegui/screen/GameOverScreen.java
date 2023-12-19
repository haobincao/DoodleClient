package com.mygdx.doodlegui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class GameOverScreen implements Screen {

	// Large systems
	final DoodleDuelJump game;
	FitViewport viewport;
	Stage stage;
	OrthographicCamera cam;

	// Fonts, texts, skins, buttons
	Skin comicSkin;
	TextureAtlas comicAtlas;
	BitmapFont smallFont;
	BitmapFont bigFont;
	BitmapFont labelFont;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter labelFontParams;
	FreeTypeFontParameter bigFontParams;
	TextButton mainMenuScreen;

	// Data gathered
	String username_input;
	String password_input;

	// Dimension constants
	final int fieldWidth = 600;
	float errorMsgPos;
	String winner = "";

	public GameOverScreen(final DoodleDuelJump g, String w) {
		this.game = g;
		this.winner = w;
		this.game.screenID = 12;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.labelFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);

		cam.update();

		// Stage command makes things a lot easier!
		stage.draw();
		stage.act();

	}

	@Override
	public void dispose() {
		comicSkin.dispose();
		stage.dispose();
		comicAtlas.dispose();
	}

	public void setFonts() {
		comicSkin = new Skin();

		// For the box
		smallFontParams.size = 50; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);

		// for the labels
		labelFontParams.size = 150; // font size
		labelFontParams.color = Color.WHITE;
		labelFontParams.borderWidth = 4f;
		labelFontParams.borderColor = Color.BLACK;
		labelFontParams.shadowOffsetX = 3;
		labelFontParams.shadowOffsetY = 3;
		labelFontParams.shadowColor = Color.DARK_GRAY;
		labelFont = game.paraFont.generateFont(labelFontParams);

		bigFontParams.size = 200; // font size
		bigFontParams.color = Color.WHITE;
		bigFont = game.titleFont.generateFont(bigFontParams);
		comicSkin.add("title", bigFont);

		comicAtlas = new TextureAtlas(Gdx.files.internal("ComicSkin/comic-ui.atlas"));
		comicSkin.addRegions(comicAtlas);
		comicSkin.load(Gdx.files.internal("ComicSkin/comic-ui.json"));

	}

	@Override
	public void show() {
		// Called when the screen is generated

		setFonts();
		stage.getViewport().setCamera(cam);

		// Draw title
		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;
		Label screenTitle = new Label("GAME OVER", titls);
		Table headTable = new Table();
		headTable.padBottom(600);
		headTable.add(screenTitle);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		// Draw credits
		LabelStyle labelsty = new LabelStyle();
		labelsty.font = labelFont;
		createButtons(labelsty);
		
		Label winLabel = new Label("Winner: " + this.winner, labelsty);
		winLabel.setWrap(true);
		winLabel.setAlignment(Align.center);
		winLabel.setColor(Color.GOLD);

		Table bottomTable = new Table();
		bottomTable.padTop(300);
		bottomTable.add(winLabel).width(1200).height(120).expand().padBottom(100).row();
		bottomTable.add(mainMenuScreen).width(220).height(120).expand();
		bottomTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(bottomTable);

		Gdx.input.setInputProcessor(stage);
	}

	public void createButtons(final LabelStyle style) {
		mainMenuScreen = new TextButton("Back", this.comicSkin);
		mainMenuScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.stopMusic();
				game.startMusic("final/audio/lobbyMusic.mp3");
				game.setScreen(new SecondMenuScreen(game, game.pBest));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		// called when the screen gets hidden (new screen made)
		dispose();

	}
}