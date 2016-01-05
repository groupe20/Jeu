package serveur.interaction;

import lanceur.ErreurLancement;
import serveur.Arene;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;

public class Poser {

	
	//Vue du personnage qui va poser la potion
	private VuePersonnage personnage;
	private Arene arene;
	
	public Poser(VuePersonnage personnage,Arene arene){
		this.personnage=personnage;
		this.arene=arene;
	}
	
	public boolean pose(){
		Personnage p = this.personnage.getElement();
		
		try{
		// ajout de la potion
			new ThreadPotion("p",this.arene,p.inventaire).start();
		//logger.info("Lanceur", "Lancement de la potion reussi");
		//arene.setPhrase(personnage.getRefRMI(), "J'ai posé la potion!!! ");
		}
		catch (Exception e) {
			//logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
			return false;
		}
		
		p.suppInventaire();
		return true;
		
	}


}
