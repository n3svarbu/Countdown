package p18.countdown.highscores;

public class Highscore implements Comparable<Highscore>
{
	public int nOFr = 0;
	public String name = "TMI";
	public int score = 0;
	
	Highscore(int numberOfRounds, String name, int score)
	{
		this.nOFr = numberOfRounds;
		this.name = name;
		this.score = score;
	}

    public int compareTo(Highscore o)
	{
        if(o == null)
            return 0;
		return o.score - score;
	}
}
