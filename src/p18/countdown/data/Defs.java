package p18.countdown.data;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Defs
{
	public static final int TCP_PORT = 6787;
	
	public static final int MAX_PLAYERS = 6;
	
	public static ArrayList<String> dictionary;
	public static ArrayList<String> conundrumD;
	private static Map<Character, Integer> indexes;
										//  a  b  c  d  e   f  g  h  i  j  k  l  m  n  o  p  q  r  s  t  u  v  w  x  y  z
	public static final int[] frequency = { 9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1 };
	public static ArrayList<Character> vowels;
	public static ArrayList<Character> consonants;

	/*
	public static final String[] vowels = { "a", "e", "i", "o", "u" };
	public static final String[] consonant = { "b", "c", "d", "f", "g", "h", "j", "k", "l", "m", "n", "p", "q",
								  "r", "s", "t", "v", "w", "x", "y", "z" };
	*/
	
	public static final int CHAR_OFFSET = 'a' - 'A';

	public static ArrayList<String> getRoundsFromPattern(String pattern)
	{
		ArrayList<String> rounds = new ArrayList<String>();
		for (int i = 0; i < pattern.length(); i++)
			switch(pattern.charAt(i))
			{
				case 'l': rounds.add("word");       break;
				case 'n': rounds.add("number");     break;
				case 'c': rounds.add("conundrum");  break;
			}
		return rounds;
	}
	
	public static int getRoundNumberFromString(String roundName)
	{
		if(roundName.equals("word"))
            return 0;
        else if(roundName.equals("number"))
		    return 1;
		else if(roundName.equals("conundrum"))
            return 2;

		return -1;
	}
	
	public static String getRoundStringFromNumber(int roundNumber)
	{
		switch(roundNumber)
		{
			case 0: 		return "word";
			case 1: 		return "number";
			case 2: 		return "conundrum";
		}
		return "";
	}
	
	public static byte[] getBytesFromInt(int integer)
	{
		byte[] first = new byte[] { (byte) ((integer >> 8 ) & 0xFF), (byte)(integer & 0xFF) };
		byte[] bytes = new byte[2];
		bytes[0] = first[0];
		bytes[1] = first[1];
		return bytes;
	}
	
	public static short getShortFromBytes(byte[] bytes)
	{
		return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getShort();
	}
	
	public static void loadDictionary()
	{
		try
		{
			indexes = new HashMap<Character, Integer>();
			dictionary = new ArrayList<String>();
			conundrumD = new ArrayList<String>();
			
			File file = new File("dictionary.txt");
			BufferedReader br;
			if(file.exists())
			{
				br = new BufferedReader(new FileReader(file));
				System.out.println("Custom dictionary file");
			}
			else
			{
				br = new BufferedReader(new InputStreamReader(Defs.class.getClassLoader().getResourceAsStream("dictionary.txt"), "UTF-8"));
				System.out.println("Default dictionary file");
			}
			
			
			String line;
			char lastChar = ' ';
			int index = 0;
			
			while((line = br.readLine()) != null)
			{
				if(line.length() > 9) continue; // only to 9 letters words
				if(line.charAt(0) != lastChar)
				{
					lastChar = line.charAt(0);
					indexes.put(lastChar, index);
				}
				dictionary.add(line);
				if(line.length() == 9)
					conundrumD.add(line);
				index++;
			}
			
			br.close();
		} catch (IOException e) { System.out.println("DEFS: Error on reading dictionary"); }
		
		if(dictionary.isEmpty()) System.out.println("No words in dictionary.");
		if(conundrumD.isEmpty()) System.out.println("No words for conundrum round are loaded.");
	}
	
	public static boolean containsDictionary(String word)
	{
		if(word == null || word.length() == 0) return false;
		if(dictionary == null)
			loadDictionary();
		
		if(dictionary.isEmpty()) return false;
		int nextIndex = indexes.get((char) (word.charAt(0) + 1)) == null ? 0 : indexes.get((char) (word.charAt(0) + 1));
		for(int i = indexes.get(word.charAt(0)); i < nextIndex; i++)
		{
			if(dictionary.get(i).equals(word))
				return true;
		}
		return false;
	}
	
	public static boolean containsConundrumD(String answer)
	{
		return conundrumD.contains(answer);
	}
	
	public static boolean containsBetween(String wrd, String lttrs)
	{
		if(wrd == null) return false;
		
		boolean stop;
		char[] word = wrd.toCharArray();
		char[] letters = lttrs.toCharArray();
		/*
		for(int i = 0; i < word.length; i++)
			System.out.print(word[i]);
		System.out.println(" ");
		for(int i = 0; i < letters.length; i++)
			System.out.print(letters[i]);
			*/
		for(char c : word)
		{
			stop = true;
			for(int j = 0; j < letters.length; j++)
				if(c == letters[j])
				{
					stop = false;
					letters[j] = '_';
					break;
				}
			if(stop) return false;
		}
		return true;
	}
	
	public static void reloadLetters()
	{
		vowels = new ArrayList<Character>();
		consonants= new ArrayList<Character>();
		for(int i = 97; i < 123; i++)
			if(i == 97 || i == 101 || i == 105 || i == 111 || i == 117)
				for(int j = 0; j < frequency[i - 97]; j++)
					vowels.add((char) i);
			else for(int j = 0; j < frequency[i - 97]; j++)
				consonants.add((char) i);
	}
	
	public static boolean isNumeric(String string)
	{
		try { Integer.parseInt(string); }
		catch (NumberFormatException e) { return false; }
		return true;
	}
	/*
	public static void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
	*/
}
