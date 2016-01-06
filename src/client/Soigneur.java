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
public class Soigneur extends Perso 
{
    
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
                    new Personnage(nom, groupe, caracts,"Soigneur"), 
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
        
        String gr="";
        
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
        	gr = player.getGroupe() ;
        	int refCibleAllie = 0 ;
        	int refCible = 0 ;
        	int distAllie = 0 ;
        	int distAdv = 0 ;
        	Element alliePlusProche = null ;
        	Element adv = null ;
        	boolean allie = false ;
        	boolean ad = false ;

        	
        	if (Calculs.alliePresent(voisins, arene, gr))
        	{
				refCibleAllie = Calculs.chercheAllieProche(position, voisins, arene, gr);
				distAllie = Calculs.distanceChebyshev(position, arene.getPosition(refCibleAllie));
				alliePlusProche = arene.elementFromRef(refCibleAllie);
				allie = true ;
        	}
        	
        	if (Calculs.adversairePresent(voisins, arene, gr))
        	{
        		refCible = Calculs.chercheAdversaireProche(position, voisins, arene, gr);
        		distAdv = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
        	
        		adv = arene.elementFromRef(refCible);
        		ad = true ;
        		
        	}

        	if (allie)
		{
        		if (ad && distAdv <= Constantes.DISTANCE_MIN_INTERACTION)
        		{
        			// duel
        			console.setPhrase("Je fais un duel avec " + adv.getNom());
        			arene.lanceAttaque(refRMI, refCible);	
        		}
        		else
        		{
        				if(distAllie <= Constantes.DISTANCE_MIN_INTERACTION)
					{	//si par hasard, je suis à portée de soigner, je soigne
						console.setPhrase("Je soigne " + alliePlusProche.getNom());
						arene.lanceSoin(refRMI, refCibleAllie);
					}
					else 
					{	//sinon on se déplace vers ce dernier
						console.setPhrase("Je vais vers mon voisin " + alliePlusProche.getNom()+ " pour le soigner!");
						arene.deplace(refRMI, refCibleAllie);
					}
        		}
		}
			
		else
		{
				if (ad && distAdv <= Constantes.DISTANCE_MIN_INTERACTION)
        			{
        			// duel
        			console.setPhrase("Je fais un duel avec " + adv.getNom());
        			arene.lanceAttaque(refRMI, refCible);	
        			}
				else
				{
					if (Calculs.potionPresente(voisins, arene))
					{
						int refCiblePot = Calculs.cherchePotionForce(voisins, arene, player.getCaract(Caracteristique.VIE));
						
						if (refCiblePot > 0)
						{
							int distBestPot = Calculs.distanceChebyshev(position, arene.getPosition(refCiblePot));
							Element bestPot = arene.elementFromRef(refCiblePot);
							
							
		
							if(bestPot.getNom().equals("basic") && distBestPot <= Constantes.DISTANCE_MIN_INTERACTION)
							{ // si suffisamment proches
								// j'interagis directement
									// ramassage
									console.setPhrase("Je ramasse une potion");
									arene.ramassePotion(refRMI, refCiblePot);	
							} 
							else 
							{ // si voisins, mais plus eloignes
								// je vais vers le plus proche
								console.setPhrase("Je vais vers mon voisin " + bestPot.getNom());
								arene.deplace(refRMI, refCiblePot);
							}
						}
						else
						{
							//sinon, j'erre
							console.setPhrase("J'erre...");
							arene.deplace(refRMI, 0); 
						}
						
						
						
					}
					
					else
					{
						//sinon, j'erre
						console.setPhrase("J'erre...");
						arene.deplace(refRMI, 0); 
					}
				}
		}
	}
        	
            
            
     }
    

}

