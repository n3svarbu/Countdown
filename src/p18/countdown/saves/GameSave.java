package p18.countdown.saves;

import p18.countdown.player.Player;

import java.util.ArrayList;

public class GameSave
{
	public Player[] players;

	public ArrayList<String> rounds;

	public int round;

	public GameSave(Player[] players, ArrayList<String> rounds, int round)
	{
		this.players = players;
		this.rounds = rounds;
		this.round = round;
	}
}
