package p18.countdown.rounds.server;

import java.util.ArrayList;

import p18.countdown.multiplayer.Answer;

public abstract class AbstractServerRound implements ServerRound
{
	protected Answer[] answers;
	
	protected boolean started;
	protected boolean stop;
	protected boolean ended;
	protected boolean kill;
	
	protected ArrayList<Message> msgs;
	
	AbstractServerRound()
	{
		this.answers = new Answer[0];
		
		this.ended = false;
		this.stop = false;
		this.started = false;
		
		this.msgs = new ArrayList<Message>();
	}
	
	public abstract void run();
	public abstract Answer[] returnAnswers();
	
	public void end()
	{
		answers = returnAnswers();
		
		ended = true;
		while(!kill) sleepOver();	
	}

	public boolean isEnded()
	{
		return ended;
	}

	public boolean isStarted()
	{
		return started;
	}

	public void stop()
	{
		stop = true;
	}

	public Answer[] getAnswers(boolean kill)
	{
		this.kill = kill;
		if(kill) stop();
		return answers;
	}

	public void kill()
	{
		kill = true;
	}
	
	public static Answer[] createEmptyAnswers(int size)
	{
		Answer[] answers = new Answer[size];
		for(int i = 0; i < size; i++)
			answers[i] = new Answer();
		return answers;
	}
	
	public void msg(int player, String msg)
	{
		msgs.add(new Message(player, msg));
		System.out.println(player+"#MSG: " + msg);
	}
	
	public String receiveFrom(int player, boolean lock)
	{
		while(lock)
		{
			sleepOver();
			if(msgs.isEmpty()) continue;
			Message msg = msgs.remove(0);
			if(msg.player == player)	
				return msg.msg;
		}
		if(!msgs.isEmpty())
		{
			for(Message msg : msgs)
				if(msg.player == player)
				{
					msgs.remove(msg);
					return msg.msg;
				}
		}
		return "0";
	}

	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	
	private class Message
	{
		private int player;
		private String msg;
		
		Message(int player, String msg)
		{
			this.player = player;
			this.msg = msg;
		}
	}
}
