package eu.tobby.momentanpol.FrameMarker;

import android.app.Activity;
import android.util.Log;

import com.vuforia.MarkerTracker;
import com.vuforia.Tracker;
import com.vuforia.TrackerManager;
import com.vuforia.Vec2F;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolFrameMarkers implements MomentanpolState {

    private final String LOGTAG = "MomentanpolFrameMarkers";
    private FrameMarkerRenderer mRenderer;

    /**
     * Constructor
     * @param activity: current activity context
     */
    public MomentanpolFrameMarkers(Activity activity) {
        mRenderer = new FrameMarkerRenderer(activity);
    }

    @Override
    public boolean doInitTrackers() {
        boolean result = true;
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker trackerBase = trackerManager.initTracker(MarkerTracker.getClassType());
        MarkerTracker markerTracker = (MarkerTracker) (trackerBase);
        if(markerTracker == null) {
            result = false;
            Log.e(LOGTAG, "MarkerTracker not initialized");
        }
        return result;
    }

    @Override
    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        MarkerTracker markerTracker = (MarkerTracker) tManager.getTracker(MarkerTracker.getClassType());
        if (markerTracker == null)
            return false;
        for (int i = 0; i < mRenderer.getExercises().getNrOfExercises(); i++) {
            int id = mRenderer.getExercises().getID(i);
            markerTracker.createFrameMarker(id, "Exercise" + Integer.toString(id), new Vec2F(50, 50));
        }
        markerTracker.start();
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
            mRenderer.getExercises().getExercise(id).addStep();
        }
    }

}
