package com.mygdx.doodlegui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mygdx.doodlegui.screen.*;

// Static class. Connects using a socket, keeps a pw and br that can be referenced by anything in the game
public class ClientConnection extends Thread {
	private Socket socket;
	private PrintWriter pw;
	private BufferedReader br;
	private Gson gson;
	private DoodleDuelJump game;

	public LoginScreen loginScreen;
	public RegisterScreen registerScreen;
	public GameScreen gameScreen;
	public HostScreen hostScreen;
	public JoinScreen joinScreen;
	public LobbyScreen lobbyScreen;
	public DeadScreen deadScreen;

	private boolean gameRunning = true;

	public ClientConnection(DoodleDuelJump g, String host, int port) {
		try {
			socket = new Socket(host, port);
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			pw = new PrintWriter(socket.getOutputStream());
			this.game = g;
			this.gson = new Gson();
			this.start();
		} catch (Exception e) {
			System.out.println("Unable to connect.");
		}
	}

	public void sendMessage(String message) {
		pw.println(message);
		pw.flush();
	}

	public void run() {
		while (gameRunning) {
			try {
				String msg = br.readLine();

				JsonObject jo = JsonParser.parseString(msg).getAsJsonObject();
				String response = jo.get("response").getAsString();
				if (response.compareTo("CLIENTCLOSED") == 0) {
					break;
				}

				parseJson(msg);
			} catch (IOException ioe) {
				System.out.println("Failed to read server json file.");
			}
			System.out.println("game running");
		}
	}

	public void parseJson(String message) {
		if (message == null) {
			return;
		}

		System.out.println(message);
		JsonObject jo = JsonParser.parseString(message).getAsJsonObject();

		String response = jo.get("response").getAsString();
		JsonObject content = jo.get("content").getAsJsonObject();
		int code = jo.get("code").getAsInt();

		switch (response) {
			case ("SIGNIN"):
				processSignin(content, code);
				break;
			case ("SIGNUP"):
				processSignup(content, code);
				break;
			case ("LEADERBOARD"):
				processLeaderboard(content);
				break;
			case ("HOSTROOM"):
				processHostRoom(content);
				break;
			case ("JOINROOM"):
				processJoinRoom(content, code);
				break;
			case ("NEWPLAYER"):
				processNewPlayer(content);
				break;
			case ("GAMESTART"):
				processGameStart();
				break;
			case ("MESSAGE"):
				processMessage(content);
				break;
			case ("LEAVEROOM"):
				processLeaveRoom(content);
				break;
			case ("DISMISSROOM"):
				processDismissRoom(content);
				break;
			case ("PLAYERSCORE"):
				processPlayerScore(content);
				break;
			case ("PLAYERDEAD"):
				processPlayerDead(content);
				break;
			case ("GAMEOVER"):
				processGameOver(content);
				break;
			default:
				break;
		}
	}

