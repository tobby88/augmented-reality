package eu.tobby.momentanpol;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import eu.tobby.momentanpol.utils.Exercise;

/**
 * Created by tobby on 08.07.15.
 */
public final class Exercises {

    /*// Right hand coordinate system: x to the right, y going up, z pointing towards the viewer
    // Size of the exercises in mm, comma separated
    private static final float[] imageSizeX = {147.5f};
    private static final float[] imageSizeY = {115.2f};
    // Offset/distance of the exercises (upper left corners) in mm
    private static final float[] offsetX = {1.0f};
    private static final float[] offsetY = {-53.0f};
    // Define the number of steps (images) for each exercise
    private static final int[] steps = {1};
    // Define the (marker-) ID for each exercise (unique!)
    private static final int[] ids_old = {4};
    // Edge length of the marker in mm on each exercise sheet
    private float[] markerSize = {50.0f};*/

    Map<Integer, Integer> ids = new HashMap<>();
    Vector<Exercise> exercises = new Vector<>();


    public Exercises(Activity activity) {
        exercises.add(new Exercise(activity, 147.5f, 115.2f, 50.0f, 1.0f, -53.0f, 1, 4));
        ids.put(4, 0);
    }

    // Find an exercise (its array index) by a given ID
    public Exercise getExercise(int id) {
        int index;
        index = ids.get(id);
        return exercises.get(index);
    }


    public int getNrOfExercises() {
        return exercises.size();
    }


    public int getID(int index) {
        return exercises.get(index).getID();
    }

}
