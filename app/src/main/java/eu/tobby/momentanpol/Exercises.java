package eu.tobby.momentanpol;

import android.app.Activity;

import java.util.Vector;

import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by tobby on 08.07.15.
 */
public final class Exercises {

    // Right hand coordinate system: x to the right, y going up, z pointing towards the viewer
    // Size of the exercises in mm, comma separated
    private static final float[] imageSizeX = {147.5f};
    private static final float[] imageSizeY = {115.2f};
    // Offset/distance of the exercises (upper left corners) in mm
    private static final float[] offsetX = {1.0f};
    private static final float[] offsetY = {-53.0f};
    // Define the number of steps (images) for each exercise
    private static final int[] steps = {1};
    // Define the (marker-) ID for each exercise (unique!)
    private static final int[] ids = {4};
    // Actual parameters for the resize and translation in the camera image (will be calculated in the constructor)
    private final float[] kLetterScaleX;
    private final float[] kLetterScaleY;
    private final float[] kLetterTranslateX;
    private final float[] kLetterTranslateY;
    // Edge length of the marker in mm on each exercise sheet
    private float[] markerSize = {50.0f};
    private Vector<Texture>[] textures;
    private int[] currentStep;
    private Activity activity;


    public Exercises(Activity activity) {
        this.activity = activity;
        // Use temp-arrays because the "final" kLetter...-arrays can only be written once
        float[] tempScaleX = new float[ids.length];
        float[] tempScaleY = new float[ids.length];
        float[] tempTranslateX = new float[ids.length];
        float[] tempTranslateY = new float[ids.length];
        // Calculate the resize and translation parameters
        for (int i = 0; i < ids.length; i++) {
            tempScaleX[i] = imageSizeX[i] * markerSize[i] / 500.0f;
            tempScaleY[i] = imageSizeY[i] * markerSize[i] / 500.0f;
            tempTranslateX[i] = offsetX[i] + (imageSizeX[i] - markerSize[i]) / 2.0f;
            tempTranslateY[i] = offsetY[i] - (imageSizeY[i] - markerSize[i]) / 2.0f;
        }
        // Now write these arrays to the final class members
        kLetterScaleX = tempScaleX;
        kLetterScaleY = tempScaleY;
        kLetterTranslateX = tempTranslateX;
        kLetterTranslateY = tempTranslateY;
        // Load the textures and set current step to 1
        currentStep = new int[ids.length];
        textures = new Vector[ids.length];
        for (int i = 0; i < ids.length; i++) {
            textures[i] = new Vector<>();
            currentStep[i] = 1;
            for (int j = 1; j <= steps[i]; j++) {
                textures[i].add(Texture.loadTextureFromApk("solutions/Exercise" + ids[i] + "_step" + j + ".png", activity.getAssets()));
            }
        }
    }

    // Find an exercise (its array index) by a given ID
    private int findExercise(int id) {
        int index = -1;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i] == id)
                index = i;
        }
        return index;
    }


    public void setCurrentStep(int id, int step) {
        int index = findExercise(id);
        currentStep[index] = step;
    }


    public int getSteps(int id) {
        int index = findExercise(id);
        return steps[index];
    }


    public float getScaleX(int id) {
        int index = findExercise(id);
        return kLetterScaleX[index];
    }


    public float getScaleY(int id) {
        int index = findExercise(id);
        return kLetterScaleY[index];
    }


    public float getTranslateX(int id) {
        int index = findExercise(id);
        return kLetterTranslateX[index];
    }


    public float getTranslateY(int id) {
        int index = findExercise(id);
        return kLetterTranslateY[index];
    }


    public Texture getCurrentTexture(int id) {
        int index = findExercise(id);
        return textures[index].elementAt(currentStep[index] - 1);
    }


    public int getNrOfExercises() {
        return ids.length;
    }


    public int getID(int index) {
        return ids[index];
    }

}
