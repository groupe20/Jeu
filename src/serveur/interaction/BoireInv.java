package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.element.Potion;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class BoireInv extends Interaction<VuePersonnage> {
	
	
	private Potion p;
	
	public BoireInv(Arene ar, VuePersonnage buveur, Potion Potion){
		super(ar,buveur,null);
		this.p=Potion;
	}

	@Override
	public void interagit() {
		
		Personnage pAttaquant = attaquant.getElement();
		VuePersonnage v = attaquant;

		try {
			//logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " essaye de boire la potion dans son inventaire: " + p.getNom());
			
			// si le personnage est vivant
			if(true) {
				//logs(Level.INFO, "vivant !");


				// caracteristiques de la potion
				//logs(Level.INFO, "avant recup !");

				HashMap<Caracteristique, Integer> valeursPotion = p.getCaracts();
				//logs(Level.INFO, "recup !");

				
				if (p.getNom().equals("teleportation")) {
                    arene.setPhrase(attaquant.getRefRMI(), "Je me teleporte");
                    Point p;
                    p = attaquant.getPosition();
                    p.x = (int)(Math.random() * (Constantes.XMAX_ARENE-Constantes.XMIN_ARENE)) + Constantes.XMIN_ARENE;
                    p.y = (int)(Math.random() * (Constantes.YMAX_ARENE-Constantes.YMIN_ARENE)) + Constantes.YMIN_ARENE;
                    arene.deplace(attaquant.getRefRMI(), p);
                }

				else if (p.getNom().equals("immobilite")) {
                    arene.setPhrase(attaquant.getRefRMI(), "Je deviens immobile pour 5 tours");
                    this.attaquant.getElement().nbToursImm=5;
                }				
				else {
					for(Caracteristique c : valeursPotion.keySet()) {
						arene.incrementeCaractElement(attaquant, c, valeursPotion.get(c));
						//logs(Level.INFO, "LOL !");

					}
					
					//logs(Level.INFO, "Potion bue !");
					
					// test si mort
					if(!attaquant.getElement().estVivant()) {
						arene.setPhrase(attaquant.getRefRMI(), "Je me suis empoisonne, je meurs ");
						//logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vient de boire un poison... Mort >_<");
					}
				}

				// suppression de la potion de l'inventaire
				pAttaquant.inventaire = null;
				pAttaquant.incrementeCaract(Caracteristique.INVENTAIRE, -1);
				//logs(Level.INFO, "Potion bue !");
				
				
			} else {
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " ou " + 
						p.getNom() + " est deja mort... Rien ne se passe");
			}
		} catch (RemoteException e) {
			//logs(Level.INFO, "\nErreur lors d'un ramassage : " + e.toString());
		}
	}
}


