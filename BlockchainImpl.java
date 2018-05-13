import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;

import java.sql.Timestamp;


public class BlockchainImpl extends UnicastRemoteObject implements Blockchain
{  
	static private int maxTransactions=5;
	private int currentTransaction=0;
	private List<Block> blocks=  new ArrayList<Block>();
	private List<Server> neighbors=  new ArrayList<Server>();
	private List<Transaction> waitingTransactions = new ArrayList<Transaction>();
	private Window viewer;

	public BlockchainImpl () throws RemoteException
	{
		super();
		viewer= new Window();
		blocks.add(new Block());
		viewer.blockchain((ArrayList<Block>)blocks);

	}

	private void newBlock() throws RemoteException
	{ 
		if(blocks.size()<2)
		{
			blocks.add(new Block());
			currentTransaction=0;
			viewer.blockchain((ArrayList<Block>)blocks);
		}
		else if(blocks.get(blocks.size()-2).getHash() != "")
		{
			if(blocks.size()==2)
				blocks.get(0).resetHash();

			blocks.add(new Block());
			currentTransaction=0;
			System.out.println("Creation of a new Block");

			while(waitingTransactions.size()>0 && currentTransaction<maxTransactions)
			{
				blocks.get(blocks.size()-1).addTransaction(waitingTransactions.get(0));
				waitingTransactions.remove(0);
				currentTransaction++;
			}

		}
		else
			System.out.println("Set up waiting transactions");
	}

	public boolean isChainValid()
	{
		if(blocks.size()>1 && !blocks.get(0).isHashValid())
			return false;
		String prev_hash=blocks.get(0).getHash();
		for(int i=1;i<blocks.size();i++)
		{
			if(blocks.get(i).getPreviousHash()!=prev_hash)
				return false;
			if(!blocks.get(i).isHashValid())
				return false;
			prev_hash=blocks.get(i).getHash();
		}
		return true;
	}

	public void newTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign) throws RemoteException
	{ 
		double senderPoints= getPoints(from);
		if(senderPoints<amount)
		{
			System.out.println("Sender has not enough points : " + senderPoints + "<" + amount);
			return;
		}
		else
		{
			Transaction newTransaction = new Transaction(from, to, amount, date, sign);
			if(!newTransaction.verifiySignature())
			{
				System.out.println("Signature erronée");
				return;
			}
			if(currentTransaction<maxTransactions)
			{
				blocks.get(blocks.size()-1).addTransaction(newTransaction);
				currentTransaction++;
				if(currentTransaction==maxTransactions)
					newBlock();
				viewer.blockchain((ArrayList<Block>)blocks);
			}
			else
				waitingTransactions.add(newTransaction);	
			broadcastTransaction(from, to, amount, date, sign);		
		}
	}

	public double getPoints (byte[] from) throws RemoteException
	{
		int nbPoints = 0;
		for (Block block : blocks) 
		{
			nbPoints+=block.getPoints(from);
		}
		return nbPoints;
	}

	public int getNumberOfBlock () throws RemoteException
	{
		return blocks.size();
	}

	public String getBlock() throws RemoteException
	{ 
		if(blocks.size()>1)
			return blocks.get(blocks.size()-2).getBlock();
		else
			return blocks.get(0).getBlock();
	}

	public void setHash(byte[] sender, int proof) throws RemoteException
	{ 
		if(blocks.size()>1)
		{
			if(blocks.get(blocks.size()-2).setHash(proof))
			{
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				addReward(sender, getHashZero(), "TODO", timestamp);
				blocks.get(blocks.size()-1).setPreviousHash(blocks.get(blocks.size()-2).getHash());
				viewer.blockchain((ArrayList<Block>)blocks);
				if(currentTransaction==maxTransactions)
					newBlock();
			}
		}
		else
		{
			if(blocks.get(0).setHash(proof))
			{
				Timestamp timestamp = new Timestamp(System.currentTimeMillis());
				addReward(sender, getHashZero(), "TODO", timestamp);
				viewer.blockchain((ArrayList<Block>)blocks);
				if(currentTransaction==maxTransactions)
					newBlock();
			}
		}
	}

	public int getHashZero() throws RemoteException
	{		
		if(blocks.size()>1)
			return blocks.get(blocks.size()-2).getZero();
		else
			return blocks.get(blocks.size()-1).getZero();

	}

	public void addReward(byte[] to, int value, String from, Timestamp date) 
	{
		Reward newReward= new Reward(to, value, from, date);
		if(blocks.size()>1)
			blocks.get(blocks.size()-2).addReward(newReward);
		else
			blocks.get(0).addReward(newReward);
		
		System.out.println("add Rewards");

		broadcastRewards(to, value, from, date);

	}

	public boolean findTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign)
	{
		String tran = Transaction.dataToString(from,to,amount,date);
		tran+=new String(sign);

		boolean end = false;

		for(int i=1; i<=blocks.size() && !end ;i++)
		{
			if((blocks.get(blocks.size()-i)).getTime().before(date))
				end=false;
			if(blocks.get(blocks.size()-i).findTransaction(tran))
				return true;
		}

		for(Transaction transaction : waitingTransactions)
		{
			if(transaction.toString().equals(tran))
				return true;
		}

		System.out.println("Doesn't find Transaction");
		return false;
	}
	
	private void broadcastTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign)
	{
		for (Server neighbor : neighbors ) 
		{
			try
			{
				neighbor.getTransaction(from, to, amount,date,sign);
			}
			catch (RemoteException re) { System.out.println("...") ; }
			System.out.println("broadcastTransaction");
		}	
	}


	public boolean findRewards(byte[] to, int value, String from, Timestamp date)
	{
		String rew = Reward.dataToString(to,value,from,date);

		boolean end = false;
		//System.out.println("size:" +blocks.size());
		for(int i=1; i<=blocks.size() && !end ;i++)
		{
		//	System.out.println("\ti=" +i + " block:" + (blocks.size()-i));
			if((blocks.get(blocks.size()-i)).getTime().before(date))
				end=false;
			if(blocks.get(blocks.size()-i).findReward(rew))
			{
		//		System.out.println("\t return TRUE");
				return true;
			}
		}

		//System.out.println("\t return FALSE");
		return false;
	}

	private void broadcastRewards(byte[] to, int value, String from, Timestamp date) 
	{
		for (Server neighbor : neighbors ) 
		{
			try
			{
				neighbor.getRewards(to, value, from, date);
			}
			catch (RemoteException re) { System.out.println("...") ; }
			System.out.println("broadcastRewards");
		}	
	}

	public boolean addNeighbor(Server neigh)
	{
		for (Server neighbor: neighbors)
		{
			try
			{
				if(neighbor.port()==neigh.port() && neighbor.addr()==neigh.addr())
					return false;
			}
			catch (RemoteException re) { System.out.println(re); }
		}
		neighbors.add(neigh);
		return true;
	}

}

