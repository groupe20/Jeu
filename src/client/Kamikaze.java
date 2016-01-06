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
public class Kamikaze extends Perso {
	
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
	public Kamikaze(String ipArene, int port, String ipConsole, 
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

	// TODO etablir une strategie afin d'evoluer dans l'arene de combat
	// une proposition de strategie (simple) est donnee ci-dessous
	/** 
	 * Decrit la strategie.
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
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

        	
			int refCible = Calculs.cherchePlusGrandAdversaire(voisins, arene, gr);
			int distPlusGrandAdv = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			Element elemPlusGrandAdv = arene.elementFromRef(refCible);

			if(distPlusGrandAdv <= Constantes.DISTANCE_MIN_INTERACTION)
			{ // si suffisamment proches
				// j'interagis directement
				// duel
				console.setPhrase("Je fais un duel avec " + elemPlusGrandAdv.getNom());
				arene.lanceAttaque(refRMI, refCible);				
				
			} 
			else 
			{ // si voisins, mais plus eloignes
				// je vais vers le plus proche
				if (elemPlusGrandAdv.getGroupe() == gr) {
					console.setPhrase("Je vais vers mon ami " + elemPlusGrandAdv.getNom());
				}
				else if (elemPlusGrandAdv.getGroupe() == "0") {
					console.setPhrase("Je vais vers une potion " + elemPlusGrandAdv.getNom());
				}
				else console.setPhrase("Je vais vers mon ennemi " + elemPlusGrandAdv.getNom());

				arene.deplace(refRMI, refCible);
			}
		}
	}

	
}
