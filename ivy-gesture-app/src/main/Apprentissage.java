package main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.swing.JOptionPane;

import fr.dgac.ivy.Ivy;
import fr.dgac.ivy.IvyException;
import fr.irit.ens.$1reco.Stroke;

public class app {
	
	private static Stroke stroke;
	private static HashMap<Stroke,String> hashMapStroke;
	
	public static void main(String[] args) throws IvyException, InterruptedException {
		Ivy bus = new Ivy("server", "Hello, im Ivy", null);
		bus.start("127.255.255.255:2010");
		Thread.sleep(1000);
		
		hashMapStroke = new HashMap();
		
		
		bus.bindMsg("Palette:MousePressed x=(.*) y=(.*)", (client, args1) -> {
			try {
				stroke = new Stroke();
				stroke.addPoint(Integer.parseInt(args1[0]), Integer.parseInt(args1[1]));
				bus.sendMsg("Palette:CreerEllipse x="+args1[0]+" y="+args1[1]+" longueur=4 hauteur=4 couleurFond=0:255:0");
			
			} catch (IvyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("("+args1[0]+","+args1[1]+")");
        });
		
		bus.bindMsg("Palette:MouseReleased x=(.*) y=(.*)", (client, args1) -> {
			try {
				stroke.addPoint(Integer.parseInt(args1[0]), Integer.parseInt(args1[1]));
				bus.sendMsg("Palette:CreerEllipse x="+args1[0]+" y="+args1[1]+" longueur=4 hauteur=4 couleurFond=255:0:0");
			
				stroke.normalize();
				
				String cmd = JOptionPane.showInputDialog(null,"Nom associé");
				
				hashMapStroke.put(stroke, cmd);
				SerialisationHashMap();
				
				for( java.awt.geom.Point2D.Double value : stroke.getPoints() ) {
					System.out.println(value);
		            bus.sendMsg("Palette:CreerEllipse x="+(int)value.getX()+" y="+(int)value.getY()+" longueur=4 hauteur=4 couleurFond=0:0:255");
		        }
				
			} catch (IvyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("release("+args1[0]+","+args1[1]+")");
        });
		
		bus.bindMsg("Palette:MouseDragged x=(.*) y=(.*)", (client, args1) -> {
			try {
				stroke.addPoint(Integer.parseInt(args1[0]), Integer.parseInt(args1[1]));
				bus.sendMsg("Palette:CreerEllipse x="+args1[0]+" y="+args1[1]+" longueur=4 hauteur=4");
			} catch (IvyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("dragged("+args1[0]+","+args1[1]+")");
        });
	}
	
	
	public static void SerialisationHashMap() {
		 try {
	           FileOutputStream fileOutputStream = new FileOutputStream("stroke-file");
	           ObjectOutputStream objectOutputStream 
	                    = new ObjectOutputStream(fileOutputStream);

	           objectOutputStream.writeObject(hashMapStroke);

	           objectOutputStream.close();
	           fileOutputStream.close();
	       } catch (IOException e) {
	           e.printStackTrace();
	       }
	}
	

}
