package p18.countdown.rounds.server;

import p18.countdown.multiplayer.Answer;
import p18.countdown.multiplayer.Server;
import p18.countdown.player.OnlinePlayer;
import p18.countdown.player.Player;

public class ServerSlotsRound extends AbstractServerRound
{
	private Server srv;
	private Player[] players;

	private int[] slots;

	public ServerSlotsRound(Player[] players, Server srv)
	{
		this.srv = srv;
		this.players = players;
		this.slots = new int[players.length];
	}

	public void run()
	{
		srv.sendTo(0, "round~msg~picking,YOU");
		srv.sendToAllExcept(0, "round~msg~picking,NOTYOU");

		OnlinePlayer[] p = srv.getPlayers();
		for(int i = 0; i < players.length; i++)
			srv.sendTo(-1, "round~msg~add," + players[i].getName() + "," + players[i].getScore() + "," + p[i].getName());

		srv.sendTo(-1, "round~msg~start");

		String[] in = receiveFrom(0, true).split(",");
		for(int i = 0; i < slots.length; i++)
			slots[i] = Integer.parseInt(in[i]);

		end();
	}

	public int[] receiveSlots()
	{
		return slots;
	}

	public Answer[] returnAnswers()
	{
		return null;
	}
}
