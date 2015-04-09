package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;

public class EntityButton extends AbstractEntity
{
	private String text;
	
	public EntityButton(String text)
	{
		super();
		this.text = text;
		pos.x = 400;
		pos.y = 225;
		
		BufferedImage img = GUIData.images[GUIData.IMG_MBUTTON_UP];
		width = img.getWidth();
		height = img.getHeight();
	}
	
	public void draw(Graphics2D g2)
	{
		BufferedImage img;
		if(!enabled)
			img = GUIData.images[GUIData.IMG_MBUTTON_DISABLED];
		else if(mousePressed)
			img = GUIData.images[GUIData.IMG_MBUTTON_DOWN];
		else if(mouseOver)
			img = GUIData.images[GUIData.IMG_MBUTTON_OVER];
		else img = GUIData.images[GUIData.IMG_MBUTTON_UP];
		if(img == null)
			return;

		g2.drawImage(img, (int) pos.x, (int) pos.y, null);
		g2.setColor(new Color(0,0,0));
		g2.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);
		float textXplacing = pos.x + (width / 2) - (g2.getFontMetrics().stringWidth(text) / 2);
		float textYplacing = pos.y + (height / 2) + (g2.getFontMetrics().getHeight() / 2) - 5;
		g2.drawString(text, (int) textXplacing, (int) textYplacing);
	}
	
	public void setText(String newText)
	{
		if(newText != null)
			this.text = newText;
		else this.text = "";
	}
	
	public Rectangle getCollisionBounds()
	{
		return new Rectangle((int) pos.x + 20, (int) pos.y + 20, width - 35, height - 39);
	}
	
	public String getType()
	{
		return "button";
	}
}
