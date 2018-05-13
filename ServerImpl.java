import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

import java.net.* ;
import java.rmi.* ;
import java.util.*;

import java.sql.Timestamp;

public class ServerImpl extends UnicastRemoteObject implements Server
{
	BlockchainImpl blockchain=null;
	private String my_addr="localhost";
	private String my_port="";

	public static void main(String [] args)
	{
		if (args.length != 1)
		{
			System.out.println("Usage : java Serveur <port du rmiregistry>") ;
			System.exit(0) ;
		}

		try
		{
			ServerImpl serv;
			serv = new ServerImpl(args) ;
			Naming.rebind("rmi://localhost:" + args[0] + "/server" ,serv) ;
			System.out.println("Serveur pret") ;
			serv.server_actions();
		}
		catch (RemoteException re) { System.out.println(re);}
		catch (MalformedURLException e) { System.out.println(e);}


	}

	public ServerImpl(String [] args) throws RemoteException
	{
		super();
		if(args.length > 0)
		{
			my_port=args[0];
			try
			{
				blockchain = new BlockchainImpl () ;
				Naming.rebind("rmi://localhost:" + args[0] + "/blockchain" ,blockchain) ;
				System.out.println("Serveur pret") ;
			}
			catch (RemoteException re) { System.out.println(re);}
			catch (MalformedURLException e) { System.out.println(e);}
		}
		
	}

	private Server addNeighbor(String addr, String port) throws InterruptedException 
	{
		Server new_neighbor=null;
		try
		{
			new_neighbor = (Server) Naming.lookup("rmi://" + addr + ":" + port + "/server") ;
			System.out.println("Client pret") ;
		}
		catch (NotBoundException re) { System.out.println(re) ; }
		catch (RemoteException re) { System.out.println(re); }
		catch (MalformedURLException e) { System.out.println(e) ; }

		return new_neighbor;
	}
		
	private void help_println()
	{
		System.out.println("\n---------HELP---------");
		System.out.println("\t- help");
		System.out.println("\t- exit");
		System.out.println("\t- neighbor <machine du Serveur> <port du rmiregistry>");
		System.out.println("---------------------\n");
	}

	public void server_actions()
	{
		help_println();
		String cmd;
		String delims = "[ ]+";
		String[] args;
		Scanner sc = new Scanner(System.in);
		while(true)
		{
			cmd= sc.nextLine();
			args =cmd.split(delims);
			if(args[0].equals("exit"))
				System.exit(0);
			else if(args[0].equals("help"))
				help_println();
			else if(args[0].equals("neighbor"))
			{
				if(args.length == 3)
				{
					try
					{
						Server new_neighbor=addNeighbor(args[1],args[2]);
						if(new_neighbor!=null)
						{
							if(blockchain.addNeighbor(new_neighbor))
								new_neighbor.newNeighbor(my_addr, my_port);
						}
						else 
							System.out.println("Impossible d'acceder à la Blockchain");
					}
					catch (InterruptedException re) { System.out.println("...") ; }
					catch (RemoteException re) { System.out.println(re); }
				}
				else
					System.out.println("\tUsage : change_block <machine du Serveur> <port du rmiregistry>");
			}
			else
				System.out.println("Unknown message : " + cmd);
		}
	}

	public void getTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign) throws RemoteException
	{

		System.out.println("getTransaction");
		if(!blockchain.findTransaction(from, to, amount, date, sign))
			blockchain.newTransaction(from, to, amount, date, sign);
	}

	public void getRewards(byte[] to, int value, String from, Timestamp date)  throws RemoteException
	{

		System.out.println("getRewards");
		if(!blockchain.findRewards(to, value, from, date))
			blockchain.addReward(to, value, from, date);
	}
	public void newNeighbor(String addr, String port) throws RemoteException
	{
		try
		{
			Server new_neighbor=addNeighbor(addr, port);
			if(new_neighbor!=null)
				blockchain.addNeighbor(new_neighbor);
			else 
				System.out.println("Impossible d'acceder à la Blockchain");
		}
		catch (InterruptedException re) { System.out.println("...") ; }
	}

	public String addr() throws RemoteException
	{
		return my_addr;
	}

	public String port() throws RemoteException
	{
		return my_port;
	}
}
