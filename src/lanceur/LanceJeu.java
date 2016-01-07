/**
 * 
 */
package lanceur;

import utilitaires.Audio;

public class LanceJeu {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LanceArene.main(args);
		LanceIHM.main(args);
		
		LancePersonnage.main("Intello", "i", "1");
		LancePersonnage.main("Fuyard", "f", "1");
		LancePersonnage.main("Kamikaze", "k", "2");	
		LancePersonnage.main("Kamikaze", "k", "2");
		LancePersonnage.main("Kamikaze", "k", "3");
		LancePersonnage.main("Pochtron", "p", "3");
		LancePersonnage.main("Crevard", "c", "4");
		LancePersonnage.main("Intello", "i", "4");


		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		
		new Audio().start();



	}

}
