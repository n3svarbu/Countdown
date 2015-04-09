package p18.countdown.multiplayer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Timer;

import p18.countdown.data.Defs;
import p18.countdown.player.OnlinePlayer;
import p18.countdown.player.Player;
import p18.countdown.rounds.server.*;

public class Server extends Thread
{
	private OnlinePlayer[] players;
	private Player[] loadPlayers;
	private ArrayList<String> rounds;
	private int round;
	private int currPlayer;
	private int numberOFrounds;
	private int nOp;
	private int connected;
	private int startRound;
	
	private ServerRound currentRound;
	
	private	ServerSocket ss;
	private Thread acceptConn;
	
	private boolean running;
	private boolean gameSet;
	private boolean load;

    private boolean debugSECRET = false;

	public Server(int numberOfPlayers) throws IOException
	{
		this.ss = new ServerSocket(Defs.TCP_PORT);
		this.players = new OnlinePlayer[numberOfPlayers];
		this.loadPlayers = new Player[numberOfPlayers];
		this.running = true;
		this.gameSet = false;
		this.load = false;
		this.nOp = numberOfPlayers;
		this.connected = 0;
		this.startRound = 1;
		this.numberOFrounds = 0;

        Timer pingTimer;
		pingTimer = new Timer(1000, new ActionListener()
			{
				public void actionPerformed(ActionEvent ae) { ping(); }
			});
		pingTimer.setRepeats(true);
		pingTimer.start();
	}
	
	private void msgs()
	{
		try
		{
			for(int i = 0; i < players.length; i++)
			{
				OnlinePlayer p = players[i];
				if(p == null) continue;
				if(p.getIN().ready())
				{
					String line = p.getIN().readLine();
					if(line == null)
						continue;
					String[] data = line.split("~");
					if(data[0].equals("set"))
						gameSet = true;
					else if(data[0].equals("load"))
						load = true;
					else if(data[0].equals("defaultRounds"))
						createRounds(Integer.parseInt(data[1]));
					else if(data[0].equals("round"))
					{
						if(data[1].equals("msg"))
							currentRound.msg(i, data[2]);
						else if(data[1].equals("set"))
							startRound = Integer.parseInt(data[2]);
						else if(data[1].equals("rounds"))
						{
							rounds = new ArrayList<String>();
							rounds.addAll(Arrays.asList(data[2].split("`")));
							numberOFrounds = rounds.size() + startRound;
						}
					}
					else if(data[0].equals("ready"))
					{
						if(!p.isReady())
						{
							p.setReady(true);
							sendTo(i, "client~isReady");
							sendTo(-1, "player~" + i + "~isReady");
						}
//						updateStatus();
					}
                    else if(data[0].equals("player"))
                    {
                        int pIndex = Integer.parseInt(data[1]);
                        if(pIndex != i)
                        {
                            if(data[2].equals("msgRound"))
                                if(pIndex == -1)
                                    sendToAllExcept(i, "round~msg~" + data[3]);
                                else sendTo(pIndex, "round~msg~" + data[3]);
                        }
                    }
					else if(data[0].equals("addPlayer"))
					{
						if(!gameSet)
						{
							for(int j = 0; j < loadPlayers.length; j++)
								if(loadPlayers[j] == null)
								{
									(loadPlayers[j] = new Player(data[1])).setScore(Integer.parseInt(data[2]));
									break;
								}
						}
					}
					else if(data[0].equals("`p"))
                    {
                        players[i].updatePing();
                        updatePings();
                    }
					else if(data[0].equals("I NEED ROUNDS!"))
					{
						String msg = "TAKE YOUR ROUNDS!~";
						for(String str : rounds)
							msg += str + "`";
						sendTo(i, msg);
					}
                    else if(data[0].equals("SECRET"))
                    {
                        debugSECRET = !debugSECRET;
                    }
					else if(data[0].equals("name"))
					{
						String newName = (data[1] == null ? "Player " + i : data[1]);
						String msg = "player~" + i + "~name~" + (data[1] == null ? "Player " + i : data[1]);
						p.setName(newName);

						sendTo(-1, msg);
						sendTo(-1, "client~info~0~Player " + p.getName() + " has connected");
					}
					else if(data[0].equals("disconnect"))
						setDisconnected(i);
					else System.out.println("SERVER: Unhandled message: " + line);
				}
			}
		}
		catch (IOException e) { System.out.println("SERVER: Error while handling message from client"); }
	}
	
