package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;

public class EntitySmallBox extends AbstractEntity
{
	char letter;
	
	public EntitySmallBox()
	{
		super();
		this.letter = ' ';
		
		BufferedImage img = GUIData.images[GUIData.IMG_SBOX_FOCUSED];
		this.width = img.getWidth();
		this.height = img.getHeight();
	}

	public void draw(Graphics2D g2)
	{
		BufferedImage img;
		if(!enabled)
			img = GUIData.images[GUIData.IMG_SBOX_DISABLED];
		else if(mouseOver || focused)
			img = GUIData.images[GUIData.IMG_SBOX_FOCUSED];
		else img = GUIData.images[GUIData.IMG_SBOX_IDLE];
		if(img == null)
			return;
		
		g2.drawImage(img, (int) pos.x, (int) pos.y, null);

		g2.setColor(new Color(255,255,255));
		g2.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);
		float letterXplacing = pos.x + (width / 2) - (g2.getFontMetrics().stringWidth(""+letter) / 2);
		float letterYplacing = pos.y + 35;
		g2.drawString(""+letter, (int) letterXplacing, (int) letterYplacing);
	}
	
	public void setChar(char letter)
	{
		this.letter = letter;
	}

	public String getType()
	{
		return "smallbox";
	}
}
