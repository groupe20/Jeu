/**
 * 
 */
package serveur.interaction;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

/**
 * Represente le stockage d'une potion par un personnage.
 *
 */

public class Stockage extends Interaction<VuePotion> {
	
	private static final Caracteristique INVENTAIRE = null;

	/**
	 * Cree une interaction de stockage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	
	
	
	public Stockage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}
	
	@Override
	public void interagit() {
		Personnage pAttaquant = attaquant.getElement();
		Potion pPotion = defenseur.getElement();

		
		logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de stocker " + 
				Constantes.nomRaccourciClient(defenseur));
		
		// si le personnage est vivant
		if(attaquant.getElement().estVivant()) {

			// caracteristiques de la potion
			HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
			
			// Stockage de la potion
			if (pAttaquant.getCaract(Caracteristique.INVENTAIRE) == 0){
				pAttaquant.inventaire = pPotion;
				pAttaquant.incrementeCaract(Caracteristique.INVENTAIRE, 1);
				logs(Level.INFO, "Potion stockée !");
			}
		
			
			// suppression de la potion
			arene.ejectePotion(defenseur.getRefRMI());
			
		} else {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
					Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
		}
	}
}


