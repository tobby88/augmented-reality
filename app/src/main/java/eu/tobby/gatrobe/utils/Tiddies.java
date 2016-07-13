package eu.tobby.gatrobe.utils;

import android.app.Activity;

import java.util.Vector;

/**
 * Tits class to add new tits with additional information in a single process
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 */
public class Tiddies {

    // Number of titpics for this tits
    private final int titpics;
    // (Marker-) ID for the tits (unique!)
    private final int id;
    // Scale factors for the resize of the rendered plane
    private final float kPlaneScaleX;
    private final float kPlaneScaleY;
    private Vector<Texture> textures = new Vector<>();
    private int currentTitpic = 1;

    /**
     * Constructor for Tits
     * @param activity: current activity
     * @param id: unique id
     * @param imageSizeX: Size of Image in Scene Units in X-direction
     * @param imageSizeY: Size of Image in Scene Units in Y-direction
     * @param titpics: Number of titpics, which can be shown
     */
    public Tiddies(Activity activity, int id, float imageSizeX, float imageSizeY, int titpics) {
        this.titpics = titpics;
        this.id = id;
        // Calculate the resize and translation parameters
        kPlaneScaleX = imageSizeX/10.0f;
        kPlaneScaleY = imageSizeY/10.0f;
        // Load the textures
        for (int i = 1; i <= titpics; i++) {
            textures.add(Texture.loadTextureFromApk("tits/tits" + id + "_pic" + i + ".jpg", activity.getAssets()));
        }
    }

    /**
     * Setter method for the current titpic
     * @param titpic: Number of titpic, which should be shown
     */
    public void setCurrentTitpic(int titpic) {
        currentTitpic = titpic;
    }

    /**
     * Add a new titpic
     */
    public void addTitpic() {
        currentTitpic++;
        if (currentTitpic > titpics) {
            currentTitpic = 1;
        }
    }

    /**
     * Getter method for the number of titpics
     * @return: titpics
     */
    public int getTitpics() {
        return titpics;
    }

    /**
     * Getter method for current Texture
     * @return: current Texture
     */
    public Texture getCurrentTexture() {
        return textures.elementAt(currentTitpic - 1);
    }

    /**
     * Getter method for the current unique ID of the Marker
     * @return: current ID
     */
    public int getID() {
        return id;
    }

    /**
     * Getter method for the rendering plane scale in X-Direction
     * @return: X-Scale of the rendering plane
     */
    public float getPlaneX() { return kPlaneScaleX;}

    /**
     * Getter method for the rendering plane scale in Y-Direction
     * @return: Y-Scale of the rendering plane
     */
    public float getPlaneY() { return kPlaneScaleY;}

}
