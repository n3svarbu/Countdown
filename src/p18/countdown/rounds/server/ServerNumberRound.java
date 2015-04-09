package p18.countdown.rounds.server;

import java.util.ArrayList;
import java.util.Random;

import p18.countdown.data.Defs;
import p18.countdown.multiplayer.Answer;
import p18.countdown.multiplayer.Server;
import p18.countdown.player.OnlinePlayer;

public class ServerNumberRound extends AbstractServerRound
{
	private int number;
	private int currPlayer;
	private NumberPlayer[] players;
	private ArrayList<Integer> cpyOFnumbers;
	private ArrayList<Integer> large;
	private ArrayList<Integer> small;

	private Calculator calculator;
	
	private Server srv;
	
	public ServerNumberRound(int nOp, int currPlayer, Server srv)
	{
		this.srv = srv;
		this.currPlayer = currPlayer;
		this.number = 101 + new Random().nextInt(898);
		createPlayers(nOp);
		populateNumbers();
		
		calculator = new Calculator();
	}
	
	private void createPlayers(int nOp)
	{
		players = new NumberPlayer[nOp];
		for(int i = 0; i < nOp; i++)
			players[i] = new NumberPlayer();
	}
	
	public void run()
	{
		Defs.reloadLetters();
		srv.sendTo(currPlayer, "round~msg~picking,YOU");
		srv.sendToAllExcept(currPlayer, "round~msg~picking,NOTYOU");
		{
			int lNumbers;
			lNumbers = Integer.parseInt(receiveFrom(currPlayer, true));
			setNumbers(lNumbers);
            srv.sendTo(-1, "round~msg~prestart," + lNumbers);
		}

        Thread t;
		t = new Thread(calculator);
		t.start();
		t.setPriority(Thread.MAX_PRIORITY);

		sendNumbers(-1);
        srv.readyReset();

		OnlinePlayer[] p = srv.getPlayers();

        while(!srv.allReady()) { sleepOver(); }

		srv.readyReset();
		srv.sendTo(-1, "round~msg~start," + number);
		
		int num1;
		int num2;
		char operator = ' ';
		
		started = true;
		
		while(!stop)
		{
			sleepOver();
			for(int i = 0; i < p.length && !stop; i++)
			{
				int[] nums;
				switch(players[i].stage)
				{
                    case 0: sendNumbers(i); players[i].stage++;
					case 1:
					{
						String in = receiveFrom(i, false);
						if(in == null || in.equals("0")) break;
						
						/*
						if(in == null || stop)
						{
							end();
							return;
						}
						*/
						nums = splitLine(in, i);
						if((operator = (char) nums[1]) == 0 && nums[0] != 0 && nums[2] != 0)
							srv.sendTo(i, "println~Invalid operator");
						else if(operator == '=' || (players[i].notPossible = (operator == 'x')))
						{
							players[i].stage++;
							break;
						}
						else if(operator == 'r')
						{
							players[i].setNumbers(cpyOFnumbers);
							nums[0] = 0;
							sendNumbers(i);
						}
						if((num1  = nums[0]) == 0 || (num2  = nums[2]) == 0 || !ifContains(num1, i) || !ifContains(num2, i) || ifSameNumber(num1, num2, i))
						{
							players[i].stage = 0;
							break;
						}
						
						int num3 = doCalculations(num1, operator, num2, true, i, srv);
						if(num3 > 0)
						{
                            players[i].numbers.add(players[i].numbers.indexOf(num1), num3);
							players[i].numbers.remove(players[i].numbers.indexOf(num1));
							players[i].numbers.remove(players[i].numbers.indexOf(num2));
						}
						
						players[i].stage = 0;						
						break;
					}
					case 2:
					{
						srv.setSubmitted(i);
						players[i].stage++;
						break;
					}
				}
			}
			boolean ready = true;
			for(int i = 0; i < p.length; i++)
				if(players[i].stage < 2) { ready = false; break; }
			if(ready) break;
		}
		
		end();
	}
	
