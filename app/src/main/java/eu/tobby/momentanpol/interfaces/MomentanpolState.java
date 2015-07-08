package eu.tobby.momentanpol.interfaces;

/**
 * Created by fabian on 30.06.15.
 */
public interface MomentanpolState {
    boolean doInitTrackers();
    boolean doLoadTrackersData();
    MomentanpolRenderer getRenderer();
    void isActionDown();
}
