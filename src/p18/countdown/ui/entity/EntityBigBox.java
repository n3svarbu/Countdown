package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;

public class EntityBigBox extends AbstractEntity
{
	char letter;
	String label;
	
	public EntityBigBox()
	{
		super();
		this.letter = ' ';
		this.label = "";
		
		BufferedImage img = GUIData.images[GUIData.IMG_BBOX_DISABLED];
		this.width = img.getWidth();
		this.height = img.getHeight();
	}

	public void draw(Graphics2D g2)
	{
		BufferedImage img;
		if(!enabled)
			img = GUIData.images[GUIData.IMG_BBOX_DISABLED];
		else if(mousePressed)
			img = GUIData.images[GUIData.IMG_BBOX_DOWN];
		else if(mouseOver)
			img = GUIData.images[GUIData.IMG_BBOX_OVER];
		else img = GUIData.images[GUIData.IMG_BBOX_UP];
		if(img == null)
			return;
		
		g2.drawImage(img, (int) pos.x, (int) pos.y, null);
		
		g2.setColor(new Color(0,0,0));
		g2.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);
		float labelXplacing = pos.x + (width / 2) - (g2.getFontMetrics().stringWidth(label) / 2);
		float labelYplacing = pos.y;
		g2.drawString(label, (int) labelXplacing, (int) labelYplacing);
		
		g2.setColor(new Color(255,255,255));
		g2.setFont(GUIData.fonts[GUIData.FONT_BBOX_CHAR]);
		float letterXplacing = pos.x + (width / 2) - (g2.getFontMetrics().stringWidth(""+letter) / 2);
		float letterYplacing = pos.y + 60;
		g2.drawString(""+letter, (int) letterXplacing, (int) letterYplacing);
	}
	
	public void setChar(char letter)
	{
		this.letter = letter;
	}
	
	public void setLabel(String label)
	{
		if(label != null)
			this.label = label;
		else this.label = "";
	}

	public String getType()
	{
		return "smallbox";
	}
}
