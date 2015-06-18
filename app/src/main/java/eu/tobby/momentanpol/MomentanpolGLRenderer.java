package eu.tobby.momentanpol;

import android.opengl.GLSurfaceView;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vuforia;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by tobby on 11.06.15.
 */
public class MomentanpolGLRenderer implements GLSurfaceView.Renderer {

    public MomentanpolGLRenderer() {}

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {}

    public void onDrawFrame(GL10 gl) {
        // Get the state from Vuforia and mark the beginning of a rendering section
        Renderer.getInstance().begin();
        // Explicitly render the Video Background
        Renderer.getInstance().drawVideoBackground();
        Renderer.getInstance().end();
    }

    // Called when the surface changed size.
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // Call Vuforia function to handle render surface size changes:
        Vuforia.onSurfaceChanged(width, height);
    }
}
