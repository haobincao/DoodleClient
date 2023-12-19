package com.mygdx.doodlegui.screen;

import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;

public class DeadScreen implements Screen {

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
	BitmapFont playerFont;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter bigFontParams;
	FreeTypeFontParameter playerFontParams;
	TextField clientMessage;
	ScrollPane chatRoomBox;
	Table chatRoomTable;
	Table playerTable;
	TextField backgroundChat;
	TextField newMessage;
	TextButton sendMessage;
	TextButton joinScreen;
	TextField codeField;

	Vector<String[]> orderedPlayerScores; // should be in descending order
	ScoreComparator scoreComp;

	int maxPlayers = 5; // NO more than 6 please!
	boolean gotResponse = false;
	boolean gameOver = false;
	String winner;

	public DeadScreen(final DoodleDuelJump g) {
		this.game = g;
		this.game.screenID = 11;
		this.game.connection.deadScreen = this;
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.playerFontParams = new FreeTypeFontParameter();
		this.cam = new OrthographicCamera();
		this.orderedPlayerScores = new Vector<String[]>();
		this.viewport = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.stage = new Stage(this.viewport);
		this.scoreComp = new ScoreComparator();
		this.cam.update();
	}

	public void setGameOver(int p, String w) {
		this.gameOver = true;
		this.game.pBest = p;
		this.winner = w;
	}

	public void updatePlayerScores(Vector<String[]> scores) {
		if (scores.size() > 5) {
			return;
		}

		Collections.sort(scores, scoreComp);
		this.orderedPlayerScores = scores;
		renderPlayerTable();
	}

	public void renderPlayerTable() {
		if (playerTable == null) {
			return;
		}
		playerTable.clearChildren();

		playerTable.top();

		LabelStyle playls = new LabelStyle();
		playls.font = playerFont;

		for (int i = 0; i < orderedPlayerScores.size(); i++) {
			Label newLab = new Label(Integer.toString(i + 1) + ". " + orderedPlayerScores.get(i)[0] + ": "
					+ orderedPlayerScores.get(i)[1], playls);
			newLab.setWrap(true);
			playerTable.add(newLab).width(500).padTop(20).row();
		}

	}

	@Override
	public void show() {
		setFonts();
		stage.getViewport().setCamera(cam);

		// Left side of the Screen
		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;

		LabelStyle parals = new LabelStyle();
		parals.font = smallFont;

		Label screenTitle = new Label("You are", titls);
		Label screenTitlee = new Label("Dead!", titls);
		Label subtitle = new Label("Leaderboard", parals);
		Table headTable = new Table();
		headTable.setPosition(0.28f * DoodleDuelJump.TOTAL_WIDTH, 0.78f * DoodleDuelJump.GAME_HEIGHT);
		headTable.add(screenTitle);
		headTable.row();
		headTable.add(screenTitlee);
		headTable.row();
		headTable.add(subtitle).padTop(50);
		stage.addActor(headTable);

		playerTable = new Table();
		playerTable.setPosition(0.28f * DoodleDuelJump.TOTAL_WIDTH, 0.56f * DoodleDuelJump.GAME_HEIGHT);
		playerTable.top();
		stage.addActor(playerTable);

		LabelStyle playls = new LabelStyle();
		playls.font = playerFont;

		createButtons();

		Table bottomTable = new Table();
		bottomTable.setPosition(0.28f * DoodleDuelJump.TOTAL_WIDTH, 0.1f * DoodleDuelJump.GAME_HEIGHT);
		bottomTable.add(joinScreen).width(450).height(120).expand();
		stage.addActor(bottomTable);

		// Chat room Setup

		chatRoomTable = new Table();
		chatRoomTable.bottom();

		chatRoomBox = new ScrollPane(chatRoomTable, comicSkin);
		chatRoomBox.setPosition(0.5625f * DoodleDuelJump.TOTAL_WIDTH, 0.1666f * DoodleDuelJump.GAME_HEIGHT);
		chatRoomBox.setSize(620, 930);
		chatRoomBox.setForceScroll(false, true);
		chatRoomBox.setScrollingDisabled(true, false);
		chatRoomBox.setFadeScrollBars(false);
		chatRoomBox.setClamp(true);
		chatRoomBox.setVariableSizeKnobs(false);

		backgroundChat = new TextField("", this.comicSkin);
		backgroundChat.setDisabled(true);

		Table outTable = new Table();
		outTable.setPosition(0.55625f * DoodleDuelJump.TOTAL_WIDTH, 0.1416f * DoodleDuelJump.GAME_HEIGHT);
		outTable.setSize(630, 960);
		outTable.add(backgroundChat).width(630).height(960).expand();
		stage.addActor(outTable);
		stage.addActor(chatRoomBox);

		Table messageTable = new Table();
		messageTable.setPosition(0.75f * DoodleDuelJump.TOTAL_WIDTH, 0.08f * DoodleDuelJump.GAME_HEIGHT);
		newMessage = new TextField("", this.comicSkin);
		createButtons();
		messageTable.add(newMessage).width(500).height(100).expand().padRight(10);
		messageTable.add(sendMessage).height(100).expand();
		stage.addActor(messageTable);

		Gdx.input.setInputProcessor(stage);

	}

