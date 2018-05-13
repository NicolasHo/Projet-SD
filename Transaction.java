import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class Transaction
{ 
	public byte[] sender; 
	public byte[] receiver; 
	public double value;
	public Timestamp timestamp;
	public byte[] signature;

	public Transaction(byte[] from, byte[] to, double amount,Timestamp date, byte[] sign) 
	{
		sender = from;
		receiver = to;
		value = amount;
		timestamp = date;
		signature = sign;
	}

	private String getSignData()
	{
		return dataToString(sender, receiver, value, timestamp);
	}

	public String toString()
	{
		String data=getSignData();
		data+=new String(signature);

		return data;
	}

	public boolean verifiySignature() 
	{
		String data=getSignData();

		return Security.verifiySignature(data, signature, sender);
	}

	static public String dataToString(byte[] from, byte[] to, double amount,Timestamp date)
	{
		String data="";
		data+=new String(from);
		data+=new String(to);
		data+=Double.toString(amount);
		data+=new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(date);

		return data;
	}

}

