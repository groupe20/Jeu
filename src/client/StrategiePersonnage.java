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
	
	protected int cpt = 0 ;
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
			cpt=10;
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
				System.err.println(refMonstre);
				
				refPot = chercheBestPotion(position, voisins, arene, refRMI) ;
				System.err.println(refPot);
	
				if (refPot != 0)
				{
					distPot = Calculs.distanceChebyshev(position, arene.getPosition(refPot));
				}
				
				
					
					if (nbAdv(voisins,arene) > 0)
					{
						System.err.println("J'ai des adversaires autour de moi");
						//ajoutAdvDansListe(voisins,arene) ;
						
						refAdv = advPlusProche(position,voisins,arene) ;
						System.err.println("après advPlusProche" + refAdv);
						if (refAdv != 0)
						{
							distAdv = Calculs.distanceChebyshev(position, arene.getPosition(refAdv));
						}
						int distPotAdv = 0 ;
						if (refPot != 0)
						{
							distPotAdv = Calculs.distanceChebyshev(arene.getPosition(refAdv), arene.getPosition(refPot)) ;
						}
						System.err.println("après distAdv et distPotAdv");
						if (refPot != 0 && distPot < distPotAdv && distAdv != 0)
						{
							System.err.println("Je suis dans le if (ennemi mais potion a prendre)");
							if(distPot <= Constantes.DISTANCE_MIN_INTERACTION)
							{ // si suffisamment proche
								// ramassage
								console.setPhrase("Je ramasse une potion");
								arene.ramassePotion(refRMI, refPot);
							}
							else if (distMonstre <= Constantes.DISTANCE_MIN_INTERACTION && distPot > Constantes.DISTANCE_MIN_INTERACTION)
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
							System.err.println("Je suis dans le else ennemi");
							if (listeadv == null){
								System.err.println("Je dois pas etre la a chaque tour");
								listeadv = new HashMap<Integer,HashMap<Caracteristique,Integer>>();
							}
							System.err.println(listeadv.containsKey(refAdv)) ;
							
							if (refAdv != 0 && !listeadv.containsKey(refAdv))
							{
								
								listeadv.put(refAdv,arene.lanceClairvoyance(refRMI, refAdv)) ;
								System.err.println("clairvoyance faite");
	
							}
							else if (refAdv != 0 && listeadv.containsKey(refAdv))
							{
								System.err.println("Je suis dans le else j'ai une cible potentielle");
								if (doitAttaquer(refRMI, refAdv, arene))
								{
									System.err.println("On doit attaquer");
	
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
									System.err.println("On doit pas attaquer");
									if(distAdv <= 10)
									{
										System.err.println("On doit fuir");
										arene.deplace(refRMI, seloignerDe(refRMI,refAdv,arene));
										console.setPhrase("Je me casse de là a cause de " + refAdv);
									}
									else
									{
										System.err.println("L'ennemi est assez loin");
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
											System.err.println("Ennemis mais monstre gérable");
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
										}
									}
								}
							}
							else
							{
								if (refPot != 0)
								{
									System.err.println("Je suis dans le if (ya une potion)");
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
									System.out.print("Je suis dans le else (pas de potion)");
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
						System.err.println("Je suis dans le else (pas d'ennemi)");
						if (refPot != 0)
						{
							System.err.println("Je suis dans le if (ya une potion)");
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
							System.out.print("Je suis dans le else (pas de potion)");
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
				System.err.println("Je suis après le if dans BestPot") ;

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
						System.err.println("Je suis après le if vie") ;
						if (initPot >= maxInit)
						{
							System.err.println("Je suis après le if init") ;
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
				
				if (dist <= 15 && dist <= distPlusProche )
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
    public boolean doitAttaquer (int refRMI, int refCible, IArene arene) throws RemoteException {
        boolean attaque = true;
        
        System.err.println("début de doitAttaquer " + attaque) ;
        System.err.println(refCible) ;
        System.err.println(listeadv.get(refCible)) ;
        System.err.println(listeadv.get(refCible).get(Caracteristique.VIE)) ;

        
        
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
        
        System.err.println("vieVois = " + vieVois) ;
        System.err.println("forceVois = " + forceVois) ;
                
        // Si le voisin a une meilleur initiative
        if (initPers < initVois){
            
            if (initPers < initVois -10) attaque = false;
            else{
                if(forceVois - forceVois*defPers/100 >= viePers) attaque = false;
                else{
                    if(forcePers - forcePers*defVois/100 < vieVois) attaque = false;
                }
            }
                
        }
        else { //Si initPers >= initVois
            
            if(initVois >= initPers -10){
                if(forcePers - forcePers*defVois/100 < vieVois) attaque = false;
            }
        }
        
        return attaque ;
        
    }
    
    public Point seloignerDe (int refRMI,int refObjectif, IArene arene) throws RemoteException {
    	Point pVoisin;
    	Point paway=new Point();
    	boolean calc = false;
    	
    	
    	Point pPerso = arene.getPosition(refRMI);
    	// on ne bouge que si la reference n'est pas la notre
    	if (refObjectif != refRMI) {
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
    			if ((pPerso.x <= pVoisin.x) && (pPerso.x >= Constantes.XMIN_ARENE )){
    				pPerso.x --;
    				calc=true;
    			}
    			else if ((pPerso.x >= pVoisin.x) && (pPerso.x <= Constantes.XMAX_ARENE )){
    				pPerso.x ++;
    				calc=true;
    			}
    			if ((pPerso.y <= pVoisin.y) && (pPerso.y >= Constantes.YMIN_ARENE )){
    				pPerso.y --;
    				calc=true;
    			}
    			else if ((pPerso.y >= pVoisin.y) && (pPerso.y <= Constantes.YMAX_ARENE)){
    				pPerso.y ++;
    				calc=true;
    			}
    			if ((pPerso.x == Constantes.XMAX_ARENE) && (pPerso.y == Constantes.YMAX_ARENE)) {
    				pPerso.x --;
    				pPerso.y --;
    				calc=true;
    			}
    			else if ((pPerso.x == Constantes.XMAX_ARENE) && (pPerso.y == Constantes.YMIN_ARENE)) {
    				pPerso.x --;
    				pPerso.y ++;
    				calc=true;
    			}
    			else if ((pPerso.x == Constantes.XMIN_ARENE) && (pPerso.y == Constantes.YMIN_ARENE)) {
    				pPerso.x ++;
    				pPerso.y ++;
    				calc=true;
    			}
    			else if ((pPerso.x == Constantes.XMIN_ARENE) && (pPerso.y == Constantes.YMAX_ARENE)) {
    				pPerso.x ++;
    				pPerso.y --;
    				calc=true;
    			}
    			if (!calc){
    				paway = Calculs.positionAleatoireArene();
    			}
    			paway.x=-pPerso.x;
    			paway.y=-pPerso.y;
    			
    			
    		}
    	}
    	return paway ;
    }
    
   




}