	public Answer[] returnAnswers()
	{
		boolean ifPoss = ifPossible();
		Answer[] answers = AbstractServerRound.createEmptyAnswers(players.length);
		OnlinePlayer[] p = srv.getPlayers();
		for(int i = 0; i < p.length; i++)
		{
			if(players[i].notPossible)
			{
				if(ifPoss) answers[i].score = -10;
				else answers[i].score = 20;
				answers[i].answer = "impossible";
			}
			else
			{
				int delta = 11;
				for(Integer j : players[i].numbers)
				{
					int newDelta = Math.abs(j - number);
					if(newDelta < delta)
					{
						answers[i].answer = "" + j;
						delta = newDelta;
					}
				}
				if(delta == 0)
					answers[i].score = 10;
				else if(delta <= 5)
					answers[i].score = 7;
				else if(delta <= 10)
					answers[i].score = 5;
				else
				{
					answers[i].score = 0;
					answers[i].answer = "not close enough";
				}
			}
		}
		return answers;
	}
	
	private boolean ifPossible()
	{
		if(!calculator.calculated)
		{
			srv.sendTo(-1, "round~msg~possibleCalc,+");
			while(!calculator.calculated) sleepOver();
		}
		String bestPossible = calculator.getBestPossible();
		if(bestPossible.equals(""))
		{
			srv.sendTo(-1, "round~msg~possibleCalc,-");
			return false;
		}
		srv.sendTo(-1, "round~msg~possibleCalc," + bestPossible);
		return true;
	}
	
	private int[] splitLine(String input, int player)
	{
		int[] nums = {0, 0, 0};
		String[] subs = input.split(",");

        if(input.equals("reset"))
        {
            nums[1] = 'r';
            return nums;
        }
        else if(input.equals("="))
        {
            nums[1] = '=';
            return nums;
        }
        else if(input.toLowerCase().equals("x"))
        {
            nums[1] = 'x';
            return nums;
        }
		else if(subs[1].equals("+"))
			nums[1] = '+';
		else if(subs[1].equals("-"))
			nums[1] = '-';
		else if(subs[1].equals("/"))
			nums[1] = '/';
		else if(subs[1].equals("*"))
			nums[1] = '*';

        int index1 = Integer.parseInt(subs[0]);
        int index2 = Integer.parseInt(subs[2]);
        if(index1 < 0 || index2 < 0)
            return nums;

        nums[0] = players[player].numbers.get(index1);
        nums[2] = players[player].numbers.get(index2);
		
		return nums;
	}
	
	private boolean ifSameNumber(int num1, int num2, int player)
	{
		if(num1 == num2)
		{
			short count = 0;
			for(Integer i : players[player].numbers)
				if(i.equals(num1)) count++;

			return !(count > 1);
		}
		
		return false;
	}
	
	private boolean ifContains(int num, int player)
	{
		if(!players[player].numbers.contains(num))
		{
			srv.sendTo(player, "println~There is no such number (" + num + "). Try again");
			return false;
		}
			
		return true;
	}
	
	private void sendNumbers(int player)
	{
        String msg = "numbers";
		if(player == -1)
		{
			OnlinePlayer[] p = srv.getPlayers();
			for(int i = 0; i < p.length; i++)
			{
                msg = "numbers";
				for(Integer j : players[i].numbers)
					msg += "," + j;
				srv.sendTo(i, "round~msg~" + msg.substring(0, msg.length()));
			}
		}
		else
		{
			for(Integer i : players[player].numbers)
				msg += "," + i;
            srv.sendTo(player, "round~msg~" + msg.substring(0, msg.length()));
		}
	}

	private void setNumbers(int largeNR)
	{
        ArrayList<Integer> numbers;
		numbers = new ArrayList<Integer>();
		int[] cArr = new int[6];
		for(int i = 0; i < 6; i++)
		{
			if(i < largeNR)
				numbers.add(large.remove(new Random().nextInt(large.size())));
			else numbers.add(small.remove(new Random().nextInt(small.size())));
			cArr[i] = numbers.get(numbers.size() - 1);
		}
		
		for(NumberPlayer p : players)
			p.setNumbers(numbers);
		
		cpyOFnumbers = new ArrayList<Integer>(numbers);
		calculator.setData(cArr);
	}
	
	private void populateNumbers()
	{
		this.large = new ArrayList<Integer>();
		this.small = new ArrayList<Integer>();
		
		for(int i = 25; i < 101; i += 25)
			large.add(i);
		
		for(int i = 1; i < 11; i++)
			small.add(i);
	}
	
