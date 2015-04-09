package p18.countdown.rounds.client;

import p18.countdown.multiplayer.Client;
import p18.countdown.ui.MenuManager;
import p18.countdown.ui.entity.Entity;
import p18.countdown.ui.entity.EntityLabel;
import p18.countdown.ui.entity.EntitySmallBox;

public class ClientConundrumRound extends AbstractClientRound
{
	private char[] answer;
	private char[] chars;

	private int[] slots;
	
	public ClientConundrumRound(Client client)
	{
		super(client);

		this.answer = new char[9];
		this.chars = new char[9];
		this.slots = new int[9];
		for(int i = 0; i < chars.length; i++)
		{
			chars[i] = '_';
			answer[i] = '_';
			slots[i] = -1;
		}
		initEntities();
	}

	public void end()
	{
		started = false;

		for(Entity e : entities)
			e.moveToY(600);

		EntityLabel el = ((EntityLabel) entities.get(9));
		el.moveToY(200);
	}

	public void initEntities()
	{
		// SMALL BOXES FROM 0 TO 8 - 9 IN TOTAL
		for(int i = 0; i < 9; i++)
		{
			EntitySmallBox esb = new EntitySmallBox();
			esb.setXY(-100, 280);
			esb.setChar('?');
			esb.setEnabled(false);
			esb.setActive(false);
			esb.moveToX(45 + i * 54);
			entities.add(esb);
		}

		// LABEL 9 - 1 IN TOTAL
		EntityLabel el = new EntityLabel(true);
		el.setLabel("Are you ready for conundrum?");
		el.setXY(-300, 200);
		el.moveToX(270);
		entities.add(el);

		MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_READY);
	}

	public void msg(String msg)
	{
		String[] data = msg.split(",");

		if(data[0].equals("start"))
		{
			MenuManager.setRoundSettings(MenuManager.SETTING_ROUND_START);

			started = true;
			String conundrum = data[1];
			for(int i = 0; i < 9; i++)
			{
				EntitySmallBox esb = ((EntitySmallBox) entities.get(i));
				esb.setChar(conundrum.charAt(i));
				esb.setActive(true);
				esb.setEnabled(true);
			}

			chars = conundrum.toLowerCase().toCharArray();

			((EntityLabel) entities.get(9)).setLabel("Construct your 9 letters word!");
		}
		else if(data[0].equals("answer"))
			((EntityLabel) entities.get(9)).setLabel("Answer: " + data[1]);
	}

	public void typing(char character)
	{
		if(started)
		{
			if (character == 10)
				client.getOUT().println("round~msg~" + submitMsg());
			else if (character == 8)
			{
				for (int i = answer.length - 1; i >= 0; i--)
					if (answer[i] != '_')
					{
						for (int j = 0; j < chars.length; j++)
							if (slots[j] == i)
							{
								entityPressed(j);
								break;
							}
						break;
					}
			}
			else if (character == 127)
			{
				for (int i = 0; i < chars.length; i++)
					if (chars[i] == '_')
						entityPressed(i);
			}
			else
			{
				for (int i = 0; i < chars.length; i++)
					if (chars[i] == character)
					{
						entityPressed(i);
						break;
					}
			}
		}
	}

	public void entityPressed(int entityID)
	{
		if(chars[entityID] != '_')
		{
			for(int i = 0; i < answer.length; i++)
				if(answer[i] == '_')
				{
					answer[i] = chars[entityID];
					entities.get(entityID).moveToXY(45 + i * 54, 340);
					chars[entityID] = '_';
					slots[entityID] = i;
					break;
				}
		}
		else
		{
			chars[entityID] = answer[slots[entityID]];
			answer[slots[entityID]] = '_';
			entities.get(entityID).moveToXY(45 + entityID * 54, 280);
			slots[entityID] = -1;
		}
	}

	public String submitMsg()
	{
		String ans = "";
		for(char c : answer)
			if(c != '_')
				ans += c;
		if(ans.length() == 0)
			ans = "-";
		return ans;
	}
}
