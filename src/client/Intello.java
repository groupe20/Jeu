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
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Strategie d'un personnage intelligent. 
 */
public class Intello extends Perso {
	
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
	public Intello(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		
		logger.info("Lanceur", "Creation de la console...");
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Personnage(nom, groupe, caracts,"Intello"), 
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
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); 
			
		} else {
			
			Element perso = arene.elementFromRef(refRMI);
			String gr = perso.getGroupe();
			
				
				if (Calculs.adversairePresent(voisins, arene, gr)){
					if (Calculs.potionPresente(voisins, arene)){
						System.err.println("LOLOL");
						
						int refPotionProche = Calculs.cherchePotionProche(position, voisins, arene);
						int refAdvProche = Calculs.chercheAdversaireProche(position, voisins, arene, gr);
						int distPotProche = Calculs.distanceChebyshev(position, arene.getPosition(refPotionProche));
						int distAdvProche = Calculs.distanceChebyshev(position, arene.getPosition(refAdvProche));
						
						if (distAdvProche < distPotProche){
							if (arene.doitAttaquer(refRMI, refAdvProche)){
								System.err.println("S'apprete a attaquer");
							attaquer(arene,refRMI,refAdvProche,distAdvProche);
							}
							else{
								console.setPhrase("Je fuis le duel avec "+arene.elementFromRef(refAdvProche).getNom());
								arene.fuite(refRMI, refAdvProche);
							}
						}
						else if (arene.bonnePotion(refRMI, refPotionProche)){
							boire(arene,refRMI, refPotionProche,distPotProche);
						}
						else {
							console.setPhrase("J'erre...");
							arene.deplace(refRMI, 0); 
						}
						
					}
					else {
						int refAdvProche = Calculs.chercheAdversaireProche(position, voisins, arene, gr);
						int distAdvProche = Calculs.distanceChebyshev(position, arene.getPosition(refAdvProche));

						if (arene.doitAttaquer(refRMI, refAdvProche)){
							attaquer(arene,refRMI,refAdvProche,distAdvProche);
							}
							else{
								arene.fuite(refRMI, refAdvProche);
							}
					}
				}
					
				else if (Calculs.potionPresente(voisins, arene)) {
					
					int refPotionProche = Calculs.cherchePotionProche(position, voisins, arene);
					int distPotProche = Calculs.distanceChebyshev(position, arene.getPosition(refPotionProche));
						
					if (arene.bonnePotion(refRMI, refPotionProche)){
						boire(arene,refRMI, refPotionProche,distPotProche);
					}
					else {
						console.setPhrase("J'erre...");
						arene.deplace(refRMI, 0); 
					}
				}
				
				else {
					console.setPhrase("J'erre...");
					arene.deplace(refRMI, 0); 
					
				}
					
			}
				
				
			
		
	}
	
	
	public void attaquer (IArene arene, int refRMI, int refCible, int distPlusFaibleAdv) throws RemoteException
	{
	
		Element plusFaibleAdv = arene.elementFromRef(refCible);
		
	
		if(distPlusFaibleAdv <= Constantes.DISTANCE_MIN_INTERACTION) //distance du crevard est dans Constantes.java
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
	
	public void boire(IArene arene, int refRMI, int refCible, int distPot) throws RemoteException{
		Element potion = arene.elementFromRef(refCible);
		
		
		if(distPot <= Constantes.DISTANCE_MIN_INTERACTION) //distance du crevard est dans Constantes.java
		{ // si suffisamment proches
			// j'interagis directement
			// duel
			console.setPhrase("Je bois " + potion.getNom());
			arene.ramassePotion(refRMI, refCible);				
			
		} 
		else 
		{ // si voisins, mais plus eloignes
			// je vais vers le plus proche
			console.setPhrase("Je vais vers la potion " + potion.getNom());
			arene.deplace(refRMI, refCible);
		}
	}
}