	// STATIC ----------------------------------------------------------
	
	private static int doCalculations(int num1, char operator, int num2, boolean warnings, int player, Server srv)
	{
		switch(operator)
		{
			case '+': return num1 + num2;
			case '*': return num1 * num2;
			case '-':
			{
				if(num1 - num2 <= 0)
				{
					if(warnings) srv.sendTo(player, "client~info~1~Answer must be grater than 0!");
					return -1;
				}
				return num1 - num2;
			}
			case '/':
			{
				if(num1 % num2 != 0)
				{
					if(warnings) srv.sendTo(player, "client~info~1~The second number should be multiple of first number!");
					return -1;
				}
				return num1 / num2;
			}
			default: return -1;
		}
	}
	
	private class Calculator implements Runnable
	{
		public boolean calculated;
		
		private int[] cArr;
		private int lastDelta;
		private String bestPossible;
		
		private boolean kill;
		
		Calculator()
		{
			this.calculated = false;
			this.kill = false;
		}
		
		public void run()
		{	
			permutation(0);
			calculated = true;
			while(!kill) sleepOver();
		}
		
		public String getBestPossible()
		{
			kill = true;
			return bestPossible;
		}
		
		public void setData(int [] cArr)
		{
			this.cArr = cArr;
		}
		
		private void swap(int pos1, int pos2)
	    {
			int temp = cArr[pos1];
			cArr[pos1] = cArr[pos2];
			cArr[pos2] = temp;
	    }

		private void permutation(int start)
		{
			if (start != 0)
			{
				ArrayList<Integer> comb = new ArrayList<Integer>();
				for (int i = 0; i < start; i++)
					comb.add(cArr[i]);
				
				answerCheck(comb);
			}
			else
			{
				bestPossible = "";
				lastDelta = 11;
			}

			for (int i = start; i < cArr.length; i++)
			{
				swap(start, i);
				sleepOver();
				permutation(start + 1);
				swap(start, i);
			}
		}
		
		private int answerCheck(ArrayList<Integer> array)
		{
			int pattern = 1023;
			int bestNum = 0;
			
			while(pattern > -1)
			{
				int temp = pattern;
				String currPattern = "";
				String currentString = "";
				ArrayList<Integer> comb = new ArrayList<Integer>(array);
				
				for(int i = 0; i < 5; i++)
				{
					if(temp > 4 && temp % 4 == temp)
						currPattern += "+";
					else currPattern += getSign(temp % 4);
					temp /= 4;
				}
				
				int i = 0;
				while(comb.size() > 1 && i < 5)
				{
					int num = doCalculations(comb.get(0), currPattern.charAt(i), comb.get(1), false, 0, null);
					if(num > 0)
					{
						currentString += comb.remove(0) + " " + currPattern.charAt(i) + " " + comb.remove(0) + " = " + num + "/n";
						comb.add(0, num);				
					}				
					i++;
				}
				
				int currentDelta = Math.abs(number - comb.get(0));
				if((currentDelta < 10 && currentDelta < lastDelta) || (currentDelta == lastDelta && currentString.length() < bestPossible.length()))
				{
					lastDelta = currentDelta;
					bestPossible = currentString;
					bestNum = comb.get(0);
				}

				pattern--;
			}
			return bestNum;
		}
		
		private String getSign(int pattern)
		{
			switch(pattern % 4)
			{
				case 0: return "+";
				case 1: return "-";
				case 2: return "*";
				case 3: return "/";
				default: return "";
			}
		}
	}
	
	private class NumberPlayer
	{
		private ArrayList<Integer> numbers;
		private int stage;
		
		private boolean notPossible;	
		
		NumberPlayer()
		{
			this.stage = 0;
			this.numbers = new ArrayList<Integer>();
			this.notPossible = false;
		}
		
		public void setNumbers(ArrayList<Integer> numbers)
		{
			this.numbers = new ArrayList<Integer>(numbers);
		}
	}
	
	private void sleepOver()
	{
		try
		{
			Thread.sleep(1);
		} catch (InterruptedException e) { e.printStackTrace(); }
	}
}
