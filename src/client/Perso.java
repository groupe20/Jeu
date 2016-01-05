package client;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import client.controle.Console;
/**
 * Strategie d'un personnage
 */
public abstract class Perso {
    
    /**
     * Console permettant d'ajouter une phrase et de recuperer le serveur 
     * (l'arene).
     */
    protected Console console;
    /**
     * Cree un personnage, la console associe et sa strategie.
     * @param ipArene ip de communication avec l'arene
     * @param port port de communication avec l'arene
     * @param ipConsole ip de la console du personnage
     * @param nom nom du personnage
     * @param groupe groupe d'etudiants du personnage
     * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
     * @param position position initiale du personnage dans l'arene
     * @param logger gestionnaire de log
     */
   
    
    /** 
     * Decrit la strategie.
     * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
     * de Arene et de ConsolePersonnage. 
     * @param voisins element voisins de cet element (elements qu'il voit)
     * @throws RemoteException
     */
    public abstract void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException;
    
}