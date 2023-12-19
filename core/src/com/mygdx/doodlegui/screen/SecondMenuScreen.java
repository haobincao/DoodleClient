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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class SecondMenuScreen implements Screen {
	// This is the menu screen that pops up after you log in. Allowing you to choose
	// to host
	// or join a game.

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
	FreeTypeFontParameter bigFontParams;
	FreeTypeFontParameter labelFontParams;
	TextButton mainMenuScreen;
	TextButton hostScreen;
	TextButton joinScreen;
	TextField pScore;

	public SecondMenuScreen(final DoodleDuelJump g, int p) {
		this.game = g;
		this.game.screenID = 6;
		this.game.pBest = p;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.labelFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();

	}

	@Override
	public void show() {
		setFonts();
		stage.getViewport().setCamera(cam);

		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;
		
		LabelStyle labls = new LabelStyle();
		labls.font = labelFont;

		Label screenTitle = new Label("Play Game", titls);
		Table headTable = new Table();
		headTable.padBottom(800);
		headTable.add(screenTitle);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		createButtons();

		pScore = new TextField(Integer.toString(game.pBest), this.comicSkin);
		LabelStyle parals = new LabelStyle();
		parals.font = smallFont;
		pScore.setDisabled(true);
		pScore.setAlignment(Align.center);

		Table bottomTable = new Table();
		Table leftTable = new Table();
		Table rightTable = new Table();

		Label welcome;
		if (this.game.getNickname().compareTo("Guest Player") == 0) {
			welcome = new Label("Hi, Guest!", labls);
		} else {
			welcome = new Label("Hi, " + this.game.getNickname() + "!", labls);
		}
		
		welcome.setAlignment(Align.center);
		welcome.setColor(Color.GOLD);
		welcome.setWrap(true);
		Label subtitle = new Label("Personal Best:", parals);
		subtitle.setWrap(true);
		bottomTable.padTop(200);
		leftTable.add(welcome).width(700).height(150).expand().row();
		leftTable.add(subtitle).width(400).height(150).expand().row();
		leftTable.add(pScore).width(400).height(150).expand();
		rightTable.add(hostScreen).width(600).height(150).expand().padBottom(60);
		rightTable.row();
		rightTable.add(joinScreen).width(600).height(150).expand().padBottom(60);
		rightTable.row();
		rightTable.add(mainMenuScreen).width(600).height(150).expand().padBottom(60);
		bottomTable.add(leftTable);
		bottomTable.add(rightTable);
		bottomTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(bottomTable);

		Gdx.input.setInputProcessor(stage);
	}

	public void createButtons() {
		hostScreen = new TextButton("Host Game", this.comicSkin);
		hostScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new HostScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		joinScreen = new TextButton("Join Game", this.comicSkin);
		joinScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new JoinScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
		mainMenuScreen = new TextButton("Log Out", this.comicSkin);
		mainMenuScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.connection.sendSignOut();

				game.setScreen(new MainMenuScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);

				// remember to reset info
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
	}

	public void setFonts() {
		comicSkin = new Skin();

		// For the box
		smallFontParams.size = 90; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);
		

		labelFontParams.size = 120; // font size
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
	public void render(float delta) {
		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);

		cam.update();

		// Stage command makes things a lot easier!
		stage.draw();
		stage.act();
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
		dispose();
	}

	@Override
	public void dispose() {
		comicSkin.dispose();
		stage.dispose();
		comicAtlas.dispose();

	}

}
