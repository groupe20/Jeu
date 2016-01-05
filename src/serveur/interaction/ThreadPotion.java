package serveur.interaction;

import java.rmi.RemoteException;

import serveur.Arene;
import serveur.element.Potion;
import utilitaires.Calculs;

public class ThreadPotion extends Thread {
	
	private Arene a;
	private Potion p;
	
	public ThreadPotion(String name,Arene a,Potion p){
		super(name);
		this.a=a;
		this.p=p;
	}
	
	public void run(){
		try {
			this.a.ajoutePotion(this.p,Calculs.positionAleatoireArene());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
