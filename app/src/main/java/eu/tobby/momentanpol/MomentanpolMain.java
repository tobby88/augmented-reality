package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.Vec2I;
import com.qualcomm.vuforia.VideoBackgroundConfig;
import com.qualcomm.vuforia.VideoMode;
import com.qualcomm.vuforia.Vuforia;


public class MomentanpolMain extends Activity {


    private Intent i;
    private Button mFrameMarkerButton;
    private Button mImageTargetButton;
    private Button mOpenCVButton;
    private Button mAboutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momentanpol_main);
        mFrameMarkerButton = (Button) findViewById(R.id.buttonFrameMarker);
        mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);
        mOpenCVButton = (Button) findViewById(R.id.buttonOpenCV);
        mAboutButton = (Button) findViewById(R.id.buttonAbout);

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
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
