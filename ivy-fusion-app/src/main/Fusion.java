package main;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import fr.irit.ens.$1reco.Stroke;
import fr.irit.ens.$1reco.Strokes;

public class Fusion {
	
	public static void main(String[] args) throws Exception {
		
		Strokes strokes = new Strokes();
        AtomicInteger compteur = new AtomicInteger();
        Stroke currentStroke = new Stroke();
        Double seuil = 50.0;
        double[] position = {Double.NaN,Double.NaN};
        String[] choixFigure = {""};
        String[] hereWords = {"ici,la,a cette position"};
        String[] deletionWord = {"Supprimer cette objet,Supprimer ce rectangle,Supprimer cette elispe"};
        String[] positionWord = {"Deplacer cette objet,Deplacer ce rectangle,Deplacer cette elispe"};
        String[] colorWord = {"rouge,vert,noire"};
        String[] id={""};
        
		Ivy bus = new Ivy("server", "", null);
		bus.start("127.255.255.255:2010");
		
		Thread.sleep(1000);
		strokes.load();
		
		// Detection vocal
		bus.bindMsg("sra5 Text=(.*) Confidence=(.*)", (client, args1) -> {
			
			
			// if user say here or other word
			if (hereWords[0].contains(args1[0])) {
				// choice figure between rectangle and circle
				if (choixFigure[0].equals("rectangle")) {
					try {
						bus.sendMsg("Palette:CreerRectangle x="+(int)position[0]+" y="+(int)position[1]+" longueur=100 hauteur=100");
					} catch (IvyException e) {
						e.printStackTrace();
					}
				} else {
					try {
						bus.sendMsg("Palette:CreerEllipse x="+(int)position[0]+" y="+(int)position[1]+" longueur=100 hauteur=100");
					} catch (IvyException e) {
						e.printStackTrace();
					}
				}
				choixFigure[0] = "";
				position[0] = Double.NaN;
				position[1] = Double.NaN;
			}
			
			if (deletionWord[0].contains(args1[0])) {
				try {
					bus.sendMsg("Palette:TesterPoint x="+(int)position[0]+" y="+(int)position[1]);
					bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)",(c, r) -> {
						try {
							bus.sendMsg("Palette:SupprimerObjet nom="+r[2]);
						} catch (IvyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
			if(positionWord[0].contains(args1[0])) {
				try {
					bus.sendMsg("Palette:TesterPoint x="+(int)position[0]+" y="+(int)position[1]);
					bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)",(c, r) -> {
						
						id[0]=r[2];
						try {
							Thread.sleep(1000);
							System.out.println(id[0]);
							bus.sendMsg("Palette:DeplacerObjetRelatif nom="+id[0]+" x="+(int)position[0]+" y="+(int)position[1]);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});
					
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
	
		// Detection des formes
        bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (client, s) -> { 
        		
        	System.out.println(s[0] +" "+ s[1]);
	        	position[0] = Double.parseDouble(s[0]);
	        	position[1] = Double.parseDouble(s[1]);
        	
	        	currentStroke.init();
	            for(int i = 0 ;i < compteur.get(); i++) {
	            }
				compteur.addAndGet(1);
				currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        });
        
        // detection des formes
        bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (client, s) -> {
			compteur.addAndGet(1);
            currentStroke.addPoint(Integer.parseInt(s[0]), Integer.parseInt(s[1]));
        });

        bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (client, s) -> {
        	if(Double.parseDouble(s[0]) == position[0] && Double.parseDouble(s[1]) == position[1]) {
        		
        	} else {
    			compteur.addAndGet(1);
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
                if (bestDistance.get() < seuil) {
                    switch (bestStroke.get()) {
                        case "carre":
                                choixFigure[0] = "rectangle";
                            break;
                        case "rond":
                                choixFigure[0] = "rond";
                            break;
                    }
                }
                
                System.out.println("La forme choisie est le "+choixFigure[0]);
        	}
        });
	}

}
