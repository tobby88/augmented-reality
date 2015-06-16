package eu.tobby.momentanpol;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Vuforia;


public class OpenGL_Renderer extends ActionBarActivity implements SampleApplicationControl {

    private GLSurfaceView glView;
    private RelativeLayout rl;
    private View androidView;
    SampleApplicationSession vuforiaAppSession;

    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    // Our renderer;
    private FrameMarkerRenderer mRenderer;
    private RelativeLayout mUILayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create a new relative layout to make a stack of views
        rl = new RelativeLayout(this);
        // Create object of an OpenGL-Viewer and add this view to the layout
        glView = new MyGLSurfaceView(this);
        //rl.addView(glView);
        // Make a view out of the Designer-XML and add this view on top of the OpenGL-Viewer
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        androidView = inflater.inflate(R.layout.activity_open_gl__renderer, null);
        rl.addView(androidView);
        startLoadingAnimation();
        vuforiaAppSession = new SampleApplicationSession(this);
        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Show this layout
        setContentView(rl);
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
        return false;
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

    private void initApplicationAR()
    {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new FrameMarkerRenderer(this, vuforiaAppSession);
        //mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);

    }

    private void startLoadingAnimation()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        mUILayout = (RelativeLayout) inflater.inflate(R.layout.camera_overlay,
                null, false);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);


        // Adds the inflated layout to the view
        addContentView(mUILayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void onInitARDone(SampleApplicationException e) {
        rl.removeAllViews();
        initApplicationAR();
        addContentView(mGlView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mUILayout.bringToFront();
        mUILayout.setBackgroundColor(Color.TRANSPARENT);
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
