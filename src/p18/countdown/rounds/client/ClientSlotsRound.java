package p18.countdown.rounds.client;
import p18.countdown.multiplayer.Client;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.entity.*;

public class ClientSlotsRound extends AbstractClientRound
{
	private int[] slots;

	private boolean picking;
	private boolean started;

	public ClientSlotsRound(Client client)
	{
		super(client);
		this.picking = false;
		this.slots = null;
	}

	public void end()
	{
		for(Entity e : entities)
			e.moveToX(-400);
	}

	public void msg(String message)
	{
		System.out.println("FROM SERVER: " + message);
		String[] data = message.split(",");
		if(data[0].equals("picking"))
		{
			picking = data[1].equals("YOU");
			MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_IDLE);
		}
		else if(data[0].equals("pressed"))
			entityPressed(Integer.parseInt(data[1]));
		else if(data[0].equals("add"))
		{
			EntityStretchyBox esb = new EntityStretchyBox();
			esb.setText(data[1] + " :\t" + data[2]);
			esb.setActive(false);
			esb.setEnabled(true);
			entities.add(entities.size() / 2, esb);

			EntityButton eb = new EntityButton(data[3]);
			eb.setActive(false);
			eb.setEnabled(false);
			entities.add(eb);
		}
		else if(data[0].equals("start"))
		{
			slots = new int[entities.size() / 2];

			for(int i = 0; i < slots.length; i++)
			{
				slots[i] = -1;

				Entity e = entities.get(i  + slots.length);
				e.setEnabled(picking);
				e.setActive(picking);
				e.setXY(-150, 150 + i * 50);
				e.moveToX(265);

				Entity e2 = entities.get(i);
				e2.setXY(-250, 160 + i * 50);
				e2.moveToX(55);
			}

			EntityLabel eb = new EntityLabel(true);
			if(picking)
				eb.setLabel("Choose slots for players");
			else eb.setLabel("Host is choosing slots for players");
			eb.setXY(-500, 120);
			eb.moveToX(380);

			started = true;
		}
	}

	public void entityPressed(int entityID)
	{
		if(started)
		{
			if(picking) client.getOUT().println("player~-1~msgRound~pressed," + entityID);
			boolean inSlot = false;
			for(int i = 0; i < slots.length; i++)
				if((inSlot = slots[i] == entityID))
				{
					slots[i] = -1;
					entities.get(entityID).moveToXY(265, 147 + (entityID - slots.length) * 50);
					entities.get(i).setEnabled(true);
					break;
				}

			if(!inSlot)
				for(int i = 0; i < slots.length; i++)
					if(slots[i] == -1)
					{
						slots[i] = entityID;
						entities.get(entityID).moveToXY(45, 147 + i * 50);
						entities.get(i).setEnabled(false);
						break;
					}

			boolean ready = true;
			for(Integer i : slots)
				if(i == -1)
				{
					ready = false;
					break;
				}

			if(picking)
			{
				if(ready)
					MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_SUBMIT);
				else MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_IDLE);
			}
		}

	}

	public String submitMsg()
	{
		String msg = "";
		for(Integer i : slots)
			msg += (i - slots.length) + ",";
		return msg;
	}
	public void typing(char character) {}
}
