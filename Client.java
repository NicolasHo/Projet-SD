import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import java.io.InterruptedIOException;

import java.sql.Timestamp;

public class Client
{
	Security keys;
	Thread tDigger=null;
	Blockchain blockchain;

	private class Mining implements Runnable
	{
		Blockchain blockchain;
		private byte[] publicKey;

		public Mining(Blockchain bc, byte[] pub) 
		{
			blockchain=bc;
			publicKey=pub;
		}

		public void run()
		{
			String toHash="";
			String hash="";

			int nbBlocks=-1;

			int nbZero=0;

			int proof;

			boolean invalid_hash=true;

			try
			{	
				System.out.println("Mining START");
				while(!Thread.currentThread().isInterrupted())
				{
					if(nbBlocks!=blockchain.getNumberOfBlock())
						toHash=blockchain.getBlock();

					nbZero=blockchain.getHashZero();

					proof= (int)(Math.random() * (999999999));
					MessageDigest md = MessageDigest.getInstance("SHA-512");
					md.update(toHash.getBytes(StandardCharsets.UTF_8));
					md.update(Integer.toString(proof).getBytes(StandardCharsets.UTF_8));
					byte[] bytes = md.digest();

					StringBuilder sb = new StringBuilder();
					for(int i=0; i< bytes.length ;i++){
						sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
					}
					hash = sb.toString();

					invalid_hash=true;
					for(int i=0; i<(nbZero+1);i++)
					{
						if (!(hash.charAt(i)=='0'))
						{
							invalid_hash=false;
						}
					}
					if(invalid_hash)
					{
						System.out.println("proof " + proof);
						blockchain.setHash(publicKey, proof);
					}

				}
			}
			catch (NoSuchAlgorithmException e){e.printStackTrace();System.out.println("NoSuchAlgorithmException");}
			catch (RemoteException e) { System.out.println(e);System.out.println("RemoteException");}	
			catch (Exception e) {Thread.currentThread().interrupt();e.printStackTrace();System.out.println("Exception");}

			System.out.println("Mining STOP");
		}

	}

	public static void main(String [] args)
	{
		if (args.length != 2)
		{
			System.out.println("Usage : java Client <machine du Serveur> <port du rmiregistry>") ;
			System.exit(0) ;
		}

		Client cl= new Client(args);
	}

	public Client(String [] args)
	{
		keys=new Security();

		try
		{
			change_block(args[0],args[1]);
			client_actions();
		}
		catch (InterruptedException re) { System.out.println("...") ; }

	}

	private void help_println()
	{
		System.out.println("\n---------------------------HELP---------------------------");
		System.out.println("\t- help");
		System.out.println("\t- my_points");
		System.out.println("\t- my_key");
		System.out.println("\t- change_block <machine du Serveur> <port du rmiregistry>");
		System.out.println("\t- transaction <receiver> <amount>");
		System.out.println("\t- mining <start/stop>");
		System.out.println("\t- exit");
		System.out.println("----------------------------------------------------------\n");
	}

	private boolean change_block(String addr, String port) throws InterruptedException 
	{
		boolean state=true;
		if(tDigger!=null)
			tDigger.interrupt();
		try
		{
			blockchain = (Blockchain) Naming.lookup("rmi://" + addr + ":" + port + "/blockchain") ;
			System.out.println("Client pret") ;

			Mining digger = new Mining(blockchain, keys.getKey());
			tDigger = new Thread(digger);
		}
		catch (NotBoundException re) { System.out.println(re) ; state=false;}
		catch (RemoteException re) { System.out.println(re); state=false;}
		catch (MalformedURLException e) { System.out.println(e) ; state=false;}
		return state;
	}
		
	private byte[] getSignature(byte[] to, double amount, Timestamp date)
	{
		String data = Transaction.dataToString(keys.getKey(), to, amount, date);
		return keys.generateSignature(data);
	}

	private void client_actions() throws InterruptedException 
	{
		help_println();

		if(tDigger!=null)
			tDigger.start();
		else 
			System.out.println("Impossible d'acceder à la Blockchain");

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

//--------------------------------------------HELP---------------------------------------

			else if(args[0].equals("help"))
				help_println();

//--------------------------------------------get ID---------------------------------------

			else if(args[0].equals("my_key"))
			{
					System.out.println("\nPublic key  : " + keys.getKey());
			}

//--------------------------------------------get Points---------------------------------------

			else if(args[0].equals("my_points"))
			{
				double nb_transactions = 0;
				try
				{
					nb_transactions=blockchain.getPoints(keys.getKey()); 
				}
				catch (RemoteException re) { System.out.println(re);}
				System.out.println("\tMy points : " + nb_transactions);
			}

//--------------------------------------------get ID---------------------------------------

			else if(args[0].equals("transaction"))
			{
				if(args.length == 3)
				{
					byte[] receiver = args[1].getBytes();
					Timestamp date = new Timestamp(System.currentTimeMillis());
					byte[] sign = getSignature(receiver, Double.parseDouble(args[2]), date);
					try
					{
						blockchain.newTransaction(keys.getKey(), receiver, Double.parseDouble(args[2]), date, sign); 
					}
					catch (RemoteException re) { System.out.println(re);}
					catch (NumberFormatException e) {System.out.println("\tLe montant n'est pas un nombre");}
				}

				else
					System.out.println("\tUsage : transaction <receiver> <amount>");
			}

//--------------------------------------------get ID---------------------------------------

			else if(args[0].equals("mining"))
			{
				if(args[1].equals("start"))
					tDigger.start();
				else if(args[1].equals("stop"))
					tDigger.interrupt();
				else
					System.out.println("\tUsage : mining <start/stop>");
			}
			
//--------------------------------------------Unknown---------------------------------------

			else if(args[0].equals("change_block"))
			{
				if(args.length == 3)
				{
					if(change_block(args[1],args[2]))
						tDigger.start();
					else 
						System.out.println("Impossible d'acceder à la Blockchain");
				}
				else
					System.out.println("\tUsage : change_block <machine du Serveur> <port du rmiregistry>");
			}

//--------------------------------------------Unknown---------------------------------------

			else
				System.out.println("Unknown message : " + cmd);

		}
	}

}
