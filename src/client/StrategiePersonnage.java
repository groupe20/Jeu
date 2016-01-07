package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.Arene;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.interaction.Deplacement;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Strategie d'un personnage. 
 */
public class StrategiePersonnage {
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	
	protected StrategiePersonnage(LoggerProjet logger){
		logger.info("Lanceur", "Creation de la console...");
	}
	
	protected static HashMap<Integer,HashMap<Caracteristique,Integer>> listeadv =null ;
	
	protected int cpt = 0 ; //compteur de tour avant réinitialisation de listeadv
	protected boolean tourun = true ;

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public StrategiePersonnage(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Personnage(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
	}

	// une proposition de strategie (simple) est donnee ci-dessous
	/** 
	 * Decrit la strategie.
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException 
	{
		// arene
		IArene arene = console.getArene();
		// reference RMI de l'element courant
		int refRMI = 0;
		// position de l'element courant
		Point position = null;
		
		
		try 
		{
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} 
		catch (RemoteException e) 
		{
			e.printStackTrace();
		}
		
		cpt-- ;
		
		if (cpt <=0 && listeadv!=null){
			listeadv.clear();
			cpt=7; //On réinitialise tout les 7 tours pour effectuer une maj des stats au cas ou
		}
		
		int refMonstre = 0 ;
		int distMonstre = 0 ;
		int refPot = 0 ;
		int distPot = 0 ;
		int refAdv = 0 ;
		int distAdv = 0 ;
		if (tourun)
		{
			console.setPhrase("j'erre tour un");
			tourun = false ;
            arene.deplace(refRMI, 0);
		}
		else
		{
			if (voisins.isEmpty()) 
			{ // je n'ai pas de voisins
				if (arene.caractFromRef(refRMI, Caracteristique.VIE) != 100)
				{
					//je me soigne s'il me manque des pv
					console.setPhrase("Je me soigne");
					arene.lanceAutoSoin(refRMI);
				}
				else
				{	//j'erre
					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0);
				}
			}
			else 
			{
				refMonstre = monstreProche(position, voisins, arene) ;
				if (refMonstre != 0)
				{
					distMonstre = Calculs.distanceChebyshev(position, arene.getPosition(refMonstre));
				}
				
				refPot = chercheBestPotion(position, voisins, arene, refRMI) ;
	
				if (refPot != 0)
				{
					distPot = Calculs.distanceChebyshev(position, arene.getPosition(refPot));
				}
				
				
					
					if (nbAdv(voisins,arene) > 0)
					{
						//ajoutAdvDansListe(voisins,arene) ;
						
						refAdv = advPlusProche(position,voisins,arene) ;
						if (refAdv != 0)
						{
							distAdv = Calculs.distanceChebyshev(position, arene.getPosition(refAdv));
						}
						int distPotAdv = 0 ;
						if (refPot != 0 && refAdv != 0)
						{
							distPotAdv = Calculs.distanceChebyshev(arene.getPosition(refAdv), arene.getPosition(refPot)) ;
						}
						if (refPot != 0 && distPot < distPotAdv && distAdv != 0)
						{
							if(distPot <= Constantes.DISTANCE_MIN_INTERACTION)
							{ // si suffisamment proche
								// ramassage
								console.setPhrase("Je ramasse une potion");
								arene.ramassePotion(refRMI, refPot);
							}
							else if (distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && distPot > Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
							{	//si un monstre vient m'attaquer, je l'attaque
								console.setPhrase("Je me bats contre un monstre");
								arene.lanceAttaque(refRMI, refMonstre);
							}
							else
							{ // si voisins, mais plus eloignes
								// je vais vers le plus proche
								console.setPhrase("Je vais vers une potion " + arene.nomFromRef(refPot));
								arene.deplace(refRMI, refPot);
							}
						}
						else
						{
							if (listeadv == null){
								listeadv = new HashMap<Integer,HashMap<Caracteristique,Integer>>();
							}
							
							if (refAdv != 0 && !listeadv.containsKey(refAdv))
							{
								listeadv.put(refAdv,arene.lanceClairvoyance(refRMI, refAdv)) ;
	
							}
							else if (refAdv != 0 && listeadv.containsKey(refAdv))
							{
								if (doitAttaquer(refRMI, refAdv, arene))
								{
	
									if(distAdv <= Constantes.DISTANCE_MIN_INTERACTION)
									{ // si suffisamment proche, j'attaque
										console.setPhrase("Je fais un duel avec " + arene.nomFromRef(refAdv));
										arene.lanceAttaque(refRMI, refAdv);
									}
									else
									{ //sinon je vais vers lui pour l'attaquer
										console.setPhrase("Je vais vers mon ennemi " + arene.nomFromRef(refAdv));
										arene.deplace(refRMI, refAdv);
									}
								}
								else
								{
									if(distAdv <= 10)
									{
										if(distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
										{
											 // si suffisamment proche
												// j'attaque le monstre
												console.setPhrase("Je me bats contre un monstre");
												arene.lanceAttaque(refRMI, refMonstre);
										}
										else if (distAdv <= Constantes.DISTANCE_MIN_INTERACTION)
										{
											 // si suffisamment proche
												// je me défend
												console.setPhrase("Je me défends contre " + arene.nomFromRef(refAdv));
												arene.lanceAttaque(refRMI, refAdv);
										}
										else
										{ 
												arene.deplace(refRMI, seloignerDe(refRMI,refAdv,arene));
												console.setPhrase("Je me casse de là a cause de " + refAdv);
										}
										
										
									}
									else
									{
										if (refPot != 0)
										{
											if(distPot <= Constantes.DISTANCE_MIN_INTERACTION)
											{ // si suffisamment proche
												// ramassage
												console.setPhrase("Je ramasse une potion");
												arene.ramassePotion(refRMI, refPot);
											}
											else if (distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && distPot > Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
											{	//si un monstre vient m'attaquer, je l'attaque
												console.setPhrase("Je me bats contre un monstre");
												arene.lanceAttaque(refRMI, refMonstre);
											}
											else
											{ // si voisins, mais plus eloignes
												// je vais vers le plus proche
												console.setPhrase("Je vais vers une potion " + arene.nomFromRef(refPot));
												arene.deplace(refRMI, refPot);
											}
										}
										else
										{
											if (refMonstre != 0)
											{
												if(distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
												{ // si suffisamment proche
													// j'attaque le monstre
													console.setPhrase("Je me bats contre un monstre");
													arene.lanceAttaque(refRMI, refMonstre);
												}
												else
												{ // si voisins, mais plus eloignes
													// je vais vers le plus proche
													console.setPhrase("Je vais vers un monstre " + arene.nomFromRef(refMonstre));
													arene.deplace(refRMI, refMonstre);
												}
											}
											else
											{
												if (arene.caractFromRef(refRMI, Caracteristique.VIE) != 100)
												{
													//je me soigne s'il me manque des pv
													console.setPhrase("Je me soigne");
													arene.lanceAutoSoin(refRMI);
												}
												else
												{	//j'erre
													console.setPhrase("J'erre...");
													arene.deplace(refRMI, 0);
												}
											}
										}
									}
								}
							}
							else
							{
								if (refPot != 0)
								{
									if(distPot <= Constantes.DISTANCE_MIN_INTERACTION)
									{ // si suffisamment proche
										// ramassage
										console.setPhrase("Je ramasse une potion");
										arene.ramassePotion(refRMI, refPot);
									}
									else if (distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && distPot > Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
									{	//si un monstre vient m'attaquer, je l'attaque
										console.setPhrase("Je me bats contre un monstre");
										arene.lanceAttaque(refRMI, refMonstre);
									}
									else
									{ // si voisins, mais plus eloignes
										// je vais vers le plus proche
										console.setPhrase("Je vais vers une potion " + arene.nomFromRef(refPot));
										arene.deplace(refRMI, refPot);
									}
								}
								else
								{
									if (refMonstre != 0)
									{
										if(distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
										{ // si suffisamment proche
											// j'attaque le monstre
											console.setPhrase("Je me bats contre un monstre");
											arene.lanceAttaque(refRMI, refMonstre);
										}
										else
										{ // si voisins, mais plus eloignes
											// je vais vers le plus proche
											console.setPhrase("Je vais vers un monstre " + arene.nomFromRef(refMonstre));
											arene.deplace(refRMI, refMonstre);
										}
									}
									else
									{	//si il n'y a que des potions ininteressantes
										if (arene.caractFromRef(refRMI, Caracteristique.VIE) != 100)
										{
											//je me soigne s'il me manque des pv
											console.setPhrase("Je me soigne");
											arene.lanceAutoSoin(refRMI);
										}
										else
										{	//j'erre
											console.setPhrase("J'erre...");
											arene.deplace(refRMI, 0);
										}
									}
								}
							}
						}
					}
					else
					{
						if (refPot != 0)
						{
							if(distPot <= Constantes.DISTANCE_MIN_INTERACTION)
							{ // si suffisamment proche
								// ramassage
								console.setPhrase("Je ramasse une potion");
								arene.ramassePotion(refRMI, refPot);
							}
							else if (distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && distPot > Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
							{	//si un monstre vient m'attaquer, je l'attaque
								console.setPhrase("Je me bats contre un monstre");
								arene.lanceAttaque(refRMI, refMonstre);
							}
							else
							{ // si voisins, mais plus eloignes
								// je vais vers le plus proche
								console.setPhrase("Je vais vers une potion " + arene.nomFromRef(refPot));
								arene.deplace(refRMI, refPot);
							}
						}
						else
						{
							if (refMonstre != 0)
							{
								if(distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && refMonstre != 0)
								{ // si suffisamment proche
									// j'attaque le monstre
									console.setPhrase("Je me bats contre un monstre");
									arene.lanceAttaque(refRMI, refMonstre);
								}
								else
								{ // si voisins, mais plus eloignes
									// je vais vers le plus proche
									console.setPhrase("Je vais vers un monstre " + arene.nomFromRef(refMonstre));
									arene.deplace(refRMI, refMonstre);
								}
							}
							else
							{	//si il n'y a que des potions ininteressantes
								if (arene.caractFromRef(refRMI, Caracteristique.VIE) != 100)
								{
									//je me soigne s'il me manque des pv
									console.setPhrase("Je me soigne");
									arene.lanceAutoSoin(refRMI);
								}
								else
								{	//j'erre
									console.setPhrase("J'erre...");
									arene.deplace(refRMI, 0);
								}
							}
						}
					}
				
				
				
				
				
			}
		}
	}
	
	public static int chercheBestPotion(Point origine, HashMap<Integer, Point> voisins, IArene arene, int refPerso) throws RemoteException {
		//int distPlusProche = VISION;
		int refBestPot = 0;
		int somStat = 0 ;
		boolean sitcrit = false ;
		/*boolean tier4 = false ;
		boolean tier3 = false ;
		boolean tier2 = false ;
		boolean tier1 = false ;*/
		int maxVie = -20 ;
		int maxFor = 0 ;
		int maxInit = 0 ;
		int maxDef = 0 ;
		int minVieAcc = -10 ;
		int minForAcc = -10 ;
		int minInitAcc = -10 ;
		int minDefAcc = -10 ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			if (arene.estPotionFromRef(refVoisin))
			{

				int viePot = arene.caractFromRef(refVoisin, Caracteristique.VIE) ;
				int forPot = arene.caractFromRef(refVoisin,Caracteristique.FORCE) ;
				int initPot = arene.caractFromRef(refVoisin,Caracteristique.INITIATIVE) ;
				int defPot = arene.caractFromRef(refVoisin,Caracteristique.DEFENSE) ;

				if (viePot == 0 && forPot == 0 && initPot == 0 && defPot == 0)
				{//si potion TP
					if (nbAdv(voisins, arene) >= 2)
					{
						refBestPot = refVoisin ;
						sitcrit = true ;
					}
				}
				else if ((viePot > 0 || forPot > 0 || initPot > 0 || defPot > 0) && !sitcrit)
				{//potion non TP
					/*Point target = voisins.get(refVoisin);
					
					if (Calculs.distanceChebyshev(origine, target) <= distPlusProche) {
						distPlusProche = Calculs.distanceChebyshev(origine, target);
						refPlusProche = refVoisin;
					}*/
					
					

					
					int viePerso = arene.caractFromRef(refPerso,Caracteristique.VIE) ;
					int forPerso = arene.caractFromRef(refPerso,Caracteristique.FORCE) ;
					int initPerso = arene.caractFromRef(refPerso,Caracteristique.INITIATIVE) ;
					int defPerso = arene.caractFromRef(refPerso,Caracteristique.DEFENSE) ;
					
					// cas critique : 
					//if (viePerso < 10 && viePot >= 10)
					if ((viePot >= -10 && viePot >= maxVie && viePerso > 20) 
							|| (viePot >= 0 && viePot >= maxVie && viePerso > 0) 
							|| (viePot < -10 && viePot >= maxVie && viePerso > 40 && initPerso > 80))
					{
						if (initPot >= maxInit)
						{
							if (defPot >= maxDef)
							{
								if (forPot > -10 && forPot >= maxFor)
								{
									refBestPot = refVoisin ;
									//tier1 = true ;
									maxVie = viePot ;
									maxInit = initPot ;
									maxDef = defPot ;
									maxFor = forPot ;
									
								}
							}
							else if (defPot < maxDef)
							{
								if (initPot >= 10 && forPot >= -10)
								{
										refBestPot = refVoisin ;
										//tier2 = true ;
										maxVie = viePot ;
										maxInit = initPot ;
										maxDef = defPot ;
										maxFor = forPot ;
								}
							}
						}
						else if (initPot < maxInit && initPot > -5)
						{
							if (defPot >= maxDef + 5)
							{
								//CHANGEMENT IMPORTANT
								if (forPot > -10 && forPot >= maxFor)
								{
									refBestPot = refVoisin ;
									//tier3 = true ;
									maxVie = viePot ;
									maxInit = initPot ;
									maxDef = defPot ;
									maxFor = forPot ;
									
								}
							}
							else if (defPot < maxDef)
							{
								if (forPot > 10 && forPot >= maxFor)
								{
									refBestPot = refVoisin ;
									//tier4 = true ;
									maxVie = viePot ;
									maxInit = initPot ;
									maxDef = defPot ;
									maxFor = forPot ;
								}
							}
						}
					}
					
					
				}
			}
		}
		
