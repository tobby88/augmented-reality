package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;

import org.opencv.android.OpenCVLoader;

import eu.tobby.momentanpol.FrameMarker.MomentanpolFrameMarkers;
import eu.tobby.momentanpol.ImageTargets.MomentanpolImageTarget;
import eu.tobby.momentanpol.OpenCVMarker.MomentanpolOpenCVMarker;
import eu.tobby.momentanpol.interfaces.MomentanpolRenderer;
import eu.tobby.momentanpol.interfaces.MomentanpolState;


public class MomentanpolTask extends Activity {

    private View androidView;
    private MomentanpolState iState;
    private MomentanpolRenderer iRenderer;
    private boolean mIsOpenCVLoaded = false;
    MomentanpolGLView glSurfaceView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OpenCVLoader.initDebug();
        //OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,this,openCVLoadCallback)


        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer


        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        addContentView(androidView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setContentView(R.layout.activity_momentanpol_task);
        switch (getIntent().getIntExtra("button",-1)) {
            case 0:
                iState = new MomentanpolFrameMarkers(this);
                break;
            case 1:
                iState = new MomentanpolImageTarget(this);
                break;
            default:
                iState = new MomentanpolOpenCVMarker(this);
                break;

        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_momentanpol_task, menu);
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
    }

    public void initAR() {
        // As long as this window is visible to the user, keep the device's screen turned on and bright:
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Vuforia.setInitParameters(this, Vuforia.GL_20, "AcCmXLj/////AAAAAWn4W4WOD0/up7Ehu28I5VBQq6sv1WL7JaOAAvhmS98exI+JmpBsPdHy4GnaLxhmgOq3BSpWKFZR4eh6xL2K2NIGN4kPDW8fQwwhKv+7uusQoE5Grc/DOTM0NMZ/G/UPJQC59Uj/SnYvr67zfibax4kVrv+tNzLkcqn+pvhLcdFX1HATddnCCb9IwC2QEc+qX2HSLwxDlS/87FVlhcsUB/NeICVSVTtB5+buqEwOGy+4ZLwJjW5RFrGWX9SLMWHffO9K7X4mn2JQqRt8ZBJXMbixO54BFT+wA7JAhtfznUN33z3QBiE8Uce3aCI8Fh6gBVUt6b35sv8IMmnbZGJ9iA6XurRBoNwJEm7bve2myxu/");
        while(Vuforia.init()<100){}
        boolean initTrackersResult;
        initTrackersResult = iState.doInitTrackers();
        if(initTrackersResult) {
            iState.doLoadTrackersData();
        }
        startCam();
    }

    private void startCam() {
        CameraDevice.getInstance().init(CameraDevice.CAMERA.CAMERA_DEFAULT);
        configureVideoBackground();
        CameraDevice.getInstance().selectVideoMode(CameraDevice.MODE.MODE_DEFAULT);
        CameraDevice.getInstance().start();
        CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
    }

    private void stopCam(){
        CameraDevice.getInstance().stop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Vuforia.deinit();
        stopCam();
    }

    private void openCVLoadCallback(){
        mIsOpenCVLoaded = true;
    }
}
