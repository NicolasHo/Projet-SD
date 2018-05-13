import java.rmi.Remote ; 
import java.rmi.RemoteException ; 

import java.sql.Timestamp;

public interface Server extends Remote
{

	public void getTransaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign)
		throws RemoteException ;

	public void getRewards(byte[] to, int value, String from, Timestamp date) 
		throws RemoteException ;

	public void newNeighbor(String addr, String port)
		throws RemoteException ;

	public String addr()
		throws RemoteException ;

	public String port()
		throws RemoteException ;
}
