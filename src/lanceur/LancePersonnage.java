package lanceur;

import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;

import client.Crevard;
import client.Fuyard;
import client.Intello;
import client.Kamikaze;
import client.Perso;
import client.Pochtron;
import client.Soigneur;
import logger.LoggerProjet;
import serveur.element.Caracteristique;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Lance une Console avec un Element sur l'Arene. 
 * A lancer apres le serveur, eventuellement plusieurs fois.
 */

public class LancePersonnage {
	

	public static void main(String p, String nom, String groupe) {

				
		// nombre de tours pour ce personnage avant d'etre deconnecte 
		// (30 minutes par defaut)
		// si negatif, illimite
		int nbTours = Constantes.NB_TOURS_PERSONNAGE_DEFAUT;
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
		
		// creation du logger
		LoggerProjet logger = null;		
		
		try {
			logger = new LoggerProjet(true, "personnage_" + nom + groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement du serveur
		try {
			String ipConsole = InetAddress.getLocalHost().getHostAddress();
			
			logger.info("Lanceur", "Creation du personnage...");
			
			// caracteristiques du personnage
			HashMap<Caracteristique, Integer> caracts = new HashMap<Caracteristique, Integer>();
			// seule la force n'a pas sa valeur par defaut (exemple)
			if (p=="Crevard"){
				caracts.put(Caracteristique.FORCE, 5);
			} /*else 
			caracts.put(Caracteristique.FORCE, 
					Calculs.valeurCaracAleatoire(Caracteristique.FORCE)); */
			
			Point position = Calculs.positionAleatoireArene();
			
			switch (p){
			case "Pochtron":
				new Pochtron(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
			case "Crevard":
				new Crevard(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
			case "Intello":
				new Intello(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
			case "Kamikaze":
				new Kamikaze(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
			case "Fuyard":
				new Fuyard(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
			case "Soigneur":
				new Soigneur(ipArene, port, ipConsole, nom, groupe, caracts, nbTours, position, logger);
				break;
				
			}
			
			logger.info("Lanceur", "Creation du personnage reussie");
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}



}
