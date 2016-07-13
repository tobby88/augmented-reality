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
public class BooobObj {

    // Number of pics for this object
    private final int pics;
    // (Marker-) ID for the booob-object (unique!)
    private final int id;
    // Scale factors for the resize of the rendered plane
    private final float kPlaneScaleX;
    private final float kPlaneScaleY;
    private Vector<Texture> textures = new Vector<>();
    private int currentPic = 1;

    /**
     * Constructor for BooobObj
     * @param activity: current activity
     * @param id: unique id
     * @param imageSizeX: Size of Image in Scene Units in X-direction
     * @param imageSizeY: Size of Image in Scene Units in Y-direction
     * @param pics: Number of pics, which can be shown
     */
    public BooobObj(Activity activity, int id, float imageSizeX, float imageSizeY, int pics) {
        this.pics = pics;
        this.id = id;
        // Calculate the resize and translation parameters
        kPlaneScaleX = imageSizeX/10.0f;
        kPlaneScaleY = imageSizeY/10.0f;
        // Load the textures
        for (int i = 1; i <= pics; i++) {
            textures.add(Texture.loadTextureFromApk("t/t" + id + "_p" + i + ".gat", activity.getAssets()));
        }
    }

    /**
     * Setter method for the current pic
     * @param pic: Number of pic, which should be shown
     */
    public void setCurrentPic(int pic) {
        currentPic = pic;
    }

    /**
     * Add a new pic
     */
    public void addPic() {
        currentPic++;
        if (currentPic > pics) {
            currentPic = 1;
        }
    }

    /**
     * Getter method for the number of pics
     * @return: pics
     */
    public int getPics() {
        return pics;
    }

    /**
     * Getter method for current Texture
     * @return: current Texture
     */
    public Texture getCurrentTexture() {
        return textures.elementAt(currentPic - 1);
    }

    /**
     * Getter method for the current unique ID
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
