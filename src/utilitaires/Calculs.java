package utilitaires;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import static utilitaires.Constantes.*;

import serveur.element.Caracteristique;
import serveur.element.Element ;
import serveur.element.Personnage;
import serveur.element.Potion ;
import serveur.IArene;
/**
 * Classe regroupant quelques methodes utiles pour l'arene (distance, case vide,
 * elements voisins...).
 */
public class Calculs {

	/**
	 * Renvoie la distance de Chebyshev entre deux points.
	 * @param p1 le premier point
	 * @param p2 le deuxieme point
	 * @return distance de Chebyshev
	 */
	public static int distanceChebyshev(Point p1, Point p2) {
		return Math.max(Math.abs(p1.x-p2.x),Math.abs(p1.y-p2.y));
	}

	/**
	 * Verifie si un element parmi les voisins occupe la position donnee. 
	 * @param p position   
	 * @param voisins voisins
	 * @return vrai si la case est vide, faux sinon
	 */
	public static boolean caseVide(Point p, HashMap<Integer, Point> voisins) {
		boolean trouve = false;
		Point pAux = null;
		Iterator<Point> it = voisins.values().iterator();
		
		while (!trouve && it.hasNext()) {
			pAux = it.next();
			trouve = p.equals(pAux); 
		}
		
		return !trouve;
	}
	
	/**
	 * Teste si le point donne est dans l'arene.
	 * @param p point
	 * @return vrai si le point est dans les limites de l'arene, faux sinon
	 */
	public static boolean estDansArene(Point p) {
		return XMIN_ARENE <= p.x && p.x <= XMAX_ARENE &&
				YMIN_ARENE <= p.y && p.y <= YMAX_ARENE;
	}
	
	/** 
	 * Renvoie le meilleur point a occuper par l'element courant dans la 
	 * direction de la cible.
	 * @param origine point sur lequel se trouve l'element courant
	 * @param objectif point sur lequel se trouve la cible
	 * @param voisins positions des elements proches 
	 * @return meilleur point libre a une distance de 1 dans la direction de la 
	 * cible, ou null s'il n'en existe aucun
	 */
	public static Point meilleurPoint(Point origine, Point objectif, 
			HashMap<Integer, Point> voisins, int vit) {
		
		// liste contenant tous les positions vers lesquelles l'element peut avancer :
		// les 8 cases autour de lui
		ArrayList<Point> listePossibles = new ArrayList<Point>();		
		
		Point tempPoint;
	
		
		for (int i = -vit; i <= vit; i++) {
			for (int j = -vit; j <= vit; j++) {
				if ((i != 0) || (j != 0))  { // pas le point lui-meme
					tempPoint = new Point(origine.x + i, origine.y + j);
					
					if(estDansArene(tempPoint)) {
						listePossibles.add(tempPoint);
					}
				}
			}
		}
		
		
		// organise les points de la liste du plus pres vers le plus eloigne de la cible
		Collections.sort(listePossibles, new PointComp(objectif));
		
		// cherche la case vide la plus proche de la cible
		boolean trouve = false;
		int i = 0;
		Point res = null;
		
		while (!trouve & i < listePossibles.size()) {
			res = listePossibles.get(i);
			trouve = caseVide(res, voisins);
			i++;
		}

		return res;
	}

	/**
	 * Cherche l'element le plus proche dans la limite
	 * de la vision du personnnage.
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 */
	public static int chercheElementProche(Point origine, HashMap<Integer, Point> voisins) {
		int distPlusProche = VISION;
		int refPlusProche = 0;
		
		for(int refVoisin : voisins.keySet()) {
			Point target = voisins.get(refVoisin);
			
			if (distanceChebyshev(origine, target) <= distPlusProche) {
				distPlusProche = Calculs.distanceChebyshev(origine, target);
				refPlusProche = refVoisin;
			}
		}
		
		return refPlusProche;
	}
	
	/**
	 * Vérifie s'il y a une potion présente parmis la liste de voisins
	 * @param voisins liste des voisins
	 * @param arene
	 * @return boolean
	 * @throws RemoteException 
	 */
	
	public static boolean potionPresente(HashMap<Integer, Point> voisins, IArene arene) throws RemoteException
	{
		Element e ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;

			if (e instanceof Potion)
			{
				return true ;
			}
		}
		
