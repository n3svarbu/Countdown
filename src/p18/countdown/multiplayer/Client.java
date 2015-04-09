package p18.countdown.multiplayer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

import p18.countdown.data.Defs;
import p18.countdown.game.GUIUnderStage;
import p18.countdown.highscores.HighscoreManager;
import p18.countdown.player.Player;
import p18.countdown.ui.InfoManager;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.entity.EntityInfoBox;

public class Client extends Thread
{
	private Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	
	private GUIUnderStage stage;
	private boolean running;
	
	private static Player[] players;

	private ArrayList<String> rounds;

	public Client(String ip, GUIUnderStage stage)
	{
		this.stage = stage;
		this.running = false;

		try
		{
			this.sock = new Socket(ip, Defs.TCP_PORT);
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			this.out = new PrintWriter(sock.getOutputStream(), true);
		}
		catch (UnknownHostException e)
		{
			InfoManager.pushInfo(EntityInfoBox.TYPE_ERROR, "Bad IP address!");
			return;
		}
		catch (IOException e)
		{
			InfoManager.pushInfo(EntityInfoBox.TYPE_ERROR, "No response from server!");
			System.out.println("Server unresponsive");
			return;
		}

		Client.players = new Player[Defs.MAX_PLAYERS];
	}
	
	public void run()
	{
		this.running = true;
		while(running && sock != null && !sock.isClosed())
		{
			sleepOver();
			if(sock.isInputShutdown() || sock.isOutputShutdown())
			{
				System.out.println("Server offline");
				break;
			}
			try
			{
				String line;
				try { line = in.readLine(); }
				catch(SocketException e)
				{
					InfoManager.pushInfo(EntityInfoBox.TYPE_INFO, "Server closed the connection");
					running = false;
					break;
				}
				if(line == null)
				{
					running = false;
					break;
				}
				String[] data = line.split("~");
				if(data[0].equals("addPlayer"))
				{
					int player = Integer.parseInt(data[1]);
					System.out.println("Player " + (player + 1) + " joined the game.");
					addPlayer(player);
				}
				else if(data[0].equals("player"))
				{
					int i = Integer.parseInt(data[1]);
					if(data[2].equals("name"))
					{
						System.out.println(players[i].getName() + " changed name to " + data[3]);
						players[i].setName(data[3]);
					}
					else if(data[2].equals("isReady"))
					{
						System.out.println(players[i].getName() + " is ready");
						players[i].setReady(true);
					}
					else if(data[2].equals("score"))
						players[i].setScore(Integer.parseInt(data[3]));
					else if(data[2].equals("ping"))
						players[i].setPing(Integer.parseInt(data[3]));
					else if(data[2].equals("notReady"))
						players[i].setReady(false);
					else if(data[2].equals("submitted"))
						players[i].setSubmitted(true);
					else if(data[2].equals("notSubmitted"))
						players[i].setSubmitted(false);
					else if(data[2].equals("disconnected"))
						players[i].disconnect();
					else if(data[2].equals("clear"))
						players[i] = null;
				}
				else if(data[0].equals("round"))
				{
					if(data[1].equals("create"))
					{
						stage.setCurrentRound(data[2]);
						MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_ANSWERS_HIDE);
					}
					else if(data[1].equals("set"))
						stage.setCurrentRound(Integer.parseInt(data[2]));
					else if(data[1].equals("msg"))
						stage.getCurrentRound().msg(data[2]);
					else if(data[1].equals("end"))
						stage.getCurrentRound().end();
					else if(data[1].equals("answers"))
					{
						stage.setAnswers(data[2].replace("/n", "\n"));
						MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_ANSWERS_SHOW);
					}
				}
				else if(data[0].equals("client"))
				{
					if(data[1].equals("beReady"))
						MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_READY);
					else if(data[1].equals("isReady"))
						MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_IDLE);
					else if(data[1].equals("info"))
						InfoManager.pushInfo(Integer.parseInt(data[2]), data[3]);
					else if(data[1].equals("finish"))
					{
						int nOFr = Integer.parseInt(data[2]);
						if(nOFr > 1)
						{
							for(Player p : players)
								if(p != null)
									HighscoreManager.addScore(nOFr, p.getName(), p.getScore());
							HighscoreManager.saveScores();
						}
					}
				}
				else if(data[0].equals("`p"))
					out.println("`p");
				else if(data[0].equals("TAKE YOUR ROUNDS!"))
				{
					rounds = new ArrayList<String>();
					rounds.addAll(Arrays.asList(data[1].split("`")));
				}
				else System.out.println("CLIENT: Unhandled message: " + line);
			}
			catch (IOException e) { System.out.println("CLIENT: Error on handling string"); }
            catch (ArrayIndexOutOfBoundsException e) { System.out.println("CLIENT: Error on handling message"); }
		}
		
		MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_EXIT);
	}
	
	public PrintWriter getOUT()
	{
		return out;
	}

	public void closeConnection()
	{
		try { sock.close(); }
		catch(IOException e) { System.out.println("Error on closing socket @class Client @function closeConnection()"); }
	}
	
	public void addPlayer(int ID)
	{
		players[ID] = new Player("Player " + (ID + 1));
	}
	
	public static Player[] getPlayers()
	{
		return players;
	}


	public void requestRounds()
	{
		out.println("I NEED ROUNDS!");
	}

	public ArrayList<String> takeRounds()
	{
		if(rounds == null) return null;
		ArrayList<String> rs = new ArrayList<String>(rounds);
		rounds = null;
		return rs;
	}
	
	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
}
