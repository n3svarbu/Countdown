package p18.countdown.player;

public class Player
{
	private String name;
	
	private int score;
	private long ping;
	
	private boolean ready;
	private boolean submitted;
	private boolean pinging;
	private boolean disconnected;

	private int timer;
	
	public Player(String name)
	{
		if(name == null || name.equals("")) this.name = "No name player";
		else this.name = name;
		this.score = 0;
		this.ping = -1;
		
		this.ready = false;
		this.submitted = false;
		this.pinging = false;
		this.disconnected = false;

		this.timer = 0;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getScore()
	{
		return score;
	}
	
	public long getPing()
	{
		return ping;
	}
	
	public boolean getReady()
	{
		return ready;
	}
	
	public boolean getSubmitted()
	{
		return submitted;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void addScore(int score)
	{
		this.score += score;
	}
	
	public void setScore(int score)
	{
		this.score = score;
	}

	public void setReady(boolean ready)
	{
		this.ready = disconnected || ready;
	}
	
	public void setSubmitted(boolean submitted)
	{
		this.submitted = submitted;
	}
	
	public void setPing(int ping)
	{
		this.ping = ping;
	}
	
	public void updatePing()
	{
		if(!pinging)
			ping = System.currentTimeMillis();
		else
		{
			ping = System.currentTimeMillis() - ping;
			timer = 0;
		}
		pinging = !pinging;
	}
	
	public boolean isPinging()
	{
		return pinging;
	}

	public boolean isDisconnected()
	{
		return disconnected;
	}

	public void disconnect()
	{
		disconnected = true;
	}

	public boolean isReady()
	{
		return ready;
	}

	public void timerInc()
	{
		timer++;
	}

	public int getTimer()
	{
		return timer;
	}
}
