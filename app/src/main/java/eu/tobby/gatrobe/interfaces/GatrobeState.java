package eu.tobby.gatrobe.interfaces;

/**
 * Abstract State for each Vuforia task
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 * @see <a href="http://www.tutorialspoint.com/design_pattern/state_pattern.htm">State Design Pattern</a>
 */
public interface GatrobeState {
    
    /**
     * Initialization of the Markers like Frame Markers or Image Targets
     * @return: was the initialization successful? true or false
     */
    boolean doInitTrackers();

    /**
     * Loading Markers
     * @return: succcessful or not?
     */
    boolean doLoadTrackersData();

    /**
     * Getter method which returns the renderer of the current Vuforia state
     * @return: current Renderer
     */
    GatrobeRenderer getRenderer();

    /**
     * Callback method for display events
     */
    void isActionDown();
}
