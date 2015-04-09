package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import p18.countdown.data.GUIData;
import p18.countdown.multiplayer.Client;
import p18.countdown.player.Player;

public class EntityScoreBoard extends AbstractEntity
{
	public EntityScoreBoard()
	{
		super();
	}
	
	public void draw(Graphics2D g2)
	{
		
		int i = 0;
		Player[] players = Client.getPlayers();
		if(players == null) return;
		g2.setFont(GUIData.fonts[GUIData.FONT_SCOREBOARD]);
		for(Player p : players)
		{
			if(p != null)
			{
				g2.setColor(new Color(0, 0, 0, 100));
				g2.fillRect((int) pos.x, (int) pos.y + i * 26, 190, 25);
				Color circleColor;
				if(p.isDisconnected()) circleColor = new Color(0,0,0);
				else if(p.getSubmitted()) circleColor = new Color(255, 255,0);
				else if(!p.getReady()) circleColor = new Color(255,0,0);
				else circleColor = new Color(0, 255,0);
				g2.setColor(circleColor);
				g2.fillOval((int) pos.x + 5, (int) pos.y + 7 + i * 26, 10, 10);
				g2.setColor(new Color(255,255,255));
				g2.drawString(p.getName(), (int) pos.x + 20, (int) pos.y + 18 + i * 26);
				g2.drawString(""+p.getScore(), (int) pos.x + 125, (int) pos.y + 18 + i * 26);
				g2.drawString(""+p.getPing(), (int) pos.x + 160, (int) pos.y + 18 + i * 26);
				i++;
			}
		}

		g2.setColor(new Color(0, 0, 0));
		g2.drawRect((int) pos.x, (int) pos.y, 190, 25 + (--i) * 26);
	}

	public String getType()
	{
		return "scoreboard";
	}

}
