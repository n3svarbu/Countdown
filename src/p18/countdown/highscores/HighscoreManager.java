package p18.countdown.highscores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import p18.countdown.data.Defs;
import p18.countdown.ui.entity.Entity;
import p18.countdown.ui.entity.EntityHighscoreRect;

import javax.swing.*;

public class HighscoreManager
{
	private static final int MAX_HS = 10;

	private static ArrayList<Highscore> HS = new ArrayList<Highscore>();
	private static ArrayList<Entity> entities = new ArrayList<Entity>();

	private static boolean showing = false;

	private static Timer showTimer = new Timer(2000, new ActionListener()
	{
		public void actionPerformed(ActionEvent e) { showing = false; }
	});
	
	public static void updateBoard(String str)
	{
		int roundsNumber;
		try { roundsNumber = Integer.parseInt(str); }
		catch (NumberFormatException e) { roundsNumber = -1; }
		ArrayList<Highscore> sublist = new ArrayList<Highscore>();
		if(!showing) entities = new ArrayList<Entity>();

		if(roundsNumber < 1)
			sublist = new ArrayList<Highscore>(HS);
		else
			for(Highscore hs : HS)
				if(hs.nOFr == roundsNumber)
					sublist.add(hs);

		if(!showing)
		{
			entities.add(new EntityHighscoreRect());
			entities.get(0).setXY(75, -50);
			entities.get(0).moveToY(55);
		}
		else
		{
			entities.get(0).moveToY(55);
			if(showTimer.isRunning())
				showTimer.stop();
		}
	
		if(sublist.isEmpty())
		{
			System.out.println("No high scores");
			if(!showing)
			{
				EntityHighscoreRect rect = new EntityHighscoreRect("", "", "No high scores", "");
				rect.setXY(75, -50);
				rect.moveToY(85);
				entities.add(rect);
			}
			else
			{
				EntityHighscoreRect rect = ((EntityHighscoreRect) entities.get(1));
				rect.setInfo("", "", "No high scores", "");
				rect.moveToY(85);
				for(int i = 2; i < entities.size(); i++)
					entities.get(i).moveToY(470);
			}
			return;
		}
		Highscore[] hsarr = new Highscore[sublist.size()];
		for(int i = 0; i < hsarr.length; i++)
			hsarr[i] = sublist.get(i);
		Arrays.sort(hsarr);

		for(int i = 0; i < hsarr.length && i < MAX_HS; i++)
		{
			if(!showing)
			{
				EntityHighscoreRect rect = new EntityHighscoreRect("" + (i + 1), "" + hsarr[i].nOFr, hsarr[i].name, "" + hsarr[i].score);
				rect.setXY(75, -50);
				rect.moveToY(85 + 30 * i);
				entities.add(rect);
			}
			else
			{
				if(i < entities.size() - 1)
				{
					EntityHighscoreRect rect = ((EntityHighscoreRect) entities.get(i + 1));
					rect.moveToY(85 + 30 * i);
					rect.setInfo("" + (i + 1), "" + hsarr[i].nOFr, hsarr[i].name, "" + hsarr[i].score);
				}
				else
				{
					EntityHighscoreRect rect = new EntityHighscoreRect("" + (i + 1), "" + hsarr[i].nOFr, hsarr[i].name, "" + hsarr[i].score);
					rect.setXY(75, 470);
					rect.moveToY(85 + 30 * i);
					entities.add(rect);
				}
			}
		}

		if(!showing)
			showing = true;
		else
		{
			for(int i = hsarr.length + 1; i < entities.size(); i++)
				entities.get(i).moveToY(470);
		}
	}
	
	public static void addScore(int nOFr, String pName, int pScore)
	{
		int count = 0;
		HS.add(new Highscore(nOFr, pName, pScore));
		int indexOFworst = HS.size() - 1;
		int worstScore = pScore;
		for(Highscore hs : HS)
		{
			if(hs.nOFr == nOFr)
				count++;

			if(worstScore > hs.score)
			{
				worstScore = hs.score;
				indexOFworst = HS.indexOf(hs);
			}
		}
		
		if(count > MAX_HS)
			HS.remove(indexOFworst);
	}
	
	public static void saveScores()
	{
		try
		{
			File file = new File("highscores.chs");
			if(!file.exists() && !file.createNewFile())
				System.out.println("Error creating file");
	
			FileOutputStream out = new FileOutputStream(file);
			for(Highscore hs : HS)
			{
				out.write(hs.nOFr);
				out.write(hs.name.length());
				out.write(hs.name.getBytes());
				out.write(Defs.getBytesFromInt(hs.score));
			}
			
			out.close();
		} catch (IOException e) { e.printStackTrace(); }
	}
	
	public static void loadScores()
	{
		HS = new ArrayList<Highscore>();
		showTimer.setRepeats(false);
		try
		{
			File file = new File("highscores.chs");
			if(!file.exists())
			{
				if(!file.createNewFile())
                    System.out.println("Error creating file");
				return;
			}
	
			FileInputStream in = new FileInputStream(file);
			int data;
			while((data = in.read()) != -1)
			{
				/*
				int type1Round = 0;
				if(data == 1) type1Round = in.read();
				*/
				byte[] bName = new byte[in.read()];
				if(in.read(bName, 0, bName.length) == -1)
                    System.out.println("Error reading file");
				
				byte[] scoreBytes = { (byte) in.read(), (byte) in.read() };
				short score = Defs.getShortFromBytes(scoreBytes);
				
				HS.add(new Highscore(data, new String(bName), score/*, type1Round*/));
			}
			
			in.close();
		} catch (IOException e) { e.printStackTrace(); }
		
	}

	public static ArrayList<Entity> getEntities()
	{
		return entities;
	}

	public static boolean isShowing()
	{
		return showing;
	}

	public static void hide()
	{
		for(Entity e : entities)
			e.moveToY(470);
		showTimer.start();
	}
}
