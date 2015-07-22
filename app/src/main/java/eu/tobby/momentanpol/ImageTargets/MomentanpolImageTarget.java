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
 * Image Target Vuforia Task, which uses Images as targets to overlay transparent solutions of "Momentanpol"-Exercises
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 * @see MomentanpolState
 */
public class MomentanpolImageTarget implements MomentanpolState {

    private final String LOGTAG = "MomentanpolImageTargets";
    private ImageTargetRenderer mRenderer;
    private DataSet mDataset;
    private ObjectTracker objectTracker;

    /**
     * Constructor
     * @param activity: current activity context
     */
    public MomentanpolImageTarget(Activity activity) {
        mRenderer = new ImageTargetRenderer(activity);
    }

    @Override
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

    @Override
    public boolean doLoadTrackersData() {
        if (mDataset == null) {
            mDataset = objectTracker.createDataSet();
        }
        if (mDataset == null) {
            return false;
        }
        // Load DataSet for the ImageTargets
        if (!mDataset.load("Marker.xml",STORAGE_TYPE.STORAGE_APPRESOURCE)){
            Log.e(LOGTAG,"Marker nicht gefunden");
            return false;
        }
        // set DataSet as active DataSet
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

    @Override
    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    /**
     * callback Method that carries that the next step of the solution will be shown
     */
    public void isActionDown() {
        Log.d(LOGTAG,"ButtonDown");
        int id = mRenderer.getLastID();
        if (id >= 0) {
            // Synchronize IDs of FrameMarker and ImageTargets (ID 1 of ImageTargets is ID 4 of FrameMarkers)
            mRenderer.getExercises().getExercise(id + 3).addStep();
        }
    }

}
