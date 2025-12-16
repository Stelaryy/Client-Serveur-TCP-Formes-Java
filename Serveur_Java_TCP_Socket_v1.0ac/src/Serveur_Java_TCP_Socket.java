import java.net.*; 
import java.io.*; 
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.*;
import javax.swing.*;

/** Exemple de serveur TCP en java en ecoute sur le port 1234, 
 * Attend une connexion TCP et renvoie un accus� de r�ception
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
	private static char charChoixAccuses = 'O';

	// UI minimale pour afficher trames recues et formes creees
	private static JFrame uiFrame;
	private static DefaultListModel<String> formesModel = new DefaultListModel<>();
	private static JTextArea logArea = new JTextArea();
		
	public static void main(String[] args) throws IOException,  java.lang.InterruptedException
	{ 
    	int intNbreCarLusEthernet = 0;
    	String strRequeteRecueConvertie = null;
    	String strRequeteRecueConvertieHex = null;
    	byte byteTabRequeteRecue [] = new byte[1500];

		// Lance une petite fenetre pour suivre les trames et les formes
		InitUI();
    	
		System.out.println("--------------------------------------------------------------------------------------------------");
		System.out.println("#                                      Serveur TEST TCP                                          #");
		System.out.println("#                                                                                                #");
		System.out.println("# Auteur : Wilfrid Grassi                                                                        #");
		System.out.println("# Date : 25/11/2017                                                                              #");
		System.out.println("# Version : 1.0                                                                                  #");
		System.out.println("--------------------------------------------------------------------------------------------------\n");
		LogTrame("Serveur demarre, en attente de connexion...");
		
		// Accuses de reception forces actifs (evite une saisie console bloquante)
		System.out.println("Accuses de reception actives (mode automatique).");
    	
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
	    	
	    	//Creation d un socket de communication TCP en �coute sur le port 1234
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
			
			// Affichage des diff�rentes interfaces TCP avec la m�thode GetHostNameAndIP()
			GetHostNameAndIP();
			System.out.println("#   	Port d ecoute : [ " + intPort+" ]                                                                 #");
			System.out.println("\nServeur en attente de connexion Client...");
			
			//Acceptation d une connexion Client
			Client = Serveur.accept();  
			ClientConnect = true;
			
			System.out.println("Client connect� au serveur".toString());    	
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
						LogTrame(strRequeteRecueConvertie);

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
								if(!TraiterTrameForme(strRequeteRecueConvertie))
								{
									System.out.println("^ La Trame de Donnees ci-dessus a ete recue (aucune forme interpretee) ^\n");
								}
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
	//		M�thode de fermeture des sockets TCP avec gestion des erreurs 	  //
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
	    	System.out.println("Impossible de faire correspondre un nom DNS � l adresse IP Locale !!! \nFermeture du serveur");
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
	// les bytes non imprimables sont affich�s uniquement en hexa(0x..) et decimal()				              //										
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
	// les bytes sont convertis pour �tre affich�s les caracteres non imprimables sont remplaces    //
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

	//------------------------------------------------------------------------------------------------------
	// Tentative d'interpretation des trames de forme (format simple "TYPE=...;A=...;B=...\n")
	// Retourne true si une forme a ete creee ou supprimee, false sinon
	public static boolean TraiterTrameForme(String trame)
	{
		if(trame == null)
			return false;

		String contenu = trame.trim();
		if(contenu.isEmpty())
			return false;

		Map<String, String> map = new HashMap<String, String>();
		try
		{
			String[] blocs = contenu.split(";");
			for(String bloc : blocs)
			{
				if(!bloc.contains("="))
					continue;
				String[] kv = bloc.split("=",2);
				map.put(kv[0].trim().toUpperCase(), kv[1].trim());
			}
		}
		catch(Exception ex)
		{
			return false;
		}

		if(!map.containsKey("TYPE"))
			return false;

		String type = map.get("TYPE").toUpperCase();
		try
		{
			switch(type)
			{
				case "RECTANGLE":
					creerRectangle(map);
					return true;
				case "CARRE":
					creerCarre(map);
					return true;
				case "ELLIPSE":
					creerEllipse(map);
					return true;
				case "CERCLE":
					creerCercle(map);
					return true;
				case "LOSANGE":
					creerLosange();
					return true;
				case "TRIANGLE":
					creerTriangle(map);
					return true;
				case "HEXAGONE":
					creerHexagone();
					return true;
				case "DELETE":
					return supprimerForme(map);
				default:
					return false;
			}
		}
		catch(Exception ex)
		{
			System.out.println("Erreur lors du traitement de la forme : " + ex.getMessage());
			return false;
		}
	}

	private static double lireDouble(Map<String,String> map, String cle, double defaut)
	{
		if(!map.containsKey(cle)) return defaut;
		try { return Double.parseDouble(map.get(cle)); } catch(Exception ex) { return defaut; }
	}

	private static Object instancier(String className, Class<?>[] sig, Object[] args) throws Exception
	{
		Class<?> clazz = Class.forName(className);
		Constructor<?> ctor = clazz.getConstructor(sig);
		return ctor.newInstance(args);
	}

	private static void afficherCreation(Object f)
	{
		try
		{
			Method getSurf = f.getClass().getMethod("getSurface");
			Method getPer = f.getClass().getMethod("getPerimetre");
			Object s = getSurf.invoke(f);
			Object p = getPer.invoke(f);
			String info = "Forme creee : " + f.getClass().getSimpleName() +
				" | Surface=" + s + " | Perimetre=" + p;
			System.out.println(info);
			AjouterForme(info);
		}
		catch(Exception ex)
		{
			String info = "Forme creee : " + f.getClass().getSimpleName();
			System.out.println(info);
			AjouterForme(info);
		}
	}

	private static void creerRectangle(Map<String,String> map) throws Exception
	{
		double a = lireDouble(map, "A", 1.0);
		double b = lireDouble(map, "B", 1.0);
		Object r = instancier("Rectangle", new Class<?>[]{double.class, double.class}, new Object[]{a, b});
		afficherCreation(r);
	}

	private static void creerCarre(Map<String,String> map) throws Exception
	{
		double c = lireDouble(map, "A", 1.0);
		Object ca = instancier("Carre", new Class<?>[]{double.class}, new Object[]{c});
		afficherCreation(ca);
	}

	private static void creerEllipse(Map<String,String> map) throws Exception
	{
		double ga = lireDouble(map, "A", 1.0);
		double pa = lireDouble(map, "B", 1.0);
		Object e = instancier("ellipse", new Class<?>[]{double.class, double.class}, new Object[]{ga, pa});
		afficherCreation(e);
	}

	private static void creerCercle(Map<String,String> map) throws Exception
	{
		double r = lireDouble(map, "R", lireDouble(map, "A", 1.0));
		Object c = instancier("cercle", new Class<?>[]{double.class}, new Object[]{r});
		afficherCreation(c);
	}

	private static void creerLosange() throws Exception
	{
		Object l = instancier("Losange", new Class<?>[]{}, new Object[]{});
		afficherCreation(l);
	}

	private static void creerTriangle(Map<String,String> map) throws Exception
	{
		double a = lireDouble(map, "A", 1.0);
		double b = lireDouble(map, "B", 1.0);
		double c = lireDouble(map, "C", -1.0);
		double ang = lireDouble(map, "ANGLE", -1.0);
		Object t;
		if(c > 0)
		{
			t = instancier("TriangleQuelconque", new Class<?>[]{double.class, double.class, double.class}, new Object[]{a, b, c});
		}
		else if(ang > 0)
		{
			double c3 = Math.sqrt(Math.max(0.0, a*a + b*b - 2*a*b*Math.cos(Math.toRadians(ang))));
			t = instancier("TriangleQuelconque", new Class<?>[]{double.class, double.class, double.class}, new Object[]{a, b, c3});
		}
		else
		{
			t = instancier("TriangleQuelconque", new Class<?>[]{}, new Object[]{});
		}
		afficherCreation(t);
	}

	private static void creerHexagone() throws Exception
	{
		Object h = instancier("HexagoneIrregulier", new Class<?>[]{}, new Object[]{});
		afficherCreation(h);
	}

	private static boolean supprimerForme(Map<String,String> map)
	{
		int id;
		try { id = Integer.parseInt(map.getOrDefault("ID", "-1")); } catch(Exception ex) { return false; }
		try
		{
			Class<?> formeClazz = Class.forName("Forme");
			Method getAt = formeClazz.getMethod("getFormeAt", int.class);
			Object f = getAt.invoke(null, id);
			if(f == null)
			{
				System.out.println("Aucune forme a l index : " + id);
				return false;
			}
			// detruire
			try { f.getClass().getMethod("detruire").invoke(f); } catch(Exception ignored) {}
			// retirer de liste
			Method retirer = formeClazz.getMethod("retirerDeListe", Object.class);
			retirer.invoke(null, f);
			String info = "Forme supprimee a l index : " + id;
			System.out.println(info);
			AjouterForme(info);
			return true;
		}
		catch(Exception ex)
		{
			System.out.println("Impossible de supprimer la forme : " + ex.getMessage());
			return false;
		}
	}

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

	//------------------------------------------------------------------------------------------------------
	// UI minimale : fenetre avec liste des formes et log des trames recues
	private static void InitUI()
	{
		SwingUtilities.invokeLater(() -> {
			uiFrame = new JFrame("Serveur TCP - Formes et Trames");
			uiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			JList<String> list = new JList<>(formesModel);
			JScrollPane listScroll = new JScrollPane(list);
			listScroll.setBorder(BorderFactory.createTitledBorder("Formes creees / supprimees"));

			logArea.setEditable(false);
			JScrollPane logScroll = new JScrollPane(logArea);
			logScroll.setBorder(BorderFactory.createTitledBorder("Trames recues"));

			JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScroll, logScroll);
			split.setResizeWeight(0.4);

			uiFrame.getContentPane().add(split);
			uiFrame.setSize(700, 500);
			uiFrame.setLocationRelativeTo(null);
			uiFrame.setVisible(true);
		});
	}

	private static void LogTrame(String trame)
	{
		if(trame == null) return;
		SwingUtilities.invokeLater(() -> {
			logArea.append(trame + "\n");
			logArea.setCaretPosition(logArea.getDocument().getLength());
		});
	}

	private static void AjouterForme(String info)
	{
		if(info == null) return;
		SwingUtilities.invokeLater(() -> formesModel.addElement(info));
	}

}

