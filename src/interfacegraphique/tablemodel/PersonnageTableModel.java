package interfacegraphique.tablemodel;

import java.util.ArrayList;

import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;

/**
 * TableModel des personnages, connectes ou deconnectes.
 * 
 */
public class PersonnageTableModel extends ElementTableModel<VuePersonnage> {

	private static final long serialVersionUID = 1L;
    
	
	public PersonnageTableModel() {
		colonnes = new ArrayList<InformationColonne<VuePersonnage>>();
		indexNom = 1;
		
		// reference RMI
		colonnes.add(new InformationColonne<VuePersonnage>("Ref", 40, Integer.class, new ValeurColonneRefRMI())); 
		
		// nom du personnage (index 1)
		colonnes.add(new InformationColonne<VuePersonnage>("Nom", 0, String.class, new ValeurColonneNom())); 
		
		// groupe du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Team", 60, String.class, new ValeurColonneGroupe()));
		
		colonnes.add(new InformationColonne<VuePersonnage>("Type", 100, String.class, new ValeurColonneType()));

		// caracteristiques
		for(Caracteristique car : Caracteristique.values()) {
			colonnes.add(new InformationColonne<VuePersonnage>(car.toString(), 40, Integer.class, new ValeurColonneCaract(car)));
		}
		
		// phrase du personnage
		colonnes.add(new InformationColonne<VuePersonnage>("Phrase", 300, String.class, new ValeurColonnePhrase())); 
	}
}
