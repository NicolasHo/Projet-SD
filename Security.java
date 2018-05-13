import java.security.*;
import java.security.spec.*;
import java.util.Base64;

import java.security.NoSuchAlgorithmException;

public class Security
{ 
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public Security() 
	{
		try
		{
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			publicKey = kp.getPublic();
			privateKey = kp.getPrivate();
		}
		catch (NoSuchAlgorithmException e){e.printStackTrace();}

	}

	public byte[] getKey()
	{
		return publicKey.getEncoded();
	}

	public byte[] generateSignature(String data)
	{
		byte[] signature= (new String("")).getBytes();
		try
		{
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(privateKey);
			sign.update(data.getBytes(),0, (data.getBytes()).length);
			signature = sign.sign();
		}
		catch (NoSuchAlgorithmException e){e.printStackTrace();}
		catch (InvalidKeyException e){e.printStackTrace();}
		catch (SignatureException e){e.printStackTrace();}


		return signature;
	}

	static public boolean verifiySignature(String data, byte[] signature,  byte[] key)
	{
		try
		{
			X509EncodedKeySpec ks = new X509EncodedKeySpec(key);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey pub = kf.generatePublic(ks);

			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initVerify(pub);

			sign.update(data.getBytes(),0, (data.getBytes()).length);
		
			return sign.verify(signature);
		}
		catch (NoSuchAlgorithmException e){e.printStackTrace();}
		catch (InvalidKeyException e){e.printStackTrace();}
		catch (InvalidKeySpecException e){e.printStackTrace();}
		catch (SignatureException e){e.printStackTrace();}

		return false;

	}

}

