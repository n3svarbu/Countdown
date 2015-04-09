package p18.countdown.ui;

import java.awt.Point;

public class Mouse
{
	private Point mousePos;
	
	private boolean mousePressed;

	private int mouseButton;
	
	Mouse()
	{
		this.mousePos = new Point();

		this.mousePressed = false;

		this.mouseButton = 0;
	}
	
	public Point getMousePos()
	{
		return mousePos;
	}
	
	public boolean getMousePressed()
	{
		return mousePressed;
	}
	
	public int getMouseButton()
	{
		return mouseButton;
	}
	
	public void setMousePos(Point pos)
	{
		mousePos = pos;
	}
	
	public void setMousePressed(boolean pressed)
	{
		mousePressed = pressed;
	}
	
	public void setMouseButton(int button)
	{
		mouseButton = button;
	}
}