		return refBestPot;
	}
	
	public static boolean estPotionTP (int refPotion, IArene arene) throws RemoteException
	{
		int viePot = arene.caractFromRef(refPotion, Caracteristique.VIE) ;
		int forPot = arene.caractFromRef(refPotion,Caracteristique.FORCE) ;
		int initPot = arene.caractFromRef(refPotion,Caracteristique.INITIATIVE) ;
		int defPot = arene.caractFromRef(refPotion,Caracteristique.DEFENSE) ;
		
		if (viePot == 0 && forPot == 0 && initPot == 0 && defPot == 0)
		{
			return true ;
		}
		
		return false ;
	}
	
	public static int nbAdv (HashMap<Integer, Point> voisins, IArene arene) throws RemoteException
	{
		int nbEnnemis = 0 ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			if (arene.estPersonnageFromRef(refVoisin) && !arene.nomFromRef(refVoisin).equals("Monstre"))
			{
				nbEnnemis++ ;
			}
		
		}
		
		return nbEnnemis ;

	
	}
	
	public static int monstreProche (Point origine, HashMap<Integer, Point> voisins, IArene arene)throws RemoteException
	{
		

		int refMonstre = 0 ;
		int distMonstreProche = Constantes.VISION ;
		
		for(int refVoisin : voisins.keySet())
		{
			if(arene.estMonstreFromRef(refVoisin))
			{
				Point target = voisins.get(refVoisin);
				int dist = Calculs.distanceChebyshev(origine, target) ;
				
				if (dist <= distMonstreProche)
				{
					distMonstreProche = dist;
					refMonstre = refVoisin;
				}
			}
		}
		
		return refMonstre ;
	}
	
	public static int advPlusProche (Point origine, HashMap<Integer, Point> voisins, IArene arene) throws RemoteException
	{
		int refAdversaire = 0 ;
		int distPlusProche = Constantes.VISION ;
		
		for(int refVoisin : voisins.keySet())
		{
			if (arene.estPersonnageFromRef(refVoisin) && !arene.nomFromRef(refVoisin).equals("Monstre"))
			{
				Point target = voisins.get(refVoisin);
				int dist = Calculs.distanceChebyshev(origine, target) ;
				
				if (dist <= 15 && dist <= distPlusProche)
				{
					distPlusProche = dist ;
					refAdversaire = refVoisin ;
				}
			}
		}
		
		return refAdversaire ;
	}
	
	public static void ajoutAdvDansListe (HashMap<Integer, Point> voisins, IArene arene) throws RemoteException
	{
		for(int refVoisin : voisins.keySet())
		{
			if (arene.estPersonnageFromRef(refVoisin))
			{
				if (!listeadv.containsKey(refVoisin))
				{
					listeadv.put(refVoisin, null) ;
				}
			}
		}
		
	}
	
	/**
     * Permet de savoir si le personnage doit attaquer son voisin.
     * Renvoie un boolean
     */
    /**
     * Permet de savoir si le personnage doit attaquer son voisin.
     * Renvoie un boolean
     * @throws RemoteException 
     */
    public boolean doitAttaquer (int refRMI, int refCible, IArene arene) throws RemoteException 
    {

 
        
        // On recupere les caracteristiques du personnages
        int viePers = arene.caractFromRef(refRMI, Caracteristique.VIE) ;
        int forcePers = arene.caractFromRef(refRMI, Caracteristique.FORCE) ;
        int initPers = arene.caractFromRef(refRMI, Caracteristique.INITIATIVE) ;     
        int defPers = arene.caractFromRef(refRMI, Caracteristique.DEFENSE) ;
        // On recupere les caracteristiques du voisin
        int vieVois = listeadv.get(refCible).get(Caracteristique.VIE);
        int forceVois = listeadv.get(refCible).get(Caracteristique.FORCE);
        int initVois = listeadv.get(refCible).get(Caracteristique.INITIATIVE);       
        int defVois = listeadv.get(refCible).get(Caracteristique.DEFENSE);
        
                
        /*// Si le voisin a une meilleur initiative
        if (initPers < initVois){
            
            if (initPers < initVois -10) attaque = false;
            else{
                if((forceVois - (forceVois*defPers/100)) >= viePers) attaque = false;
                else{
                    if((forcePers - (forcePers*defVois/100)) < vieVois) attaque = false;
                }
            }
                
        }
        else { //Si initPers >= initVois
            
            if(initVois >= initPers -10){
                if((forcePers - (forcePers*defVois/100)) < vieVois) attaque = false;
            }
        }*/
        
        if ((forcePers - (forcePers*defVois/100)) > vieVois && initPers > initVois)
        {	//si je one-shot l'adversaire
        	return true ;
        }
        if ((initVois - initPers) > 0 
        		&& (initVois - initPers) < 10 
        		&& viePers > 40 
        		&& (forceVois - (forceVois*defPers/100)) <= 20
        		&& (forcePers - (forcePers*defVois/100)) > vieVois)
        {	//si en subissant un coup, on peut le one-shot derriere
        		return true ;	
        }
        if (initPers > (initVois + 20) && viePers > (1.5*forceVois))
        {	//si je vais attaquer en premier, 
        	return true ;
        }
        
        return false ;
        
    }
    
    public Point seloignerDe (int refRMI,int refObjectif, IArene arene) throws RemoteException 
    {
    	
    	Point pVoisin;
    	Point paway=new Point();
    	boolean calc = false;
    	
    	
    	
    	Point pPerso = arene.getPosition(refRMI);
    	
    	// on ne bouge que si la reference n'est pas la notre
    	if (refObjectif != refRMI) 
    	{
    		// la reference est nulle (en fait, nulle ou negative) :
    		// le personnage erre
    		if (refObjectif <= 0) {
    			paway = Calculs.positionAleatoireArene();
    		}
    		// sinon :
    		// la cible devient le point sur lequel se trouve l'element objectif
    		pVoisin = arene.getPosition(refObjectif);
    		// on ne bouge que si l'element voisin existe
    		if(pVoisin != null) {
    			if ((pPerso.x <= pVoisin.x) && (pPerso.x > Constantes.XMIN_ARENE )){
    				pPerso.x --;
    				calc=true;
    			}
    			if ((pPerso.x > pVoisin.x) && (pPerso.x < Constantes.XMAX_ARENE )){
    				pPerso.x ++;
    				calc=true;
    			}
    			if ((pPerso.y <= pVoisin.y) && (pPerso.y > Constantes.YMIN_ARENE )){
    				pPerso.y --;
    				calc=true;
    			}
    			if ((pPerso.y > pVoisin.y) && (pPerso.y < Constantes.YMAX_ARENE)){
    				pPerso.y ++;
    				calc=true;
    			}
    			
    			
    			
    			if (!calc){
    				paway = Calculs.positionAleatoireArene();
    			}
    			else{
    			paway.x=pPerso.x;
    			paway.y=pPerso.y;
    			}
    			
    			
    		}
    	}
    	
    	return paway ;
    }
    
    




}
