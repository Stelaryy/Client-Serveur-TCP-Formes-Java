package com.clienttcp.clienttcpsocket_android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import com.clienttcp.clienttcpsocket_androidv1pt0.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * 
 * 
 * Client_Android_Ethernet:  Client socket connexion TCP sans authentification USER/ PASSWORD.
 *						Pour test avec Horloge POV, envoi de la commande de MAJ Heure/Date
 *	
 *  @author Wilfrid Grassi : 26/11/2017  v1.0
 *  
 *  
 */
public class Client_Android_TCP_Activity extends Activity  implements OnClickListener, OnSeekBarChangeListener {

	private static String ADR_SERVEUR="192.168.1.129";
	private static final int PORT_SERVEUR_TCP=1234; //numero de port arbitraire
	private static TextView textReponseClient;
    private static TextView textReponseServeur;
    private static EditText textFieldIPServeur;
    
    private int ModeSec = 0;
    private int NbLedSec = 5;
    private int ModeAnn = 0;
	private int NumMotif = 0;
	private int VitDegrade = 1;
    private static TextView TxtModeSec,TxtDate,logview,TxtValCli, TxtValRot, TxtModeAnneau, TxtVitAnneau;
    private TextView[] TxtMsg = new TextView[30];
    private ToggleButton btnRechercherServeur;
    private ToggleButton btnArretServeur;
    private Button btnQuitter;
    private Button btnMAJHeure,btnSetDate,btnMajModeSec,btnMajModeAnneau;
    private static EditText ValTxtHeure, ValTxtDate;
    private SeekBar SkbModeSec, SkbDebCli, SkbFinCli, SkbVitCli, SkbVitRot, SkbModeAnneau, SkbVitesseAnneau;
    private CheckBox ChkAffHeureNum, ChkAffHeureAna, ChkAffDate;
	private CheckBox ChkRougeAnneau, ChkVertAnneau;
	
	private OutputStream _outTrame = null;
	private InputStream _inTrame = null;
	private Socket _socket = null;
	
	private static boolean reboucler = true;
	private static boolean isConnect = false;
	private static int ValeurClick = 0;
    private static final byte NB_DATA_EMI = 13;
    private static byte maTrame[]; 
    private static String laTrameLue = null;
    public static boolean Envoyer_Commande=false;
    
    public ThreadLectureFluxReseau monEcouteEthernet=null;
    
    //****************************************************************//
    //				variables pour les fonction d'Edouard Burtz 	  //
    //****************************************************************//
    protected static final int COL_ROUGE = 0xFFFF0000;
	protected static final int COL_VERT = 0xFF00FF00;
	protected static final int COL_ORANGE = 0xFFFF8000;
	private static final int NB_POINT_CARAC = 6;
	private static final int MAX_PAGE_EEP = 255;
	protected static final int MAX_CARAC_MSG = 30;
	
	//-------------- table generatrice de caractere ------------------//
		char CGRom[]={  //heritage horloge_POV.c version V2_6
				0x00,0x00,0x00,0x00,0x00,0x00,                    //
				0x00,0x00,0xBE,0x00,0x00,0x00,                    // !
				0x00,0x0E,0x00,0x0E,0x00,0x00,                    // "
				0x28,0xFE,0x28,0xFE,0x28,0x00,                    // #
				0x48,0x54,0xFE,0x54,0x24,0x00,                    // $
				0x46,0x26,0x10,0xC8,0xC4,0x00,                    // %
				0x6C,0x92,0xAA,0x44,0xA0,0x00,                    // &
				0x00,0x0A,0x06,0x00,0x00,0x00,                    // '
				0x00,0x38,0x44,0x82,0x00,0x00,                    // (
				0x00,0x82,0x44,0x38,0x00,0x00,                    // )
				0x28,0x10,0x7C,0x10,0x28,0x00,                    // *
				0x10,0x10,0x7C,0x10,0x10,0x00,                    // +
				0x00,0xA0,0x60,0x00,0x00,0x00,                    // ,
				0x00,0x10,0x10,0x10,0x00,0x00,                    // -
				0x00,0xC0,0xC0,0x00,0x00,0x00,                    // .
				0x40,0x20,0x10,0x08,0x04,0x00,                    // /

				0x7C, 0xA2, 0x92, 0x8A, 0x7C, 0x00,               // 0
				0x00, 0x84, 0xFE, 0x80, 0x00, 0x00,               // 1
				0x84, 0xC2, 0xA2, 0x92, 0x8C, 0x00,               // 2
				0x42, 0x82, 0x8A, 0x96, 0x62, 0x00,               // 3
				0x30, 0x28, 0x24, 0xFE, 0x20, 0x00,               // 4
				0x4E, 0x8A, 0x8A, 0x8A, 0x72, 0x00,               // 5
				0x78, 0x94, 0x92, 0x92, 0x60, 0x00,               // 6
				0x06, 0x02, 0xE2, 0x12, 0x0E, 0x00,               // 7
				0x6C, 0x92, 0x92, 0x92, 0x6C, 0x00,               // 8
				0x0C, 0x92, 0x92, 0x52, 0x3C, 0x00,               // 9
				0x00,0x6C,0x6C,0x00,0x00,0x00,                    // :
				0x00,0xAC,0x6C,0x00,0x00,0x00,                    // ;
				0x10,0x28,0x44,0x82,0x00,0x00,                    // <
				0x00,0x28,0x28,0x28,0x00,0x00,                    // =
				0x82,0x44,0x28,0x10,0x00,0x00,                    // >
				0x04,0x02,0xA2,0x12,0x0C,0x00,                    // ?

				0x64,0x92,0xF2,0x82,0x7C,0x00,                    // @
				0xFC,0x12,0x12,0x12,0xFC,0x00,                    // A
				0xFE,0x92,0x92,0x92,0x6C,0x00,
				0x7C,0x82,0x82,0x82,0x44,0x00,
				0xFE,0x82,0x82,0x44,0x38,0x00,                    // D
				0xFE,0x92,0x92,0x92,0x82,0x00,
				0xFE,0x12,0x12,0x12,0x02,0x00,
				0x7C,0x82,0x92,0x92,0xF4,0x00,                    // G
				0xFE,0x10,0x10,0x10,0xFE,0x00,
				0x00,0x82,0xFE,0x82,0x00,0x00,
				0x40,0x80,0x82,0x7E,0x02,0x00,                    // J
				0xFE,0x10,0x28,0x44,0x82,0x00,                    // K
				0xFE,0x80,0x80,0x80,0x80,0x00,
				0xFE,0x04,0x08,0x04,0xFE,0x00,
				0xFE,0x08,0x10,0x20,0xFE,0x00,
				0x7C,0x82,0x82,0x82,0x7C,0x00,                    // O

				0xFE,0x12,0x12,0x12,0x0C,0x00,                    // P
				0x7C,0x82,0xA2,0x42,0xBC,0x00,
				0xFE,0x12,0x32,0x52,0x8C,0x00,
				0x8C,0x92,0x92,0x92,0x62,0x00,                    // S
				0x02,0x02,0xFE,0x02,0x02,0x00,
				0x7E,0x80,0x80,0x80,0x7E,0x00,
				0x3E,0x40,0x80,0x40,0x3E,0x00,                    // V
				0x7E,0x80,0x70,0x80,0x7E,0x00,
				0xC6,0x28,0x10,0x28,0xC6,0x00,
				0x0E,0x10,0xE0,0x10,0x0E,0x00,
				0xC2,0xA2,0x92,0x8A,0x86,0x00,                    // Z
				0xFE,0x82,0x82,0x00,0x00,0x00,                    // [
				0x2A,0x24,0xF8,0x24,0x2A,0x00,
				0x82,0x82,0xFE,0x00,0x00,0x00,                    // ]
				0x08,0x04,0x02,0x04,0x08,0x00,                    // ^
				0x80,0x80,0x80,0x80,0x80,0x00,                    // _

				0x00,0x02,0x04,0x08,0x00,0x00,                    //
				0x40,0xA8,0xA8,0xA8,0xF0,0x00,                    // a
				0xFE,0x90,0x88,0x88,0x70,0x00,
				0x70,0x88,0x88,0x88,0x40,0x00,
				0x70,0x88,0x88,0x90,0xFE,0x00,
				0x70,0xA8,0xA8,0xA8,0x30,0x00,                    // e
				0x10,0xFC,0x12,0x02,0x04,0x00,
				0x18,0xA4,0xA4,0xA4,0x7C,0x00,                    // g
				0xFE,0x10,0x08,0x08,0xF0,0x00,
				0x00,0x88,0xFA,0x80,0x00,0x00,
				0x40,0x80,0x88,0x7A,0x00,0x00,                    // j
				0xFE,0x20,0x50,0x88,0x00,0x00,
				0x00,0x82,0xFE,0x80,0x00,0x00,
				0xF8,0x08,0x30,0x08,0xF0,0x00,
				0xF8,0x10,0x08,0x08,0xF0,0x00,
				0x60,0x90,0x90,0x90,0x60,0x00,                    // o

				0xF8,0x28,0x28,0x28,0x10,0x00,                    // p
				0x10,0x28,0x28,0x10,0xF8,0x00,
				0xF8,0x10,0x08,0x08,0x10,0x00,
				0x90,0xA8,0xA8,0xA8,0x40,0x00,
				0x08,0x7E,0x88,0x80,0x40,0x00,                    // t
				0x78,0x80,0x80,0x40,0xF8,0x00,
				0x38,0x40,0x80,0x40,0x38,0x00,
				0x78,0x80,0x70,0x80,0x78,0x00,
				0x88,0x50,0x20,0x50,0x88,0x00,                    // x
				0x18,0xA0,0xA0,0xA0,0x78,0x00,
				0x88,0xC8,0xA8,0x98,0x88,0x00,                    // z
				0x00,0x10,0x6C,0x82,0x00,0x00,                    // {
				0x00,0x00,0xFE,0x00,0x00,0x00,                    // |
				0x00,0x82,0x6C,0x10,0x00,0x00,                    // }
				0x10,0x10,0x54,0x38,0x10,0x00,                    // ->
				0x10,0x38,0x54,0x10,0x10,0x00};                   // <-
		
		
		
//---------------------------------------------------------------------------------------------------------------------------------------
//
//		Methode d'initialisation des composants graphiques
//
//---------------------------------------------------------------------------------------------------------------------------------------      
    /** Appellée lorsque l'activité est crée pour la première fois */
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.client_android_tcp_activity);
        
        logview = (TextView)findViewById(R.id.logview);
        textReponseClient=(TextView)findViewById(R.id.editTextClient);
        textReponseClient.setEnabled(false);
        textReponseClient.setText("");
        textReponseServeur=(TextView)findViewById(R.id.editTextServeur);
        textReponseServeur.setEnabled(false);
        textReponseServeur.setText("");
        
        btnRechercherServeur=(ToggleButton)findViewById(R.id.toggleRechercherServeur);
        btnRechercherServeur.setOnClickListener(this);
        
        btnArretServeur=(ToggleButton)findViewById(R.id.toggleButtonArretServeur);
        btnArretServeur.setOnClickListener(this);
        
        textFieldIPServeur = (EditText)findViewById(R.id.editTextIPServeur);
        ValTxtHeure = (EditText)findViewById(R.id.eTxtHeure);
        ValTxtDate = (EditText)findViewById(R.id.eTxtDate);
        TxtDate = (TextView)findViewById(R.id.txtDate);
        TxtModeSec = (TextView)findViewById(R.id.txtModeSec);
        btnQuitter=(Button)findViewById(R.id.buttonQuitter);
        btnQuitter.setOnClickListener(this);
        btnMAJHeure=(Button)findViewById(R.id.buttonMAJHeure);
        btnMAJHeure.setOnClickListener(this);
        btnSetDate = (Button)findViewById(R.id.btnSetDate);
        btnSetDate.setOnClickListener(this);
        btnMajModeSec = (Button)findViewById(R.id.bpMajModeSec);
        btnMajModeSec.setOnClickListener(this);
        btnMajModeAnneau = (Button)findViewById(R.id.bpMajModeAnneau);
        btnMajModeAnneau.setOnClickListener(this);
        
        TxtModeAnneau = (TextView)findViewById(R.id.txtModeAnneau);
        TxtVitAnneau = (TextView)findViewById(R.id.txtVitAnneau);
               
        //------------------- check box -------------------------//
        ChkAffHeureAna = (CheckBox)findViewById(R.id.chkAffHeureAna);   
        ChkAffHeureNum = (CheckBox)findViewById(R.id.chkAffHeureNum);
        ChkAffDate = (CheckBox)findViewById(R.id.chkAffDateNum);   
        ChkRougeAnneau = (CheckBox)findViewById(R.id.chkRougeAnneau);  
        ChkVertAnneau = (CheckBox)findViewById(R.id.chkVertAnneau);  
        
      //------------------ curseur -----------------------------//
	    SkbModeSec = (SeekBar) findViewById(R.id.skbModeSeconde);
	    SkbModeSec.setOnSeekBarChangeListener(this);
	    SkbModeAnneau = (SeekBar) findViewById(R.id.skbModeAnneau);
	    SkbModeAnneau.setOnSeekBarChangeListener(this);
	    SkbVitesseAnneau = (SeekBar) findViewById(R.id.skbVitesseAnneau);
	    SkbVitesseAnneau.setOnSeekBarChangeListener(this);
       	new Thread(new Client()).start(); 
    }
    
	
