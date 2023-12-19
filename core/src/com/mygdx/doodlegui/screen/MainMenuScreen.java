package com.mygdx.doodlegui.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class MainMenuScreen implements Screen {

	final DoodleDuelJump game;
	FitViewport viewport;
	Stage stage;
	OrthographicCamera cam;

	Skin comicSkin;
	TextureAtlas comicAtlas;
	BitmapFont smallFont;
	BitmapFont bigFont;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter bigFontParams;
	TextButton loginScreen;
	TextButton faqScreen;
	TextButton registerScreen;
	TextButton creditScreen;
	TextButton leaderboardScreen;
	Image doodler;

	public MainMenuScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 1;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();
		this.doodler = new Image(new Texture(Gdx.files.internal("doodler_single_legs.png")));
		game.startMusic("final/audio/lobbyMusic.mp3");
	}

	public void setFonts() {
		// Unfortunately, looks like we may need to create a new skin every screen;
		// which is fine.
		// just copy paste the code.

		comicSkin = new Skin();

		// For the box
		smallFontParams.size = 60; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);

		// for the labels
		smallFontParams.size = 120; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);

		bigFontParams.size = 170; // font size
		bigFontParams.color = Color.WHITE;
		bigFont = game.titleFont.generateFont(bigFontParams);
		comicSkin.add("title", bigFont);

		comicAtlas = new TextureAtlas(Gdx.files.internal("ComicSkin/comic-ui.atlas"));
		comicSkin.addRegions(comicAtlas);
		comicSkin.load(Gdx.files.internal("ComicSkin/comic-ui.json"));

	}

	@Override
	public void show() {
		setFonts();
		stage.getViewport().setCamera(cam);

		createButtons();

		int buttonHeight = 120;
		int buttonWidth = 500;
		int padding = 70;
		Table rightTable = new Table();
		rightTable.setPosition(3 * DoodleDuelJump.TOTAL_WIDTH / 4, DoodleDuelJump.GAME_HEIGHT / 2);
		rightTable.add(loginScreen).width(buttonWidth).height(buttonHeight).expand();
		rightTable.row();
		rightTable.add(registerScreen).width(buttonWidth).height(buttonHeight).expand().padTop(padding);
		rightTable.row();
		rightTable.add(faqScreen).width(buttonWidth).height(buttonHeight).expand().padTop(padding);
		rightTable.row();
		rightTable.add(creditScreen).width(buttonWidth).height(buttonHeight).expand().padTop(padding);
		stage.addActor(rightTable);

		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;
		Label screenTitle1 = new Label("Doodle", titls);
		Label screenTitle2 = new Label("Duel", titls);
		Label screenTitle3 = new Label("Jump", titls);

		Table leftTable = new Table();
		leftTable.setPosition(0.28f * DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT / 2);
		// leftTable.setDebug(true);
		leftTable.add(screenTitle1).height(170).expand();
		leftTable.row();
		leftTable.add(screenTitle2).height(170).expand();
		leftTable.row();
		leftTable.add(screenTitle3).height(170).expand();
		leftTable.row();
		leftTable.add(doodler).size(200, 300).padTop(100);
		stage.addActor(leftTable);

		Gdx.input.setInputProcessor(stage);

	}

	public void createButtons() {
		loginScreen = new TextButton("Login", this.comicSkin);
		loginScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new LoginScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		faqScreen = new TextButton("FAQs", this.comicSkin);
		faqScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new FaqScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		registerScreen = new TextButton("Sign Up", this.comicSkin);
		registerScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new RegisterScreen(game));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		creditScreen = new TextButton("Credits", this.comicSkin);
		creditScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new CreditsScreen(game));
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
