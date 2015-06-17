package eu.tobby.momentanpol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Vuforia;


public class OpenGL_Renderer extends ActionBarActivity implements SampleApplicationControl {

    private View androidView;
    SampleApplicationSession vuforiaAppSession;
    private SampleApplicationGLView mGlView;
    private FrameMarkerRenderer mRenderer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create Vuforia instance and initialize it
        vuforiaAppSession = new SampleApplicationSession(this);
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Create object of an OpenGL-Viewer
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();
        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);
        // Create Renderer for OpenGL and add it to the view
        mRenderer = new FrameMarkerRenderer(this, vuforiaAppSession);
        mGlView.setRenderer(mRenderer);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
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
    }

    @Override
    public boolean doInitTrackers() {
        return false;
    }

    @Override
    public boolean doLoadTrackersData() {
        return true;
    }

    @Override
    public boolean doStartTrackers() {
        return false;
    }

    @Override
    public boolean doStopTrackers() {
        return false;
    }

    @Override
    public boolean doUnloadTrackersData() {
        return false;
    }

    @Override
    public boolean doDeinitTrackers() {
        return false;
    }

    @Override
    public void onInitARDone(SampleApplicationException e) {
        // Activate renderer and show the OpenGL-Viewer
        mRenderer.mIsActive = true;
        setContentView(mGlView);
        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        addContentView(androidView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        // Start Camera
        try {
            vuforiaAppSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
        } catch (SampleApplicationException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onQCARUpdate(State state) {
    }
}