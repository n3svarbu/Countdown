package p18.countdown.ui;

import p18.countdown.ui.entity.Entity;
import p18.countdown.ui.entity.EntityInfoBox;

import java.util.ArrayList;

public class InfoManager
{
	private static ArrayList<Entity> entities = new ArrayList<Entity>();
	private static ArrayList<Data> times = new ArrayList<Data>();

	public static void update()
	{

		for(int i = 0; i < entities.size() && i < times.size() ; i++)
		{
			long delta = System.currentTimeMillis() - times.get(i).time;
			if(delta > 6000)
			{
				entities.remove(i);
				times.remove(i);
				break;
			}
			else if (!times.get(i).moved && delta > 5000)
			{
				times.get(i).moved = true;
				entities.get(i).moveToX(-400);
			}
		}
	}

	public static void pushInfo(int type, String text)
	{
		EntityInfoBox eib = new EntityInfoBox(type, text);
		eib.setXY(15, 420);
		entities.add(eib);
		times.add(new Data(System.currentTimeMillis()));

		for(int i = 0; i < entities.size(); i++)
		{
			int index = entities.size() - 1 - i;
			if (!times.get(index).moved)
				entities.get(index).moveToY(365 - 45 * i);
		}
	}

	public static ArrayList<Entity> getEntities()
	{
		return entities;
	}

	private static class Data
	{
		private long time = 0;
		private boolean moved = false;

		Data(long time)
		{
			this.time = time;
		}
	}
}
