package eu.tobby.momentanpol.interfaces;

import android.opengl.GLSurfaceView;

import com.qualcomm.vuforia.Matrix44F;

import java.util.Vector;

import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 01.07.15.
 */
public interface MomentanpolRenderer extends GLSurfaceView.Renderer {
    Matrix44F getProjectionMatrix();

    void setProjectionMatrix();

    void setTextures(Vector<Texture> textures);

}
