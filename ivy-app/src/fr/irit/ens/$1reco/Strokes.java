package fr.irit.ens.$1reco;

import java.io.*;
import java.util.HashMap;

public class Strokes implements Serializable {

    private HashMap<Stroke, String> strokes;

    private static final String filepath="stroke-file";

    public Strokes(HashMap<Stroke, String> strokes) {
        this.strokes = strokes;
    }

    public Strokes() {
        this.strokes = new HashMap<>();
    }

    public void addStroke(Stroke stroke, String name) {
        this.strokes.put(new Stroke(stroke), name);
    }

    public HashMap<Stroke, String> getStrokes() {
        return strokes;
    }

    public void save() {
        try {
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this.strokes);
            objectOut.close();
            System.out.println("The Object  was succesfully written to a file");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        try {

            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            this.strokes = (HashMap<Stroke, String>) objectIn.readObject();

            System.out.println("The Object has been read from the file");
            objectIn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        this.strokes.forEach((stroke, s) -> System.out.println(s));
    }
}
