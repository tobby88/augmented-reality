package eu.tobby.momentanpol.OpenCVTask;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;
import android.hardware.Camera.Size;


import org.opencv.android.JavaCameraView;

import java.util.List;


/**
 * Created by fabian on 15.07.15.
 */
public class MomentanpolOpenCVView extends JavaCameraView {

    private static final String LOGTAG = "MomentanpolOpenCVivew";

    public MomentanpolOpenCVView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(int width, int height) {
        disconnectCamera();
        mMaxHeight = height;
        mMaxWidth = width;
        connectCamera(getWidth(), getHeight());
    }

    public void setPortrait() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("orientation", "portrait");
        mCamera.setParameters(parameters);

    }


}
