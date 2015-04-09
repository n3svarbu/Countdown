package p18.countdown.rounds.client;

import java.util.ArrayList;

import p18.countdown.multiplayer.Client;
import p18.countdown.ui.entity.Entity;

public abstract class AbstractClientRound implements ClientRound
{
	protected int score;
	
	protected boolean started;

	protected ArrayList<Entity> entities;
	
	protected Client client;
	
	AbstractClientRound(Client client)
	{
		this.score = 0;

		this.started = false;
		
		this.entities = new ArrayList<Entity>();
		
		this.client = client;
	}

	public ArrayList<Entity> getEntities()
	{
		return entities;
	}
}
