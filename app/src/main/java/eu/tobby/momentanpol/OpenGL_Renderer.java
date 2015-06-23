package eu.tobby.momentanpol;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
/*import android.view.Menu;
import android.view.MenuItem;*/
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;

import com.qualcomm.vuforia.CameraCalibration;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Marker;
import com.qualcomm.vuforia.MarkerTracker;
import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vec2F;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.Vuforia.UpdateCallbackInterface;

import java.util.Vector;
import java.util.concurrent.locks.Lock;

import eu.tobby.momentanpol.utils.Texture;


public class OpenGL_Renderer extends ActionBarActivity implements UpdateCallbackInterface {

    private static final String LOGTAG = "OpenGL_renderer";

    private View androidView;
    private MomentanpolGLRenderer mRenderer;
    OpenGL_View glSurfaceView;
    private Marker dataSet[];
    private Vector<Texture> mTextures;
    private Object mShutdownLock = new Object();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create Vuforia instance, initialize it and start the camera
        mTextures = new Vector<>();
        loadTextures();
        mRenderer = new MomentanpolGLRenderer();

        // Create object of an OpenGL-Viewer with OpenGL2.0
        glSurfaceView = new OpenGL_View(this, mRenderer);
        glSurfaceView.setEGLContextClientVersion(2);
        // Create Renderer for OpenGL, add it to the view and show it
        mRenderer.setTextures(mTextures);
        glSurfaceView.setRenderer(mRenderer);
        setContentView(glSurfaceView);
        initAR();
        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        addContentView(androidView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));




    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CameraDevice.getInstance().stop();
        CameraDevice.getInstance().deinit();
        Vuforia.deinit();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_gl__renderer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    // Initializes Vuforia and sets up preferences.
    public void initAR() {
        // As long as this window is visible to the user, keep the device's screen turned on and bright:
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Vuforia.setInitParameters(this, Vuforia.GL_20, "AcCmXLj/////AAAAAWn4W4WOD0/up7Ehu28I5VBQq6sv1WL7JaOAAvhmS98exI+JmpBsPdHy4GnaLxhmgOq3BSpWKFZR4eh6xL2K2NIGN4kPDW8fQwwhKv+7uusQoE5Grc/DOTM0NMZ/G/UPJQC59Uj/SnYvr67zfibax4kVrv+tNzLkcqn+pvhLcdFX1HATddnCCb9IwC2QEc+qX2HSLwxDlS/87FVlhcsUB/NeICVSVTtB5+buqEwOGy+4ZLwJjW5RFrGWX9SLMWHffO9K7X4mn2JQqRt8ZBJXMbixO54BFT+wA7JAhtfznUN33z3QBiE8Uce3aCI8Fh6gBVUt6b35sv8IMmnbZGJ9iA6XurRBoNwJEm7bve2myxu/");
        while(Vuforia.init()<100){}
        boolean initTrackersResult;
        initTrackersResult = doInitTrackers();
        if(initTrackersResult) {
            doLoadTrackersData();
        }
        CameraDevice.getInstance().init(CameraDevice.CAMERA.CAMERA_DEFAULT);
        configureVideoBackground();
        CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        CameraDevice.getInstance().start();
        CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
        mRenderer.setProjectionMatrix();

    }

    public boolean doInitTrackers() {
        boolean result = true;
        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker trackerBase = trackerManager.initTracker(MarkerTracker.getClassType());
        MarkerTracker markerTracker = (MarkerTracker) (trackerBase);
        if(markerTracker == null) {
            result = false;
            Log.e(LOGTAG, "MarkerTracker not initialized");
        }
        return result;
    }

    public boolean doLoadTrackersData(){

        TrackerManager tManager = TrackerManager.getInstance();
        MarkerTracker markerTracker = (MarkerTracker) tManager.getTracker(MarkerTracker.getClassType());
        if (markerTracker == null)
                return false;
        dataSet = new Marker[4];
        dataSet[0] = markerTracker.createFrameMarker(0, "MarkerQ", new Vec2F(50, 50));
        dataSet[1] = markerTracker.createFrameMarker(1, "MarkerC", new Vec2F(50, 50));
        dataSet[2] = markerTracker.createFrameMarker(2, "MarkerA", new Vec2F(50, 50));
        dataSet[3] = markerTracker.createFrameMarker(3, "MarkerR", new Vec2F(50, 50));
        markerTracker.start();
        return true;
        // Here Exception handling



    }

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

    // Callback called every cycle
    @Override
    public void QCAR_onUpdate(State s) {}

    private void loadTextures()
    {
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_Q.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_C.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_A.png",
                getAssets()));
        mTextures.add(Texture.loadTextureFromApk("FrameMarkers/letter_R.png",
                getAssets()));
    }



}
