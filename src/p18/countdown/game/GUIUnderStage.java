package p18.countdown.game;

import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

import p18.countdown.data.Defs;
import p18.countdown.highscores.HighscoreManager;
import p18.countdown.multiplayer.Client;
import p18.countdown.multiplayer.Server;
import p18.countdown.player.Player;
import p18.countdown.rounds.client.*;
import p18.countdown.saves.GameSave;
import p18.countdown.saves.Loader;
import p18.countdown.ui.GUIStage;
import p18.countdown.ui.InfoManager;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.Mouse;
import p18.countdown.ui.entity.*;

public class GUIUnderStage extends Thread
{
	private final int TYPE_MAIN_MENU = -1;
	private final int TYPE_NEW_GAME = -2;
	private final int TYPE_RESUME_GAME = -3;
	private final int TYPE_HIGHSCORES = -4;
	private final int TYPE_SINGLE_PLAYER = -5;
	private final int TYPE_MULTI_PLAYER = -6;
	private final int TYPE_FULL_GAME = -7;
	private final int TYPE_CUSTOM_GAME = -8;
	private final int TYPE_ONE_ROUND = -9;
	private final int TYPE_CREATE_SERVER = -10;
	private final int TYPE_JOIN_SERVER = -11;
	private final int MODE_FULL_GAME = -12;
	private final int MODE_CUSTOM_GAME = -13;
	private final int MODE_1_LETTERS = -14;
	private final int MODE_1_NUMBERS = -15;
	private final int MODE_1_CONUNDRUM = -16;
	private final int MODE_CREATE_SERVER = -17;
	private final int MODE_JOIN_SERVER = -18;

	private ArrayList<Entity> entities;
	private EntityTextBox focusedTextBox;
	private EntityLabel roundLabel;
	private EntityLabel answerLabel;
	
	private EntityLabel fpsLabel;

	private int fps;
	private int round;

	private boolean running;
	private boolean menuSet;

	private GUIStage win;

	private Client client;
	private Server server;

	private ClientRound cr;

	private long[] debugSECRET =
	{ 0, 0, 0, 0, 0, 0, 0 };
	private int debugSECRETindex = 0;
	private boolean debugCLICK = false;

	public GUIUnderStage(GUIStage win)
	{
		this.win = win;

		this.entities = new ArrayList<Entity>();
		this.focusedTextBox = null;
		this.roundLabel = new EntityLabel(false);
		this.answerLabel = new EntityLabel(false);

		this.fps = 0;
		this.round = TYPE_MAIN_MENU;

		this.running = true;
		this.menuSet = false;

		this.cr = null;

		HighscoreManager.loadScores();
	}

	private void init()
	{
		ArrayList<EntityButton> buttons = new ArrayList<EntityButton>();
		ArrayList<EntityTextBox> tBoxes = new ArrayList<EntityTextBox>();
		EntityScoreBoard scoreBoard = new EntityScoreBoard();
		EntityClock clock = new EntityClock();

		for (int i = 0; i < 10; i++)
		{
			EntityButton e = new EntityButton("NEW GAME");
			entities.add(e);
			buttons.add(e);
			e.setXY(280, -100);
		}

		for (int i = 0; i < 6; i++)
		{
			EntityTextBox e = new EntityTextBox("Your name:");
			entities.add(e);
			tBoxes.add(e);
			e.setXY(280, -100);
		}

		clock.setXY(-200, -200);

		entities.add(scoreBoard);
		entities.add(clock);
		roundLabel.setLabel("Welcome!");
		roundLabel.setFontSize(72.0f);
		entities.add(roundLabel);
		entities.add(answerLabel);
		
		entities.add(fpsLabel = new EntityLabel(false));
		fpsLabel.setXY(10, 20);

		/*
		 * entities.add(new EntityHighscoreRect()); entities.get(entities.size()
		 * - 1).setXY(100, 60);
		 */

		MenuManager.initMenuButtons(buttons, tBoxes, scoreBoard, clock,
				roundLabel, answerLabel);
	}

