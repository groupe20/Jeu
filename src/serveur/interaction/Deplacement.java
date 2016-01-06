package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente le deplacement d'un personnage.
 *
 */
public class Deplacement {

	/**
	 * Vue du personnage qui veut se deplacer.
	 */
	private VuePersonnage personnage;
	
	/**
	 * References RMI et vues des voisins (calcule au prealable). 
	 */
	private HashMap<Integer, Point> voisins;
	
	/**
	 * Cree un deplacement.
	 * @param personnage personnage voulant se deplacer
	 * @param voisins voisins du personnage
	 */
	public Deplacement(VuePersonnage personnage, HashMap<Integer, Point> voisins) { 
		this.personnage = personnage;

		if (voisins == null) {
			this.voisins = new HashMap<Integer, Point>();
		} else {
			this.voisins = voisins;
		}
	}

	/**
	 * Deplace ce sujet d'une case en direction de l'element dont la reference
	 * est donnee.
	 * Si la reference est la reference de l'element courant, il ne bouge pas ;
	 * si la reference est egale a 0, il erre ;
	 * sinon il va vers le voisin correspondant (s'il existe dans les voisins).
	 * @param refObjectif reference de l'element cible
	 */    
	public void seDirigeVers(int refObjectif) throws RemoteException {
		Point pvers;

		// on ne bouge que si la reference n'est pas la notre et que l'on est pas immobile
		if ((refObjectif != personnage.getRefRMI()) && (this.personnage.getElement().nbToursImm == 0)) {
			
			// la reference est nulle (en fait, nulle ou negative) : 
			// le personnage erre
			if (refObjectif <= 0) { 
				pvers = Calculs.positionAleatoireArene();
						
			} else { 
				// sinon :
				// la cible devient le point sur lequel se trouve l'element objectif
				pvers = voisins.get(refObjectif);
			}
	
			// on ne bouge que si l'element existe
			if(pvers != null) {
				seDirigeVers(pvers);
			}
		}
		else{
			this.personnage.getElement().nbToursImm--;
		}
	}

	
	public void seloignerDe (int refObjectif) throws RemoteException {
        Point pVoisin;
        Point paway=new Point();
        boolean calc = false;
        
        Point pPerso = personnage.getPosition();
        
        
        // on ne bouge que si la reference n'est pas la notre
        if (refObjectif != personnage.getRefRMI()) {
            
            // la reference est nulle (en fait, nulle ou negative) : 
            // le personnage erre
            if (refObjectif <= 0) { 
                paway = Calculs.positionAleatoireArene();
                        
            }
            // sinon :
            // la cible devient le point sur lequel se trouve l'element objectif
            pVoisin = voisins.get(refObjectif);

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

                System.err.println("x: "+pPerso.x+" y: "+pPerso.y);
                paway.x=-pPerso.x;
                paway.y=-pPerso.y;

                seDirigeVers(paway);
            }
            
        }
    }


	
	/**
	 * Deplace ce sujet d'une case en direction de la case donnee.
	 * @param objectif case cible
	 * @throws RemoteException
	 */
	public void seDirigeVers(Point objectif) throws RemoteException {
		Point cible = Calculs.restreintPositionArene(objectif); 
		
		// on cherche le point voisin vide
		Point dest = Calculs.meilleurPoint(personnage.getPosition(), cible, voisins,personnage.getElement().getCaract(Caracteristique.VITESSE));
		
		if(dest != null) {
			personnage.setPosition(dest);
		}
	}
}
