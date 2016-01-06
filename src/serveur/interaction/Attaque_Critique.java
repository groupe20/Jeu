package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

/**
 * Represente une attaque critiqe.
 *
 */
public class Attaque_Critique extends Duel {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Attaque_Critique(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	@Override
	public void interagit() {
		try {
			Personnage pAttaquant = attaquant.getElement();
			
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int fureurAttaquant = pAttaquant.getCaract(Caracteristique.CRITIQUE);
			
			int perteVie = forceAttaquant + fureurAttaquant;
		
			Point positionEjection = positionEjection(defenseur.getPosition(), attaquant.getPosition(), perteVie);

			// ejection du defenseur
			defenseur.setPosition(positionEjection);
			
		
				// degats
				if (perteVie > 0) {
					arene.incrementeCaractElement(defenseur, Caracteristique.VIE, -perteVie);
				
					logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " met un high kick ("
						+ perteVie + " points de degats) a " + Constantes.nomRaccourciClient(defenseur) + "et l'envoi valser");
				}
			
				// màj caracteristiques
				arene.incrementeCaractElement(defenseur, Caracteristique.DEFENSE, -15);
				arene.incrementeCaractElement(attaquant, Caracteristique.CRITIQUE, 50);


		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}
}
