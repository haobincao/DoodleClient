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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class LoginScreen implements Screen {

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
	TextField usernameT;
	TextField passwordT;
	TextButton submit;
	TextButton mainMenuScreen;
	Table errorTable;
	Table bottomTable;
	Label errorMsg;
	TextButton guestButton;

	// Data gathered
	String username_input;
	String password_input;

	// Data received
	boolean gotResponse = false;
	boolean guestResponse = false;
	int code;
	String response = "";
	String nickName;

	public LoginScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 2;
		this.game.connection.loginScreen = this;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.labelFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();
	}

	public void signinUnsuccessful(int c, String r) {
		this.gotResponse = true;
		this.code = c;
		this.response = r;
	}

	public void signinSuccessful(int c, String r, String nn, int p) {
		this.gotResponse = true;
		this.code = c;
		this.response = r;
		this.nickName = nn;
		this.game.pBest = p;
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);

		cam.update();

		// Stage command makes things a lot easier!
		stage.draw();
		stage.act();

		if (gotResponse) {
			if (code == 1) {
				LabelStyle labelsty = new LabelStyle();
				labelsty.font = smallFont;
				createErrorMsg(response, labelsty);
				gotResponse = false;
			} else {
				gotResponse = false;
				game.setUsername(username_input);
				game.setNickname(nickName);
				game.setScreen(new SecondMenuScreen(game, game.pBest));
			}
		}
	}

	@Override
	public void dispose() {
		comicSkin.dispose();
		stage.dispose();
		comicAtlas.dispose();
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

		// table.setDebug(true); //use for debugging as needed.

		setFonts();
		stage.getViewport().setCamera(cam);

		// Draw title
		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;
		Label screenTitle = new Label("Login", titls);
		Table headTable = new Table();
		headTable.padBottom(800);
		headTable.add(screenTitle);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		// Draw Logins
		LabelStyle parals = new LabelStyle();
		parals.font = labelFont;
		Label usernameL = new Label("Username: ", parals);
		Label passwordL = new Label("Password: ", parals);
		usernameT = new TextField("", this.comicSkin);
		passwordT = new TextField("", this.comicSkin);
		passwordT.setPasswordMode(true);
		passwordT.setPasswordCharacter('*');

		Table table = new Table();
		// table.padTop(100);
		table.add(usernameL).expand().fill().pad(40);
		table.add(usernameT).width(600).height(120).expand();
		table.row();
		table.add(passwordL).expand().fill().pad(40);
		table.add(passwordT).width(600).height(120).expand();
		table.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(table);

		errorTable = new Table();
		stage.addActor(errorTable);

		LabelStyle labelsty = new LabelStyle();
		labelsty.font = smallFont;
		createButtons(labelsty);

		bottomTable = new Table();
		bottomTable.add(mainMenuScreen).width(250).height(120).expand().padRight(500);
		bottomTable.add(submit).width(220).height(120).expand().row();
		bottomTable.add(guestButton).width(970).height(120).padTop(50).colspan(2).expandX();
		bottomTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, 0.23f * DoodleDuelJump.GAME_HEIGHT);
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

		submit = new TextButton("Submit", this.comicSkin);
		submit.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				username_input = usernameT.getText();
				password_input = passwordT.getText();

				if (errorMsg != null) {
					errorTable.clearChildren();
					errorMsg.remove();
				}

				if (username_input.length() > 25 || username_input.length() < 6 || username_input.contains(" ")) {
					createErrorMsg("Username/Password Invalid.", style);
					return;
				} else if (password_input.length() < 6) {
					createErrorMsg("Username/Password Invalid.", style);
					return;
				}
				gotResponse = false;
				game.connection.requestSignin(username_input, password_input);

				System.out.println("username: " + username_input + "\npassword: " + password_input);
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		guestButton = new TextButton("Play as Guest", this.comicSkin);
		guestButton.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.connection.requestGuestUser();
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
	}

	public void createErrorMsg(String str, LabelStyle style) {
		errorMsg = new Label(str, style);
		errorMsg.setWrap(true);
		errorMsg.setAlignment(Align.center);
		errorMsg.setColor(Color.BLACK);
		errorTable.add(errorMsg).width(DoodleDuelJump.TOTAL_WIDTH / 4).expand();
		errorTable.setPosition(0.5f * DoodleDuelJump.TOTAL_WIDTH - errorTable.getWidth() / 2,
				0.29f * DoodleDuelJump.GAME_HEIGHT);

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

// Scratch: remove at end.

// Login, MainMenu, Register, FAQ, Credits, GameScreen, DeadScreen, GameOver,
// Leaderboard,

//
// tBoxDims = game.getTextSize(smallFont, "username");
// inputStyle.font = smallFont;
// inputStyle.fontColor = Color.WHITE; // got to do it again

// game.batch.begin();
// bigFont.draw(game.batch, this.title, Gdx.graphics.getWidth()/2 -
// titleDims[0]/2, 1000 + titleDims[1]/2);
// game.batch.end();
//
//// Draw background shapes next. MUST be a separate batch or the font and
// shapes conflict.
// game.batch.begin();
//
// game.shape.begin(ShapeType.Filled);
// game.shape.setColor(Color.GRAY);
// game.shape.rect(uBoxPos[0] - 30, uBoxPos[1] , fieldWidth + 60, tBoxDims[1] +
// 50);
// game.shape.end();
//
// game.shape.begin(ShapeType.Filled);
// game.shape.setColor(Color.GRAY);
// game.shape.rect(pBoxPos[0] - 30, pBoxPos[1], fieldWidth + 60, tBoxDims[1] +
// 50);
// game.shape.end();

// default: {
// font: font
// fontColor: black
// up: button
// down: button-pressed
// over: button-highlighted
// }
//
// game.batch.end();

// game.comicSkin.add("myFont", smallFont);
