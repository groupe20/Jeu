package interfacegraphique.interfacesimple.components;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panneau permettant de dessiner le portrait d'un element.
 *
 */
public class Portrait extends JPanel {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Couleur a afficher.
	 */
	
	/**
	 * Vrai si on fait le portrait d'un personnage.
	 */
	private String personnage;
	
	/**
	 * Cree le portrait d'un element.
	 * @param c couleur
	 * @param personnage vrai si on dessine un personnage
	 */
	public Portrait( String personnage) {
		this.personnage = personnage;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		Image img=null;
		
		switch(this.personnage){
		
		case "Pochtron" : try {
				img = ImageIO.read(new File("images/pochtron(portrait).png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}break;
		case "Intello" : try {
				img = ImageIO.read(new File("images/intello(portrait).png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}break;
		case "Kamikaze" : try {
				img = ImageIO.read(new File("images/kamikaze(portrait).png"));
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}break;
		case "Soigneur" : try {
				img = ImageIO.read(new File("images/docteur(portrait).png"));
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}break;
		case "Fuyard" : try {
				img = ImageIO.read(new File("images/fuyard(portrait).png"));
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
			}break;
		case "other" :	try {
				img = ImageIO.read(new File("images/potion(portrait).png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}break;
		case "mortelle" : try {
				img = ImageIO.read(new File("images/mortelle(portrait).png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}break;
		case "Crevard" : try {
				img = ImageIO.read(new File("images/criminal24.png"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}break;
		
		}
	g.drawImage(img, 10 , 10 , null);
	}
}
