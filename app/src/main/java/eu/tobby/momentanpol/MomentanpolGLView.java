package eu.tobby.momentanpol;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import eu.tobby.momentanpol.interfaces.MomentanpolState;

/**
 * Extends GLSurfaceView to listen to TouchEvents
 * @author janna
 * @author tobby
 * @author fabian
 * @version 1.0
 */
public class MomentanpolGLView extends GLSurfaceView {

    // Handle for the current Renderer
    private GLSurfaceView.Renderer mRenderer;
    // Habdle for the current State
    private MomentanpolState mState;


    public MomentanpolGLView(Context context, GLSurfaceView.Renderer renderer, MomentanpolState state) {
        super(context);
        mRenderer = renderer;
        mState = state;
    }


    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean retVal = false;
        if(e.getAction() == MotionEvent.ACTION_DOWN) {
            //Callback if the action is action_down
            mState.isActionDown();
            retVal = true;
        }
        return retVal;
    }

}
