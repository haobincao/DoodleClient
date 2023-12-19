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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class RegisterScreen implements Screen {

	final DoodleDuelJump game;
	FitViewport viewport;
	Stage stage;
	OrthographicCamera cam;

	// Fonts, texts, skins, buttons
	Skin comicSkin;
	TextureAtlas comicAtlas;
	BitmapFont smallFont;
	BitmapFont bigFont;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter bigFontParams;
	TextField usernameT;
	TextField nickNameT;
	TextField passwordT;
	TextField emailT;
	TextButton submit;
	TextButton mainMenuScreen;
	Label errorMsg;

	// Data gathered
	String username_input;
	String password_input;
	String nickName_input;
	String email_input;

	// Dimension constants
	final int fieldWidth = 400;

	// Data received
	boolean gotResponse = false;
	int code;
	String response = "";

	public RegisterScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 3;
		this.game.connection.registerScreen = this;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();
	}

	public void signUpResponse(int c, String r) {
		this.gotResponse = true;
		this.code = c;
		this.response = r;
	}

	public void createButtons(final LabelStyle style) {

		submit = new TextButton("Submit", this.comicSkin);
		submit.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				nickName_input = nickNameT.getText();
				username_input = usernameT.getText();
				password_input = passwordT.getText();
				email_input = emailT.getText();

				if (errorMsg != null) {
					errorMsg.remove();
				}

				// Give error messages if style isn't correct
				if (nickName_input.length() == 0 ||
						username_input.length() == 0 || password_input.length() == 0 ||
						email_input.length() == 0) {
					createErrorMsg("Fill in all fields", style);
					return;
				} else if (!email_input.contains("@")) {
					createErrorMsg("Invalid Email", style);
					return;
				} else if (username_input.length() > 25 || username_input.length() < 6) {
					createErrorMsg("Username must be 6-25 characters.", style);
					return;
				} else if (nickName_input.length() > 10 || nickName_input.length() < 6) {
					createErrorMsg("Nickname must be 6-10 characters.", style);
					return;
				} else if (password_input.length() < 6) {
					createErrorMsg("Password must be at least 6 characters.", style);
					return;
				} else if (username_input.contains(" ")) {
					createErrorMsg("Username must not contain spaces.", style);
					return;
				}

				gotResponse = false;
				game.connection.requestSignup(nickName_input, username_input, password_input, email_input);

				System.out.println("nick name: " + nickName_input);
				System.out.println("username: " + username_input + "\npassword: " + password_input);
				System.out.println("email: " + email_input);
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

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

	public void createErrorMsg(String str, LabelStyle style) {

		errorMsg = new Label(str, style);
		errorMsg.setColor(Color.BLACK);
		errorMsg.setWrap(true);
		errorMsg.setWidth(DoodleDuelJump.TOTAL_WIDTH / 4);
		errorMsg.setPosition(0.8f * DoodleDuelJump.TOTAL_WIDTH - errorMsg.getWidth() / 2,
				0.16f * DoodleDuelJump.GAME_HEIGHT);
		stage.addActor(errorMsg);
	}

	@Override
	public void show() {
		setFonts();
		stage.getViewport().setCamera(cam);

		// Draw title
		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;
		Label screenTitle = new Label("Sign Up", titls);
		Table headTable = new Table();
		headTable.padBottom(800);
		headTable.add(screenTitle);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		LabelStyle parals = new LabelStyle();
		parals.font = smallFont;
		Label emailL = new Label("Email:", parals);
		Label nickNameL = new Label("Nick Name:", parals);
		Label usernameL = new Label("Username:", parals);
		Label passwordL = new Label("Password:", parals);
		usernameT = new TextField("", this.comicSkin);
		emailT = new TextField("", this.comicSkin);
		nickNameT = new TextField("", this.comicSkin);
		passwordT = new TextField("", this.comicSkin);
		passwordT.setPasswordMode(true);
		passwordT.setPasswordCharacter('*');

		createButtons(parals);

		int pad = 40;
		Table lefttable = new Table();
		// lefttable.setDebug(true);
		lefttable.padTop(300);
		lefttable.add(nickNameL).expand().fill().pad(pad);
		lefttable.add(nickNameT).width(fieldWidth).height(120).expand();
		lefttable.row();
		lefttable.row();
		lefttable.add(usernameL).expand().fill().pad(pad);
		lefttable.add(usernameT).width(fieldWidth).height(120).expand();
		lefttable.row();
		lefttable.add(passwordL).expand().fill().pad(pad);
		lefttable.add(passwordT).width(fieldWidth).height(120).expand();
		lefttable.row();
		lefttable.add(emailL).expand().fill().pad(pad);
		lefttable.add(emailT).width(fieldWidth).height(120).expand();
		lefttable.setPosition(0.3f * DoodleDuelJump.TOTAL_WIDTH, 0.55f * DoodleDuelJump.GAME_HEIGHT);
		stage.addActor(lefttable);

		Table righttable = new Table();
		righttable.setPosition(0.8f * DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT / 2);
		righttable.add(mainMenuScreen).width(400).height(150).expand().padBottom(60);
		righttable.row();
		righttable.add(submit).width(400).height(150).expand();
		stage.addActor(righttable);

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);

		cam.update();

		// Stage command makes things a lot easier!
		stage.draw();
		stage.act();

		if (gotResponse) {
			LabelStyle labelsty = new LabelStyle();
			labelsty.font = smallFont;
			createErrorMsg(response, labelsty);
			gotResponse = false;
		}

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

	public void setFonts() {

		// Unfortunately, looks like we may need to create a new skin every screen;
		// which is fine.
		// just copy paste the code.

		comicSkin = new Skin();

		// For the box
		smallFontParams.size = 70; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);

		// for the labels
		smallFontParams.size = 70; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);

		bigFontParams.size = 200; // font size
		bigFontParams.color = Color.WHITE;
		bigFont = game.titleFont.generateFont(bigFontParams);
		comicSkin.add("title", bigFont);

		comicAtlas = new TextureAtlas(Gdx.files.internal("ComicSkin/comic-ui.atlas"));
		comicSkin.addRegions(comicAtlas);
		comicSkin.load(Gdx.files.internal("ComicSkin/comic-ui.json"));

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
