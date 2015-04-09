package p18.countdown.rounds.server;

import p18.countdown.multiplayer.Answer;

public interface ServerRound extends Runnable
{
	//public void run();
	public boolean isEnded();
	public boolean isStarted();
	public void stop();
	public Answer[] getAnswers(boolean kill);
	public void kill();
	
	public void msg(int player, String msg);
}
