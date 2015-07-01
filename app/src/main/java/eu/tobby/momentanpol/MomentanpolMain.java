package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;


public class MomentanpolMain extends Activity {


    Intent i;

    MomentanpolTask mTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momentanpol_main);
        LinearLayout mMenu = (LinearLayout) findViewById(R.id.tableLayout);
        Button mFrameMarkerButton = (Button) findViewById(R.id.buttonFrameMarker);
        Button mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);
        Button mOpenCVButton = (Button) findViewById(R.id.buttonOpenCV);
        i =  new Intent(getApplicationContext(),MomentanpolTask.class);
        mFrameMarkerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("button",0);
                startActivity(i);
            }
        });
        mImageTargetButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                i.putExtra("button",1);
                startActivity(i);
            }
        });
        mOpenCVButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("button",2);
                startActivity(i);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_momentanpol_main, menu);
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

    // Initializes Vufo

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
}
