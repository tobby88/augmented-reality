package eu.tobby.gatrobe;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import eu.tobby.gatrobe.interfaces.GatrobeState;

/**
 * Extends GLSurfaceView to listen to TouchEvents
 * @author janna
 * @author tobby
 * @author fabian
 * @version 2.0
 */
public class GatrobeGLView extends GLSurfaceView {

    // Handle for the current Renderer
    private GLSurfaceView.Renderer mRenderer;
    // Habdle for the current State
    private GatrobeState mState;


    public GatrobeGLView(Context context, GLSurfaceView.Renderer renderer, GatrobeState state) {
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
