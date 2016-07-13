package eu.tobby.gatrobe.ImageTargets;

import android.app.Activity;
import android.util.Log;

import com.vuforia.DataSet;
import com.vuforia.ObjectTracker;
import com.vuforia.STORAGE_TYPE;
import com.vuforia.Trackable;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;

import eu.tobby.gatrobe.interfaces.GatrobeRenderer;
import eu.tobby.gatrobe.interfaces.GatrobeState;

/**
 * Image Target Vuforia Task, which uses Images as targets to overlay
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 * @see GatrobeState
 */
public class GatrobeImageTarget implements GatrobeState {

    private final String LOGTAG = "GatrobeImageTargets";
    private ImageTargetRenderer mRenderer;
    private DataSet mDataset;
    private ObjectTracker objectTracker;

    /**
     * Constructor
     * @param activity: current activity context
     */
    public GatrobeImageTarget(Activity activity) {
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
        if (!mDataset.load("Gatrobe.xml", STORAGE_TYPE.STORAGE_APPRESOURCE)) {
            Log.e(LOGTAG, "ImageTarget XML config not found");
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
    public GatrobeRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    /**
     * callback Method that carries that the next pic of will be shown
     */
    public void isActionDown() {
        //Log.d(LOGTAG,"ButtonDown");
        int id = mRenderer.getLastID();
        if (id >= 0) {
            mRenderer.getBooobs().getBooobObj(id).addPic();
        }
    }

}