	public void createButtons() {
		sendMessage = new TextButton("Send", this.comicSkin);
		sendMessage.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				String message = newMessage.getText();
				if (message != "" && !message.matches("[ ]+")) {
					game.connection.sendMessageBroadcast(message);
					newMessage.setText("");
				}
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});

		joinScreen = new TextButton("Leave Lobby", this.comicSkin);
		joinScreen.addListener(new InputListener() {
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				game.connection.sendLeaveLobby();
				game.setScreen(new SecondMenuScreen(game, game.pBest));
				// Should notify server that they left
				Sound jumpSound = Gdx.audio.newSound(Gdx.files.internal("final/audio/jump.mp3"));
				jumpSound.play(1.0f);
			}

			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				return true;
			}
		});
	}

	public void addChatMessage(String username, String msg) {
		LabelStyle parals = new LabelStyle();
		smallFont.getData().markupEnabled = true;
		parals.font = smallFont;
		Label newLab = new Label("[#000000]" + username + ": [#666666]" + msg, parals);
		newLab.setWrap(true);
		chatRoomTable.bottom();
		chatRoomTable.add(newLab).width(550).padTop(20).row();
		chatRoomBox.scrollTo(0, 0, 0, 0);
	}

	public void setFonts() {
		comicSkin = new Skin();

		// For the box
		smallFontParams.size = 70; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		comicSkin.add("font", smallFont);

		// for the labels
		smallFontParams.size = 80; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);

		playerFontParams.size = 80;
		playerFontParams.color = Color.WHITE;
		playerFontParams.shadowOffsetX = 3;
		playerFontParams.shadowOffsetY = 3;
		playerFontParams.shadowColor = Color.DARK_GRAY;
		playerFont = game.paraFont.generateFont(playerFontParams);

		bigFontParams.size = 150; // font size
		bigFontParams.color = Color.WHITE;
		bigFont = game.titleFont.generateFont(bigFontParams);
		comicSkin.add("title", bigFont);

		comicAtlas = new TextureAtlas(Gdx.files.internal("ComicSkin/comic-ui.atlas"));
		comicSkin.addRegions(comicAtlas);
		comicSkin.load(Gdx.files.internal("ComicSkin/comic-ui.json"));
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);

		cam.update();

		// Stage command makes things a lot easier!
		stage.draw();
		stage.act();

		if (Gdx.input.isKeyJustPressed(Keys.ENTER)) {
			String message = newMessage.getText();
			if (message != "" && !message.matches("[ ]+")) {
				game.connection.sendMessageBroadcast(message);
				newMessage.setText("");
			}
		}

		if (gameOver) {
			this.game.setScreen(new GameOverScreen(this.game, this.winner));
		}

	}

	public void removePlayer(String p, String message) {
		// no need to remove from leaderboard. just show in chat.

		LabelStyle parals = new LabelStyle();
		smallFont.getData().markupEnabled = true;
		parals.font = smallFont;
		Label newLab = new Label("[#FF0000]" + message, parals);
		newLab.setWrap(true);
		chatRoomTable.bottom();
		chatRoomTable.add(newLab).width(550).padTop(40).padBottom(20).row();
		chatRoomBox.scrollTo(0, 0, 0, 0);
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

class ScoreComparator implements Comparator<String[]> {

	@Override
	public int compare(String[] o1, String[] o2) {
		// for descending order
		return Integer.valueOf(o2[1]) - Integer.valueOf(o1[1]);
	}

}