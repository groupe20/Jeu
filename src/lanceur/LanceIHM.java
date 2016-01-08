package lanceur;

import java.io.IOException;

import interfacegraphique.IHM;
import logger.LoggerProjet;
import utilitaires.Constantes;

/**
 * Interface graphique qui se connecte a l'arene. 
 * A lancer apres l'arene.
 * Arguments : numero de port et adresse IP de l'arene.
 */
public class LanceIHM {
	
	private static String usage = "USAGE : java " + LanceIHM.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		int port = Constantes.PORT_DEFAUT;
		String ipArene = /*"130.120.250.20";*/Constantes.IP_DEFAUT;
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				ErreurLancement.aide(usage);
			}
			
			if (args.length > 2) {
				ErreurLancement.TROP_ARGS.erreur(usage);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				ErreurLancement.PORT_NAN.erreur(usage);
			}
			
			if (args.length > 1) {
				ipArene = args[1];
			}
		}
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true,"IHM");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		try {
			logger.info("Lanceur", "Creation de l'IHM...");
			IHM ihm = new IHM(port, ipArene, logger);
			logger.info("Lanceur", "Creation de l'IHM reussie");

			logger.info("Lanceur", "Connexion de l'IHM au serveur...");
			ihm.connecte();
			logger.info("Lanceur", "Connexion de l'IHM au serveur reussie");
			ihm.start();
			logger.info("Lanceur", "Mise en route de l'IHM reussie");
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n"+e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
