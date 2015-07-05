package eu.tobby.momentanpol.OpenCVMarker;

import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Created by fabian on 05.07.15.
 */
public class MomentanpolOpenCVMarker implements MomentanpolState {

    OpenCVMarkerRenderer mRenderer;

    public boolean doLoadTrackersData(){return false;}
    public void loadTextures(){}
    public boolean doInitTrackers(){return false;}
    public MomentanpolRenderer getRenderer() {return mRenderer;}
}
