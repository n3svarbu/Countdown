package p18.countdown.saves;

import java.awt.HeadlessException;
import java.io.*;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import p18.countdown.data.Defs;
import p18.countdown.player.Player;
import p18.countdown.ui.InfoManager;
import p18.countdown.ui.entity.EntityInfoBox;

public class Loader
{
	public static GameSave gameSaves(boolean save, Player[] players, int currRound, ArrayList<String> rounds)
	{
		try
		{
			File file = new File("saves/");
			if(!file.exists() && !file.mkdir())
            {
                System.out.println("Failed to create folder");
	            InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "Failed to create folder");
                return null;
            }
			
			JFileChooser fc = new JFileChooser();
			fc.setFileFilter(new FileNameExtensionFilter("Countdown Save File", "csf"));
			fc.setCurrentDirectory(file);
			if(save)
			{
				if(fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					if(!file.getName().endsWith(".csf"))
						file = new File(file + ".csf");
					FileOutputStream out = new FileOutputStream(file);
					out.write("Arturas".getBytes());
					out.write(currRound); out.write(rounds.size());
					for(String r : rounds)
						out.write(Defs.getRoundNumberFromString(r));
					out.write(players.length);
					for(Player p : players)
					{
						out.write(p.getName().length());
						out.write(p.getName().getBytes());
						out.write(Defs.getBytesFromInt(p.getScore()));
					}
					out.close();
				}

				return null;
			}
			else
			{
				GameSave gs = null;
				if(fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION)
				{
					file = fc.getSelectedFile();
					FileInputStream in = new FileInputStream(file);
					byte[] signature = new byte[7];
					if(in.read(signature, 0, 7) == -1 || !new String(signature).equals("Arturas"))
					{
						InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "File is probably not .csf");
						in.close();
						return null;
					}
					currRound = in.read();
					rounds = new ArrayList<String>();
					for(int i = in.read(); i > 0; i--)
						rounds.add(Defs.getRoundStringFromNumber(in.read()));
					players = new Player[in.read()];
					for(int i = 0; i < players.length; i++)
					{
						int nSize = in.read();
						String pName = "No name player";
						if(nSize > 0)
						{
							byte[] bName = new byte[nSize];
							if(in.read(bName, 0, bName.length) == -1)
							{
								InfoManager.pushInfo(EntityInfoBox.TYPE_WARNING, "Error reading file");
								System.out.println("Error on reading");
							}
							pName = new String(bName);
						}
						byte[] scoreBytes = { (byte) in.read(), (byte) in.read() };
						short score = Defs.getShortFromBytes(scoreBytes);
						
						Player p = new Player(pName);
						p.addScore(score);
						players[i] = p;
					}
					in.close();
					gs = new GameSave(players, rounds, currRound);
				}

				return gs;
			}
		}
		catch (FileNotFoundException e) { InfoManager.pushInfo(EntityInfoBox.TYPE_ERROR, "File not found!"); }
        catch (IOException e) { e.printStackTrace(); }
		catch (HeadlessException e) { e.printStackTrace(); }
		return null;
	}
}
