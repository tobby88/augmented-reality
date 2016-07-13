package eu.tobby.gatrobe;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.vuforia.CameraDevice;
import com.vuforia.PIXEL_FORMAT;
import com.vuforia.Renderer;
import com.vuforia.Vec2I;
import com.vuforia.VideoBackgroundConfig;
import com.vuforia.VideoMode;
import com.vuforia.Vuforia;

import eu.tobby.gatrobe.ImageTargets.GatrobeImageTarget;
import eu.tobby.gatrobe.interfaces.GatrobeRenderer;
import eu.tobby.gatrobe.interfaces.GatrobeState;

/**
 * Context for the Vuforia-statemachine for initializations of vuforia
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 * @see <a href="http://www.tutorialspoint.com/design_pattern/state_pattern.htm">State Design Pattern</a>
 */

public class GatrobeTask extends Activity {

    //View for the OpenGL rendering with Vuforia extensions
    GatrobeGLView glSurfaceView;
    // Interface for handling the Vuforia based states (like a pointer in C++)
    private GatrobeState iState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View androidView;
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        addContentView(androidView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.activity_gatrobe_task);
        // check which state is chosen and starts the chosen state
        switch (getIntent().getIntExtra("button",-1)) {
            case 0:
                iState = new GatrobeImageTarget(this);
                break;
            default:
                break;
        }
        // Handles the different renderer types
        GatrobeRenderer iRenderer;
        iRenderer = iState.getRenderer();
        // Create object of an OpenGL-Viewer with OpenGL2.0
        glSurfaceView = new GatrobeGLView(this, iRenderer, iState);
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
        Vuforia.setInitParameters(this, Vuforia.GL_20, "AVynWNT/////AAAAGVVxHDOd2UItoZYY37jyXOZaYXvb0NZ3T6E+xRUZAeYD9DGud1yMR+8NYOViOv/OlGHdFw/f2TbZzDTZT0CDXL9ROSuoMqSTuh4uXiEiLMLzkHos4nrEswXJ/OVr1wJjclHvT5duw9fRbwzTaUxsp67IKRq9AsGtj4nlLbzSNmXdS3K/IA7t/ACxX9BNjlAYXJi6oCXgC89OArUAnjJ5LVwWNzz2DZJtvwycytQ6gUQ9Q1sIFoX0PeOJ05JaQ9uk7GsV82TXV714om7HBhZeyxc16iWJFQdpfXS4160zWRuuuozvdWizFUPMJhzK9Iq3MDdZjag8ogYLZD92xYxRJhVZRimWu2EuaGXHxMlNjL5u");
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
        CameraDevice.getInstance().init(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);
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
        //config.setSynchronous(true);
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