//---------------------------------------------------------------------------------------------------------------------------------------
//
//	Methode d'instanciation du menu de l'application
//
//---------------------------------------------------------------------------------------------------------------------------------------  
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.client__udp__tcp_, menu);
        return true;
    }
   
	
//---------------------------------------------------------------------------------------------------------------------------------------
//
//	Classe definissant l'objet du Thread Principal
//
//---------------------------------------------------------------------------------------------------------------------------------------  
    public class Client implements Runnable 
    {
		@Override
        public void run() 
        {
        	SetTxtBoutonConnexion("Se connecter au Serveur TCP");
			UpdateTxtClient("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<   Client_Android_Ethernet:  exemple de client socket                                     <<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<                                                                                                                                <<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<   Connexion TCP sans authentification USER/ PASSWORD.                        <<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<   Gestion des accuses de reception                                                                  <<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<   @author Wilfrid Grassi : 26/11/2017  v1.0                                                   <<<\n",Color.BLACK,Color.CYAN);
			UpdateTxtClient("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n",Color.BLACK,Color.CYAN);
			
        	EnableDisableUI(false); //active ou desactive les boutons saisie texte etc..
    		//---------------- initialisation date -------------------//
    		UpdateTxtDate(affiche_date(),Color.WHITE,Color.BLACK);
        	UpdateTxtClient("\nLancez une connexion au serveur en cliquant sur connexion\n",Color.BLACK,Color.GREEN);
        	      	
	        while( reboucler )
	        {
	        	switch(ValeurClick)
	        	{
	        	case 1:
	        		ValeurClick = 0;
	        		ConnexionTCP(false); //Connexion au serveur TCP
	        		break;
	        	
	        	case 2:
	        		ValeurClick = 0;
	        		ConnexionTCP(true);  //Deconnexion du serveur TCP
	        		break;
	        		
	        	case 3:
	        		ValeurClick = 0;
	        		MAJDateHeure();	//Mise a jour de l heure
	        		break;
	        		
	        	case 4:
	    	    	ValeurClick = 0;
	    	    	MajModeSec();	//Mise a jour des secondes
	        		break;
	        		
	        	case 5:
	    	    	ValeurClick = 0;
	    	    	MajModeAnneau();	//Mise a jour des anneaux
	        		break;
	        		
	        	case 99:
	        		ValeurClick = 0;
	        		QuitterApplication();   //On quitte l application
	        		reboucler = false;
	        		
	        	case 100:
	        		ValeurClick=0;
	        		ArreterServeur();
	        		ConnexionTCP(true);  //Deconnexion du serveur TCP
	        		UpdateTxtLogview(false,"Dernière commande éxécutée :",COL_VERT);
	        		UpdateTxtLogview(true,"Cmd : ShutDown\n",COL_VERT);
	        		break;
	        	}
	        	
	        	if(isConnect)
					try {
						LectureFluxTCP();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	        	
				Temporisation(10);	
	        }		
	        
	        //Fin de l'application après click sur quitter
	        UpdateTxtClient("\n\nDeconnexion et fermeture du client Android ...\n",Color.BLACK,Color.RED);
			Temporisation(3000);// délai d'attente pour être sur que le serveur à prit en compte la demande d'arrêt du client
			
			if(monEcouteEthernet != null)  // Fermeture du flux de lecture si celui-ci a ete cree
				ThreadLectureFluxReseau.stopThreadThreadLectureFluxReseau();
			Temporisation(200);
			
			try 
			{
				_outTrame.close();
			} 
			catch (Exception e) 
			{
			}
			
			try 
			{
				_inTrame.close();
			} 
			catch (Exception e) 
			{
			}
			
			try 
			{
				_socket.close();
			} 
			catch (Exception e) 
			{
			}
			
			Thread.currentThread().interrupt(); 
			System.exit(0);
		}
    }     
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//    	Methode de lecture du Flux TCP en entrée faisant appel au thread de Lecture (lecture non bloquante)
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void LectureFluxTCP() throws IOException, InterruptedException
    {
    	if(monEcouteEthernet != null)
	    	if(monEcouteEthernet.IsTrameRecu())
			{	
				EnableDisableUI(true); //active ou desactive les boutons saisie texte etc..
				laTrameLue = monEcouteEthernet.getStringTrame();
				if(laTrameLue != null)
					UpdateTxtServeur(laTrameLue,Color.LTGRAY,Color.BLACK);
			}
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//    	Methode de connexion au serveur distant TCP
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public boolean ConnexionTCP(boolean Deconnect)
    {
    	if(!isConnect && !Deconnect)
		{
			ADR_SERVEUR = textFieldIPServeur.getText().toString();
			UpdateTxtClient("Demande de connexion au serveur : IP du Serveur [ " + ADR_SERVEUR +" ]-- Port de connexion du serveur [ "+ PORT_SERVEUR_TCP +" ]\n",Color.BLACK,Color.CYAN);

			InetAddress addr = null;
			
			try 
			{
				addr = InetAddress.getByName(ADR_SERVEUR);
			} 
			catch (UnknownHostException e1) 
			{
				UpdateTxtClient("Erreur de format d'adresse !!!\n",Color.BLACK,Color.RED);
				Temporisation(3000);
				isConnect = false;
				e1.printStackTrace();
			}
			
            // Creation d un socket non connecte
            SocketAddress sockaddr = new InetSocketAddress(addr, PORT_SERVEUR_TCP);
            Socket _socket = new Socket();

            int timeout = 5000;   // 5000 millis = 5 seconds
            // Connexion du socket au serveur avec un timeout
            // If timeout occurs, SocketTimeoutException is thrown
			try 
			{
	            _socket.connect(sockaddr, timeout);
				isConnect = true;
			}
			catch (UnknownHostException e) 
			{
				UpdateTxtClient("Serveur introuvable !!!\n",Color.BLACK,Color.RED);
				Temporisation(3000);
				isConnect =false;
				e.printStackTrace();
			}
			catch (IOException e) 
			{
				UpdateTxtClient("Problème de connexion serveur !!!\n",Color.BLACK,Color.RED);
				Temporisation(3000);
				isConnect = false;
				e.printStackTrace();
			}
			
			if(isConnect)
			{
				UpdateTxtClient("Client connecte au _socket du serveur : Port de communication[ "+ _socket.getLocalPort() + " ]\n",Color.BLACK,Color.CYAN);
				ModeSec = 0;
	            NbLedSec = 5;
	            ModeAnn = 0;
	        	NumMotif = 0;
	        	VitDegrade = 1;
	        	Envoyer_Commande=false;
	        	isConnect = false;
	        	laTrameLue = null;
        		maTrame = new byte[] {'H',0,0,0,0,0,0,0,0,0,0,0,'\n'};
        						
				//Ouverture du flux en sortie, ecriture vers serveur 
				try 
				{
					_outTrame = _socket.getOutputStream();
					isConnect = true;
				} 
				catch (IOException e) 
				{
					UpdateTxtClient("Ouverture du flux de sortie impossible !!!\n",Color.BLACK,Color.RED);
					Temporisation(3000);
					isConnect = false;
					e.printStackTrace();
				}
				
				//Ouverture du flux d'entrée, lecture depuis le serveur 
				try 
				{
					_inTrame = _socket.getInputStream();
					isConnect = true;
				} 
				catch (IOException e) 
				{
					UpdateTxtClient("Ouverture du flux d'entrée impossible !!!\n",Color.BLACK,Color.RED);
					Temporisation(3000);
					isConnect = false;
					e.printStackTrace();
				}
						
				if(isConnect)
				{
					if(monEcouteEthernet != null)  // Fermeture du flux de lecture si celui-ci a ete cree
						ThreadLectureFluxReseau.stopThreadThreadLectureFluxReseau();
					Temporisation(200);
									
					monEcouteEthernet = null;
					//Creation et lancement du thread de lecture 
					monEcouteEthernet = new ThreadLectureFluxReseau(_inTrame);
					monEcouteEthernet.start();
									
					EnableDisable_btnQuitter(true);
					EnableDisableUI(true); //active ou desactive les boutons saisie texte etc..
					SetTxtBoutonConnexion("Se déconnecter du Serveur TCP");
					UpdateTxtClient("Maintenant envoyez vos commandes ....\n",Color.BLACK,Color.GREEN);
	        		//Effacement des zones de texte Client et Serveur
					ClearTxtServeur();
	        		//---------------- initialisation date -------------------//
	        		UpdateTxtDate(affiche_date(),Color.WHITE,Color.BLACK);
				}
				else
				{
					UpdateTxtClient("\nReLancez une nouvelle connexion au serveur en cliquant sur connexion\n",Color.BLACK,Color.GREEN);
					SetTxtBoutonConnexion("Se connecter au Serveur TCP");
					isConnect = false;
				}
			}
			else
			{
				//EnableDisable_btnQuitter(false);
				EnableDisableUI(false); //active ou desactive les boutons saisie texte etc..
				UpdateTxtClient("\nConnexion Impossible le serveur ne répond pas !!!\n",Color.BLACK,Color.RED);
				UpdateTxtClient("\nLancez une connexion au serveur en cliquant sur connexion\n",Color.BLACK,Color.GREEN);
				SetTxtBoutonConnexion("Se connecter au Serveur TCP");
				isConnect = false;
			}
		}	
		else
		{					
			UpdateTxtClient("Deconnexion en cours....",Color.BLACK,Color.RED);
			maTrame="Cmd ClientDeConnecte\n".getBytes();
		
			if(monEcouteEthernet != null)  // Fermeture du flux de lecture si celui-ci a ete cree
				ThreadLectureFluxReseau.stopThreadThreadLectureFluxReseau();
			
			try 
			{
				_outTrame.write(maTrame, 0, maTrame.length);
			}
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			Temporisation(1000);
			
			try 
			{
				_outTrame.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			
			try 
			{
				_inTrame.close();
			} 
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
	
			try 
			{
				_socket.close();
			} 
	        catch (Exception e) 
	        {
	        	//Fin de l'application avec une erreur de connexion car le serveur a deja ferme la connexion
	        	e.printStackTrace();
	        }
				
			UpdateTxtClient("Client déconnecté\n",Color.BLACK,Color.RED);
			EnableDisableUI(false); //active ou desactive les boutons saisie texte etc..
			SetTxtBoutonConnexion("Se connecter au Serveur TCP");
    		//---------------- initialisation date -------------------//
    		UpdateTxtDate(affiche_date(),Color.WHITE,Color.BLACK);
        	UpdateTxtClient("\nLancez une connexion au serveur en cliquant sur connexion\n",Color.BLACK,Color.GREEN);
			
			isConnect = false;
		}
    	
		return isConnect;
    }

//---------------------------------------------------------------------------------------------------------------------------------------
//
//    	Methode permettant de quitter l'application (arrêt du thread de lecture également)
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void QuitterApplication()
    {
		maTrame="Cmd ClientDeConnecte\n".getBytes();   //Cmd : ShutDown
		
		try 
		{
			_outTrame.write(maTrame, 0, maTrame.length);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
		Temporisation(500);
		isConnect = false;
		reboucler = false; //On ne reboucle pas sur la recherche de serveurs on quitte l'appli	
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//    	Methode permettant l'arret dun serveur distant
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void ArreterServeur()
    {
		SetTxtBoutonArretServeur("Arrêt du serveur en cours...");
		
		maTrame="Cmd : ShutDown\n".getBytes();   //Cmd : ShutDown
		
		try 
		{
			_outTrame.write(maTrame, 0, maTrame.length);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		Temporisation(1000);
		SetTxtBoutonArretServeur("Arrêter le serveur distant");
		
		UpdateTxtClient("Client déconnecté\n",Color.BLACK,Color.RED);
		EnableDisableUI(false); //active ou desactive les boutons saisie texte etc..
		SetTxtBoutonConnexion("Se connecter au Serveur TCP");
		//---------------- initialisation date -------------------//
		UpdateTxtDate(affiche_date(),Color.WHITE,Color.BLACK);
    	UpdateTxtClient("\nLancez une connexion au serveur en cliquant sur connexion\n",Color.BLACK,Color.GREEN);
		isConnect = false;
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//    	Methode de traitement des évènements des boutons de l'interface
//
//---------------------------------------------------------------------------------------------------------------------------------------
    @SuppressLint("SimpleDateFormat") 
	@Override
    public void onClick(View v) //Gestion des boutons de l'interface
    {    	 
		//Bouton Connexion Recherche d un serveur
     	if(v == btnRechercherServeur)
     	{   	
     		if(!isConnect)
     			ValeurClick = 1;
     		else
     			ValeurClick = 2;
     	}
     	
    	//------ mise a jour de l'heure dans variable ------//
     	if (v ==  btnSetDate) 
     	{
			GregorianCalendar gc = new GregorianCalendar();
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("HH:mm:ss");
			String heureString = dateFormat1.format(gc.getTime());
			SetValTxtDate(TxtDate.getText().toString(),Color.WHITE,Color.BLACK);
			SetValTxtHeure(heureString,Color.WHITE,Color.BLACK);
			EnableDisable_btnMAJHeure(true); //On active le bouton de MAJHeure
		}
     	       
	    //Mise à jour de l'heure sur l'horloge
	    if(v == btnMAJHeure)  
	    {
	    	ValeurClick =3;
	    }
	   
	    //------ mise a jour gestion mode seconde ------//
	    if (v == btnMajModeSec) 
	    {
	    	ValeurClick = 4;
	    }
	   
	    //-- changement du mode anneaux exterieurs --//
	    if (v==btnMajModeAnneau) 
	    {
	    	ValeurClick = 5;
	    }
	   
	    //Quitter l'application
       	if(v == btnQuitter)
		{
       		ValeurClick = 99;
		}
       	
        //Arret du serveur a distance
       	if(v == btnArretServeur)
		{
       		ValeurClick = 100;
		}
    }

//---------------------------------------------------------------------------------------------------------------------------------------
//
//        	Methode permettant de désactiver certaines fonctionnalités graphiques
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisableUI(boolean activer)
    {
		EnableDisable_btnSetDate(activer); //On active/desactive le bouton SetDate
		EnableDisable_btnMajModeSec(activer); //On active/desactive le bouton ModeSec
		EnableDisable_btnMajModeAnneau(activer); //On active/desactive le bouton ModeAnneau
		EnableDisable_btnArretServeur(activer);  // On active/desactive le bouton ArretServeur
    }
 	
//---------------------------------------------------------------------------------------------------------------------------------------
//
//        	Methode acces UI  : setter MAJ du textView Client
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void UpdateTxtClient(final String Letexte,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   textReponseClient.setBackgroundColor(bkgcouleur);
               textReponseClient.setTextColor(txtcouleur);
        	   textReponseClient.append(Letexte);
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(textReponseClient.getWindowToken(), 0);
            }
        });
    }

 	
//---------------------------------------------------------------------------------------------------------------------------------------
//
//        	Methode acces UI  : setter MAJ du textview Serveur
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void UpdateTxtServeur(final String Letexte,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   textReponseServeur.setBackgroundColor(bkgcouleur);
               textReponseServeur.setTextColor(txtcouleur);
        	   textReponseServeur.append(Letexte);
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(textReponseServeur.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//            	Methode acces UI  : setter MAJ du logview
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void UpdateTxtLogview(final boolean Append, final String msg, final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   logview.setTextColor(txtcouleur);
        	   
        	   if(!Append)
        		   logview.setText(msg+"\n");
        	   else
       				logview.append(msg +"\n");
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(TxtDate.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                	Methode acces UI  : setter MAJ du texte de la date
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void UpdateTxtDate(final String LaDate,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   TxtDate.setBackgroundColor(bkgcouleur);
        	   TxtDate.setTextColor(txtcouleur);
        	   TxtDate.setText(LaDate);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(TxtDate.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                    	Methode acces UI  : setter MAJ de la valeur de la date
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetValTxtDate(final String LaDate,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   ValTxtDate.setBackgroundColor(bkgcouleur);
        	   ValTxtDate.setTextColor(txtcouleur);
        	   ValTxtDate.setText(LaDate);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(ValTxtDate.getWindowToken(), 0);
            }
        });
    }
  
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                        	Methode acces UI  : setter MAJ de l'heure
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetValTxtHeure(final String Lheure,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   ValTxtHeure.setBackgroundColor(bkgcouleur);
        	   ValTxtHeure.setTextColor(txtcouleur);
        	   ValTxtHeure.setText(Lheure);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(ValTxtHeure.getWindowToken(), 0);
            }
        });
    }

//---------------------------------------------------------------------------------------------------------------------------------------
//
//                            	Methode acces UI  : setter MAJ du texte mode secondes
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetTxtModeSec(final String LeTexte,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   TxtModeSec.setBackgroundColor(bkgcouleur);
        	   TxtModeSec.setTextColor(txtcouleur);
        	   TxtModeSec.setText(LeTexte);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(ValTxtHeure.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                	Methode acces UI  : setter MAJ texte mode anneau
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetTxtModeAnneau(final String LeTexte,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   TxtModeAnneau.setBackgroundColor(bkgcouleur);
        	   TxtModeAnneau.setTextColor(txtcouleur);
        	   TxtModeAnneau.setText(LeTexte);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(TxtModeAnneau.getWindowToken(), 0);
            }
        });
    }

//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                	Methode acces UI  : setter MAJ de du texte vitesse Anneau
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetTxtVitAnneau(final String LeTexte,  final int bkgcouleur,  final int txtcouleur)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   TxtVitAnneau.setBackgroundColor(bkgcouleur);
        	   TxtVitAnneau.setTextColor(txtcouleur);
        	   TxtVitAnneau.setText(LeTexte);
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(TxtVitAnneau.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                    	Methode acces UI  : setter Efface le texte Client
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void ClearTxtClient()
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   textReponseClient.setText("");
        	   
        	   //-- fermeture du clavier --//
   			   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(textReponseClient.getWindowToken(), 0);
            }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                        	Methode acces UI  : setter Efface le texte Serveur
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void ClearTxtServeur()
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
           		//code exécuté par l'UI thread
        	   textReponseServeur.setText("");
        	   
        	   InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   			   imm.hideSoftInputFromWindow(textReponseServeur.getWindowToken(), 0);
            }
        });
    }
     
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                        	Methode acces UI  : setter met à jour le texte du bouton connexion
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetTxtBoutonConnexion(final String Letexte)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnRechercherServeur.setText(Letexte);
        	   if( btnRechercherServeur.getText().toString().contains("Se connecter au Serveur TCP") )
        	   {
        		   SetActiveLedBoutonConnexion(false);
        	   }
        	   else
        	   {
        		   SetActiveLedBoutonConnexion(true);
        	   }
           }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                            	Methode acces UI  : setter met à jour le texte du bouton connexion
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetActiveLedBoutonConnexion(final boolean activer)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnRechercherServeur.setChecked(activer);
           }
        });
    }
  
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                            	Methode acces UI  : setter met à jour le texte du bouton connexion
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetTxtBoutonArretServeur(final String Letexte)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnArretServeur.setText(Letexte);
        	   if( btnArretServeur.getText().toString().contains("Arrêter le serveur distant") )
        	   {
        		   SetActiveLedBoutonArretServeur(false);
        	   }
        	   else
        	   {
        		   SetActiveLedBoutonArretServeur(true);
        	   }
           }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                            	Methode acces UI  : setter met à jour le texte du bouton ArretServeur etat ON/OFF
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void SetActiveLedBoutonArretServeur(final boolean activer)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnArretServeur.setChecked(activer);
           }
        });
    }
           
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter active/desactive le bouton Quitter
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnQuitter(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnQuitter.setEnabled(enable);
           }
        });
    }
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter active/desactive le bouton MAJHeure
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnMAJHeure(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnMAJHeure.setEnabled(enable);
           }
        });
    }
     
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter active/desactive le bouton SetDate
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnSetDate(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnSetDate.setEnabled(enable);
           }
        });
    }
    
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter desactive le bouton MajModeSec
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnMajModeSec(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnMajModeSec.setEnabled(enable);
           }
        });
    }
    
    
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter active/desactive le bouton MajModeAnneau
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnMajModeAnneau(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnMajModeAnneau.setEnabled(enable);
           }
        });
    }
       
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                               Methode acces UI  : setter active/desactive le bouton btnArretServeur
//
//---------------------------------------------------------------------------------------------------------------------------------------
    public void EnableDisable_btnArretServeur(final boolean enable)
    {
    	//Déposer le Runnable dans la file d'attente de l'UI thread
    	runOnUiThread(new Runnable() {
           @Override
           public void run() {
        	   btnArretServeur.setEnabled(enable);
           }
        });
    }
