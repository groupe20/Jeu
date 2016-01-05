package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Constantes;

public class Soigner extends Interaction<VuePersonnage> {
	/**
	 * Cree une interaction de soin.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Soigner(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	

	@Override
	public void interagit(){
		try{
			Personnage pAttaquant = attaquant.getElement();
			Personnage pDefenseur = defenseur.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int vieDefenseur = pDefenseur.getCaract(Caracteristique.VIE);
			
			int gainVie = forceAttaquant;
			
			
			// soin
			if (gainVie > 0) {
				
				if ((vieDefenseur + gainVie) > 100) arene.incrementeCaractElement(defenseur, Caracteristique.VIE, 100-vieDefenseur);
				else arene.incrementeCaractElement(defenseur, Caracteristique.VIE,gainVie);
			
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " soigne avec un montant ("
					+ gainVie + " points de vie)  " + Constantes.nomRaccourciClient(defenseur));
			}
			
			// initiative
			
			decrementeInitiative(attaquant);
			decrementeInitiative(defenseur);
			
		}
		catch(RemoteException e){
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}
	
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementeInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.incrementeCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
}



