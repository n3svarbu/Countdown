package p18.countdown.rounds.server;

import java.util.Random;

import p18.countdown.data.Defs;
import p18.countdown.multiplayer.Answer;
import p18.countdown.multiplayer.Server;
import p18.countdown.player.OnlinePlayer;

public class ServerWordRound extends AbstractServerRound
{
	private int currPlayer;
	private int letterC = 0;
	
	private String bestAnswer = "no best answer";
	private String letters;
	
	private Server srv;
	private WordPlayer[] players;
	
	public ServerWordRound(int nOp, int currPlayer, Server srv)
	{
		this.currPlayer = currPlayer;
		this.srv = srv;
		this.letters = "";
		
		createPlayers(nOp);
	}
	
	private void createPlayers(int nOp)
	{
		players = new WordPlayer[nOp];
		for(int i = 0; i < nOp; i++)
			players[i] = new WordPlayer();
	}
	
	public void run()
	{
		Defs.reloadLetters();
		srv.sendTo(currPlayer, "round~msg~picking,YOU");
		srv.sendToAllExcept(currPlayer, "round~msg~picking,NOTYOU");
		while(letters.length() != 9)
		{
			srv.sendTo(-1, "round~msg~lettersC," + (9 - letters.length()));
			String vORc = receiveFrom(currPlayer, true);
			letters += vORc.equals("v") ? getVowel() : getConsonant();
			srv.sendTo(-1, "round~msg~addLetter," + vORc + "," + letters.charAt(letters.length() - 1));
		}
		
		srv.sendTo(-1, "round~msg~start");
		started = true;
		
		OnlinePlayer[] p = srv.getPlayers();
		
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

						if(in.equals("-"))
						{
							srv.sendTo(i, "client~info~1~You must enter something to submit");
							break;
						}
						else if(!Defs.containsBetween(in, letters))
						{
							srv.sendTo(i, "client~info~2~HACKS!?");
							srv.sendTo(i, "round~msg~reset");
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

		for(int i = 0; i < p.length; i++)
		{
			if(players[i].finalAnswer == null)
				srv.sendTo(i, "client~info~1~You did not enter anything!");
			else if(Defs.containsDictionary(players[i].answer))
				players[i].contains = true;
		}
		
		end();
	}
	
	public Answer[] returnAnswers()
	{
		for(String word : Defs.dictionary)
		{
			if(word.length() > letterC && Defs.containsBetween(word, letters))
			{
				letterC = word.length();
				bestAnswer = word;
			}
		}

		if(bestAnswer.equals("no best answer"))
			srv.sendTo(-1, "round~msg~bestAnswer,-");
		else srv.sendTo(-1, "round~msg~bestAnswer," + bestAnswer);
		
		Answer[] answers = AbstractServerRound.createEmptyAnswers(players.length);
		for(int i = 0; i < players.length; i++)
		{
			answers[i].answer = players[i].finalAnswer;
			answers[i].score = players[i].contains ? players[i].finalAnswer.length() : 0;
		}

		return answers;
	}
	
	private char getVowel()
	{
		return Defs.vowels.remove((new Random()).nextInt(Defs.vowels.size()));
	}
	
	private char getConsonant()
	{
		return Defs.consonants.remove((new Random()).nextInt(Defs.consonants.size()));
	}
	
	private class WordPlayer
	{
		private int stage;
		private String answer;
		private String finalAnswer;
		
		private boolean answered;
		private boolean contains;
		
		WordPlayer()
		{
			this.stage = 0;
			this.answer = " ";
			this.finalAnswer = null;
			this.answered = false;
			this.contains = false;
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
