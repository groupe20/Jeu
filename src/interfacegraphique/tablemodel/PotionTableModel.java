package interfacegraphique.tablemodel;

import java.util.ArrayList;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePotion;

/**
 * TableModel des potions.
 * 
 */
public class PotionTableModel extends ElementTableModel<VuePotion> {
	
	private static final long serialVersionUID = 1L;

	
	public PotionTableModel() {
		colonnes = new ArrayList<InformationColonne<VuePotion>>();
		indexNom = 1;
		
		// type de la potion
		colonnes.add(new InformationColonne<VuePotion>("Ref", 40, Integer.class, new ValeurColonneRefRMI())); 
		
		// nom de la potion (index 1)
		colonnes.add(new InformationColonne<VuePotion>("Nom", 0, String.class, new ValeurColonneNom())); 
		
		
		// caracteristiques
			colonnes.add(new InformationColonne<VuePotion>(Caracteristique.VIE.toString(), 40, Integer.class, new ValeurColonneCaract(Caracteristique.VIE)));
			colonnes.add(new InformationColonne<VuePotion>(Caracteristique.FORCE.toString(), 40, Integer.class, new ValeurColonneCaract(Caracteristique.FORCE)));
			colonnes.add(new InformationColonne<VuePotion>(Caracteristique.INITIATIVE.toString(), 40, Integer.class, new ValeurColonneCaract(Caracteristique.INITIATIVE)));
			colonnes.add(new InformationColonne<VuePotion>(Caracteristique.VITESSE.toString(), 40, Integer.class, new ValeurColonneCaract(Caracteristique.VITESSE)));


	}
   
}
