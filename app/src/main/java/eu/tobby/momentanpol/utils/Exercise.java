package eu.tobby.momentanpol.utils;

import android.app.Activity;

import java.util.Vector;

/**
 * Exercise class to add new exercises with additional information in a single process
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 */
public class Exercise {

    // Number of steps (images) for the exercise
    private final int steps;
    // (Marker-) ID for the exercise (unique!)
    private final int id;
    // Actual parameters for the resize and translation in the camera image (calculated in the constructor)
    private final float kLetterScaleX;
    private final float kLetterScaleY;
    private final float kLetterTranslateX;
    private final float kLetterTranslateY;
    // Scale factors for the resize of the rendered plane
    private final float kPlaneScaleX;
    private final float kPlaneScaleY;
    private Vector<Texture> textures = new Vector<>();
    private int currentStep = 1;

    /**
     * Constructor for Exercises
     * @param activity: current activity
     * @param id: unique id
     * @param imageSizeX: Size of Image in Scene Units in X-direction
     * @param imageSizeY: Size of Image in Scene Units in Y-direction
     * @param markerSize: Size of a Frame Marker square
     * @param offsetX: Difference between the top left corners of Frame Marker and image in X-Direction
     * @param offsetY: Difference between the top left corners of Frame Marker and image in Y-Direction
     * @param steps: Number of steps, which can be shown
     */
    public Exercise(Activity activity, int id, float imageSizeX, float imageSizeY, float markerSize, float offsetX, float offsetY, int steps) {
        this.steps = steps;
        this.id = id;
        // Calculate the resize and translation parameters
        kLetterScaleX = imageSizeX * markerSize / 500.0f;
        kLetterScaleY = imageSizeY * markerSize / 500.0f;
        kPlaneScaleX = imageSizeX/10.0f;
        kPlaneScaleY = imageSizeY/10.0f;
        kLetterTranslateX = offsetX + (imageSizeX - markerSize) / 2.0f;
        kLetterTranslateY = offsetY - (imageSizeY - markerSize) / 2.0f;
        // Load the textures
        for (int i = 1; i <= steps; i++) {
            textures.add(Texture.loadTextureFromApk("solutions/Exercise" + id + "_step" + i + ".png", activity.getAssets()));
        }
    }

    /**
     * Setter method for the possible steps of the solution
     * @param step: Number of steps, which can be shown
     */
    public void setCurrentStep(int step) {
        currentStep = step;
    }

    /**
     * Add a new Step
     */
    public void addStep() {
        currentStep++;
        if(currentStep>steps){
            currentStep=1;
        }
    }

    /**
     * Getter method for the number of steps
     * @return: steps
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Getter method for X-Scale
     * @return: value for the X-Scale
     */
    public float getScaleX() {
        return kLetterScaleX;
    }

    /**
     * Getter method for Y-Scale
     * @return: value for the Y-Scale
     */
    public float getScaleY() {
        return kLetterScaleY;
    }

    /**
     * Getter method for X-Translation
     * @return: value for X-Translation
     */
    public float getTranslateX() {
        return kLetterTranslateX;
    }

    /**
     * Getter method for Y-Translation
     * @return: value for Y-Translation
     */
    public float getTranslateY() {
        return kLetterTranslateY;
    }

    /**
     * Getter method for current Texture
     * @return: current Texture
     */
    public Texture getCurrentTexture() {
        return textures.elementAt(currentStep - 1);
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
