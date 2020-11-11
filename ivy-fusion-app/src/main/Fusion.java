package main;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

public class Fusion {

	public static void main(String[] args) throws Exception {		
		Ivy bus = new Ivy("server", "Palette:CreerRectangle x=50 y=50 couleurFond=255:0:0", null);
		bus.start("127.255.255.255:2010");
		
		Thread.sleep(1000);
		
		
		
		bus.bindMsg("sra5 Text=(.*) Confidence=(.*)", (client, args1) -> {
			//String text = args1[0].replaceAll("\.+", "").trim();
            System.out.println("Le pointeur de la souris se trouve à la position ("+args1[0]+")");
        });
		
	}

}
