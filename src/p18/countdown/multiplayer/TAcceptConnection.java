package p18.countdown.multiplayer;

import java.io.IOException;
import java.net.ServerSocket;


public class TAcceptConnection extends Thread
{
	private	Server server;
	private ServerSocket ss;
	
	TAcceptConnection(ServerSocket ss, Server server)
	{
		this.ss = ss;
		this.server = server;
	}
	
	public void run()
	{
		try { server.pushPlayer(ss.accept()); }
		catch (IOException e) { System.out.println("Error on accepting connection @class TAcceptConnection @function run()");}
	}
}
