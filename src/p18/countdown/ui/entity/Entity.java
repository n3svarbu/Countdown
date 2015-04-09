package p18.countdown.ui.entity;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

public interface Entity
{
	public void draw(Graphics2D g2);
	
	public float getX();
	public float getY();
	public void setX(float x);
	public void setY(float y);
	public void setXY(float x, float y);
	
	public void moveToXY(float x, float y);
	public void moveToX(float x);
	public void moveToY(float y);
	
	public int getWidth();
	public int getHeight();
	public Dimension getSize();
	public void setWidth(int width);
	public void setHeight(int height);
	public void setSize(Dimension d);
	public Rectangle getCollisionBounds();
	
	public void setMouseOver(boolean over);
	public void setMousePressed(boolean pressed);
	
	public boolean isActive();
//	public boolean isFocused();
//	public boolean isEnabled();
	public void setActive(boolean active);
	public void setFocused(boolean focused);
	public void setEnabled(boolean enabled);
	
	public String getType();
	
	public void update();
}
