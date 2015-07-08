package eu.tobby.momentanpol.exercises;

import android.app.Activity;

import java.util.Vector;

import eu.tobby.momentanpol.interfaces.ExerciseSheet;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by tobby on 08.07.15.
 */
public class Exercise1 implements ExerciseSheet {

    // right hand coordinate system: x to the right, y going up, z pointing towards the viewer
    // size of the exercise in mm
    private static final float imageSizeX = 147.5f;
    private static final float imageSizeY = 115.2f;
    // offset/distance of the exercise (upper left corners) in mm
    private static final float offsetX = 1.0f;
    private static final float offsetY = -53.0f;
    private static int steps = 1;
    // Actual parameters for the resize and translation in the camera image
    private final float kLetterScaleX;
    private final float kLetterScaleY;
    private final float kLetterTranslateX;
    private final float kLetterTranslateY;
    private Vector<Texture> textures;
    private int currentStep = 1;
    private Activity activity;
    // edge length of the marker in mm
    private float markerSize = 50.0f;


    public Exercise1(Activity activity) {
        this.activity = activity;

        // Load the textures
        textures = new Vector<>();
        for (int i = 1; i <= steps; i++) {
            textures.add(Texture.loadTextureFromApk("solutions/Exercise1_step" + i + ".png", activity.getAssets()));
        }

        // Calculate the resize and translation parameters
        kLetterScaleX = imageSizeX * markerSize / 500.0f;
        kLetterScaleY = imageSizeY * markerSize / 500.0f;
        kLetterTranslateX = offsetX + (imageSizeX - markerSize) / 2.0f;
        kLetterTranslateY = offsetY - (imageSizeY - markerSize) / 2.0f;
    }


    public void setCurrentStep(int step) {
        currentStep = step;
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

}
