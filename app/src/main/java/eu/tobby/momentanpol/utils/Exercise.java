package eu.tobby.momentanpol.utils;

import android.app.Activity;

import java.util.Vector;

/**
 * Created by tobby on 09.07.15.
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
    private Vector<Texture> textures = new Vector<>();
    private int currentStep = 1;

    public Exercise(Activity activity, int id, float imageSizeX, float imageSizeY, float markerSize, float offsetX, float offsetY, int steps) {
        this.steps = steps;
        this.id = id;
        // Calculate the resize and translation parameters
        kLetterScaleX = imageSizeX * markerSize / 500.0f;
        kLetterScaleY = imageSizeY * markerSize / 500.0f;
        kLetterTranslateX = offsetX + (imageSizeX - markerSize) / 2.0f;
        kLetterTranslateY = offsetY - (imageSizeY - markerSize) / 2.0f;
        // Load the textures
        for (int i = 1; i <= steps; i++) {
            textures.add(Texture.loadTextureFromApk("solutions/Exercise" + id + "_step" + i + ".png", activity.getAssets()));
        }
    }


    public void setCurrentStep(int step) {
        currentStep = step;
    }


    public void addStep() {
        currentStep++;
        if(currentStep>steps){
            currentStep=1;
        }
    }


    public int getSteps() {
        return steps;
    }


    public float getScaleX() {
        return kLetterScaleX;
    }


    public float getScaleY() {
        return kLetterScaleY;
    }


    public float getTranslateX() {
        return kLetterTranslateX;
    }


    public float getTranslateY() {
        return kLetterTranslateY;
    }


    public Texture getCurrentTexture() {
        return textures.elementAt(currentStep - 1);
    }


    public int getID() {
        return id;
    }

}
