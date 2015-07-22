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
 * View to get the images of OpenCV
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 */
public class MomentanpolOpenCVView extends JavaCameraView {

    private static final String LOGTAG = "MomentanpolOpenCVivew";

    public MomentanpolOpenCVView(Context context, AttributeSet attrs) {
        super(context,attrs);
    }

    /**
     * Getter method for the suppored camera effects
     * @return: List of the supported camera effects
     */
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    /**
     * Getter method for the supported display resolutions
     * @return: List of the supported resolutions
     */
    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    /**
     * Method to change the display resolution
     * @param width: desired width (in Pixel)
     * @param height: desired height (in Pixel)
     */
    public void setResolution(int width, int height) {
        disconnectCamera();
        mMaxHeight = height;
        mMaxWidth = width;
        connectCamera(getWidth(), getHeight());
    }

    /**
     * Method to set the Orientation to Portrait
     */
    public void setPortrait() {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.set("orientation", "portrait");
        mCamera.setParameters(parameters);

    }


}
