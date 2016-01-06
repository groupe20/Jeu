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
	            else if ((distPlusProcheAdv > 2) && (distPlusProcheAdv < 5) && (player.inventaire != null))
	            {
	            	System.err.println("cas critique");

	            	//je suis dans une situation critique et je dispose d'une potion
	            	if (player.inventaire.getNom().equals("teleportation") || player.inventaire.getNom().equals("nitro"))
	            	{
	            		//je me téléporte ou j'accélère
	            		console.setPhrase("Je me casse d'ici, trop dangereux");
	            		arene.boireInv(refRMI);
	            	}
	            	else
	            	{
	            		//je pose un piege (mortelle ou immobilite)
	            		console.setPhrase("J'ai un cadeau pour toi " + advPlusProche.getNom());
	            		arene.deposePotion(refRMI);
	            	}
	            }
	            //On ramasse la potion si elle est plus proche que l'adversaire
	            else if (Calculs.potionPresente(voisins,arene)){
	            	int refCiblePot = Calculs.cherchePotionProche(position, voisins, arene);
        			int distPlusProchePot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
				
        			Element potPlusProche = arene.elementFromRef(refCiblePot);
        			if (player.inventaire == null && !potPlusProche.getNom().equals("basic") && distPlusProchePot <= distPlusProcheAdv){
        				if(distPlusProchePot <= Constantes.DISTANCE_MIN_INTERACTION)
    					{ // si suffisamment proches
    						// j'interagis directement
    						// ramassage
							console.setPhrase("Je ramasse une potion");
							arene.stockPotion(refRMI, refCiblePot);				
						
    					} 
    					else
    					{ // si voisins, mais plus eloignes
    						// je vais vers le plus proche
    						console.setPhrase("Je vais vers une potion " + potPlusProche.getNom());
							arene.deplace(refRMI, refCiblePot);
    					}
        			}
        			else {
        				//sinon je fuis le duel
    	            	System.err.println("cas fuite");
    		            console.setPhrase("Je fuis le duel avec " + advPlusProche.getNom());
    		            arene.fuite(refRMI, refCibleAdv);
        			}
	            }
	            else
	            {	//sinon je fuis le duel
	            	System.err.println("cas fuite");
		            console.setPhrase("Je fuis le duel avec " + advPlusProche.getNom());
		            arene.fuite(refRMI, refCibleAdv);
		            
	            }
        	}
        	else
        	{
        		if (Calculs.potionPresente(voisins, arene)){
        			System.err.println("cas potion");

        			int refCiblePot = Calculs.cherchePotionProche(position, voisins, arene);
        			int distPlusProchePot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
				
        			Element potPlusProche = arene.elementFromRef(refCiblePot);
        			if (player.inventaire == null && !potPlusProche.getNom().equals("basic")) 
        			{	//si mon inventaire est vide (les basic m'interessent pas)
				
        					if(distPlusProchePot <= Constantes.DISTANCE_MIN_INTERACTION)
        					{ // si suffisamment proches
        						// j'interagis directement
        						// ramassage
								console.setPhrase("Je ramasse une potion");
								arene.stockPotion(refRMI, refCiblePot);				
							
        					} 
        					else
        					{ // si voisins, mais plus eloignes
        						// je vais vers le plus proche
        						console.setPhrase("Je vais vers une potion " + potPlusProche.getNom());
								arene.deplace(refRMI, refCiblePot);
        					}
        			}
        			else{
        				//si basic, j'erre
    					console.setPhrase("J'erre...");
    		            arene.deplace(refRMI, 0); 
        			}
					
				}
				else
				{
					//si inventaire plein, j'erre
					console.setPhrase("J'erre...");
		            arene.deplace(refRMI, 0); 
				}
        	}
            
            
            
            
            
            
        }
    }
    
}
    
