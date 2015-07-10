package eu.tobby.momentanpol.ImageTargets;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ObjectTracker;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 30.06.15.
 * Hier entsteht gerade das ImageTarget-Beispiel
 */
public class MomentanpolImageTarget implements MomentanpolState {

    private final String LOGTAG = "MomentanpolImageTargets";
    private ImageTargetRenderer mRenderer;
    private DataSet mDataset;
    private ObjectTracker objectTracker;


    public MomentanpolImageTarget(Activity activity) {
        mRenderer = new ImageTargetRenderer(activity);
    }


    public boolean doInitTrackers() {
        boolean result = true;
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker trackerBase = trackerManager.initTracker(ObjectTracker.getClassType());
        objectTracker = (ObjectTracker) trackerBase;
        if(objectTracker == null) {
            result = false;
            Log.e(LOGTAG, "MarkerTracker not initialized");
        }
        return result;
    }


    public boolean doLoadTrackersData() {
        if (mDataset == null) {
            mDataset = objectTracker.createDataSet();
        }
        if (mDataset == null) {
            return false;
        }
        if (!mDataset.load("Marker.xml",STORAGE_TYPE.STORAGE_APPRESOURCE)){
            Log.e(LOGTAG,"Marker nicht gefunden");
            return false;
        }
        if (!objectTracker.activateDataSet(mDataset))
            return false;
        int numTrackables = mDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mDataset.getTrackable(count);
            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data " + trackable.getUserData());
        }
        objectTracker.start();
        return true;
    }


    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }


    public void isActionDown() {
        Log.d(LOGTAG,"ButtonDown");
        int id = mRenderer.getLastID();
        if (id >= 0) {
            // Synchronize IDs of FrameMarker and ImageTargets (ID 1 of ImageTargets is ID 4 of FrameMarkers)
            mRenderer.getExercises().getExercise(id + 3).addStep();
        }
    }

}
