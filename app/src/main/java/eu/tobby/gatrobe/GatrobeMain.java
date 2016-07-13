package eu.tobby.gatrobe;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

/**
 * Main menu of this application with one button for each state
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 *
 */

public class GatrobeMain extends Activity {

    // Vuforia-Statemachine-Intent to start different Vuforia-Tasks
    private Intent statemachine;

    /**
     * creates the layout and callbacks for the main menu
     * with a button for every state (only one button at the moment
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gatrobe_main);

        final Button mImageTargetButton;
        mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);

        // check if there is access to the camera
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // request access to the camera if there is no permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            // wait-time-workaround, because the permission is not granted instantly
            try {
                TimeUnit.SECONDS.sleep(1L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // check again for camera permission and close app if there is still no access
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                finish();
        }

        statemachine = new Intent(getApplicationContext(), GatrobeTask.class);

        // set up some onClickListener
        mImageTargetButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Callback method for the Image Targets Button
             * @param v: current View
             */
            @Override
            public void onClick(View v) {
                // color the button to dark grey and start the statemachine
                mImageTargetButton.setBackgroundColor(Color.DKGRAY);
                mImageTargetButton.setBackgroundColor(Color.TRANSPARENT);
                mImageTargetButton.setBackgroundColor(Color.DKGRAY);
                statemachine.putExtra("button", 0);
                startActivity(statemachine);
            }
        });
    }

    /**
     * Make button transparent again when closing the Vuforia camera activity
     */
    @Override
    protected void onResume() {
        super.onResume();
        final Button mImageTargetButton;
        mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);
        mImageTargetButton.setBackgroundColor(Color.TRANSPARENT);
    }

}