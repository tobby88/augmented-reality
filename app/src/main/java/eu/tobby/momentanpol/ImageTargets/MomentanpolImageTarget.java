package eu.tobby.momentanpol.ImageTargets;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.ObjectTracker;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;

import java.util.Vector;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 30.06.15.
 * Hier entsteht gerade das ImageTarget-Beispiel
 */
public class MomentanpolImageTarget implements MomentanpolState {

    private final String LOGTAG = "MomentanpolImageTargets";
    private Activity mActivity;
    private ImageTargetRenderer mRenderer;
    private Vector<Texture> mTextures;
    private DataSet mDataset;
    private ObjectTracker objectTracker;


    public MomentanpolImageTarget(Activity activity) {
        mActivity = activity;
        mRenderer = new ImageTargetRenderer();
        mTextures = new Vector<>();
        loadTextures();
        mRenderer.setTextures(mTextures);
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


    public void loadTextures() {
        mTextures.add(Texture.loadTextureFromApk("solutions/Exercise4_step1.png", mActivity.getAssets()));
    }


    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }


    public void isActionDown() {
        Log.d(LOGTAG,"ButtonDown");
    }

}
