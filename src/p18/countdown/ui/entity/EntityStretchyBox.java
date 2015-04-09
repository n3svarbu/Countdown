package p18.countdown.ui.entity;

import java.awt.*;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;

public class EntityStretchyBox extends AbstractEntity
{
	private BufferedImage[] imgs;
	private BufferedImage image;
	private String text;

    private int highlight;
	private int lastMode;

	private Graphics2D graph;
	
	public EntityStretchyBox()
	{
		super();
		
		this.imgs = new BufferedImage[3];
		this.image = null;
		this.graph = null;
		this.text = "";

        this.highlight = 0;
		this.lastMode = -1;
	}
	
	public void draw(Graphics2D g2)
	{
		if(graph == null) graph = g2;
		if(!enabled)
			makeImage(GUIData.images[GUIData.IMG_SBOX_DISABLED], 0);
		else if(mouseOver || focused)
			makeImage(GUIData.images[GUIData.IMG_SBOX_FOCUSED], 1);
		else makeImage(GUIData.images[GUIData.IMG_SBOX_IDLE], 2);
		if(imgs[0] == null)
			return;

		g2.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);
		g2.drawImage(image, (int) pos.x, (int) pos.y, null);
//		g2.drawImage(imgs[0], (int) pos.x, (int) pos.y, null);
//		width = imgs[0].getWidth();
//		int i;
//		for(i = 0; i < g2.getFontMetrics().stringWidth(text); i++)
//		{
//			g2.drawImage(imgs[1], (int) pos.x + imgs[0].getWidth() + i, (int) pos.y, null);
//			width += 1;
//		}
//		g2.drawImage(imgs[2], (int) pos.x + imgs[0].getWidth() + i, (int) pos.y, null);
//		width += imgs[2].getWidth();
		
		float textXplacing = pos.x + (width / 2) - (g2.getFontMetrics().stringWidth(text) / 2);
		float textYplacing = pos.y + (height / 2) + (g2.getFontMetrics().getHeight() / 2) - 5;
        if(highlight == 0)
		    g2.drawString(text, (int) textXplacing, (int) textYplacing);
        else
        {
            highlight = text.length() < highlight ? text.length() : highlight;
            g2.setColor(new Color(255, 255, 255));
            g2.drawString(text.substring(0, highlight), (int) textXplacing, (int) textYplacing);
            g2.setColor(new Color(0, 0, 0));
            g2.drawString(text.substring(highlight), (int) textXplacing + g2.getFontMetrics().stringWidth(text.substring(0, highlight)), (int) textYplacing);
        }
	}
	
	public void setText(String newText)
	{
		this.text = newText;

		if(graph == null) return;

		graph.setFont(GUIData.fonts[GUIData.FONT_MBUTTONS]);

		int length = graph.getFontMetrics().stringWidth(text);
		image = new BufferedImage(imgs[0].getWidth() + length + imgs[2].getWidth(), imgs[0].getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < imgs[0].getWidth(); x++)
				image.setRGB(x, y, imgs[0].getRGB(x,y));

		for(int y = 0; y < image.getHeight(); y++)
			for(int x = imgs[0].getWidth(); x < imgs[0].getWidth() + length; x++)
				image.setRGB(x, y, imgs[1].getRGB(0,y));

		for(int y = 0; y < image.getHeight(); y++)
			for(int x = 0; x < imgs[2].getWidth(); x++)
				image.setRGB(x + imgs[0].getWidth() - 2 + length, y, imgs[2].getRGB(x,y));

		width = image.getWidth();
	}
	
	private void makeImage(BufferedImage theImage, int mode)
	{
		if(mode == lastMode) return;
		lastMode = mode;

		BufferedImage left = new BufferedImage(15, theImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage middle = new BufferedImage(1, theImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		BufferedImage right = new BufferedImage(15, theImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		for(int y = 0; y < left.getHeight(); y++)
			for(int x = 0; x < left.getWidth(); x++)
				left.setRGB(x, y, theImage.getRGB(x, y));
		
		for(int y = 0; y < middle.getHeight(); y++)
			middle.setRGB(0, y, theImage.getRGB(16, y));
		
		for(int y = 0; y < right.getHeight(); y++)
			for(int x = 0; x < right.getWidth(); x++)
				right.setRGB(x, y, theImage.getRGB(theImage.getWidth() - right.getWidth()+ x, y));
		
		imgs[0] = left;
		imgs[1] = middle;
		imgs[2] = right;
		
		height = theImage.getHeight();

		setText(text);
	}

    public void highlight(int number)
    {
        this.highlight = number;
    }

	public String getType()
	{
		return "stretchybox";
	}

}
