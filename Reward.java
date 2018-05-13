import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Reward
{ 
	public byte[] receiver; 
	public int nbZero;
	public String server;
	public Timestamp timestamp;

	public Reward(byte[] to, int value, String from, Timestamp date) 
	{
		receiver = to;
		nbZero = value;
		server=from;
		timestamp = date;
	}

	public double getPoints()
	{
		return nbZero * 10.0;
	}

	public String toString()
	{
		String data="";
		data+=new String(receiver);
		data+=Integer.toString(nbZero);
		data+=server;
		data+=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(timestamp);

		return data;
	}	

	static public String dataToString(byte[] to, int value, String from, Timestamp date)
	{
		String data="";
		data+=new String(to);
		data+=Integer.toString(value);
		data+=from;
		data+=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(date);

		return data;
	}


}

