package main;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import fr.irit.ens.$1reco.Stroke;
import fr.irit.ens.$1reco.Strokes;

public class Fusion {


	public static void DrawRectangle(Ivy bus,int x,int y,String color) {
		try {
			System.out.println("dd"+ color);
			if (color.equals("rouge")) {
				bus.sendMsg("Palette:CreerRectangle x="+x+" y="+y+" couleurFond=255:0:0 longueur=100 hauteur=100  ");
			} else if (color.equals("noir")) {
				bus.sendMsg("Palette:CreerRectangle x="+x+" y="+y+" couleurFond=0:0:0 longueur=100 hauteur=100 ");
			} else if (color.equals("jaune")) {
				bus.sendMsg("Palette:CreerRectangle x="+x+" y="+y+" couleurFond=255:255:0 longueur=100 hauteur=100 ");
			} else if (color.equals("default")){
				bus.sendMsg("Palette:CreerRectangle x="+x+" y="+y+" couleurFond=0:128:0 longueur=100 hauteur=100 ");
			}

		} catch (IvyException e) {
			e.printStackTrace();
		}

	}

	public static void DrawCircle(Ivy bus,int x,int y,String color) {
		try {
			if (color.equals("rouge")) {
				bus.sendMsg("Palette:CreerEllipse x="+x+" y="+y+" couleurFond=255:0:0 longueur=100 hauteur=100  ");
			} else if (color.equals("noir")) {
				bus.sendMsg("Palette:CreerEllipse x="+x+" y="+y+" couleurFond=0:0:0 longueur=100 hauteur=100  ");
			} else if (color.equals("jaune")) {
				bus.sendMsg("Palette:CreerEllipse x="+x+" y="+y+" couleurFond=255:255:0 longueur=100 hauteur=100  ");
			} else if (color.equals("default")){
				bus.sendMsg("Palette:CreerEllipse x="+x+" y="+y+" couleurFond=0:128:0 longueur=100 hauteur=100  ");
			}

		} catch (IvyException e) {
			e.printStackTrace();
		}		
	}

	public static String getNameObject(Ivy bus,int x,int y) {
		String[] idToReturn = {""};
		System.out.println(x);
		try {
			bus.sendMsg("Palette:TesterPoint x="+ x +" y="+y);
			bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)",(c, r) -> {
				System.out.println(r[2]);
				if (r[2] != null) {
					idToReturn[0] += r[2];
				}
			});
		} catch (IvyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return idToReturn[0];
	}

	public static void MoveObject (Ivy bus,int x,int y,String name) {
		try {
			bus.sendMsg("Palette:DeplacerObjetRelatif nom="+name+" x="+x+" y="+y);
		} catch (IvyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void DeletionDrawing(Ivy bus,int x,int y) {
		try {
			bus.sendMsg("Palette:TesterPoint x="+x+" y="+y);
			bus.bindMsg("Palette:ResultatTesterPoint x=(.*) y=(.*) nom=(.*)",(c, r) -> {
				try {
					bus.sendMsg("Palette:SupprimerObjet nom="+r[2]);
				} catch (IvyException e) { 
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (IvyException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {

		Strokes strokes = new Strokes();
		AtomicInteger compteur = new AtomicInteger();
		Stroke currentStroke = new Stroke();
		Double seuil = 50.0;
		double[] position = {Double.NaN,Double.NaN};
		String[] choixFigure = {""};
		String[] hereWord = {"ici,la,position"};
		String[] deletionWord = {"objet,rectangle,elispe"};
		String[] positionWord = {"Deplacer cette objet,Deplacer ce rectangle,Deplacer cette elispe"};
		String[] colorWord = {"rouge,jaune,noire"};
		String[] idToMove={""};

		Ivy bus = new Ivy("server", "", null);
		bus.start("127.255.255.255:2010");

		Thread.sleep(1000);
		strokes.load();

		// Detection vocal
		bus.bindMsg("sra5 Text=(.*) Confidence=(.*)", (client, args1) -> {

			System.out.println(args1[0]);

			String couleur = "";
			String ici = "";
			String wordForDelet = "";

			for (String value: args1[0].split(" ")) { 
				// decoupe le string recu et compare au tableau de string des mots
				if (hereWord[0].contains(value)) {
					ici += " "+value;
				}
				if(colorWord[0].contains(value)) {
					couleur = value;
				} else {
					couleur ="default";
				}
				if(deletionWord[0].contains(value)) {
					wordForDelet += " "+value;
				}
			}

			System.out.println(wordForDelet+" , "+ici);

			// position + color && postion without color
			if (!ici.equals("") && wordForDelet.equals("")
					&& !Double.isNaN(position[0]) 
					&& !Double.isNaN(position[1])) {

				System.out.println("Adding form ...");
				// creation de la forme avec la position
				// si il dit ici et une couleur dans n'importe qu elle sens
				if (choixFigure[0].equals("rectangle")) {
					DrawRectangle(bus,(int)position[0],(int)position[1],couleur);
				}
				else {
					DrawCircle(bus,(int)position[0],(int)position[1],couleur);
				}
				return;
			}


			// To do deletion with color
			// Word for deletion
			if (!wordForDelet.equals("") && ici.equals("")
					&& !Double.isNaN(position[0]) 
					&& !Double.isNaN(position[1])) {
				System.out.println("Deleting form ...");
				// Suppression de l'element avec le mot magique x)
				// si il dit cette objet ca le supprime
				DeletionDrawing(bus,(int)position[0],(int)position[1]);
				return;
			}

			if(!wordForDelet.equals("") && !idToMove[0].equals("");
										&& !ici.equals("")
										&& !Double.isNaN(position[0]) 
										&& !Double.isNaN(position[1])) {
				
				MoveObject(bus,(int)position[0],(int)position[1], idToMove[0]);
				System.out.println("Moving form ...");
				System.out.println("dd: "+idToMove[0]);
				return;
			}


			/*
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
			}*/

		});

		// Detection des formes
		bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (client, s) -> { 

			position[0] = Double.parseDouble(s[0]);
			position[1] = Double.parseDouble(s[1]);

			idToMove[0] = getNameObject(bus, (int)Double.parseDouble(s[0]), (int)Double.parseDouble(s[1]));

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
