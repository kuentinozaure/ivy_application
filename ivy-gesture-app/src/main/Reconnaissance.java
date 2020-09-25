package main;

import java.awt.geom.Point2D;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JOptionPane;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import fr.irit.ens.$1reco.Stroke;
import fr.irit.ens.$1reco.Strokes;

public class Reconnaissance {
	
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

        double seuil = 50.0;

        bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (client, s) -> {
            try {
                bus.sendMsg("Palette:CreerRectangle x=" + s[0] + " y=" + s[1] + " longueur=1 hauteur=1");
                compteur.addAndGet(1);
            } catch (IvyException e) {
                e.printStackTrace();
            }

            currentStroke.normalize();

            AtomicReference<String> bestStroke = new AtomicReference<>("");
            AtomicReference<Double> bestDistance = new AtomicReference<>((double) 1000);

            strokes.getStrokes().forEach((stroke, s1) -> {
                double distance = currentStroke.calculDistance(stroke);
                if (distance < bestDistance.get()) {
                    bestDistance.set(distance);
                    bestStroke.set(s1);
                }
            });
            System.out.println(bestDistance.get());
            if (bestDistance.get() < seuil) {
                switch (bestStroke.get()) {
                    case "rectangle":
                        try {
                            bus.sendMsg("Palette:CreerRectangle x=10 y=10 longueur=30 hauteur=20");
                        } catch (IvyException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "cercle":
                        try {
                            bus.sendMsg("Palette:CreerEllipse x=10 y=10 longueur=20 hauteur=20");
                        } catch (IvyException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "supprimer":
                        for (int i = 0; i < 10000; i++) {
                            try {
                                bus.sendMsg("Palette:SupprimerObjet nom=R" + i);
                                bus.sendMsg("Palette:SupprimerObjet nom=E" + i);
                            } catch (IvyException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
            }
        });
    }

    
}
