import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

import java.net.* ;
import java.rmi.* ;
import java.util.*;

public class Simulation
{ 

	private List<ServerImpl> servers = new ArrayList<ServerImpl>();
	private List<Client> clients = new ArrayList<Client>();

	public static void main(String [] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage : java Simumation <Server number> <Client number>") ;
			System.exit(0) ;
		}

		int nbS = Integer.parseInt(args[0]);
		int nbC = Integer.parseInt(args[1]);
		Simulation cl= new Simulation(nbS, nbC);
	}

	public Simulation(int nbServ, int nbClient) 
	{

		for (int i=0;i<nbServ; i++) 
		{		
			try
			{
				ServerImpl serv;
				serv = new ServerImpl(1200+i);
				Naming.rebind("rmi://localhost:" + Integer.toString(1200+i) + "/server" ,serv);
				System.out.println("Serveur " + i + " : pret");
				servers.add(serv);
			}
			catch (RemoteException re) { System.out.println(re);}
			catch (MalformedURLException e) { System.out.println(e);}
		}
		for (int i=0; i<nbClient; i++) 
		{
			int servId = (int)(Math.random() * (nbServ));
			int port = 1200 + servId;
			Client cl= new Client("localhost", Integer.toString(port));
			clients.add(cl);
			System.out.println("Client " + i + " : connected to Server " + servId);
		}
		try
		{
			simulationActions();
		}
		catch (Exception re) { System.out.println(re);}
	}

	private void help_println()
	{
		System.out.println("\n---------------------------HELP---------------------------");
		System.out.println("\t- help");
		System.out.println("\t- all <stop/start>");
		System.out.println("\t- auto <number of transactions>");
		System.out.println("\t- <client id> points");
		System.out.println("\t- <client id> key");
		System.out.println("\t- <client id> change_block <server id>");
		System.out.println("\t- <client id> transaction <receiver (client id)> <amount>");
		System.out.println("\t- <client id> mining <start/stop>");
		System.out.println("\t- <server id> neighbor <neighbor (server id)>");
		System.out.println("\t- exit");
		System.out.println("----------------------------------------------------------\n");
	}

	public void simulationActions() throws InterruptedException 
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

//--------------------------------------------EXIT---------------------------------------
			if(args[0].equals("exit"))
				return;

//--------------------------------------------allDigger---------------------------------------
			else if(args[0].equals("all"))
			{
				if(args[1].equals("start") || args[1].equals("stop"))
				{
					cmd= "mining "+args[1];
					args =cmd.split(delims);

					for (int i=0;i<clients.size();i++) 
					{
						clients.get(i).actions(3, args);
					}
				}
				else
					System.out.println("Usage: allDigger <stop/start>");

			}

//--------------------------------------------auto---------------------------------------
			else if(args[0].equals("auto"))
			{
				int nb= Integer.parseInt(args[1]);
				if(nb>0)
				{
					for (int i=0;i<nb;i++) 
					{
						
						System.out.println("---------------loop: " + (i+1) + "-------------------");	
						int cl1= (int)(Math.random() * (clients.size()));
						int cl2= (int)(Math.random() * (clients.size()));
						System.out.println("Sender: " + cl1 + "  | Receiver: " + cl2);	
						clients.get(cl1).doTransaction(clients.get(cl2).getKey(), 1.0);
           				Thread.sleep(1000);
					}
					System.out.println("--------------------------------------");	
				}
				else
					System.out.println("auto <number of transactions>");

			}

//--------------------------------------------HELP---------------------------------------
			else if(args[0].equals("help"))
				help_println();
//--------------------------------------------get ID---------------------------------------
			else if(args.length==2 && args[1].equals("key"))
			{
				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=clients.size())
					System.out.println("out of range");
				else
					clients.get(Integer.parseInt(args[0])).actions(0,args);
			}

//--------------------------------------------get Points---------------------------------------
			else if(args.length==2 && args[1].equals("points"))
			{
				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=clients.size())
					System.out.println("out of range");
				else
					clients.get(Integer.parseInt(args[0])).actions(1,args);
			}

//--------------------------------------------get ID---------------------------------------
			else if(args.length==4 && args[1].equals("transaction"))
			{
				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=clients.size() ||
					Integer.parseInt(args[2])<0 || Integer.parseInt(args[2])>=clients.size() )
					System.out.println("out of range");
				else
				{
					int cl=Integer.parseInt(args[0]);
					clients.get(cl).doTransaction(clients.get(Integer.parseInt(args[2])).getKey(), Double.parseDouble(args[3]));
				}
			}

//--------------------------------------------get ID---------------------------------------
			else if(args.length==3 && args[1].equals("mining"))
			{

				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=clients.size())
					System.out.println("out of range");
				else
				{
					int cl=Integer.parseInt(args[0]);
					cmd="mining "+args[2];
					args =cmd.split(delims);
					clients.get(cl).actions(3,args);
				}
			}

//--------------------------------------------change_block---------------------------------------
			else if(args.length==3 && args[1].equals("change_block"))
			{
				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=clients.size() ||
					Integer.parseInt(args[2])<0 || Integer.parseInt(args[2])>=servers.size() )
					System.out.println("out of range");
				else
				{
					int cl=Integer.parseInt(args[0]);	
					int sv=Integer.parseInt(args[2]);
					cmd="change_block localhost " + Integer.toString(1200+sv);
					args =cmd.split(delims);
					clients.get(cl).actions(4,args);
				}	
			}

//--------------------------------------------neighbor---------------------------------------
			else if(args.length==3 && args[1].equals("neighbor"))
			{
				if(Integer.parseInt(args[0])<0 || Integer.parseInt(args[0])>=servers.size()||
					Integer.parseInt(args[2])<0 || Integer.parseInt(args[2])>=servers.size() )
					System.out.println("out of range");
				else
				{
					int sv=Integer.parseInt(args[0]);
					int nb=Integer.parseInt(args[2]);
					try
					{
						servers.get(sv).newNeighbor("localhost", Integer.toString(1200+nb));
					}
					catch (RemoteException re) { System.out.println(re);}
				}
			}
//--------------------------------------------Unknown---------------------------------------
			else
				System.out.println("Unknown message : " + cmd);
		}
	}

}

