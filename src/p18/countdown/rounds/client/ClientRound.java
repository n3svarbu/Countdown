package p18.countdown.rounds.client;

import java.util.ArrayList;

import p18.countdown.ui.entity.Entity;

public interface ClientRound
{
	public ArrayList<Entity> getEntities();
	public void msg(String msg);
	public void typing(char character);
	public void entityPressed(int entityID);
	public void end();
	
	public String submitMsg();
}