//---------------------------------------------------------------------------------------------------------------------------------------
//
//                                       	Methode acces UI  : setter met à jour le texte du bouton connexion
//
//---------------------------------------------------------------------------------------------------------------------------------------
	public void EmissionDesMAJ(String msgAffiche_logview_txtClient)
	{
		String laTrameAConvertir = null;
		
		UpdateTxtLogview(false,"Dernière commande éxécutée :",COL_VERT);
		UpdateTxtLogview(true, msgAffiche_logview_txtClient+"\n",COL_VERT);
		
		try 
		{
			laTrameAConvertir= "Cmd POV -> " + ConvTrameCarHex(maTrame,NB_DATA_EMI);
			UpdateTxtClient("\nMAJ en cours...\n",Color.BLACK,Color.WHITE);
			UpdateTxtClient(laTrameAConvertir,Color.BLACK,Color.CYAN);
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		
		try 
		{
			_outTrame.write(maTrame, 0, NB_DATA_EMI);
			_outTrame.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public void Temporisation(int delai)
	{
		try 
		{
		  		// Temporisation 
		  		Thread.sleep(delai);
		} catch (InterruptedException ex) {
			ex.printStackTrace();}
	}

//##################################################################//    
// Methode pour afficher le contenu	de la trame						// 
// pour les tests de mise au point									// 
//						 											//
// les bytes sont convertis pour être affichés sous la forme 		//
// (0x0..)															//																		
//##################################################################// 
	public String ConvTrameCarHex(byte[] chainedeBytes, int taille) throws UnsupportedEncodingException
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
				chaine = chaine + " \""+monBytetoString +"\" : (0x" + Integer.toHexString(lebytetoint) + ") ";
			}
			else
			{
				Lebyte[0]=chainedeBytes[i];
				lebytetoint = byteToUnsignedInt(Lebyte[0]);
				if(lebytetoint < 16)
					chaine=chaine + "(0x0"+ Integer.toHexString(lebytetoint) + ") ";
				else
					chaine=chaine + "(0x"+ Integer.toHexString(lebytetoint) + ") ";
			}
		}
		return chaine;
	}
	