	public void run()
	{
		init();
		long fpsTime = System.currentTimeMillis();

		new Thread()
		{ 
			public void run()
			{
				long drawTime = System.currentTimeMillis();
				while(true)
				{
					long newTime = System.currentTimeMillis();
					if (GUIStage.isDrawing() && newTime - drawTime > 16)
					{
						drawTime = newTime;
						fps++;
						win.repaint();
					}
					try
					{
						Thread.sleep(1);
					}
					catch(InterruptedException e) {} ;
				}
			}
		}.start();
		
		while (running)
		{
			sleepOver();
			game();
			updateEntities();
			checkCollisions();
				
			long newTime = System.currentTimeMillis();
			if(newTime - fpsTime > 999)
			{
				fpsLabel.setLabel("" + fps);
				fps = 0;
				fpsTime = newTime;
			}
			
		}
		System.exit(0);
	}

	private void game()
	{
		if (!menuSet)
		{
			switch (round)
			{
				case TYPE_MAIN_MENU: MenuManager.setMenu(MenuManager.TYPE_MAIN);				break;
				case TYPE_NEW_GAME: MenuManager.setMenu(MenuManager.TYPE_SPMP_OPTION);			break;
				case TYPE_SINGLE_PLAYER: MenuManager.setMenu(MenuManager.TYPE_SP_GAME);			break;
				case TYPE_MULTI_PLAYER: MenuManager.setMenu(MenuManager.TYPE_MP_GAME);			break;
				case TYPE_FULL_GAME: MenuManager.setMenu(MenuManager.TYPE_SP_SETUP);			break;
				case TYPE_CUSTOM_GAME: MenuManager.setMenu(MenuManager.TYPE_SP_CUSTOMSETUP);	break;
				case TYPE_ONE_ROUND: MenuManager.setMenu(MenuManager.TYPE_SP_ONE_ROUND);		break;
				case TYPE_CREATE_SERVER: MenuManager.setMenu(MenuManager.TYPE_MP_SETUP_SERVER);	break;
				case TYPE_JOIN_SERVER: MenuManager.setMenu(MenuManager.TYPE_MP_SETUP_CLIENT);	break;
				case TYPE_HIGHSCORES:
				{
					MenuManager.setMenu(MenuManager.TYPE_HIGHSCORE);
					HighscoreManager.updateBoard("-1");

					break;
				}
				case TYPE_RESUME_GAME:
				{
					round = TYPE_MAIN_MENU;
					GameSave gs = Loader.gameSaves(false, new Player[Defs.MAX_PLAYERS], 0, new ArrayList<String>());
					if (gs != null) runGame(gs.players[0].getName(), gs.players.length, true, gs.rounds, gs.round, true, gs.players);
					break;
				}
				case MODE_1_LETTERS:
				{
					ArrayList<String> rnds = new ArrayList<String>();
					rnds.add("word");
					runGame("You", 1, true, rnds, 0, false, null);
					break;
				}
				case MODE_1_NUMBERS:
				{
					ArrayList<String> rnds = new ArrayList<String>();
					rnds.add("number");
					runGame("You", 1, true, rnds, 0, false, null);
					break;
				}
				case MODE_1_CONUNDRUM:
				{
					ArrayList<String> rnds = new ArrayList<String>();
					rnds.add("conundrum");
					runGame("You", 1, true, rnds, 0, false, null);
					break;
				}
				case MODE_FULL_GAME:
				{
					String[] input = MenuManager.getTextBoxesInfo();
					if (input[0] == null || input[0].trim().equals(""))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "You must enter your name!");
						round = TYPE_FULL_GAME;
						break;
					}
					runGame(input[0], 5, 1);
					break;
				}
				case MODE_CUSTOM_GAME:
				{
					String[] input = MenuManager.getTextBoxesInfo();
					if (input[0] == null || input[0].trim().equals(""))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "You must enter your name!");
						round = TYPE_CUSTOM_GAME;
						break;
					}
					int nOFr = 1;
					boolean custom = false;
					boolean create = true;
					if (Defs.isNumeric(input[1]))
					{
						nOFr = Integer.parseInt(input[1]);
						if(nOFr < 1)
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "'Rounds' must be above 0");
							round = TYPE_CUSTOM_GAME;
							break;
						}
					}
					else
					{
						custom = true;
						if (!input[1].trim().equals(""))
						{
							input[1] = input[1].toLowerCase();
							for (int i = 0; i < input[1].length(); i++)
								if (input[1].charAt(i) != 'c'
										&& input[1].charAt(i) != 'l'
										&& input[1].charAt(i) != 'n')
								{
									InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING,"'" + input[1].charAt(i) + "' is not assigned to a round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'L' for letters round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'N' for numbers round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'C' for conundrum round.");
									create = false;
									break;
								}
						}
						else
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING,
									"'Rounds' must be a number or a pattern!");
							create = false;
						}
						if (!create)
						{
							round = TYPE_CUSTOM_GAME;
							break;
						}
					}

					if (!custom) runGame(input[0], nOFr, 1);
					else
					{
						ArrayList<String> rounds = Defs.getRoundsFromPattern(input[1]);
						runGame(input[0], 1, true, rounds, 0, false, null);
					}
					break;
				}
				case MODE_CREATE_SERVER:
				{
					String[] input = MenuManager.getTextBoxesInfo();
					if (input[0] == null || input[0].trim().equals(""))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "You must enter your name!");
						round = TYPE_CREATE_SERVER;
						break;
					}
					int nOFr = 1;
					int players;
					boolean custom = false;
					boolean create = true;
					if (Defs.isNumeric(input[1]))
					{
						nOFr = Integer.parseInt(input[1]);
						if(nOFr < 1)
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "'Rounds' must be above 0");
							round = TYPE_CREATE_SERVER;
							break;
						}
					}
					else
					{
						custom = true;
						if (!input[1].trim().equals(""))
						{
							input[1] = input[1].toLowerCase();
							for (int i = 0; i < input[1].length(); i++)
								if (input[1].charAt(i) != 'c' && input[1].charAt(i) != 'l' && input[1].charAt(i) != 'n')
								{
									InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING,"'" + input[1].charAt(i) + "' is not assigned to a round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'L' for letters round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'N' for numbers round.");
									InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "'C' for conundrum round.");
									create = false;
									break;
								}
						}
						else
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "'Rounds' must be a number or a pattern!");
							create = false;
						}
						if (!create)
						{
							round = TYPE_CUSTOM_GAME;
							break;
						}
					}
					if (Defs.isNumeric(input[2]))
					{
						players = Integer.parseInt(input[2]);
						System.out.println(players);
						if(players < 1 || players > Defs.MAX_PLAYERS)
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "'Players' range must be between 1 and " + Defs.MAX_PLAYERS);
							round = TYPE_CREATE_SERVER;
							break;
						}
					}
					else
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "'Players' must be a number!");
						System.out.println(input[2]);
						round = TYPE_CREATE_SERVER;
						break;
					}
					if (!custom) runGame(input[0], nOFr, players);
					else
					{
						ArrayList<String> rounds = Defs.getRoundsFromPattern(input[1]);
						runGame(input[0], players, true, rounds, 0, false, null);
					}
					break;
				}
				case MODE_JOIN_SERVER:
				{
					String[] input = MenuManager.getTextBoxesInfo();
					if (input[0] == null || input[0].trim().equals(""))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "You must enter your name!");
						round = TYPE_JOIN_SERVER;
						break;
					}
					else if (input[1] == null || input[1].trim().equals(""))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "You must enter the IP address!");
						round = TYPE_JOIN_SERVER;
						break;
					}
					joinGame(input[0], input[1]);
					break;
				}
				default:
				{
					if (!client.isAlive())
					{
						round = TYPE_MAIN_MENU;
						client = null;
						if (server != null) server = null;
						break;
					}
					break;
				}
			}
			menuSet = true;
		}
	}

	private void runGame(String name, int nOFr, int players)
	{
		runGame(name, players, false, null, nOFr, false, null);
	}

	private void runGame(String name, int playersCount, boolean custom,
			ArrayList<String> rounds, int currRound, boolean load,
			Player[] players)
	{
		InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "Creating server");
		roundLabel.setLabel("Welcome");
		try
		{
			server = new Server(playersCount);
			server.start();
			client = new Client("localhost", this);
			client.start();

			client.getOUT().println("name~" + name);
			if (custom)
			{
				client.getOUT().println("round~set~" + currRound);

				String msg = "round~rounds~";
				for (String str : rounds)
					msg += str + "`";
				client.getOUT().println(msg);
				if (load)
				{
					client.getOUT().println("load");
					for (Player p : players)
						client.getOUT().println("addPlayer~" + p.getName() + "~" + p.getScore());
				}
			}
			else
			{
				client.getOUT().println("defaultRounds~" + currRound);
			}
			client.getOUT().println("set");
			MenuManager.setMenu(MenuManager.TYPE_ROUND);
			round = currRound;
		}
		catch (IOException e)
		{
			InfoManager.pushInfo(EntityInfoBox.TYPE_ERROR, "Failed to create server!");
		}

	}

	private void joinGame(String name, String ip)
	{
		MenuManager.setMenuSettings(MenuManager.SETTING_MENU_DISABLE_JOIN);
		InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "Connecting to server...");
		roundLabel.setLabel("Welcome");

		client = new Client(ip, this);
		client.start();

		if (!client.isAlive() || client.getOUT() == null)
		{
			round = TYPE_JOIN_SERVER;
			MenuManager.setMenuSettings(MenuManager.SETTING_MENU_ENABLE_JOIN);
			InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "Connection failed");
			return;
		}
		client.getOUT().println("name~" + name);
		MenuManager.setMenu(MenuManager.TYPE_ROUND);
		InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "Successfully connected");
		round = 1;
	}

	private void updateEntities()
	{
		for (Entity e : entities)
			e.update();
		for (Entity e : InfoManager.getEntities())
			e.update();
		if (cr != null) for (Entity e : cr.getEntities())
			e.update();
		else if (HighscoreManager.isShowing()) for (Entity e : HighscoreManager.getEntities())
			e.update();

		InfoManager.update();
	}

	private void checkCollisions()
	{
		Mouse mouse = win.getMouse();
		if (mouse == null) return;

		boolean collision = false;

		ArrayList<Entity> clone = new ArrayList<Entity>(entities);
		int indexRoundEntities = entities.size();
		if (cr != null) clone.addAll(cr.getEntities());

		// RESETS
		if (mouse.getMousePressed())
		{
			for (Entity e : clone)
				e.setFocused(false);
			focusedTextBox = null;
		}

		for (Entity e : clone)
			if (e.getCollisionBounds().contains(mouse.getMousePos()))
			{
				collision = true;
				e.setMouseOver(true);
				if (!e.isActive()) continue;
				if (mouse.getMousePressed() && mouse.getMouseButton() == MouseEvent.BUTTON1)
				{
					e.setMousePressed(true);
					e.setFocused(true);
					if (e.getType().equals("textbox")) focusedTextBox = (EntityTextBox) e;
					else if (!debugCLICK && e.getType().equals("clock"))
					{
						long time = System.currentTimeMillis();
						debugCLICK = true;
						if (debugSECRETindex == debugSECRET.length || (debugSECRETindex > 0 && time - debugSECRET[debugSECRETindex - 1] > 2000))
							debugSECRETindex = 0;
						debugSECRET[debugSECRETindex++] = System
								.currentTimeMillis();
						checkForSECRET();
					}
					// System.out.println("O");
				}
				else if (!mouse.getMousePressed())
				{
					debugCLICK = false;
					if (mouse.getMouseButton() == MouseEvent.BUTTON1)
					{
						mouse.setMouseButton(0);
						if (indexRoundEntities <= clone.indexOf(e)) cr.entityPressed(clone.indexOf(e) - indexRoundEntities);
						else if (e.getType().equals("button"))
						{
							menuSet = false;
							buttonPressed(entities.indexOf(e) % 5);
							System.out.println("BUTTON PRESSED!!! :: " + entities.indexOf(e) % 5);
						}
					}
					e.setMousePressed(false);
				}
				else e.setFocused(false);
			}
			else
			{
				e.setMouseOver(false);
				e.setMousePressed(false);
			}

		if (!collision) mouse.setMouseButton(0);
	}

	private void buttonPressed(int buttonID)
	{
		if (round > TYPE_MAIN_MENU)
		{
			switch (buttonID)
			{
				case 0: client.getOUT().println("round~msg~" + cr.submitMsg());	break; // SUBMIT
				case 1: client.getOUT().println("ready");						break; // READY
				case 2:
				{
					client.requestRounds();
					ArrayList<String> rounds;
					long time = System.currentTimeMillis();
					while ((rounds = client.takeRounds()) == null)
					{
						sleepOver();
						if (System.currentTimeMillis() - time > 1000)
						{
							InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING,
									"Could not get data from server");
							break;
						}
					}

					if (rounds != null)
					{
						ArrayList<Player> players = new ArrayList<Player>();
						for (Player p : Client.getPlayers())
							if (p != null) players.add(p);
						Player[] playersArr = new Player[players.size()];
						for (int i = 0; i < playersArr.length; i++)
							playersArr[i] = players.get(i);
						Loader.gameSaves(true, playersArr, round, rounds);
					}
					break;
				}
				case 3:
				{
					if (client != null) client.getOUT().println("disconnect");
					round = TYPE_MAIN_MENU;
					cr = null;
					if (client != null)
					{
						client.closeConnection();
						client = null;
					}
					if (server != null)
					{
						server.closeConnections();
						server = null;
					}
					MenuManager
							.setRoundSettings(MenuManager.SETTING_ROUND_ANSWERS_HIDE);
					break;
				}
			}
		}
		else if (round == TYPE_MAIN_MENU)
		{
			switch (buttonID)
			{
				case 0: round = TYPE_NEW_GAME;		break;
				case 1: round = TYPE_RESUME_GAME;	break;
				case 2: round = TYPE_HIGHSCORES;	break;
				case 3: running = false;			break; // EXIT
			}
		}
		else if (round == TYPE_NEW_GAME)
		{
			switch (buttonID)
			{
				case 0: round = TYPE_SINGLE_PLAYER;	break;
				case 1: round = TYPE_MULTI_PLAYER;	break;
				case 2: round = TYPE_MAIN_MENU;		break;
			}
		}
		else if (round == TYPE_SINGLE_PLAYER)
		{
			switch (buttonID)
			{
				case 0: round = TYPE_FULL_GAME;		break;
				case 1: round = TYPE_CUSTOM_GAME;	break;
				case 2: round = TYPE_ONE_ROUND;		break;
				case 3: round = TYPE_NEW_GAME;		break;
			}
		}
		else if (round == TYPE_MULTI_PLAYER)
		{
			switch (buttonID)
			{
				case 0:round = TYPE_CREATE_SERVER;	break;
				case 1:round = TYPE_JOIN_SERVER;	break;
				case 2:round = TYPE_NEW_GAME;		break;
			}
		}
		else if (round == TYPE_FULL_GAME)
		{
			switch (buttonID)
			{
				case 0:round = MODE_FULL_GAME;break;
				case 1:round = TYPE_SINGLE_PLAYER;break;
			}
		}
		else if (round == TYPE_CUSTOM_GAME)
		{
			switch (buttonID)
			{
				case 0:round = MODE_CUSTOM_GAME;break;
				case 1:round = TYPE_SINGLE_PLAYER;break;
			}
		}
		else if (round == TYPE_ONE_ROUND)
		{
			switch (buttonID)
			{
				case 0:round = MODE_1_LETTERS;break;
				case 1:round = MODE_1_NUMBERS;break;
				case 2:round = MODE_1_CONUNDRUM;break;
				case 3:round = TYPE_SINGLE_PLAYER;break;
			}
		}
		else if (round == TYPE_CREATE_SERVER)
		{
			switch (buttonID)
			{
				case 0:round = MODE_CREATE_SERVER;break;
				case 1:round = TYPE_MULTI_PLAYER;break;
			}
		}
		else if (round == TYPE_JOIN_SERVER)
		{
			switch (buttonID)
			{
				case 0:round = MODE_JOIN_SERVER;break;
				case 1:round = TYPE_MULTI_PLAYER;break;
			}
		}
		else if (round == TYPE_HIGHSCORES)
		{
			switch (buttonID)
			{
				case 0:
					round = TYPE_MAIN_MENU;
					HighscoreManager.hide();
					break;
			}
		}
	}

	public void typing(char character)
	{
		if (focusedTextBox != null && focusedTextBox.isFocused())
		{
			focusedTextBox.input(character);
			if (HighscoreManager.isShowing()) HighscoreManager
					.updateBoard(focusedTextBox.getInput());
		}
		else focusedTextBox = null;
		if (cr != null) cr.typing(character);
	}

	public ArrayList<Entity> getEntities()
	{
		ArrayList<Entity> clone = new ArrayList<Entity>(entities);
		if (cr != null) clone.addAll(cr.getEntities());
		else if (HighscoreManager.isShowing()) clone.addAll(HighscoreManager
				.getEntities());
		clone.addAll(InfoManager.getEntities());
		return clone;
	}

	public void setCurrentRound(String currRoundType)
	{
		MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_CLOCK_RESET);
		if (currRoundType.equals("word")) cr = new ClientWordRound(client);
		else if (currRoundType.equals("number")) cr = new ClientNumberRound(client);
		else if (currRoundType.equals("conundrum")) cr = new ClientConundrumRound(client);
		else if (currRoundType.equals("slots")) cr = new ClientSlotsRound(client);
	}

	public void setCurrentRound(int round)
	{
		this.round = round;
		if (round == 0) roundLabel.setLabel("Welcome");
		else roundLabel.setLabel("Round " + round);
	}

	public void setAnswers(String ans)
	{
		answerLabel.setLabel(ans);
	}

	public ClientRound getCurrentRound()
	{
		return cr;
	}

	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	private void checkForSECRET()
	{
		long[] SECRETdebug = new long[6];
		SECRETdebug[0] = debugSECRET[1] - debugSECRET[0];
		SECRETdebug[1] = debugSECRET[2] - debugSECRET[1];
		SECRETdebug[2] = debugSECRET[3] - debugSECRET[2];
		SECRETdebug[3] = debugSECRET[5] - debugSECRET[3];
		SECRETdebug[4] = debugSECRET[6] - debugSECRET[4];
		SECRETdebug[5] = debugSECRET[6] - debugSECRET[5];

		if (Math.abs(SECRETdebug[0] - 200) < 200
				&& Math.abs(SECRETdebug[1] - 430) < 200
				&& Math.abs(SECRETdebug[2] - 380) < 200
				&& Math.abs(SECRETdebug[3] - 600) < 200
				&& Math.abs(SECRETdebug[4] - 1175) < 400
				&& Math.abs(SECRETdebug[5] - 700) < 300)
		{
			client.getOUT().println("SECRET");
			System.out.println("SECRET");
		}
	}
}
