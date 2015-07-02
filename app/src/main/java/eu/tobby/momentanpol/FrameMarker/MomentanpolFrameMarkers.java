package eu.tobby.momentanpol.FrameMarker;

import android.app.Activity;
import android.util.Log;

import com.qualcomm.vuforia.Marker;
import com.qualcomm.vuforia.MarkerTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2F;

import java.util.Vector;

import eu.tobby.momentanpol.FrameMarker.FrameMarkerRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolFrameMarkers implements MomentanpolState {

    private Vector<Texture> mTextures;
    private Marker[] dataSet;
    private Activity mActivity;
    private final String LOGTAG = "MomentanpolFrameMarkers";
    private FrameMarkerRenderer mRenderer;

    public MomentanpolFrameMarkers(Activity activity){
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
        dataSet = new Marker[6];
        dataSet[0] = markerTracker.createFrameMarker(0, "MarkerQ", new Vec2F(50, 50));
        dataSet[1] = markerTracker.createFrameMarker(1, "MarkerC", new Vec2F(50, 50));
        dataSet[2] = markerTracker.createFrameMarker(2, "MarkerA", new Vec2F(50, 50));
        dataSet[3] = markerTracker.createFrameMarker(3, "MarkerR", new Vec2F(50, 50));
        dataSet[4] = markerTracker.createFrameMarker(4, "Momentanpol", new Vec2F(50, 50));
        dataSet[5] = markerTracker.createFrameMarker(5, "Bart", new Vec2F(50, 50));
        markerTracker.start();
        return true;
        // Here Exception handling



    }

    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_Q.png",
                mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_A.png",
                mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_C.png",
                mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_R.png",
                mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/Momentanpol1_Loesung.png", mActivity.getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/bart.jpg", mActivity.getAssets()));
    }

    public MomentanpolRenderer getRenderer(){return mRenderer;}

    public void QCAR_onUpdate(State s){}

}
