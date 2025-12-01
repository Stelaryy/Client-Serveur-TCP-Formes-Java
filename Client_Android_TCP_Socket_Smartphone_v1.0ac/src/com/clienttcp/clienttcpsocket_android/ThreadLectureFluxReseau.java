package com.clienttcp.clienttcpsocket_android;

import java.io.IOException;
import java.io.InputStream;
/**
 * 
 * 
 *ThreadLectureFluxReseau : Permet de lire le flux réseau ethernet en provenance du serveur raspberry
 *	
 *  @author Wilfrid Grassi : 28/03/2016  v1.0
 *  
 *  
 */

//Thread d'EcouteEthernet
public class ThreadLectureFluxReseau extends Thread 
{
	private InputStream _inTrame = null;
	private String laTrameAConvertir = null;
	private boolean TrameRecue = false;
	private static boolean flagstopClient = false;
	
	public ThreadLectureFluxReseau(InputStream inTrame)
	{
		_inTrame=inTrame;
	}
	
    @Override
    public void run() 
    {
    	int nbbytes = 0;
    	byte[] recvBuf = new byte[15000];
    	String maTrame;
    	flagstopClient = false;
    	
    	System.out.println("debug msg : START lecture eth");
    	while(flagstopClient == false)
    	{	
	    	try 
	    	{
				nbbytes = _inTrame.read(recvBuf);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			if(nbbytes > 0)
			{
				TrameRecue = true;
				//Recherche des caracteres de fin de la trame 0x13 et 0x10
				int i;
				for(i=0;i<nbbytes-4;i++)
					if(recvBuf[i] == 13 && recvBuf[i+1] == 10 && recvBuf[i+2] == 13 && recvBuf[i+3] == 10)
						{recvBuf[nbbytes] = 0x00;break;}
				maTrame = ConvTrameCar(recvBuf,i);
				laTrameAConvertir = maTrame +"\n";
				if(maTrame.contains("exit") || maTrame.contains("closeserveur"))
				{
					flagstopClient = true;
				}
			}
			Temporisation(100);
    	}
    	System.out.println("debug msg : STOP lecture eth");
    }
    
    public static void stopThreadThreadLectureFluxReseau()
	{
    	flagstopClient = true;
	}
    
    public String getStringTrame()
    {
    	TrameRecue = false;
		return laTrameAConvertir;
    	
    }
    
    public boolean IsTrameRecu()
    {
   		return TrameRecue;
    }
    
    
    public static void Temporisation(int delai)
	{
		try 
		{
		  		// Temporisation 
		  		Thread.sleep(delai);
		} catch (InterruptedException ex) {
			ex.printStackTrace();}
	}
    
	//##################################################################//    
	// Methode pour afficher le contenu	de la trame					// 
	// pour les tests de mise au point								// 
//							 										//
	// les bytes sont convertis pour être affichés sous la forme 	//
	// (0x0..)														//																		
	//##################################################################// 
  /*  public static String ConvTrameCar(byte[] chainedeBytes, int taille) throws UnsupportedEncodingException
	{
		String chaine="";
		byte[] Lebyte=new byte[1];
		int lebytetoint;
				
		for(int i=0; i < taille; i++)
		{
			if(chainedeBytes[i] > 32 && chainedeBytes[i]<126)
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				String monBytetoString = new String(Lebyte);
				chaine = chaine + monBytetoString; // + " \""+monBytetoString +"\" : (0x" + Integer.toHexString(lebytetoint) + ") ";
			}
			else
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				if(lebytetoint < 16)
				{
					String monBytetoString = new String(Lebyte);
					chaine = chaine + monBytetoString;
				}
				else
				{
					String monBytetoString = new String(Lebyte);
					chaine = chaine + monBytetoString;
				}
				
			}
		}
		return chaine;
	}*/
	
	public static String ConvTrameCar(byte[] chainedeBytes, int taille) 
	{
		String chaine="";
		byte[] Lebyte=new byte[1];
		int lebytetoint;
				
		for(int i=0; i < taille; i++)
		{
			if(chainedeBytes[i] > 32 && chainedeBytes[i]<126)
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				String monBytetoString = new String(Lebyte);
				chaine = chaine + monBytetoString; 
			}
			else
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				if(lebytetoint < 16)
					chaine=chaine + ".";
				else
				{
					String monBytetoString = new String(Lebyte);
					chaine = chaine + monBytetoString;
				}
				
			}
		}
		return chaine;
	}

	public static int byteToUnsignedInt(byte b)
	{
    	return 0x00 << 24 | b & 0xff;
	}
}
