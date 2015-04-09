package p18.countdown.rounds.server;

import java.util.ArrayList;
import java.util.Random;

import p18.countdown.data.Defs;
import p18.countdown.multiplayer.Answer;
import p18.countdown.multiplayer.Server;
import p18.countdown.player.OnlinePlayer;

public class ServerConundrumRound extends AbstractServerRound
{
	private String conundrum;
	private String scrambled;
	
	private Server srv;
	private ConundrumPlayer[] players;
	
	public ServerConundrumRound(int nOp, Server srv)
	{
		this.srv = srv;
		this.conundrum = "";
		this.scrambled = "";
		
		createPlayers(nOp);
	}
	
	private void createPlayers(int nOp)
	{
		players = new ConundrumPlayer[nOp];
		for(int i = 0; i < nOp; i++)
			players[i] = new ConundrumPlayer();
	}
	
	public void run()
	{
		OnlinePlayer[] p = srv.getPlayers();
		srv.readyReset();

		while(!srv.allReady()) { sleepOver(); }

		srv.readyReset();

		conundrum = Defs.conundrumD.get(new Random().nextInt(Defs.conundrumD.size()));
		scrambled = scramble(conundrum);

		srv.sendTo(-1, "round~msg~start," + scrambled.toUpperCase());
		started = true;

		System.out.println(conundrum);

		while(!stop)
		{
			sleepOver();
			for(int i = 0; i < p.length && !stop; i++)
			{
				switch(players[i].stage)
				{
					case 0:
					{
						String in = receiveFrom(i, false);
						if(in == null || in.equals("0")) break;
						in = in.toLowerCase();

						if(in.length() != 9)
						{
							srv.sendTo(i, "client~info~0~We need all 9 letters!");
							break;
						}
						else if(!Defs.containsBetween(in, scrambled))
						{
							players[i].stage = 0;
							srv.sendTo(i, "client~info~2~HACKS!?");
							break;
						}
						
						players[i].answer = in;
						players[i].stage++;
						break;
					}
					case 1:
					{
						srv.setSubmitted(i);
						players[i].stage = 0;
						players[i].answered = true;
						players[i].finalAnswer = players[i].answer;
						break;
					}
				}
			}
			boolean ready = true;
			for(int i = 0; i < p.length; i++)
				if(!players[i].answered) { ready = false; break; }
			if(ready) break;
		} 
		
		for(int i = 0; i < p.length && !stop; i++)
		{
			if(players[i].answer == null)
				srv.sendTo(i, "client~info~1~You did not enter anything!");
			else if(players[i].answer.equals(conundrum) || Defs.containsConundrumD(players[i].answer))
				players[i].contains = true;
		}
		
		end();
	}
	
	public Answer[] returnAnswers()
	{
		srv.sendTo(-1, "round~msg~answer," + conundrum);
		
		Answer[] answers = AbstractServerRound.createEmptyAnswers(players.length);
		for(int i = 0; i < players.length && !stop; i++)
		{
			answers[i].answer = players[i].finalAnswer;
			answers[i].score = players[i].contains ? 15 : 0;
		}

		return answers;
	}

	private class ConundrumPlayer
	{
		private int stage;
		private String answer;
		private String finalAnswer;
		
		private boolean answered;
		private boolean contains;
		
		ConundrumPlayer()
		{
			this.stage = 0;
			this.answer = " ";
			this.finalAnswer = " ";
			this.answered = false;
			this.contains = false;
		}
	}
	
	public String scramble(String word)
	{
		String newWord = "";
		ArrayList<Character> chars = new ArrayList<Character>();
		for(int i = 0; i < word.length(); i++)
			chars.add(word.charAt(i));
		
		while(!chars.isEmpty())
			newWord += chars.remove(new Random().nextInt(chars.size()));
		return newWord;
	}
	
	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
}
