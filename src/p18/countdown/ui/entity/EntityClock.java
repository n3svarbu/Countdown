package p18.countdown.ui.entity;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import p18.countdown.data.GUIData;
import p18.countdown.ui.GUIStage;

public class EntityClock extends AbstractEntity
{
	private long seconds;
	private long duration;
	private long lastTime;
	
	private boolean running;
	
	public EntityClock()
	{
		super();
		this.seconds = 0;
		this.duration = 0;
		
		this.running = false;
		
		BufferedImage img = GUIData.images[GUIData.IMG_CLOCK_FRAME];
		width = img.getWidth();
		height = img.getHeight();
	}
	
	public void draw(Graphics2D g2)
	{		
		// DRAWING FRAME - START
		g2.drawImage(GUIData.images[GUIData.IMG_CLOCK_FRAME], (int) pos.x, (int) pos.y, null);
		// DRAWING FRAME - END
		
		// DRAWING LIGHTS - START
		BufferedImage light = GUIData.images[GUIData.IMG_CLOCK_LIGHT];
		double lightW = light.getWidth() / 2.0;
		double lightH = light.getHeight() / 2.0;
		
		double rotationRequired;
		AffineTransform tx;
		AffineTransformOp op;
		
		for(long i = 0; i <= seconds; i += 1000)
		{
			rotationRequired = Math.toRadians((360.0 / 60000.0) * (i - 1000));
			tx = AffineTransform.getRotateInstance(rotationRequired, lightW, lightH);
			op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
			g2.drawImage(op.filter(light, null), (int)(pos.x + (width / 2) - lightW), (int) pos.y, null);
		}
		// DRAWING LIGHTS - END
		
		// DRAWING ARROW - START
		BufferedImage arrow = GUIData.images[GUIData.IMG_CLOCK_ARROW];
		double arrowW = arrow.getWidth() / 2.0;
		double arrowH = arrow.getHeight() / 2.0;
		rotationRequired = Math.toRadians((360.0 / 60000.0) * seconds);
		tx = AffineTransform.getRotateInstance(rotationRequired, arrowW, arrowH);
		op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);
		g2.drawImage(op.filter(arrow, null), (int) (pos.x + (width / 2) - arrowW), (int) pos.y, null);
		// DRAWING ARROW - END
	}
	
	public void update()
	{
		super.update();
		
		if(running)
		{
			long newTime = System.currentTimeMillis();
			seconds += newTime - lastTime;
			lastTime = newTime;
			if(seconds >= duration)
				running = false;

			GUIStage.draw();
		}
	}
	
	public void start(long msDuration)
	{
		this.seconds = 0;
		this.running = true;
		this.duration = msDuration;
		this.lastTime = System.currentTimeMillis();
	}
	
	public void stop()
	{
		running = false;
	}

	public String getType()
	{
		return "clock";
	}

}
