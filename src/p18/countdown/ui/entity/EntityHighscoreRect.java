package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;

import p18.countdown.data.GUIData;

public class EntityHighscoreRect extends AbstractEntity
{
	private String place;
	private String rounds;
	private String player;
	private String score;

	public EntityHighscoreRect()
	{
		super();
		setInfo("No.", "Round", "Player", "Score");
	}

	public EntityHighscoreRect(String place, String rounds, String player, String score)
	{
		super();
		setInfo(place, rounds, player, score);
	}

	public void draw(Graphics2D g2)
	{
		g2.setFont(GUIData.fonts[GUIData.FONT_SCOREBOARD]);
		g2.setColor(new Color(0, 0, 255, 100));
		g2.fillRect((int) pos.x, (int) pos.y, 300, 25);
		g2.setColor(new Color(0, 0, 0));
		g2.drawString(place, (int) pos.x + 10, (int) pos.y + 18);
		g2.drawString(rounds, (int) pos.x + 40, (int) pos.y + 18);
		g2.drawString(player, (int) pos.x + 100, (int) pos.y + 18);
		g2.drawString(score, (int) pos.x + 245, (int) pos.y + 18);
	}

	public void setInfo(String place, String rounds, String player, String score)
	{
		this.place = place;
		this.rounds = rounds;
		this.player = player;
		this.score = score;
	}

	public String getType()
	{
		return "highscorerect";
	}

}
