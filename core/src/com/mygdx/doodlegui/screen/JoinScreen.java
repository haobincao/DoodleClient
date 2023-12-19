package com.mygdx.doodlegui.screen;

import java.util.Vector;

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

public class JoinScreen implements Screen {

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
	TextButton secondMenuScreen;
	TextButton lobbyScreen;
	TextField codeField;
	Label errorMsg;
	Table errorTable;

	String joinCode;

	boolean gotResponse = false;
	int code;
	String response;
	String successfulJoinCode;
	Vector<String> playerNames;

	public JoinScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 8;
		this.game.connection.joinScreen = this;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.playerNames = new Vector<String>();
		this.cam = new OrthographicCamera();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.cam.update();
	}

	public void joinRoomUnsuccessful(int c, String r) {
		this.gotResponse = true;
		this.code = c;
		this.response = r;
	}

	public void joinRoomSuccessful(int c, Vector<String> pn) {
		this.gotResponse = true;
		this.code = c;
		this.playerNames = pn;
		this.successfulJoinCode = joinCode;
	}

	@Override
	public void show() {
		setFonts(100);
		stage.getViewport().setCamera(cam);

		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;

		LabelStyle parals = new LabelStyle();
		parals.font = smallFont;

		Label screenTitle = new Label("Join Game", titls);
		Label subtitle = new Label("Enter join Code:", parals);
		Table headTable = new Table();
		headTable.padBottom(600);
		headTable.add(screenTitle);
		headTable.row();
		headTable.add(subtitle).padTop(50);
		headTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(headTable);

		codeField = new TextField("", this.comicSkin);
		codeField.setAlignment(Align.center);

		Table codeTable = new Table();
		codeTable.padTop(120);
		codeTable.add(codeField).width(600).height(150).expand();
		codeTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, DoodleDuelJump.GAME_HEIGHT / 2);
		stage.addActor(codeTable);

		setFonts(60);

		errorTable = new Table();
		stage.addActor(errorTable);
		createButtons(parals);

		Table bottomTable = new Table();
		bottomTable.add(secondMenuScreen).width(220).height(120).expand().padRight(500);
		bottomTable.add(lobbyScreen).width(240).height(120).expand();
		bottomTable.setPosition(DoodleDuelJump.TOTAL_WIDTH / 2, 0.16f * DoodleDuelJump.GAME_HEIGHT);
		stage.addActor(bottomTable);

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
			if (code == 1) {
				LabelStyle labelsty = new LabelStyle();
				labelsty.font = smallFont;
				createErrorMsg(response, labelsty);
				gotResponse = false;
			} else {
				gotResponse = false;
				game.setScreen(new LobbyScreen(game, successfulJoinCode, playerNames));
			}
		}

	}

	public void createButtons(final LabelStyle style) {
		secondMenuScreen = new TextButton("Back", this.comicSkin);
		secondMenuScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.setScreen(new SecondMenuScreen(game, game.pBest));
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		lobbyScreen = new TextButton("Join", this.comicSkin);
		lobbyScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				joinCode = codeField.getText();

				if (errorMsg != null) {
					errorTable.clearChildren();
					errorMsg.remove();
				}

				if (joinCode.length() > 6 || joinCode.length() < 6 || joinCode.contains(" ")) {
					createErrorMsg("Invalid join code.", style);
					return;
				}

				game.connection.requestJoinRoom(joinCode);
				System.out.println("code: " + joinCode);
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
				0.16f * DoodleDuelJump.GAME_HEIGHT);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	public void setFonts(int size) {
		comicSkin = new Skin();

		// For the box
		smallFontParams.size = size; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);

		// for the labels
		smallFontParams.size = 80; // font size
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
