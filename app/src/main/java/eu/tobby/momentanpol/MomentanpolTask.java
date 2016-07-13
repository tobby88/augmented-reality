package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.PIXEL_FORMAT;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;

import eu.tobby.momentanpol.FrameMarker.MomentanpolFrameMarkers;
import eu.tobby.momentanpol.ImageTargets.MomentanpolImageTarget;
import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Context for the Vuforia-statemachine for initializations of vuforia
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 * @see <a href="http://www.tutorialspoint.com/design_pattern/state_pattern.htm">State Design Pattern</a>
 */

public class MomentanpolTask extends Activity {

    //View for the OpenGL rendering with Vuforia extensions
    MomentanpolGLView glSurfaceView;
    // Interface for handling the Vuforia based states (like a pointer in C++)
    private MomentanpolState iState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View androidView;
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        addContentView(androidView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.activity_momentanpol_task);
        // check which state is chosen and starts the chosen state
        switch (getIntent().getIntExtra("button",-1)) {
            case 0:
                iState = new MomentanpolFrameMarkers(this);
                break;
            case 1:
                iState = new MomentanpolImageTarget(this);
                break;
            default:
                break;
        }
        // Handles the different renderer types
        MomentanpolRenderer iRenderer;
        iRenderer = iState.getRenderer();
        // Create object of an OpenGL-Viewer with OpenGL2.0
        glSurfaceView = new MomentanpolGLView(this,iRenderer,iState);
        glSurfaceView.setEGLContextClientVersion(2);
        // Create Renderer for OpenGL, add it to the view and show it
        glSurfaceView.setRenderer(iRenderer);
        setContentView(glSurfaceView);
        initAR();
        iRenderer.setProjectionMatrix();
    }

    /**
     * Initialization method for Vuforia
     */
    public void initAR() {
        // As long as this window is visible to the user, keep the device's screen turned on and bright:
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Vuforia.setInitParameters(this, Vuforia.GL_20, "AcCmXLj/////AAAAAWn4W4WOD0/up7Ehu28I5VBQq6sv1WL7JaOAAvhmS98exI+JmpBsPdHy4GnaLxhmgOq3BSpWKFZR4eh6xL2K2NIGN4kPDW8fQwwhKv+7uusQoE5Grc/DOTM0NMZ/G/UPJQC59Uj/SnYvr67zfibax4kVrv+tNzLkcqn+pvhLcdFX1HATddnCCb9IwC2QEc+qX2HSLwxDlS/87FVlhcsUB/NeICVSVTtB5+buqEwOGy+4ZLwJjW5RFrGWX9SLMWHffO9K7X4mn2JQqRt8ZBJXMbixO54BFT+wA7JAhtfznUN33z3QBiE8Uce3aCI8Fh6gBVUt6b35sv8IMmnbZGJ9iA6XurRBoNwJEm7bve2myxu/");
        int init = 0;
        while (init < 100) {
            init = Vuforia.init();
        }
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGBA8888, true);
        boolean initTrackersResult;
        initTrackersResult = iState.doInitTrackers();
        //Check if the trackers are initialized
        if(initTrackersResult) {
            iState.doLoadTrackersData();
        }
        startCam();
    }

    /**
     * Initialization and parametrization
     */
    private void startCam() {
        CameraDevice.getInstance().init(CameraDevice.CAMERA.CAMERA_DEFAULT);
        configureVideoBackground();
        CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        CameraDevice.getInstance().start();
        Vuforia.setFrameFormat(PIXEL_FORMAT.RGB888, true);
        CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
    }


    private void stopCam(){
        CameraDevice.getInstance().stop();
    }

    /**
     * Sets up the display dimensions in according to your device's specification
     */
    private void configureVideoBackground() {
        // Query display dimensions:
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        // Configures the video mode and sets offsets for the camera's image
        CameraDevice cameraDevice = CameraDevice.getInstance();
        VideoMode vm = cameraDevice.getVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        VideoBackgroundConfig config = new VideoBackgroundConfig();
        config.setEnabled(true);
        config.setSynchronous(true);
        config.setPosition(new Vec2I(0, 0));
        int xSize, ySize;
        xSize = (int) (vm.getHeight() * (metrics.heightPixels / (float) vm.getWidth()));
        ySize = metrics.heightPixels;
        if (xSize < metrics.widthPixels) {
            xSize = metrics.widthPixels;
            ySize = (int) (metrics.widthPixels * (vm.getWidth() / (float) vm.getHeight()));
        }
        config.setSize(new Vec2I(xSize, ySize));
        Renderer.getInstance().setVideoBackgroundConfig(config);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Vuforia.deinit();
        stopCam();
    }

}