package main;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyApplicationListener;
import fr.dgac.ivy.IvyClient;
import fr.dgac.ivy.IvyException;

public class app {

	public static void main(String[] args) throws IvyException, InterruptedException {
		
		Ivy bus = new Ivy("server", "Palette:CreerRectangle x=50 y=50 couleurFond=255:0:0", null);
		bus.start("127.255.255.255:2010");
		
		Thread.sleep(1000);
		bus.sendMsg("Palette:CreerRectangle x=50 y=50 couleurFond=25:0:0");
		
		
		
		bus.bindMsg("Palette:MouseMoved x=(.*) y=(.*)", (client, args1) -> {
            System.out.println("Le pointeur de la souris se trouve à la position ("+args1[0]+","+args1[1]+")");
        });
		
		
	}

}
