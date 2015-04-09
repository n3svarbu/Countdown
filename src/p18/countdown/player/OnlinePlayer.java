package p18.countdown.player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class OnlinePlayer extends Player
{
	private	Socket sock;
	private BufferedReader in;
	private PrintWriter out;
	
	public OnlinePlayer(Socket sock, String name)
	{
		super(name);

		this.sock = sock;
		try
		{
			this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			this.out = new PrintWriter(sock.getOutputStream(), true);
		}
		catch (IOException e)
		{
			System.out.println("Error on streaming @class OnlinePlayer @CONSTURCTOR");
		}
	}
	
	public PrintWriter getOUT()
	{
		return out;
	}
	
	public BufferedReader getIN()
	{
		return in;
	}
	
	public Socket getSocket()
	{
		return sock;
	}
}
