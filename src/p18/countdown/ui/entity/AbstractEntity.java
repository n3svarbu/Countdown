package p18.countdown.ui.entity;

import java.awt.Dimension;
import java.awt.Rectangle;

import p18.countdown.ui.GUIStage;



public abstract class AbstractEntity implements Entity
{
	protected int width;
	protected int height;
	
	protected XY pos;
	protected XY lastPos;
	protected XY targetPos;
	protected XY speed;
	
	private float lastDistance;
	
	private boolean move;
	private boolean active;
	protected boolean focused;
	protected boolean enabled;
	protected boolean mouseOver;
	protected boolean mousePressed;
	
	AbstractEntity()
	{
		pos = new XY();
		lastPos = new XY();
		targetPos = new XY();
		speed = new XY();
		
		this.width = 0;
		this.height = 0;
		
		this.move = false;
		this.active = true;
		this.focused = false;
		this.enabled = true;
		this.mouseOver = false;
		this.mousePressed = false;
	}
	
	public void update()
	{
		if(move)
		{
			if(Math.abs(pos.x - targetPos.x) < 2 &&
			   Math.abs(pos.y - targetPos.y) < 2)
			{
				move = false;
				speed.x = 0;
				speed.y = 0;
				return;
			}
			
			GUIStage.draw();
			move();
			
			lastPos.x = pos.x;
			lastPos.y = pos.y;
			
			pos.x += speed.x;
			pos.y += speed.y;
		}
	}
	
	public void move()
	{
		float xLength = targetPos.x - pos.x;
		float yLength = targetPos.y - pos.y;
		float distance = (float) Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2));
		float normalizedDistanceLeft = distance / lastDistance;
		float relativeSpeed = 0.6f - Math.abs(normalizedDistanceLeft - 0.5f);
		//	     the close this ^ is to 0.5 the smoother start and stop will be
		/*
		if(normalizedDistanceLeft > 0.5f)
		{
			//if(Math.abs(speed.x) < Math.abs(xLength / distance))
				speed.x += 0.002f * (xLength / distance);
			//if(Math.abs(speed.y) < Math.abs(yLength / distance))
				speed.y += 0.002f * (yLength / distance);
		}
		else
		{
			if(Math.abs(speed.x) - Math.abs(xLength / distance) * 0.05 > 0)
				speed.x += speed.x * -0.005f;
			if(Math.abs(speed.y) - Math.abs(yLength / distance) * 0.05 > 0)
				speed.y += speed.y * -0.005f;
		}
		*/
		
		// SO THIS ONES' SPEED DEPENDS ON CURRENT DISTANCE AND TIME DEPENDS ON
		// THE TOTAL DISTANCE. THAT MEANS THE TRAVEL TIME IS ALWAYS THE SAME
		// DESPITE THE DISTANCE. ALSO THIS METHOD LOOKS KIND OF STIFF AS IT
		// GOES FROM POINT TO POINT IN A LINE. FOR MORE FLUIDITY - USE ABOVE
		speed.x = 0.02f * (xLength / distance) * relativeSpeed * lastDistance / 4;
		speed.y = 0.02f * (yLength / distance) * relativeSpeed * lastDistance / 4;
		// DISTANCE 0.00 0.25 0.50 0.75 1.00
		// SPEED *	 1    2    3    2    1			           <-----\
		// SO THE SPEED IS NOT DYNAMIC BUT RELETIVE TO DISTANCE!!!    |
		// THE 0.02 * vel SHOULD BE A MINIMUM OR 'base' SPEED a.k.a SPEED 1
		
		 
	//	System.out.println(speed.x + " " + speed.y);
	}
	
	public void moveToXY(float x, float y)
	{
		this.targetPos.x = x;
		this.targetPos.y = y;

//		if(!this.move)
//        {
            float xLength = targetPos.x - pos.x;
            float yLength = targetPos.y - pos.y;
            this.lastDistance = (float) Math.sqrt(Math.pow(xLength, 2) + Math.pow(yLength, 2));
//        }

        this.move = true;
	}
	
	public void moveToX(float x)
	{
		moveToXY(x, this.pos.y);
	}
	
	public void moveToY(float y)
	{
		moveToXY(this.pos.x, y);
	}
	public float getX()
	{
		return pos.x;
	}

	public float getY()
	{
		return pos.y;
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public Dimension getSize()
	{
		return new Dimension(width, height);
	}

	public void setX(float x)
	{
		setXY(x, this.pos.y);
	}

	public void setY(float y)
	{
        setXY(this.pos.x, y);
	}

	public void setXY(float x, float y)
	{
		GUIStage.draw();
		this.pos.x = x;
		this.pos.y = y;
        move = false;
	}
	
	public void setWidth(int width)
	{
		this.width = width;
	}
	
	public void setHeight(int height)
	{
		this.height = height;
	}
	
	public void setSize(Dimension d)
	{
		this.width = d.width;
		this.height = d.height;
	}
	
	public Rectangle getCollisionBounds()
	{
		return new Rectangle((int) pos.x, (int) pos.y, width, height);
	}
	
	public void setMouseOver(boolean over)
	{
		if(this.mouseOver != over)
		{
			this.mouseOver = over;
			GUIStage.draw();
		}
	}
	
	public void setMousePressed(boolean pressed)
	{
		if(this.mousePressed != pressed)
		{
			this.mousePressed = pressed;
			GUIStage.draw();
		}
	}
	
	public boolean isActive()
	{
		return active;
	}
	
	public boolean isFocused()
	{
		return focused;
	}
	
//	public boolean isEnabled()
//	{
//		return enabled;
//	}
	
	public void setActive(boolean active)
	{
		if(this.active != active)
		{
			this.active = active;
			GUIStage.draw();
		}
	}
	
	public void setFocused(boolean focused)
	{
		if(this.focused != focused)
		{
			this.focused = focused;
			GUIStage.draw();
		}
	}
	
	public void setEnabled(boolean enabled)
	{
		if(this.enabled != enabled)
		{
			this.enabled = enabled;
			GUIStage.draw();
		}
	}
}
