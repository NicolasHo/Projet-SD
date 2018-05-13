import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

import java.sql.Timestamp;

public interface Blockchain extends Remote
{
	public void newTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign)
		throws RemoteException ;

	public double getPoints (byte[] from)
		throws RemoteException ;

	public int getNumberOfBlock()
		throws RemoteException ;

	public String getBlock()
		throws RemoteException ;

	public void setHash(byte[] sender, int proof)
		throws RemoteException ;

	public int getHashZero()
		throws RemoteException ;
}
