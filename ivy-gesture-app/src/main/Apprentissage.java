package main;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicInteger;

import fr.irit.ens.$1reco.*;


public class Apprentissage {

    public static void main(String[] args) throws IvyException {
	// write your code here

        Strokes strokes = new Strokes();
        strokes.load();

        AtomicInteger compteur = new AtomicInteger();

        System.out.println("START");

        Ivy bus = new Ivy("agent", "coucou", null);

        bus.start("127.255.255.255:2010");

        Stroke currentStroke = new Stroke();

        bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (client, s) -> {
            currentStroke.init();
            for(int i = 0 ;i < compteur.get(); i++) {
                try {
                    bus.sendMsg("Palette:SupprimerObjet nom=R" + i);
                } catch (IvyException e) {
                    e.printStackTrace();
                }
            }
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
                compteur.addAndGet(1);
            } catch (IvyException e) {
                e.printStackTrace();
            }
            currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));

        });
        bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (client, s) -> {
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
                compteur.addAndGet(1);
            } catch (IvyException e) {
                e.printStackTrace();
            }
            currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        });

        bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (client, s) -> {
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
                compteur.addAndGet(1);
            } catch (IvyException e) {
                e.printStackTrace();
            }

            currentStroke.normalize();

            String name = JOptionPane.showInputDialog(null, "Quel nom ?");
            if (name != null) {
                strokes.addStroke(currentStroke, name);
                strokes.save();
            }
        });
    }
}
