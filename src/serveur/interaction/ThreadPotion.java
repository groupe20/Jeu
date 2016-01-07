package serveur.interaction;

import java.rmi.RemoteException;

import java.awt.Point;

import serveur.Arene;
import serveur.element.Potion;

public class ThreadPotion extends Thread {
	
	private Arene a;
	private Potion p;
	private Point pos;
	
	public ThreadPotion(String name,Arene a,Potion p, Point pos){
		super(name);
		this.a=a;
		this.p=p;
		this.pos=pos;
	}
	
	public void run(){
		try {
			this.a.ajoutePotion(this.p,this.pos);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
