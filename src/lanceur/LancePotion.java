package lanceur;

import java.io.IOException;
import java.util.HashMap;


import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class LancePotion {
	
	private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		String nom = "Anduril";
		
		String groupe = "1"; 
	
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
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
			logger = new LoggerProjet(true, "potion_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));

			logger.info("Lanceur", "Lancement de la potion sur le serveur...");
			
			// caracteristiques de la potion
			HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();
			
			Potion potion = null;
			int lower = 0;
			int higher = 4;

			int typePotion = (int)(Math.random() * (higher-lower)) + lower;

			switch(typePotion){
			case 0 :caractsPotion.put(Caracteristique.VIE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.VIE));
					caractsPotion.put(Caracteristique.FORCE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.FORCE));
					caractsPotion.put(Caracteristique.INITIATIVE, Calculs.valeurCaracAleatoirePosNeg(Caracteristique.INITIATIVE)); 
					potion=new Potion("basic", groupe, caractsPotion);
					break;
			
			case 1 : caractsPotion.put(Caracteristique.VIE, -100);
					 caractsPotion.put(Caracteristique.FORCE, 0);
					 caractsPotion.put(Caracteristique.INITIATIVE, 0); 
					 potion=new Potion("mortelle", groupe, caractsPotion);
					 break;
					 
			case 2 : caractsPotion.put(Caracteristique.VIE, 0);
					 caractsPotion.put(Caracteristique.FORCE, 0);
					 caractsPotion.put(Caracteristique.INITIATIVE, 0); 
					 potion=new Potion("teleportation", groupe, caractsPotion);
					 break;
					 
			case 3 : caractsPotion.put(Caracteristique.VIE, 0);
			 		 caractsPotion.put(Caracteristique.FORCE, 0);
			 		 caractsPotion.put(Caracteristique.INITIATIVE, 0); 
			 		 potion=new Potion("immobilité", groupe, caractsPotion);
			 		 break;
			}
			
			
			// ajout de la potion
			arene.ajoutePotion(potion, Calculs.positionAleatoireArene());
			logger.info("Lanceur", "Lancement de la potion reussi");
			
		} catch (Exception e) {
			logger.severe("Lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
