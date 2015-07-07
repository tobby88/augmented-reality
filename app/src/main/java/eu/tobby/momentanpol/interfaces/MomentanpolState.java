package eu.tobby.momentanpol.interfaces;

import android.app.Activity;
import android.opengl.GLSurfaceView;

/**
 * Created by fabian on 30.06.15.
 */
public interface MomentanpolState {
    boolean doInitTrackers();
    boolean doLoadTrackersData();
    MomentanpolRenderer getRenderer();
    void isActionDown();
}
