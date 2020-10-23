package main;

import javax.swing.JOptionPane;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import fr.irit.ens.$1reco.*;

public class Geste {

	public static void main(String[] args) throws IvyException, InterruptedException {
		
        Ivy bus = new Ivy("agent", "coucou", null);
        bus.start("127.255.255.255:2010");
        
        Thread.sleep(1000);
        
        
        Stroke currentStroke = new Stroke();

        bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (client, s) -> {
            currentStroke.init();
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
            } catch (IvyException e) {
                e.printStackTrace();
            }
            currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));

        });
        bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (client, s) -> {
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
            } catch (IvyException e) {
                e.printStackTrace();
            }
            currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        });

        bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (client, s) -> {
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
            } catch (IvyException e) {
                e.printStackTrace();
            }

            currentStroke.normalize();
        });
        
    }


}