//##################################################################//    
// Methode pour afficher le contenu	de la trame						// 
// pour les tests de mise au point									// 
//							 										//
// les bytes sont convertis pour être affichés sous la forme 		//
// (0x0..)															//																		
//##################################################################// 
	public String ConvTrameCar(byte[] chainedeBytes, int taille)
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


//##################################################################//    
// Methode pour convertir un byte en entier non signé				// 
// pour les tests de mise au point									// 
//							 										//															
//##################################################################// 	
	public int byteToUnsignedInt(byte b)
	{
    	return 0x00 << 24 | b & 0xff;
	}
  
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//##########																					############//
//##########   					Méthodes de gestion Horloge POV d'Edouard Burtz					############//
//##########																					############//
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//

//##################################################################//    
// Methode appelée lors d'une modification d'un curseur				//
//	 		 														//
// PE: SeekBar  : Nom du curseur									//
//	 	   progress : valeur du curseur								//
//	 																//
//##################################################################// 	
 	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
 	{
 		//byte maTrame[] = {72,65,121,4,5,0,0,1,0,0,0,0,0};
 		int Deb, Fin, i;
 		byte DecodeMotifAff[] = {20,10,5,4,2,1};
 		
 		//Vibro(10);
 		if(seekBar == SkbModeSec) 
 		{
 			//
 			//------------- gestion mode seconde --------------//
 			//
 			ModeSec = (int)(progress / 10);
 			switch (ModeSec) 
 			{
 				case 0: 
 				{
 					SetTxtModeSec("1 point rouge",Color.RED,Color.BLACK);
 					break;
 				}
 				case 1: 
 				{
 					SetTxtModeSec("1 point vert",Color.RED,Color.BLACK);
 					break;
 				}
 				case 2: 
 				{
 					SetTxtModeSec("1 point orange",Color.RED,Color.BLACK);
 					break;
 				}
 				case 3: 
 				{
 					SetTxtModeSec("barre rouge",Color.RED,Color.BLACK);
 					break;
 				}
 				case 4: 
 				{
 					SetTxtModeSec("barre verte",Color.RED,Color.BLACK);
 					break;
 				}
 				case 5: 
 				{
 					SetTxtModeSec("barre orange",Color.RED,Color.BLACK);
 					break;
 				}
 				case 6: 
 				{
 					SetTxtModeSec("barre bicolore 5s",Color.RED,Color.BLACK);
 					NbLedSec = 5;
 					break;
 				}
 				case 7: 
 				{
 					SetTxtModeSec("barre bicolore 10s",Color.RED,Color.BLACK);
 					NbLedSec = 10;
 					ModeSec = 6;
 					break;
 				}
 				case 8: 
 				{
 					SetTxtModeSec("barre bicolore 30s",Color.RED,Color.BLACK);
 					NbLedSec = 30;
 					ModeSec = 6;
 					break;
 				}
 				case 9: 
 				{
 					SetTxtModeSec("barre tricolore 5s",Color.RED,Color.BLACK);
 					NbLedSec = 5;
 					ModeSec = 7;
 					break;
 				}
 				case 10: 
 				{
 					SetTxtModeSec("barre tricolore 10s",Color.RED,Color.BLACK);
 					NbLedSec = 10;
 					ModeSec = 7;
 					break;
 				}
 				case 11: 
 				{
 					SetTxtModeSec("barre tricolore 20s",Color.RED,Color.BLACK);
 					NbLedSec = 20;
 					ModeSec = 7;
 					break;
 				}
 			}
 		}
 		else 
 			if ((seekBar == SkbDebCli) || (seekBar == SkbFinCli)) 
 			{
	 			//
	 			//-------- selection clignottement --------------------//
	 			//
	 			Deb = (int)(SkbDebCli.getProgress()/10);
	 			Fin = (int)(SkbFinCli.getProgress()/10);
	 			for (i=0 ; i < MAX_CARAC_MSG ; i++) 
	 			{
	 				TxtMsg[i].setTextColor(Color.WHITE);
	 			}
	 		    if (Deb < Fin) 
	 		    {
	 				//-- clignottement sens antihoraire 
	 				for (i=Deb ; i < Fin+1 ; i++) 
	 				{
	 					TxtMsg[i].setTextColor(Color.YELLOW);
	 				}
	 			}
	 		    else
	 		    	if (Deb > Fin) 
	 		    	{
		 				//-- clignottement sens horaire 
		 				for (i=Deb ; i < MAX_CARAC_MSG ; i++) 
		 				{
		 					TxtMsg[i].setTextColor(Color.YELLOW);
		 				}
		 				for (i=0 ; i < Fin+1 ; i++) 
		 				{
		 					TxtMsg[i].setTextColor(Color.YELLOW);
		 				}
	 		    	}
 			
 			}
 			else 
 				if(seekBar == SkbVitCli) 
 				{
 					TxtValCli.setText(Float.toString(((float)(SkbVitCli.getProgress())/20)));
 				}
 				else 
 					if(seekBar == SkbVitRot) 
 					{
 						TxtValRot.setText(Float.toString(((float)(SkbVitRot.getProgress())/20)));
 					}
 					else 
 						if(seekBar == SkbModeAnneau) 
 						{
				 			//
				 			//------------- gestion mode seconde --------------//
				 			//
 							ModeAnn = (int)(progress / 20);
				 			switch (ModeAnn) 
				 			{
				 				case 0: 
				 				{
				 					SetTxtModeAnneau("Motif dégradé:",Color.RED,Color.BLACK);
				 					SetTxtVitAnneau(" x".concat((Integer.toString(DecodeMotifAff[NumMotif]))),Color.RED,Color.BLACK);
				 					break;
				 				}
				 				case 1: 
				 				{
				 					SetTxtModeAnneau("Anneau dégradé:",Color.RED,Color.BLACK);
				 					SetTxtVitAnneau((Float.toString(((float)((SkbVitesseAnneau.getProgress() + 1))/20))).concat(" s"),Color.RED,Color.BLACK);	
				 					break;
				 				}
				 				case 2: 
				 				{
				 					SetTxtModeAnneau("Pulse montant:",Color.RED,Color.BLACK);
				 					SetTxtVitAnneau((Float.toString(((float)((SkbVitesseAnneau.getProgress() + 1))/20))).concat(" s"),Color.RED,Color.BLACK);	
				 					break;
				 				}
				 				case 3: 
				 				{
				 					SetTxtModeAnneau("Pulse descendant:",Color.RED,Color.BLACK);
				 					SetTxtVitAnneau((Float.toString(((float)((SkbVitesseAnneau.getProgress() + 1))/20))).concat(" s"),Color.RED,Color.BLACK);	
				 					break;
				 				}
				 				case 4: 
				 				{
				 					SetTxtModeAnneau("Pulse:",Color.RED,Color.BLACK);
				 					SetTxtVitAnneau((Float.toString(((float)((SkbVitesseAnneau.getProgress() + 1))/20))).concat(" s"),Color.RED,Color.BLACK);	
				 					break;
				 				}
				 			}
 						}
 						else 
 							if(seekBar == SkbVitesseAnneau) 
 							{
				 				//
				 				//------------- gestion mode seconde --------------//
				 				//
				 				NumMotif = (int)(progress / 20);
				 				VitDegrade = (int)(progress) + 1;
				 				if (ModeAnn == 0)
				 				{
				 					SetTxtVitAnneau(" x".concat((Integer.toString(DecodeMotifAff[NumMotif]))),Color.RED,Color.BLACK);
				 				}
				 				else 
				 				{
				 					SetTxtVitAnneau((Float.toString(((float)((SkbVitesseAnneau.getProgress() + 1))/20))).concat(" s"),Color.RED,Color.BLACK);					
				 				}
 		}
 	}	

	 	
