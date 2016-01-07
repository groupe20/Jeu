package serveur.element;

import java.util.HashMap;

/**
 * Caracteristiques possibles pour les personnages et les potions. 
 *
 */
public enum Caracteristique {
	
	/**
	 * Vie : nombre de points de vie.
	 */
	VIE("Vie", "VIE", 0, 100, 100),
	
	/**
	 * Force : indique les degats infliges. 
	 */
	FORCE("Force", "FOR", 0, 100, 30),
		
	/**
	 * Initiative : definit l'ordre d'action des personnages lors d'un tour de jeu. 
	 */
	INITIATIVE("Initiative", "INIT", 0, 200, 50),
	
	/**
	 * Inventaire : Permet de stocker une potion. 
	 */
	INVENTAIRE("Inventaire", "INV", 0, 1, 0),
	
	/**
	 * Fureur : permet de réaliser une attaque critique.
	 */
	CRITIQUE("Critique", "CRIT", 0, 50, 0),
	
	/**
	 * Defense : pourcentage de dégâts pouvant être bloqués.
	 */
	DEFENSE("Defense", "DEF", 0, 50, 50),
	
	/**
	 * Vitesse : vitesse de déplacement.
	 */
	VITESSE("Vitesse", "VIT", 1, 3, 1);
	
	/**
	 * Nom complet.
	 */
	private final String nomComplet;
	
	/**
	 * Nom court. 
	 */
	private final String nomCourt;
	
	/**
	 * Valeur minimale.
	 */
	private final int min;
	
	/**
	 * Valeur maximale.
	 */
	private final int max;
	
	/**
	 * Valeur initiale par defaut. 
	 */
	private final int init;
	
	/**
	 * Cree une caracteristique.
	 * @param nomComplet nom complet
	 * @param nomCourt nom raccourci
	 * @param min valeur minimale
	 * @param max valeur maximale
	 * @param init valeur initiale
	 * @param prix prix d'achat
	 */
	private Caracteristique(String nomComplet, String nomCourt, int min, int max, int init) {
		this.nomComplet = nomComplet;
		this.nomCourt = nomCourt;
		this.min = min;
		this.max = max;
		this.init = init;
	}

	/**
	 * Cree un map de caracteristiques contenant toutes les caracteristiques 
	 * avec leur valeur d'initialisation par defaut. 
	 * @return map caracteristique/valeur contenant les valeurs par defaut
	 */
	public static HashMap<Caracteristique,Integer> mapCaracteristiquesDefaut() {
		HashMap<Caracteristique, Integer> caractsValues = new HashMap<Caracteristique, Integer>();
		
		for (Caracteristique caract : values()) {
			caractsValues.put(caract, caract.init);
		}
		
		return caractsValues;
	}

	/**
	 * Compte le nombre total de caracteristiques.
	 * @return nombre de caracteristiques
	 */
	public static int nbCaracts() {
		return values().length;
	}
	
	public String getNomComplet() {
		return nomComplet;
	}

	public int getMin() {
		return min;
	}

	public int getMax() {
		return max;
	}

	public int getInit() {
		return init;
	}

	@Override
	public String toString() {
		return nomCourt;
	}


	
}
