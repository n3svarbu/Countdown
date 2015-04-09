package p18.countdown.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

import p18.countdown.game.GUIUnderStage;
import p18.countdown.ui.entity.Entity;

public class GUIStage extends JFrame
{
	private static final long serialVersionUID = 1L;
	private static final int WIN_WIDTH = 800;
	private static final int WIN_HEIGHT = 450;
	private static final Color COLOR_BORDERS = new Color(0,0,0);
	private static final GradientPaint PAINT_BACKGROUND = new GradientPaint(0, 0, new Color(140, 200, 240), 0, 460, new Color(0, 100, 200));
	
	public static boolean draw = false;
	
	private PaintJPanel panel;
	
	private GUIUnderStage stage;
	
	private Mouse mouse;
	
	public GUIStage()
	{
		super("Countdown");
		this.stage = new GUIUnderStage(this);
		this.mouse = new Mouse();
		makeWindow();
		makeListeners();
	}
	
	private void makeWindow()
	{
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	
		Dimension size = new Dimension(WIN_WIDTH, WIN_HEIGHT);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setBounds((screenSize.width / 2) - (size.width / 2), (screenSize.height / 2) - (size.height / 2), 800, 450);
		this.setMinimumSize(size);
		this.setResizable(false);
		
		this.add(panel = new PaintJPanel());
		
		panel.setPreferredSize(size);
		this.setVisible(true);
			
		this.stage.start();
	}
	
	private void makeListeners()
	{
		panel.requestFocus();
		panel.addMouseListener(new MouseListener()
		{
			public void mouseClicked(MouseEvent me) {}

			public void mouseEntered(MouseEvent me) {}

			public void mouseExited(MouseEvent me) {}

			public void mousePressed(MouseEvent me)
			{
				mouse.setMousePressed(true);
				mouse.setMouseButton(me.getButton());
			}

			public void mouseReleased(MouseEvent me)
			{
				mouse.setMousePressed(false);
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionListener()
		{
			public void mouseDragged(MouseEvent me)
			{
				mouse.setMousePos(me.getPoint());
			}
			
			public void mouseMoved(MouseEvent me)
			{
				mouse.setMousePos(me.getPoint());
			}
			
		});
		
		panel.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent ke) {}
			
			public void keyReleased(KeyEvent ke) {}

			public void keyTyped(KeyEvent ke)
			{
				//System.out.println("3. " + ke.getKeyChar() + " - " + (int) ke.getKeyChar());
				stage.typing(ke.getKeyChar());
			}
			
		});
	}
	
	public Mouse getMouse()
	{
		return mouse;
	}

	private class PaintJPanel extends JPanel
	{
		private static final long serialVersionUID = 1L;

		PaintJPanel()
		{
			super();
			this.setDoubleBuffered(true);
		}
		
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			this.requestFocus();
			Graphics2D g2 = (Graphics2D) g;
			g2.setPaint(PAINT_BACKGROUND);
			g2.fillRect(0, 0, panel.getWidth(), panel.getHeight());

		    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			for(Entity e : stage.getEntities())
				e.draw(g2);
			
			g2.setColor(COLOR_BORDERS);
			g2.fillRect(0, 0, panel.getWidth(), 2);
			g2.fillRect(0, 0, 2, panel.getHeight());
			g2.fillRect(panel.getWidth() - 2, 0, panel.getWidth(), panel.getHeight());
			g2.fillRect(0, panel.getHeight() - 2, panel.getWidth(), panel.getHeight());
			
			draw = false;
		}
	}
	
	public static void draw()
	{
		draw = true;
	}
	
	public static boolean isDrawing()
	{
		return draw;
	}
}
