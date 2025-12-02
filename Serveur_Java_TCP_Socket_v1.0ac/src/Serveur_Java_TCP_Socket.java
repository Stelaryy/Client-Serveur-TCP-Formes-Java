import java.net.*; 
import java.io.*; 
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Exemple de serveur TCP en java en ecoute sur le port 1234, 
 * Attend une connexion TCP et renvoie un accusé de réception
 *
 * Auteur : Wilfrid Grassi
 * date : 25/11/2017
 * version 1.0
 **/

public class Serveur_Java_TCP_Socket 
{ 
	private static int intPort = 1234;  					//port de communication 
	private static ServerSocket Serveur = null;				//Prise de communication cote Serveur
	private static Socket Client = null;					//Prise de communication cote Client
	private static InputStream FluxEntreeEthernet = null; 	//Flux d entree TCP  Serveur <- Client
	private static OutputStream FluxSortieEthernet = null;	//Flux de sortie TCP Serveur -> Client
	
	private static boolean ServeurUp = true; 				//Propriete d etat de fonctionnement du serveur
	private static boolean ClientConnect = false;			//Propriete d etat de Client connecte
	private static char charChoixAccuses = 'N';
		
	public static void main(String[] args) throws IOException,  java.lang.InterruptedException
	{ 
    	int intNbreCarLusEthernet = 0;
    	String strRequeteRecueConvertie = null;
    	String strRequeteRecueConvertieHex = null;
    	byte byteTabRequeteRecue [] = new byte[1500];
    	
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println("#                                      Serveur TEST TCP                                          #");
		System.out.println("#                                                                                                #");
		System.out.println("# Auteur : Wilfrid Grassi                                                                        #");
		System.out.println("# Date : 25/11/2017                                                                              #");
		System.out.println("# Version : 1.0                                                                                  #");
		System.out.println("--------------------------------------------------------------------------------------------------\n");
		
		// Affichage du choix reception ou non des accuses de reception du Client
		System.out.println("Activer les accuses de receptions des requetes TCP du Client (bidirectionnels)  O/N :");
		do
		{
			charChoixAccuses = (char) System.in.read();
		}
		while( charChoixAccuses  != 'O' && charChoixAccuses != 'o' && charChoixAccuses != 'N' && charChoixAccuses !='n');
		
		if( charChoixAccuses == 'O' || charChoixAccuses == 'o' ) 
			System.out.println("Accuses de reception active.");
		else
			System.out.println("Accuses de reception desactive.");
    	
		// Boucle principale du serveur
		while( ServeurUp ) 
		{
	        System.out.println(".........................................................");
	        // initialisation des flux et socket
	        intNbreCarLusEthernet =0;
	    	FluxEntreeEthernet = null; 
	    	FluxSortieEthernet = null;
	    	Serveur = null;
	    	Client = null;
	    	
	    	//Creation d un socket de communication TCP en écoute sur le port 1234
			try 
			{ 
				Serveur = new ServerSocket(1234);    
				Temporisation(100);
			}
			catch(IOException e) 
			{
				System.out.println("Impossible d'ecouter le port: 1234 !!! \nFermeture du serveur");
				System.exit(0);
			}
			
			// Affichage des différentes interfaces TCP avec la méthode GetHostNameAndIP()
			GetHostNameAndIP();
			System.out.println("#   	Port d ecoute : [ " + intPort+" ]                                                                 #");
			System.out.println("\nServeur en attente de connexion Client...");
			
			//Acceptation d une connexion Client
			Client = Serveur.accept();  
			ClientConnect = true;
			
			System.out.println("Client connecté au serveur".toString());    	
		    // Creation d un flux de sortie avec le Client (emission de donnees serveur->Client)
			FluxSortieEthernet = Client.getOutputStream();
			
			// Envoi ou non d un accuse de reception au Client pour lui signifier qu il est connecte au serveur	
			if(EnvoyerAccuseReception("Connexion") == false)
			{
				System.out.println("Impossible de communiquer avec le Client !!! \nFermeture du serveur");
				System.exit(0);
			}
			 
			// Creation d un flux d entree avec le Client pour recevoir ses commandes (reception Serveur <- Client)
			FluxEntreeEthernet = Client.getInputStream();
			Temporisation(100);
			
			// Boucle tant que le client est connecte et que nous n avons pas recu de demande de fermeture du serveur
			while(ClientConnect && ServeurUp)
			{
				try
				{	
					System.out.println("\nAttente de la Requete Client...");
					// Lecture des trames venant du Client reception, on recupere le nombre de caracteres lus et la trame est un tableau de bytes
					intNbreCarLusEthernet = FluxEntreeEthernet.read(byteTabRequeteRecue);
					
					// Envoi ou non d un accuse de reception au Client pour lui signifier que la commande a bien ete recue
					if(EnvoyerAccuseReception("OK") == false)
					{
						System.out.println("Impossible de communiquer avec le Client !!!");
						CleanSockets();
						ClientConnect = false;
					}
	   	
					if(intNbreCarLusEthernet > 0) // si des caracteres ont ete lus
					{
						//On convertit la trame recue en chaine de caracteres pour l afficher avec des carateres imprimables
						strRequeteRecueConvertie = ConvTrameCar(byteTabRequeteRecue,intNbreCarLusEthernet); 

				    	//-----------------------------------------------------------------------------------------------------------------
				    	//  Ici sont traitees les differentes requetes de commandes envoyees par le Client
				    	//-----------------------------------------------------------------------------------------------------------------
				    	
						// Le Client une requete de demande de fermeture a distance du serveur en envoyant : "Cmd : ShutDown"
						if( strRequeteRecueConvertie.contains("Cmd : ShutDown") )  //Si l on a recu ShutDown alors fermeture du serveur
						{
							System.out.println("_______________________________________________________________________________________________________________\n");
							System.out.println(strRequeteRecueConvertie);
							System.out.println("_______________________________________________________________________________________________________________\n");
							System.out.println("^ La Commande ci-dessus a ete recue ^\n");
							System.out.println("\nFermeture du serveur par le Client\n");
							
							CleanSockets();
							ClientConnect = false;
							ServeurUp = false;
						}
						else
						{	// Le Client envoi une requete de demande de deconnexion en envoyant : "Cmd : ClientDeConnecte"
							if(  strRequeteRecueConvertie.contains("Cmd : ClientDeConnecte") && (ClientConnect == true) )
							{
								System.out.println("_______________________________________________________________________________________________________________\n");
								System.out.println(strRequeteRecueConvertie);
								System.out.println("_______________________________________________________________________________________________________________\n");
								System.out.println("^ La Commande ci-dessus a ete recue ^\n");
								System.out.println("\nLe Client vient de se deconnecter !!!   Redemarrage du serveur en cours ....\n");
								
								CleanSockets();
								ClientConnect = false;
							}
							else	// Le Client envoi,une trame de donnees
							{
								// Conversion de la trame de donnees pour un affichage en Hexadecimal et en decimal
								strRequeteRecueConvertieHex = ConvTrameCarHex(byteTabRequeteRecue,intNbreCarLusEthernet); 
								System.out.println("________________________________________________________________________________________________________________________________________________________________\n");
								System.out.println(ConvTrameCar(byteTabRequeteRecue,intNbreCarLusEthernet)); 
								System.out.println(strRequeteRecueConvertieHex);
								System.out.println("_________________________________________________________________________________________________________________________________________________________________\n");
								System.out.println("^ La Trame de Donnees  ci-dessus a ete recue ^\n");
							}
						}
					}
				} 
				catch (IOException e1) // Si il y a eu une erreur de communication alors on considere que le client est deconnecte
				{
					System.out.println("Perte de la connexion !!!".toString());
					CleanSockets();
					ClientConnect = false;
				}
			} 
		}
	}
	//------------------------------------------------------------------------------------------------------
	
