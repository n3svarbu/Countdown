package p18.countdown.ui.entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;
import p18.countdown.ui.GUIStage;

public class EntityTextBox extends AbstractEntity
{
	private String mark;
	private String label;
	private String input;
	
	private long time;
	
	private boolean tick;
	
	public EntityTextBox(String label)
	{
		this.mark = "|";
		this.label = label;
		this.input = "";
		
		this.tick = false;
		
		pos.x = 400;
		pos.y = 225;
		
		time = System.currentTimeMillis();
		
		BufferedImage img = GUIData.images[GUIData.IMG_MBUTTON_UP];
		width = img.getWidth();
		height = img.getHeight();
	}
	
	public void draw(Graphics2D g2)
	{
		BufferedImage img;
		if(!enabled)
			img = GUIData.images[GUIData.IMG_TEXTBOX_DISABLED];
		else if(focused)
			img = GUIData.images[GUIData.IMG_TEXTBOX_FOCUSED];
		else img = GUIData.images[GUIData.IMG_TEXTBOX_IDLE];
		if(img == null)
			return;

		g2.drawImage(img, (int) pos.x, (int) pos.y, null);
		g2.setColor(new Color(0,0,0));
		g2.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);
		float labelXplacing = pos.x - g2.getFontMetrics().stringWidth(label);
		float labelYplacing = pos.y + (height / 2) + (g2.getFontMetrics().getHeight() / 2) - 5;
		g2.drawString(label, (int) labelXplacing, (int) labelYplacing);
		
		String refined = input;
		while(g2.getFontMetrics().stringWidth(refined) > 190)
			refined = refined.substring(1, refined.length());
		float inputXplacing = pos.x + 20;
		float inputYplacing = pos.y + (height / 2) + (g2.getFontMetrics().getHeight() / 2) - 5;
		if(focused && tick)
		{
			tick = false;
			mark = mark.equals("|") ? "" : "|";
		}
		else if(!focused) mark = "";
		g2.drawString(refined + mark, (int) inputXplacing, (int) inputYplacing);
	}
	
	public void update()
	{
		super.update();
		
		long newTime = System.currentTimeMillis();
		if(newTime - time > 500)
		{
			tick = true;
			time = newTime;
			GUIStage.draw();
		}
	}
	
	public void input(char character)
	{
		//System.out.println(character + " " + (int) character);
		if(character == 10)
			focused = false;
		else if(character == 8)
		{
			if(input.length() > 0)
				input = input.substring(0, input.length() - 1);
		}
		else if(character == 127)
			input = "";
		else input += character;
		GUIStage.draw(); 
	}
	
	public String getInput()
	{
		return input;
	}
	
	public void setLabel(String newLabel)
	{
		if(newLabel != null)
			this.label = newLabel;
		else this.label = "";
	}

	public void setInput(String newInput)
	{
		if(newInput != null)
			this.input = newInput;
		else this.input = "";
	}
	
	public Rectangle getCollisionBounds()
	{
		return new Rectangle((int) pos.x + 20, (int) pos.y + 20, width - 35, height - 39);
	}
	
	public String getType()
	{
		return "textbox";
	}
}
