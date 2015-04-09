package p18.countdown.ui.entity;

import java.awt.*;

import p18.countdown.data.GUIData;

public class EntityLabel extends AbstractEntity
{
	String label;
	
	private boolean center;
	private Font font;
	
	public EntityLabel(boolean center)
	{
		this.label = "";
		this.center = center;
		this.font = GUIData.fonts[GUIData.FONT_MBUTTONS];
	}
	
	public void draw(Graphics2D g2)
	{
		g2.setColor(new Color(0,0,0));
		g2.setFont(font);
		int posX = (int) pos.x;
		int posY = (int) pos.y;
		if(center)
		{
			posX -= (g2.getFontMetrics().stringWidth(label.split("\n")[0]) / 2);
		}
		for (String line : label.split("\n"))
		{
			g2.drawString(line, posX, posY);
			posY += g2.getFontMetrics().getHeight();
		}
	}

	public void setLabel(String newLabel)
	{
		if(newLabel != null)
			this.label = newLabel;
		else this.label = "";
	}
	
	public String getType()
	{
		return "label";
	}
	
	public void setFontSize(float size)
	{
		font = font.deriveFont(size);
	}
	
}