//##################################################################//
// Methode de MAJ de l'heure et de la date Horloge POV              //
//																	//
//																	//
//##################################################################//
	public void MAJDateHeure()
	{
   	 	boolean Res = false;
   	 	
   	 	maTrame = new byte[] {'H',0,0,0,0,0,0,0,0,0,0,0,'\n'};
		//------ mise a jour de l'heure ------//
		maTrame[1] = 2;  
		Res = VerifDate(ValTxtDate.getText().toString(), ValTxtHeure.getText().toString(), maTrame);
		maTrame[7] = 0;
		if (ChkAffHeureNum.isChecked() == true)
			maTrame[7] = (byte) (maTrame[7] | 0x02);
		if (ChkAffHeureAna.isChecked() == true)
			maTrame[7] = (byte) (maTrame[7] | 0x04);
		if (ChkAffDate.isChecked() == true)
			maTrame[7] = (byte) (maTrame[7] | 0x01);
		CalcChk (maTrame, NB_DATA_EMI); //Calcul du checksum de la trame
	
		//Si la vérification de date est correcte
		if (Res == true) 
		{
			EmissionDesMAJ("Emission demande de mise à jour de l'heure.");
		}
		else  //Erreur de format Heure / Date
		{
			UpdateTxtLogview(true,"Mise a jour impossible ",Color.RED);
			UpdateTxtClient("Mise a jour impossible \n",Color.BLACK,Color.GREEN);
			//-- fermeture du clavier --//
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(ValTxtHeure.getWindowToken(), 0);	
	    }
	}
		
