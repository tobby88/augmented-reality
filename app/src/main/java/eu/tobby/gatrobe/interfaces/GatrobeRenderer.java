package eu.tobby.gatrobe.interfaces;

import android.opengl.GLSurfaceView;

import com.vuforia.Matrix44F;

/**
 * Interface for the Renderer of each Vuforia-Task
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 */
public interface GatrobeRenderer extends GLSurfaceView.Renderer {
    /**
     * Getter method for the projection matrix of the marker
     * @return: return matrix
     */
    Matrix44F getProjectionMatrix();

    /**
     * gets the device specific camera calibration and stores it in the current Vuforia task
     */
    void setProjectionMatrix();

}
