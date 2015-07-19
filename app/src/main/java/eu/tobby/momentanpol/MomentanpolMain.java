package eu.tobby.momentanpol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import eu.tobby.momentanpol.OpenCVTask.OpenCVTask;

public class MomentanpolMain extends Activity {

    private Intent i;
    private Intent j;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_momentanpol_main);
        Button mFrameMarkerButton;
        Button mImageTargetButton;
        Button mOpenCVButton;
        Button mAboutButton;
        mFrameMarkerButton = (Button) findViewById(R.id.buttonFrameMarker);
        mImageTargetButton = (Button) findViewById(R.id.buttonImageTarget);
        mOpenCVButton = (Button) findViewById(R.id.buttonOpenCV);
        mAboutButton = (Button) findViewById(R.id.buttonFarben);

        i = new Intent(getApplicationContext(),MomentanpolTask.class);
        j = new Intent(getApplicationContext(), OpenCVTask.class);

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
                startActivity(j);
            }
        });
        mAboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("button", 2);
                startActivity(i);
            }
        });
    }

}