	//########################################################################// 
	//		Méthode de fermeture des sockets TCP avec gestion des erreurs 	  //
	// 																	      // 																
	//########################################################################//
	public static void CleanSockets()
	{
		try
		{
			if(FluxEntreeEthernet!=null)
				FluxEntreeEthernet.close();
		} 
		catch (IOException e1) 
		{
			System.out.println("CleanSockets : Erreur sur FluxEntreeEthernet.close() !!!");
		}
		
    	try 
    	{
    		if(FluxSortieEthernet!=null)
    			FluxSortieEthernet.close();
		} 
    	catch (IOException e2) 
    	{
    		System.out.println("CleanSockets : Erreur sur FluxSortieEthernet.close() !!!");
		}
    	FluxEntreeEthernet = null; 
		FluxSortieEthernet = null;

		try 
		{
			if(Client!=null)
				Client.close();
		} 
		catch (IOException e3) 
		{
			System.out.println("CleanSockets : Erreur sur Client.close() !!!");
		}		
		Client=null;
		
		try 
		{
			if(Serveur!=null)
				Serveur.close();
		} 
		catch (IOException e4) 
		{
			System.out.println("CleanSockets : Erreur sur Serveur.close() !!!");
		}
		Serveur = null;
	}
	//------------------------------------------------------------------------------------------------------
	
