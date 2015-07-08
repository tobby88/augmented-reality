package eu.tobby.momentanpol.FrameMarker;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.vuforia.MarkerTracker;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2F;

import java.util.Vector;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolFrameMarkers implements MomentanpolState {

    private final String LOGTAG = "MomentanpolFrameMarkers";
    private Vector<Texture> mTextures;
    private Activity mActivity;
    private FrameMarkerRenderer mRenderer;


    public MomentanpolFrameMarkers(Activity activity) {
        mActivity = activity;
        mRenderer = new FrameMarkerRenderer();
        mTextures = new Vector<>();
        loadTextures();
        mRenderer.setTextures(mTextures);
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
        markerTracker.createFrameMarker(0, "MarkerQ", new Vec2F(50, 50));
        markerTracker.createFrameMarker(1, "MarkerC", new Vec2F(50, 50));
        markerTracker.createFrameMarker(2, "MarkerA", new Vec2F(50, 50));
        markerTracker.createFrameMarker(3, "MarkerR", new Vec2F(50, 50));
        markerTracker.createFrameMarker(4, "Momentanpol", new Vec2F(50, 50));
        markerTracker.createFrameMarker(5, "Bart", new Vec2F(50, 50));
        markerTracker.start();
        return true;
    }


    private void loadTextures() {
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_Q.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_A.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_C.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_R.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/Momentanpol1_Loesung.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/bart.jpg", mActivity.getAssets()));
    }


    public MomentanpolRenderer getRenderer() {
        return mRenderer;
    }

    public void isActionDown() {
        Log.d(LOGTAG,"ButtonDown");
    }

}