//##################################################################//
// Methode de MAJ Mode des secondes Horloge POV    					//
//																	//
//																	//
//##################################################################//
	public void MajModeSec()
	{
		//------ mise a jour Mode des secondes ------//
		maTrame[1] = 1;  
		maTrame[2] = (byte) ModeSec;
		maTrame[3] = (byte) NbLedSec;
		CalcChk (maTrame, NB_DATA_EMI);
		EmissionDesMAJ("Emission demande de mise à jour du mode des secondes.");
	}
		
//##################################################################//
// Methode de MAJ Mode anneaux exterieurs Horloge POV    			//
//																	//
//																	//
//##################################################################//
	public void MajModeAnneau()
	{
		byte DecodeMotif[] = {1,2,4,5,10,20};
			//------ mise a jour Mode anneaux exterieurs ------//
		maTrame[1] = 12;
		maTrame[2] = (byte) ModeAnn;
		if (ModeAnn == 0)
			maTrame[3] = DecodeMotif[NumMotif];
		else
			maTrame[3] = (byte) VitDegrade;
		maTrame[4] = 0;
		if (ChkRougeAnneau.isChecked() && !(ChkVertAnneau.isChecked()))
			maTrame[4] = 0;
		if (!(ChkRougeAnneau.isChecked()) && ChkVertAnneau.isChecked())
			maTrame[4] = 2;
		if (ChkRougeAnneau.isChecked() && ChkVertAnneau.isChecked())
			maTrame[4] = 1;
		CalcChk (maTrame, NB_DATA_EMI);
		EmissionDesMAJ("Emission demande de mise à jour des anneaux extérieurs.");
    }
		
//##################################################################//    
// Methode pour afficher une date									//
//					 												//
// PE: N/A															//
// PS: dateString  : valeur correspondante de 0 à 255				//																		
//##################################################################//  
	@SuppressLint("SimpleDateFormat") String affiche_date()
	{
		GregorianCalendar gc = new GregorianCalendar();
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		String dateString = dateFormat1.format(gc.getTime());
		return dateString;
	}

//##################################################################//    
// Methode pour convertir une chaine de caractere en point			//
//									 								//
// PE:  ImagePoint[] : Image d'un message en point					//
//			    Trame[]  : trame a mettre a jour					//
//		      Numero   : numero de la portion d'image (8 colonnes)	//
// PS: N/A															//																		
//##################################################################//  
	void ConvCharPoint(String Message, boolean Sens, boolean Double, byte[] ImagePoint) 
	{	
	
		byte[] MessageATrt = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		int i,j,k;
		
		//-- on travaille sur une chaine de 30 caracteres --//
		while (Message.length() != MAX_CARAC_MSG ) {
			Message = Message + ' ';
		}
		//-- recalage des caracteres par rapport a la disposition horloge --//
		if (Sens == false) {
			//-- horaire --//
			for (i=0 ; i != 15 ; i++) {
				MessageATrt[i] = (byte) Message.charAt(i+15);
			}
			for (i=0 ; i != 15 ; i++) {
				MessageATrt[i+15] = (byte) Message.charAt(i);
			}
		}
		else {
			//-- anti horaire --//
			for (i=0 ; i != 15 ; i++) {
				MessageATrt[MAX_CARAC_MSG -1 -i] = (byte) Message.charAt(i+15);
			}
			for (i=0 ; i != 15 ; i++) {
				MessageATrt[14-i] = (byte) Message.charAt(i);
			}			
		}
		//-- construction tableau de point --//
		if (Double == false) {
			for (i=0 ; i != MAX_CARAC_MSG ; i++ ) {
				for (j=0 ; j != NB_POINT_CARAC ; j++) {
					if (Sens == true) {
						ImagePoint[(i * NB_POINT_CARAC)+j] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) + NB_POINT_CARAC-1-j];
					}
					else {
						ImagePoint[(i * NB_POINT_CARAC)+j] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) + j];
						ImagePoint[(i * NB_POINT_CARAC)+j] = GesMiroir(ImagePoint[(i* NB_POINT_CARAC)+j]);
					}
				}
			}
		}
		else {
			for (i=0 ; i != MAX_CARAC_MSG/2 ; i++ ) {
				k = 0;
				for (j=0 ; j != NB_POINT_CARAC ; j++) {
					if (Sens == true) {
						ImagePoint[(i*2* NB_POINT_CARAC)+k] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) + NB_POINT_CARAC - j -1];
						ImagePoint[(i*2* NB_POINT_CARAC)+k+1] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) + NB_POINT_CARAC - j - 1];
					}
					else {
						ImagePoint[(i*2* NB_POINT_CARAC)+k] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) +  j];
						ImagePoint[(i*2* NB_POINT_CARAC)+k] = GesMiroir(ImagePoint[(i*2* NB_POINT_CARAC)+k]);
						ImagePoint[(i*2* NB_POINT_CARAC)+k+1] = (byte) CGRom[((MessageATrt[i] -0x20) * NB_POINT_CARAC) + j];
						ImagePoint[(i*2* NB_POINT_CARAC)+k+1] = GesMiroir(ImagePoint[(i*2* NB_POINT_CARAC)+k+1]);
					}
					k = k+2;
				}
			}			
		}
			
	}

