package interfacegraphique.interfacesimple.components;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import serveur.vuelement.VuePersonnage;

/**
 * Panel de victoire.
 *
 */
public class VictoryScreen extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Nom du vainqueur a afficher.
	 */
	private String nom;

	/**
	 * Couleur du vainqueur.
	 */
	private String type;

	/**
	 * Cree un ecran de victoire.
	 * @param vue vue du personnage gagnant
	 */
	public VictoryScreen(VuePersonnage vue) {
		nom = vue.getElement().getNom();
		type = vue.getElement().getType();
	}

	@Override
	public void paintComponent(Graphics g) {
		// dessine un rectangle noir transparent de la taille de la fenetre
		g.setColor(new Color(0, 0, 0, 0.5f));
		g.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		// dessine le disque de la couleur du gagnant puis ajoute l'image par dessus
		
		
		//g.setColor(new Color(color.getRGB(), false));
		//g.fillOval(240, 160, 200, 200);
		
		Image vainqueur=null;
		
		switch(this.type){
		
		case "Pochtron" : try {
				vainqueur = ImageIO.read(new File("images/pochtron(portrait).png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}break;
		case "Intello" : try {
				vainqueur = ImageIO.read(new File("images/intello(portrait).png"));
			} catch (IOException e7) {
				// TODO Auto-generated catch block
				e7.printStackTrace();
			}break;
		case "Kamikaze" : try {
				vainqueur = ImageIO.read(new File("images/kamikaze(portrait).png"));
			} catch (IOException e6) {
				// TODO Auto-generated catch block
				e6.printStackTrace();
			}break;
		case "Soigneur" : try {
				vainqueur = ImageIO.read(new File("images/docteur(portrait).png"));
			} catch (IOException e5) {
				// TODO Auto-generated catch block
				e5.printStackTrace();
			}break;
		case "Fuyard" : try {
				vainqueur = ImageIO.read(new File("images/fuyard(portrait).png"));
			} catch (IOException e4) {
				// TODO Auto-generated catch block
				e4.printStackTrace();
			}break;
		case "Crevard" : try {
				vainqueur = ImageIO.read(new File("images/criminal24.png"));
			} catch (IOException e3) {
				// TODO Auto-generated catch block
				e3.printStackTrace();
			}break;
		}
		g.drawImage(vainqueur, 280, 200, null);

		Image logo;
		try {
			logo = ImageIO.read(new File("images/WinScreen.png"));
			g.drawImage(logo, 50, 0, null);

		} catch (IOException e) {
			e.printStackTrace();
		}

		// ajuste l'emplacement et la taille du nom en fonction du nombre de caracteres
		Font of = g.getFont();
		g.setColor(new Color(250, 179, 98));
		if (nom.length() >= 7) {
			g.setFont(new Font("Helvetica", Font.BOLD, 75));
			g.drawString(nom, 325 - (nom.length() * 18), 450);
		} else {
			g.setFont(new Font("Helvetica", Font.BOLD, 100));
			g.drawString(nom, 325 - (nom.length() * 25), 450);
			g.setFont(of);
		}
	}
}
