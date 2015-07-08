package eu.tobby.momentanpol.interfaces;

import android.opengl.GLSurfaceView;

import com.qualcomm.vuforia.Matrix44F;

/**
 * Created by fabian on 01.07.15.
 */
public interface MomentanpolRenderer extends GLSurfaceView.Renderer {

    Matrix44F getProjectionMatrix();
    void setProjectionMatrix();

}