//##################################################################//    
// Methode pour effectuer la mise à jour du mode de clignottement   //
// et de rotation sur le dernier message du tableau de point à      //
// envoyer															//
//										 							//
// PE:  DebCli: debut clignottement [0..29]							//
//			FinCli: Fin clignottement [0..29]						//
//	      VitCli: Vitesse clignottement en % [0..100] -> 0 à 5s     //
//	  	VitRot: Vitesse rotation en % [0..100] -> 0 à 5s       		//
//			SensRot : rotation sens horaire [true, false]			//
//			MsgCliVit[4] : commande clignottement et vitesse		//
//	      SensClk : Sens affichage message [true, false]			//
//																	//
// PS: N/A															//																		
//##################################################################//  
	void MajCliRot(int DebCli, int FinCli, int VitCli, int VitRot, boolean SensRot, byte[] MsgCliVit, boolean SensClk) 
	{	
	 int PositionD, PositionF;
		
		//-- calcul debut clignottement en nb point avec racadrage sens --//
	  	if (SensClk == false) {
	  		DebCli = MAX_CARAC_MSG - 1- DebCli;
	  		FinCli = MAX_CARAC_MSG - 1- FinCli;
	  		
	  		if (DebCli < MAX_CARAC_MSG/2) 
	  			PositionD = (MAX_CARAC_MSG/2 - 1 - DebCli) * NB_POINT_CARAC;
	  		else
	  			PositionD = (MAX_CARAC_MSG + MAX_CARAC_MSG/2 - 1  - DebCli) * NB_POINT_CARAC;
	  		if (FinCli < MAX_CARAC_MSG/2) 
	  			PositionF = (MAX_CARAC_MSG/2  - FinCli) * NB_POINT_CARAC - 1;
	  		else
	  			PositionF = (MAX_CARAC_MSG + MAX_CARAC_MSG/2 - FinCli) * NB_POINT_CARAC - 1;  /////
	  	}
	  	else
	  	{
	  		if (DebCli < MAX_CARAC_MSG/2) 
	  			PositionD = (MAX_CARAC_MSG/2 - DebCli) * NB_POINT_CARAC;
	  		else
	  			PositionD = (MAX_CARAC_MSG + MAX_CARAC_MSG/2  - DebCli) * NB_POINT_CARAC;
	  		if (FinCli < MAX_CARAC_MSG/2) 
	  			PositionF = (MAX_CARAC_MSG/2 - 1 - FinCli) * NB_POINT_CARAC;
	  		else
	  			PositionF = (MAX_CARAC_MSG + MAX_CARAC_MSG/2 - 1 - FinCli) * NB_POINT_CARAC;
	  	}
		//-- mise a jour dans le tableau, 
		if (SensClk == false) {
			MsgCliVit[0] = (byte)PositionD;			//inversion debut fin du au sens de parcour des led 
			MsgCliVit[1] = (byte)PositionF;	
		}
		else {
			MsgCliVit[0] = (byte)PositionF;			//inversion debut fin du au sens de parcour des led 
			MsgCliVit[1] = (byte)PositionD;
		}
	  
		MsgCliVit[2] = (byte)VitCli;
		MsgCliVit[3] = (byte)VitRot;
		if (SensRot == true)
			MsgCliVit[3] = (byte) (MsgCliVit[3] | 0x80);   
	}
		
//##################################################################//    
// Methode pour inverser en mode mirroir une colonne				//
//									 								//
// PE:  Colonne: Image d'une colonne								//
// PS:  ColonneInv : Image inverser									//																		
//##################################################################//  
	byte GesMiroir(byte Colonne) 
	{	
		byte ColonneInv = 0;
		
		if ((Colonne & 0x80) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x01);
		}
		if ((Colonne & 0x40) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x02);
		}
		if ((Colonne & 0x20) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x04);
		}
		if ((Colonne & 0x10) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x08);
		}
		if ((Colonne & 0x08) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x10);
		}
		if ((Colonne & 0x04) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x20);
		}
		if ((Colonne & 0x02) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x40);
		}
		if ((Colonne & 0x01) != 0) {
			ColonneInv = (byte) (ColonneInv | 0x80);
		}
		return ColonneInv;
	}	
		
//##################################################################//    
// Methode pour construire la trame pour la memorisation des pages  //
//								 									//
// PE:  ImagePoint[] : Image d'un message en point					//
//		    Trame[]  : trame a mettre a jour						//
//	      Numero   : numero de la portion d'image (8 colonnes)		//
// PS: N/A															//																		
//##################################################################//  
	void GesMemoPage(byte[] ImagePoint, byte[] Trame, int Numero, byte[] MsgCliVit) 
	{	
		int i;
		
		if (Numero != 22) {
			for (i=0 ; i != 8 ; i++) {
				Trame[i+3] = ImagePoint[(Numero * 8)+i];
			}
		}
		else {
			for (i=0 ; i != 4 ; i++) {
				Trame[i+3] = ImagePoint[(Numero * 8)+i];
			}
			for (i=0 ; i != 4 ; i++) {
				Trame[i+7] = MsgCliVit[i];   //-- maj mode clignottement et rotation
				ImagePoint[(Numero * 8)+i+4] = MsgCliVit[i];
			}
		}
	}
		
//##################################################################//    
// Methode pour verifier la valeur de la page eeprom				//
//									 								//
// PE:  Valeur : chaine correspondant aune valeur					//
//			ValPage: valeur numerique de la page					//
// PS:  VerifOK: resultat verification								//																		
//##################################################################//  
	boolean VerifPage (String Valeur) 
	{	
		boolean VerifOK = false;
		int Val;

		if (Valeur.length() != 0) {
			Val = Integer.valueOf(Valeur);
			if ((Val >= 0) && (Val <= MAX_PAGE_EEP)) {
				VerifOK = true;
			}
		}
		
		return VerifOK;
	}
			
//##################################################################//    
// Methode pour construire la trame pour l'affichage 6 caracteres   //
//							 										//
// PE:  Message : Message a afficher (30 caractere maxi)     		//
//	      Sens    : sens d'affichage (clkw, cclkw)					//
//	      Trame[]  : trame a mettre a jour							//
// PS: N/A															//																		
//##################################################################//  
	void GesTrame6Carac(String Message, boolean Sens, byte[] Trame) {
	
	    int i = 0;
	    int j = 0;
	    byte[] TabCarac;
	    
		//-- calcul position d'affichage --
		//(on cherche le 1er caractere <> d'espace)
	    TabCarac = Message.getBytes();
		while (TabCarac[i] == ' ') {
			i++;
		}
		if (i >= 15)
			Trame[2] = (byte)((byte) 6*(i-15));
		else
			Trame[2] = (byte)((byte) 6*(i+15));
		
		//-- affectation tableau avec les 6 premier caractere suivant --//
//			if (Sens != false) {
			//-- sens horaire --//
			while ((j != 6) && (i+j != Message.length())) {
				Trame[j+4] = TabCarac[i+j];
				j++;
			}
			Trame[j+4] = 0;
/*		}
		else {
			//-- sens anti horaire --//
			//-- on commence par la fin --//
			if (Message.length() >= 6) {
				//-- au moins 6 caractere dans la chaine --//
				while (j != 6)  {
					Trame[j+4] = TabCarac[i+5-j];
					j++;
				}
			}
			else {
				//-- moins de 6 caracteres dans la chaine --//
				while (j != Message.length())  {
					Trame[j+4] = TabCarac[i+Message.length()-1-j];
					j++;
				}				
			}
			Trame[j+4] = 0;		
		} */
	}
		

