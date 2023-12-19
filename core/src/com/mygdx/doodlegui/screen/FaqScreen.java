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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class FaqScreen implements Screen {

	// Large systems
	final DoodleDuelJump game;
	FitViewport viewport;
	Stage stage;
	OrthographicCamera cam;

	// Fonts, texts, skins, buttons
	Skin comicSkin;
	TextureAtlas comicAtlas;
	BitmapFont smallFont;
	BitmapFont small2Font;
	BitmapFont bigFont;
	BitmapFont labelFont;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter small2FontParams;
	FreeTypeFontParameter labelFontParams;
	FreeTypeFontParameter bigFontParams;
	TextButton mainMenuScreen;

	// Data gathered
	String username_input;
	String password_input;

	// Dimension constants
	final int fieldWidth = 600;
	float errorMsgPos;

	public FaqScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 4;
		this.smallFontParams = new FreeTypeFontParameter();
		this.small2FontParams = new FreeTypeFontParameter();
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

		// For the box
		small2FontParams.size = 60; // font size
		small2FontParams.color = Color.WHITE;
		small2Font = game.paraFont.generateFont(small2FontParams);

		// for the labels
		labelFontParams.size = 120; // font size
		labelFontParams.color = Color.WHITE;
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
		Label screenTitle = new Label("FAQs", titls);
		Table headTable = new Table();
		headTable.padBottom(600);
		headTable.add(screenTitle);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		// Draw q&as
		LabelStyle labelsty = new LabelStyle();
		LabelStyle labelsty2 = new LabelStyle();
		labelsty2.font = small2Font;
		labelsty.font = smallFont;
		Label q1 = new Label("Q1: How do I control my character?", labelsty2);
		Label a1 = new Label("A1: With the a & d keys.", labelsty);
		Label q2 = new Label("Q2: Why are platforms different colors?", labelsty2);
		Label a2 = new Label(
				"A2: They correspond to different platform types: red = normal, blue = moving, green = breakable",
				labelsty);
				Label a2b = new Label(
				"(colors for the normal theme)",
				labelsty);
		Label q3 = new Label("Q3: What is my goal as a player?", labelsty2);
		Label a3 = new Label("A3: Get the highest score by climbing the highest!", labelsty);
		Table midTable = new Table();
		midTable.padTop(300);
		midTable.padBottom(300);
		midTable.row();
		midTable.add(q1);
		midTable.row();
		midTable.add(a1);
		midTable.row();
		midTable.add(q2);
		midTable.row();
		midTable.add(a2);
		midTable.row();
		midTable.add(a2b);
		midTable.row();
		midTable.add(q3);
		midTable.row();
		midTable.add(a3);
		midTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(midTable);
		createButtons(labelsty);

		Table bottomTable = new Table();
		bottomTable.padTop(600);
		bottomTable.add(mainMenuScreen).width(220).height(120).expand();
		bottomTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(bottomTable);

		Gdx.input.setInputProcessor(stage);
	}

	public void createButtons(final LabelStyle style) {
		mainMenuScreen = new TextButton("Main Menu", this.comicSkin);
		mainMenuScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new MainMenuScreen(game));
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