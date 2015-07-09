package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import eu.tobby.momentanpol.utils.Exercise;

/**
 * Created by tobby on 08.07.15.
 */
public final class Exercises {

    Map<Integer, Integer> ids = new HashMap<>();
    Vector<Exercise> exercises = new Vector<>();


    public Exercises(Activity activity) {
        readExercisesJSON(activity);
        /*// Example for the JSON file:
        // Right hand coordinate system: x to the right, y going up, z pointing towards the viewer
        // Begin JSON file
        [
        // Begin exercise element
          {
        // (Marker-) ID for the exercise (unique!)
            "id": 4,
        // Size of the exercise in mm
            "imageSizeX": 147.5,
            "imageSizeY": 115.2,
        // Edge length of the marker in mm on the exercise sheet
            "markerSize": 50.0,
        // Offset/distance of the exercise from the marker (upper left corners) in mm
            "offsetX": 1.0,
            "offsetY": -53.0,
        // Number of steps (images) for the exercise
            "steps": 0
        // End exercise element and begin new one
          },
          {
        // Next element here
        // Close last element
          }
        // End JSON file
        ]
        */
    }


    private void readExercisesJSON(Activity activity) {
        InputStream inputStream;
        AssetManager assets = activity.getAssets();
        try {
            inputStream = assets.open("solutions/exercises.json", AssetManager.ACCESS_BUFFER);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                int id = -1;
                float imageSizeX = 0.0f;
                float imageSizeY = 0.0f;
                float markerSize = 0.0f;
                float offsetX = 0.0f;
                float offsetY = 0.0f;
                int steps = 0;
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextInt();
                    } else if (name.equals("imageSizeX")) {
                        imageSizeX = (float) reader.nextDouble();
                    } else if (name.equals("imageSizeY")) {
                        imageSizeY = (float) reader.nextDouble();
                    } else if (name.equals("markerSize")) {
                        markerSize = (float) reader.nextDouble();
                    } else if (name.equals("offsetX")) {
                        offsetX = (float) reader.nextDouble();
                    } else if (name.equals("offsetY")) {
                        offsetY = (float) reader.nextDouble();
                    } else if (name.equals("steps")) {
                        steps = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if (id > -1 && imageSizeX > 0 && imageSizeY > 0 && markerSize > 0 && steps > 0) {
                    exercises.add(new Exercise(activity, id, imageSizeX, imageSizeY, markerSize, offsetX, offsetY, steps));
                    ids.put(id, exercises.size() - 1);
                }
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            // Handle errors (or not ;) )
        }
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