	public void requestGuestUser() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "GUESTSIGNIN");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processGameOver(JsonObject content) {
		int pBest = content.get("personal_best").getAsInt();
		String winner = content.get("winner").getAsString();
		if (this.game.getScreenID() == 10) {
			this.gameScreen.setGameOver(pBest, winner);
		} else if (this.game.getScreenID() == 11) {
			this.deadScreen.setGameOver(pBest, winner);
		}
	}

	public void sendSignOut() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "SIGNOUT");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	public void sendClientClosed() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "CLIENTCLOSED");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	public void sendPlayerScore(int score) {
		JsonObject request = new JsonObject();
		request.addProperty("request", "PLAYERSCORE");

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("score", score);
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processDismissRoom(JsonObject content) {

		if (this.game.getScreenID() == 9) {
			this.lobbyScreen.dismissRoom();
		}

	}

	private void processLeaveRoom(JsonObject content) {
		String player = content.get("player").getAsString();
		String message = content.get("message").getAsString();

		if (this.game.getScreenID() == 7) {
			this.hostScreen.removePlayer(player, message);
		} else if (this.game.getScreenID() == 9) {
			this.lobbyScreen.removePlayer(player, message);
		} else if (this.game.getScreenID() == 11) {
			this.deadScreen.removePlayer(player, message);
		}
	}

	private void processNewPlayer(JsonObject content) {
		String name = content.get("player").getAsString();
		String message = content.get("message").getAsString();

		if (this.game.getScreenID() == 7) {
			this.hostScreen.addNewPlayer(name, message);
		} else if (this.game.getScreenID() == 9) {
			this.lobbyScreen.addNewPlayer(name, message);
		}

	}

	public void sendLeaveLobby() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "LEAVEROOM");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));

	}

	public void sendDismissRoom() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "DISMISSROOM");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));

	}

	private void processPlayerDead(JsonObject content) {
		// Do nothing.
	}

	public void sendPlayerDeath() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "PLAYERDEAD");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));

	}

	private void processPlayerScore(JsonObject content) {
		JsonArray playerInfo = content.get("players").getAsJsonArray();

		// Set<String> players = content.keySet();
		// Vector<String[]> playerHeights = new Vector<String[]>();
		// for (String p: players) {
		// String playerHeight = playerInfo.get(p).getAsString();
		// String [] playerCombo = {p, playerHeight};
		// playerHeights.add(playerCombo);
		// }

		Vector<String[]> playerScores = new Vector<String[]>();

		for (JsonElement s : playerInfo) {
			JsonObject info = s.getAsJsonObject();
			String playerName = info.get("player").getAsString();
			String playerScore = info.get("score").getAsString();
			String[] newInfo = { playerName, playerScore };
			playerScores.add(newInfo);
		}

		if (this.game.getScreenID() == 10) {
			this.gameScreen.updatePlayerScores(playerScores);
		}

		if (this.game.getScreenID() == 11) {
			this.deadScreen.updatePlayerScores(playerScores);
		}

	}

	private void processMessage(JsonObject content) {

		// messages format not set up properly in doodleServer code/??
		String nickname = content.get("nickname").getAsString();
		String message = content.get("message").getAsString();

		// Set<String> messages = content.keySet();
		// Vector<String[]> playerMessages = new Vector<String[]>();
		// for (String s: messages) {
		// String message = content.get(s).getAsString();
		// String [] newMessage = {s, message};
		// playerMessages.add(newMessage);
		// }

		if (this.game.getScreenID() == 7) {
			this.hostScreen.addChatMessage(nickname, message);
		} else if (this.game.getScreenID() == 9) {
			this.lobbyScreen.addChatMessage(nickname, message);
		} else if (this.game.getScreenID() == 11) {
			this.deadScreen.addChatMessage(nickname, message);
		}
	}

	public void sendMessageBroadcast(String message) {

		// ask about this one --> does server just keep track of the username?
		JsonObject request = new JsonObject();
		request.addProperty("request", "MESSAGE");

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("message", message);
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processGameStart() {

		if (this.game.getScreenID() == 7) {
			this.hostScreen.startGame();
		} else if (this.game.getScreenID() == 9) {
			this.lobbyScreen.startGame();
		}
	}

	public void requestGameStart() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "GAMESTART");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processJoinRoom(JsonObject content, int code) {
		String message = content.get("message").getAsString();

		if (code != 0) {
			if (this.game.getScreenID() == 8) {
				this.joinScreen.joinRoomUnsuccessful(code, message);
				return;
			}
		} else {
			JsonArray players = content.get("players").getAsJsonArray();
			Vector<String> playerNames = new Vector<String>();

			for (JsonElement s : players) {
				playerNames.add(s.getAsString());
			}

			if (this.game.getScreenID() == 8) {
				this.joinScreen.joinRoomSuccessful(code, playerNames);
			}
		}

	}

	public void requestJoinRoom(String code) {
		JsonObject request = new JsonObject();
		request.addProperty("request", "JOINROOM");

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("room_code", code);
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processHostRoom(JsonObject content) {

		String roomCode = content.get("room_code").getAsString();

		if (this.game.getScreenID() == 7) {
			this.hostScreen.setRoomCodeSeed(roomCode);
		}
	}

	public void requestHostRoom() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "HOSTROOM");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processLeaderboard(JsonObject content) {
		JsonObject topPlayers = content.get("top_10").getAsJsonObject();
		Iterator<String> keys = topPlayers.keySet().iterator();

		while (keys.hasNext()) {
			String key = keys.next();
			String score = topPlayers.get(key).getAsString();
			if (score.compareTo("null") == 0) {
				continue;
			}
			String[] player = { key, score };
			this.game.leaderboard.add(player);
		}
		String playerRank = content.get("your_ranking").getAsString();
		String playerScore = content.get("your_score").getAsString();
		if (playerRank != null) {
			this.game.playerLeaderboardRanking = Integer.valueOf(playerRank);

		}
		if (playerScore != null) {
			this.game.playerLeaderboardScore = Float.valueOf(playerScore);
		}

	}

	public void requestLeaderboard() {
		JsonObject request = new JsonObject();
		request.addProperty("request", "LEADERBOARD");

		JsonObject requestContent = new JsonObject();
		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processSignup(JsonObject content, int code) {
		String message = content.get("message").getAsString();
		if (code == 1) {
			if (this.game.getScreenID() == 3) {
				this.registerScreen.signUpResponse(code, message);
				return;
			}
		} else {
			if (this.game.getScreenID() == 3) {
				this.registerScreen.signUpResponse(code, message);
			}
		}
	}

	public void requestSignup(String nn, String un, String pass, String em) {

		// too many value params in servercode
		JsonObject request = new JsonObject();
		request.addProperty("request", "SIGNUP");

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("username", un);
		requestContent.addProperty("password", pass);
		requestContent.addProperty("email", em);
		requestContent.addProperty("nickname", nn);

		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	private void processSignin(JsonObject content, int code) {
		String message = content.get("message").getAsString();
		if (code == 1) {
			if (this.game.getScreenID() == 2) {
				this.loginScreen.signinUnsuccessful(code, message);
				return;
			}
		} else {
			if (this.game.getScreenID() == 2) {
				String nickname = content.get("nickname").getAsString();
				int pBest = content.get("personal_best").getAsInt();
				this.loginScreen.signinSuccessful(code, message, nickname, pBest);
			}
		}
	}

	public void requestSignin(String un, String pass) {

		// ALSO process sign outs on page??? or just let them override with new login
		// nexttime they log in?
		JsonObject request = new JsonObject();
		request.addProperty("request", "SIGNIN");

		JsonObject requestContent = new JsonObject();
		requestContent.addProperty("username", un);
		requestContent.addProperty("password", pass);

		request.add("content", requestContent);

		sendMessage(gson.toJson(request));
	}

	public void stopConnection() {
		gameRunning = false;
	}
}

// 2. theme rng on client end instead of receiving from sign in (once game
// starts)
// 6. passwoerd case sentitev???
// 9. notify server of location everytime you stand on platform? server sends
// back most uptodate positions
// 10. game over from server code
// 11. sign out?
// 12. leave obby?
// 13. FIX THE DISPLAYING OF PLAYERS

// highest position
// personal best not best best
// dismissroom

// Figure out how to render table on
