package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;


/**
 * Personnage qui attaque les personnages qui ont le plus de vie
 */
public class Crevard extends Perso {
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;

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
	public Crevard(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		        
        logger.info("Lanceur", "Creation de la console...");
        
        try {
            console = new Console(ipArene, port, ipConsole, this, 
                    new Personnage(nom, groupe, caracts,"Crevard"), 
                    nbTours, position, logger);
            logger.info("Lanceur", "Creation de la console reussie");
            
        } catch (Exception e) {
            logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
            e.printStackTrace();
        }
	}

	// TODO etablir une strategie afin d'evoluer dans l'arene de combat
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
		
		try {
			refRMI = console.getRefRMI();
			position = arene.getPosition(refRMI);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		Element player = arene.elementFromRef(refRMI) ;
    		String gr = player.getGroupe() ;
		
		if (!Calculs.adversairePresent(voisins, arene, gr)) 
		{ // je n'ai pas d'adversaires, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); 
			
		} 
		else
		{
			if (Calculs.potionPresente(voisins, arene))
			{
				int refCiblePot = Calculs.cherchePotionProche(position, voisins, arene) ;
    			int distPlusProchePot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
    			Element plusProchePot = arene.elementFromRef(refCiblePot);
    			
				if((plusProchePot.getNom().equals("mortelle") || plusProchePot.getNom().equals("immobilite")) && distPlusProchePot <= Constantes.DISTANCE_MIN_INTERACTION)
				{	//si je tombe sur un piege mortelle ou immobilite, je bois la potion
					console.setPhrase("Je ramasse une potion " + plusProchePot.getNom());
					arene.ramassePotion(refRMI, refCiblePot);
				}
				/*else
				{
					attaquer(position, voisins, arene, refRMI, gr) ;
				}*/
			}
			else
			{
				attaquer(position, voisins, arene, refRMI, gr) ;
			}
			
		}
	}
	
	public void attaquer (Point position, HashMap<Integer, Point> voisins, IArene arene, int refRMI, String gr) throws RemoteException
	{
	
		int refCible = Calculs.cherchePlusFaibleAdversaire(voisins, arene, gr); //chercherPlusFaibleAdv
		int distPlusFaibleAdv = Calculs.distanceChebyshev(position, arene.getPosition(refCible)); //distPlusFaibleAdv
	
		Element plusFaibleAdv = arene.elementFromRef(refCible); //plusFaibleAdv
	
		if(distPlusFaibleAdv <= Constantes.DISTANCE_MIN_INTERACTION_CREVARD) //distance du crevard est dans Constantes.java
		{ // si suffisamment proches
			// j'interagis directement
			// duel
			console.setPhrase("Je fais un duel avec " + plusFaibleAdv.getNom());
			arene.lanceAttaque(refRMI, refCible);				
			
		} 
		else 
		{ // si voisins, mais plus eloignes
			// je vais vers le plus proche
			console.setPhrase("Je vais vers mon ennemi " + plusFaibleAdv.getNom());
			arene.deplace(refRMI, refCible);
		}
	}

	
}
