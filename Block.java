import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Map;
import java.util.HashMap;

public class Block
{  
	private Timestamp timestamp;

	private List<Transaction> transactions;
	private List<Reward> rewards;

	private static int minNbZero=3;
	private int nbZero;

	private int proof;
	private String previous_hash;
	private	String hash;


	public Block()
	{
		timestamp = new Timestamp(System.currentTimeMillis());

		transactions = new ArrayList<Transaction>();
		rewards = new ArrayList<Reward>();

		int nbZero=0;
		int proof=0;
		previous_hash="";
		hash="";

		System.out.println("New Block ("+timestamp+")");

	}

	public Timestamp getTime()
	{
		return timestamp;
	}

	public int nbTransactions()
	{
		return transactions.size();
	}

	public String getPreviousHash()
	{
		return previous_hash;
	}

	public void setPreviousHash(String str)
	{
		previous_hash=str;
		System.out.println("\tPrevious hash set") ;
	}

	public void addReward(Reward newReward) 
	{
		rewards.add(newReward);
	}

	public boolean findReward(String rew)
	{
		for(Reward reward : rewards)
		{
			if(reward.toString().equals(rew))
				return true;
		}
		return false;
	}

	public void addTransaction(Transaction newTransaction)
	{
		transactions.add(newTransaction);
		System.out.println("\tTransaction saved") ;
	}

	public double getPoints(byte[] publicKey)
	{
		double nbPoints = 0.0;

		for(Transaction transaction : transactions)
		{
			if(Arrays.equals(transaction.sender,publicKey))
				nbPoints-=transaction.value;
			else if(Arrays.equals(transaction.receiver,publicKey))
				nbPoints+=transaction.value;
		}		

		for(Reward reward : rewards)
		{
			if(Arrays.equals(reward.receiver,publicKey))
				nbPoints+=reward.getPoints();
		}


		return nbPoints;
	}

	public boolean findTransaction(String tran)
	{
		for(Transaction transaction : transactions)
		{
			if(transaction.toString().equals(tran))
				return true;
		}
		return false;
	}

	public String getBlock()
	{
		String toHash="";
		toHash+=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timestamp);

		for(Transaction transaction : transactions)
			toHash+=transaction.toString();
		for(Reward reward : rewards)
			toHash+=reward.toString();

		return toHash;
	}

	public int getZero()
	{
		if(nbZero==0)
			return minNbZero-1;
		else
			return nbZero;
	}

	public boolean isHashValid()
	{
		String newHash="";
		String toHash=getBlock();

		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(toHash.getBytes(StandardCharsets.UTF_8));
			md.update(Integer.toString(proof).getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for(int i=0; i< bytes.length ;i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}	
			newHash = sb.toString();
		}
		catch (NoSuchAlgorithmException e){e.printStackTrace();}

		return newHash == hash;
	}

	public void resetHash()
	{
		hash="";
		nbZero=0;
	}

	public String getHash()
	{
		return hash;
	}

	public boolean setHash(int newProof)
	{
		String newHash="";
		String toHash=getBlock();

		int newNbZero=0;
		int i=0;

		try
		{
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(toHash.getBytes(StandardCharsets.UTF_8));
			md.update(Integer.toString(newProof).getBytes(StandardCharsets.UTF_8));
			byte[] bytes = md.digest();

			StringBuilder sb = new StringBuilder();
			for(i=0; i< bytes.length ;i++){
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			newHash = sb.toString();

			for(i=0; i<newHash.length();i++)
			{
				if (!(newHash.charAt(i)=='0'))
					break;
			}
			newNbZero=i;
			if(newNbZero>nbZero && newNbZero>=minNbZero)
			{
				nbZero=newNbZero;
				proof=newProof;
				hash=newHash;
				System.out.println("\tNew proof: " + newProof +"\n\tNumber of Zero: " + nbZero);
			}
		}
		catch (NoSuchAlgorithmException e){e.printStackTrace();}
		return proof==newProof;
	}

}