	public void run()
	{
		while(running && !ss.isClosed())
		{
			sleepOver();
			msgs();

			switch(round)
			{
				case 0:
				{
					if((acceptConn == null || !acceptConn.isAlive()) && connected < nOp)
					{
						acceptConn = new TAcceptConnection(ss, this);
						acceptConn.start();
					}
					else if(nOp == connected) round--;
					break;
				}
				case -1: if(gameSet) round--; break;
				case -2:
				{
					if(load)
					{
						round = -5;
						break;
					}
					readyReset();
					sendTo(-1, "client~beReady"); round--;
				}
				case -3: if(!allReady()) break;
				case -4:
				{
					if(allReady())
					{
						round = (numberOFrounds - rounds.size() + 1);
						sendTo(-1, "round~set~" + round);
						if(++currPlayer == nOp) currPlayer = 0;			
					}
					break;
				}
				case -5:
				{
					sendTo(-1, "round~set~0");
					int count = 0;
					for(Player p : loadPlayers)
						if(p != null)
							count++;

					if(count == 1)
					{
						players[0].setScore(loadPlayers[0].getScore());
						sendTo(0, "player~0~score~" + players[0].getScore());
					}
					else
					{
						ServerSlotsRound choosingSlots = new ServerSlotsRound(loadPlayers, this);
						currentRound = choosingSlots;
						sendTo(-1, "round~create~slots");
						Thread t = new Thread(choosingSlots);
						t.start();
						choosingSlots.kill();
						while(t.isAlive())
						{
							msgs();
							sleepOver();
						}

						int[] slots = choosingSlots.receiveSlots();
						for(int i = 0; i < slots.length; i++)
						{
							players[slots[i]].setScore(loadPlayers[i].getScore());
							sendTo(-1, "player~" + slots[i] + "~score~" + players[slots[i]].getScore());
						}

						sendTo(-1, "round~end");
					}
					round = -2;
					load = false;
					sendTo(-1, "client~beReady");
					break;
				}
				default:
				{
					if(rounds.isEmpty())
					{
						sendTo(-1, "client~finish~" + (round - 1));
						closeConnections();
						running = false;
						round = -1;
						break;
					}

					readyReset();
					
					String currRoundType = rounds.remove(0);
					
					if(currRoundType.equals("word"))
					{
						currentRound = new ServerWordRound(nOp, currPlayer, this); //new MPWordRound();
						sendTo(-1, "round~create~word");
					}
					else if(currRoundType.equals("number"))
					{
						currentRound = new ServerNumberRound(nOp, currPlayer, this);
						sendTo(-1, "round~create~number");
					}
					else
					{
						currentRound = new ServerConundrumRound(nOp, this);// new MPConundrumRound();
						sendTo(-1, "round~create~conundrum");
					}
					
					Thread t = new Thread(currentRound);
					t.start();
					while(!currentRound.isStarted())
					{
					//	System.out.println("");
						msgs();
						sleepOver();
					}
					long time = System.currentTimeMillis();
					while(!currentRound.isEnded())
					{
						sleepOver();
						msgs();
						if(System.currentTimeMillis() - time > 30000 && !debugSECRET)
						{
							currentRound.stop();
							currentRound.kill();
							while(t.isAlive()) sleepOver();
							break;
						}
					}

					sendTo(-1, "round~end");
									
					Answer[] answers = currentRound.getAnswers(true);
					submitReset();
					readyReset();
					String msg = "round~answers~Answers:/n";
					for(int i = 0; i < nOp; i++)
					{
						OnlinePlayer p = players[i];
						if(available(p))
						{	
							p.addScore(answers[i].score);
							sendTo(-1, "player~" + i + "~score~" + p.getScore());

							if(answers[i].answer == null || answers[i].answer.equals(" "))
								msg += players[i].getName() + ": no answer./n";
							else msg += players[i].getName() + ": " + answers[i].answer + " (" + answers[i].score + ")./n";
						}
					}
					sendTo(-1, msg);
					sendTo(-1, "client~beReady");
					round = -4;
					break;
				}	
			}
		}
	}
	
