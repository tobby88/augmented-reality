package eu.tobby.gatrobe;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.JsonReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import eu.tobby.gatrobe.utils.Tiddies;

/**
 * Tits-JSON-Reader
 *
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 */
public final class Tits {

    Map<Integer, Integer> ids = new HashMap<>();
    Vector<Tiddies> tits = new Vector<>();


    public Tits(Activity activity) {
        readTitsJSON(activity);
        /*// Example for the JSON file:
        // Right hand coordinate system: x to the right, y going up, z pointing towards the viewer
        // Begin JSON file
        [
        // Begin tits element
          {
        // (Marker-) ID for the tits (unique!)
            "id": 4,
        // Size of the tits in mm
            "imageSizeX": 147.5,
            "imageSizeY": 115.2,
        // Number of tit-images for the tits-element
            "titpics": 0
        // End tits element and begin new one
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
     * Tits-Read-Method
     *
     * @param activity needed to get access to the assets-folder
     */
    private void readTitsJSON(Activity activity) {
        InputStream inputStream;
        AssetManager assets = activity.getAssets();
        try {
            inputStream = assets.open("tits/tits.json", AssetManager.ACCESS_BUFFER);
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
                int titpics = 0;
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("id")) {
                        id = reader.nextInt();
                    } else if (name.equals("imageSizeX")) {
                        imageSizeX = (float) reader.nextDouble();
                    } else if (name.equals("imageSizeY")) {
                        imageSizeY = (float) reader.nextDouble();
                    } else if (name.equals("titpics")) {
                        titpics = reader.nextInt();
                    } else {
                        reader.skipValue();
                    }
                }
                reader.endObject();
                if (id > -1 && imageSizeX > 0 && imageSizeY > 0 && titpics > 0) {
                    tits.add(new Tiddies(activity, id, imageSizeX, imageSizeY, titpics));
                    ids.put(id, tits.size() - 1);
                }
            }
            reader.endArray();
            reader.close();
        } catch (Exception e) {
            // Handle errors (or not ;) )
        }
    }


    /**
     * @param id: ID of the tits and the Frame Marker
     * @return: current tits
     */
    public Tiddies getTiddies(int id) {
        int index;
        index = ids.get(id);
        return tits.get(index);
    }

    /**
     * Getter function for the Numbers of tits
     *
     * @return: number of tits
     */
    public int getNrOfTits() {
        return tits.size();
    }

    /**
     * Getter for Marker ID
     *
     * @param index: current index
     * @return: ID of current tits
     */
    public int getID(int index) {
        return tits.get(index).getID();
    }

}
