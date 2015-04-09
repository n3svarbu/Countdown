package p18.countdown.ui.entity;

import p18.countdown.data.GUIData;

import java.awt.*;
import java.awt.image.BufferedImage;

public class EntityInfoBox extends AbstractEntity
{
	public static final int TYPE_INFO = 0;
	public static final int TYPE_WARNING = 1;
	public static final int TYPE_ERROR = 2;

	private String text;
	private int type;


	public EntityInfoBox(int type, String text)
	{
		super();
		this.type = type;
		this.text = text;

		BufferedImage img = GUIData.images[GUIData.IMG_INFO_INFO];
		width = img.getWidth();
		height = img.getHeight();
	}

	public void draw(Graphics2D g2)
	{
		BufferedImage img;
		Color color;
		if(type == TYPE_INFO)
		{
			img = GUIData.images[GUIData.IMG_INFO_INFO];
			color = new Color(153, 0, 102);
		}
		else if(type == TYPE_WARNING)
		{
			img = GUIData.images[GUIData.IMG_INFO_WARNING];
			color = new Color(255, 204, 51);
		}
		else if(type == TYPE_ERROR)
		{
			img = GUIData.images[GUIData.IMG_INFO_ERROR];
			color = new Color(204, 0, 0);
		}
		else return;

		g2.drawImage(img, (int) pos.x, (int) pos.y, null);
		g2.setColor(color);
		g2.setFont(GUIData.fonts[GUIData.FONT_INFO_BOX]);
		g2.drawString(text, (int) pos.x + width + 5, (int) pos.y + 25);
	}



	public String getType()
	{
		return "infobox";
	}
}
