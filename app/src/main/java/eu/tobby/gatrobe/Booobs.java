package eu.tobby.gatrobe;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import eu.tobby.gatrobe.utils.BooobObj;

/**
 * Booobs-JSON-Reader
 *
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 */
public final class Booobs {

    Map<Integer, Integer> ids = new HashMap<>();
    Vector<BooobObj> booobs = new Vector<>();


    public Booobs(Activity activity) {
        readJSON(activity);
        /*// Example for the JSON file:
        // Right hand coordinate system: x to the right, y going up, z pointing towards the viewer
        // Begin JSON file
        [
        // Begin pic element
          {
        // (Marker-) ID for the pic-set (unique!)
            "id": 4,
        // Size of the pic in mm
            "imageSizeX": 147.5,
            "imageSizeY": 115.2,
        // Number of images for the element
            "pics": 0
        // End element and begin new one
          },
          {
        // Next element here
        // Close last element
          }
        // End JSON file
        ]
        */
    }

    /**
     * JSON-Read-Method
     *
     * @param activity needed to get access to the assets-folder
     */
    private void readJSON(Activity activity) {
        InputStream inputStream;
        AssetManager assets = activity.getAssets();
        try {
            inputStream = assets.open("t/ts.json", AssetManager.ACCESS_BUFFER);
            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                int id = -1;
                float imageSizeX = 0.0f;
                float imageSizeY = 0.0f;
                int pics = 0;
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextInt();
                    } else if (name.equals("imageSizeX")) {
                        imageSizeX = (float) reader.nextDouble();
                    } else if (name.equals("imageSizeY")) {
                        imageSizeY = (float) reader.nextDouble();
                    } else if (name.equals("pics")) {
                        pics = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if (id > -1 && imageSizeX > 0 && imageSizeY > 0 && pics > 0) {
                    booobs.add(new BooobObj(activity, id, imageSizeX, imageSizeY, pics));
                    ids.put(id, booobs.size() - 1);
                }
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            // Handle errors (or not ;) )
        }
    }


    /**
     * @param id: ID of the pic-set
     * @return: current booob-object
     */
    public BooobObj getBooobObj(int id) {
        int index;
        index = ids.get(id);
        return booobs.get(index);
    }

    /**
     * Getter function for the numbers of pic-sets
     *
     * @return: number of pic-sets
     */
    public int getNrOfBooobs() {
        return booobs.size();
    }

    /**
     * Getter for ID
     *
     * @param index: current index
     * @return: ID of current pic-set
     */
    public int getID(int index) {
        return booobs.get(index).getID();
    }

}