	//##################################################################################// 
	// Methode d Enumeration des adaptateurs TCP actifs et des adresses ip associees    //
	//##################################################################################// 
	public static void GetHostNameAndIP()
	{
		System.out.println("\n#    Interface(s) reseau et adresse(s) disponible(s) sur ce serveur :                            #");
		// Creation d une liste pour recuperer les adresses IP des adaptateurs reseau
	    List<InetAddress> addrList = new ArrayList<InetAddress>();
	    // Utilisation d une enumeration pour lister les interfaces TCP reseau
	    Enumeration<NetworkInterface> interfaces = null;
	    try 
	    {
	    	// Stockage des informations de toutes les interfaces TCP reseau dans l enumeration
	        interfaces = NetworkInterface.getNetworkInterfaces(); 
	    } 
	    catch (SocketException e) 
	    {
	        System.out.println("Impossible de recuperer la liste des interfaces reseau !!! \nFermeture du serveur");
	        System.exit(0);
	    }

	    InetAddress localhost = null;

	    try
	    {
	    	// recuperation du nom du poste hote sur lequel fonctionne le serveur
	        localhost = InetAddress.getByName("127.0.0.1");
	    }
	    catch (UnknownHostException e) 
	    {
	    	System.out.println("Impossible de faire correspondre un nom DNS à l adresse IP Locale !!! \nFermeture du serveur");
	    	System.exit(0);
	    }

	    // recuperation des adresses de tous les adaptateur TCP reseau et affichage des informations sur les adaptateurs
	    while (interfaces.hasMoreElements()) 
	    {
	        NetworkInterface ifc = interfaces.nextElement();
	        Enumeration<InetAddress> addressesOfAnInterface = ifc.getInetAddresses();

	        while (addressesOfAnInterface.hasMoreElements()) 
	        {
	            InetAddress address = addressesOfAnInterface.nextElement();

	            if (!address.equals(localhost) && !address.toString().contains(":"))
	            {
	                addrList.add(address);
	                String strInfosAdaptateur;
	                strInfosAdaptateur = "#       [ Carte reseau : " + ifc.getDisplayName().toString() + " : " + address.getHostAddress()+ "  ]";
	                while(strInfosAdaptateur.length() < 97)
	                	strInfosAdaptateur = strInfosAdaptateur + " ";
	                System.out.println(strInfosAdaptateur+"#");
	            }
	        }
	    }
	}
	//------------------------------------------------------------------------------------------------------
	
