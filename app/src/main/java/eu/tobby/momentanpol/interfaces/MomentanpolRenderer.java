package eu.tobby.momentanpol.interfaces;

import android.opengl.GLSurfaceView;

import com.qualcomm.vuforia.Matrix44F;

import java.util.Vector;

import eu.tobby.momentanpol.utils.Texture;

/**
 * Created by fabian on 01.07.15.
 */
public interface MomentanpolRenderer extends GLSurfaceView.Renderer {
    public Matrix44F getProjectionMatrix();
    public void setProjectionMatrix();
    public void setTextures(Vector<Texture> textures);

}
