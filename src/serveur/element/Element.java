package serveur.element;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Un element du jeu (potion ou personnage). 
 *
 */
public abstract class Element implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Nom de l'element.
	 */
	protected String nom;
	
	/**
	 * Groupe a l'origine de l'element. 
	 */
	protected String groupe;

	/**
	 * Caracteristiques de l'element (au moins sa vie).
	 */
	protected HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique,Integer>();
	
	/**
	 * Inventaire de l'element (Pour un personnage)
	 */
	public Potion inventaire;

	/**
	 * Type de l'élément
	 */
	public String type;
	
	/**
	 * Cree un element avec son nom et son groupe. Les caracteristiques sont
	 * initialisees a leur valeur par defaut. 
	 * @param nom nom de l'element
	 * @param groupe d'etudiants de l'element
	 */
	public Element(String nom, String groupe, String t) {
		this.nom = nom;
		this.groupe = groupe;
		this.caracts = Caracteristique.mapCaracteristiquesDefaut();
		this.inventaire= null;
		this.type = t;
	}
	
	/**
	 * Cree un element avec son nom, son groupe et ses caracteristiques. 
	 * @param nom nom de l'element
	 * @param groupe d'etudiants de l'element
	 * @param caracts caracteristiques de l'element
	 */
	public Element(String nom, String groupe, HashMap<Caracteristique, Integer> caracts, String t) {	
		this(nom, groupe, t);

		// toutes les caracteristiques sont toujours initialisees
		// les caracteristiques donnees remplacent celles par defaut
		for(Caracteristique c : caracts.keySet()) {
			this.caracts.put(c, caracts.get(c));
		}
	}
	
	/**
	 * Retourne la valeur associee a la caracteristique specifiee.
	 * @param c caracterisique
	 * @return valeur correspondant a la caracteristique, ou null si elle 
	 * n'existe pas
	 */
	public Integer getCaract(Caracteristique c) {
		return caracts.get(c);
	}

	public String getNom() {
		return nom;
	}
	
	public String getGroupe() {
		return groupe;
	}
	
	public String getNomGroupe() {
		return nom + "_" + groupe;
	}
	
	public HashMap<Caracteristique, Integer> getCaracts() {
		return caracts;
	}

	@Override
	public String toString() {
		return getNomGroupe() + " " + caracts;
	}
	
	/**
	 * Retourne la potion stocké dans l'inventaire
	 */
	public Potion getInventaire() {
		return inventaire;
	}
	
	/**
	 * Vide l'inventaire du personnage
	 */
	public void suppInventaire(){
		this.inventaire=null;
	}
	
}
