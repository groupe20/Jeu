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
public class Soigneur extends Perso {
    
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
    public Soigneur(String ipArene, int port, String ipConsole, 
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
        	Element healer = arene.elementFromRef(refRMI) ;
            String gr = healer.getGroupe() ;
            
        	if (Calculs.alliePresent(voisins, arene, gr))
        	{
        		int refCibleAllie = Calculs.chercheAllieProche(position, voisins, arene, gr);
	            int distAllie = Calculs.distanceChebyshev(position, arene.getPosition(refCibleAllie));
	            Element allieMoinsPv = arene.elementFromRef(refCibleAllie);
	            
	            if(distAllie <= Constantes.DISTANCE_MIN_INTERACTION)
	            {
	            	//si a portee, je le soigne
	            	console.setPhrase("Je soigne " + allieMoinsPv.getNom());
					//TODO
	            	//arene.soigner(refRMI,refCibleAllie);
	            }
	            else
	            {
	            	console.setPhrase("Je vais vers mon voisin " + allieMoinsPv.getNom() + " pour le soigner");
					arene.deplace(refRMI, refCibleAllie);
	            }
        	}
        	else
        	{
        		if (Calculs.potionPresente(voisins, arene))
        		{
        			int refCiblePot = Calculs.cherchePotionProche(position, voisins, arene);
    				int distPlusProchePot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
    				
    				Element potPlusProche = arene.elementFromRef(refCiblePot);

    				if(distPlusProchePot <= Constantes.DISTANCE_MIN_INTERACTION)
    				{ // si suffisamment proche
    						// ramassage
    						console.setPhrase("Je ramasse une potion");
    						arene.ramassePotion(refRMI, refCiblePot);

    					
    					
    				} 
    				else 
    				{ // si potions, mais plus eloignees
    					// je vais vers la plus proche
    					console.setPhrase("Je vais vers mon voisin " + potPlusProche.getNom());
    					arene.deplace(refRMI, refCiblePot);
    				}
        		}
        		else
        		{	//si je n'ai ni potions, ni alliés a soigner, j'erre
        			console.setPhrase("J'erre...");
                    arene.deplace(refRMI, 0); 
        		}
        	}
        	
   
            
            
        }
    }
}
