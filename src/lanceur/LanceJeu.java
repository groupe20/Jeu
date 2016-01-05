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
		
		LancePersonnage.main("Fuyard", "fuyard", "1");
		LancePersonnage.main("Kamikaze", "kami", "1");
		LancePersonnage.main("Pochtron", "pochtron", "1");
		/*LancePersonnage.main("Soigneur", "doc", "1");
		LancePersonnage.main("Soigneur", "doc", "1");
		LancePersonnage.main("Soigneur", "doc", "1");
		LancePersonnage.main("Soigneur", "doc", "1");
		LancePersonnage.main("Soigneur", "doc", "1");
		LancePersonnage.main("Soigneur", "doc", "1");*/

		
		LancePersonnage.main("Fuyard", "fuyard2", "2");
		LancePersonnage.main("Kamikaze", "kami2", "2");
		LancePersonnage.main("Pochtron", "pochtron2", "2");
		LancePersonnage.main("StrategiePersonnage", "strat2", "2");



		
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);
		LancePotion.main(args);


	}

}
