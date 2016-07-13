package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Main menu of this application with one button for each state
 * @author janna
 * @author tobby
 * @author fabian
 * @version 0.1
 *
 */

public class MomentanpolMain extends Activity {

    //Vuforia-Statemachine-Intent for start different Vuforia-Tasks
    private Intent statemachine;
    // for starting OpenCV-Task

    /**
     * creates the layout and callbacks for the main menu
     * with a button for every state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momentanpol_main);

        Button mFrameMarkerButton;
        Button mImageTargetButton;

        mFrameMarkerButton = (Button) findViewById(R.id.buttonFrameMarker);
        mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);

        statemachine = new Intent(getApplicationContext(),MomentanpolTask.class);
        // set up some onClickListener
        mFrameMarkerButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Callback method for the Frame Marker Button
             * @param v: current View
             */
            @Override
            public void onClick(View v) {
                statemachine.putExtra("button",0);
                startActivity(statemachine);
            }
        });

        mImageTargetButton.setOnClickListener(new View.OnClickListener() {
            /**
             * Callback method for the Image Targets Button
             * @param v: current View
             */
            @Override
            public void onClick(View v) {
                statemachine.putExtra("button",1);
                startActivity(statemachine);
            }
        });
    }

}