		return false ;
	}
	
	/**
	 * Vérifie s'il y a un adversaire présent parmis la liste de voisins
	 * @param voisins liste des voisins
	 * @param arene
	 * @param groupe nom du groupe auquel appartient l'attaquant
	 * @return boolean
	 * @throws RemoteException 
	 */
	
	public static boolean adversairePresent(HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException
	{
		Element e ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;

			if (e instanceof Personnage && !groupe.equals(e.getGroupe()))
			{
				return true ;
			}
		}
		
		return false ;
	}
	
	/**
	 * Vérifie s'il y a un allié présent parmis la liste de voisins
	 * @param voisins liste des voisins
	 * @param arene
	 * @param groupe nom du groupe auquel appartient l'attaquant
	 * @return boolean
	 * @throws RemoteException 
	 */
	
	public static boolean alliePresent(HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException
	{
		Element e ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;

			if (e instanceof Personnage && groupe.equals(e.getGroupe()))
			{
				return true ;
			}
		}
		
		return false ;
	}
	 
	/**
	 * Cherche la potion la plus proche dans la limite
	 * de la vision du personnnage.
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @param arene
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 * @throws RemoteException 
	 */
	public static int cherchePotionProche(Point origine, HashMap<Integer, Point> voisins, IArene arene) throws RemoteException {
		int distPlusProche = VISION;
		int refPlusProche = 0;
		Element e ;
		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;

			if (e instanceof Potion)
			{
				Point target = voisins.get(refVoisin);
				
				if (distanceChebyshev(origine, target) <= distPlusProche) {
					distPlusProche = Calculs.distanceChebyshev(origine, target);
					refPlusProche = refVoisin;
				}
			}
		}
		
		return refPlusProche;
	}
	
	/**
	 * Cherche la potion de regain de force dans la limite
	 * de la vision du personnnage.
	 * @param voisins liste des voisins
	 * @param arene
	 * @param vie du soigneur cherchant la potion
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 * @throws RemoteException 
	 */
	
	public static int cherchePotionForce(HashMap<Integer, Point> voisins, IArene arene, int vie) throws RemoteException
	{
		int refBestPot = 0;
		int max = 0 ;
		Element e ;
	
	
		for(int refVoisin : voisins.keySet())
		{
			 e = arene.elementFromRef(refVoisin) ;
			 
			 if (e.getNom().equals("basic"))
			 {
				 int viePot = e.getCaract(Caracteristique.VIE);
				 int forcePot = e.getCaract(Caracteristique.FORCE);
				 if (vie+viePot > 0 && forcePot > max)
				 {
					 refBestPot = refVoisin;
					 max = forcePot;
				 }
			 }

		}
	
	 	return refBestPot;
	}
	
	/**
	 * Cherche l'adversaire le plus proche dans la limite
	 * de la vision du personnnage.
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @param aren
	 * @param groupe nom du groupe de l'attaquant
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 * @throws RemoteException 
	 */
	public static int chercheAdversaireProche(Point origine, HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException 
	{
		int distPlusProche = VISION;
		int refPlusProche = 0;
		Element e ;
		

		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;
			if (e instanceof Personnage && !groupe.equals(e.getGroupe()))
			{
	
					Point target = voisins.get(refVoisin);
					
					if (distanceChebyshev(origine, target) <= distPlusProche)
					{
						distPlusProche = Calculs.distanceChebyshev(origine, target);
						refPlusProche = refVoisin;
					}
				
			}
		}

		return refPlusProche;
	}
	
	/**
	 * Cherche l'allié ayant le moins de pv dans la limite
	 * de la vision du personnnage.
	 * @param origine position a partir de laquelle on cherche
	 * @param voisins liste des voisins
	 * @param aren
	 * @param groupe nom du groupe de l'attaquant
	 * @return reference de l'element le plus proche, 0 si il n'y en a pas
	 * @throws RemoteException 
	 */
	public static int chercheAllieProche(Point origine, HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException {

		int refMoinsPv = 0;
		Element e ;
		int min = 500;
		
		for(int refVoisin : voisins.keySet()) 
		{
			e = arene.elementFromRef(refVoisin) ;
			int vieElement = e.getCaract(Caracteristique.VIE);
			 if (vieElement < min && groupe.equals(e.getGroupe()) && e instanceof Personnage)
			 {
				 refMoinsPv = refVoisin;
				 min=vieElement;
			 }
		}
		
		return refMoinsPv;
	}
	
	 /**
	  * Cherche l'adversaire ayant le plus grand nombre de pv vers lequel se diriger, dans la limite
	  * de la vision du personnnage.e
	  * @param voisins liste des voisins
	  * @param arene
	  * @return reference de l'element le plus proche, 0 si il n'y en a pas	
	  * @throws RemoteException */
	
	
	 public static int cherchePlusGrandAdversaire(HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException 
	 {
	
		 int refPlusGrand = 0;
		 int max = 0 ;
		 Element e ;
	
	
		 for(int refVoisin : voisins.keySet())
		 {
			 e = arene.elementFromRef(refVoisin) ;
			 int vieElement = e.getCaract(Caracteristique.VIE);
			 if (vieElement > max && !groupe.equals(e.getGroupe()) && e instanceof Personnage)
			 {
				 refPlusGrand = refVoisin;
				 max = vieElement;
			 }
		 }
	
	 	return refPlusGrand;
	 }
	 
	 /**
	  * Cherche l'adversaire ayant le plus petit nombre de pv vers lequel se diriger, dans la limite
	  * de la vision du personnnage.e
	  * @param voisins liste des voisins
	  * @param arene
	  * @return reference de l'element le plus proche, 0 si il n'y en a pas	
	  * @throws RemoteException */
	
	
	 public static int cherchePlusFaibleAdversaire(HashMap<Integer, Point> voisins, IArene arene, String groupe) throws RemoteException 
	 {
	
		 int refPlusFaible = 0;
		 int max = 100 ;
		 Element e ;
	
	
		 for(int refVoisin : voisins.keySet())
		 {
			 e = arene.elementFromRef(refVoisin) ;
			 int vieElement = e.getCaract(Caracteristique.VIE);
			 if (vieElement <= max && !groupe.equals(e.getGroupe()) && e instanceof Personnage)
			 {
				 refPlusFaible = refVoisin;
				 max = vieElement;
			 }
		 }
	
	 	return refPlusFaible;
	 }
	 
	 /**
	  * TODO
	  * Simulation d'un duel 
	  * @param e1 joueur 1
	  * @param e2 joueur 2
	  * @return true si joueur 1 gagne, false sinon
	  */
	 public boolean vainqueurDuel (Element e1, Element e2)
	 {
		 if ((e1.getCaract(Caracteristique.VIE) < e2.getCaract(Caracteristique.VIE)) || (e1.getCaract(Caracteristique.FORCE) < e2.getCaract(Caracteristique.FORCE)))
		 {
			 return false ;
		 }
		 
		 return true ;
	 }
	/**
	 * Genere un entier dans un intervalle donne.
	 * @param min borne inferieure de l'intervalle
	 * @param max borne superieure de l'intervalle
	 * @return valeur aleatoire generee
	 */
	public static int nombreAleatoire(int min, int max) {
		return new Random().nextInt(max - min + 1) + min;
	}

	/**
	 * Genere un valeur aleatoire pour une caracteristique donnee, entre min et
	 * max.
	 * @param c caracteristique
	 * @return valeur aleatoire generee
	 */
	public static int valeurCaracAleatoire(Caracteristique c) {
		return nombreAleatoire(c.getMin(), c.getMax());
	}

	/**
	 * Genere un valeur aleatoire pour une caracteristique donnee, entre -max
	 * et +max (pour les potions).
	 * @param c caracteristique
	 * @return valeur aleatoire generee
	 */
	public static int valeurCaracAleatoirePosNeg(Caracteristique c) {
		return nombreAleatoire(-c.getMax(), c.getMax());
	}
	
	/**
	 * Renvoie un point aleatoire de l'arene.
	 * @return position aleatoire
	 */
	public static Point positionAleatoireArene() {
		return new Point(
				Calculs.nombreAleatoire(XMIN_ARENE, XMAX_ARENE), 
				Calculs.nombreAleatoire(YMIN_ARENE, YMAX_ARENE));
	}

	/**
	 * Cape une valeur dans un intervalle donne.
	 * @param min borne inferieure de l'intervalle
	 * @param max borne superieure de l'intervalle
	 * @param val valeur a caper
	 * @return valeur capee
	 */
	public static int restreintNombre(int min, int max, int val) {
		return Math.min(Math.max(val, min), max);
	}

	/**
	 * Cape une valeur correspondant a une caracteristique donnee.
	 * @param c caracteristique 
	 * @param val valeur
	 * @return valeur capee
	 */
	public static int restreintCarac(Caracteristique c, int val) {		
		return restreintNombre(c.getMin(), c.getMax(), val);
	}

	public static Point restreintPositionArene(Point position) {		
		return new Point(restreintNombre(XMIN_ARENE, XMAX_ARENE, position.x), 
				restreintNombre(YMIN_ARENE, YMAX_ARENE, position.y));
	}

	/**
	 * Transforme une duree en seconde en une chaine de caracteres de type 
	 * H:M:S.
	 * @param duree en secondes
	 * @return duree en chaine sous la forme H:M:S
	 */
	public static String timerToString(int duree) {	
		String res;
		
		if (duree < 0) {
			res = "illimite";
		} else {
			int heure, minute, seconde;
			
			seconde = duree % 60;
			minute = duree / 60;
			heure = minute / 60;
			minute = minute % 60;
			
			if (heure == 0) {
				res = minute + ":" + ((seconde<10) ? "0" : "") + seconde ;
			} else {
				res = heure + ":" + ((minute<10) ? "0" : "") + minute + ":" + ((seconde<10) ? "0" : "") + seconde;				
			}
		}
		
		return res;
	}
}
