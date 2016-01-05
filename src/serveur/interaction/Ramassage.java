package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import serveur.vuelement.VuePersonnage;
import serveur.vuelement.VuePotion;
import utilitaires.Constantes;

/**
 * Represente le ramassage d'une potion par un personnage.
 *
 */
public class Ramassage extends Interaction<VuePotion> {

	/**
	 * Cree une interaction de ramassage.
	 * @param arene arene
	 * @param ramasseur personnage ramassant la potion
	 * @param potion potion a ramasser
	 */
	public Ramassage(Arene arene, VuePersonnage ramasseur, VuePotion potion) {
		super(arene, ramasseur, potion);
	}

	@Override
	public void interagit() {
		Potion Potion = defenseur.getElement();

		try {
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de rammasser " + 
					Constantes.nomRaccourciClient(defenseur));
			
			// si le personnage est vivant
			if(attaquant.getElement().estVivant()) {

				// caracteristiques de la potion
				HashMap<Caracteristique, Integer> valeursPotion = defenseur.getElement().getCaracts();
				
				if (Potion.getNom().equals("teleportation")) {
                    arene.setPhrase(attaquant.getRefRMI(), "Je me teleporte");
                    Point p;
                    p = attaquant.getPosition();
                    p.x = (int)(Math.random() * (Constantes.XMAX_ARENE-Constantes.XMIN_ARENE)) + Constantes.XMIN_ARENE;
                    p.y = (int)(Math.random() * (Constantes.YMAX_ARENE-Constantes.YMIN_ARENE)) + Constantes.YMIN_ARENE;
                    arene.deplace(attaquant.getRefRMI(), p);
                }
                if (Potion.getNom().equals("immobilité")) {
                    arene.setPhrase(attaquant.getRefRMI(), "Je deviens immobile pour 5 tours");
                   this.attaquant.getElement().nbToursImm=5;
                }

                
                

				if (Potion.getNom().equals("invincible")) {
					//invincible
				}
				else {
					for(Caracteristique c : valeursPotion.keySet()) {
						arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
					}
					
					logs(Level.INFO, "Potion bue !");
					
					// test si mort
					if(!attaquant.getElement().estVivant()) {
						arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
						logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
					}
				}

				// suppression de la potion
				arene.ejectePotion(defenseur.getRefRMI());
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						Constantes.nomRaccourciClient(defenseur) + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}
