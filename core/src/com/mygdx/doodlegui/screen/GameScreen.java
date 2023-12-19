package com.mygdx.doodlegui.screen;

import java.util.Collections;
import java.util.Vector;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.mygdx.doodlegui.DoodleDuelJump;
import com.mygdx.doodlegui.model.Level;

public class GameScreen implements Screen {
	public final DoodleDuelJump game;
	public Level level;
	public OrthographicCamera cam;
	public Vector<String[]> orderedPlayerScores; // will need to cast String heights as floats, etc.
	public String seed;
	public boolean gameOver = false;

	public FitViewport leftSide;
	public FitViewport rightSide;
	public OrthographicCamera leaderBoardcam;

	FreeTypeFontParameter playerFontParams;
	FreeTypeFontParameter myFontParams;
	FreeTypeFontParameter smallFontParams;
	FreeTypeFontParameter bigFontParams;
	ScoreComparator scoreComp;
	Stage stage;
	Table playerTable;
	BitmapFont playerFont;
	BitmapFont smallFont;
	BitmapFont bigFont;
	BitmapFont myFont;

	public Texture texture;
	String winner;

	public GameScreen(final DoodleDuelJump g, String s) {
		this.game = g;
		this.game.screenID = 10;
		this.game.connection.gameScreen = this;
		this.seed = s;
		this.cam = new OrthographicCamera(DoodleDuelJump.GAME_WIDTH, DoodleDuelJump.GAME_HEIGHT);
		this.leftSide = new FitViewport(DoodleDuelJump.GAME_WIDTH, DoodleDuelJump.GAME_HEIGHT, cam);
		this.leftSide.setScreenBounds(0, 0, DoodleDuelJump.GAME_WIDTH, DoodleDuelJump.GAME_HEIGHT);
		this.leaderBoardcam = new OrthographicCamera(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT);
		this.rightSide = new FitViewport(DoodleDuelJump.TOTAL_WIDTH, DoodleDuelJump.GAME_HEIGHT, leaderBoardcam);
		this.rightSide.setScreenBounds(DoodleDuelJump.GAME_WIDTH, 0, DoodleDuelJump.TOTAL_WIDTH,
				DoodleDuelJump.GAME_HEIGHT);
		cam.position.x = DoodleDuelJump.GAME_WIDTH / 2;
		this.stage = new Stage(rightSide);
		this.playerFontParams = new FreeTypeFontParameter();
		this.smallFontParams = new FreeTypeFontParameter();
		this.bigFontParams = new FreeTypeFontParameter();
		this.myFontParams = new FreeTypeFontParameter();
		scoreComp = new ScoreComparator();
		level = new Level(this);
		game.startMusic("final/audio/gameMusic.mp3");
	}

	public void setGameOver(int p, String w) {
		this.gameOver = true;
		this.game.pBest = p;
		this.winner = w;
	}

	@Override
	public void render(float delta) {
		level.UpdateObjects(delta);

		// problem when resizing
		/* Left Half */
		leftSide.apply();

		game.batch.begin();

		ScreenUtils.clear(0.25882352941f, 0.74117647058f, 0.47843137254f, 1);
		cam.position.y = 0;

		cam.update();
		game.batch.setProjectionMatrix(cam.combined);

		game.batch.draw(texture, 0, cam.position.y - DoodleDuelJump.GAME_HEIGHT / 2);

		cam.position.y = level.jumper.getCurY() + 1 * DoodleDuelJump.GAME_HEIGHT / 3;

		cam.update();
		game.batch.setProjectionMatrix(cam.combined);

		level.DrawObjects(game.batch);
		game.batch.end();

		/* Right Half */
		rightSide.apply();

		leaderBoardcam.update();

		stage.draw();
		stage.act();

		if (gameOver) {
			this.game.setScreen(new GameOverScreen(this.game, this.winner));
		}
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
		
		LabelStyle myls = new LabelStyle();
		myls.font = myFont;

		for (int i = 0; i < orderedPlayerScores.size(); i++) {
			Label newLab;
			if (orderedPlayerScores.get(i)[0].compareTo(this.game.getNickname()) == 0) {
				newLab = new Label(Integer.toString(i + 1) + ". " + orderedPlayerScores.get(i)[0] + ": "
						+ orderedPlayerScores.get(i)[1], myls);
				newLab.setWrap(true);
			} else {
				newLab = new Label(Integer.toString(i + 1) + ". " + orderedPlayerScores.get(i)[0] + ": "
						+ orderedPlayerScores.get(i)[1], playls);
				newLab.setWrap(true);
			}
			
			playerTable.add(newLab).width(500).padTop(20).row();
		}

	}

	public void setFonts() {
		
		smallFontParams.size = 100; // font size
		smallFontParams.color = Color.WHITE;
		smallFont = game.paraFont.generateFont(smallFontParams);
		
		playerFontParams.size = 80;
		playerFontParams.color = Color.WHITE;
		playerFontParams.shadowOffsetX = 3;
		playerFontParams.shadowOffsetY = 3;
		playerFontParams.shadowColor = Color.DARK_GRAY;
		playerFont = game.paraFont.generateFont(playerFontParams);
		
		myFontParams.size = 80;
		myFontParams.color = Color.WHITE;
		myFontParams.borderWidth = 2f;
		myFontParams.borderColor = Color.BLACK;
		myFontParams.shadowOffsetX = 3;
		myFontParams.shadowOffsetY = 3;
		myFontParams.shadowColor = Color.DARK_GRAY;
		myFont = game.paraFont.generateFont(myFontParams);
		
		bigFontParams.size = 80; // font size
		bigFontParams.color = Color.WHITE;
		bigFont = game.titleFont.generateFont(bigFontParams);
	}

	@Override
	public void show() {
		// Called when the screen is generated
		stage.getViewport().setCamera(leaderBoardcam);
		setFonts();

		playerTable = new Table();
		playerTable.setPosition(.83f * DoodleDuelJump.TOTAL_WIDTH, 0.80f * DoodleDuelJump.GAME_HEIGHT);
		playerTable.top();

		stage.addActor(playerTable);
		
		LabelStyle parals = new LabelStyle();
		parals.font = smallFont;
		
		LabelStyle titls = new LabelStyle();
		titls.font = bigFont;

		Label subtitle = new Label("Leaderboard", parals);
		Label title = new Label ("Doodle Duel", titls);
		Label title2 = new Label("Jump", titls);
		subtitle.setWrap(true);
		title.setWrap(true);
		title.setAlignment(Align.center);
		title2.setWrap(true);
		title2.setAlignment(Align.center);
		Table totalTable = new Table();
		totalTable.top();
		totalTable.setPosition(.82f * DoodleDuelJump.TOTAL_WIDTH, 0.95f * DoodleDuelJump.GAME_HEIGHT);
		totalTable.add(subtitle).width(500).padBottom(850).row();
		totalTable.add(title).width(500).row();
		totalTable.add(title2).width(500);
		
		stage.addActor(totalTable);
	}

	@Override
	public void dispose() {
		texture.dispose();
	}

	@Override
	public void resize(int width, int height) {

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
