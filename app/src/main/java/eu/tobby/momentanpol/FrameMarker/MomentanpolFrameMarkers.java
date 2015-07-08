package eu.tobby.momentanpol.FrameMarker;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.vuforia.MarkerTracker;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2F;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolFrameMarkers implements MomentanpolState {

    private final String LOGTAG = "MomentanpolFrameMarkers";
    private FrameMarkerRenderer mRenderer;


    public MomentanpolFrameMarkers(Activity activity) {
        mRenderer = new FrameMarkerRenderer(activity);
    }


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


    public boolean doLoadTrackersData() {
        TrackerManager tManager = TrackerManager.getInstance();
        MarkerTracker markerTracker = (MarkerTracker) tManager.getTracker(MarkerTracker.getClassType());
        if (markerTracker == null)
            return false;
        markerTracker.createFrameMarker(4, "Exercise1", new Vec2F(50, 50));
        markerTracker.createFrameMarker(5, "Bart", new Vec2F(50, 50));
        markerTracker.start();
        return true;
    }


    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }


    public void isActionDown() {
        Log.d(LOGTAG,"ButtonDown");
    }

}