//##################################################################//    
// Methode pour verifier l'integrité d'une date et heure			//
//					 												//
// PE: StrDate : Date en chaine de caractere format dd/mm/aa		//
//	     StrHeure : Date en chaine de caractere format hh:mm:ss		//	
//		   Trame[]  : trame a mettre a jour							//
// PS: Res : flag de respect du format [true, false]				//																		
//##################################################################//  
	boolean VerifDate(String StrDate, String StrHeure, byte[] Trame) {
			boolean Res = true;
			int Val;
			String[] ChDate = StrDate.split("/");
			String[] ChHeure = StrHeure.split(":");
			
	       try 
	       {
	    	   //---- verification coherence date -----//
	    	   if (ChDate[0].length() == 0) 
	    	   {
	    		   UpdateTxtClient("Erreur: mauvais format de date (utiliser le séparateur / )\n",Color.BLACK,Color.RED);
	    		   Res = false;
	    	   }
	    	   else if (ChDate[1].length() == 0) 
	    	   {
	    		   UpdateTxtClient("Erreur: mauvais format de date (utiliser le séparateur / )\n",Color.BLACK,Color.RED);
	    		   Res = false;
	    	   }
	    	   else if (ChDate[2].length() == 0) 
	    	   {
	    		   UpdateTxtClient("Erreur: mauvais format de date (utiliser le séparateur / )\n",Color.BLACK,Color.RED);
	    		   Res = false;
	    	   }
	    	   else
	    	   {
	    		   Val = Integer.valueOf(ChDate[0]);
	    		   if ((Val <= 0) || (Val >31)) 
	    		   {
	    			   Res = false;
	    			   UpdateTxtClient("Erreur: date hors domaine \n",Color.BLACK,Color.RED);
	    		   }
	    		   else
	    			   Trame[4] = (byte) Val;
	    		   Val = Integer.valueOf(ChDate[1]);
	    		   if ((Val <= 0) || (Val >12)) 
	    		   {
	    			   Res = false;
	    			   UpdateTxtClient("Erreur: mois hors domaine \n",Color.BLACK,Color.RED);
	    		   }
	    		   else
	    			   Trame[5] = (byte) Val;
	    		   Val = Integer.valueOf(ChDate[2]);
	    		   Trame[6] = (byte) (Val % 100);
	    	   }
	       }
	       catch (Exception e) 
	       {
	    	   e.printStackTrace();
			   UpdateTxtClient("Erreur: mauvais format de date (utiliser le séparateur / )\n",Color.BLACK,Color.RED);
			   Res = false;
	       }
		   
		   //---- verification coherence heure -----//
	       try 
	       {
	    	   if (ChHeure[0].length() == 0) 
	    	   {
	    		   UpdateTxtClient("Erreur: mauvais format d'heure (utiliser le séparateur : )\n",Color.BLACK,Color.RED);
	    	   }
	    	   else if (ChHeure[1].length() == 0) 
	    	   {
	    		   UpdateTxtClient("Erreur: mauvais format d'heure (utiliser le séparateur : )\n",Color.BLACK,Color.RED);
	    	   }		
	    	   else 
	    	   {
	   	   		   Val = Integer.valueOf(ChHeure[0]);
	   	   		   if ((Val < 0) || (Val >23)) 
	   	   		   {
	   	   			   Res = false;
	   	   			   UpdateTxtClient("Erreur: heure hors domaine \n",Color.BLACK,Color.RED);
	   	   		   }
	   	   		   else
	   	   			   Trame[3] = (byte) Val;
	   	   		   Val = Integer.valueOf(ChHeure[1]);
	   	   		   if ((Val < 0) || (Val >59)) 
	   	   		   {
	   	   			   Res = false;
	   	   			   UpdateTxtClient("Erreur: minute hors domaine \n",Color.BLACK,Color.RED);
	   	   		   }
	   	   		   else
	   	   			   Trame[2] = (byte) Val;
			   }
		   }	
	       catch (Exception e) 
	       {
	    	   e.printStackTrace();
			   UpdateTxtClient("Erreur: mauvais format d'heure (utiliser le séparateur : )\n",Color.BLACK,Color.RED);
			   Res = false;
	       }
	       return Res;
		}
					
//##################################################################//    
// Methode pour transformer un Octet (forcement signé) en entier non //
// signé											 				//
//				 													//
// PE: Octet : valeur de -128 à 127									//
// PS: Res   : valeur correspondante de 0 à 255						//																		
//##################################################################//  
	int ByteNSToInt(byte Octet) {
		int Res = 0;
    
		if (Octet >= 0) Res = Octet;
     	else Res = Octet + 256;
     	
		return Res;
	}
		
//##################################################################//    
// Methode pour calculé le ckecksum (0 à n-2) d'un tableau de n     //
// octet. Le resultat est placé sur le dernier octet (n-1)			//
//						 											//
// PE: cmd     : tableau de n Octet									//
//		     NbOctet : nombre d'octet du tableau					//
//		 															//																		//
//##################################################################//  	
	void CalcChk (byte[] cmd, int NbOctet) {
		int i;
		byte Sum = 0;
		
		for (i=0 ; i != NbOctet-1 ; i++) {
			Sum = (byte) (Sum + cmd[i]);
		}
		cmd[i] = Sum;
	}

//##################################################################//    
// Methode pour calculé le ckecksum barre (0 à n-2) d'un tableau de //
// n octet. Le resultat est placé sur le dernier octet (n-1)		//
//							 										//
// PE: cmd     : tableau de n Octet									//
//			   NbOctet : nombre d'octet du tableau					//
//			 														//																		//
//##################################################################//  	
		void CalcChkB (byte[] cmd, byte NbOctet) {
			byte i;
			byte Sum = 0;
			
			for (i=0 ; i != NbOctet-1 ; i++) {
				Sum = (byte) (Sum + cmd[i]);
			}
			cmd[i] = (byte) ~Sum;
		}	
	
	
//##################################################################//    
// Methode pour verifier le ckecksum (0 à n-2) d'un tableau de n    //
// octet. La valeur a comparé est sur le dernier octet (n-1)		//
//							 										//
// PE: Trame   : tableau de n Octet									//
//			   NbOctet : nombre d'octet du tableau					//
// PS: Res     : resultat verification [true, false]				//
//			 														//																		//
//##################################################################//  	
	boolean VerifChk (byte[] Trame, int NbOctet) {
		boolean Res = false;
		byte Sum = 0;
		int i;
		
		for (i=0 ; i != NbOctet-1 ; i++) {
			Sum = (byte) (Sum + Trame[i]);
		}
		if (Trame[i] == Sum) {
			Res = true;
		}
		return Res;
	}

//##################################################################//    
// Methode pour verifier le ckecksum barre (0 à n-2) d'un tableau de//
// n octet. La valeur a comparé est sur le dernier octet (n-1)		//
//								 									//
// PE: Trame   : tableau de n Octet									//
//				   NbOctet : nombre d'octet du tableau				//
// PS: Res     : resultat verification [true, false]				//
//				 													//																		//
//##################################################################//  	
	boolean VerifChkB (byte[] Trame, int NbOctet) {
		boolean Res = false;
		byte Sum = 0;
		int i;
		
		for (i=0 ; i != NbOctet-1 ; i++) {
			Sum = (byte) (Sum + Trame[i]);
		}
		if (Trame[i] == ~Sum) {
			Res = true;
		}
		return Res;
	}	

//##################################################################//    
// Methode non utilisées mais nécessaire à la compilation           //
//																					//
//##################################################################//	
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}