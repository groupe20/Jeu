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
 * Strategie d'un personnage. 
 */
public class Fuyard extends Perso {
    
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
    public Fuyard(String ipArene, int port, String ipConsole, 
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
        
        try 
        {
            refRMI = console.getRefRMI();
            position = arene.getPosition(refRMI);
            
            
        } 
        catch (RemoteException e) 
        {
            e.printStackTrace();
        }
        
        if (voisins.isEmpty()) 
        { // je n'ai pas de voisins, j'erre
            console.setPhrase("J'erre...");
            arene.deplace(refRMI, 0); 
            
        } 
        else 
        {
        	Element player = arene.elementFromRef(refRMI) ;
        	String gr = player.getGroupe() ;
        	
        	if (Calculs.adversairePresent(voisins, arene, gr))
        	{
	            int refCibleAdv = Calculs.chercheAdversaireProche(position, voisins, arene, gr);
	            int distPlusProcheAdv = Calculs.distanceChebyshev(position, arene.getPosition(refCibleAdv));
	            Element advPlusProche = arene.elementFromRef(refCibleAdv);
	            
	            if(distPlusProcheAdv <= Constantes.DISTANCE_MIN_INTERACTION)
				{	//si par malchance, je suis à portée de duel, je fais le duel
					console.setPhrase("Je fais un duel avec " + advPlusProche.getNom());
					arene.lanceAttaque(refRMI, refCibleAdv);
				}
	            else
	            {	//sinon je fuis le duel
	            	/*if (player.inventaire != null)
	            	{
	            		if (player.inventaire.getNom().equals("teleportation"))
	            		{
	            			console.setPhrase("Je me casse d'ici, trop dangereux");
	    					//TODO arene.boire();
	            		}
	            		else if (player.inventaire.getNom().equals("immobilité") || player.inventaire.getNom().equals("mortelle"))
	            		{
	            			console.setPhrase("Je pose la potion !");
	    					arene.deposePotion(refRMI) ;
	            		}
	            	}*/
		            console.setPhrase("Je fuis le duel avec " + advPlusProche.getNom());
		            arene.fuite(refRMI, refCibleAdv);
		            
	            }
        	}
        	else
        	{
        		int refCiblePot = Calculs.cherchePotionProche(position, voisins, arene);
				int distPlusProchePot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
				
				Element potPlusProche = arene.elementFromRef(refCiblePot);

				if(distPlusProchePot <= Constantes.DISTANCE_MIN_INTERACTION && (potPlusProche.getNom().equals("teleportation") || potPlusProche.getNom().equals("immobilité") || potPlusProche.getNom().equals("mortelle")))
				{ // si suffisamment proches
					// j'interagis directement
						// ramassage
						console.setPhrase("Je ramasse une potion");
						arene.ramassePotion(refRMI, refCiblePot);

					
					
				} 
				else 
				{ // si voisins, mais plus eloignes
					// je vais vers le plus proche
					console.setPhrase("Je vais vers mon voisin " + potPlusProche.getNom());
					arene.deplace(refRMI, refCiblePot);
				}
        	}
            
            
            
            
            
            
        }
    }
}
    