	//############################################################################################################//    
	// Methode pour envoyer les accuses de reception au Client													  // 
	//						 									                                                  //
	// Les donnees a envoyer sont stockees dans un tableau de bytes									              //	
	// Accuses de reception utilises "Connexion" et "OK"														  //
	//############################################################################################################// 
	public static boolean EnvoyerAccuseReception(String strAccuseReception )
	{
    	boolean EtatEnvoiAccuseReception = true;
    	
		//Envoi d un accuse de reception au Client si accuses de reception active pour lui signifier qu il est connecte
		if( charChoixAccuses == 'O' || charChoixAccuses == 'o' ) 
		{ 	
	    	try 
	    	{
	    		// Envoi de l accuse de reception vers le Client
	    		System.out.println("Envoi de l accuse de reception au Client");
				FluxSortieEthernet.write(strAccuseReception.getBytes());
				FluxSortieEthernet.flush();
			} 
	    	catch (IOException e) 
	    	{
	    		// Erreur lors de l envoi au Client
	    		System.out.println("Erreur lors de l envoi de l accuse de reception au Client !!!");
	    		EtatEnvoiAccuseReception=false;
			}
		}
		return EtatEnvoiAccuseReception;	// Retour si accuse de reception bien effectue ou non
	}
	
	//############################################################################################################//    
	// Methode pour afficher le contenu	de la trame	en caracteres imprimables et au format hexa et decimal		  // 
	//						 									                                                  //
	// les bytes non imprimables sont affichés uniquement en hexa(0x..) et decimal()				              //										
	//############################################################################################################// 
	public static String ConvTrameCarHex(byte[] chainedeBytes, int taille) throws UnsupportedEncodingException
	{
		String chaine="";
		byte[] Lebyte=new byte[1];
		int lebytetoint;
				
		for(int i=0; i < taille; i++)
		{
			if(chainedeBytes[i] >= 32 && chainedeBytes[i]<127)
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				String monBytetoString = new String(Lebyte);
				chaine = chaine + " \""+monBytetoString +"\" : (0x" + Integer.toHexString(lebytetoint) + ")" + "(" + Integer.toUnsignedString(lebytetoint,10) + "), ";
			}
			else
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				if(lebytetoint < 16)
					chaine=chaine + "(0x0"+ Integer.toHexString(lebytetoint) + "), ";
				else
					chaine=chaine + "(0x"+ Integer.toHexString(lebytetoint) + "), ";
			}
		}
		return chaine;
	}
	//------------------------------------------------------------------------------------------------------
	
	//##############################################################################################//    
	// Methode pour afficher le contenu	de la trame	avec des caracters imprimables				    // 
	//							 									                               	//
	// les bytes sont convertis pour être affichés les caracteres non imprimables sont remplaces    //
	// par un '.'													                                //	
	// Les reours chariot 0x0D 0x0A sont transformes en '\n'                                        //
	//##############################################################################################// 
	public static String ConvTrameCar(byte[] chainedeBytes, int taille)
	{
		String chaine="";
		byte[] Lebyte=new byte[1];
		int lebytetoint;
				
		for(int i=0; i < taille; i++)
		{
			if(chainedeBytes[i] >= 32 && chainedeBytes[i]<127)   //Cas des caracteres affichables
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				String monBytetoString = new String(Lebyte);
				chaine = chaine + monBytetoString; 
			}
			else
			{
				if(chainedeBytes[i] == 0x0D && chainedeBytes[i+1] == 0x0A)   //Cas du retour chariot /r/n
				{
					chaine = chaine + "\n";
					i++;
				}
				else														//Cas des carateres non affichables
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
		}
		return chaine;
	}
	//------------------------------------------------------------------------------------------------------

	//##################################################################//    
	// Methode pour convertir un octet signe en entier non signe		// 												
	//##################################################################// 	
	public static int byteToUnsignedInt(byte b)
	{
    	return (0x00 << 24 | b & 0xff);
	}
	//------------------------------------------------------------------------------------------------------

	//##################################################################//    
	// Methode pour faire une pause en ms                          		// 												
	//##################################################################// 	
	public static void Temporisation(int delai)
	{
		try 
		{
		  		// Temporisation 
		  		Thread.sleep(delai);
		} 
		catch (InterruptedException ex) 
		{
			//ex.printStackTrace();
		}
	}

}