	public boolean allReady()
	{
		for(OnlinePlayer p : players)
			if(available(p) && !p.isReady()) {/* System.out.println(p.isReady()); */return false; }
		return true;
	}

	public void setSubmitted(int player)
	{
		OnlinePlayer p = players[player];
		if(available(p))
		{
			p.setSubmitted(true);
			sendTo(-1, "player~" + player + "~submitted");
		}
	}

	private void createRounds(int NOR)
	{
		rounds = new ArrayList<String>();
		
		for(int i = 0; i < NOR; i++)
		{
			rounds.add("word");
			rounds.add("word");
			rounds.add("number");
		}
		rounds.add("conundrum");

		numberOFrounds = rounds.size();
	}
	
	public void pushPlayer(Socket sock)
	{
		OnlinePlayer newPlayer = new OnlinePlayer(sock, "New Player");
		for(int i = 0; i < players.length; i++)
			if(players[i] == null)
			{
				newPlayer.setName("Player " + i);
				players[i] = newPlayer;
				sendTo(-1, "addPlayer~" + i);
				for(int j = 0; j < players.length; j++)
					if(players[j] != null && i != j)
					{
						sendTo(i, "addPlayer~" + j);
						sendTo(i, "player~" + j + "~name~" + players[j].getName());
						if(players[j].getReady())
							sendTo(i, "player~" + j + "~isReady");
					}
				connected++;
				return;
			}

		newPlayer.getOUT().println("client~info~0~Server is full");
		
		try { sock.close(); }
		catch (IOException e) { System.out.println("Error on closing socket (SERVER FULL) @class Server @function pushPlayer()"); }
		
	}

	public void closeConnections()
	{
		try
		{
			for(OnlinePlayer p : players)
				if(p != null)
					p.getSocket().close();
			
			ss.close();
		}
		catch(IOException e) { System.out.println("Error on closing socket @class Server @function closeConnections()"); }
	}

	public OnlinePlayer[] getPlayers()
	{
		return players;
	}
	
	public void sendTo(int player, String msg)
	{
		if(player == -1)
		{
			for(OnlinePlayer p : players)
				if(available(p))
					p.getOUT().println(msg);
		}
		else if(available(players[player]))
			players[player].getOUT().println(msg);
	}
	
	public void sendToAllExcept(int player, String msg)
	{
		for(int i = 0; i < players.length; i++)
			if(i != player && available(players[i]))
				players[i].getOUT().println(msg);
	}
	
	public void readyReset()
	{
		for(int i = 0; i < nOp; i++)
		{
			OnlinePlayer p = players[i];
			if(available(p))
			{
				p.setReady(false);
				sendTo(-1, "player~" + i + "~notReady");
			}
		}
	}
	
	public void submitReset()
	{
		for(int i = 0; i < nOp; i++)
		{
			OnlinePlayer p = players[i];
			if(available(p))
			{
				p.setSubmitted(false);
				sendTo(-1, "player~" + i + "~notSubmitted");
			}
		}
	}
	
	private void ping()
	{
		for(int i = 0; i < nOp; i++)
		{
			OnlinePlayer p = players[i];
			if(available(p))
			{
				if(!p.isPinging())
				{
					sendTo(i, "`p");
					p.updatePing();
				}
				else if(!p.isDisconnected())
				{
					p.timerInc();
					if(p.getTimer() > 5) // disconnect after 5 sec;
						setDisconnected(i);
				}
			}
		}
	}

	private boolean available(OnlinePlayer player)
	{
		return player != null && !player.isDisconnected();
	}
	
	private void updatePings()
	{
		for(int i = 0; i < nOp; i++)
		{
			OnlinePlayer p = players[i];
			if(available(p) && !p.isPinging())
				sendTo(-1, "player~" + i + "~ping~" + p.getPing());
		}
	}

	private void setDisconnected(int player)
	{
		OnlinePlayer p = players[player];
		p.disconnect();
		sendTo(-1, "client~info~0~Player " + p.getName() + " has disconnected");
		if(connected < nOp)
		{
			sendTo(-1, "player~" + player + "~clear");
			players[player] = null;
			connected--;
		}
		else
		{
			sendTo(-1, "player~" + player + "~disconnected");
			sendTo(-1, "player~" + player + "~ping~" + -1);
		}
	}
	
	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
}

