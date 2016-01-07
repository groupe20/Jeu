/**
 * 
 */
package lanceur;



public class LanceJeu {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		LanceArene.main(args);
		LanceIHM.main(args);
		
		/*LancePersonnage.main("Fuyard", "f", "1");
		LancePersonnage.main("Kamikaze", "k", "1");
		LancePersonnage.main("Pochtron", "p", "1");
		LancePersonnage.main("Soigneur", "d", "1");
		LancePersonnage.main("Crevard", "c", "1");*/
		LancePersonnage.main("Intello", "i", "1");
		

		LancePersonnage.main("Fuyard", "f3", "3");


		LancePotion.main(args);



	}

}
