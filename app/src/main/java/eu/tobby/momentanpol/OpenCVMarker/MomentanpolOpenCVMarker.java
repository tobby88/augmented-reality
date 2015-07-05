package eu.tobby.momentanpol.OpenCVMarker;

import android.app.Activity;
import android.util.Log;

import java.util.Vector;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;
import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 05.07.15.
 */
public class MomentanpolOpenCVMarker implements MomentanpolState {
    private final String LOGTAG = "MomentalpolOpenCV";
    private OpenCVMarkerRenderer mRenderer;
    private Vector<Texture> mTextures;
    private Activity mActivity;

    public MomentanpolOpenCVMarker(Activity activity) {
        mActivity = activity;
        mRenderer = new OpenCVMarkerRenderer();
        mTextures = new Vector<>();
        loadTextures();
        mRenderer.setTextures(mTextures);
    }
    public boolean doLoadTrackersData(){return false;}
    public void loadTextures(){}
    public boolean doInitTrackers(){return false;}
    public MomentanpolRenderer getRenderer() {return mRenderer;}

    public void isActionDown() {
        Log.d(LOGTAG, "ButtonDown");

    }
}
