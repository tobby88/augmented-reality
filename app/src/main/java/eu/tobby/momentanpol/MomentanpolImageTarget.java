package eu.tobby.momentanpol;

import android.opengl.GLSurfaceView;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 30.06.15.
 */
public class MomentanpolImageTarget implements MomentanpolState {

    public MomentanpolImageTarget(){    }
    public boolean doInitTrackers(){
        return true;
    }
    public boolean doLoadTrackersData(){
        return true;
    }

    public MomentanpolRenderer getRenderer() {return null;}


}
